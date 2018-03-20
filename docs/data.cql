CREATE KEYSPACE IF NOT EXISTS ocds WITH replication = {'class':'SimpleStrategy', 'replication_factor':1};


CREATE TABLE IF NOT EXISTS  ocds.contracting_contract_award_notice (
    cp_id UUID,
    can_id UUID,
    ac_id UUID,
    award_id text,
    owner text,
    status text,
    status_details text,
    primary key(cp_id,can_id,award_id)
);

CREATE TABLE IF NOT EXISTS  ocds.contracting_awarded_contract (
    cp_id UUID,
    ac_id UUID,
    can_id UUID,
    owner text,
    release_date timestamp,
    status text,
    status_details text,
    json_data text,
    primary key((cp_id,ac_id),release_date)
)WITH CLUSTERING ORDER BY (release_date DESC);
