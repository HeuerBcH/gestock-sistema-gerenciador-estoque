DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_roles WHERE rolname = 'gestock'
    ) THEN
        CREATE ROLE gestock WITH LOGIN PASSWORD 'gestock';
    END IF;
END
$$;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_database WHERE datname = 'gestock'
    ) THEN
        CREATE DATABASE gestock OWNER gestock;
    END IF;
END
$$;
