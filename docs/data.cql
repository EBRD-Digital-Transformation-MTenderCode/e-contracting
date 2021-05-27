CREATE KEYSPACE IF NOT EXISTS ocds WITH replication = {'class':'SimpleStrategy', 'replication_factor':1};


CREATE TABLE IF NOT EXISTS  ocds.contracting_can (
    cp_id text,
    can_id UUID,
    token_entity UUID,
    ac_id text,
    award_id text,
    lot_id text,
    created_date timestamp,
    owner text,
    status text,
    status_details text,
    json_data text,
    primary key(cp_id, can_id)
);

ALTER TABLE ocds.contracting_can ADD json_data text;
ALTER TABLE ocds.contracting_can ADD token_entity UUID;
ALTER TABLE ocds.contracting_can ADD lot_id text;

CREATE TABLE IF NOT EXISTS  ocds.contracting_ac (
    cp_id text,
    ac_id text,
    token_entity UUID,
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
INSERT INTO ocds.contracting_templates (country,pmd,language,template_id,template) VALUES ('MD','OT','en','cs-tenderer-confirmation-on','{"id": "cs-tenderer-confirmation-on-", "description": "Supplier has to sign the transferred document", "title": "Document signing", "type": "digitalSignature", "relatesTo": "document", "requestTitle": "parties[role:supplier].persones[role:authority].", "requestDescription": "Defined person has to sign the transferred document"}');
INSERT INTO ocds.contracting_templates (country,pmd,language,template_id,template) VALUES ('MD','OT','en','cs-buyer-approval-on','{"id": "cs-buyer-approval-on-","description": "Buyer has to sign the contract","title": "Signature by the Buyer","type": "approval","relatesTo": "document","requestTitle": "parties[role:buyer].persones[role:authority].","requestDescription": "Defined person has to sign the transferred document"}');
INSERT INTO ocds.contracting_templates (country,pmd,language,template_id,template) VALUES ('MD','OT','en','cs-supplier-approval-on','{"id": "cs-supplier-approval-on-","description": "Supplier has to sign the contract","title": "Signature by the Supplier","type": "approval","relatesTo": "document","requestTitle": "parties[role:supplier].persones[role:authority].","requestDescription": "Defined person has to sign the transferred document"}');
INSERT INTO ocds.contracting_templates (country,pmd,language,template_id,template) VALUES ('MD','OT','en','cs-buyer-activate-on','{"id": "cs-buyer-activate-on-","description": "Buyer has to activate the contract","title": "Contract activation","type": "approval","relatesTo": "document","requestTitle": "parties[role:buyer].persones[role:authority].","requestDescription": "Defined person has to sign the transferred document"}');
INSERT INTO ocds.contracting_templates (country,pmd,language,template_id,template) VALUES ('MD','OT','en','cs-treasury-validate-on','{"id": "cs-treasury-validate-on-","description": "Treasury has to approve the contract","title": "Contract validation","type": "approval","relatesTo": "document","requestTitle": "parties[role:treasury].persones[role:authority].","requestDescription": "Defined person has to sign the transferred document"}');
INSERT INTO ocds.contracting_templates (country,pmd,language,template_id,template) VALUES ('MD','OT','en','verification_rationale','This document shall be signed electronically in accordance with Law no. 91 of 29.05.2014. Signature verification can be done at https://msign.gov.md');

INSERT INTO ocds.contracting_templates (country,pmd,language,template_id,template) VALUES ('MD','TEST_OT','en','cs-buyer-confirmation-on','{"id": "cs-buyer-confirmation-on-", "description": "Buyer has to sign the transferred document", "title": "Document signing", "type": "digitalSignature", "relatesTo": "document", "requestTitle": "parties[role:buyer].persones[role:authority].", "requestDescription": "Defined person has to sign the transferred document"}');
INSERT INTO ocds.contracting_templates (country,pmd,language,template_id,template) VALUES ('MD','TEST_OT','en','cs-tenderer-confirmation-on','{"id": "cs-tenderer-confirmation-on-", "description": "Supplier has to sign the transferred document", "title": "Document signing", "type": "digitalSignature", "relatesTo": "document", "requestTitle": "parties[role:supplier].persones[role:authority].", "requestDescription": "Defined person has to sign the transferred document"}');
INSERT INTO ocds.contracting_templates (country,pmd,language,template_id,template) VALUES ('MD','TEST_OT','en','cs-buyer-approval-on','{"id": "cs-buyer-approval-on-","description": "Buyer has to sign the contract","title": "Signature by the Buyer","type": "approval","relatesTo": "document","requestTitle": "parties[role:buyer].persones[role:authority].","requestDescription": "Defined person has to sign the transferred document"}');
INSERT INTO ocds.contracting_templates (country,pmd,language,template_id,template) VALUES ('MD','TEST_OT','en','cs-supplier-approval-on','{"id": "cs-supplier-approval-on-","description": "Supplier has to sign the contract","title": "Signature by the Supplier","type": "approval","relatesTo": "document","requestTitle": "parties[role:supplier].persones[role:authority].","requestDescription": "Defined person has to sign the transferred document"}');
INSERT INTO ocds.contracting_templates (country,pmd,language,template_id,template) VALUES ('MD','TEST_OT','en','cs-buyer-activate-on','{"id": "cs-buyer-activate-on-","description": "Buyer has to activate the contract","title": "Contract activation","type": "approval","relatesTo": "document","requestTitle": "parties[role:buyer].persones[role:authority].","requestDescription": "Defined person has to sign the transferred document"}');
INSERT INTO ocds.contracting_templates (country,pmd,language,template_id,template) VALUES ('MD','TEST_OT','en','cs-treasury-validate-on','{"id": "cs-treasury-validate-on-","description": "Treasury has to approve the contract","title": "Contract validation","type": "approval","relatesTo": "document","requestTitle": "parties[role:treasury].persones[role:authority].","requestDescription": "Defined person has to sign the transferred document"}');
INSERT INTO ocds.contracting_templates (country,pmd,language,template_id,template) VALUES ('MD','TEST_OT','en','verification_rationale','This document shall be signed electronically in accordance with Law no. 91 of 29.05.2014. Signature verification can be done at https://msign.gov.md');

INSERT INTO ocds.contracting_templates (country,pmd,language,template_id,template) VALUES ('MD','SV','en','cs-buyer-confirmation-on','{"id": "cs-buyer-confirmation-on-", "description": "Buyer has to sign the transferred document", "title": "Document signing", "type": "digitalSignature", "relatesTo": "document", "requestTitle": "parties[role:buyer].persones[role:authority].", "requestDescription": "Defined person has to sign the transferred document"}');
INSERT INTO ocds.contracting_templates (country,pmd,language,template_id,template) VALUES ('MD','SV','en','cs-tenderer-confirmation-on','{"id": "cs-tenderer-confirmation-on-", "description": "Supplier has to sign the transferred document", "title": "Document signing", "type": "digitalSignature", "relatesTo": "document", "requestTitle": "parties[role:supplier].persones[role:authority].", "requestDescription": "Defined person has to sign the transferred document"}');
INSERT INTO ocds.contracting_templates (country,pmd,language,template_id,template) VALUES ('MD','SV','en','cs-buyer-approval-on','{"id": "cs-buyer-approval-on-","description": "Buyer has to sign the contract","title": "Signature by the Buyer","type": "approval","relatesTo": "document","requestTitle": "parties[role:buyer].persones[role:authority].","requestDescription": "Defined person has to sign the transferred document"}');
INSERT INTO ocds.contracting_templates (country,pmd,language,template_id,template) VALUES ('MD','SV','en','cs-supplier-approval-on','{"id": "cs-supplier-approval-on-","description": "Supplier has to sign the contract","title": "Signature by the Supplier","type": "approval","relatesTo": "document","requestTitle": "parties[role:supplier].persones[role:authority].","requestDescription": "Defined person has to sign the transferred document"}');
INSERT INTO ocds.contracting_templates (country,pmd,language,template_id,template) VALUES ('MD','SV','en','cs-buyer-activate-on','{"id": "cs-buyer-activate-on-","description": "Buyer has to activate the contract","title": "Contract activation","type": "approval","relatesTo": "document","requestTitle": "parties[role:buyer].persones[role:authority].","requestDescription": "Defined person has to sign the transferred document"}');
INSERT INTO ocds.contracting_templates (country,pmd,language,template_id,template) VALUES ('MD','SV','en','cs-treasury-validate-on','{"id": "cs-treasury-validate-on-","description": "Treasury has to approve the contract","title": "Contract validation","type": "approval","relatesTo": "document","requestTitle": "parties[role:treasury].persones[role:authority].","requestDescription": "Defined person has to sign the transferred document"}');
INSERT INTO ocds.contracting_templates (country,pmd,language,template_id,template) VALUES ('MD','SV','en','verification_rationale','This document shall be signed electronically in accordance with Law no. 91 of 29.05.2014. Signature verification can be done at https://msign.gov.md');

INSERT INTO ocds.contracting_templates (country,pmd,language,template_id,template) VALUES ('MD','TEST_SV','en','cs-buyer-confirmation-on','{"id": "cs-buyer-confirmation-on-", "description": "Buyer has to sign the transferred document", "title": "Document signing", "type": "digitalSignature", "relatesTo": "document", "requestTitle": "parties[role:buyer].persones[role:authority].", "requestDescription": "Defined person has to sign the transferred document"}');
INSERT INTO ocds.contracting_templates (country,pmd,language,template_id,template) VALUES ('MD','TEST_SV','en','cs-tenderer-confirmation-on','{"id": "cs-tenderer-confirmation-on-", "description": "Supplier has to sign the transferred document", "title": "Document signing", "type": "digitalSignature", "relatesTo": "document", "requestTitle": "parties[role:supplier].persones[role:authority].", "requestDescription": "Defined person has to sign the transferred document"}');
INSERT INTO ocds.contracting_templates (country,pmd,language,template_id,template) VALUES ('MD','TEST_SV','en','cs-buyer-approval-on','{"id": "cs-buyer-approval-on-","description": "Buyer has to sign the contract","title": "Signature by the Buyer","type": "approval","relatesTo": "document","requestTitle": "parties[role:buyer].persones[role:authority].","requestDescription": "Defined person has to sign the transferred document"}');
INSERT INTO ocds.contracting_templates (country,pmd,language,template_id,template) VALUES ('MD','TEST_SV','en','cs-supplier-approval-on','{"id": "cs-supplier-approval-on-","description": "Supplier has to sign the contract","title": "Signature by the Supplier","type": "approval","relatesTo": "document","requestTitle": "parties[role:supplier].persones[role:authority].","requestDescription": "Defined person has to sign the transferred document"}');
INSERT INTO ocds.contracting_templates (country,pmd,language,template_id,template) VALUES ('MD','TEST_SV','en','cs-buyer-activate-on','{"id": "cs-buyer-activate-on-","description": "Buyer has to activate the contract","title": "Contract activation","type": "approval","relatesTo": "document","requestTitle": "parties[role:buyer].persones[role:authority].","requestDescription": "Defined person has to sign the transferred document"}');
INSERT INTO ocds.contracting_templates (country,pmd,language,template_id,template) VALUES ('MD','TEST_SV','en','cs-treasury-validate-on','{"id": "cs-treasury-validate-on-","description": "Treasury has to approve the contract","title": "Contract validation","type": "approval","relatesTo": "document","requestTitle": "parties[role:treasury].persones[role:authority].","requestDescription": "Defined person has to sign the transferred document"}');
INSERT INTO ocds.contracting_templates (country,pmd,language,template_id,template) VALUES ('MD','TEST_SV','en','verification_rationale','This document shall be signed electronically in accordance with Law no. 91 of 29.05.2014. Signature verification can be done at https://msign.gov.md');

INSERT INTO ocds.contracting_templates (country,pmd,language,template_id,template) VALUES ('MD','OT','ro','cs-buyer-confirmation-on','{"id": "cs-buyer-confirmation-on-", "description": "Buyer has to sign the transferred document", "title": "Document signing", "type": "digitalSignature", "relatesTo": "document", "requestTitle": "parties[role:buyer].persones[role:authority].", "requestDescription": "Defined person has to sign the transferred document"}');
INSERT INTO ocds.contracting_templates (country,pmd,language,template_id,template) VALUES ('MD','OT','ro','cs-tenderer-confirmation-on','{"id": "cs-tenderer-confirmation-on-", "description": "Supplier has to sign the transferred document", "title": "Document signing", "type": "digitalSignature", "relatesTo": "document", "requestTitle": "parties[role:supplier].persones[role:authority].", "requestDescription": "Defined person has to sign the transferred document"}');
INSERT INTO ocds.contracting_templates (country,pmd,language,template_id,template) VALUES ('MD','OT','ro','cs-buyer-approval-on','{"id": "cs-buyer-approval-on-","description": "Buyer has to sign the contract","title": "Signature by the Buyer","type": "approval","relatesTo": "document","requestTitle": "parties[role:buyer].persones[role:authority].","requestDescription": "Defined person has to sign the transferred document"}');
INSERT INTO ocds.contracting_templates (country,pmd,language,template_id,template) VALUES ('MD','OT','ro','cs-supplier-approval-on','{"id": "cs-supplier-approval-on-","description": "Supplier has to sign the contract","title": "Signature by the Supplier","type": "approval","relatesTo": "document","requestTitle": "parties[role:supplier].persones[role:authority].","requestDescription": "Defined person has to sign the transferred document"}');
INSERT INTO ocds.contracting_templates (country,pmd,language,template_id,template) VALUES ('MD','OT','ro','cs-buyer-activate-on','{"id": "cs-buyer-activate-on-","description": "Buyer has to activate the contract","title": "Contract activation","type": "approval","relatesTo": "document","requestTitle": "parties[role:buyer].persones[role:authority].","requestDescription": "Defined person has to sign the transferred document"}');
INSERT INTO ocds.contracting_templates (country,pmd,language,template_id,template) VALUES ('MD','OT','ro','cs-treasury-validate-on','{"id": "cs-treasury-validate-on-","description": "Treasury has to approve the contract","title": "Contract validation","type": "approval","relatesTo": "document","requestTitle": "parties[role:treasury].persones[role:authority].","requestDescription": "Defined person has to sign the transferred document"}');
INSERT INTO ocds.contracting_templates (country,pmd,language,template_id,template) VALUES ('MD','OT','ro','verification_rationale','Acest document se semnează electronic în conformitate cu Legea nr. 91 din 29.05.2014. Verificarea semnăturii poate fi realizată la adresa https://msign.gov.md');

INSERT INTO ocds.contracting_templates (country,pmd,language,template_id,template) VALUES ('MD','TEST_OT','ro','cs-buyer-confirmation-on','{"id": "cs-buyer-confirmation-on-", "description": "Buyer has to sign the transferred document", "title": "Document signing", "type": "digitalSignature", "relatesTo": "document", "requestTitle": "parties[role:buyer].persones[role:authority].", "requestDescription": "Defined person has to sign the transferred document"}');
INSERT INTO ocds.contracting_templates (country,pmd,language,template_id,template) VALUES ('MD','TEST_OT','ro','cs-tenderer-confirmation-on','{"id": "cs-tenderer-confirmation-on-", "description": "Supplier has to sign the transferred document", "title": "Document signing", "type": "digitalSignature", "relatesTo": "document", "requestTitle": "parties[role:supplier].persones[role:authority].", "requestDescription": "Defined person has to sign the transferred document"}');
INSERT INTO ocds.contracting_templates (country,pmd,language,template_id,template) VALUES ('MD','TEST_OT','ro','cs-buyer-approval-on','{"id": "cs-buyer-approval-on-","description": "Buyer has to sign the contract","title": "Signature by the Buyer","type": "approval","relatesTo": "document","requestTitle": "parties[role:buyer].persones[role:authority].","requestDescription": "Defined person has to sign the transferred document"}');
INSERT INTO ocds.contracting_templates (country,pmd,language,template_id,template) VALUES ('MD','TEST_OT','ro','cs-supplier-approval-on','{"id": "cs-supplier-approval-on-","description": "Supplier has to sign the contract","title": "Signature by the Supplier","type": "approval","relatesTo": "document","requestTitle": "parties[role:supplier].persones[role:authority].","requestDescription": "Defined person has to sign the transferred document"}');
INSERT INTO ocds.contracting_templates (country,pmd,language,template_id,template) VALUES ('MD','TEST_OT','ro','cs-buyer-activate-on','{"id": "cs-buyer-activate-on-","description": "Buyer has to activate the contract","title": "Contract activation","type": "approval","relatesTo": "document","requestTitle": "parties[role:buyer].persones[role:authority].","requestDescription": "Defined person has to sign the transferred document"}');
INSERT INTO ocds.contracting_templates (country,pmd,language,template_id,template) VALUES ('MD','TEST_OT','ro','cs-treasury-validate-on','{"id": "cs-treasury-validate-on-","description": "Treasury has to approve the contract","title": "Contract validation","type": "approval","relatesTo": "document","requestTitle": "parties[role:treasury].persones[role:authority].","requestDescription": "Defined person has to sign the transferred document"}');
INSERT INTO ocds.contracting_templates (country,pmd,language,template_id,template) VALUES ('MD','TEST_OT','ro','verification_rationale','Acest document se semnează electronic în conformitate cu Legea nr. 91 din 29.05.2014. Verificarea semnăturii poate fi realizată la adresa https://msign.gov.md');

INSERT INTO ocds.contracting_templates (country,pmd,language,template_id,template) VALUES ('MD','SV','ro','cs-buyer-confirmation-on','{"id": "cs-buyer-confirmation-on-", "description": "Buyer has to sign the transferred document", "title": "Document signing", "type": "digitalSignature", "relatesTo": "document", "requestTitle": "parties[role:buyer].persones[role:authority].", "requestDescription": "Defined person has to sign the transferred document"}');
INSERT INTO ocds.contracting_templates (country,pmd,language,template_id,template) VALUES ('MD','SV','ro','cs-tenderer-confirmation-on','{"id": "cs-tenderer-confirmation-on-", "description": "Supplier has to sign the transferred document", "title": "Document signing", "type": "digitalSignature", "relatesTo": "document", "requestTitle": "parties[role:supplier].persones[role:authority].", "requestDescription": "Defined person has to sign the transferred document"}');
INSERT INTO ocds.contracting_templates (country,pmd,language,template_id,template) VALUES ('MD','SV','ro','cs-buyer-approval-on','{"id": "cs-buyer-approval-on-","description": "Buyer has to sign the contract","title": "Signature by the Buyer","type": "approval","relatesTo": "document","requestTitle": "parties[role:buyer].persones[role:authority].","requestDescription": "Defined person has to sign the transferred document"}');
INSERT INTO ocds.contracting_templates (country,pmd,language,template_id,template) VALUES ('MD','SV','ro','cs-supplier-approval-on','{"id": "cs-supplier-approval-on-","description": "Supplier has to sign the contract","title": "Signature by the Supplier","type": "approval","relatesTo": "document","requestTitle": "parties[role:supplier].persones[role:authority].","requestDescription": "Defined person has to sign the transferred document"}');
INSERT INTO ocds.contracting_templates (country,pmd,language,template_id,template) VALUES ('MD','SV','ro','cs-buyer-activate-on','{"id": "cs-buyer-activate-on-","description": "Buyer has to activate the contract","title": "Contract activation","type": "approval","relatesTo": "document","requestTitle": "parties[role:buyer].persones[role:authority].","requestDescription": "Defined person has to sign the transferred document"}');
INSERT INTO ocds.contracting_templates (country,pmd,language,template_id,template) VALUES ('MD','SV','ro','cs-treasury-validate-on','{"id": "cs-treasury-validate-on-","description": "Treasury has to approve the contract","title": "Contract validation","type": "approval","relatesTo": "document","requestTitle": "parties[role:treasury].persones[role:authority].","requestDescription": "Defined person has to sign the transferred document"}');
INSERT INTO ocds.contracting_templates (country,pmd,language,template_id,template) VALUES ('MD','SV','ro','verification_rationale','Acest document se semnează electronic în conformitate cu Legea nr. 91 din 29.05.2014. Verificarea semnăturii poate fi realizată la adresa https://msign.gov.md');

INSERT INTO ocds.contracting_templates (country,pmd,language,template_id,template) VALUES ('MD','TEST_SV','ro','cs-buyer-confirmation-on','{"id": "cs-buyer-confirmation-on-", "description": "Buyer has to sign the transferred document", "title": "Document signing", "type": "digitalSignature", "relatesTo": "document", "requestTitle": "parties[role:buyer].persones[role:authority].", "requestDescription": "Defined person has to sign the transferred document"}');
INSERT INTO ocds.contracting_templates (country,pmd,language,template_id,template) VALUES ('MD','TEST_SV','ro','cs-tenderer-confirmation-on','{"id": "cs-tenderer-confirmation-on-", "description": "Supplier has to sign the transferred document", "title": "Document signing", "type": "digitalSignature", "relatesTo": "document", "requestTitle": "parties[role:supplier].persones[role:authority].", "requestDescription": "Defined person has to sign the transferred document"}');
INSERT INTO ocds.contracting_templates (country,pmd,language,template_id,template) VALUES ('MD','TEST_SV','ro','cs-buyer-approval-on','{"id": "cs-buyer-approval-on-","description": "Buyer has to sign the contract","title": "Signature by the Buyer","type": "approval","relatesTo": "document","requestTitle": "parties[role:buyer].persones[role:authority].","requestDescription": "Defined person has to sign the transferred document"}');
INSERT INTO ocds.contracting_templates (country,pmd,language,template_id,template) VALUES ('MD','TEST_SV','ro','cs-supplier-approval-on','{"id": "cs-supplier-approval-on-","description": "Supplier has to sign the contract","title": "Signature by the Supplier","type": "approval","relatesTo": "document","requestTitle": "parties[role:supplier].persones[role:authority].","requestDescription": "Defined person has to sign the transferred document"}');
INSERT INTO ocds.contracting_templates (country,pmd,language,template_id,template) VALUES ('MD','TEST_SV','ro','cs-buyer-activate-on','{"id": "cs-buyer-activate-on-","description": "Buyer has to activate the contract","title": "Contract activation","type": "approval","relatesTo": "document","requestTitle": "parties[role:buyer].persones[role:authority].","requestDescription": "Defined person has to sign the transferred document"}');
INSERT INTO ocds.contracting_templates (country,pmd,language,template_id,template) VALUES ('MD','TEST_SV','ro','cs-treasury-validate-on','{"id": "cs-treasury-validate-on-","description": "Treasury has to approve the contract","title": "Contract validation","type": "approval","relatesTo": "document","requestTitle": "parties[role:treasury].persones[role:authority].","requestDescription": "Defined person has to sign the transferred document"}');
INSERT INTO ocds.contracting_templates (country,pmd,language,template_id,template) VALUES ('MD','TEST_SV','ro','verification_rationale','Acest document se semnează electronic în conformitate cu Legea nr. 91 din 29.05.2014. Verificarea semnăturii poate fi realizată la adresa https://msign.gov.md');

INSERT INTO ocds.contracting_templates (country,pmd,language,template_id,template) VALUES ('MD','GPA','en','cs-buyer-activate-on','{"id": "cs-buyer-activate-on-","description": "Buyer has to activate the contract","title": "Contract activation","type": "approval","relatesTo": "document","requestTitle": "parties[role:buyer].persones[role:authority].","requestDescription": "Defined person has to sign the transferred document"}');
INSERT INTO ocds.contracting_templates (country,pmd,language,template_id,template) VALUES ('MD','GPA','en','cs-buyer-approval-on','{"id": "cs-buyer-approval-on-","description": "Buyer has to sign the contract","title": "Signature by the Buyer","type": "approval","relatesTo": "document","requestTitle": "parties[role:buyer].persones[role:authority].","requestDescription": "Defined person has to sign the transferred document"}');
INSERT INTO ocds.contracting_templates (country,pmd,language,template_id,template) VALUES ('MD','GPA','en','cs-buyer-confirmation-on','{"id": "cs-buyer-confirmation-on-", "description": "Buyer has to sign the transferred document", "title": "Document signing", "type": "digitalSignature", "relatesTo": "document", "requestTitle": "parties[role:buyer].persones[role:authority].", "requestDescription": "Defined person has to sign the transferred document"}');
INSERT INTO ocds.contracting_templates (country,pmd,language,template_id,template) VALUES ('MD','GPA','en','cs-supplier-approval-on','{"id": "cs-supplier-approval-on-","description": "Supplier has to sign the contract","title": "Signature by the Supplier","type": "approval","relatesTo": "document","requestTitle": "parties[role:supplier].persones[role:authority].","requestDescription": "Defined person has to sign the transferred document"}');
INSERT INTO ocds.contracting_templates (country,pmd,language,template_id,template) VALUES ('MD','GPA','en','cs-tenderer-confirmation-on','{"id": "cs-tenderer-confirmation-on-", "description": "Supplier has to sign the transferred document", "title": "Document signing", "type": "digitalSignature", "relatesTo": "document", "requestTitle": "parties[role:supplier].persones[role:authority].", "requestDescription": "Defined person has to sign the transferred document"}');
INSERT INTO ocds.contracting_templates (country,pmd,language,template_id,template) VALUES ('MD','GPA','en','cs-treasury-validate-on','{"id": "cs-treasury-validate-on-","description": "Treasury has to approve the contract","title": "Contract validation","type": "approval","relatesTo": "document","requestTitle": "parties[role:treasury].persones[role:authority].","requestDescription": "Defined person has to sign the transferred document"}');
INSERT INTO ocds.contracting_templates (country,pmd,language,template_id,template) VALUES ('MD','GPA','en','verification_rationale','This document shall be signed electronically in accordance with Law no. 91 of 29.05.2014. Signature verification can be done at https://msign.gov.md');
INSERT INTO ocds.contracting_templates (country,pmd,language,template_id,template) VALUES ('MD','GPA','ro','cs-buyer-activate-on','{"id": "cs-buyer-activate-on-","description": "Buyer has to activate the contract","title": "Contract activation","type": "approval","relatesTo": "document","requestTitle": "parties[role:buyer].persones[role:authority].","requestDescription": "Defined person has to sign the transferred document"}');
INSERT INTO ocds.contracting_templates (country,pmd,language,template_id,template) VALUES ('MD','GPA','ro','cs-buyer-approval-on','{"id": "cs-buyer-approval-on-","description": "Buyer has to sign the contract","title": "Signature by the Buyer","type": "approval","relatesTo": "document","requestTitle": "parties[role:buyer].persones[role:authority].","requestDescription": "Defined person has to sign the transferred document"}');
INSERT INTO ocds.contracting_templates (country,pmd,language,template_id,template) VALUES ('MD','GPA','ro','cs-buyer-confirmation-on','{"id": "cs-buyer-confirmation-on-", "description": "Buyer has to sign the transferred document", "title": "Document signing", "type": "digitalSignature", "relatesTo": "document", "requestTitle": "parties[role:buyer].persones[role:authority].", "requestDescription": "Defined person has to sign the transferred document"}');
INSERT INTO ocds.contracting_templates (country,pmd,language,template_id,template) VALUES ('MD','GPA','ro','cs-supplier-approval-on','{"id": "cs-supplier-approval-on-","description": "Supplier has to sign the contract","title": "Signature by the Supplier","type": "approval","relatesTo": "document","requestTitle": "parties[role:supplier].persones[role:authority].","requestDescription": "Defined person has to sign the transferred document"}');
INSERT INTO ocds.contracting_templates (country,pmd,language,template_id,template) VALUES ('MD','GPA','ro','cs-tenderer-confirmation-on','{"id": "cs-tenderer-confirmation-on-", "description": "Supplier has to sign the transferred document", "title": "Document signing", "type": "digitalSignature", "relatesTo": "document", "requestTitle": "parties[role:supplier].persones[role:authority].", "requestDescription": "Defined person has to sign the transferred document"}');
INSERT INTO ocds.contracting_templates (country,pmd,language,template_id,template) VALUES ('MD','GPA','ro','cs-treasury-validate-on','{"id": "cs-treasury-validate-on-","description": "Treasury has to approve the contract","title": "Contract validation","type": "approval","relatesTo": "document","requestTitle": "parties[role:treasury].persones[role:authority].","requestDescription": "Defined person has to sign the transferred document"}');
INSERT INTO ocds.contracting_templates (country,pmd,language,template_id,template) VALUES ('MD','GPA','ro','verification_rationale','Acest document se semnează electronic în conformitate cu Legea nr. 91 din 29.05.2014. Verificarea semnăturii poate fi realizată la adresa https://msign.gov.md');

INSERT INTO ocds.contracting_templates (country,pmd,language,template_id,template) VALUES ('MD','TEST_GPA','en','cs-buyer-activate-on','{"id": "cs-buyer-activate-on-","description": "Buyer has to activate the contract","title": "Contract activation","type": "approval","relatesTo": "document","requestTitle": "parties[role:buyer].persones[role:authority].","requestDescription": "Defined person has to sign the transferred document"}');
INSERT INTO ocds.contracting_templates (country,pmd,language,template_id,template) VALUES ('MD','TEST_GPA','en','cs-buyer-approval-on','{"id": "cs-buyer-approval-on-","description": "Buyer has to sign the contract","title": "Signature by the Buyer","type": "approval","relatesTo": "document","requestTitle": "parties[role:buyer].persones[role:authority].","requestDescription": "Defined person has to sign the transferred document"}');
INSERT INTO ocds.contracting_templates (country,pmd,language,template_id,template) VALUES ('MD','TEST_GPA','en','cs-buyer-confirmation-on','{"id": "cs-buyer-confirmation-on-", "description": "Buyer has to sign the transferred document", "title": "Document signing", "type": "digitalSignature", "relatesTo": "document", "requestTitle": "parties[role:buyer].persones[role:authority].", "requestDescription": "Defined person has to sign the transferred document"}');
INSERT INTO ocds.contracting_templates (country,pmd,language,template_id,template) VALUES ('MD','TEST_GPA','en','cs-supplier-approval-on','{"id": "cs-supplier-approval-on-","description": "Supplier has to sign the contract","title": "Signature by the Supplier","type": "approval","relatesTo": "document","requestTitle": "parties[role:supplier].persones[role:authority].","requestDescription": "Defined person has to sign the transferred document"}');
INSERT INTO ocds.contracting_templates (country,pmd,language,template_id,template) VALUES ('MD','TEST_GPA','en','cs-tenderer-confirmation-on','{"id": "cs-tenderer-confirmation-on-", "description": "Supplier has to sign the transferred document", "title": "Document signing", "type": "digitalSignature", "relatesTo": "document", "requestTitle": "parties[role:supplier].persones[role:authority].", "requestDescription": "Defined person has to sign the transferred document"}');
INSERT INTO ocds.contracting_templates (country,pmd,language,template_id,template) VALUES ('MD','TEST_GPA','en','cs-treasury-validate-on','{"id": "cs-treasury-validate-on-","description": "Treasury has to approve the contract","title": "Contract validation","type": "approval","relatesTo": "document","requestTitle": "parties[role:treasury].persones[role:authority].","requestDescription": "Defined person has to sign the transferred document"}');
INSERT INTO ocds.contracting_templates (country,pmd,language,template_id,template) VALUES ('MD','TEST_GPA','en','verification_rationale','This document shall be signed electronically in accordance with Law no. 91 of 29.05.2014. Signature verification can be done at https://msign.gov.md');
INSERT INTO ocds.contracting_templates (country,pmd,language,template_id,template) VALUES ('MD','TEST_GPA','ro','cs-buyer-activate-on','{"id": "cs-buyer-activate-on-","description": "Buyer has to activate the contract","title": "Contract activation","type": "approval","relatesTo": "document","requestTitle": "parties[role:buyer].persones[role:authority].","requestDescription": "Defined person has to sign the transferred document"}');
INSERT INTO ocds.contracting_templates (country,pmd,language,template_id,template) VALUES ('MD','TEST_GPA','ro','cs-buyer-approval-on','{"id": "cs-buyer-approval-on-","description": "Buyer has to sign the contract","title": "Signature by the Buyer","type": "approval","relatesTo": "document","requestTitle": "parties[role:buyer].persones[role:authority].","requestDescription": "Defined person has to sign the transferred document"}');
INSERT INTO ocds.contracting_templates (country,pmd,language,template_id,template) VALUES ('MD','TEST_GPA','ro','cs-buyer-confirmation-on','{"id": "cs-buyer-confirmation-on-", "description": "Buyer has to sign the transferred document", "title": "Document signing", "type": "digitalSignature", "relatesTo": "document", "requestTitle": "parties[role:buyer].persones[role:authority].", "requestDescription": "Defined person has to sign the transferred document"}');
INSERT INTO ocds.contracting_templates (country,pmd,language,template_id,template) VALUES ('MD','TEST_GPA','ro','cs-supplier-approval-on','{"id": "cs-supplier-approval-on-","description": "Supplier has to sign the contract","title": "Signature by the Supplier","type": "approval","relatesTo": "document","requestTitle": "parties[role:supplier].persones[role:authority].","requestDescription": "Defined person has to sign the transferred document"}');
INSERT INTO ocds.contracting_templates (country,pmd,language,template_id,template) VALUES ('MD','TEST_GPA','ro','cs-tenderer-confirmation-on','{"id": "cs-tenderer-confirmation-on-", "description": "Supplier has to sign the transferred document", "title": "Document signing", "type": "digitalSignature", "relatesTo": "document", "requestTitle": "parties[role:supplier].persones[role:authority].", "requestDescription": "Defined person has to sign the transferred document"}');
INSERT INTO ocds.contracting_templates (country,pmd,language,template_id,template) VALUES ('MD','TEST_GPA','ro','cs-treasury-validate-on','{"id": "cs-treasury-validate-on-","description": "Treasury has to approve the contract","title": "Contract validation","type": "approval","relatesTo": "document","requestTitle": "parties[role:treasury].persones[role:authority].","requestDescription": "Defined person has to sign the transferred document"}');
INSERT INTO ocds.contracting_templates (country,pmd,language,template_id,template) VALUES ('MD','TEST_GPA','ro','verification_rationale','Acest document se semnează electronic în conformitate cu Legea nr. 91 din 29.05.2014. Verificarea semnăturii poate fi realizată la adresa https://msign.gov.md');

CREATE TABLE IF NOT EXISTS ocds.contracting_fc
(
    cpid           TEXT,
    ocid           TEXT,
    id             TEXT,
    token_entity   TEXT,
    owner          TEXT,
    created_date   TIMESTAMP,
    status         TEXT,
    status_details TEXT,
    json_data      TEXT,
    PRIMARY KEY (cpid, ocid, id)
);

CREATE TABLE IF NOT EXISTS ocds.contracting_rules
(
    country        TEXT,
    pmd            TEXT,
    operation_type TEXT,
    parameter      TEXT,
    value          TEXT,
    PRIMARY KEY (country, pmd, operation_type, parameter)
);

CREATE TABLE IF NOT EXISTS ocds.contracting_pac
(
    cpid           TEXT,
    ocid           TEXT,
    id             TEXT,
    token_entity   TEXT,
    owner          TEXT,
    created_date   TIMESTAMP,
    status         TEXT,
    status_details TEXT,
    json_data      TEXT,
    PRIMARY KEY (cpid, ocid, id)
);

ALTER TABLE ocds.contracting_pac DROP token_entity;

CREATE TABLE IF NOT EXISTS ocds.contracting_confirmation_requests
(
    cpid           TEXT,
    ocid           TEXT,
    contract_id    TEXT,
    id             TEXT,
    requests       set<TEXT>,
    json_data      TEXT,
    PRIMARY KEY (cpid, ocid, contract_id, id)
);

CREATE TABLE IF NOT EXISTS ocds.contracting_confirmation_responses
(
    cpid           TEXT,
    ocid           TEXT,
    contract_id    TEXT,
    id             TEXT,
    request_id     TEXT,
    json_data      TEXT,
    PRIMARY KEY (cpid, ocid, contract_id, id)
);

DROP TABLE IF EXISTS ocds.contracting_rules;

CREATE TABLE IF NOT EXISTS ocds.contracting_rules
(
    key            TEXT,
    parameter      TEXT,
    value          TEXT,
    PRIMARY KEY (key, parameter)
    );

INSERT INTO ocds.contracting_rules (key,parameter,value) VALUES ('MD-CF-applyConfirmations-PC','validContractStates','[{"status":{"value":"pending"},"statusDetails":{"value":"signed"}}]');
INSERT INTO ocds.contracting_rules (key,parameter,value) VALUES ('MD-OF-applyConfirmations-PC','validContractStates','[{"status":{"value":"pending"},"statusDetails":{"value":"signed"}}]');
INSERT INTO ocds.contracting_rules (key,parameter,value) VALUES ('MD-TEST_CF-applyConfirmations-PC','validContractStates','[{"status":{"value":"pending"},"statusDetails":{"value":"signed"}}]');
INSERT INTO ocds.contracting_rules (key,parameter,value) VALUES ('MD-TEST_OF-applyConfirmations-PC','validContractStates','[{"status":{"value":"pending"},"statusDetails":{"value":"signed"}}]');