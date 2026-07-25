# Architecture — Moteur RH (Solde de tout compte)

Ce document est le **livrable d'architecture**, rédigé *avant* toute ligne de code métier.
Il présente la structure du projet, le rôle de chaque package et la justification de chaque
choix au regard de SOLID, de la Clean Architecture et de l'architecture hexagonale.

---

## 1. Vision d'ensemble

Le système est un **moteur de décision métier** : il reçoit les informations de départ d'un
employé et produit un solde de tout compte (indemnités, brut, impôts, net, audit éventuel).

La valeur du logiciel réside dans ses **règles métier**, pas dans son exposition HTTP.
L'architecture protège donc le métier de la technique : c'est le principe de la
**dépendance dirigée vers l'intérieur** (Clean Architecture).

```
                    ┌──────────────────────────────────────────────┐
                    │                    API                       │
   HTTP  ─────────► │  SoldeControleur · DTO · Mapper         │  adaptateur primaire
                    │  GestionnaireGlobalErreurs                      │
                    └───────────────────┬──────────────────────────┘
                                        │ dépend de ↓
                    ┌───────────────────▼──────────────────────────┐
                    │                APPLICATION                   │
                    │  CalculSoldeCasUsage (port primaire)           │
                    │  SoldeService (orchestration fine)      │
                    └───────────────────┬──────────────────────────┘
                                        │ dépend de ↓
                    ┌───────────────────▼──────────────────────────┐
                    │                  DOMAINE                     │
                    │  model · engine · calculator · validator     │
                    │  predicate · function · exception            │
                    │  port  ◄── interfaces (ports secondaires)    │
                    │                                              │
                    │  ne dépend de RIEN (ni Spring, ni HTTP)      │
                    └───────────────────▲──────────────────────────┘
                                        │ implémente (inversion)
                    ┌───────────────────┴──────────────────────────┐
                    │              INFRASTRUCTURE                  │
                    │  AdaptateurFiscaliteProgressive (impôts)           │  adaptateurs secondaires
                    │  AdaptateurInspectionJournalisee (audit)             │
                    │  ConfigurationDomaine (câblage Spring)        │
                    └──────────────────────────────────────────────┘
```

**Règle de dépendance :** les flèches ne pointent jamais du domaine vers l'extérieur.
L'infrastructure dépend du domaine (elle implémente ses interfaces), jamais l'inverse.
C'est le **D** de SOLID (Dependency Inversion) appliqué à l'échelle de l'architecture.

---

## 2. Arborescence des packages

```
com.company.hrsettlement
│
├── MoteurRhApplication              amorçage Spring Boot (seul point "framework")
│
└── settlement
    │
    ├── api                              ADAPTATEUR PRIMAIRE (entrant)
    │   ├── SoldeControleur         expose POST /api/v1/settlements
    │   ├── dto
    │   │   ├── SoldeRequete        contrat d'entrée (record + Bean Validation)
    │   │   ├── SoldeReponse       contrat de sortie (record immuable)
    │   │   └── ErreurReponse            format d'erreur uniforme
    │   ├── mapper
    │   │   └── SoldeConvertisseur         DTO ⇄ domaine, isolé du reste
    │   └── exception
    │       └── GestionnaireGlobalErreurs   @RestControllerAdvice, traduit métier → HTTP
    │
    ├── application                       COUCHE APPLICATIVE (cas d'usage)
    │   ├── CalculSoldeCasUsage            port primaire (interface)
    │   └── SoldeService            implémentation : délègue au moteur
    │
    ├── domain                            CŒUR MÉTIER — aucune dépendance externe
    │   ├── model
    │   │   ├── DepartEmploye        donnée d'entrée (record immuable)
    │   │   ├── Solde               résultat du calcul (record immuable)
    │   │   ├── MotifDepart          enum des motifs de départ
    │   │   ├── AssietteFiscale                  assiette fiscale (taxable + exonéré)
    │   │   └── Monnaie                    règle unique d'arrondi monétaire
    │   ├── engine
    │   │   └── MoteurSolde         ORCHESTRE, ne calcule pas lui-même
    │   ├── calculator                    une règle = un calculateur
    │   │   ├── CalculateurConges          indemnité de congés non pris
    │   │   ├── CalculateurPrimeAnciennete prime d'ancienneté
    │   │   ├── CalculateurPenalitePreavis  pénalité de préavis
    │   │   ├── CalculateurBrut          montant brut
    │   │   ├── CalculateurAssietteFiscale        répartition taxable / exonéré
    │   │   └── CalculateursSolde    paramètre-objet regroupant les calculateurs
    │   ├── validator                     validation métier (indépendante de Spring)
    │   │   ├── RegleDepart            contrat d'une règle de validation
    │   │   ├── ValidateurDepart       composition des règles
    │   │   └── rule/…                   une classe par règle
    │   ├── predicate                     règles booléennes réutilisables
    │   ├── function                      transformations réutilisables
    │   ├── port                          PORTS SECONDAIRES (interfaces)
    │   │   ├── PortAdministrationFiscale     calcul de l'impôt (système externe)
    │   │   ├── PortInspectionTravail      notification d'audit (système externe)
    │   │   └── PortHistoriqueSoldes     archivage des soldes calculés
    │   └── exception                     exceptions métier explicites
    │
    └── infrastructure                    ADAPTATEURS SECONDAIRES (sortants)
        ├── tax
        │   └── AdaptateurFiscaliteProgressive
        ├── inspection
        │   └── AdaptateurInspectionJournalisee
        ├── persistence                   PostgreSQL (Docker) + Flyway
        │   ├── HistoriqueSoldeEntite   table historique_solde
        │   ├── HistoriqueSoldeDepotJpa
        │   └── AdaptateurHistoriqueSoldeJpa
        └── config
            ├── ConfigurationDomaine       instancie le domaine en beans Spring
            ├── ConfigurationTemps         horloge injectable
            └── ConfigurationOpenApi      métadonnées de la documentation
```

---

## 3. Justification package par package

### 3.1 `domain` — le cœur

**Choix : aucune annotation Spring, aucun import `jakarta.*`, aucun JSON.**

*Pourquoi ?* Un moteur de calcul RH survit aux frameworks. En gardant le domaine « nu » :

- il est testable en **millisecondes**, sans contexte Spring (TDD réellement rapide) ;
- il est réutilisable dans un batch, un job Kafka ou une CLI sans réécriture ;
- il ne peut pas être pollué par une préoccupation technique — la contrainte est
  structurelle, pas une question de discipline.

**`model` — des records.** Les données de départ sont des **valeurs**, pas des entités
mutables. Un `record` donne l'immuabilité, `equals`/`hashCode`/`toString` corrects et un
constructeur canonique où placer les invariants (`Objects.requireNonNull`). Aucun `Optional`
en attribut : un attribut absent d'un record métier signalerait un modèle mal défini.

**`Monnaie`** centralise `scale = 2` et `RoundingMode.HALF_UP`. Sans lui, la règle d'arrondi
serait dupliquée dans chaque calculateur — violation directe de DRY, et source classique de
centimes qui divergent entre deux calculs.

**`engine` — `MoteurSolde` orchestre.** Il ne connaît aucune formule : il enchaîne les
calculateurs, appelle les ports et assemble le `Solde`. C'est le **S** de SOLID : sa
seule responsabilité est la *coordination*. Une modification du barème des congés ne
touche jamais le moteur.

**`calculator` — une règle = une classe.** Chaque calculateur est une unité testable qui
répond à une seule question métier. Ajouter une règle (ex. indemnité de licenciement) =
créer une classe et l'injecter, **sans modifier** l'existant : c'est le **O** (Open/Closed).
`CalculateursSolde` est un *paramètre-objet* : il évite un constructeur de moteur à huit
arguments tout en conservant l'injection individuelle de chaque calculateur.

**`predicate` / `function`.** Les conditions métier récurrentes (`ELIGIBLE_FOR_SENIORITY_BONUS`,
`IS_RESIGNATION`, `NOTICE_NOT_RESPECTED`) et les transformations (`DAILY_SALARY`,
`YEARS_WORKED`) sont nommées une fois et réutilisées. Le code du moteur se lit alors comme
l'énoncé métier. `Predicate`/`Function` ne sont pas utilisés pour « faire moderne » : ils
suppriment de la duplication et donnent un **nom métier** à une expression booléenne.

**`validator`.** La validation métier (cohérence des dates, salaire, règles liées au motif)
appartient au domaine : elle doit s'appliquer même si l'appel ne vient pas de HTTP. Elle est
distincte de la Bean Validation, qui, elle, protège le **contrat d'API**. Chaque règle est
une classe implémentant `RegleDepart` : le validateur compose une liste de règles
(Open/Closed encore, et **I** — interface d'une seule méthode).

**`port`.** Deux interfaces courtes, une par système externe :
`PortAdministrationFiscale` (question → réponse) et `PortInspectionTravail` (action → effet de
bord). Les séparer respecte le **I** (Interface Segregation) : un adaptateur d'audit n'a
aucune raison de connaître le calcul de l'impôt. Toute implémentation est substituable sans
que le moteur s'en aperçoive (**L**, Liskov) — c'est précisément ce que les tests prouvent
en injectant des mocks Mockito.

### 3.2 `application` — le cas d'usage

`CalculSoldeCasUsage` est un **port primaire** : il décrit ce que le système sait faire, en
vocabulaire métier, sans HTTP. `SoldeService` l'implémente en déléguant au moteur. Le
service est volontairement **anémique** : toute logique qui s'y glisserait serait une règle
métier échappée du domaine. Son rôle est le pilotage applicatif (transaction, sécurité,
journalisation) — préoccupations qui n'ont rien à faire dans le moteur.

L'interface permet aussi au contrôleur d'être testé (`@WebMvcTest`) contre un mock, sans
jamais charger le domaine.

### 3.3 `api` — l'adaptateur primaire

Le contrôleur ne fait que trois choses : recevoir, déléguer, répondre. Aucun `if` métier,
aucun calcul, aucune construction manuelle de réponse.

**DTO ≠ modèle métier.** Les records du domaine ne sont jamais sérialisés : exposer
`DepartEmploye` en JSON couplerait le contrat public à la structure interne, et toute
évolution du domaine deviendrait un *breaking change* pour les clients.

**Le mapper est une classe dédiée.** Le contrôleur ne contient pas de code de mapping (il
n'écrit aucun `new DepartEmploye(...)`) : il *invoque* le mapper. Le mapping reste ainsi
isolé, testable seul, et modifiable sans toucher au contrôleur.

**`GestionnaireGlobalErreurs`** traduit les exceptions métier en codes HTTP et produit un format
d'erreur unique. C'est la frontière de traduction : le domaine lève `InvalidDatesException`
(vocabulaire métier), l'API répond `400` (vocabulaire HTTP). Aucune exception brute ne fuit.

### 3.4 `infrastructure` — les adaptateurs secondaires

Les implémentations des ports vivent ici, annotées Spring. Remplacer l'adaptateur fiscal
par un appel à une API externe se fait **sans modifier une seule ligne du domaine**.

**Persistance.** La base PostgreSQL est un détail d'infrastructure de plus. Le domaine
exprime `PortHistoriqueSoldes.archiver(Solde)` ; `AdaptateurHistoriqueSoldeJpa` le
satisfait avec JPA. L'entité `HistoriqueSoldeEntite` est **distincte du record
`Solde`** : les exigences de JPA (constructeur sans argument, champs mutables,
identifiant technique) ne doivent pas rendre le modèle métier mutable.

L'archivage est déclenché par `SoldeService`, pas par le moteur : conserver une trace
est une préoccupation applicative, et le moteur doit rester un calcul pur, exécutable dans
un test sans base de données. Le `@Transactional` du service garantit qu'un départ rejeté
par le domaine ne laisse aucune ligne derrière lui.

Le schéma est versionné par **Flyway** et Hibernate est en `ddl-auto: validate` : la
structure de la base est du code relu et rejouable, jamais une modification manuelle. La
suite de tests, elle, tourne sur H2 en mémoire pour rester exécutable sans Docker.

`ConfigurationDomaine` instancie les objets du domaine (calculateurs, validateur, moteur) en
`@Bean` par injection de constructeur. C'est ce qui permet au domaine de rester sans
annotation tout en bénéficiant de l'injection de dépendances : le câblage est une décision
d'infrastructure, pas une propriété du métier.

---

## 4. Approche API First

Le contrat OpenAPI 3 (`src/main/resources/openapi/solde-api.yaml`) est écrit **avant**
les contrôleurs et fait foi. Il fixe :

| Élément | Décision |
|---|---|
| Endpoint | `POST /api/v1/settlements` |
| Entrée | `SoldeRequete` (JSON) |
| Sortie | `SoldeReponse` (JSON) |
| `200 OK` | solde calculé |
| `400 Bad Request` | violation de la Bean Validation **ou** règle métier invalide |
| `422 Unprocessable Entity` | *non retenu* : le client ne peut pas distinguer utilement les deux cas |
| `500 Internal Server Error` | erreur inattendue, message générique (aucune fuite technique) |

Swagger UI (springdoc) publie la documentation vivante ; l'implémentation doit rester
conforme au contrat.

---

## 5. Règles métier et où elles vivent

| # | Règle | Classe responsable |
|---|-------|--------------------|
| 1 | Congés non pris : `jours × (salaire / 21)` | `CalculateurConges` |
| 2 | Prime d'ancienneté : retraite et licenciement économique uniquement ; 10 %/an les 5 premières années, 15 %/an au-delà | `CalculateurPrimeAnciennete` |
| 3 | Préavis : démission non respectée → −1 salaire mensuel (net final possiblement négatif) | `CalculateurPenalitePreavis` |
| 4 | Brut = congés + prime − pénalité | `CalculateurBrut` |
| 5 | Assiette : prime exonérée jusqu'à 5 000 000 XOF, le reste taxable | `CalculateurAssietteFiscale` |
| 6 | Impôt : **jamais calculé dans le moteur** | `PortAdministrationFiscale` (externe) |
| 7 | Net = brut − impôt | `MoteurSolde` |
| 8 | Net > 30 000 000 XOF → `notifierAudit(matricule)` immédiat | `MoteurSolde` + `PortInspectionTravail` |

**Ordre d'évaluation dans le moteur :** validation métier → congés → prime → pénalité →
brut → assiette → impôt (port) → net → audit (port). L'audit est déclenché **après** le net
et uniquement à partir de lui.

---

## 6. Contraintes techniques tenues

- `BigDecimal` pour **tout** montant — `double`/`float` interdits (erreurs d'arrondi
  inacceptables sur de la paie) ;
- `LocalDate` pour **toute** date — `Date`/`Calendar` interdits (mutables, API datée) ;
- toute division précise explicitement `scale` **et** `RoundingMode` (centralisés dans `Monnaie`) ;
- aucune récupération de la date courante dans le domaine : les dates arrivent en entrée,
  ce qui rend chaque test déterministe ;
- pas de Lombok : les `record` et le Java 21 suffisent, sans génération de bytecode masquée.

---

## 7. Stratégie de test (TDD)

| Niveau | Outil | Ce qui est prouvé |
|---|---|---|
| Calculateurs | JUnit 5 + AssertJ | exactitude des formules, valeurs limites (seuils exacts) |
| Moteur | Mockito (`when`, `verify`, `ArgumentCaptor`) | orchestration, appel des ports, déclenchement de l'audit |
| Validation | JUnit 5 + AssertJ (`assertThatThrownBy`) | rejet des données incohérentes |
| Mapper | JUnit 5 + AssertJ | fidélité de la traduction DTO ⇄ domaine |
| Contrôleur | `@WebMvcTest` + MockMvc | contrat HTTP, codes de statut, format d'erreur |
| Bout en bout | `@SpringBootTest` | câblage réel de l'ensemble |

Chaque règle est introduite par un test qui **échoue d'abord** (RED), suivi du code minimal
(GREEN), puis d'un nettoyage sous filet vert (REFACTOR). L'historique Git en est la preuve.
