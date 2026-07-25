# Architecture — Moteur RH (Solde de tout compte)

Ce document présente la structure du projet, le rôle de chaque couche et la justification de
chaque choix.

L'architecture retenue est une **architecture en couches classique** (Spring MVC),
volontairement simple : chaque couche a une responsabilité claire et ne parle qu'à la couche
du dessous.

---

## 1. Vision d'ensemble

Le système est un **moteur de décision métier** : il reçoit les informations de départ d'un
employé et produit un solde de tout compte (indemnités, brut, impôt, net, audit éventuel).

```
                 HTTP
                   │
                   ▼
        ┌────────────────────────────┐
        │        controller          │  reçoit, délègue, répond
        │      SoldeControleur       │
        └─────────────┬──────────────┘
                      │  mapper : DTO ⇄ domaine
                      ▼
        ┌────────────────────────────┐
        │          service           │  toute l'intelligence métier
        │  MoteurSolde ← orchestre   │
        │  ValidateurDepart          │
        │  Calculateur × 3           │
        │  AdministrationFiscale (interface)
        │  InspectionTravail     (interface)
        │  ArchiveurSolde            │
        └─────────────┬──────────────┘
                      ▼
        ┌────────────────────────────┐
        │        repository          │  accès aux données
        │    HistoriqueSoldeDepot    │
        └─────────────┬──────────────┘
                      ▼
                 PostgreSQL

  domain  ── records et enum, utilisés par toutes les couches
  dto     ── contrat public de l'API (jamais le modèle métier)
```

---

## 2. Arborescence

```
com.company.hrsettlement
│
├── ApplicationRh.java                amorçage Spring Boot
│
├── controller
│   └── SoldeControleur              expose POST /api/v1/settlements
│
├── dto
│   ├── request
│   │   └── SoldeRequeteDto          contrat d'entrée + Bean Validation
│   └── response
│       ├── SoldeReponseDto          contrat de sortie
│       └── ErreurReponseDto         format d'erreur uniforme
│
├── domain                            objets métier immuables (records)
│   ├── DepartEmploye                donnée d'entrée
│   ├── Solde                        résultat du calcul
│   ├── MotifDepart                  enum des motifs de départ
│   ├── AssietteFiscale              part imposable + part exonérée
│   └── Monnaie                      règle unique d'arrondi monétaire
│
├── mapper
│   └── SoldeConvertisseur           DTO ⇄ domaine, isolé du reste
│
├── service                           LE MÉTIER
│   ├── MoteurSolde                  orchestre le calcul complet
│   ├── ValidateurDepart             règles de cohérence métier
│   ├── CalculateurConges            indemnité de congés non pris
│   ├── CalculateurPrimeAnciennete   prime d'ancienneté
│   ├── CalculateurPenalitePreavis   pénalité de préavis
│   ├── AdministrationFiscale        interface — calcul de l'impôt
│   ├── AdministrationFiscaleProgressive   barème par tranches
│   ├── InspectionTravail            interface — notification d'audit
│   ├── InspectionTravailJournalisee implémentation par journalisation
│   └── ArchiveurSolde               archivage du résultat
│
├── repository
│   ├── HistoriqueSoldeEntite        entité JPA (table historique_solde)
│   └── HistoriqueSoldeDepot         Spring Data JPA
│
├── exception
│   ├── ExceptionMetier              racine des erreurs métier
│   ├── ExceptionDatesInvalides
│   ├── ExceptionSalaireInvalide
│   ├── ExceptionDepartInvalide
│   └── GestionnaireGlobalErreurs    @RestControllerAdvice
│
└── config
    └── ConfigurationApplication     horloge injectable + métadonnées OpenAPI
```

---

## 3. Justification couche par couche

### 3.1 `controller` — la porte d'entrée

Le contrôleur fait trois choses : recevoir, déléguer, répondre. Aucun `if` métier, aucun
calcul, aucune conversion écrite à la main — il appelle le convertisseur.

`@Valid` déclenche la **validation technique** (Bean Validation) : elle protège le contrat
d'API. La validation *métier*, elle, vit dans le service, car elle doit s'appliquer même si
l'appel ne vient pas de HTTP.

### 3.2 `dto` — le contrat public

**Les objets métier ne sont jamais sérialisés.** Exposer `Solde` directement coupleraient le
JSON public à la structure interne : toute évolution du métier deviendrait un *breaking
change* pour les clients. Les DTO sont des records immuables, séparés en `request` et
`response` pour que le sens de chacun soit évident.

### 3.3 `service` — le métier

C'est ici que se trouve la valeur du logiciel.

**`MoteurSolde` orchestre, il ne calcule pas.** Il enchaîne : validation → congés → prime →
pénalité → brut → impôt → net → audit → archivage. Une modification du barème des congés ne
le touche jamais : une classe = une responsabilité.

**Un calculateur par règle.** `CalculateurConges`, `CalculateurPrimeAnciennete` et
`CalculateurPenalitePreavis` sont testables isolément, en millisecondes, sans Spring ni base.
Ajouter une règle = créer une classe, sans modifier les existantes.

**Deux interfaces pour les systèmes externes.** `AdministrationFiscale` et
`InspectionTravail` sont des interfaces, pas des classes concrètes, pour trois raisons :

1. le barème fiscal évolue indépendamment des règles RH ;
2. les tests les remplacent par des mocks Mockito (`when`, `verify`) ;
3. brancher la vraie API des impôts ne demandera aucune modification du moteur.

**`Monnaie`** centralise `scale = 2` et `RoundingMode.HALF_UP`. Sans elle, la règle d'arrondi
serait dupliquée dans chaque calcul — source classique de centimes qui divergent.

### 3.4 `repository` — les données

`HistoriqueSoldeEntite` est **distincte du record `Solde`** : les contraintes de JPA
(constructeur sans argument, champs mutables, identifiant technique) ne doivent pas rendre le
modèle métier mutable.

Le `@Transactional` de `MoteurSolde` garantit qu'un départ rejeté ne laisse aucune ligne
derrière lui : l'archivage suit le calcul, jamais l'inverse.

Le schéma est versionné par **Flyway**, Hibernate étant en `ddl-auto: validate` : la
structure de la base est du code relu et rejouable, jamais une modification manuelle.

### 3.5 `exception` — la traduction

`GestionnaireGlobalErreurs` traduit les exceptions métier en codes HTTP et produit un format
d'erreur unique. Le métier lève `ExceptionDatesInvalides` (vocabulaire RH), l'API répond
`400` (vocabulaire HTTP). Aucune exception brute ne remonte au client.

---

## 4. Approche API First

Le contrat OpenAPI 3 (`src/main/resources/openapi/solde-api.yaml`) est la référence, écrit
avant l'implémentation. Il fixe :

| Élément | Décision |
|---|---|
| Endpoint | `POST /api/v1/settlements` |
| Entrée | `SoldeRequeteDto` (JSON) |
| Sortie | `SoldeReponseDto` (JSON) |
| `200 OK` | solde calculé |
| `400 Bad Request` | violation du contrat **ou** règle métier invalide |
| `500 Internal Server Error` | erreur inattendue, message générique |

Swagger UI (springdoc) publie la documentation vivante ; l'implémentation doit rester
conforme au contrat.

---

## 5. Règles métier et où elles vivent

| # | Règle | Classe responsable |
|---|-------|--------------------|
| 1 | Congés non pris : `jours × (salaire / 21)` | `CalculateurConges` |
| 2 | Prime : retraite et licenciement économique uniquement ; 10 %/an les 5 premières années, 15 %/an ensuite | `CalculateurPrimeAnciennete` |
| 3 | Préavis non respecté sur démission → −1 salaire mensuel | `CalculateurPenalitePreavis` |
| 4 | Brut = congés + prime − pénalité (peut être négatif) | `MoteurSolde` |
| 5 | Prime exonérée jusqu'à 5 000 000 XOF | `MoteurSolde` |
| 6 | Impôt : **jamais** calculé par le moteur | `AdministrationFiscale` |
| 7 | Net = brut − impôt | `MoteurSolde` |
| 8 | Net > 30 000 000 XOF → `notifierAudit(matricule)` | `MoteurSolde` + `InspectionTravail` |
| 9 | Archivage de tout solde calculé | `ArchiveurSolde` |

---

## 6. Contraintes techniques tenues

- `BigDecimal` pour **tout** montant — `double`/`float` interdits ;
- `LocalDate` pour **toute** date — `Date`/`Calendar` interdits ;
- toute division précise `scale` **et** `RoundingMode` (centralisés dans `Monnaie`) ;
- aucune lecture de l'horloge pendant le calcul : les dates arrivent en entrée, chaque test
  est déterministe ; l'horloge ne sert qu'à l'archivage, et elle est injectée ;
- pas de Lombok : les `record` et Java 21 suffisent.

---

## 7. Stratégie de test — 42 tests

| Niveau | Outil | Ce qui est prouvé |
|---|---|---|
| Calculateurs | JUnit 5 + AssertJ | exactitude des formules, valeurs limites |
| Validateur | AssertJ (`assertThatThrownBy`) | rejet des données incohérentes |
| Moteur | Mockito (`when`, `verify`, `ArgumentCaptor`) | orchestration, appels externes, audit, archivage |
| Barème fiscal | JUnit 5 + AssertJ | progressivité par tranche |
| Convertisseur | JUnit 5 + AssertJ | fidélité du mapping |
| Contrôleur | `@WebMvcTest` + MockMvc | contrat HTTP, codes de statut, format d'erreur |
| Bout en bout | `@SpringBootTest` | câblage réel, barème réel, base réelle |

Le développement a suivi le cycle **RED → GREEN → REFACTOR** : chaque règle métier est
introduite par un test qui échoue, constaté avant d'écrire la moindre ligne de code métier.
L'historique Git en conserve la trace.
