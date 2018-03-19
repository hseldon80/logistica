create sequence seq_anagrafiche INCREMENT 1  START 1;
create sequence seq_movimenti INCREMENT 1  START 1;
create sequence seq_ordini INCREMENT 1  START 1;
create sequence seq_articoli INCREMENT 1  START 1;


-- Generato da Oracle SQL Developer Data Modeler 17.4.0.355.2121
--   in:        2018-03-02 11:20:19 CET
--   sito:      Oracle Database 12c
--   tipo:      Oracle Database 12c

CREATE TABLE anagrafica (
    ana_id                NUMERIC(10) NOT NULL,
    ana_ragione_sociale   varchar(255) NOT NULL,
    ana_tipologia         varchar(1) NOT NULL,
    ana_agg_dt            DATE,
    ana_cf_piva				  varchar(16) 
);

ALTER TABLE anagrafica
    ADD CONSTRAINT check_c_f CHECK ( ana_tipologia IN (
        'C',
        'E',
        'F'
    ) );

ALTER TABLE anagrafica ADD CONSTRAINT anagrafica_pk PRIMARY KEY ( ana_id );

CREATE TABLE articolo (
    art_id            NUMERIC(10) NOT NULL,
    art_codice        varchar(20) NOT NULL,
    art_descrizione   varchar(255) NOT NULL,
    art_barcode       varchar(50),
    art_giacenza      numeric(7),
    art_agg_dt        DATE
);

ALTER TABLE articolo ADD CONSTRAINT articolo_pk PRIMARY KEY ( art_id );

CREATE TABLE corrieri (
    cor_id              NUMERIC(10) NOT NULL,
    cor_nominativo      varchar(100) NOT NULL,
    cor_url_tracking    varchar(255),
    cor_abil_tracking   numeric(1) NOT NULL
);

ALTER TABLE corrieri
    ADD CONSTRAINT chec_0_1 CHECK ( cor_abil_tracking IN (
        0,
        1
    ) );

ALTER TABLE corrieri ADD CONSTRAINT corrieri_pk PRIMARY KEY ( cor_id );

CREATE TABLE movimentazioni (
    mov_id              NUMERIC(10) NOT NULL,
    mov_data            DATE NOT NULL,
    mov_ora             varchar(5) NOT NULL,
    mov_tipo            varchar(1) NOT NULL,
    anagrafica_ana_id   NUMERIC(10) 
);

ALTER TABLE movimentazioni
    ADD CHECK ( mov_tipo IN (
        'C',
        'S'
    ) );

ALTER TABLE movimentazioni ADD CONSTRAINT movimentazioni_pk PRIMARY KEY ( mov_id );

CREATE TABLE movimentazioni_articoli (
    mvd_id                  NUMERIC(10) NOT NULL,
    mvd_qta                 numeric(7) NOT NULL,
    articolo_art_id         NUMERIC(10) NOT NULL,
    movimentazioni_mov_id   NUMERIC(10) NOT NULL
);

ALTER TABLE movimentazioni_articoli ADD CONSTRAINT movimentazioni_articoli_pk PRIMARY KEY ( mvd_id );

CREATE TABLE ordini (
    ord_id              NUMERIC(10) NOT NULL,
    ord_data            DATE NOT NULL,
    ord_numero          varchar(20) NOT NULL,
    ord_sped_data       DATE,
    ord_spe_numero      varchar(50),
    ord_stato           varchar(1),
    anagrafica_ana_id   NUMERIC(10) NOT NULL,
    corrieri_cor_id     NUMERIC(10),
    ord_consegnato      DATE
);

ALTER TABLE ordini ADD CONSTRAINT ordini_pk PRIMARY KEY ( ord_id );

CREATE TABLE ordini_articoli (
    ode_id            NUMERIC(10) NOT NULL,
    ode_qta           numeric(5) NOT NULL,
    ordini_ord_id     NUMERIC(10) NOT NULL,
    articolo_art_id   NUMERIC(10) NOT NULL
);

ALTER TABLE ordini_articoli ADD CONSTRAINT ordini_articoli_pk PRIMARY KEY ( ode_id );

CREATE TABLE utente (
    ute_id           NUMERIC(10) NOT NULL,
    ute_nominativo   varchar(50) NOT NULL,
    ute_email        varchar(100) NOT NULL,
    ute_password     varchar(255),
    ute_cf_piva      varchar(16) NOT NULL,
    ute_profilo      varchar(2) NOT NULL,
    ute_stato        varchar(1),
    ute_last_login   DATE,
    ute_tentativi    numeric(2)
);

ALTER TABLE utente ADD CONSTRAINT utente_pk PRIMARY KEY ( ute_id );

ALTER TABLE movimentazioni
    ADD CONSTRAINT movimentazioni_anagrafica_fk FOREIGN KEY ( anagrafica_ana_id )
        REFERENCES anagrafica ( ana_id );

--  ERROR: FK name length exceeds maximum allowed length(30) 
ALTER TABLE movimentazioni_articoli
    ADD CONSTRAINT movimentazioni_articoli_articolo_fk FOREIGN KEY ( articolo_art_id )
        REFERENCES articolo ( art_id );

--  ERROR: FK name length exceeds maximum allowed length(30) 
ALTER TABLE movimentazioni_articoli
    ADD CONSTRAINT movimentazioni_articoli_movimentazioni_fk FOREIGN KEY ( movimentazioni_mov_id )
        REFERENCES movimentazioni ( mov_id );

ALTER TABLE ordini
    ADD CONSTRAINT ordini_anagrafica_fk FOREIGN KEY ( anagrafica_ana_id )
        REFERENCES anagrafica ( ana_id );

ALTER TABLE ordini_articoli
    ADD CONSTRAINT ordini_articoli_articolo_fk FOREIGN KEY ( articolo_art_id )
        REFERENCES articolo ( art_id );

ALTER TABLE ordini_articoli
    ADD CONSTRAINT ordini_articoli_ordini_fk FOREIGN KEY ( ordini_ord_id )
        REFERENCES ordini ( ord_id );

ALTER TABLE ordini
    ADD CONSTRAINT ordini_corrieri_fk FOREIGN KEY ( corrieri_cor_id )
        REFERENCES corrieri ( cor_id );

