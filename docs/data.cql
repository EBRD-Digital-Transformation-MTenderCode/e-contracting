CREATE KEYSPACE IF NOT EXISTS ocds WITH replication = {'class':'SimpleStrategy', 'replication_factor':1};


CREATE TABLE IF NOT EXISTS  ocds.contracting_notice (
    cp_id text,
    stage text,
    can_id UUID,
    award_id text,
    ac_id text,
    created_date timestamp,
    owner text,
    status text,
    status_details text,
    primary key(cp_id, stage, can_id)
);

CREATE TABLE IF NOT EXISTS  ocds.contracting_contract (
    cp_id text,
    stage text,
    token_entity UUID,
    owner text,
    can_id text,
    created_date timestamp,
    status text,
    status_details text,
    json_data text,
    primary key(cp_id, stage, token_entity)
);

CREATE TABLE IF NOT EXISTS  ocds.contracting_history (
    operation_id text,
    command text,
    operation_date timestamp,
    json_data text,
    primary key(operation_id, command)
);
