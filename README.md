# Moteur RH

TP TDD — moteur de décision RH.
Binôme : **bayebaradiop** / **abdoulayely**.

La méthode suivie (RED → GREEN → REFACTOR, convention de commits) est décrite dans le
[guide TDD](../README.md) du dépôt de TP.

## Stack

- Java 17 / Maven
- JUnit 5 (`junit-jupiter`) + Mockito (`mockito-junit-jupiter`)
- Package métier : `sn.ecole221.rh`

## Lancer les tests

```bash
mvn test
```

## Fiche TP

À compléter à partir de l'énoncé.

## Contexte métier

<en une phrase : que fait ce système, pour qui, quelle décision produit-il ?>

## Objet(s) d'entrée (Record / immuable)

`<NomDuRecord>(<champ1 type1>, <champ2 type2>, …)`

## Règles métier, par niveau

| Niveau | Règle (condition → décision) | Type de test | Valeur limite à tester |
|--------|------------------------------|--------------|------------------------|
| 1      | <…>                          | logique pure | <seuil exact>          |

## Dépendances à injecter (constructeur)

- `<PortExterne>` → interface, remplacée par `@Mock` en test
- `<Clock>` → figé via `Clock.fixed(...)` en test

## Journal des commits

- [x] `chore:` init projet
- [ ] N1 `test:` … / `feat:` …
