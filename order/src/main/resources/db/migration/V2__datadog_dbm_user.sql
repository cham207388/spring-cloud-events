DO $$
BEGIN
    IF NOT EXISTS (SELECT FROM pg_catalog.pg_roles WHERE rolname = 'datadog') THEN
        CREATE USER datadog WITH PASSWORD 'datadog_password';
    END IF;
END $$;

GRANT pg_monitor TO datadog;
GRANT SELECT ON ALL TABLES IN SCHEMA orders_space TO datadog;
