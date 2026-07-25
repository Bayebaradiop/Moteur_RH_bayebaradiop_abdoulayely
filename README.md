# Moteur RH — Solde de tout compte

API REST de calcul du solde de tout compte d'un employé : indemnité de congés non pris,
prime d'ancienneté, pénalité de préavis, brut, impôt, net et déclenchement d'audit.

Projet développé en **architecture en couches** et **TDD strict** (RED → GREEN → REFACTOR),
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
mvn test                 # 42 tests — ne nécessite pas Docker (base H2 en mémoire)
mvn spring-boot:run      # http://localhost:8090
```

- Swagger UI : http://localhost:8090/swagger-ui.html
- Contrat de référence (API First) : [`src/main/resources/openapi/solde-api.yaml`](src/main/resources/openapi/solde-api.yaml)

> Si le port est occupé : `mvn spring-boot:run -Dspring-boot.run.arguments=--server.port=8095`

### Base de données

| | |
|---|---|
| Image | `postgres:16-alpine` ([docker-compose.yml](docker-compose.yml)) |
| Hôte / port | `localhost:5436` (le port 5432 est souvent déjà pris) |
| Base / utilisateur / mot de passe | `moteur_rh` / `moteur_rh` / `moteur_rh` |
| Schéma | géré par **Flyway** ([`db/migration`](src/main/resources/db/migration)), `ddl-auto: validate` |
| Table | `historique_solde` — chaque solde calculé y est archivé |

Les paramètres de connexion sont surchargeables sans rebuild :
`DATABASE_URL`, `DATABASE_USER`, `DATABASE_PASSWORD` (et `POSTGRES_PORT` côté compose).

```bash
docker compose up -d      # démarrer
docker compose down       # arrêter (données conservées dans le volume)
docker compose down -v    # arrêter et supprimer les données

docker exec moteur-rh-postgres psql -U moteur_rh -d moteur_rh \
  -c "SELECT matricule_employe, montant_net, audit_declenche, enregistre_le FROM historique_solde;"
```

La suite de tests utilise **H2 en mémoire** ([`src/test/resources/application.yml`](src/test/resources/application.yml)) :
`mvn test` reste exécutable sur une machine sans Docker.

## Appeler l'API

```bash
curl -X POST http://localhost:8090/api/v1/settlements \
  -H 'Content-Type: application/json' \
  -d '{
    "matriculeEmploye": "EMP-001",
    "dateEmbauche": "2016-01-01",
    "dateDepart": "2026-01-01",
    "motifDepart": "RETRAITE",
    "salaireBase": 1050000,
    "joursCongesRestants": 10,
    "preavisRespecte": true
  }'
```

```json
{
  "matriculeEmploye": "EMP-001",
  "indemniteConges": 500000.00,
  "primeAnciennete": 1312500.00,
  "penalitePreavis": 0.00,
  "montantBrut": 1812500.00,
  "montantImpot": 0.00,
  "montantNet": 1812500.00,
  "auditDeclenche": false
}
```

En cas d'erreur, la réponse a toujours la même forme :

```json
{
  "horodatage": "2026-01-01T10:15:30",
  "statut": 400,
  "erreur": "Bad Request",
  "message": "La date de depart doit etre posterieure a la date d'embauche",
  "chemin": "/api/v1/settlements"
}
```

---

## Règles métier implémentées

| # | Règle | Classe responsable |
|---|-------|--------------------|
| 1 | Congés non pris : `jours × (salaire / 21)` | `CalculateurConges` |
| 2 | Prime d'ancienneté : retraite et licenciement économique **uniquement** ; 10 %/an les 5 premières années, 15 %/an ensuite | `CalculateurPrimeAnciennete` |
| 3 | Préavis non respecté sur démission : −1 salaire mensuel (net final possiblement négatif) | `CalculateurPenalitePreavis` |
| 4 | Brut = congés + prime − pénalité | `MoteurSolde` |
| 5 | Prime exonérée jusqu'à 5 000 000 XOF, surplus imposable | `MoteurSolde` |
| 6 | Impôt : **jamais** calculé par le moteur | `AdministrationFiscale` |
| 7 | Net = brut − impôt | `MoteurSolde` |
| 8 | Net > 30 000 000 XOF → `notifierAudit(matricule)` immédiat | `MoteurSolde` + `InspectionTravail` |
| 9 | Tout solde calculé est archivé ; un départ rejeté ne laisse aucune trace | `MoteurSolde` + `ArchiveurSolde` |

### Hypothèses d'interprétation de l'énoncé

Deux points de l'énoncé admettaient plusieurs lectures ; le choix retenu est isolé dans une
constante, donc modifiable en un point unique :

1. **Prime d'ancienneté — taux annuels.** « 5 premières années 10 %, puis années
   supplémentaires 15 % » est appliqué **par année** :
   `prime = salaire × (10 % × min(années, 5) + 15 % × max(années − 5, 0))`.
   Exemple : 10 ans → 125 % du salaire. *(constantes `TAUX_ANNEE_BASE` /
   `TAUX_ANNEE_SUPPLEMENTAIRE` dans `CalculateurPrimeAnciennete`)*
2. **Règle de validation dépendante du motif.** L'énoncé demande « certaines règles
   dépendent du motif de départ » sans les nommer : un départ à la retraite exige au moins
   **une année** d'ancienneté *(`ValidateurDepart`)*.

Le barème fiscal progressif de `AdministrationFiscaleProgressive` (0 / 20 / 30 / 35 / 40 %)
est une implémentation d'exemple : le domaine n'en dépend pas et l'adaptateur est
remplaçable sans toucher au métier.

---

## Structure

```
com.company.hrsettlement
├── controller       SoldeControleur — reçoit, délègue, répond
├── dto
│   ├── request      SoldeRequeteDto — contrat d'entrée + Bean Validation
│   └── response     SoldeReponseDto, ErreurReponseDto
├── domain           records métier immuables + enum + Monnaie
├── mapper           SoldeConvertisseur — DTO ⇄ domaine
├── service          MoteurSolde, calculateurs, validateur,
│                    AdministrationFiscale et InspectionTravail (interfaces)
├── repository       HistoriqueSoldeEntite + HistoriqueSoldeDepot (JPA)
├── exception        exceptions métier + GestionnaireGlobalErreurs
└── config           horloge injectable + métadonnées OpenAPI
```

Le détail et la justification de chaque couche sont dans [ARCHITECTURE.md](ARCHITECTURE.md).

## Tests — 42 tests, 9 classes

| Classe | Ce qu'elle prouve |
|---|---|
| `CalculateurCongesTest` | valeur journalière, arrondi au centime |
| `CalculateurPrimeAncienneteTest` | barème par tranche, seuils exacts, éligibilité |
| `CalculateurPenalitePreavisTest` | retenue limitée aux démissions sans préavis |
| `ValidateurDepartTest` | rejet des données incohérentes, valeurs limites |
| `MoteurSoldeTest` | orchestration, `when()`, `ArgumentCaptor`, `verify()`, archivage |
| `AdministrationFiscaleProgressiveTest` | barème progressif par tranche |
| `SoldeConvertisseurTest` | fidélité du mapping DTO ⇄ domaine |
| `SoldeControleurTest` | contrat HTTP, 200/400, format d'erreur |
| `SoldeApiIntegrationTest` | parcours complet, câblage réel, ligne en base |

## Historique Git

Un commit par phase du cycle, en français :

```
RED : ajoute le test en echec de l'indemnite de conges non pris
GREEN : implemente le calcul de l'indemnite de conges non pris
REFACTOR : centralise l'arrondi monetaire et extrait la fonction valeur journaliere
```

`git log --oneline` retrace ainsi l'ordre réel du développement : chaque règle métier
commence par un test rouge, constaté avant d'écrire la moindre ligne de code métier.
