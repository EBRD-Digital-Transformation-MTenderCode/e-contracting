CREATE KEYSPACE IF NOT EXISTS ocds WITH replication = {'class':'SimpleStrategy', 'replication_factor':1};


CREATE TABLE IF NOT EXISTS  ocds.contract_award_notice (
    cp_id UUID,
    can_id UUID,
    award_id text,
    owner text,
    awarded_contract_id UUID,
    status text,
    status_details text,
    primary key(cp_id,can_id)
);

CREATE TABLE IF NOT EXISTS  ocds.contract_award (
    cp_id text,
    id text,
    owner text,
    release_date timestamp,
    status text,
    json_data text,
    primary key((cp_id,id,owner),release_date)
)WITH CLUSTERING ORDER BY (release_date DESC);
