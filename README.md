# Moteur RH — Solde de tout compte

API REST de calcul du solde de tout compte d'un employé : indemnité de congés non pris,
prime d'ancienneté, pénalité de préavis, brut, impôt, net et déclenchement d'audit.

Projet développé en **architecture hexagonale** et **TDD strict** (RED → GREEN → REFACTOR),
binôme **bayebaradiop / abdoulayely**.

> L'architecture complète et la justification de chaque choix sont dans
> **[ARCHITECTURE.md](ARCHITECTURE.md)**.

---

## Stack

| Élément | Choix |
|---|---|
| Langage | Java 21 |
| Framework | Spring Boot 3.5 (Web, Validation) |
| Build | Maven |
| Documentation | springdoc-openapi (Swagger UI) |
| Tests | JUnit 5, Mockito, AssertJ, MockMvc |
| Lombok | **non utilisé** — records et Java moderne |

## Démarrer

```bash
docker compose up -d     # PostgreSQL 16 (port hôte 5436)
mvn test                 # 49 tests — ne nécessite pas Docker (base H2 en mémoire)
mvn spring-boot:run      # http://localhost:8090
```

- Swagger UI : http://localhost:8090/swagger-ui.html
- Contrat de référence (API First) : [`src/main/resources/openapi/settlement-api.yaml`](src/main/resources/openapi/settlement-api.yaml)

> Si le port est occupé : `mvn spring-boot:run -Dspring-boot.run.arguments=--server.port=8095`

### Base de données

| | |
|---|---|
| Image | `postgres:16-alpine` ([docker-compose.yml](docker-compose.yml)) |
| Hôte / port | `localhost:5436` (le port 5432 est souvent déjà pris) |
| Base / utilisateur / mot de passe | `moteur_rh` / `moteur_rh` / `moteur_rh` |
| Schéma | géré par **Flyway** ([`db/migration`](src/main/resources/db/migration)), `ddl-auto: validate` |
| Table | `settlement_history` — chaque solde calculé y est archivé |

Les paramètres de connexion sont surchargeables sans rebuild :
`DATABASE_URL`, `DATABASE_USER`, `DATABASE_PASSWORD` (et `POSTGRES_PORT` côté compose).

```bash
docker compose up -d      # démarrer
docker compose down       # arrêter (données conservées dans le volume)
docker compose down -v    # arrêter et supprimer les données

docker exec moteur-rh-postgres psql -U moteur_rh -d moteur_rh \
  -c "SELECT employee_id, net_amount, audit_triggered, recorded_at FROM settlement_history;"
```

La suite de tests utilise **H2 en mémoire** ([`src/test/resources/application.yml`](src/test/resources/application.yml)) :
`mvn test` reste exécutable sur une machine sans Docker.

## Appeler l'API

```bash
curl -X POST http://localhost:8080/api/v1/settlements \
  -H 'Content-Type: application/json' \
  -d '{
    "employeeId": "EMP-001",
    "hireDate": "2016-01-01",
    "departureDate": "2026-01-01",
    "departureReason": "RETIREMENT",
    "baseSalary": 1050000,
    "remainingLeaveDays": 10,
    "noticeRespected": true
  }'
```

```json
{
  "employeeId": "EMP-001",
  "leaveCompensation": 500000.00,
  "seniorityBonus": 1312500.00,
  "noticePenalty": 0.00,
  "grossAmount": 1812500.00,
  "taxAmount": 0.00,
  "netAmount": 1812500.00,
  "auditTriggered": false
}
```

En cas d'erreur, la réponse a toujours la même forme :

```json
{
  "timestamp": "2026-01-01T10:15:30",
  "status": 400,
  "error": "Bad Request",
  "message": "La date de depart doit etre posterieure a la date d'embauche",
  "path": "/api/v1/settlements"
}
```

---

## Règles métier implémentées

| # | Règle | Classe responsable |
|---|-------|--------------------|
| 1 | Congés non pris : `jours × (salaire / 21)` | `LeaveCalculator` |
| 2 | Prime d'ancienneté : retraite et licenciement économique **uniquement** ; 10 %/an les 5 premières années, 15 %/an ensuite | `SeniorityBonusCalculator` |
| 3 | Préavis non respecté sur démission : −1 salaire mensuel (net final possiblement négatif) | `NoticePenaltyCalculator` |
| 4 | Brut = congés + prime − pénalité | `GrossCalculator` |
| 5 | Prime exonérée jusqu'à 5 000 000 XOF, surplus imposable | `TaxBaseCalculator` |
| 6 | Impôt : **jamais** calculé par le moteur | `TaxAdministrationPort` |
| 7 | Net = brut − impôt | `SettlementEngine` |
| 8 | Net > 30 000 000 XOF → `notifyAudit(employeeId)` immédiat | `SettlementEngine` + `LabourInspectionPort` |
| 9 | Tout solde calculé est archivé ; un départ rejeté ne laisse aucune trace | `SettlementService` + `SettlementHistoryPort` |

### Hypothèses d'interprétation de l'énoncé

Deux points de l'énoncé admettaient plusieurs lectures ; le choix retenu est isolé dans une
constante, donc modifiable en un point unique :

1. **Prime d'ancienneté — taux annuels.** « 5 premières années 10 %, puis années
   supplémentaires 15 % » est appliqué **par année** :
   `prime = salaire × (10 % × min(années, 5) + 15 % × max(années − 5, 0))`.
   Exemple : 10 ans → 125 % du salaire. *(constantes `BASE_YEAR_RATE` /
   `ADDITIONAL_YEAR_RATE` dans `SeniorityBonusCalculator`)*
2. **Règle de validation dépendante du motif.** L'énoncé demande « certaines règles
   dépendent du motif de départ » sans les nommer : un départ à la retraite exige au moins
   **une année** d'ancienneté *(`RetirementSeniorityRule`)*.

Le barème fiscal progressif de `ProgressiveTaxAdministrationAdapter` (0 / 20 / 30 / 35 / 40 %)
est une implémentation d'exemple : le domaine n'en dépend pas et l'adaptateur est
remplaçable sans toucher au métier.

---

## Structure

```
com.company.hrsettlement.settlement
├── api              contrôleur REST, DTO, mapper, gestion des erreurs
├── application      SettlementUseCase (port primaire) + SettlementService
├── domain           modèles, moteur, calculateurs, validateurs, predicates,
│                    functions, ports, exceptions  ← aucune dépendance externe
└── infrastructure   adaptateurs (impôts, inspection) + câblage Spring
```

## Tests — 49 tests, 12 classes

| Classe | Ce qu'elle prouve |
|---|---|
| `LeaveCalculatorTest` | valeur journalière, arrondi au centime |
| `SeniorityBonusCalculatorTest` | barème par tranche, seuils exacts, éligibilité |
| `NoticePenaltyCalculatorTest` | retenue limitée aux démissions sans préavis |
| `GrossCalculatorTest` | consolidation, brut négatif autorisé |
| `TaxBaseCalculatorTest` | plafond d'exonération, assiette jamais négative |
| `DepartureValidatorTest` | règles métier + extension par composition (Mockito) |
| `SettlementEngineTest` | orchestration, `when()`, `ArgumentCaptor`, `verify()` sur l'audit |
| `SettlementServiceTest` | délégation stricte au domaine, archivage, absence d'archivage si rejet |
| `SettlementMapperTest` | fidélité du mapping DTO ⇄ domaine |
| `SettlementControllerTest` | contrat HTTP, 200/400, format d'erreur |
| `ProgressiveTaxAdministrationAdapterTest` | barème progressif par tranche |
| `SettlementApiIntegrationTest` | parcours complet, câblage réel |

## Historique Git

Un commit par phase du cycle, en français :

```
RED : ajoute le test en echec de l'indemnite de conges non pris
GREEN : implemente le calcul de l'indemnite de conges non pris
REFACTOR : centralise l'arrondi monetaire et extrait la fonction valeur journaliere
```

`git log --oneline` retrace ainsi l'ordre réel du développement : chaque règle métier
commence par un test rouge, constaté avant d'écrire la moindre ligne de code métier.
