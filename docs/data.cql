CREATE KEYSPACE IF NOT EXISTS ocds WITH replication = {'class':'SimpleStrategy', 'replication_factor':1};


CREATE TABLE IF NOT EXISTS  ocds.contracting_can (
    cp_id text,
    can_id UUID,
    ac_id text,
    award_id text,
    created_date timestamp,
    owner text,
    status text,
    status_details text,
    primary key(cp_id, can_id)
);

CREATE TABLE IF NOT EXISTS  ocds.contracting_ac (
    cp_id text,
    ac_id text,
    token_entity UUID,
    can_id text,
    owner text,
    created_date timestamp,
    status text,
    status_details text,
    mpc text,
    language text,
    json_data text,
    primary key(cp_id, ac_id)
);

CREATE TABLE IF NOT EXISTS  ocds.contracting_history (
    operation_id text,
    command text,
    operation_date timestamp,
    json_data text,
    primary key(operation_id, command)
);

CREATE TABLE IF NOT EXISTS  ocds.contracting_templates (
    country text,
    pmd text,
    language text,
    template_id text,
    template text,
    primary key(country, pmd, language, template_id)
);

INSERT INTO ocds.contracting_templates (country,pmd,language,template_id,template) VALUES ('MD','OT','en','cs-buyer-confirmation-on','{"id": "cs-buyer-confirmation-on-", "description": "Buyer has to sign the transferred document", "title": "Document signing", "type": "digitalSignature", "relatesTo": "document", "requestTitle": "parties[role:buyer].persones[role:authority].", "requestDescription": "Defined person has to sign the transferred document"}');
INSERT INTO ocds.contracting_templates (country,pmd,language,template_id,template) VALUES ('MD','OT','en','cs-tenderer-confirmation-on','{"id": "cs-tenderer-confirmation-on-", "description": "Supplier has to sign the transferred document", "title": "Document signing", "type": "digitalSignature", "relatesTo": "document", "requestTitle": "parties[role:supplier].persones[role:authority].", "requestDescription": "Defined person has to sign the transferred document"}')