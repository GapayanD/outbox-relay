CREATE USER orders_svc PASSWORD 'orders'; CREATE USER enrichment_svc PASSWORD 'enrichment';
CREATE SCHEMA orders AUTHORIZATION orders_svc;
CREATE SCHEMA enrichment AUTHORIZATION enrichment_svc;
