CREATE KEYSPACE IF NOT EXISTS ocds WITH replication = {'class':'SimpleStrategy', 'replication_factor':1};


CREATE TABLE IF NOT EXISTS  ocds.contracting_contract_notice (
    cp_id text,
    token_entity UUID,
    ac_id UUID,
    award_id text,
    owner text,
    status text,
    status_details text,
    primary key(cp_id, token_entity, award_id)
);

CREATE TABLE IF NOT EXISTS  ocds.contracting_contract (
    cp_id text,
    token_entity UUID,
    can_id UUID,
    owner text,
    created_date timestamp,
    status text,
    status_details text,
    json_data text,
    primary key(cp_id, token_entity)
);
