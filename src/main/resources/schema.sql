DROP TABLE IF EXISTS meta_cost;
DROP TABLE IF EXISTS snapchat_cost;
DROP TABLE IF EXISTS revenue_table;

CREATE TABLE meta_cost (
    campaign_id VARCHAR(255),
    campaign_name VARCHAR(255),
    status VARCHAR(50),
    spend DOUBLE,
    impressions BIGINT,
    event_time_la TIMESTAMP,
    ingested_at TIMESTAMP
);

CREATE TABLE snapchat_cost (
    campaign_id VARCHAR(255),
    campaign_name VARCHAR(255),
    status VARCHAR(50),
    spend DOUBLE,
    impressions BIGINT,
    event_time_la TIMESTAMP,
    ingested_at TIMESTAMP
);

CREATE TABLE revenue_table (
    campaign_name VARCHAR(255),
    clicks BIGINT,
    unique_clicks BIGINT,
    conversions BIGINT,
    lp_views BIGINT,
    lp_clicks BIGINT,
    revenue DOUBLE,
    ingested_at TIMESTAMP
);
