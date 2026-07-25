-- Historique des soldes de tout compte calcules par le moteur.
-- Les montants utilisent NUMERIC : aucun type flottant sur de la paie.

CREATE TABLE historique_solde
(
    id                 BIGSERIAL PRIMARY KEY,
    matricule_employe        VARCHAR(50)    NOT NULL,
    indemnite_conges NUMERIC(19, 2) NOT NULL,
    prime_anciennete    NUMERIC(19, 2) NOT NULL,
    penalite_preavis     NUMERIC(19, 2) NOT NULL,
    montant_brut       NUMERIC(19, 2) NOT NULL,
    montant_impot         NUMERIC(19, 2) NOT NULL,
    montant_net         NUMERIC(19, 2) NOT NULL,
    audit_declenche    BOOLEAN        NOT NULL,
    enregistre_le        TIMESTAMP      NOT NULL
);

-- Consultation courante : l'historique d'un employe donne.
CREATE INDEX idx_historique_solde_matricule ON historique_solde (matricule_employe);
