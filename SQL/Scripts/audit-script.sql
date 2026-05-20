-- =========================================================
-- GLOBAL AUDIT SYSTEM
-- =========================================================

-- =====================================
-- AUDIT TABLE
-- =====================================

CREATE TABLE IF NOT EXISTS audit_log (
    audit_id BIGSERIAL PRIMARY KEY,

    schema_name TEXT NOT NULL,
    table_name TEXT NOT NULL,

    operation TEXT NOT NULL,

    changed_by TEXT DEFAULT session_user,
    changed_at TIMESTAMPTZ DEFAULT now(),

    old_data JSONB,
    new_data JSONB
);

-- =====================================
-- GENERIC AUDIT FUNCTION
-- =====================================

CREATE OR REPLACE FUNCTION fn_audit_all_tables()
RETURNS trigger AS $$
BEGIN

    -- INSERT
    IF TG_OP = 'INSERT' THEN

        INSERT INTO audit_log(
            schema_name,
            table_name,
            operation,
            new_data
        )
        VALUES (
            TG_TABLE_SCHEMA,
            TG_TABLE_NAME,
            TG_OP,
            to_jsonb(NEW)
        );

        RETURN NEW;

    -- UPDATE
    ELSIF TG_OP = 'UPDATE' THEN

        INSERT INTO audit_log(
            schema_name,
            table_name,
            operation,
            old_data,
            new_data
        )
        VALUES (
            TG_TABLE_SCHEMA,
            TG_TABLE_NAME,
            TG_OP,
            to_jsonb(OLD),
            to_jsonb(NEW)
        );

        RETURN NEW;

    -- DELETE
    ELSIF TG_OP = 'DELETE' THEN

        INSERT INTO audit_log(
            schema_name,
            table_name,
            operation,
            old_data
        )
        VALUES (
            TG_TABLE_SCHEMA,
            TG_TABLE_NAME,
            TG_OP,
            to_jsonb(OLD)
        );

        RETURN OLD;

    END IF;

    RETURN NULL;

END;
$$ LANGUAGE plpgsql;

-- =====================================
-- AUTO CREATE TRIGGERS FOR ALL TABLES
-- =====================================

DO $$
DECLARE
    r RECORD;
BEGIN

    FOR r IN
        SELECT tablename
        FROM pg_tables
        WHERE schemaname = 'public'

        -- EXCLUDE AUDIT TABLE
        AND tablename <> 'audit_log'
    LOOP

        -- DROP OLD TRIGGER IF EXISTS
        EXECUTE format(
            'DROP TRIGGER IF EXISTS trg_audit_%I ON %I;',
            r.tablename,
            r.tablename
        );

        -- CREATE NEW TRIGGER
        EXECUTE format(
            '
            CREATE TRIGGER trg_audit_%I
            AFTER INSERT OR UPDATE OR DELETE
            ON %I
            FOR EACH ROW
            EXECUTE FUNCTION fn_audit_all_tables();
            ',
            r.tablename,
            r.tablename
        );

    END LOOP;

END $$;

-- =====================================
-- TRUNCATE AUDIT TABLE
-- =====================================

CREATE TABLE IF NOT EXISTS truncate_audit_log (
    truncate_audit_id BIGSERIAL PRIMARY KEY,

    schema_name TEXT,
    table_name TEXT,

    operation TEXT,

    changed_by TEXT DEFAULT session_user,
    changed_at TIMESTAMPTZ DEFAULT now()
);

-- =====================================
-- TRUNCATE FUNCTION
-- =====================================

CREATE OR REPLACE FUNCTION fn_audit_truncate()
RETURNS trigger AS $$
BEGIN

    INSERT INTO truncate_audit_log(
        schema_name,
        table_name,
        operation
    )
    VALUES (
        TG_TABLE_SCHEMA,
        TG_TABLE_NAME,
        TG_OP
    );

    RETURN NULL;

END;
$$ LANGUAGE plpgsql;

-- =====================================
-- AUTO CREATE TRUNCATE TRIGGERS
-- =====================================

DO $$
DECLARE
    r RECORD;
BEGIN

    FOR r IN
        SELECT tablename
        FROM pg_tables
        WHERE schemaname = 'public'
        AND tablename NOT IN (
            'audit_log',
            'truncate_audit_log'
        )
    LOOP

        EXECUTE format(
            'DROP TRIGGER IF EXISTS trg_truncate_audit_%I ON %I;',
            r.tablename,
            r.tablename
        );

        EXECUTE format(
            '
            CREATE TRIGGER trg_truncate_audit_%I
            AFTER TRUNCATE
            ON %I
            FOR EACH STATEMENT
            EXECUTE FUNCTION fn_audit_truncate();
            ',
            r.tablename,
            r.tablename
        );

    END LOOP;

END $$;

-- =====================================
-- DDL AUDIT TABLE
-- =====================================

CREATE TABLE IF NOT EXISTS ddl_audit_log (
    ddl_audit_id BIGSERIAL PRIMARY KEY,

    event_type TEXT,
    object_type TEXT,

    schema_name TEXT,
    object_identity TEXT,

    executed_by TEXT DEFAULT session_user,
    executed_at TIMESTAMPTZ DEFAULT now()
);

-- =====================================
-- DDL AUDIT FUNCTION
-- =====================================

CREATE OR REPLACE FUNCTION fn_audit_ddl()
RETURNS event_trigger AS $$
DECLARE
    obj RECORD;
BEGIN

    FOR obj IN
        SELECT *
        FROM pg_event_trigger_ddl_commands()
    LOOP

        INSERT INTO ddl_audit_log(
            event_type,
            object_type,
            schema_name,
            object_identity
        )
        VALUES (
            TG_TAG,
            obj.object_type,
            obj.schema_name,
            obj.object_identity
        );

    END LOOP;

END;
$$ LANGUAGE plpgsql;

-- =====================================
-- CREATE DDL EVENT TRIGGER
-- =====================================

DROP EVENT TRIGGER IF EXISTS trg_audit_ddl;

CREATE EVENT TRIGGER trg_audit_ddl
ON ddl_command_end
EXECUTE FUNCTION fn_audit_ddl();