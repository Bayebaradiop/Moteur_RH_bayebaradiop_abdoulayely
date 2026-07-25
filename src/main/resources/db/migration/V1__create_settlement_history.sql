-- Historique des soldes de tout compte calcules par le moteur.
-- Les montants utilisent NUMERIC : aucun type flottant sur de la paie.

CREATE TABLE settlement_history
(
    id                 BIGSERIAL PRIMARY KEY,
    employee_id        VARCHAR(50)    NOT NULL,
    leave_compensation NUMERIC(19, 2) NOT NULL,
    seniority_bonus    NUMERIC(19, 2) NOT NULL,
    notice_penalty     NUMERIC(19, 2) NOT NULL,
    gross_amount       NUMERIC(19, 2) NOT NULL,
    tax_amount         NUMERIC(19, 2) NOT NULL,
    net_amount         NUMERIC(19, 2) NOT NULL,
    audit_triggered    BOOLEAN        NOT NULL,
    recorded_at        TIMESTAMP      NOT NULL
);

-- Consultation courante : l'historique d'un employe donne.
CREATE INDEX idx_settlement_history_employee_id ON settlement_history (employee_id);
