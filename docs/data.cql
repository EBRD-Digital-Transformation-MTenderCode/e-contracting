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
