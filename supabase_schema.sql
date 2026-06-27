--
-- PostgreSQL database dump
--

\restrict OuXNc0eLarZi23fMfkVitcnVYOhjBhpcnTNMxVLr7ohoZbudTMPPlm0oK9aZ9XC

-- Dumped from database version 17.6
-- Dumped by pg_dump version 17.10 (Ubuntu 17.10-1.pgdg24.04+1)

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET transaction_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- Name: auth; Type: SCHEMA; Schema: -; Owner: -
--

CREATE SCHEMA auth;


--
-- Name: extensions; Type: SCHEMA; Schema: -; Owner: -
--

CREATE SCHEMA extensions;


--
-- Name: graphql; Type: SCHEMA; Schema: -; Owner: -
--

CREATE SCHEMA graphql;


--
-- Name: graphql_public; Type: SCHEMA; Schema: -; Owner: -
--

CREATE SCHEMA graphql_public;


--
-- Name: pgbouncer; Type: SCHEMA; Schema: -; Owner: -
--

CREATE SCHEMA pgbouncer;


--
-- Name: realtime; Type: SCHEMA; Schema: -; Owner: -
--

CREATE SCHEMA realtime;


--
-- Name: storage; Type: SCHEMA; Schema: -; Owner: -
--

CREATE SCHEMA storage;


--
-- Name: vault; Type: SCHEMA; Schema: -; Owner: -
--

CREATE SCHEMA vault;


--
-- Name: pg_stat_statements; Type: EXTENSION; Schema: -; Owner: -
--

CREATE EXTENSION IF NOT EXISTS pg_stat_statements WITH SCHEMA extensions;


--
-- Name: EXTENSION pg_stat_statements; Type: COMMENT; Schema: -; Owner: -
--

COMMENT ON EXTENSION pg_stat_statements IS 'track planning and execution statistics of all SQL statements executed';


--
-- Name: pgcrypto; Type: EXTENSION; Schema: -; Owner: -
--

CREATE EXTENSION IF NOT EXISTS pgcrypto WITH SCHEMA extensions;


--
-- Name: EXTENSION pgcrypto; Type: COMMENT; Schema: -; Owner: -
--

COMMENT ON EXTENSION pgcrypto IS 'cryptographic functions';


--
-- Name: supabase_vault; Type: EXTENSION; Schema: -; Owner: -
--

CREATE EXTENSION IF NOT EXISTS supabase_vault WITH SCHEMA vault;


--
-- Name: EXTENSION supabase_vault; Type: COMMENT; Schema: -; Owner: -
--

COMMENT ON EXTENSION supabase_vault IS 'Supabase Vault Extension';


--
-- Name: uuid-ossp; Type: EXTENSION; Schema: -; Owner: -
--

CREATE EXTENSION IF NOT EXISTS "uuid-ossp" WITH SCHEMA extensions;


--
-- Name: EXTENSION "uuid-ossp"; Type: COMMENT; Schema: -; Owner: -
--

COMMENT ON EXTENSION "uuid-ossp" IS 'generate universally unique identifiers (UUIDs)';


--
-- Name: vector; Type: EXTENSION; Schema: -; Owner: -
--

CREATE EXTENSION IF NOT EXISTS vector WITH SCHEMA public;


--
-- Name: EXTENSION vector; Type: COMMENT; Schema: -; Owner: -
--

COMMENT ON EXTENSION vector IS 'vector data type and ivfflat and hnsw access methods';


--
-- Name: aal_level; Type: TYPE; Schema: auth; Owner: -
--

CREATE TYPE auth.aal_level AS ENUM (
    'aal1',
    'aal2',
    'aal3'
);


--
-- Name: code_challenge_method; Type: TYPE; Schema: auth; Owner: -
--

CREATE TYPE auth.code_challenge_method AS ENUM (
    's256',
    'plain'
);


--
-- Name: factor_status; Type: TYPE; Schema: auth; Owner: -
--

CREATE TYPE auth.factor_status AS ENUM (
    'unverified',
    'verified'
);


--
-- Name: factor_type; Type: TYPE; Schema: auth; Owner: -
--

CREATE TYPE auth.factor_type AS ENUM (
    'totp',
    'webauthn',
    'phone'
);


--
-- Name: oauth_authorization_status; Type: TYPE; Schema: auth; Owner: -
--

CREATE TYPE auth.oauth_authorization_status AS ENUM (
    'pending',
    'approved',
    'denied',
    'expired'
);


--
-- Name: oauth_client_type; Type: TYPE; Schema: auth; Owner: -
--

CREATE TYPE auth.oauth_client_type AS ENUM (
    'public',
    'confidential'
);


--
-- Name: oauth_registration_type; Type: TYPE; Schema: auth; Owner: -
--

CREATE TYPE auth.oauth_registration_type AS ENUM (
    'dynamic',
    'manual'
);


--
-- Name: oauth_response_type; Type: TYPE; Schema: auth; Owner: -
--

CREATE TYPE auth.oauth_response_type AS ENUM (
    'code'
);


--
-- Name: one_time_token_type; Type: TYPE; Schema: auth; Owner: -
--

CREATE TYPE auth.one_time_token_type AS ENUM (
    'confirmation_token',
    'reauthentication_token',
    'recovery_token',
    'email_change_token_new',
    'email_change_token_current',
    'phone_change_token'
);


--
-- Name: expense_reference_type; Type: TYPE; Schema: public; Owner: -
--

CREATE TYPE public.expense_reference_type AS ENUM (
    'Bus',
    'ChildOrganization',
    'Salary'
);


--
-- Name: gender_type; Type: TYPE; Schema: public; Owner: -
--

CREATE TYPE public.gender_type AS ENUM (
    'male',
    'female',
    'other'
);


--
-- Name: period_name_enum; Type: TYPE; Schema: public; Owner: -
--

CREATE TYPE public.period_name_enum AS ENUM (
    'Period 1',
    'Period 2',
    'Period 3',
    'Period 4',
    'Period 5',
    'Period 6',
    'Period 7',
    'Period 8',
    'Period 9',
    'Period 10',
    'Period 11',
    'Period 12',
    'Period 13',
    'Period 14',
    'Period 15',
    'Period 16',
    'Period 17',
    'Period 18',
    'Period 19',
    'Period 20',
    'Half Time'
);


--
-- Name: release_platform; Type: TYPE; Schema: public; Owner: -
--

CREATE TYPE public.release_platform AS ENUM (
    'android',
    'window'
);


--
-- Name: release_type; Type: TYPE; Schema: public; Owner: -
--

CREATE TYPE public.release_type AS ENUM (
    'beta',
    'stable',
    'latest'
);


--
-- Name: student_attendance_status_enum; Type: TYPE; Schema: public; Owner: -
--

CREATE TYPE public.student_attendance_status_enum AS ENUM (
    'Present',
    'Absent',
    'Late',
    'Half Day'
);


--
-- Name: action; Type: TYPE; Schema: realtime; Owner: -
--

CREATE TYPE realtime.action AS ENUM (
    'INSERT',
    'UPDATE',
    'DELETE',
    'TRUNCATE',
    'ERROR'
);


--
-- Name: equality_op; Type: TYPE; Schema: realtime; Owner: -
--

CREATE TYPE realtime.equality_op AS ENUM (
    'eq',
    'neq',
    'lt',
    'lte',
    'gt',
    'gte',
    'in'
);


--
-- Name: user_defined_filter; Type: TYPE; Schema: realtime; Owner: -
--

CREATE TYPE realtime.user_defined_filter AS (
	column_name text,
	op realtime.equality_op,
	value text
);


--
-- Name: wal_column; Type: TYPE; Schema: realtime; Owner: -
--

CREATE TYPE realtime.wal_column AS (
	name text,
	type_name text,
	type_oid oid,
	value jsonb,
	is_pkey boolean,
	is_selectable boolean
);


--
-- Name: wal_rls; Type: TYPE; Schema: realtime; Owner: -
--

CREATE TYPE realtime.wal_rls AS (
	wal jsonb,
	is_rls_enabled boolean,
	subscription_ids uuid[],
	errors text[]
);


--
-- Name: buckettype; Type: TYPE; Schema: storage; Owner: -
--

CREATE TYPE storage.buckettype AS ENUM (
    'STANDARD',
    'ANALYTICS',
    'VECTOR'
);


--
-- Name: email(); Type: FUNCTION; Schema: auth; Owner: -
--

CREATE FUNCTION auth.email() RETURNS text
    LANGUAGE sql STABLE
    AS $$
  select 
  coalesce(
    nullif(current_setting('request.jwt.claim.email', true), ''),
    (nullif(current_setting('request.jwt.claims', true), '')::jsonb ->> 'email')
  )::text
$$;


--
-- Name: FUNCTION email(); Type: COMMENT; Schema: auth; Owner: -
--

COMMENT ON FUNCTION auth.email() IS 'Deprecated. Use auth.jwt() -> ''email'' instead.';


--
-- Name: jwt(); Type: FUNCTION; Schema: auth; Owner: -
--

CREATE FUNCTION auth.jwt() RETURNS jsonb
    LANGUAGE sql STABLE
    AS $$
  select 
    coalesce(
        nullif(current_setting('request.jwt.claim', true), ''),
        nullif(current_setting('request.jwt.claims', true), '')
    )::jsonb
$$;


--
-- Name: role(); Type: FUNCTION; Schema: auth; Owner: -
--

CREATE FUNCTION auth.role() RETURNS text
    LANGUAGE sql STABLE
    AS $$
  select 
  coalesce(
    nullif(current_setting('request.jwt.claim.role', true), ''),
    (nullif(current_setting('request.jwt.claims', true), '')::jsonb ->> 'role')
  )::text
$$;


--
-- Name: FUNCTION role(); Type: COMMENT; Schema: auth; Owner: -
--

COMMENT ON FUNCTION auth.role() IS 'Deprecated. Use auth.jwt() -> ''role'' instead.';


--
-- Name: uid(); Type: FUNCTION; Schema: auth; Owner: -
--

CREATE FUNCTION auth.uid() RETURNS uuid
    LANGUAGE sql STABLE
    AS $$
  select 
  coalesce(
    nullif(current_setting('request.jwt.claim.sub', true), ''),
    (nullif(current_setting('request.jwt.claims', true), '')::jsonb ->> 'sub')
  )::uuid
$$;


--
-- Name: FUNCTION uid(); Type: COMMENT; Schema: auth; Owner: -
--

COMMENT ON FUNCTION auth.uid() IS 'Deprecated. Use auth.jwt() -> ''sub'' instead.';


--
-- Name: grant_pg_cron_access(); Type: FUNCTION; Schema: extensions; Owner: -
--

CREATE FUNCTION extensions.grant_pg_cron_access() RETURNS event_trigger
    LANGUAGE plpgsql
    AS $$
BEGIN
  IF EXISTS (
    SELECT
    FROM pg_event_trigger_ddl_commands() AS ev
    JOIN pg_extension AS ext
    ON ev.objid = ext.oid
    WHERE ext.extname = 'pg_cron'
  )
  THEN
    grant usage on schema cron to postgres with grant option;

    alter default privileges in schema cron grant all on tables to postgres with grant option;
    alter default privileges in schema cron grant all on functions to postgres with grant option;
    alter default privileges in schema cron grant all on sequences to postgres with grant option;

    alter default privileges for user supabase_admin in schema cron grant all
        on sequences to postgres with grant option;
    alter default privileges for user supabase_admin in schema cron grant all
        on tables to postgres with grant option;
    alter default privileges for user supabase_admin in schema cron grant all
        on functions to postgres with grant option;

    grant all privileges on all tables in schema cron to postgres with grant option;
    revoke all on table cron.job from postgres;
    grant select on table cron.job to postgres with grant option;
  END IF;
END;
$$;


--
-- Name: FUNCTION grant_pg_cron_access(); Type: COMMENT; Schema: extensions; Owner: -
--

COMMENT ON FUNCTION extensions.grant_pg_cron_access() IS 'Grants access to pg_cron';


--
-- Name: grant_pg_graphql_access(); Type: FUNCTION; Schema: extensions; Owner: -
--

CREATE FUNCTION extensions.grant_pg_graphql_access() RETURNS event_trigger
    LANGUAGE plpgsql
    AS $_$
begin
    if not exists (
        select 1
        from pg_event_trigger_ddl_commands() ev
        join pg_catalog.pg_extension e on ev.objid = e.oid
        where e.extname = 'pg_graphql'
    ) then
        return;
    end if;

    drop function if exists graphql_public.graphql;
    create or replace function graphql_public.graphql(
        "operationName" text default null,
        query text default null,
        variables jsonb default null,
        extensions jsonb default null
    )
        returns jsonb
        language sql
    as $$
        select graphql.resolve(
            query := query,
            variables := coalesce(variables, '{}'),
            "operationName" := "operationName",
            extensions := extensions
        );
    $$;

    -- Attach the wrapper to the extension so DROP EXTENSION cascades to it,
    -- which in turn triggers set_graphql_placeholder to reinstall the "not enabled" stub.
    alter extension pg_graphql add function graphql_public.graphql(text, text, jsonb, jsonb);

    grant usage on schema graphql to postgres, anon, authenticated, service_role;
    grant execute on function graphql.resolve to postgres, anon, authenticated, service_role;
    grant usage on schema graphql to postgres with grant option;
    grant usage on schema graphql_public to postgres with grant option;
end;
$_$;


--
-- Name: FUNCTION grant_pg_graphql_access(); Type: COMMENT; Schema: extensions; Owner: -
--

COMMENT ON FUNCTION extensions.grant_pg_graphql_access() IS 'Grants access to pg_graphql';


--
-- Name: grant_pg_net_access(); Type: FUNCTION; Schema: extensions; Owner: -
--

CREATE FUNCTION extensions.grant_pg_net_access() RETURNS event_trigger
    LANGUAGE plpgsql
    AS $$
BEGIN
  IF EXISTS (
    SELECT 1
    FROM pg_event_trigger_ddl_commands() AS ev
    JOIN pg_extension AS ext
    ON ev.objid = ext.oid
    WHERE ext.extname = 'pg_net'
  )
  THEN
    IF NOT EXISTS (
      SELECT 1
      FROM pg_roles
      WHERE rolname = 'supabase_functions_admin'
    )
    THEN
      CREATE USER supabase_functions_admin NOINHERIT CREATEROLE LOGIN NOREPLICATION;
    END IF;

    GRANT USAGE ON SCHEMA net TO supabase_functions_admin, postgres, anon, authenticated, service_role;

    IF EXISTS (
      SELECT FROM pg_extension
      WHERE extname = 'pg_net'
      -- all versions in use on existing projects as of 2025-02-20
      -- version 0.12.0 onwards don't need these applied
      AND extversion IN ('0.2', '0.6', '0.7', '0.7.1', '0.8', '0.10.0', '0.11.0')
    ) THEN
      ALTER function net.http_get(url text, params jsonb, headers jsonb, timeout_milliseconds integer) SECURITY DEFINER;
      ALTER function net.http_post(url text, body jsonb, params jsonb, headers jsonb, timeout_milliseconds integer) SECURITY DEFINER;

      ALTER function net.http_get(url text, params jsonb, headers jsonb, timeout_milliseconds integer) SET search_path = net;
      ALTER function net.http_post(url text, body jsonb, params jsonb, headers jsonb, timeout_milliseconds integer) SET search_path = net;

      REVOKE ALL ON FUNCTION net.http_get(url text, params jsonb, headers jsonb, timeout_milliseconds integer) FROM PUBLIC;
      REVOKE ALL ON FUNCTION net.http_post(url text, body jsonb, params jsonb, headers jsonb, timeout_milliseconds integer) FROM PUBLIC;

      GRANT EXECUTE ON FUNCTION net.http_get(url text, params jsonb, headers jsonb, timeout_milliseconds integer) TO supabase_functions_admin, postgres, anon, authenticated, service_role;
      GRANT EXECUTE ON FUNCTION net.http_post(url text, body jsonb, params jsonb, headers jsonb, timeout_milliseconds integer) TO supabase_functions_admin, postgres, anon, authenticated, service_role;
    END IF;
  END IF;
END;
$$;


--
-- Name: FUNCTION grant_pg_net_access(); Type: COMMENT; Schema: extensions; Owner: -
--

COMMENT ON FUNCTION extensions.grant_pg_net_access() IS 'Grants access to pg_net';


--
-- Name: pgrst_ddl_watch(); Type: FUNCTION; Schema: extensions; Owner: -
--

CREATE FUNCTION extensions.pgrst_ddl_watch() RETURNS event_trigger
    LANGUAGE plpgsql
    AS $$
DECLARE
  cmd record;
BEGIN
  FOR cmd IN SELECT * FROM pg_event_trigger_ddl_commands()
  LOOP
    IF cmd.command_tag IN (
      'CREATE SCHEMA', 'ALTER SCHEMA'
    , 'CREATE TABLE', 'CREATE TABLE AS', 'SELECT INTO', 'ALTER TABLE'
    , 'CREATE FOREIGN TABLE', 'ALTER FOREIGN TABLE'
    , 'CREATE VIEW', 'ALTER VIEW'
    , 'CREATE MATERIALIZED VIEW', 'ALTER MATERIALIZED VIEW'
    , 'CREATE FUNCTION', 'ALTER FUNCTION'
    , 'CREATE TRIGGER'
    , 'CREATE TYPE', 'ALTER TYPE'
    , 'CREATE RULE'
    , 'COMMENT'
    )
    -- don't notify in case of CREATE TEMP table or other objects created on pg_temp
    AND cmd.schema_name is distinct from 'pg_temp'
    THEN
      NOTIFY pgrst, 'reload schema';
    END IF;
  END LOOP;
END; $$;


--
-- Name: pgrst_drop_watch(); Type: FUNCTION; Schema: extensions; Owner: -
--

CREATE FUNCTION extensions.pgrst_drop_watch() RETURNS event_trigger
    LANGUAGE plpgsql
    AS $$
DECLARE
  obj record;
BEGIN
  FOR obj IN SELECT * FROM pg_event_trigger_dropped_objects()
  LOOP
    IF obj.object_type IN (
      'schema'
    , 'table'
    , 'foreign table'
    , 'view'
    , 'materialized view'
    , 'function'
    , 'trigger'
    , 'type'
    , 'rule'
    )
    AND obj.is_temporary IS false -- no pg_temp objects
    THEN
      NOTIFY pgrst, 'reload schema';
    END IF;
  END LOOP;
END; $$;


--
-- Name: set_graphql_placeholder(); Type: FUNCTION; Schema: extensions; Owner: -
--

CREATE FUNCTION extensions.set_graphql_placeholder() RETURNS event_trigger
    LANGUAGE plpgsql
    AS $_$
    DECLARE
    graphql_is_dropped bool;
    BEGIN
    graphql_is_dropped = (
        SELECT ev.schema_name = 'graphql_public'
        FROM pg_event_trigger_dropped_objects() AS ev
        WHERE ev.schema_name = 'graphql_public'
    );

    IF graphql_is_dropped
    THEN
        create or replace function graphql_public.graphql(
            "operationName" text default null,
            query text default null,
            variables jsonb default null,
            extensions jsonb default null
        )
            returns jsonb
            language plpgsql
        as $$
            DECLARE
                server_version float;
            BEGIN
                server_version = (SELECT (SPLIT_PART((select version()), ' ', 2))::float);

                IF server_version >= 14 THEN
                    RETURN jsonb_build_object(
                        'errors', jsonb_build_array(
                            jsonb_build_object(
                                'message', 'pg_graphql extension is not enabled.'
                            )
                        )
                    );
                ELSE
                    RETURN jsonb_build_object(
                        'errors', jsonb_build_array(
                            jsonb_build_object(
                                'message', 'pg_graphql is only available on projects running Postgres 14 onwards.'
                            )
                        )
                    );
                END IF;
            END;
        $$;
    END IF;

    END;
$_$;


--
-- Name: FUNCTION set_graphql_placeholder(); Type: COMMENT; Schema: extensions; Owner: -
--

COMMENT ON FUNCTION extensions.set_graphql_placeholder() IS 'Reintroduces placeholder function for graphql_public.graphql';


--
-- Name: graphql(text, text, jsonb, jsonb); Type: FUNCTION; Schema: graphql_public; Owner: -
--

CREATE FUNCTION graphql_public.graphql("operationName" text DEFAULT NULL::text, query text DEFAULT NULL::text, variables jsonb DEFAULT NULL::jsonb, extensions jsonb DEFAULT NULL::jsonb) RETURNS jsonb
    LANGUAGE plpgsql
    AS $$
            DECLARE
                server_version float;
            BEGIN
                server_version = (SELECT (SPLIT_PART((select version()), ' ', 2))::float);

                IF server_version >= 14 THEN
                    RETURN jsonb_build_object(
                        'errors', jsonb_build_array(
                            jsonb_build_object(
                                'message', 'pg_graphql extension is not enabled.'
                            )
                        )
                    );
                ELSE
                    RETURN jsonb_build_object(
                        'errors', jsonb_build_array(
                            jsonb_build_object(
                                'message', 'pg_graphql is only available on projects running Postgres 14 onwards.'
                            )
                        )
                    );
                END IF;
            END;
        $$;


--
-- Name: get_auth(text); Type: FUNCTION; Schema: pgbouncer; Owner: -
--

CREATE FUNCTION pgbouncer.get_auth(p_usename text) RETURNS TABLE(username text, password text)
    LANGUAGE plpgsql SECURITY DEFINER
    SET search_path TO ''
    AS $_$
  BEGIN
      RAISE DEBUG 'PgBouncer auth request: %', p_usename;

      RETURN QUERY
      SELECT
          rolname::text,
          CASE WHEN rolvaliduntil < now()
              THEN null
              ELSE rolpassword::text
          END
      FROM pg_authid
      WHERE rolname=$1 and rolcanlogin;
  END;
  $_$;


--
-- Name: check_message_cooldown(); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION public.check_message_cooldown() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
DECLARE
  cooldown_limit integer;
  last_message_time timestamp with time zone;
BEGIN
  -- सुरक्षा के लिए क्लाइंट के भेजे टाइमस्टैम्प को ओवरराइड करके सर्वर का वर्तमान समय सेट करें
  NEW.created_at = now();
  NEW.updated_at = now();
  NEW.is_deleted = false;
  NEW.is_hidden = false;

  -- चर्चा कक्ष का कूलडाउन लिमिट प्राप्त करें
  SELECT message_cooldown_seconds INTO cooldown_limit
  FROM public.rooms
  WHERE id = NEW.room_id;

  -- यदि कूलडाउन सेट नहीं है, तो डिफ़ॉल्ट 5 सेकंड लें
  IF cooldown_limit IS NULL THEN
    cooldown_limit := 5;
  END IF;

  -- यूज़र द्वारा इस कक्ष में भेजे गए आखिरी एक्टिव संदेश का समय प्राप्त करें
  SELECT created_at INTO last_message_time
  FROM public.messages
  WHERE room_id = NEW.room_id
    AND user_id = NEW.user_id
    AND is_deleted = false
  ORDER BY created_at DESC
  LIMIT 1;

  -- यदि पिछला संदेश भेजा गया है, तो जाँच करें कि क्या कूलडाउन का उल्लंघन हुआ है
  IF last_message_time IS NOT NULL THEN
    IF now() < last_message_time + (cooldown_limit * INTERVAL '1 second') THEN
      RAISE EXCEPTION 'Spam Protection: कृपया % सेकंड प्रतीक्षा करें। संदेश भेजने की सीमा का उल्लंघन हुआ है।', cooldown_limit;
    END IF;
  END IF;

  RETURN NEW;
END;
$$;


--
-- Name: clean_old_messages_trigger(); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION public.clean_old_messages_trigger() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
BEGIN
  DELETE FROM public.messages
  WHERE created_at < now() - INTERVAL '15 minutes';
  RETURN NEW;
END;
$$;


--
-- Name: create_user_profile(); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION public.create_user_profile() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
begin

    insert into public.user_profiles (
        user_id
    )
    values (
        new.id
    );

    return new;

end;
$$;


--
-- Name: generate_full_name(); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION public.generate_full_name() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
BEGIN
    NEW.full_name :=
        trim(
            concat_ws(
                ' ',
                NEW.first_name,
                NEW.last_name
            )
        );

    RETURN NEW;
END;
$$;


--
-- Name: handle_new_user(); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION public.handle_new_user() RETURNS trigger
    LANGUAGE plpgsql SECURITY DEFINER
    AS $$
begin

    insert into public.users (
        auth_id,
        email
    )
    values (
        new.id,
        new.email
    );

    return new;

end;
$$;


--
-- Name: handle_update_timestamp(); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION public.handle_update_timestamp() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
BEGIN
    NEW.updated_at = now();
    RETURN NEW;
END;
$$;


--
-- Name: handle_updated_at(); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION public.handle_updated_at() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
BEGIN
  NEW.updated_at = now();
  RETURN NEW;
END;
$$;


--
-- Name: rls_auto_enable(); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION public.rls_auto_enable() RETURNS event_trigger
    LANGUAGE plpgsql SECURITY DEFINER
    SET search_path TO 'pg_catalog'
    AS $$
DECLARE
  cmd record;
BEGIN
  FOR cmd IN
    SELECT *
    FROM pg_event_trigger_ddl_commands()
    WHERE command_tag IN ('CREATE TABLE', 'CREATE TABLE AS', 'SELECT INTO')
      AND object_type IN ('table','partitioned table')
  LOOP
     IF cmd.schema_name IS NOT NULL AND cmd.schema_name IN ('public') AND cmd.schema_name NOT IN ('pg_catalog','information_schema') AND cmd.schema_name NOT LIKE 'pg_toast%' AND cmd.schema_name NOT LIKE 'pg_temp%' THEN
      BEGIN
        EXECUTE format('alter table if exists %s enable row level security', cmd.object_identity);
        RAISE LOG 'rls_auto_enable: enabled RLS on %', cmd.object_identity;
      EXCEPTION
        WHEN OTHERS THEN
          RAISE LOG 'rls_auto_enable: failed to enable RLS on %', cmd.object_identity;
      END;
     ELSE
        RAISE LOG 'rls_auto_enable: skip % (either system schema or not in enforced list: %.)', cmd.object_identity, cmd.schema_name;
     END IF;
  END LOOP;
END;
$$;


--
-- Name: set_audit_fields(); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION public.set_audit_fields() RETURNS trigger
    LANGUAGE plpgsql SECURITY DEFINER
    AS $$
DECLARE
  v_user_id uuid;
  v_new_json jsonb;
BEGIN
  -- वर्तमान लॉग-इन ऑथेंटिकेटेड यूजर (auth.uid) के आधार पर public.users.id प्राप्त करें
  SELECT id INTO v_user_id
  FROM public.users
  WHERE auth_id = auth.uid()
  LIMIT 1;

  -- यदि लॉग-इन यूजर मिल जाता है (यानी एपीआई अनुरोध ऑथेंटिकेटेड है)
  IF v_user_id IS NOT NULL THEN
    -- NEW रिकॉर्ड को डायनेमिक JSONB में बदलें ताकि कंपाइल-टाइम कॉलम एरर से बचा जा सके
    v_new_json := to_jsonb(NEW);
    
    -- प्रविष्टि (INSERT) के समय
    IF TG_OP = 'INSERT' THEN
      -- यदि 'created_by' कॉलम तालिका में मौजूद है और खाली है
      IF v_new_json ? 'created_by' AND (v_new_json->>'created_by') IS NULL THEN
        v_new_json := jsonb_set(v_new_json, '{created_by}', to_jsonb(v_user_id));
      END IF;
      
      -- यदि 'updated_by' कॉलम तालिका में मौजूद है और खाली है
      IF v_new_json ? 'updated_by' AND (v_new_json->>'updated_by') IS NULL THEN
        v_new_json := jsonb_set(v_new_json, '{updated_by}', to_jsonb(v_user_id));
      END IF;
      
    -- अपडेट (UPDATE) के समय
    ELSIF TG_OP = 'UPDATE' THEN
      -- यदि 'updated_by' कॉलम तालिका में मौजूद है
      IF v_new_json ? 'updated_by' THEN
        v_new_json := jsonb_set(v_new_json, '{updated_by}', to_jsonb(v_user_id));
      END IF;
    END IF;

    -- संशोधित JSONB डेटा को वापस NEW रिकॉर्ड में लोड करें
    NEW := jsonb_populate_record(NEW, v_new_json);
  END IF;

  RETURN NEW;
END;
$$;


--
-- Name: set_only_one_latest_version(); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION public.set_only_one_latest_version() RETURNS trigger
    LANGUAGE plpgsql
    AS $$BEGIN
  IF NEW.is_latest = true THEN
    UPDATE public.app_versions
    SET is_latest = false
    WHERE platform = NEW.platform
      AND id != NEW.id;
  END IF;
  RETURN NEW;
END;$$;


--
-- Name: set_updated_at(); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION public.set_updated_at() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
BEGIN
    NEW.updated_at = now();
    RETURN NEW;
END;
$$;


--
-- Name: update_updated_at_column(); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION public.update_updated_at_column() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
begin
    new.updated_at = now();
    return new;
end;
$$;


--
-- Name: apply_rls(jsonb, integer); Type: FUNCTION; Schema: realtime; Owner: -
--

CREATE FUNCTION realtime.apply_rls(wal jsonb, max_record_bytes integer DEFAULT (1024 * 1024)) RETURNS SETOF realtime.wal_rls
    LANGUAGE plpgsql
    AS $$
declare
    -- Regclass of the table e.g. public.notes
    entity_ regclass = (quote_ident(wal ->> 'schema') || '.' || quote_ident(wal ->> 'table'))::regclass;

    -- I, U, D, T: insert, update ...
    action realtime.action = (
        case wal ->> 'action'
            when 'I' then 'INSERT'
            when 'U' then 'UPDATE'
            when 'D' then 'DELETE'
            else 'ERROR'
        end
    );

    -- Is row level security enabled for the table
    is_rls_enabled bool = relrowsecurity from pg_class where oid = entity_;

    subscriptions realtime.subscription[] = array_agg(subs)
        from
            realtime.subscription subs
        where
            subs.entity = entity_
            -- Filter by action early - only get subscriptions interested in this action
            -- action_filter column can be: '*' (all), 'INSERT', 'UPDATE', or 'DELETE'
            and (subs.action_filter = '*' or subs.action_filter = action::text);

    -- Subscription vars
    working_role regrole;
    working_selected_columns text[];
    claimed_role regrole;
    claims jsonb;

    subscription_id uuid;
    subscription_has_access bool;
    visible_to_subscription_ids uuid[] = '{}';

    -- structured info for wal's columns
    columns realtime.wal_column[];
    -- previous identity values for update/delete
    old_columns realtime.wal_column[];

    error_record_exceeds_max_size boolean = octet_length(wal::text) > max_record_bytes;

    -- Primary jsonb output for record
    output jsonb;

    -- Loop record for iterating unique roles (outer loop)
    role_record record;
    -- Loop record for iterating unique selected_columns within a role (inner loop)
    cols_record record;
    -- Subscription ids visible at the role level (before fanning out by selected_columns)
    visible_role_sub_ids uuid[] = '{}';

begin
    perform set_config('role', null, true);

    columns =
        array_agg(
            (
                x->>'name',
                x->>'type',
                x->>'typeoid',
                realtime.cast(
                    (x->'value') #>> '{}',
                    coalesce(
                        (x->>'typeoid')::regtype, -- null when wal2json version <= 2.4
                        (x->>'type')::regtype
                    )
                ),
                (pks ->> 'name') is not null,
                true
            )::realtime.wal_column
        )
        from
            jsonb_array_elements(wal -> 'columns') x
            left join jsonb_array_elements(wal -> 'pk') pks
                on (x ->> 'name') = (pks ->> 'name');

    old_columns =
        array_agg(
            (
                x->>'name',
                x->>'type',
                x->>'typeoid',
                realtime.cast(
                    (x->'value') #>> '{}',
                    coalesce(
                        (x->>'typeoid')::regtype, -- null when wal2json version <= 2.4
                        (x->>'type')::regtype
                    )
                ),
                (pks ->> 'name') is not null,
                true
            )::realtime.wal_column
        )
        from
            jsonb_array_elements(wal -> 'identity') x
            left join jsonb_array_elements(wal -> 'pk') pks
                on (x ->> 'name') = (pks ->> 'name');

    for role_record in
        select claims_role
        from (select distinct claims_role from unnest(subscriptions)) t
        order by claims_role::text
    loop
        working_role := role_record.claims_role;

        -- Update `is_selectable` for columns and old_columns (once per role)
        columns =
            array_agg(
                (
                    c.name,
                    c.type_name,
                    c.type_oid,
                    c.value,
                    c.is_pkey,
                    pg_catalog.has_column_privilege(working_role, entity_, c.name, 'SELECT')
                )::realtime.wal_column
            )
            from
                unnest(columns) c;

        old_columns =
                array_agg(
                    (
                        c.name,
                        c.type_name,
                        c.type_oid,
                        c.value,
                        c.is_pkey,
                        pg_catalog.has_column_privilege(working_role, entity_, c.name, 'SELECT')
                    )::realtime.wal_column
                )
                from
                    unnest(old_columns) c;

        if action <> 'DELETE' and count(1) = 0 from unnest(columns) c where c.is_pkey then
            -- Fan out 400 error per distinct selected_columns for this role
            for cols_record in
                select selected_columns
                from (select distinct selected_columns from unnest(subscriptions) s where s.claims_role = working_role) t
                order by coalesce(array_to_string(selected_columns, ','), '')
            loop
                working_selected_columns := cols_record.selected_columns;
                return next (
                    jsonb_build_object(
                        'schema', wal ->> 'schema',
                        'table', wal ->> 'table',
                        'type', action
                    ),
                    is_rls_enabled,
                    (select array_agg(s.subscription_id) from unnest(subscriptions) as s where s.claims_role = working_role and (s.selected_columns is not distinct from working_selected_columns)),
                    array['Error 400: Bad Request, no primary key']
                )::realtime.wal_rls;
            end loop;

        -- The claims role does not have SELECT permission to the primary key of entity
        elsif action <> 'DELETE' and sum(c.is_selectable::int) <> count(1) from unnest(columns) c where c.is_pkey then
            -- Fan out 401 error per distinct selected_columns for this role
            for cols_record in
                select selected_columns
                from (select distinct selected_columns from unnest(subscriptions) s where s.claims_role = working_role) t
                order by coalesce(array_to_string(selected_columns, ','), '')
            loop
                working_selected_columns := cols_record.selected_columns;
                return next (
                    jsonb_build_object(
                        'schema', wal ->> 'schema',
                        'table', wal ->> 'table',
                        'type', action
                    ),
                    is_rls_enabled,
                    (select array_agg(s.subscription_id) from unnest(subscriptions) as s where s.claims_role = working_role and (s.selected_columns is not distinct from working_selected_columns)),
                    array['Error 401: Unauthorized']
                )::realtime.wal_rls;
            end loop;

        else
            -- Create the prepared statement (once per role)
            if is_rls_enabled and action <> 'DELETE' then
                if (select 1 from pg_prepared_statements where name = 'walrus_rls_stmt' limit 1) > 0 then
                    deallocate walrus_rls_stmt;
                end if;
                execute realtime.build_prepared_statement_sql('walrus_rls_stmt', entity_, columns);
            end if;

            -- Collect all visible subscription IDs for this role (filter check + RLS check)
            visible_role_sub_ids = '{}';

            for subscription_id, claims in (
                    select
                        subs.subscription_id,
                        subs.claims
                    from
                        unnest(subscriptions) subs
                    where
                        subs.entity = entity_
                        and subs.claims_role = working_role
                        and (
                            realtime.is_visible_through_filters(columns, subs.filters)
                            or (
                              action = 'DELETE'
                              and realtime.is_visible_through_filters(old_columns, subs.filters)
                            )
                        )
            ) loop

                if not is_rls_enabled or action = 'DELETE' then
                    visible_role_sub_ids = visible_role_sub_ids || subscription_id;
                else
                    -- Check if RLS allows the role to see the record
                    perform
                        -- Trim leading and trailing quotes from working_role because set_config
                        -- doesn't recognize the role as valid if they are included
                        set_config('role', trim(both '"' from working_role::text), true),
                        set_config('request.jwt.claims', claims::text, true);

                    execute 'execute walrus_rls_stmt' into subscription_has_access;

                    if subscription_has_access then
                        visible_role_sub_ids = visible_role_sub_ids || subscription_id;
                    end if;
                end if;
            end loop;

            perform set_config('role', null, true);

            -- Inner loop: per distinct selected_columns for this role
            for cols_record in
                select selected_columns
                from (select distinct selected_columns from unnest(subscriptions) s where s.claims_role = working_role) t
                order by coalesce(array_to_string(selected_columns, ','), '')
            loop
                working_selected_columns := cols_record.selected_columns;

                output = jsonb_build_object(
                    'schema', wal ->> 'schema',
                    'table', wal ->> 'table',
                    'type', action,
                    'commit_timestamp', to_char(
                        ((wal ->> 'timestamp')::timestamptz at time zone 'utc'),
                        'YYYY-MM-DD"T"HH24:MI:SS.MS"Z"'
                    ),
                    'columns', (
                        select
                            jsonb_agg(
                                jsonb_build_object(
                                    'name', pa.attname,
                                    'type', pt.typname
                                )
                                order by pa.attnum asc
                            )
                        from
                            pg_attribute pa
                            join pg_type pt
                                on pa.atttypid = pt.oid
                            left join (
                                select unnest(conkey) as pkey_attnum
                                from pg_constraint
                                where conrelid = entity_ and contype = 'p'
                            ) pk on pk.pkey_attnum = pa.attnum
                        where
                            attrelid = entity_
                            and attnum > 0
                            and pg_catalog.has_column_privilege(working_role, entity_, pa.attname, 'SELECT')
                            and (working_selected_columns is null or pa.attname = any(working_selected_columns) or pk.pkey_attnum is not null)
                    )
                )
                -- Add "record" key for insert and update
                || case
                    when action in ('INSERT', 'UPDATE') then
                        jsonb_build_object(
                            'record',
                            (
                                select
                                    jsonb_object_agg(
                                        -- if unchanged toast, get column name and value from old record
                                        coalesce((c).name, (oc).name),
                                        case
                                            when (c).name is null then (oc).value
                                            else (c).value
                                        end
                                    )
                                from
                                    unnest(columns) c
                                    full outer join unnest(old_columns) oc
                                        on (c).name = (oc).name
                                where
                                    coalesce((c).is_selectable, (oc).is_selectable)
                                    and (working_selected_columns is null or coalesce((c).name, (oc).name) = any(working_selected_columns) or coalesce((c).is_pkey, (oc).is_pkey))
                                    and ( not error_record_exceeds_max_size or (octet_length((c).value::text) <= 64))
                            )
                        )
                    else '{}'::jsonb
                end
                -- Add "old_record" key for update and delete
                || case
                    when action = 'UPDATE' then
                        jsonb_build_object(
                                'old_record',
                                (
                                    select jsonb_object_agg((c).name, (c).value)
                                    from unnest(old_columns) c
                                    where
                                        (c).is_selectable
                                        and (working_selected_columns is null or (c).name = any(working_selected_columns) or (c).is_pkey)
                                        and ( not error_record_exceeds_max_size or (octet_length((c).value::text) <= 64))
                                )
                            )
                    when action = 'DELETE' then
                        jsonb_build_object(
                            'old_record',
                            (
                                select jsonb_object_agg((c).name, (c).value)
                                from unnest(old_columns) c
                                where
                                    (c).is_selectable
                                    and (working_selected_columns is null or (c).name = any(working_selected_columns) or (c).is_pkey)
                                    and ( not error_record_exceeds_max_size or (octet_length((c).value::text) <= 64))
                                    and ( not is_rls_enabled or (c).is_pkey ) -- if RLS enabled, we can't secure deletes so filter to pkey
                            )
                        )
                    else '{}'::jsonb
                end;

                -- Filter visible_role_sub_ids to those matching the current selected_columns group
                visible_to_subscription_ids = coalesce(
                    (
                        select array_agg(s.subscription_id)
                        from unnest(subscriptions) s
                        where s.claims_role = working_role
                          and (s.selected_columns is not distinct from working_selected_columns)
                          and s.subscription_id = any(visible_role_sub_ids)
                    ),
                    '{}'::uuid[]
                );

                return next (
                    output,
                    is_rls_enabled,
                    visible_to_subscription_ids,
                    case
                        when error_record_exceeds_max_size then array['Error 413: Payload Too Large']
                        else '{}'
                    end
                )::realtime.wal_rls;
            end loop;

        end if;
    end loop;

    perform set_config('role', null, true);
end;
$$;


--
-- Name: broadcast_changes(text, text, text, text, text, record, record, text); Type: FUNCTION; Schema: realtime; Owner: -
--

CREATE FUNCTION realtime.broadcast_changes(topic_name text, event_name text, operation text, table_name text, table_schema text, new record, old record, level text DEFAULT 'ROW'::text) RETURNS void
    LANGUAGE plpgsql
    AS $$
DECLARE
    -- Declare a variable to hold the JSONB representation of the row
    row_data jsonb := '{}'::jsonb;
BEGIN
    IF level = 'STATEMENT' THEN
        RAISE EXCEPTION 'function can only be triggered for each row, not for each statement';
    END IF;
    -- Check the operation type and handle accordingly
    IF operation = 'INSERT' OR operation = 'UPDATE' OR operation = 'DELETE' THEN
        row_data := jsonb_build_object('old_record', OLD, 'record', NEW, 'operation', operation, 'table', table_name, 'schema', table_schema);
        PERFORM realtime.send (row_data, event_name, topic_name);
    ELSE
        RAISE EXCEPTION 'Unexpected operation type: %', operation;
    END IF;
EXCEPTION
    WHEN OTHERS THEN
        RAISE EXCEPTION 'Failed to process the row: %', SQLERRM;
END;

$$;


--
-- Name: build_prepared_statement_sql(text, regclass, realtime.wal_column[]); Type: FUNCTION; Schema: realtime; Owner: -
--

CREATE FUNCTION realtime.build_prepared_statement_sql(prepared_statement_name text, entity regclass, columns realtime.wal_column[]) RETURNS text
    LANGUAGE sql
    AS $$
      /*
      Builds a sql string that, if executed, creates a prepared statement to
      tests retrive a row from *entity* by its primary key columns.
      Example
          select realtime.build_prepared_statement_sql('public.notes', '{"id"}'::text[], '{"bigint"}'::text[])
      */
          select
      'prepare ' || prepared_statement_name || ' as
          select
              exists(
                  select
                      1
                  from
                      ' || entity || '
                  where
                      ' || string_agg(quote_ident(pkc.name) || '=' || quote_nullable(pkc.value #>> '{}') , ' and ') || '
              )'
          from
              unnest(columns) pkc
          where
              pkc.is_pkey
          group by
              entity
      $$;


--
-- Name: cast(text, regtype); Type: FUNCTION; Schema: realtime; Owner: -
--

CREATE FUNCTION realtime."cast"(val text, type_ regtype) RETURNS jsonb
    LANGUAGE plpgsql IMMUTABLE
    AS $$
declare
  res jsonb;
begin
  if type_::text = 'bytea' then
    return to_jsonb(val);
  end if;
  execute format('select to_jsonb(%L::'|| type_::text || ')', val) into res;
  return res;
end
$$;


--
-- Name: check_equality_op(realtime.equality_op, regtype, text, text); Type: FUNCTION; Schema: realtime; Owner: -
--

CREATE FUNCTION realtime.check_equality_op(op realtime.equality_op, type_ regtype, val_1 text, val_2 text) RETURNS boolean
    LANGUAGE plpgsql IMMUTABLE
    AS $$
      /*
      Casts *val_1* and *val_2* as type *type_* and check the *op* condition for truthiness
      */
      declare
          op_symbol text = (
              case
                  when op = 'eq' then '='
                  when op = 'neq' then '!='
                  when op = 'lt' then '<'
                  when op = 'lte' then '<='
                  when op = 'gt' then '>'
                  when op = 'gte' then '>='
                  when op = 'in' then '= any'
                  else 'UNKNOWN OP'
              end
          );
          res boolean;
      begin
          execute format(
              'select %L::'|| type_::text || ' ' || op_symbol
              || ' ( %L::'
              || (
                  case
                      when op = 'in' then type_::text || '[]'
                      else type_::text end
              )
              || ')', val_1, val_2) into res;
          return res;
      end;
      $$;


--
-- Name: is_visible_through_filters(realtime.wal_column[], realtime.user_defined_filter[]); Type: FUNCTION; Schema: realtime; Owner: -
--

CREATE FUNCTION realtime.is_visible_through_filters(columns realtime.wal_column[], filters realtime.user_defined_filter[]) RETURNS boolean
    LANGUAGE sql IMMUTABLE
    AS $_$
    /*
    Should the record be visible (true) or filtered out (false) after *filters* are applied
    */
        select
            -- Default to allowed when no filters present
            $2 is null -- no filters. this should not happen because subscriptions has a default
            or array_length($2, 1) is null -- array length of an empty array is null
            or bool_and(
                coalesce(
                    realtime.check_equality_op(
                        op:=f.op,
                        type_:=coalesce(
                            col.type_oid::regtype, -- null when wal2json version <= 2.4
                            col.type_name::regtype
                        ),
                        -- cast jsonb to text
                        val_1:=col.value #>> '{}',
                        val_2:=f.value
                    ),
                    false -- if null, filter does not match
                )
            )
        from
            unnest(filters) f
            join unnest(columns) col
                on f.column_name = col.name;
    $_$;


--
-- Name: list_changes(name, name, integer, integer); Type: FUNCTION; Schema: realtime; Owner: -
--

CREATE FUNCTION realtime.list_changes(publication name, slot_name name, max_changes integer, max_record_bytes integer) RETURNS TABLE(wal jsonb, is_rls_enabled boolean, subscription_ids uuid[], errors text[], slot_changes_count bigint)
    LANGUAGE sql
    SET log_min_messages TO 'fatal'
    AS $$
  WITH pub AS (
    SELECT
      concat_ws(
        ',',
        CASE WHEN bool_or(pubinsert) THEN 'insert' ELSE NULL END,
        CASE WHEN bool_or(pubupdate) THEN 'update' ELSE NULL END,
        CASE WHEN bool_or(pubdelete) THEN 'delete' ELSE NULL END
      ) AS w2j_actions,
      coalesce(
        string_agg(
          realtime.quote_wal2json(format('%I.%I', schemaname, tablename)::regclass),
          ','
        ) filter (WHERE ppt.tablename IS NOT NULL),
        ''
      ) AS w2j_add_tables
    FROM pg_publication pp
    LEFT JOIN pg_publication_tables ppt ON pp.pubname = ppt.pubname
    WHERE pp.pubname = publication
    GROUP BY pp.pubname
    LIMIT 1
  ),
  -- MATERIALIZED ensures pg_logical_slot_get_changes is called exactly once
  w2j AS MATERIALIZED (
    SELECT x.*, pub.w2j_add_tables
    FROM pub,
         pg_logical_slot_get_changes(
           slot_name, null, max_changes,
           'include-pk', 'true',
           'include-transaction', 'false',
           'include-timestamp', 'true',
           'include-type-oids', 'true',
           'format-version', '2',
           'actions', pub.w2j_actions,
           'add-tables', pub.w2j_add_tables
         ) x
  ),
  slot_count AS (
    SELECT count(*)::bigint AS cnt
    FROM w2j
    WHERE w2j.w2j_add_tables <> ''
  ),
  rls_filtered AS (
    SELECT xyz.wal, xyz.is_rls_enabled, xyz.subscription_ids, xyz.errors
    FROM w2j,
         realtime.apply_rls(
           wal := w2j.data::jsonb,
           max_record_bytes := max_record_bytes
         ) xyz(wal, is_rls_enabled, subscription_ids, errors)
    WHERE w2j.w2j_add_tables <> ''
      AND xyz.subscription_ids[1] IS NOT NULL
  )
  SELECT rf.wal, rf.is_rls_enabled, rf.subscription_ids, rf.errors, sc.cnt
  FROM rls_filtered rf, slot_count sc

  UNION ALL

  SELECT null, null, null, null, sc.cnt
  FROM slot_count sc
  WHERE NOT EXISTS (SELECT 1 FROM rls_filtered)
$$;


--
-- Name: quote_wal2json(regclass); Type: FUNCTION; Schema: realtime; Owner: -
--

CREATE FUNCTION realtime.quote_wal2json(entity regclass) RETURNS text
    LANGUAGE sql IMMUTABLE STRICT
    AS $$
  SELECT
    realtime.wal2json_escape_identifier(nsp.nspname::text)
    || '.'
    || realtime.wal2json_escape_identifier(pc.relname::text)
  FROM pg_class pc
  JOIN pg_namespace nsp ON pc.relnamespace = nsp.oid
  WHERE pc.oid = entity
$$;


--
-- Name: send(jsonb, text, text, boolean); Type: FUNCTION; Schema: realtime; Owner: -
--

CREATE FUNCTION realtime.send(payload jsonb, event text, topic text, private boolean DEFAULT true) RETURNS void
    LANGUAGE plpgsql
    AS $$
DECLARE
  generated_id uuid;
  final_payload jsonb;
BEGIN
  BEGIN
    generated_id := gen_random_uuid();

    -- Check if payload has an 'id' key, if not, add the generated UUID
    IF payload ? 'id' THEN
      final_payload := payload;
    ELSE
      final_payload := jsonb_set(payload, '{id}', to_jsonb(generated_id));
    END IF;

    -- Set the topic configuration
    EXECUTE format('SET LOCAL realtime.topic TO %L', topic);

    INSERT INTO realtime.messages (id, payload, event, topic, private, extension)
    VALUES (generated_id, final_payload, event, topic, private, 'broadcast');
  EXCEPTION
    WHEN OTHERS THEN
      RAISE WARNING 'WarnSendingBroadcastMessage: %', SQLERRM;
  END;
END;
$$;


--
-- Name: send_binary(bytea, text, text, boolean); Type: FUNCTION; Schema: realtime; Owner: -
--

CREATE FUNCTION realtime.send_binary(payload bytea, event text, topic text, private boolean DEFAULT true) RETURNS void
    LANGUAGE plpgsql
    AS $$
DECLARE
  generated_id uuid;
BEGIN
  BEGIN
    generated_id := gen_random_uuid();

    EXECUTE format('SET LOCAL realtime.topic TO %L', topic);

    INSERT INTO realtime.messages (id, binary_payload, event, topic, private, extension)
    VALUES (generated_id, payload, event, topic, private, 'broadcast');
  EXCEPTION
    WHEN OTHERS THEN
      RAISE WARNING 'WarnSendingBroadcastMessage: %', SQLERRM;
  END;
END;
$$;


--
-- Name: subscription_check_filters(); Type: FUNCTION; Schema: realtime; Owner: -
--

CREATE FUNCTION realtime.subscription_check_filters() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
declare
    col_names text[] = coalesce(
            array_agg(a.attname order by a.attnum),
            '{}'::text[]
        )
        from
            pg_catalog.pg_attribute a
        where
            a.attrelid = new.entity
            and a.attnum > 0
            and not a.attisdropped
            and pg_catalog.has_column_privilege(
                (new.claims ->> 'role'),
                a.attrelid,
                a.attnum,
                'SELECT'
            );
    filter realtime.user_defined_filter;
    col_type regtype;
    in_val jsonb;
    selected_col text;
begin
    for filter in select * from unnest(new.filters) loop
        if not filter.column_name = any(col_names) then
            raise exception 'invalid column for filter %', filter.column_name;
        end if;

        col_type = (
            select atttypid::regtype
            from pg_catalog.pg_attribute
            where attrelid = new.entity
                  and attname = filter.column_name
        );
        if col_type is null then
            raise exception 'failed to lookup type for column %', filter.column_name;
        end if;

        if filter.op = 'in'::realtime.equality_op then
            in_val = realtime.cast(filter.value, (col_type::text || '[]')::regtype);
            if coalesce(jsonb_array_length(in_val), 0) > 100 then
                raise exception 'too many values for `in` filter. Maximum 100';
            end if;
        else
            perform realtime.cast(filter.value, col_type);
        end if;
    end loop;

    if new.selected_columns is not null then
        for selected_col in select * from unnest(new.selected_columns) loop
            if not selected_col = any(col_names) then
                raise exception 'invalid column for select %', selected_col;
            end if;
        end loop;
    end if;

    new.filters = coalesce(
        array_agg(f order by f.column_name, f.op, f.value),
        '{}'
    ) from unnest(new.filters) f;

    new.selected_columns = (
        select array_agg(c order by c)
        from unnest(new.selected_columns) c
    );

    return new;
end;
$$;


--
-- Name: to_regrole(text); Type: FUNCTION; Schema: realtime; Owner: -
--

CREATE FUNCTION realtime.to_regrole(role_name text) RETURNS regrole
    LANGUAGE sql IMMUTABLE
    AS $$ select role_name::regrole $$;


--
-- Name: topic(); Type: FUNCTION; Schema: realtime; Owner: -
--

CREATE FUNCTION realtime.topic() RETURNS text
    LANGUAGE sql STABLE
    AS $$
select nullif(current_setting('realtime.topic', true), '')::text;
$$;


--
-- Name: wal2json_escape_identifier(text); Type: FUNCTION; Schema: realtime; Owner: -
--

CREATE FUNCTION realtime.wal2json_escape_identifier(name text) RETURNS text
    LANGUAGE sql IMMUTABLE STRICT
    AS $$
  -- Prefix `\`, `,`, `.`, and any whitespace with `\`
  SELECT regexp_replace(name, '([\\,.[:space:]])', '\\\1', 'g')
$$;


--
-- Name: allow_any_operation(text[]); Type: FUNCTION; Schema: storage; Owner: -
--

CREATE FUNCTION storage.allow_any_operation(expected_operations text[]) RETURNS boolean
    LANGUAGE sql STABLE
    AS $$
  WITH current_operation AS (
    SELECT storage.operation() AS raw_operation
  ),
  normalized AS (
    SELECT CASE
      WHEN raw_operation LIKE 'storage.%' THEN substr(raw_operation, 9)
      ELSE raw_operation
    END AS current_operation
    FROM current_operation
  )
  SELECT EXISTS (
    SELECT 1
    FROM normalized n
    CROSS JOIN LATERAL unnest(expected_operations) AS expected_operation
    WHERE expected_operation IS NOT NULL
      AND expected_operation <> ''
      AND n.current_operation = CASE
        WHEN expected_operation LIKE 'storage.%' THEN substr(expected_operation, 9)
        ELSE expected_operation
      END
  );
$$;


--
-- Name: allow_only_operation(text); Type: FUNCTION; Schema: storage; Owner: -
--

CREATE FUNCTION storage.allow_only_operation(expected_operation text) RETURNS boolean
    LANGUAGE sql STABLE
    AS $$
  WITH current_operation AS (
    SELECT storage.operation() AS raw_operation
  ),
  normalized AS (
    SELECT
      CASE
        WHEN raw_operation LIKE 'storage.%' THEN substr(raw_operation, 9)
        ELSE raw_operation
      END AS current_operation,
      CASE
        WHEN expected_operation LIKE 'storage.%' THEN substr(expected_operation, 9)
        ELSE expected_operation
      END AS requested_operation
    FROM current_operation
  )
  SELECT CASE
    WHEN requested_operation IS NULL OR requested_operation = '' THEN FALSE
    ELSE COALESCE(current_operation = requested_operation, FALSE)
  END
  FROM normalized;
$$;


--
-- Name: can_insert_object(text, text, uuid, jsonb); Type: FUNCTION; Schema: storage; Owner: -
--

CREATE FUNCTION storage.can_insert_object(bucketid text, name text, owner uuid, metadata jsonb) RETURNS void
    LANGUAGE plpgsql
    AS $$
BEGIN
  INSERT INTO "storage"."objects" ("bucket_id", "name", "owner", "metadata") VALUES (bucketid, name, owner, metadata);
  -- hack to rollback the successful insert
  RAISE sqlstate 'PT200' using
  message = 'ROLLBACK',
  detail = 'rollback successful insert';
END
$$;


--
-- Name: enforce_bucket_name_length(); Type: FUNCTION; Schema: storage; Owner: -
--

CREATE FUNCTION storage.enforce_bucket_name_length() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
begin
    if length(new.name) > 100 then
        raise exception 'bucket name "%" is too long (% characters). Max is 100.', new.name, length(new.name);
    end if;
    return new;
end;
$$;


--
-- Name: extension(text); Type: FUNCTION; Schema: storage; Owner: -
--

CREATE FUNCTION storage.extension(name text) RETURNS text
    LANGUAGE plpgsql IMMUTABLE
    AS $$
DECLARE
    _parts text[];
    _filename text;
BEGIN
    -- Split on "/" to get path segments
    SELECT string_to_array(name, '/') INTO _parts;
    -- Get the last path segment (the actual filename)
    SELECT _parts[array_length(_parts, 1)] INTO _filename;
    -- Extract extension: reverse, split on '.', then reverse again
    RETURN reverse(split_part(reverse(_filename), '.', 1));
END
$$;


--
-- Name: filename(text); Type: FUNCTION; Schema: storage; Owner: -
--

CREATE FUNCTION storage.filename(name text) RETURNS text
    LANGUAGE plpgsql
    AS $$
DECLARE
_parts text[];
BEGIN
	select string_to_array(name, '/') into _parts;
	return _parts[array_length(_parts,1)];
END
$$;


--
-- Name: foldername(text); Type: FUNCTION; Schema: storage; Owner: -
--

CREATE FUNCTION storage.foldername(name text) RETURNS text[]
    LANGUAGE plpgsql IMMUTABLE
    AS $$
DECLARE
    _parts text[];
BEGIN
    -- Split on "/" to get path segments
    SELECT string_to_array(name, '/') INTO _parts;
    -- Return everything except the last segment
    RETURN _parts[1 : array_length(_parts,1) - 1];
END
$$;


--
-- Name: get_common_prefix(text, text, text); Type: FUNCTION; Schema: storage; Owner: -
--

CREATE FUNCTION storage.get_common_prefix(p_key text, p_prefix text, p_delimiter text) RETURNS text
    LANGUAGE sql IMMUTABLE
    AS $$
SELECT CASE
    WHEN position(p_delimiter IN substring(p_key FROM length(p_prefix) + 1)) > 0
    THEN left(p_key, length(p_prefix) + position(p_delimiter IN substring(p_key FROM length(p_prefix) + 1)))
    ELSE NULL
END;
$$;


--
-- Name: get_size_by_bucket(); Type: FUNCTION; Schema: storage; Owner: -
--

CREATE FUNCTION storage.get_size_by_bucket() RETURNS TABLE(size bigint, bucket_id text)
    LANGUAGE plpgsql STABLE
    AS $$
BEGIN
    return query
        select sum((metadata->>'size')::bigint)::bigint as size, obj.bucket_id
        from "storage".objects as obj
        group by obj.bucket_id;
END
$$;


--
-- Name: list_multipart_uploads_with_delimiter(text, text, text, integer, text, text); Type: FUNCTION; Schema: storage; Owner: -
--

CREATE FUNCTION storage.list_multipart_uploads_with_delimiter(bucket_id text, prefix_param text, delimiter_param text, max_keys integer DEFAULT 100, next_key_token text DEFAULT ''::text, next_upload_token text DEFAULT ''::text) RETURNS TABLE(key text, id text, created_at timestamp with time zone)
    LANGUAGE plpgsql
    AS $_$
BEGIN
    RETURN QUERY EXECUTE
        'SELECT DISTINCT ON(key COLLATE "C") * from (
            SELECT
                CASE
                    WHEN position($2 IN substring(key from length($1) + 1)) > 0 THEN
                        substring(key from 1 for length($1) + position($2 IN substring(key from length($1) + 1)))
                    ELSE
                        key
                END AS key, id, created_at
            FROM
                storage.s3_multipart_uploads
            WHERE
                bucket_id = $5 AND
                key ILIKE $1 || ''%'' AND
                CASE
                    WHEN $4 != '''' AND $6 = '''' THEN
                        CASE
                            WHEN position($2 IN substring(key from length($1) + 1)) > 0 THEN
                                substring(key from 1 for length($1) + position($2 IN substring(key from length($1) + 1))) COLLATE "C" > $4
                            ELSE
                                key COLLATE "C" > $4
                            END
                    ELSE
                        true
                END AND
                CASE
                    WHEN $6 != '''' THEN
                        id COLLATE "C" > $6
                    ELSE
                        true
                    END
            ORDER BY
                key COLLATE "C" ASC, created_at ASC) as e order by key COLLATE "C" LIMIT $3'
        USING prefix_param, delimiter_param, max_keys, next_key_token, bucket_id, next_upload_token;
END;
$_$;


--
-- Name: list_objects_with_delimiter(text, text, text, integer, text, text, text); Type: FUNCTION; Schema: storage; Owner: -
--

CREATE FUNCTION storage.list_objects_with_delimiter(_bucket_id text, prefix_param text, delimiter_param text, max_keys integer DEFAULT 100, start_after text DEFAULT ''::text, next_token text DEFAULT ''::text, sort_order text DEFAULT 'asc'::text) RETURNS TABLE(name text, id uuid, metadata jsonb, updated_at timestamp with time zone, created_at timestamp with time zone, last_accessed_at timestamp with time zone)
    LANGUAGE plpgsql STABLE
    AS $_$
DECLARE
    v_peek_name TEXT;
    v_current RECORD;
    v_common_prefix TEXT;

    -- Configuration
    v_is_asc BOOLEAN;
    v_prefix TEXT;
    v_start TEXT;
    v_upper_bound TEXT;
    v_file_batch_size INT;

    -- Seek state
    v_next_seek TEXT;
    v_count INT := 0;

    -- Dynamic SQL for batch query only
    v_batch_query TEXT;

BEGIN
    -- ========================================================================
    -- INITIALIZATION
    -- ========================================================================
    v_is_asc := lower(coalesce(sort_order, 'asc')) = 'asc';
    v_prefix := coalesce(prefix_param, '');
    v_start := CASE WHEN coalesce(next_token, '') <> '' THEN next_token ELSE coalesce(start_after, '') END;
    v_file_batch_size := LEAST(GREATEST(max_keys * 2, 100), 1000);

    -- Calculate upper bound for prefix filtering (bytewise, using COLLATE "C")
    IF v_prefix = '' THEN
        v_upper_bound := NULL;
    ELSIF right(v_prefix, 1) = delimiter_param THEN
        v_upper_bound := left(v_prefix, -1) || chr(ascii(delimiter_param) + 1);
    ELSE
        v_upper_bound := left(v_prefix, -1) || chr(ascii(right(v_prefix, 1)) + 1);
    END IF;

    -- Build batch query (dynamic SQL - called infrequently, amortized over many rows)
    IF v_is_asc THEN
        IF v_upper_bound IS NOT NULL THEN
            v_batch_query := 'SELECT o.name, o.id, o.updated_at, o.created_at, o.last_accessed_at, o.metadata ' ||
                'FROM storage.objects o WHERE o.bucket_id = $1 AND o.name COLLATE "C" >= $2 ' ||
                'AND o.name COLLATE "C" < $3 ORDER BY o.name COLLATE "C" ASC LIMIT $4';
        ELSE
            v_batch_query := 'SELECT o.name, o.id, o.updated_at, o.created_at, o.last_accessed_at, o.metadata ' ||
                'FROM storage.objects o WHERE o.bucket_id = $1 AND o.name COLLATE "C" >= $2 ' ||
                'ORDER BY o.name COLLATE "C" ASC LIMIT $4';
        END IF;
    ELSE
        IF v_upper_bound IS NOT NULL THEN
            v_batch_query := 'SELECT o.name, o.id, o.updated_at, o.created_at, o.last_accessed_at, o.metadata ' ||
                'FROM storage.objects o WHERE o.bucket_id = $1 AND o.name COLLATE "C" < $2 ' ||
                'AND o.name COLLATE "C" >= $3 ORDER BY o.name COLLATE "C" DESC LIMIT $4';
        ELSE
            v_batch_query := 'SELECT o.name, o.id, o.updated_at, o.created_at, o.last_accessed_at, o.metadata ' ||
                'FROM storage.objects o WHERE o.bucket_id = $1 AND o.name COLLATE "C" < $2 ' ||
                'ORDER BY o.name COLLATE "C" DESC LIMIT $4';
        END IF;
    END IF;

    -- ========================================================================
    -- SEEK INITIALIZATION: Determine starting position
    -- ========================================================================
    IF v_start = '' THEN
        IF v_is_asc THEN
            v_next_seek := v_prefix;
        ELSE
            -- DESC without cursor: find the last item in range
            IF v_upper_bound IS NOT NULL THEN
                SELECT o.name INTO v_next_seek FROM storage.objects o
                WHERE o.bucket_id = _bucket_id AND o.name COLLATE "C" >= v_prefix AND o.name COLLATE "C" < v_upper_bound
                ORDER BY o.name COLLATE "C" DESC LIMIT 1;
            ELSIF v_prefix <> '' THEN
                SELECT o.name INTO v_next_seek FROM storage.objects o
                WHERE o.bucket_id = _bucket_id AND o.name COLLATE "C" >= v_prefix
                ORDER BY o.name COLLATE "C" DESC LIMIT 1;
            ELSE
                SELECT o.name INTO v_next_seek FROM storage.objects o
                WHERE o.bucket_id = _bucket_id
                ORDER BY o.name COLLATE "C" DESC LIMIT 1;
            END IF;

            IF v_next_seek IS NOT NULL THEN
                v_next_seek := v_next_seek || delimiter_param;
            ELSE
                RETURN;
            END IF;
        END IF;
    ELSE
        -- Cursor provided: determine if it refers to a folder or leaf
        IF EXISTS (
            SELECT 1 FROM storage.objects o
            WHERE o.bucket_id = _bucket_id
              AND o.name COLLATE "C" LIKE v_start || delimiter_param || '%'
            LIMIT 1
        ) THEN
            -- Cursor refers to a folder
            IF v_is_asc THEN
                v_next_seek := v_start || chr(ascii(delimiter_param) + 1);
            ELSE
                v_next_seek := v_start || delimiter_param;
            END IF;
        ELSE
            -- Cursor refers to a leaf object
            IF v_is_asc THEN
                v_next_seek := v_start || delimiter_param;
            ELSE
                v_next_seek := v_start;
            END IF;
        END IF;
    END IF;

    -- ========================================================================
    -- MAIN LOOP: Hybrid peek-then-batch algorithm
    -- Uses STATIC SQL for peek (hot path) and DYNAMIC SQL for batch
    -- ========================================================================
    LOOP
        EXIT WHEN v_count >= max_keys;

        -- STEP 1: PEEK using STATIC SQL (plan cached, very fast)
        IF v_is_asc THEN
            IF v_upper_bound IS NOT NULL THEN
                SELECT o.name INTO v_peek_name FROM storage.objects o
                WHERE o.bucket_id = _bucket_id AND o.name COLLATE "C" >= v_next_seek AND o.name COLLATE "C" < v_upper_bound
                ORDER BY o.name COLLATE "C" ASC LIMIT 1;
            ELSE
                SELECT o.name INTO v_peek_name FROM storage.objects o
                WHERE o.bucket_id = _bucket_id AND o.name COLLATE "C" >= v_next_seek
                ORDER BY o.name COLLATE "C" ASC LIMIT 1;
            END IF;
        ELSE
            IF v_upper_bound IS NOT NULL THEN
                SELECT o.name INTO v_peek_name FROM storage.objects o
                WHERE o.bucket_id = _bucket_id AND o.name COLLATE "C" < v_next_seek AND o.name COLLATE "C" >= v_prefix
                ORDER BY o.name COLLATE "C" DESC LIMIT 1;
            ELSIF v_prefix <> '' THEN
                SELECT o.name INTO v_peek_name FROM storage.objects o
                WHERE o.bucket_id = _bucket_id AND o.name COLLATE "C" < v_next_seek AND o.name COLLATE "C" >= v_prefix
                ORDER BY o.name COLLATE "C" DESC LIMIT 1;
            ELSE
                SELECT o.name INTO v_peek_name FROM storage.objects o
                WHERE o.bucket_id = _bucket_id AND o.name COLLATE "C" < v_next_seek
                ORDER BY o.name COLLATE "C" DESC LIMIT 1;
            END IF;
        END IF;

        EXIT WHEN v_peek_name IS NULL;

        -- STEP 2: Check if this is a FOLDER or FILE
        v_common_prefix := storage.get_common_prefix(v_peek_name, v_prefix, delimiter_param);

        IF v_common_prefix IS NOT NULL THEN
            -- FOLDER: Emit and skip to next folder (no heap access needed)
            name := rtrim(v_common_prefix, delimiter_param);
            id := NULL;
            updated_at := NULL;
            created_at := NULL;
            last_accessed_at := NULL;
            metadata := NULL;
            RETURN NEXT;
            v_count := v_count + 1;

            -- Advance seek past the folder range
            IF v_is_asc THEN
                v_next_seek := left(v_common_prefix, -1) || chr(ascii(delimiter_param) + 1);
            ELSE
                v_next_seek := v_common_prefix;
            END IF;
        ELSE
            -- FILE: Batch fetch using DYNAMIC SQL (overhead amortized over many rows)
            -- For ASC: upper_bound is the exclusive upper limit (< condition)
            -- For DESC: prefix is the inclusive lower limit (>= condition)
            FOR v_current IN EXECUTE v_batch_query USING _bucket_id, v_next_seek,
                CASE WHEN v_is_asc THEN COALESCE(v_upper_bound, v_prefix) ELSE v_prefix END, v_file_batch_size
            LOOP
                v_common_prefix := storage.get_common_prefix(v_current.name, v_prefix, delimiter_param);

                IF v_common_prefix IS NOT NULL THEN
                    -- Hit a folder: exit batch, let peek handle it
                    v_next_seek := v_current.name;
                    EXIT;
                END IF;

                -- Emit file
                name := v_current.name;
                id := v_current.id;
                updated_at := v_current.updated_at;
                created_at := v_current.created_at;
                last_accessed_at := v_current.last_accessed_at;
                metadata := v_current.metadata;
                RETURN NEXT;
                v_count := v_count + 1;

                -- Advance seek past this file
                IF v_is_asc THEN
                    v_next_seek := v_current.name || delimiter_param;
                ELSE
                    v_next_seek := v_current.name;
                END IF;

                EXIT WHEN v_count >= max_keys;
            END LOOP;
        END IF;
    END LOOP;
END;
$_$;


--
-- Name: operation(); Type: FUNCTION; Schema: storage; Owner: -
--

CREATE FUNCTION storage.operation() RETURNS text
    LANGUAGE plpgsql STABLE
    AS $$
BEGIN
    RETURN current_setting('storage.operation', true);
END;
$$;


--
-- Name: protect_delete(); Type: FUNCTION; Schema: storage; Owner: -
--

CREATE FUNCTION storage.protect_delete() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
BEGIN
    -- Check if storage.allow_delete_query is set to 'true'
    IF COALESCE(current_setting('storage.allow_delete_query', true), 'false') != 'true' THEN
        RAISE EXCEPTION 'Direct deletion from storage tables is not allowed. Use the Storage API instead.'
            USING HINT = 'This prevents accidental data loss from orphaned objects.',
                  ERRCODE = '42501';
    END IF;
    RETURN NULL;
END;
$$;


--
-- Name: search(text, text, integer, integer, integer, text, text, text); Type: FUNCTION; Schema: storage; Owner: -
--

CREATE FUNCTION storage.search(prefix text, bucketname text, limits integer DEFAULT 100, levels integer DEFAULT 1, offsets integer DEFAULT 0, search text DEFAULT ''::text, sortcolumn text DEFAULT 'name'::text, sortorder text DEFAULT 'asc'::text) RETURNS TABLE(name text, id uuid, updated_at timestamp with time zone, created_at timestamp with time zone, last_accessed_at timestamp with time zone, metadata jsonb)
    LANGUAGE plpgsql STABLE
    AS $_$
DECLARE
    v_peek_name TEXT;
    v_current RECORD;
    v_common_prefix TEXT;
    v_delimiter CONSTANT TEXT := '/';

    -- Configuration
    v_limit INT;
    v_prefix TEXT;
    v_prefix_lower TEXT;
    v_is_asc BOOLEAN;
    v_order_by TEXT;
    v_sort_order TEXT;
    v_upper_bound TEXT;
    v_file_batch_size INT;

    -- Dynamic SQL for batch query only
    v_batch_query TEXT;

    -- Seek state
    v_next_seek TEXT;
    v_count INT := 0;
    v_skipped INT := 0;
BEGIN
    -- ========================================================================
    -- INITIALIZATION
    -- ========================================================================
    v_limit := LEAST(coalesce(limits, 100), 1500);
    v_prefix := coalesce(prefix, '') || coalesce(search, '');
    v_prefix_lower := lower(v_prefix);
    v_is_asc := lower(coalesce(sortorder, 'asc')) = 'asc';
    v_file_batch_size := LEAST(GREATEST(v_limit * 2, 100), 1000);

    -- Validate sort column
    CASE lower(coalesce(sortcolumn, 'name'))
        WHEN 'name' THEN v_order_by := 'name';
        WHEN 'updated_at' THEN v_order_by := 'updated_at';
        WHEN 'created_at' THEN v_order_by := 'created_at';
        WHEN 'last_accessed_at' THEN v_order_by := 'last_accessed_at';
        ELSE v_order_by := 'name';
    END CASE;

    v_sort_order := CASE WHEN v_is_asc THEN 'asc' ELSE 'desc' END;

    -- ========================================================================
    -- NON-NAME SORTING: Use path_tokens approach (unchanged)
    -- ========================================================================
    IF v_order_by != 'name' THEN
        RETURN QUERY EXECUTE format(
            $sql$
            WITH folders AS (
                SELECT path_tokens[$1] AS folder
                FROM storage.objects
                WHERE objects.name ILIKE $2 || '%%'
                  AND bucket_id = $3
                  AND array_length(objects.path_tokens, 1) <> $1
                GROUP BY folder
                ORDER BY folder %s
            )
            (SELECT folder AS "name",
                   NULL::uuid AS id,
                   NULL::timestamptz AS updated_at,
                   NULL::timestamptz AS created_at,
                   NULL::timestamptz AS last_accessed_at,
                   NULL::jsonb AS metadata FROM folders)
            UNION ALL
            (SELECT path_tokens[$1] AS "name",
                   id, updated_at, created_at, last_accessed_at, metadata
             FROM storage.objects
             WHERE objects.name ILIKE $2 || '%%'
               AND bucket_id = $3
               AND array_length(objects.path_tokens, 1) = $1
             ORDER BY %I %s)
            LIMIT $4 OFFSET $5
            $sql$, v_sort_order, v_order_by, v_sort_order
        ) USING levels, v_prefix, bucketname, v_limit, offsets;
        RETURN;
    END IF;

    -- ========================================================================
    -- NAME SORTING: Hybrid skip-scan with batch optimization
    -- ========================================================================

    -- Calculate upper bound for prefix filtering
    IF v_prefix_lower = '' THEN
        v_upper_bound := NULL;
    ELSIF right(v_prefix_lower, 1) = v_delimiter THEN
        v_upper_bound := left(v_prefix_lower, -1) || chr(ascii(v_delimiter) + 1);
    ELSE
        v_upper_bound := left(v_prefix_lower, -1) || chr(ascii(right(v_prefix_lower, 1)) + 1);
    END IF;

    -- Build batch query (dynamic SQL - called infrequently, amortized over many rows)
    IF v_is_asc THEN
        IF v_upper_bound IS NOT NULL THEN
            v_batch_query := 'SELECT o.name, o.id, o.updated_at, o.created_at, o.last_accessed_at, o.metadata ' ||
                'FROM storage.objects o WHERE o.bucket_id = $1 AND lower(o.name) COLLATE "C" >= $2 ' ||
                'AND lower(o.name) COLLATE "C" < $3 ORDER BY lower(o.name) COLLATE "C" ASC LIMIT $4';
        ELSE
            v_batch_query := 'SELECT o.name, o.id, o.updated_at, o.created_at, o.last_accessed_at, o.metadata ' ||
                'FROM storage.objects o WHERE o.bucket_id = $1 AND lower(o.name) COLLATE "C" >= $2 ' ||
                'ORDER BY lower(o.name) COLLATE "C" ASC LIMIT $4';
        END IF;
    ELSE
        IF v_upper_bound IS NOT NULL THEN
            v_batch_query := 'SELECT o.name, o.id, o.updated_at, o.created_at, o.last_accessed_at, o.metadata ' ||
                'FROM storage.objects o WHERE o.bucket_id = $1 AND lower(o.name) COLLATE "C" < $2 ' ||
                'AND lower(o.name) COLLATE "C" >= $3 ORDER BY lower(o.name) COLLATE "C" DESC LIMIT $4';
        ELSE
            v_batch_query := 'SELECT o.name, o.id, o.updated_at, o.created_at, o.last_accessed_at, o.metadata ' ||
                'FROM storage.objects o WHERE o.bucket_id = $1 AND lower(o.name) COLLATE "C" < $2 ' ||
                'ORDER BY lower(o.name) COLLATE "C" DESC LIMIT $4';
        END IF;
    END IF;

    -- Initialize seek position
    IF v_is_asc THEN
        v_next_seek := v_prefix_lower;
    ELSE
        -- DESC: find the last item in range first (static SQL)
        IF v_upper_bound IS NOT NULL THEN
            SELECT o.name INTO v_peek_name FROM storage.objects o
            WHERE o.bucket_id = bucketname AND lower(o.name) COLLATE "C" >= v_prefix_lower AND lower(o.name) COLLATE "C" < v_upper_bound
            ORDER BY lower(o.name) COLLATE "C" DESC LIMIT 1;
        ELSIF v_prefix_lower <> '' THEN
            SELECT o.name INTO v_peek_name FROM storage.objects o
            WHERE o.bucket_id = bucketname AND lower(o.name) COLLATE "C" >= v_prefix_lower
            ORDER BY lower(o.name) COLLATE "C" DESC LIMIT 1;
        ELSE
            SELECT o.name INTO v_peek_name FROM storage.objects o
            WHERE o.bucket_id = bucketname
            ORDER BY lower(o.name) COLLATE "C" DESC LIMIT 1;
        END IF;

        IF v_peek_name IS NOT NULL THEN
            v_next_seek := lower(v_peek_name) || v_delimiter;
        ELSE
            RETURN;
        END IF;
    END IF;

    -- ========================================================================
    -- MAIN LOOP: Hybrid peek-then-batch algorithm
    -- Uses STATIC SQL for peek (hot path) and DYNAMIC SQL for batch
    -- ========================================================================
    LOOP
        EXIT WHEN v_count >= v_limit;

        -- STEP 1: PEEK using STATIC SQL (plan cached, very fast)
        IF v_is_asc THEN
            IF v_upper_bound IS NOT NULL THEN
                SELECT o.name INTO v_peek_name FROM storage.objects o
                WHERE o.bucket_id = bucketname AND lower(o.name) COLLATE "C" >= v_next_seek AND lower(o.name) COLLATE "C" < v_upper_bound
                ORDER BY lower(o.name) COLLATE "C" ASC LIMIT 1;
            ELSE
                SELECT o.name INTO v_peek_name FROM storage.objects o
                WHERE o.bucket_id = bucketname AND lower(o.name) COLLATE "C" >= v_next_seek
                ORDER BY lower(o.name) COLLATE "C" ASC LIMIT 1;
            END IF;
        ELSE
            IF v_upper_bound IS NOT NULL THEN
                SELECT o.name INTO v_peek_name FROM storage.objects o
                WHERE o.bucket_id = bucketname AND lower(o.name) COLLATE "C" < v_next_seek AND lower(o.name) COLLATE "C" >= v_prefix_lower
                ORDER BY lower(o.name) COLLATE "C" DESC LIMIT 1;
            ELSIF v_prefix_lower <> '' THEN
                SELECT o.name INTO v_peek_name FROM storage.objects o
                WHERE o.bucket_id = bucketname AND lower(o.name) COLLATE "C" < v_next_seek AND lower(o.name) COLLATE "C" >= v_prefix_lower
                ORDER BY lower(o.name) COLLATE "C" DESC LIMIT 1;
            ELSE
                SELECT o.name INTO v_peek_name FROM storage.objects o
                WHERE o.bucket_id = bucketname AND lower(o.name) COLLATE "C" < v_next_seek
                ORDER BY lower(o.name) COLLATE "C" DESC LIMIT 1;
            END IF;
        END IF;

        EXIT WHEN v_peek_name IS NULL;

        -- STEP 2: Check if this is a FOLDER or FILE
        v_common_prefix := storage.get_common_prefix(lower(v_peek_name), v_prefix_lower, v_delimiter);

        IF v_common_prefix IS NOT NULL THEN
            -- FOLDER: Handle offset, emit if needed, skip to next folder
            IF v_skipped < offsets THEN
                v_skipped := v_skipped + 1;
            ELSE
                name := split_part(rtrim(storage.get_common_prefix(v_peek_name, v_prefix, v_delimiter), v_delimiter), v_delimiter, levels);
                id := NULL;
                updated_at := NULL;
                created_at := NULL;
                last_accessed_at := NULL;
                metadata := NULL;
                RETURN NEXT;
                v_count := v_count + 1;
            END IF;

            -- Advance seek past the folder range
            IF v_is_asc THEN
                v_next_seek := lower(left(v_common_prefix, -1)) || chr(ascii(v_delimiter) + 1);
            ELSE
                v_next_seek := lower(v_common_prefix);
            END IF;
        ELSE
            -- FILE: Batch fetch using DYNAMIC SQL (overhead amortized over many rows)
            -- For ASC: upper_bound is the exclusive upper limit (< condition)
            -- For DESC: prefix_lower is the inclusive lower limit (>= condition)
            FOR v_current IN EXECUTE v_batch_query
                USING bucketname, v_next_seek,
                    CASE WHEN v_is_asc THEN COALESCE(v_upper_bound, v_prefix_lower) ELSE v_prefix_lower END, v_file_batch_size
            LOOP
                v_common_prefix := storage.get_common_prefix(lower(v_current.name), v_prefix_lower, v_delimiter);

                IF v_common_prefix IS NOT NULL THEN
                    -- Hit a folder: exit batch, let peek handle it
                    v_next_seek := lower(v_current.name);
                    EXIT;
                END IF;

                -- Handle offset skipping
                IF v_skipped < offsets THEN
                    v_skipped := v_skipped + 1;
                ELSE
                    -- Emit file
                    name := split_part(v_current.name, v_delimiter, levels);
                    id := v_current.id;
                    updated_at := v_current.updated_at;
                    created_at := v_current.created_at;
                    last_accessed_at := v_current.last_accessed_at;
                    metadata := v_current.metadata;
                    RETURN NEXT;
                    v_count := v_count + 1;
                END IF;

                -- Advance seek past this file
                IF v_is_asc THEN
                    v_next_seek := lower(v_current.name) || v_delimiter;
                ELSE
                    v_next_seek := lower(v_current.name);
                END IF;

                EXIT WHEN v_count >= v_limit;
            END LOOP;
        END IF;
    END LOOP;
END;
$_$;


--
-- Name: search_by_timestamp(text, text, integer, integer, text, text, text, text); Type: FUNCTION; Schema: storage; Owner: -
--

CREATE FUNCTION storage.search_by_timestamp(p_prefix text, p_bucket_id text, p_limit integer, p_level integer, p_start_after text, p_sort_order text, p_sort_column text, p_sort_column_after text) RETURNS TABLE(key text, name text, id uuid, updated_at timestamp with time zone, created_at timestamp with time zone, last_accessed_at timestamp with time zone, metadata jsonb)
    LANGUAGE plpgsql STABLE
    AS $_$
DECLARE
    v_cursor_op text;
    v_query text;
    v_prefix text;
BEGIN
    v_prefix := coalesce(p_prefix, '');

    IF p_sort_order = 'asc' THEN
        v_cursor_op := '>';
    ELSE
        v_cursor_op := '<';
    END IF;

    v_query := format($sql$
        WITH raw_objects AS (
            SELECT
                o.name AS obj_name,
                o.id AS obj_id,
                o.updated_at AS obj_updated_at,
                o.created_at AS obj_created_at,
                o.last_accessed_at AS obj_last_accessed_at,
                o.metadata AS obj_metadata,
                storage.get_common_prefix(o.name, $1, '/') AS common_prefix
            FROM storage.objects o
            WHERE o.bucket_id = $2
              AND o.name COLLATE "C" LIKE $1 || '%%'
        ),
        -- Aggregate common prefixes (folders)
        -- Both created_at and updated_at use MIN(obj_created_at) to match the old prefixes table behavior
        aggregated_prefixes AS (
            SELECT
                rtrim(common_prefix, '/') AS name,
                NULL::uuid AS id,
                MIN(obj_created_at) AS updated_at,
                MIN(obj_created_at) AS created_at,
                NULL::timestamptz AS last_accessed_at,
                NULL::jsonb AS metadata,
                TRUE AS is_prefix
            FROM raw_objects
            WHERE common_prefix IS NOT NULL
            GROUP BY common_prefix
        ),
        leaf_objects AS (
            SELECT
                obj_name AS name,
                obj_id AS id,
                obj_updated_at AS updated_at,
                obj_created_at AS created_at,
                obj_last_accessed_at AS last_accessed_at,
                obj_metadata AS metadata,
                FALSE AS is_prefix
            FROM raw_objects
            WHERE common_prefix IS NULL
        ),
        combined AS (
            SELECT * FROM aggregated_prefixes
            UNION ALL
            SELECT * FROM leaf_objects
        ),
        filtered AS (
            SELECT *
            FROM combined
            WHERE (
                $5 = ''
                OR ROW(
                    date_trunc('milliseconds', %I),
                    name COLLATE "C"
                ) %s ROW(
                    COALESCE(NULLIF($6, '')::timestamptz, 'epoch'::timestamptz),
                    $5
                )
            )
        )
        SELECT
            split_part(name, '/', $3) AS key,
            name,
            id,
            updated_at,
            created_at,
            last_accessed_at,
            metadata
        FROM filtered
        ORDER BY
            COALESCE(date_trunc('milliseconds', %I), 'epoch'::timestamptz) %s,
            name COLLATE "C" %s
        LIMIT $4
    $sql$,
        p_sort_column,
        v_cursor_op,
        p_sort_column,
        p_sort_order,
        p_sort_order
    );

    RETURN QUERY EXECUTE v_query
    USING v_prefix, p_bucket_id, p_level, p_limit, p_start_after, p_sort_column_after;
END;
$_$;


--
-- Name: search_v2(text, text, integer, integer, text, text, text, text); Type: FUNCTION; Schema: storage; Owner: -
--

CREATE FUNCTION storage.search_v2(prefix text, bucket_name text, limits integer DEFAULT 100, levels integer DEFAULT 1, start_after text DEFAULT ''::text, sort_order text DEFAULT 'asc'::text, sort_column text DEFAULT 'name'::text, sort_column_after text DEFAULT ''::text) RETURNS TABLE(key text, name text, id uuid, updated_at timestamp with time zone, created_at timestamp with time zone, last_accessed_at timestamp with time zone, metadata jsonb)
    LANGUAGE plpgsql STABLE
    AS $$
DECLARE
    v_sort_col text;
    v_sort_ord text;
    v_limit int;
BEGIN
    -- Cap limit to maximum of 1500 records
    v_limit := LEAST(coalesce(limits, 100), 1500);

    -- Validate and normalize sort_order
    v_sort_ord := lower(coalesce(sort_order, 'asc'));
    IF v_sort_ord NOT IN ('asc', 'desc') THEN
        v_sort_ord := 'asc';
    END IF;

    -- Validate and normalize sort_column
    v_sort_col := lower(coalesce(sort_column, 'name'));
    IF v_sort_col NOT IN ('name', 'updated_at', 'created_at') THEN
        v_sort_col := 'name';
    END IF;

    -- Route to appropriate implementation
    IF v_sort_col = 'name' THEN
        -- Use list_objects_with_delimiter for name sorting (most efficient: O(k * log n))
        RETURN QUERY
        SELECT
            split_part(l.name, '/', levels) AS key,
            l.name AS name,
            l.id,
            l.updated_at,
            l.created_at,
            l.last_accessed_at,
            l.metadata
        FROM storage.list_objects_with_delimiter(
            bucket_name,
            coalesce(prefix, ''),
            '/',
            v_limit,
            start_after,
            '',
            v_sort_ord
        ) l;
    ELSE
        -- Use aggregation approach for timestamp sorting
        -- Not efficient for large datasets but supports correct pagination
        RETURN QUERY SELECT * FROM storage.search_by_timestamp(
            prefix, bucket_name, v_limit, levels, start_after,
            v_sort_ord, v_sort_col, sort_column_after
        );
    END IF;
END;
$$;


--
-- Name: update_updated_at_column(); Type: FUNCTION; Schema: storage; Owner: -
--

CREATE FUNCTION storage.update_updated_at_column() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
BEGIN
    NEW.updated_at = now();
    RETURN NEW; 
END;
$$;


SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: audit_log_entries; Type: TABLE; Schema: auth; Owner: -
--

CREATE TABLE auth.audit_log_entries (
    instance_id uuid,
    id uuid NOT NULL,
    payload json,
    created_at timestamp with time zone,
    ip_address character varying(64) DEFAULT ''::character varying NOT NULL
);


--
-- Name: TABLE audit_log_entries; Type: COMMENT; Schema: auth; Owner: -
--

COMMENT ON TABLE auth.audit_log_entries IS 'Auth: Audit trail for user actions.';


--
-- Name: custom_oauth_providers; Type: TABLE; Schema: auth; Owner: -
--

CREATE TABLE auth.custom_oauth_providers (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    provider_type text NOT NULL,
    identifier text NOT NULL,
    name text NOT NULL,
    client_id text NOT NULL,
    client_secret text NOT NULL,
    acceptable_client_ids text[] DEFAULT '{}'::text[] NOT NULL,
    scopes text[] DEFAULT '{}'::text[] NOT NULL,
    pkce_enabled boolean DEFAULT true NOT NULL,
    attribute_mapping jsonb DEFAULT '{}'::jsonb NOT NULL,
    authorization_params jsonb DEFAULT '{}'::jsonb NOT NULL,
    enabled boolean DEFAULT true NOT NULL,
    email_optional boolean DEFAULT false NOT NULL,
    issuer text,
    discovery_url text,
    skip_nonce_check boolean DEFAULT false NOT NULL,
    cached_discovery jsonb,
    discovery_cached_at timestamp with time zone,
    authorization_url text,
    token_url text,
    userinfo_url text,
    jwks_uri text,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL,
    CONSTRAINT custom_oauth_providers_authorization_url_https CHECK (((authorization_url IS NULL) OR (authorization_url ~~ 'https://%'::text))),
    CONSTRAINT custom_oauth_providers_authorization_url_length CHECK (((authorization_url IS NULL) OR (char_length(authorization_url) <= 2048))),
    CONSTRAINT custom_oauth_providers_client_id_length CHECK (((char_length(client_id) >= 1) AND (char_length(client_id) <= 512))),
    CONSTRAINT custom_oauth_providers_discovery_url_length CHECK (((discovery_url IS NULL) OR (char_length(discovery_url) <= 2048))),
    CONSTRAINT custom_oauth_providers_identifier_format CHECK ((identifier ~ '^[a-z0-9][a-z0-9:-]{0,48}[a-z0-9]$'::text)),
    CONSTRAINT custom_oauth_providers_issuer_length CHECK (((issuer IS NULL) OR ((char_length(issuer) >= 1) AND (char_length(issuer) <= 2048)))),
    CONSTRAINT custom_oauth_providers_jwks_uri_https CHECK (((jwks_uri IS NULL) OR (jwks_uri ~~ 'https://%'::text))),
    CONSTRAINT custom_oauth_providers_jwks_uri_length CHECK (((jwks_uri IS NULL) OR (char_length(jwks_uri) <= 2048))),
    CONSTRAINT custom_oauth_providers_name_length CHECK (((char_length(name) >= 1) AND (char_length(name) <= 100))),
    CONSTRAINT custom_oauth_providers_oauth2_requires_endpoints CHECK (((provider_type <> 'oauth2'::text) OR ((authorization_url IS NOT NULL) AND (token_url IS NOT NULL) AND (userinfo_url IS NOT NULL)))),
    CONSTRAINT custom_oauth_providers_oidc_discovery_url_https CHECK (((provider_type <> 'oidc'::text) OR (discovery_url IS NULL) OR (discovery_url ~~ 'https://%'::text))),
    CONSTRAINT custom_oauth_providers_oidc_issuer_https CHECK (((provider_type <> 'oidc'::text) OR (issuer IS NULL) OR (issuer ~~ 'https://%'::text))),
    CONSTRAINT custom_oauth_providers_oidc_requires_issuer CHECK (((provider_type <> 'oidc'::text) OR (issuer IS NOT NULL))),
    CONSTRAINT custom_oauth_providers_provider_type_check CHECK ((provider_type = ANY (ARRAY['oauth2'::text, 'oidc'::text]))),
    CONSTRAINT custom_oauth_providers_token_url_https CHECK (((token_url IS NULL) OR (token_url ~~ 'https://%'::text))),
    CONSTRAINT custom_oauth_providers_token_url_length CHECK (((token_url IS NULL) OR (char_length(token_url) <= 2048))),
    CONSTRAINT custom_oauth_providers_userinfo_url_https CHECK (((userinfo_url IS NULL) OR (userinfo_url ~~ 'https://%'::text))),
    CONSTRAINT custom_oauth_providers_userinfo_url_length CHECK (((userinfo_url IS NULL) OR (char_length(userinfo_url) <= 2048)))
);


--
-- Name: flow_state; Type: TABLE; Schema: auth; Owner: -
--

CREATE TABLE auth.flow_state (
    id uuid NOT NULL,
    user_id uuid,
    auth_code text,
    code_challenge_method auth.code_challenge_method,
    code_challenge text,
    provider_type text NOT NULL,
    provider_access_token text,
    provider_refresh_token text,
    created_at timestamp with time zone,
    updated_at timestamp with time zone,
    authentication_method text NOT NULL,
    auth_code_issued_at timestamp with time zone,
    invite_token text,
    referrer text,
    oauth_client_state_id uuid,
    linking_target_id uuid,
    email_optional boolean DEFAULT false NOT NULL
);


--
-- Name: TABLE flow_state; Type: COMMENT; Schema: auth; Owner: -
--

COMMENT ON TABLE auth.flow_state IS 'Stores metadata for all OAuth/SSO login flows';


--
-- Name: identities; Type: TABLE; Schema: auth; Owner: -
--

CREATE TABLE auth.identities (
    provider_id text NOT NULL,
    user_id uuid NOT NULL,
    identity_data jsonb NOT NULL,
    provider text NOT NULL,
    last_sign_in_at timestamp with time zone,
    created_at timestamp with time zone,
    updated_at timestamp with time zone,
    email text GENERATED ALWAYS AS (lower((identity_data ->> 'email'::text))) STORED,
    id uuid DEFAULT gen_random_uuid() NOT NULL
);


--
-- Name: TABLE identities; Type: COMMENT; Schema: auth; Owner: -
--

COMMENT ON TABLE auth.identities IS 'Auth: Stores identities associated to a user.';


--
-- Name: COLUMN identities.email; Type: COMMENT; Schema: auth; Owner: -
--

COMMENT ON COLUMN auth.identities.email IS 'Auth: Email is a generated column that references the optional email property in the identity_data';


--
-- Name: instances; Type: TABLE; Schema: auth; Owner: -
--

CREATE TABLE auth.instances (
    id uuid NOT NULL,
    uuid uuid,
    raw_base_config text,
    created_at timestamp with time zone,
    updated_at timestamp with time zone
);


--
-- Name: TABLE instances; Type: COMMENT; Schema: auth; Owner: -
--

COMMENT ON TABLE auth.instances IS 'Auth: Manages users across multiple sites.';


--
-- Name: mfa_amr_claims; Type: TABLE; Schema: auth; Owner: -
--

CREATE TABLE auth.mfa_amr_claims (
    session_id uuid NOT NULL,
    created_at timestamp with time zone NOT NULL,
    updated_at timestamp with time zone NOT NULL,
    authentication_method text NOT NULL,
    id uuid NOT NULL
);


--
-- Name: TABLE mfa_amr_claims; Type: COMMENT; Schema: auth; Owner: -
--

COMMENT ON TABLE auth.mfa_amr_claims IS 'auth: stores authenticator method reference claims for multi factor authentication';


--
-- Name: mfa_challenges; Type: TABLE; Schema: auth; Owner: -
--

CREATE TABLE auth.mfa_challenges (
    id uuid NOT NULL,
    factor_id uuid NOT NULL,
    created_at timestamp with time zone NOT NULL,
    verified_at timestamp with time zone,
    ip_address inet NOT NULL,
    otp_code text,
    web_authn_session_data jsonb
);


--
-- Name: TABLE mfa_challenges; Type: COMMENT; Schema: auth; Owner: -
--

COMMENT ON TABLE auth.mfa_challenges IS 'auth: stores metadata about challenge requests made';


--
-- Name: mfa_factors; Type: TABLE; Schema: auth; Owner: -
--

CREATE TABLE auth.mfa_factors (
    id uuid NOT NULL,
    user_id uuid NOT NULL,
    friendly_name text,
    factor_type auth.factor_type NOT NULL,
    status auth.factor_status NOT NULL,
    created_at timestamp with time zone NOT NULL,
    updated_at timestamp with time zone NOT NULL,
    secret text,
    phone text,
    last_challenged_at timestamp with time zone,
    web_authn_credential jsonb,
    web_authn_aaguid uuid,
    last_webauthn_challenge_data jsonb
);


--
-- Name: TABLE mfa_factors; Type: COMMENT; Schema: auth; Owner: -
--

COMMENT ON TABLE auth.mfa_factors IS 'auth: stores metadata about factors';


--
-- Name: COLUMN mfa_factors.last_webauthn_challenge_data; Type: COMMENT; Schema: auth; Owner: -
--

COMMENT ON COLUMN auth.mfa_factors.last_webauthn_challenge_data IS 'Stores the latest WebAuthn challenge data including attestation/assertion for customer verification';


--
-- Name: oauth_authorizations; Type: TABLE; Schema: auth; Owner: -
--

CREATE TABLE auth.oauth_authorizations (
    id uuid NOT NULL,
    authorization_id text NOT NULL,
    client_id uuid NOT NULL,
    user_id uuid,
    redirect_uri text NOT NULL,
    scope text NOT NULL,
    state text,
    resource text,
    code_challenge text,
    code_challenge_method auth.code_challenge_method,
    response_type auth.oauth_response_type DEFAULT 'code'::auth.oauth_response_type NOT NULL,
    status auth.oauth_authorization_status DEFAULT 'pending'::auth.oauth_authorization_status NOT NULL,
    authorization_code text,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    expires_at timestamp with time zone DEFAULT (now() + '00:03:00'::interval) NOT NULL,
    approved_at timestamp with time zone,
    nonce text,
    CONSTRAINT oauth_authorizations_authorization_code_length CHECK ((char_length(authorization_code) <= 255)),
    CONSTRAINT oauth_authorizations_code_challenge_length CHECK ((char_length(code_challenge) <= 128)),
    CONSTRAINT oauth_authorizations_expires_at_future CHECK ((expires_at > created_at)),
    CONSTRAINT oauth_authorizations_nonce_length CHECK ((char_length(nonce) <= 255)),
    CONSTRAINT oauth_authorizations_redirect_uri_length CHECK ((char_length(redirect_uri) <= 2048)),
    CONSTRAINT oauth_authorizations_resource_length CHECK ((char_length(resource) <= 2048)),
    CONSTRAINT oauth_authorizations_scope_length CHECK ((char_length(scope) <= 4096)),
    CONSTRAINT oauth_authorizations_state_length CHECK ((char_length(state) <= 4096))
);


--
-- Name: oauth_client_states; Type: TABLE; Schema: auth; Owner: -
--

CREATE TABLE auth.oauth_client_states (
    id uuid NOT NULL,
    provider_type text NOT NULL,
    code_verifier text,
    created_at timestamp with time zone NOT NULL
);


--
-- Name: TABLE oauth_client_states; Type: COMMENT; Schema: auth; Owner: -
--

COMMENT ON TABLE auth.oauth_client_states IS 'Stores OAuth states for third-party provider authentication flows where Supabase acts as the OAuth client.';


--
-- Name: oauth_clients; Type: TABLE; Schema: auth; Owner: -
--

CREATE TABLE auth.oauth_clients (
    id uuid NOT NULL,
    client_secret_hash text,
    registration_type auth.oauth_registration_type NOT NULL,
    redirect_uris text NOT NULL,
    grant_types text NOT NULL,
    client_name text,
    client_uri text,
    logo_uri text,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL,
    deleted_at timestamp with time zone,
    client_type auth.oauth_client_type DEFAULT 'confidential'::auth.oauth_client_type NOT NULL,
    token_endpoint_auth_method text NOT NULL,
    CONSTRAINT oauth_clients_client_name_length CHECK ((char_length(client_name) <= 1024)),
    CONSTRAINT oauth_clients_client_uri_length CHECK ((char_length(client_uri) <= 2048)),
    CONSTRAINT oauth_clients_logo_uri_length CHECK ((char_length(logo_uri) <= 2048)),
    CONSTRAINT oauth_clients_token_endpoint_auth_method_check CHECK ((token_endpoint_auth_method = ANY (ARRAY['client_secret_basic'::text, 'client_secret_post'::text, 'none'::text])))
);


--
-- Name: oauth_consents; Type: TABLE; Schema: auth; Owner: -
--

CREATE TABLE auth.oauth_consents (
    id uuid NOT NULL,
    user_id uuid NOT NULL,
    client_id uuid NOT NULL,
    scopes text NOT NULL,
    granted_at timestamp with time zone DEFAULT now() NOT NULL,
    revoked_at timestamp with time zone,
    CONSTRAINT oauth_consents_revoked_after_granted CHECK (((revoked_at IS NULL) OR (revoked_at >= granted_at))),
    CONSTRAINT oauth_consents_scopes_length CHECK ((char_length(scopes) <= 2048)),
    CONSTRAINT oauth_consents_scopes_not_empty CHECK ((char_length(TRIM(BOTH FROM scopes)) > 0))
);


--
-- Name: one_time_tokens; Type: TABLE; Schema: auth; Owner: -
--

CREATE TABLE auth.one_time_tokens (
    id uuid NOT NULL,
    user_id uuid NOT NULL,
    token_type auth.one_time_token_type NOT NULL,
    token_hash text NOT NULL,
    relates_to text NOT NULL,
    created_at timestamp without time zone DEFAULT now() NOT NULL,
    updated_at timestamp without time zone DEFAULT now() NOT NULL,
    CONSTRAINT one_time_tokens_token_hash_check CHECK ((char_length(token_hash) > 0))
);


--
-- Name: refresh_tokens; Type: TABLE; Schema: auth; Owner: -
--

CREATE TABLE auth.refresh_tokens (
    instance_id uuid,
    id bigint NOT NULL,
    token character varying(255),
    user_id character varying(255),
    revoked boolean,
    created_at timestamp with time zone,
    updated_at timestamp with time zone,
    parent character varying(255),
    session_id uuid
);


--
-- Name: TABLE refresh_tokens; Type: COMMENT; Schema: auth; Owner: -
--

COMMENT ON TABLE auth.refresh_tokens IS 'Auth: Store of tokens used to refresh JWT tokens once they expire.';


--
-- Name: refresh_tokens_id_seq; Type: SEQUENCE; Schema: auth; Owner: -
--

CREATE SEQUENCE auth.refresh_tokens_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: refresh_tokens_id_seq; Type: SEQUENCE OWNED BY; Schema: auth; Owner: -
--

ALTER SEQUENCE auth.refresh_tokens_id_seq OWNED BY auth.refresh_tokens.id;


--
-- Name: saml_providers; Type: TABLE; Schema: auth; Owner: -
--

CREATE TABLE auth.saml_providers (
    id uuid NOT NULL,
    sso_provider_id uuid NOT NULL,
    entity_id text NOT NULL,
    metadata_xml text NOT NULL,
    metadata_url text,
    attribute_mapping jsonb,
    created_at timestamp with time zone,
    updated_at timestamp with time zone,
    name_id_format text,
    CONSTRAINT "entity_id not empty" CHECK ((char_length(entity_id) > 0)),
    CONSTRAINT "metadata_url not empty" CHECK (((metadata_url = NULL::text) OR (char_length(metadata_url) > 0))),
    CONSTRAINT "metadata_xml not empty" CHECK ((char_length(metadata_xml) > 0))
);


--
-- Name: TABLE saml_providers; Type: COMMENT; Schema: auth; Owner: -
--

COMMENT ON TABLE auth.saml_providers IS 'Auth: Manages SAML Identity Provider connections.';


--
-- Name: saml_relay_states; Type: TABLE; Schema: auth; Owner: -
--

CREATE TABLE auth.saml_relay_states (
    id uuid NOT NULL,
    sso_provider_id uuid NOT NULL,
    request_id text NOT NULL,
    for_email text,
    redirect_to text,
    created_at timestamp with time zone,
    updated_at timestamp with time zone,
    flow_state_id uuid,
    CONSTRAINT "request_id not empty" CHECK ((char_length(request_id) > 0))
);


--
-- Name: TABLE saml_relay_states; Type: COMMENT; Schema: auth; Owner: -
--

COMMENT ON TABLE auth.saml_relay_states IS 'Auth: Contains SAML Relay State information for each Service Provider initiated login.';


--
-- Name: schema_migrations; Type: TABLE; Schema: auth; Owner: -
--

CREATE TABLE auth.schema_migrations (
    version character varying(255) NOT NULL
);


--
-- Name: TABLE schema_migrations; Type: COMMENT; Schema: auth; Owner: -
--

COMMENT ON TABLE auth.schema_migrations IS 'Auth: Manages updates to the auth system.';


--
-- Name: sessions; Type: TABLE; Schema: auth; Owner: -
--

CREATE TABLE auth.sessions (
    id uuid NOT NULL,
    user_id uuid NOT NULL,
    created_at timestamp with time zone,
    updated_at timestamp with time zone,
    factor_id uuid,
    aal auth.aal_level,
    not_after timestamp with time zone,
    refreshed_at timestamp without time zone,
    user_agent text,
    ip inet,
    tag text,
    oauth_client_id uuid,
    refresh_token_hmac_key text,
    refresh_token_counter bigint,
    scopes text,
    CONSTRAINT sessions_scopes_length CHECK ((char_length(scopes) <= 4096))
);


--
-- Name: TABLE sessions; Type: COMMENT; Schema: auth; Owner: -
--

COMMENT ON TABLE auth.sessions IS 'Auth: Stores session data associated to a user.';


--
-- Name: COLUMN sessions.not_after; Type: COMMENT; Schema: auth; Owner: -
--

COMMENT ON COLUMN auth.sessions.not_after IS 'Auth: Not after is a nullable column that contains a timestamp after which the session should be regarded as expired.';


--
-- Name: COLUMN sessions.refresh_token_hmac_key; Type: COMMENT; Schema: auth; Owner: -
--

COMMENT ON COLUMN auth.sessions.refresh_token_hmac_key IS 'Holds a HMAC-SHA256 key used to sign refresh tokens for this session.';


--
-- Name: COLUMN sessions.refresh_token_counter; Type: COMMENT; Schema: auth; Owner: -
--

COMMENT ON COLUMN auth.sessions.refresh_token_counter IS 'Holds the ID (counter) of the last issued refresh token.';


--
-- Name: sso_domains; Type: TABLE; Schema: auth; Owner: -
--

CREATE TABLE auth.sso_domains (
    id uuid NOT NULL,
    sso_provider_id uuid NOT NULL,
    domain text NOT NULL,
    created_at timestamp with time zone,
    updated_at timestamp with time zone,
    CONSTRAINT "domain not empty" CHECK ((char_length(domain) > 0))
);


--
-- Name: TABLE sso_domains; Type: COMMENT; Schema: auth; Owner: -
--

COMMENT ON TABLE auth.sso_domains IS 'Auth: Manages SSO email address domain mapping to an SSO Identity Provider.';


--
-- Name: sso_providers; Type: TABLE; Schema: auth; Owner: -
--

CREATE TABLE auth.sso_providers (
    id uuid NOT NULL,
    resource_id text,
    created_at timestamp with time zone,
    updated_at timestamp with time zone,
    disabled boolean,
    CONSTRAINT "resource_id not empty" CHECK (((resource_id = NULL::text) OR (char_length(resource_id) > 0)))
);


--
-- Name: TABLE sso_providers; Type: COMMENT; Schema: auth; Owner: -
--

COMMENT ON TABLE auth.sso_providers IS 'Auth: Manages SSO identity provider information; see saml_providers for SAML.';


--
-- Name: COLUMN sso_providers.resource_id; Type: COMMENT; Schema: auth; Owner: -
--

COMMENT ON COLUMN auth.sso_providers.resource_id IS 'Auth: Uniquely identifies a SSO provider according to a user-chosen resource ID (case insensitive), useful in infrastructure as code.';


--
-- Name: users; Type: TABLE; Schema: auth; Owner: -
--

CREATE TABLE auth.users (
    instance_id uuid,
    id uuid NOT NULL,
    aud character varying(255),
    role character varying(255),
    email character varying(255),
    encrypted_password character varying(255),
    email_confirmed_at timestamp with time zone,
    invited_at timestamp with time zone,
    confirmation_token character varying(255),
    confirmation_sent_at timestamp with time zone,
    recovery_token character varying(255),
    recovery_sent_at timestamp with time zone,
    email_change_token_new character varying(255),
    email_change character varying(255),
    email_change_sent_at timestamp with time zone,
    last_sign_in_at timestamp with time zone,
    raw_app_meta_data jsonb,
    raw_user_meta_data jsonb,
    is_super_admin boolean,
    created_at timestamp with time zone,
    updated_at timestamp with time zone,
    phone text DEFAULT NULL::character varying,
    phone_confirmed_at timestamp with time zone,
    phone_change text DEFAULT ''::character varying,
    phone_change_token character varying(255) DEFAULT ''::character varying,
    phone_change_sent_at timestamp with time zone,
    confirmed_at timestamp with time zone GENERATED ALWAYS AS (LEAST(email_confirmed_at, phone_confirmed_at)) STORED,
    email_change_token_current character varying(255) DEFAULT ''::character varying,
    email_change_confirm_status smallint DEFAULT 0,
    banned_until timestamp with time zone,
    reauthentication_token character varying(255) DEFAULT ''::character varying,
    reauthentication_sent_at timestamp with time zone,
    is_sso_user boolean DEFAULT false NOT NULL,
    deleted_at timestamp with time zone,
    is_anonymous boolean DEFAULT false NOT NULL,
    CONSTRAINT users_email_change_confirm_status_check CHECK (((email_change_confirm_status >= 0) AND (email_change_confirm_status <= 2)))
);


--
-- Name: TABLE users; Type: COMMENT; Schema: auth; Owner: -
--

COMMENT ON TABLE auth.users IS 'Auth: Stores user login data within a secure schema.';


--
-- Name: COLUMN users.is_sso_user; Type: COMMENT; Schema: auth; Owner: -
--

COMMENT ON COLUMN auth.users.is_sso_user IS 'Auth: Set this column to true when the account comes from SSO. These accounts can have duplicate emails.';


--
-- Name: webauthn_challenges; Type: TABLE; Schema: auth; Owner: -
--

CREATE TABLE auth.webauthn_challenges (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    user_id uuid,
    challenge_type text NOT NULL,
    session_data jsonb NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    expires_at timestamp with time zone NOT NULL,
    CONSTRAINT webauthn_challenges_challenge_type_check CHECK ((challenge_type = ANY (ARRAY['signup'::text, 'registration'::text, 'authentication'::text])))
);


--
-- Name: webauthn_credentials; Type: TABLE; Schema: auth; Owner: -
--

CREATE TABLE auth.webauthn_credentials (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    user_id uuid NOT NULL,
    credential_id bytea NOT NULL,
    public_key bytea NOT NULL,
    attestation_type text DEFAULT ''::text NOT NULL,
    aaguid uuid,
    sign_count bigint DEFAULT 0 NOT NULL,
    transports jsonb DEFAULT '[]'::jsonb NOT NULL,
    backup_eligible boolean DEFAULT false NOT NULL,
    backed_up boolean DEFAULT false NOT NULL,
    friendly_name text DEFAULT ''::text NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL,
    last_used_at timestamp with time zone
);


--
-- Name: app_versions; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.app_versions (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    platform public.release_platform NOT NULL,
    version_number text NOT NULL,
    build_number integer NOT NULL,
    release_type public.release_type DEFAULT 'stable'::public.release_type NOT NULL,
    file_url text NOT NULL,
    changelog text,
    is_latest boolean DEFAULT false NOT NULL,
    force_update boolean DEFAULT false NOT NULL,
    published_at timestamp with time zone DEFAULT timezone('utc'::text, now()) NOT NULL,
    created_at timestamp with time zone DEFAULT timezone('utc'::text, now()) NOT NULL,
    is_deleted boolean DEFAULT false,
    updated_at timestamp with time zone DEFAULT now()
);


--
-- Name: case_studies; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.case_studies (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    title text NOT NULL,
    slug text NOT NULL,
    cover_image_url text NOT NULL,
    short_description text NOT NULL,
    language text DEFAULT 'hindi'::text NOT NULL,
    tags text[] DEFAULT '{}'::text[] NOT NULL,
    read_time_minutes integer,
    schema_version integer DEFAULT 1 NOT NULL,
    content_blocks jsonb DEFAULT '{"blocks": [], "schema_version": 1}'::jsonb NOT NULL,
    additional_image_urls text[] DEFAULT '{}'::text[] NOT NULL,
    author_type text DEFAULT 'platform'::text NOT NULL,
    author_user_id uuid,
    author_organization_id uuid,
    author_parent_organization_id uuid,
    status text DEFAULT 'draft'::text NOT NULL,
    visibility text DEFAULT 'public'::text NOT NULL,
    published_at timestamp with time zone,
    scheduled_publish_at timestamp with time zone,
    moderated_by uuid,
    moderated_at timestamp with time zone,
    moderation_note text,
    view_count integer DEFAULT 0 NOT NULL,
    is_deleted boolean DEFAULT false NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL,
    CONSTRAINT case_studies_author_type_check CHECK ((author_type = ANY (ARRAY['platform'::text, 'user'::text, 'organization'::text, 'parent_organization'::text]))),
    CONSTRAINT case_studies_language_check CHECK ((language = ANY (ARRAY['hindi'::text, 'english'::text, 'bilingual'::text]))),
    CONSTRAINT case_studies_status_check CHECK ((status = ANY (ARRAY['draft'::text, 'under_review'::text, 'published'::text, 'archived'::text, 'rejected'::text]))),
    CONSTRAINT case_studies_visibility_check CHECK ((visibility = ANY (ARRAY['public'::text, 'institution_only'::text]))),
    CONSTRAINT cs_author_type_id_consistency CHECK ((((author_type = 'platform'::text) AND (author_user_id IS NULL) AND (author_organization_id IS NULL) AND (author_parent_organization_id IS NULL)) OR ((author_type = 'user'::text) AND (author_user_id IS NOT NULL) AND (author_organization_id IS NULL) AND (author_parent_organization_id IS NULL)) OR ((author_type = 'organization'::text) AND (author_organization_id IS NOT NULL) AND (author_user_id IS NULL) AND (author_parent_organization_id IS NULL)) OR ((author_type = 'parent_organization'::text) AND (author_parent_organization_id IS NOT NULL) AND (author_user_id IS NULL) AND (author_organization_id IS NULL)))),
    CONSTRAINT cs_max_additional_images CHECK (((array_length(additional_image_urls, 1) IS NULL) OR (array_length(additional_image_urls, 1) <= 4)))
);


--
-- Name: case_studies_preview; Type: VIEW; Schema: public; Owner: -
--

CREATE VIEW public.case_studies_preview AS
 SELECT id,
    title,
    slug,
    cover_image_url,
    short_description,
    language,
    tags,
    read_time_minutes,
    author_type,
    author_user_id,
    author_organization_id,
    author_parent_organization_id,
    view_count,
    published_at,
    created_at,
    updated_at
   FROM public.case_studies
  WHERE ((status = 'published'::text) AND (is_deleted = false))
  ORDER BY published_at DESC;


--
-- Name: case_study_bookmarks; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.case_study_bookmarks (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    case_study_id uuid NOT NULL,
    user_id uuid NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL
);


--
-- Name: case_study_reactions; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.case_study_reactions (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    case_study_id uuid NOT NULL,
    user_id uuid NOT NULL,
    reaction_type text DEFAULT 'helpful'::text NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL,
    CONSTRAINT case_study_reactions_reaction_type_check CHECK ((reaction_type = ANY (ARRAY['helpful'::text, 'insightful'::text, 'inspiring'::text])))
);


--
-- Name: content_contributor_verifications; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.content_contributor_verifications (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    contributor_type text NOT NULL,
    user_id uuid,
    organization_id uuid,
    parent_organization_id uuid,
    status text DEFAULT 'pending'::text NOT NULL,
    applicant_note text,
    reviewed_by uuid,
    reviewed_at timestamp with time zone,
    rejection_reason text,
    suspension_reason text,
    applied_at timestamp with time zone DEFAULT now() NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL,
    CONSTRAINT ccv_type_id_consistency CHECK ((((contributor_type = 'user'::text) AND (user_id IS NOT NULL)) OR ((contributor_type = 'organization'::text) AND (user_id IS NOT NULL) AND (organization_id IS NOT NULL)) OR ((contributor_type = 'parent_organization'::text) AND (user_id IS NOT NULL) AND (parent_organization_id IS NOT NULL)))),
    CONSTRAINT content_contributor_verifications_contributor_type_check CHECK ((contributor_type = ANY (ARRAY['user'::text, 'organization'::text, 'parent_organization'::text]))),
    CONSTRAINT content_contributor_verifications_status_check CHECK ((status = ANY (ARRAY['pending'::text, 'approved'::text, 'rejected'::text, 'suspended'::text])))
);


--
-- Name: experience_inspirations; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.experience_inspirations (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    experience_id uuid NOT NULL,
    user_id uuid NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL
);


--
-- Name: experiences; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.experiences (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    title text NOT NULL,
    cover_image_url text,
    description text NOT NULL,
    author_user_id uuid NOT NULL,
    inspired_count integer DEFAULT 0 NOT NULL,
    status text DEFAULT 'published'::text NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL,
    CONSTRAINT experiences_status_check CHECK ((status = ANY (ARRAY['published'::text, 'draft'::text, 'archived'::text])))
);


--
-- Name: global_areas; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.global_areas (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    state_id uuid NOT NULL,
    district_id uuid NOT NULL,
    tehsil_id uuid NOT NULL,
    name text NOT NULL,
    code text,
    is_active boolean DEFAULT true NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL,
    panchayat_id uuid
);


--
-- Name: global_attendance_status; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.global_attendance_status (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    name text NOT NULL,
    code text NOT NULL,
    is_active boolean DEFAULT true NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL
);


--
-- Name: global_blood_groups; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.global_blood_groups (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    name text NOT NULL,
    is_active boolean DEFAULT true NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL
);


--
-- Name: global_boards; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.global_boards (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    name text NOT NULL,
    code text NOT NULL,
    description text,
    is_active boolean DEFAULT true NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL,
    created_by uuid,
    updated_by uuid
);


--
-- Name: global_classes; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.global_classes (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    name text NOT NULL,
    code text NOT NULL,
    level_order integer NOT NULL,
    education_level text,
    is_active boolean DEFAULT true NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL,
    created_by uuid,
    updated_by uuid,
    is_deleted boolean DEFAULT false NOT NULL
);


--
-- Name: global_countries; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.global_countries (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    name text NOT NULL,
    code text,
    is_active boolean DEFAULT true NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL
);


--
-- Name: global_districts; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.global_districts (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    state_id uuid NOT NULL,
    name text NOT NULL,
    code text,
    is_active boolean DEFAULT true NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL
);


--
-- Name: global_document_types; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.global_document_types (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    code text NOT NULL,
    name text NOT NULL,
    category text NOT NULL,
    is_active boolean DEFAULT true NOT NULL,
    is_deleted boolean DEFAULT false NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL
);


--
-- Name: global_exam_subject_rules; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.global_exam_subject_rules (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    class_id uuid NOT NULL,
    subject_id uuid NOT NULL,
    exam_type_id uuid NOT NULL,
    default_max_marks numeric DEFAULT 100 NOT NULL,
    default_passing_marks numeric DEFAULT 33 NOT NULL,
    default_grading_system jsonb DEFAULT '{"A": 80, "B": 70, "C": 60, "D": 50, "E": 40, "F": 0, "A+": 90}'::jsonb NOT NULL,
    is_active boolean DEFAULT true NOT NULL
);


--
-- Name: global_exam_types; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.global_exam_types (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    name text NOT NULL,
    code text,
    description text,
    is_active boolean DEFAULT true NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL,
    created_by uuid,
    updated_by uuid
);


--
-- Name: global_expense_types; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.global_expense_types (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    name text NOT NULL,
    code text,
    is_active boolean DEFAULT true NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL,
    reference_type public.expense_reference_type
);


--
-- Name: global_facilities; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.global_facilities (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    name text NOT NULL,
    code text NOT NULL,
    description text,
    is_active boolean DEFAULT true NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL
);


--
-- Name: global_fee_heads; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.global_fee_heads (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    name text NOT NULL,
    code text NOT NULL,
    description text,
    is_active boolean DEFAULT true NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL,
    created_by uuid,
    updated_by uuid,
    is_additional boolean DEFAULT false NOT NULL
);


--
-- Name: global_journey_mcqs; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.global_journey_mcqs (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    template_id uuid NOT NULL,
    day_number integer NOT NULL,
    question_text text NOT NULL,
    option_a text NOT NULL,
    option_b text NOT NULL,
    option_c text NOT NULL,
    option_d text NOT NULL,
    correct_option character(1) NOT NULL,
    explanation text,
    difficulty_level text DEFAULT 'medium'::text NOT NULL,
    points integer DEFAULT 1 NOT NULL,
    is_active boolean DEFAULT true NOT NULL,
    is_deleted boolean DEFAULT false NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL,
    created_by uuid,
    updated_by uuid
);


--
-- Name: global_journey_tasks; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.global_journey_tasks (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    template_id uuid NOT NULL,
    day_number integer NOT NULL,
    task_title text NOT NULL,
    task_description text,
    task_type text DEFAULT 'read'::text NOT NULL,
    sort_order integer DEFAULT 1 NOT NULL,
    is_active boolean DEFAULT true NOT NULL,
    is_deleted boolean DEFAULT false NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL,
    created_by uuid,
    updated_by uuid
);


--
-- Name: global_journey_templates; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.global_journey_templates (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    title text NOT NULL,
    description text,
    duration_days integer NOT NULL,
    category text,
    version integer DEFAULT 1 NOT NULL,
    is_active boolean DEFAULT true NOT NULL,
    is_deleted boolean DEFAULT false NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL,
    created_by uuid,
    updated_by uuid
);


--
-- Name: global_languages; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.global_languages (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    name text NOT NULL,
    code text NOT NULL,
    is_active boolean DEFAULT true NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL
);


--
-- Name: global_marksheet_templates; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.global_marksheet_templates (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    board_id uuid NOT NULL,
    class_id uuid NOT NULL,
    name text NOT NULL,
    code text NOT NULL,
    template_json jsonb NOT NULL,
    is_default boolean DEFAULT false NOT NULL,
    is_active boolean DEFAULT true NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL,
    created_by uuid,
    updated_by uuid
);


--
-- Name: global_mediums; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.global_mediums (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    board_id uuid NOT NULL,
    name text NOT NULL,
    code text NOT NULL,
    is_active boolean DEFAULT true NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL,
    created_by uuid,
    updated_by uuid
);


--
-- Name: global_organization_parent_types; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.global_organization_parent_types (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    name text NOT NULL,
    code text NOT NULL,
    description text,
    is_active boolean DEFAULT true NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL
);


--
-- Name: global_organization_types; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.global_organization_types (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    name text NOT NULL,
    code text NOT NULL,
    description text,
    is_active boolean DEFAULT true NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL,
    is_own boolean DEFAULT true NOT NULL
);


--
-- Name: global_panchayats; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.global_panchayats (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    state_id uuid NOT NULL,
    district_id uuid NOT NULL,
    tehsil_id uuid NOT NULL,
    name text NOT NULL,
    code text,
    is_active boolean DEFAULT true NOT NULL,
    created_at timestamp with time zone DEFAULT now(),
    updated_at timestamp with time zone DEFAULT now()
);


--
-- Name: global_relationship_types; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.global_relationship_types (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    name text NOT NULL,
    code text NOT NULL,
    is_active boolean DEFAULT true NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL
);


--
-- Name: global_result_status; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.global_result_status (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    name text NOT NULL,
    code text NOT NULL,
    is_active boolean DEFAULT true NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL
);


--
-- Name: global_sessions; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.global_sessions (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    name text NOT NULL,
    is_active boolean DEFAULT true NOT NULL,
    is_deleted boolean DEFAULT false NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL,
    starting_date date,
    ending_date date,
    update_before_ending_days integer DEFAULT 30
);


--
-- Name: global_staff_roles; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.global_staff_roles (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    name text NOT NULL,
    code text NOT NULL,
    description text,
    is_active boolean DEFAULT true NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL,
    is_deleted boolean DEFAULT false NOT NULL
);


--
-- Name: global_states; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.global_states (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    country_id uuid NOT NULL,
    name text NOT NULL,
    code text,
    is_active boolean DEFAULT true NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL
);


--
-- Name: global_student_categories; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.global_student_categories (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    name text NOT NULL,
    code text NOT NULL,
    description text,
    is_active boolean DEFAULT true NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL
);


--
-- Name: global_student_status; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.global_student_status (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    name text NOT NULL,
    code text NOT NULL,
    is_active boolean DEFAULT true NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL
);


--
-- Name: global_subjects; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.global_subjects (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    name text NOT NULL,
    code text,
    subject_type text,
    is_active boolean DEFAULT true NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL,
    created_by uuid,
    updated_by uuid
);


--
-- Name: global_tehsils; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.global_tehsils (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    state_id uuid NOT NULL,
    district_id uuid NOT NULL,
    name text NOT NULL,
    code text,
    is_active boolean DEFAULT true NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL
);


--
-- Name: messages; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.messages (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    room_id uuid NOT NULL,
    user_id uuid NOT NULL,
    content text NOT NULL,
    is_hidden boolean DEFAULT false NOT NULL,
    is_deleted boolean DEFAULT false NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL
);


--
-- Name: moderation_settings; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.moderation_settings (
    id integer DEFAULT 1 NOT NULL,
    blocked_keywords jsonb DEFAULT '[]'::jsonb NOT NULL,
    is_active boolean DEFAULT true NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL,
    CONSTRAINT only_one_row CHECK ((id = 1))
);


--
-- Name: organization_attendance_status; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.organization_attendance_status (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    organization_id uuid NOT NULL,
    attendance_status_id uuid NOT NULL,
    salary_payout_percentage numeric DEFAULT 100.0 NOT NULL,
    is_active boolean DEFAULT true NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL
);


--
-- Name: organization_boards; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.organization_boards (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    organization_id uuid NOT NULL,
    board_id uuid NOT NULL,
    is_active boolean DEFAULT true NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    is_deleted boolean DEFAULT false NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL,
    created_by uuid,
    updated_by uuid
);


--
-- Name: organization_bus_child_assignments; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.organization_bus_child_assignments (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    parent_organization_id uuid NOT NULL,
    organization_id uuid NOT NULL,
    active_session_id uuid NOT NULL,
    bus_id uuid NOT NULL,
    is_active boolean DEFAULT true NOT NULL,
    is_deleted boolean DEFAULT false NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL,
    created_by uuid,
    updated_by uuid
);


--
-- Name: organization_classes; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.organization_classes (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    organization_id uuid NOT NULL,
    class_id uuid NOT NULL,
    custom_class_name text,
    next_promotion_class_id uuid,
    is_active boolean DEFAULT true NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    is_deleted boolean DEFAULT false NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL,
    created_by uuid,
    updated_by uuid
);


--
-- Name: organization_content_assignments; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.organization_content_assignments (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    content_id uuid NOT NULL,
    target_organization_id uuid NOT NULL,
    assigned_at timestamp with time zone DEFAULT now() NOT NULL
);


--
-- Name: organization_contents; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.organization_contents (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    content_type text NOT NULL,
    publisher_type text NOT NULL,
    parent_organization_id uuid,
    organization_id uuid,
    session_id uuid NOT NULL,
    title text,
    description text,
    image_url text,
    target_scope text DEFAULT 'self'::text NOT NULL,
    target_roles text[] DEFAULT '{}'::text[] NOT NULL,
    status text DEFAULT 'published'::text NOT NULL,
    is_active boolean DEFAULT true NOT NULL,
    is_deleted boolean DEFAULT false NOT NULL,
    created_by uuid,
    updated_by uuid,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL,
    CONSTRAINT check_content_type CHECK ((content_type = ANY (ARRAY['notice'::text, 'gallery'::text]))),
    CONSTRAINT check_gallery_requirements CHECK (((content_type <> 'gallery'::text) OR (image_url IS NOT NULL))),
    CONSTRAINT check_notice_requirements CHECK (((content_type <> 'notice'::text) OR ((title IS NOT NULL) AND (description IS NOT NULL)))),
    CONSTRAINT check_publisher_id_consistency CHECK ((((publisher_type = 'child'::text) AND (organization_id IS NOT NULL)) OR ((publisher_type = 'parent'::text) AND (parent_organization_id IS NOT NULL) AND (organization_id IS NULL)))),
    CONSTRAINT check_publisher_type CHECK ((publisher_type = ANY (ARRAY['parent'::text, 'child'::text]))),
    CONSTRAINT check_target_roles CHECK ((target_roles <@ ARRAY['student'::text, 'parents'::text, 'teacher'::text, 'all'::text])),
    CONSTRAINT check_target_scope CHECK ((target_scope = ANY (ARRAY['self'::text, 'all_children'::text, 'selected_children'::text])))
);


--
-- Name: organization_exam_subject_settings; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.organization_exam_subject_settings (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    organization_id uuid NOT NULL,
    active_session_id uuid NOT NULL,
    exam_id uuid NOT NULL,
    class_id uuid NOT NULL,
    subject_id uuid NOT NULL,
    max_marks numeric,
    minimum_passing_marks numeric,
    grading_system jsonb,
    is_deleted boolean DEFAULT false NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL
);


--
-- Name: organization_exams; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.organization_exams (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    organization_id uuid NOT NULL,
    active_session_id uuid NOT NULL,
    exam_type_id uuid NOT NULL,
    name text NOT NULL,
    start_date date,
    end_date date,
    is_active boolean DEFAULT true NOT NULL,
    is_deleted boolean DEFAULT false NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL
);


--
-- Name: organization_fee_assignments; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.organization_fee_assignments (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    organization_id uuid NOT NULL,
    session_id uuid NOT NULL,
    organization_class_id uuid NOT NULL,
    fee_head_id uuid NOT NULL,
    amount numeric DEFAULT 0 NOT NULL,
    is_active boolean DEFAULT true NOT NULL,
    is_deleted boolean DEFAULT false NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL,
    created_by uuid,
    updated_by uuid
);


--
-- Name: organization_guardian_user_links; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.organization_guardian_user_links (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    organization_id uuid NOT NULL,
    active_session_id uuid NOT NULL,
    guardian_id uuid NOT NULL,
    user_id uuid NOT NULL,
    is_approved boolean DEFAULT false NOT NULL,
    approved_by uuid,
    approved_at timestamp with time zone,
    is_active boolean DEFAULT true NOT NULL,
    is_deleted boolean DEFAULT false NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL
);


--
-- Name: organization_guardians; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.organization_guardians (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    organization_id uuid NOT NULL,
    active_session_id uuid NOT NULL,
    name text NOT NULL,
    mobile_number text NOT NULL,
    relationship_type_id uuid NOT NULL,
    is_active boolean DEFAULT true NOT NULL,
    is_deleted boolean DEFAULT false NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL,
    created_by uuid,
    updated_by uuid
);


--
-- Name: organization_holidays; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.organization_holidays (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    parent_organization_id uuid NOT NULL,
    organization_id uuid NOT NULL,
    active_session_id uuid NOT NULL,
    name text NOT NULL,
    start_date date NOT NULL,
    end_date date NOT NULL,
    description text,
    is_active boolean DEFAULT true NOT NULL,
    is_deleted boolean DEFAULT false NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL,
    created_by uuid,
    updated_by uuid
);


--
-- Name: organization_inquiries; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.organization_inquiries (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    organization_id uuid NOT NULL,
    active_session_id uuid NOT NULL,
    student_name text NOT NULL,
    guardian_name text NOT NULL,
    mobile_number text NOT NULL,
    class_id uuid,
    inquiry_date date DEFAULT CURRENT_DATE NOT NULL,
    status text DEFAULT 'Pending'::text NOT NULL,
    remarks text,
    is_active boolean DEFAULT true NOT NULL,
    is_deleted boolean DEFAULT false NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL,
    created_by uuid,
    updated_by uuid,
    CONSTRAINT organization_inquiries_status_check CHECK ((status = ANY (ARRAY['Pending'::text, 'Admitted'::text, 'Cancelled'::text])))
);


--
-- Name: organization_journey_mcqs; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.organization_journey_mcqs (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    template_id uuid NOT NULL,
    day_number integer NOT NULL,
    question_text text NOT NULL,
    option_a text NOT NULL,
    option_b text NOT NULL,
    option_c text NOT NULL,
    option_d text NOT NULL,
    correct_option character(1) NOT NULL,
    explanation text,
    difficulty_level text DEFAULT 'medium'::text NOT NULL,
    points integer DEFAULT 1 NOT NULL,
    is_active boolean DEFAULT true NOT NULL,
    is_deleted boolean DEFAULT false NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL,
    created_by uuid,
    updated_by uuid
);


--
-- Name: organization_journey_tasks; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.organization_journey_tasks (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    template_id uuid NOT NULL,
    day_number integer NOT NULL,
    task_title text NOT NULL,
    task_description text,
    task_type text DEFAULT 'read'::text NOT NULL,
    sort_order integer DEFAULT 1 NOT NULL,
    is_active boolean DEFAULT true NOT NULL,
    is_deleted boolean DEFAULT false NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL,
    created_by uuid,
    updated_by uuid
);


--
-- Name: organization_journey_templates; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.organization_journey_templates (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    organization_id uuid NOT NULL,
    title text NOT NULL,
    description text,
    duration_days integer NOT NULL,
    category text,
    version integer DEFAULT 1 NOT NULL,
    is_active boolean DEFAULT true NOT NULL,
    is_deleted boolean DEFAULT false NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL,
    created_by uuid,
    updated_by uuid
);


--
-- Name: organization_languages; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.organization_languages (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    organization_id uuid NOT NULL,
    language_id uuid NOT NULL,
    is_active boolean DEFAULT true NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    is_deleted boolean DEFAULT false NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL,
    created_by uuid,
    updated_by uuid
);


--
-- Name: organization_leaves; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.organization_leaves (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    parent_organization_id uuid NOT NULL,
    organization_id uuid,
    active_session_id uuid NOT NULL,
    applicant_type text NOT NULL,
    staff_id uuid,
    student_id uuid,
    leave_type text NOT NULL,
    start_date date NOT NULL,
    end_date date NOT NULL,
    is_half_day boolean DEFAULT false NOT NULL,
    half_day_period text,
    reason text,
    status text DEFAULT 'Pending'::text NOT NULL,
    action_remarks text,
    action_by uuid,
    action_at timestamp with time zone,
    is_active boolean DEFAULT true NOT NULL,
    is_deleted boolean DEFAULT false NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL,
    created_by uuid,
    updated_by uuid,
    CONSTRAINT leaves_applicant_check CHECK ((((applicant_type = 'staff'::text) AND (staff_id IS NOT NULL) AND (student_id IS NULL)) OR ((applicant_type = 'student'::text) AND (student_id IS NOT NULL) AND (staff_id IS NULL)))),
    CONSTRAINT organization_leaves_applicant_type_check CHECK ((applicant_type = ANY (ARRAY['staff'::text, 'student'::text]))),
    CONSTRAINT organization_leaves_half_day_period_check CHECK ((half_day_period = ANY (ARRAY['First Half'::text, 'Second Half'::text]))),
    CONSTRAINT organization_leaves_status_check CHECK ((status = ANY (ARRAY['Pending'::text, 'Approved'::text, 'Rejected'::text])))
);


--
-- Name: organization_mediums; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.organization_mediums (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    organization_id uuid NOT NULL,
    medium_id uuid NOT NULL,
    is_active boolean DEFAULT true NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    is_deleted boolean DEFAULT false NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL,
    created_by uuid,
    updated_by uuid
);


--
-- Name: organization_parent_bus_live_locations; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.organization_parent_bus_live_locations (
    bus_id uuid NOT NULL,
    parent_organization_id uuid NOT NULL,
    active_session_id uuid,
    latitude double precision NOT NULL,
    longitude double precision NOT NULL,
    speed numeric DEFAULT 0.0 NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL
);

ALTER TABLE ONLY public.organization_parent_bus_live_locations REPLICA IDENTITY FULL;


--
-- Name: organization_parent_bus_staff_assignments; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.organization_parent_bus_staff_assignments (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    parent_organization_id uuid NOT NULL,
    active_session_id uuid NOT NULL,
    bus_id uuid NOT NULL,
    staff_id uuid NOT NULL,
    role_in_bus text NOT NULL,
    is_active boolean DEFAULT true NOT NULL,
    is_deleted boolean DEFAULT false NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL,
    created_by uuid,
    updated_by uuid,
    CONSTRAINT organization_parent_bus_staff_assignments_role_in_bus_check CHECK ((role_in_bus = ANY (ARRAY['Driver'::text, 'Conductor'::text, 'Helper'::text])))
);


--
-- Name: organization_parent_buses; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.organization_parent_buses (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    parent_organization_id uuid NOT NULL,
    active_session_id uuid NOT NULL,
    bus_number text NOT NULL,
    bus_name text,
    route_name text,
    max_capacity integer,
    insurance_expiry_date date,
    insurance_image_url text,
    fitness_expiry_date date,
    fitness_image_url text,
    pollution_expiry_date date,
    pollution_image_url text,
    is_active boolean DEFAULT true NOT NULL,
    is_deleted boolean DEFAULT false NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL,
    created_by uuid,
    updated_by uuid
);


--
-- Name: organization_parent_expenses; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.organization_parent_expenses (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    parent_organization_id uuid NOT NULL,
    active_session_id uuid NOT NULL,
    expense_type_id uuid NOT NULL,
    payment_method text NOT NULL,
    amount numeric DEFAULT 0 NOT NULL,
    receipt_uuid uuid,
    admin_note text,
    expense_date date DEFAULT CURRENT_DATE NOT NULL,
    reference_id uuid,
    reference_type text,
    cash_paid_to text,
    cheque_number text,
    cheque_date date,
    cheque_bank_name text,
    online_transaction_id text,
    online_payment_app text,
    vendor_name text,
    bill_number text,
    is_active boolean DEFAULT true NOT NULL,
    is_deleted boolean DEFAULT false NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL,
    created_by uuid,
    updated_by uuid,
    CONSTRAINT organization_parent_expenses_payment_method_check CHECK ((payment_method = ANY (ARRAY['Cash'::text, 'Cheque'::text, 'UPI'::text, 'Online'::text]))),
    CONSTRAINT organization_parent_expenses_reference_type_check CHECK ((reference_type = ANY (ARRAY['Bus'::text, 'ChildOrganization'::text, 'Salary'::text])))
);


--
-- Name: organization_parent_journey_mcqs; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.organization_parent_journey_mcqs (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    template_id uuid NOT NULL,
    day_number integer NOT NULL,
    question_text text NOT NULL,
    option_a text NOT NULL,
    option_b text NOT NULL,
    option_c text NOT NULL,
    option_d text NOT NULL,
    correct_option character(1) NOT NULL,
    explanation text,
    difficulty_level text DEFAULT 'medium'::text NOT NULL,
    points integer DEFAULT 1 NOT NULL,
    is_active boolean DEFAULT true NOT NULL,
    is_deleted boolean DEFAULT false NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL,
    created_by uuid,
    updated_by uuid
);


--
-- Name: organization_parent_journey_tasks; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.organization_parent_journey_tasks (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    template_id uuid NOT NULL,
    day_number integer NOT NULL,
    task_title text NOT NULL,
    task_description text,
    task_type text DEFAULT 'read'::text NOT NULL,
    sort_order integer DEFAULT 1 NOT NULL,
    is_active boolean DEFAULT true NOT NULL,
    is_deleted boolean DEFAULT false NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL,
    created_by uuid,
    updated_by uuid
);


--
-- Name: organization_parent_journey_templates; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.organization_parent_journey_templates (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    parent_organization_id uuid NOT NULL,
    title text NOT NULL,
    description text,
    duration_days integer NOT NULL,
    category text,
    version integer DEFAULT 1 NOT NULL,
    is_active boolean DEFAULT true NOT NULL,
    is_deleted boolean DEFAULT false NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL,
    created_by uuid,
    updated_by uuid
);


--
-- Name: organization_parent_staff; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.organization_parent_staff (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    parent_organization_id uuid NOT NULL,
    active_session_id uuid NOT NULL,
    role_id uuid NOT NULL,
    name text NOT NULL,
    mobile_number text NOT NULL,
    email text,
    gender text,
    date_of_joining date DEFAULT CURRENT_DATE NOT NULL,
    date_of_birth date,
    pan_number text,
    aadhaar_number text,
    license_number text,
    license_expiry_date date,
    address_area_id uuid,
    subject_id uuid,
    bus_id uuid,
    is_active boolean DEFAULT true NOT NULL,
    is_deleted boolean DEFAULT false NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL,
    created_by uuid,
    updated_by uuid,
    CONSTRAINT organization_parent_staff_gender_check CHECK ((gender = ANY (ARRAY['Male'::text, 'Female'::text, 'Other'::text])))
);


--
-- Name: organization_parent_staff_attendance; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.organization_parent_staff_attendance (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    parent_organization_id uuid NOT NULL,
    active_session_id uuid NOT NULL,
    staff_id uuid NOT NULL,
    attendance_date date DEFAULT CURRENT_DATE NOT NULL,
    status_id uuid NOT NULL,
    is_paid_leave boolean DEFAULT true NOT NULL,
    check_in_time time without time zone,
    check_out_time time without time zone,
    remarks text,
    is_active boolean DEFAULT true NOT NULL,
    is_deleted boolean DEFAULT false NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL,
    created_by uuid,
    updated_by uuid
);


--
-- Name: organization_parent_staff_bus_enrollments; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.organization_parent_staff_bus_enrollments (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    parent_organization_id uuid NOT NULL,
    organization_id uuid NOT NULL,
    active_session_id uuid NOT NULL,
    staff_id uuid NOT NULL,
    bus_id uuid NOT NULL,
    joining_date date DEFAULT CURRENT_DATE NOT NULL,
    monthly_fare numeric DEFAULT 0 NOT NULL,
    auto_deduct_from_salary boolean DEFAULT true NOT NULL,
    is_active boolean DEFAULT true NOT NULL,
    is_deleted boolean DEFAULT false NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL,
    created_by uuid,
    updated_by uuid
);


--
-- Name: organization_parent_staff_bus_fares; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.organization_parent_staff_bus_fares (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    parent_organization_id uuid NOT NULL,
    active_session_id uuid NOT NULL,
    staff_id uuid NOT NULL,
    bus_id uuid NOT NULL,
    fare_amount numeric DEFAULT 0 NOT NULL,
    is_active boolean DEFAULT true NOT NULL,
    is_deleted boolean DEFAULT false NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL,
    created_by uuid,
    updated_by uuid
);


--
-- Name: organization_parent_staff_leave_quotas; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.organization_parent_staff_leave_quotas (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    parent_organization_id uuid NOT NULL,
    organization_id uuid,
    active_session_id uuid NOT NULL,
    staff_id uuid NOT NULL,
    total_leaves numeric DEFAULT 12.0 NOT NULL,
    is_active boolean DEFAULT true NOT NULL,
    is_deleted boolean DEFAULT false NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL,
    created_by uuid,
    updated_by uuid
);


--
-- Name: organization_parent_staff_salaries; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.organization_parent_staff_salaries (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    parent_organization_id uuid NOT NULL,
    active_session_id uuid NOT NULL,
    staff_id uuid NOT NULL,
    monthly_salary numeric DEFAULT 0 NOT NULL,
    bank_name text,
    bank_account_number text,
    ifsc_code text,
    upi_id text,
    is_active boolean DEFAULT true NOT NULL,
    is_deleted boolean DEFAULT false NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL,
    created_by uuid,
    updated_by uuid
);


--
-- Name: organization_parent_staff_salary_payments; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.organization_parent_staff_salary_payments (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    parent_organization_id uuid NOT NULL,
    active_session_id uuid NOT NULL,
    staff_id uuid NOT NULL,
    salary_payout_id uuid NOT NULL,
    payment_date date DEFAULT CURRENT_DATE NOT NULL,
    amount_paid numeric DEFAULT 0 NOT NULL,
    payment_mode text NOT NULL,
    cash_paid_by_user_id uuid,
    cheque_number text,
    cheque_date date,
    cheque_bank_name text,
    online_transaction_id text,
    online_payment_app text,
    remarks text,
    is_active boolean DEFAULT true NOT NULL,
    is_deleted boolean DEFAULT false NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL,
    created_by uuid,
    updated_by uuid,
    CONSTRAINT organization_parent_staff_salary_payments_payment_mode_check CHECK ((payment_mode = ANY (ARRAY['Cash'::text, 'Cheque'::text, 'UPI'::text, 'Online'::text])))
);


--
-- Name: organization_parent_staff_salary_payouts; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.organization_parent_staff_salary_payouts (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    parent_organization_id uuid NOT NULL,
    active_session_id uuid NOT NULL,
    staff_id uuid NOT NULL,
    payout_month integer NOT NULL,
    payout_year integer NOT NULL,
    salary_amount numeric NOT NULL,
    is_locked boolean DEFAULT false NOT NULL,
    locked_at timestamp with time zone,
    locked_by uuid,
    is_active boolean DEFAULT true NOT NULL,
    is_deleted boolean DEFAULT false NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL,
    bonus numeric DEFAULT 0.0 NOT NULL,
    deduction numeric DEFAULT 0.0 NOT NULL,
    CONSTRAINT organization_parent_staff_salary_payouts_payout_month_check CHECK (((payout_month >= 1) AND (payout_month <= 12)))
);


--
-- Name: organization_parent_staff_user_links; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.organization_parent_staff_user_links (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    parent_organization_id uuid NOT NULL,
    active_session_id uuid NOT NULL,
    staff_id uuid NOT NULL,
    user_id uuid NOT NULL,
    is_approved boolean DEFAULT false NOT NULL,
    approved_by uuid,
    approved_at timestamp with time zone,
    is_active boolean DEFAULT true NOT NULL,
    is_deleted boolean DEFAULT false NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL
);


--
-- Name: organization_parents; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.organization_parents (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    name text NOT NULL,
    type_id uuid NOT NULL,
    is_active boolean DEFAULT true NOT NULL,
    is_deleted boolean DEFAULT false NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL
);


--
-- Name: organization_parents_profiles; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.organization_parents_profiles (
    parent_organization_id uuid NOT NULL,
    logo_url text,
    website_url text,
    email text NOT NULL,
    mobile_number text NOT NULL,
    alternate_mobile_number text,
    registration_number text,
    gst_number text,
    pan_number text,
    address_line1 text NOT NULL,
    address_line2 text,
    city text NOT NULL,
    state text DEFAULT 'Rajasthan'::text NOT NULL,
    pincode text NOT NULL,
    active_session_id uuid,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL,
    is_setup_complete boolean DEFAULT false NOT NULL
);


--
-- Name: organization_parents_session_tokens; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.organization_parents_session_tokens (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    parent_organization_user_id uuid NOT NULL,
    session_token text NOT NULL,
    refresh_token text NOT NULL,
    is_active boolean DEFAULT true NOT NULL,
    expires_at timestamp with time zone NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL
);


--
-- Name: organization_parents_users; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.organization_parents_users (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    parent_organization_id uuid NOT NULL,
    user_id uuid NOT NULL,
    role_id uuid,
    is_active boolean DEFAULT true NOT NULL,
    is_deleted boolean DEFAULT false NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL
);


--
-- Name: organization_periods; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.organization_periods (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    organization_id uuid NOT NULL,
    session_id uuid NOT NULL,
    name public.period_name_enum NOT NULL,
    start_time time without time zone NOT NULL,
    end_time time without time zone NOT NULL,
    is_break boolean DEFAULT false NOT NULL,
    sort_order integer NOT NULL,
    is_active boolean DEFAULT true NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL,
    created_by uuid,
    updated_by uuid,
    CONSTRAINT organization_periods_time_check CHECK ((start_time < end_time))
);


--
-- Name: organization_profiles; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.organization_profiles (
    organization_id uuid NOT NULL,
    logo_url text,
    website_url text,
    email text NOT NULL,
    mobile_number text NOT NULL,
    alternate_mobile_number text,
    registration_number text,
    gst_number text,
    pan_number text,
    address_line1 text NOT NULL,
    address_line2 text,
    city text NOT NULL,
    state text DEFAULT 'Rajasthan'::text NOT NULL,
    pincode text NOT NULL,
    active_session_id uuid,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL,
    is_setup_complete boolean DEFAULT false NOT NULL,
    is_active boolean DEFAULT true NOT NULL,
    is_deleted boolean DEFAULT false NOT NULL
);


--
-- Name: organization_sections; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.organization_sections (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    organization_class_id uuid NOT NULL,
    name text NOT NULL,
    code text NOT NULL,
    room_number text,
    max_capacity integer DEFAULT 40 NOT NULL,
    is_active boolean DEFAULT true NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    is_deleted boolean DEFAULT false NOT NULL,
    updated_by uuid,
    created_by uuid,
    updated_at timestamp with time zone DEFAULT now() NOT NULL
);


--
-- Name: organization_session_classes; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.organization_session_classes (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    organization_id uuid NOT NULL,
    session_id uuid NOT NULL,
    organization_class_id uuid NOT NULL,
    is_active boolean DEFAULT true NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    is_deleted boolean DEFAULT false NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL,
    created_by uuid,
    updated_by uuid
);


--
-- Name: organization_session_sections; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.organization_session_sections (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    organization_session_class_id uuid NOT NULL,
    organization_section_id uuid NOT NULL,
    class_teacher_id uuid,
    is_active boolean DEFAULT true NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    is_deleted boolean DEFAULT false NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL,
    created_by uuid,
    updated_by uuid
);


--
-- Name: organization_session_subjects; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.organization_session_subjects (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    organization_session_class_id uuid NOT NULL,
    subject_id uuid NOT NULL,
    subject_teacher_id uuid,
    custom_subject_name text,
    subject_code text,
    is_elective boolean DEFAULT false NOT NULL,
    weekly_hours numeric,
    is_active boolean DEFAULT true NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    is_deleted boolean DEFAULT false NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL,
    created_by uuid,
    updated_by uuid
);


--
-- Name: organization_session_tokens; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.organization_session_tokens (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    organization_user_id uuid NOT NULL,
    session_token text NOT NULL,
    refresh_token text NOT NULL,
    is_active boolean DEFAULT true NOT NULL,
    expires_at timestamp with time zone NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL
);


--
-- Name: organization_student_additional_details; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.organization_student_additional_details (
    student_id uuid NOT NULL,
    mother_tongue_id uuid,
    mother_tongue_text text,
    religion text,
    nationality text DEFAULT 'Indian'::text NOT NULL,
    identification_mark text,
    is_single_girl_child boolean DEFAULT false NOT NULL,
    caste_certificate_number text,
    father_name text,
    father_mobile text,
    father_email text,
    father_qualification text,
    father_occupation text,
    parents_aadhaar_father text,
    mother_name text,
    mother_mobile text,
    mother_email text,
    mother_qualification text,
    mother_occupation text,
    parents_aadhaar_mother text,
    family_annual_income numeric DEFAULT 0,
    permanent_address_details text,
    permanent_address_area text,
    permanent_address_area_id uuid,
    previous_school_name text,
    previous_class text,
    previous_board text,
    tc_number text,
    tc_date date,
    previous_marks numeric,
    height numeric,
    weight numeric,
    medical_conditions text,
    regular_medications text,
    emergency_contact_name text,
    emergency_contact_phone text,
    bank_account_number text,
    bank_name text,
    bank_branch text,
    bank_ifsc text,
    bank_account_holder text,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL,
    created_by uuid,
    updated_by uuid,
    student_aadhar text,
    CONSTRAINT religion_check CHECK ((religion = ANY (ARRAY['Hindu'::text, 'Muslim'::text, 'Sikh'::text, 'Christian'::text, 'Jain'::text, 'Buddhist'::text, 'Other'::text])))
);


--
-- Name: organization_student_additional_fees; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.organization_student_additional_fees (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    organization_id uuid NOT NULL,
    active_session_id uuid NOT NULL,
    student_id uuid NOT NULL,
    global_fee_head_id uuid NOT NULL,
    amount numeric DEFAULT 0 NOT NULL,
    is_active boolean DEFAULT true NOT NULL,
    is_deleted boolean DEFAULT false NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL,
    created_by uuid,
    updated_by uuid
);


--
-- Name: organization_student_attendance; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.organization_student_attendance (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    organization_id uuid NOT NULL,
    active_session_id uuid NOT NULL,
    student_id uuid NOT NULL,
    attendance_date date DEFAULT CURRENT_DATE NOT NULL,
    status text NOT NULL,
    remarks text,
    marked_by_staff_id uuid,
    is_active boolean DEFAULT true NOT NULL,
    is_deleted boolean DEFAULT false NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL,
    created_by uuid,
    updated_by uuid,
    CONSTRAINT organization_student_attendance_status_check CHECK ((status = ANY (ARRAY['Present'::text, 'Absent'::text, 'Late'::text, 'Half Day'::text, 'On Leave'::text])))
);


--
-- Name: organization_student_bus_assignments; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.organization_student_bus_assignments (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    organization_id uuid NOT NULL,
    active_session_id uuid NOT NULL,
    student_id uuid NOT NULL,
    bus_id uuid NOT NULL,
    pickup_stop text,
    is_active boolean DEFAULT true NOT NULL,
    is_deleted boolean DEFAULT false NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL,
    created_by uuid,
    updated_by uuid
);


--
-- Name: organization_student_enrollments; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.organization_student_enrollments (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    organization_id uuid NOT NULL,
    active_session_id uuid NOT NULL,
    student_id uuid NOT NULL,
    class_id uuid NOT NULL,
    section_id uuid NOT NULL,
    result_status_id uuid,
    roll_number integer,
    is_active boolean DEFAULT true NOT NULL,
    is_deleted boolean DEFAULT false NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL,
    created_by uuid,
    updated_by uuid
);


--
-- Name: organization_student_exam_marks; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.organization_student_exam_marks (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    organization_id uuid NOT NULL,
    active_session_id uuid NOT NULL,
    exam_id uuid NOT NULL,
    class_id uuid NOT NULL,
    subject_id uuid NOT NULL,
    student_id uuid NOT NULL,
    obtained_marks numeric,
    is_absent boolean DEFAULT false NOT NULL,
    is_medical_leave boolean DEFAULT false NOT NULL,
    teacher_remarks text,
    is_active boolean DEFAULT true NOT NULL,
    is_deleted boolean DEFAULT false NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL,
    created_by uuid
);


--
-- Name: organization_student_fee_payments; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.organization_student_fee_payments (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    organization_id uuid NOT NULL,
    active_session_id uuid NOT NULL,
    student_id uuid NOT NULL,
    receipt_number text NOT NULL,
    payment_mode text NOT NULL,
    payment_date date DEFAULT CURRENT_DATE NOT NULL,
    amount_paid numeric DEFAULT 0 NOT NULL,
    discount_amount numeric DEFAULT 0 NOT NULL,
    fine_amount numeric DEFAULT 0 NOT NULL,
    discount_reason text,
    cash_received_by_user_id uuid,
    cheque_number text,
    cheque_date date,
    cheque_bank_name text,
    online_transaction_id text,
    online_payment_app text,
    remarks text,
    status text DEFAULT 'Completed'::text NOT NULL,
    is_active boolean DEFAULT true NOT NULL,
    is_deleted boolean DEFAULT false NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL,
    created_by uuid,
    updated_by uuid,
    CONSTRAINT organization_student_fee_payments_payment_mode_check CHECK ((payment_mode = ANY (ARRAY['Cash'::text, 'Cheque'::text, 'UPI'::text, 'Online'::text]))),
    CONSTRAINT organization_student_fee_payments_status_check CHECK ((status = ANY (ARRAY['Completed'::text, 'Cancelled'::text])))
);


--
-- Name: organization_student_homeworks; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.organization_student_homeworks (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    organization_id uuid NOT NULL,
    active_session_id uuid NOT NULL,
    class_id uuid NOT NULL,
    section_id uuid NOT NULL,
    subject_id uuid NOT NULL,
    title text NOT NULL,
    description text,
    attachment_url text,
    submission_date date,
    is_active boolean DEFAULT true NOT NULL,
    is_deleted boolean DEFAULT false NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL,
    created_by uuid,
    updated_by uuid
);


--
-- Name: organization_student_image_vectors; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.organization_student_image_vectors (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    organization_id uuid NOT NULL,
    student_id uuid NOT NULL,
    person_type text NOT NULL,
    face_vector public.vector(512) NOT NULL,
    image_url text NOT NULL,
    is_active boolean DEFAULT true NOT NULL,
    is_deleted boolean DEFAULT false NOT NULL,
    deleted_at timestamp with time zone,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL,
    created_by uuid,
    updated_by uuid,
    CONSTRAINT check_person_type CHECK ((person_type = ANY (ARRAY['student'::text, 'guardian'::text])))
);


--
-- Name: organization_student_marks; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.organization_student_marks (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    organization_id uuid NOT NULL,
    active_session_id uuid NOT NULL,
    exam_subject_id uuid NOT NULL,
    student_id uuid NOT NULL,
    class_id uuid NOT NULL,
    marks_obtained numeric,
    is_absent boolean DEFAULT false NOT NULL,
    grade text,
    is_passed boolean,
    is_active boolean DEFAULT true NOT NULL,
    is_deleted boolean DEFAULT false NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL,
    created_by uuid,
    updated_by uuid
);


--
-- Name: organization_student_subjects; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.organization_student_subjects (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    organization_id uuid NOT NULL,
    parent_organization_id uuid,
    active_session_id uuid NOT NULL,
    student_id uuid NOT NULL,
    session_subject_id uuid NOT NULL,
    is_optional boolean DEFAULT false NOT NULL,
    is_active boolean DEFAULT true NOT NULL,
    is_deleted boolean DEFAULT false NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL,
    created_by uuid,
    updated_by uuid
);


--
-- Name: organization_student_user_links; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.organization_student_user_links (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    organization_id uuid NOT NULL,
    active_session_id uuid NOT NULL,
    student_id uuid NOT NULL,
    user_id uuid NOT NULL,
    is_approved boolean DEFAULT false NOT NULL,
    approved_by uuid,
    approved_at timestamp with time zone,
    is_active boolean DEFAULT true NOT NULL,
    is_deleted boolean DEFAULT false NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL
);


--
-- Name: organization_students; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.organization_students (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    organization_id uuid NOT NULL,
    active_session_id uuid NOT NULL,
    guardian_id uuid NOT NULL,
    category_id uuid,
    blood_group_id uuid,
    student_status_id uuid,
    name text NOT NULL,
    gender text,
    sr_number text NOT NULL,
    admission_date date NOT NULL,
    date_of_birth date NOT NULL,
    enrollment_number text,
    address_area_id uuid,
    address_details text,
    image_url text,
    is_active boolean DEFAULT true NOT NULL,
    is_deleted boolean DEFAULT false NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL,
    created_by uuid,
    updated_by uuid,
    guardian_image_url text,
    CONSTRAINT organization_students_gender_check CHECK ((gender = ANY (ARRAY['Male'::text, 'Female'::text, 'Other'::text])))
);


--
-- Name: organization_timetable_schedules; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.organization_timetable_schedules (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    parent_organization_id uuid NOT NULL,
    organization_id uuid NOT NULL,
    active_session_id uuid NOT NULL,
    class_id uuid NOT NULL,
    section_id uuid NOT NULL,
    period_id uuid NOT NULL,
    subject_id uuid NOT NULL,
    teacher_id uuid NOT NULL,
    days_of_week text[] NOT NULL,
    start_date date,
    end_date date,
    is_active boolean DEFAULT true NOT NULL,
    is_deleted boolean DEFAULT false NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL,
    created_by uuid,
    updated_by uuid
);


--
-- Name: organization_users; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.organization_users (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    organization_id uuid NOT NULL,
    user_id uuid NOT NULL,
    role_id uuid,
    is_active boolean DEFAULT true NOT NULL,
    is_deleted boolean DEFAULT false NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL
);


--
-- Name: organization_website_announcements; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.organization_website_announcements (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    website_id uuid NOT NULL,
    title text NOT NULL,
    content text NOT NULL,
    is_pinned boolean DEFAULT false NOT NULL,
    is_active boolean DEFAULT true NOT NULL,
    is_deleted boolean DEFAULT false NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL,
    created_by uuid,
    updated_by uuid
);


--
-- Name: organization_website_galleries; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.organization_website_galleries (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    website_id uuid NOT NULL,
    image_url text NOT NULL,
    title text,
    description text,
    display_order integer DEFAULT 0 NOT NULL,
    is_active boolean DEFAULT true NOT NULL,
    is_deleted boolean DEFAULT false NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL,
    created_by uuid,
    updated_by uuid
);


--
-- Name: organizations; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.organizations (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    name text NOT NULL,
    type_id uuid,
    is_active boolean DEFAULT true NOT NULL,
    is_deleted boolean DEFAULT false NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL,
    parent_organization_id uuid,
    is_own boolean DEFAULT true NOT NULL
);


--
-- Name: parent_organization_website_facilities; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.parent_organization_website_facilities (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    website_id uuid NOT NULL,
    facility_id uuid NOT NULL,
    custom_description text,
    is_active boolean DEFAULT true NOT NULL,
    is_deleted boolean DEFAULT false NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL,
    created_by uuid,
    updated_by uuid
);


--
-- Name: parent_organization_websites; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.parent_organization_websites (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    parent_organization_id uuid NOT NULL,
    subdomain text NOT NULL,
    custom_domain text,
    theme_color_primary text DEFAULT '#059669'::text NOT NULL,
    theme_color_secondary text DEFAULT '#10b981'::text NOT NULL,
    hero_title text NOT NULL,
    hero_subtitle text,
    hero_image_url text,
    about_us text,
    contact_email text,
    contact_phone text,
    contact_address text,
    social_links jsonb DEFAULT '{}'::jsonb NOT NULL,
    is_published boolean DEFAULT false NOT NULL,
    is_active boolean DEFAULT true NOT NULL,
    is_deleted boolean DEFAULT false NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL,
    created_by uuid,
    updated_by uuid
);


--
-- Name: reports; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.reports (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    message_id uuid NOT NULL,
    reporter_user_id uuid NOT NULL,
    reason text NOT NULL,
    status text DEFAULT 'pending'::text NOT NULL,
    is_deleted boolean DEFAULT false NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL
);


--
-- Name: rooms; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.rooms (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    name text NOT NULL,
    category text,
    description text,
    message_cooldown_seconds integer DEFAULT 5 NOT NULL,
    is_active boolean DEFAULT true NOT NULL,
    is_deleted boolean DEFAULT false NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL
);


--
-- Name: student_document_audit_logs; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.student_document_audit_logs (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    registry_id uuid,
    action_type text NOT NULL,
    performed_by_user_id uuid,
    old_state jsonb,
    new_state jsonb,
    remarks text,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    CONSTRAINT action_type_check CHECK ((action_type = ANY (ARRAY['Upload'::text, 'Status Change'::text, 'Location Update'::text, 'Handover Initiated'::text, 'Handover Confirmed'::text, 'Soft Delete'::text])))
);


--
-- Name: student_document_physical_locations; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.student_document_physical_locations (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    registry_id uuid NOT NULL,
    almirah_id text NOT NULL,
    rack_id text NOT NULL,
    file_folder_id text NOT NULL,
    received_by_staff_id uuid,
    received_at timestamp with time zone DEFAULT now() NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL,
    created_by uuid,
    updated_by uuid
);


--
-- Name: student_document_registry; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.student_document_registry (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    organization_id uuid NOT NULL,
    parent_organization_id uuid NOT NULL,
    active_session_id uuid NOT NULL,
    student_id uuid NOT NULL,
    document_type_id uuid NOT NULL,
    status text DEFAULT 'Pending'::text NOT NULL,
    copy_type text DEFAULT 'Photocopy'::text NOT NULL,
    file_url text,
    rejection_remarks text,
    is_active boolean DEFAULT true NOT NULL,
    is_deleted boolean DEFAULT false NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL,
    created_by uuid,
    updated_by uuid,
    CONSTRAINT copy_type_check CHECK ((copy_type = ANY (ARRAY['Original'::text, 'Photocopy'::text, 'Soft Copy Only'::text]))),
    CONSTRAINT status_check CHECK ((status = ANY (ARRAY['Pending'::text, 'Submitted'::text, 'Under Review'::text, 'Verified'::text, 'Rejected'::text, 'Returned'::text])))
);


--
-- Name: student_document_return_logs; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.student_document_return_logs (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    registry_id uuid NOT NULL,
    returned_by_staff_id uuid,
    returned_at timestamp with time zone DEFAULT now() NOT NULL,
    handover_proof_image_url text,
    approval_status text DEFAULT 'Pending Approval'::text NOT NULL,
    approved_by_user_id uuid,
    approved_at timestamp with time zone,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    CONSTRAINT return_approval_status_check CHECK ((approval_status = ANY (ARRAY['Pending Approval'::text, 'Received'::text])))
);


--
-- Name: user_documents; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.user_documents (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    user_id uuid NOT NULL,
    document_type_id uuid NOT NULL,
    custom_name text,
    file_url text NOT NULL,
    is_active boolean DEFAULT true NOT NULL,
    is_deleted boolean DEFAULT false NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL
);


--
-- Name: user_inspirations; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.user_inspirations (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    inspired_user_id uuid NOT NULL,
    inspiring_user_id uuid NOT NULL,
    is_deleted boolean DEFAULT false NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL
);


--
-- Name: user_journey_mcq_progress; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.user_journey_mcq_progress (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    user_journey_id uuid NOT NULL,
    day_number integer NOT NULL,
    mcq_id uuid NOT NULL,
    selected_option character(1) NOT NULL,
    is_correct boolean NOT NULL,
    score_earned integer DEFAULT 0 NOT NULL,
    answered_at timestamp with time zone DEFAULT now() NOT NULL,
    is_synced boolean DEFAULT false NOT NULL,
    is_deleted boolean DEFAULT false NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL,
    is_active boolean DEFAULT true NOT NULL
);


--
-- Name: user_journey_task_progress; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.user_journey_task_progress (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    user_journey_id uuid NOT NULL,
    day_number integer NOT NULL,
    task_id uuid NOT NULL,
    is_completed boolean DEFAULT false NOT NULL,
    completed_at timestamp with time zone,
    is_synced boolean DEFAULT false NOT NULL,
    is_deleted boolean DEFAULT false NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL,
    is_active boolean DEFAULT true NOT NULL
);


--
-- Name: user_journeys; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.user_journeys (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    user_id uuid NOT NULL,
    global_template_id uuid,
    organization_template_id uuid,
    organization_parent_template_id uuid,
    start_date date DEFAULT CURRENT_DATE NOT NULL,
    current_day integer DEFAULT 1 NOT NULL,
    notification_time time without time zone,
    status text DEFAULT 'active'::text NOT NULL,
    total_score integer DEFAULT 0 NOT NULL,
    current_streak integer DEFAULT 0 NOT NULL,
    best_streak integer DEFAULT 0 NOT NULL,
    is_active boolean DEFAULT true NOT NULL,
    is_deleted boolean DEFAULT false NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL
);


--
-- Name: user_profiles; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.user_profiles (
    user_id uuid NOT NULL,
    username text,
    first_name text,
    last_name text,
    full_name text,
    profile_picture_url text,
    cover_photo_url text,
    gender public.gender_type,
    date_of_birth date,
    bio text,
    preferred_language character varying(10),
    is_private boolean DEFAULT false NOT NULL,
    is_verified boolean DEFAULT false NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL,
    is_active boolean DEFAULT true NOT NULL,
    is_deleted boolean DEFAULT false NOT NULL
);


--
-- Name: user_sessions; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.user_sessions (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    user_id uuid NOT NULL,
    session_id uuid DEFAULT gen_random_uuid() NOT NULL,
    device_id text NOT NULL,
    device_name text,
    fcm_token text,
    refresh_token_hash text,
    is_active boolean DEFAULT true NOT NULL,
    last_seen_at timestamp with time zone,
    expires_at timestamp with time zone,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    platform text NOT NULL
);


--
-- Name: users; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.users (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    auth_id uuid NOT NULL,
    email text NOT NULL,
    mobile_number text,
    is_active boolean DEFAULT true NOT NULL,
    is_deleted boolean DEFAULT false NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL
);


--
-- Name: messages; Type: TABLE; Schema: realtime; Owner: -
--

CREATE TABLE realtime.messages (
    topic text NOT NULL,
    extension text NOT NULL,
    payload jsonb,
    event text,
    private boolean DEFAULT false,
    updated_at timestamp without time zone DEFAULT now() NOT NULL,
    inserted_at timestamp without time zone DEFAULT now() NOT NULL,
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    binary_payload bytea
)
PARTITION BY RANGE (inserted_at);


--
-- Name: messages_2026_06_19; Type: TABLE; Schema: realtime; Owner: -
--

CREATE TABLE realtime.messages_2026_06_19 (
    topic text NOT NULL,
    extension text NOT NULL,
    payload jsonb,
    event text,
    private boolean DEFAULT false,
    updated_at timestamp without time zone DEFAULT now() NOT NULL,
    inserted_at timestamp without time zone DEFAULT now() NOT NULL,
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    binary_payload bytea,
    CONSTRAINT messages_payload_exclusive CHECK (((payload IS NULL) OR (binary_payload IS NULL)))
);


--
-- Name: messages_2026_06_20; Type: TABLE; Schema: realtime; Owner: -
--

CREATE TABLE realtime.messages_2026_06_20 (
    topic text NOT NULL,
    extension text NOT NULL,
    payload jsonb,
    event text,
    private boolean DEFAULT false,
    updated_at timestamp without time zone DEFAULT now() NOT NULL,
    inserted_at timestamp without time zone DEFAULT now() NOT NULL,
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    binary_payload bytea,
    CONSTRAINT messages_payload_exclusive CHECK (((payload IS NULL) OR (binary_payload IS NULL)))
);


--
-- Name: messages_2026_06_21; Type: TABLE; Schema: realtime; Owner: -
--

CREATE TABLE realtime.messages_2026_06_21 (
    topic text NOT NULL,
    extension text NOT NULL,
    payload jsonb,
    event text,
    private boolean DEFAULT false,
    updated_at timestamp without time zone DEFAULT now() NOT NULL,
    inserted_at timestamp without time zone DEFAULT now() NOT NULL,
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    binary_payload bytea,
    CONSTRAINT messages_payload_exclusive CHECK (((payload IS NULL) OR (binary_payload IS NULL)))
);


--
-- Name: messages_2026_06_22; Type: TABLE; Schema: realtime; Owner: -
--

CREATE TABLE realtime.messages_2026_06_22 (
    topic text NOT NULL,
    extension text NOT NULL,
    payload jsonb,
    event text,
    private boolean DEFAULT false,
    updated_at timestamp without time zone DEFAULT now() NOT NULL,
    inserted_at timestamp without time zone DEFAULT now() NOT NULL,
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    binary_payload bytea,
    CONSTRAINT messages_payload_exclusive CHECK (((payload IS NULL) OR (binary_payload IS NULL)))
);


--
-- Name: messages_2026_06_23; Type: TABLE; Schema: realtime; Owner: -
--

CREATE TABLE realtime.messages_2026_06_23 (
    topic text NOT NULL,
    extension text NOT NULL,
    payload jsonb,
    event text,
    private boolean DEFAULT false,
    updated_at timestamp without time zone DEFAULT now() NOT NULL,
    inserted_at timestamp without time zone DEFAULT now() NOT NULL,
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    binary_payload bytea,
    CONSTRAINT messages_payload_exclusive CHECK (((payload IS NULL) OR (binary_payload IS NULL)))
);


--
-- Name: messages_2026_06_24; Type: TABLE; Schema: realtime; Owner: -
--

CREATE TABLE realtime.messages_2026_06_24 (
    topic text NOT NULL,
    extension text NOT NULL,
    payload jsonb,
    event text,
    private boolean DEFAULT false,
    updated_at timestamp without time zone DEFAULT now() NOT NULL,
    inserted_at timestamp without time zone DEFAULT now() NOT NULL,
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    binary_payload bytea,
    CONSTRAINT messages_payload_exclusive CHECK (((payload IS NULL) OR (binary_payload IS NULL)))
);


--
-- Name: messages_2026_06_25; Type: TABLE; Schema: realtime; Owner: -
--

CREATE TABLE realtime.messages_2026_06_25 (
    topic text NOT NULL,
    extension text NOT NULL,
    payload jsonb,
    event text,
    private boolean DEFAULT false,
    updated_at timestamp without time zone DEFAULT now() NOT NULL,
    inserted_at timestamp without time zone DEFAULT now() NOT NULL,
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    binary_payload bytea,
    CONSTRAINT messages_payload_exclusive CHECK (((payload IS NULL) OR (binary_payload IS NULL)))
);


--
-- Name: schema_migrations; Type: TABLE; Schema: realtime; Owner: -
--

CREATE TABLE realtime.schema_migrations (
    version bigint NOT NULL,
    inserted_at timestamp(0) without time zone
);


--
-- Name: subscription; Type: TABLE; Schema: realtime; Owner: -
--

CREATE TABLE realtime.subscription (
    id bigint NOT NULL,
    subscription_id uuid NOT NULL,
    entity regclass NOT NULL,
    filters realtime.user_defined_filter[] DEFAULT '{}'::realtime.user_defined_filter[] NOT NULL,
    claims jsonb NOT NULL,
    claims_role regrole GENERATED ALWAYS AS (realtime.to_regrole((claims ->> 'role'::text))) STORED NOT NULL,
    created_at timestamp without time zone DEFAULT timezone('utc'::text, now()) NOT NULL,
    action_filter text DEFAULT '*'::text,
    selected_columns text[],
    CONSTRAINT subscription_action_filter_check CHECK ((action_filter = ANY (ARRAY['*'::text, 'INSERT'::text, 'UPDATE'::text, 'DELETE'::text])))
);


--
-- Name: subscription_id_seq; Type: SEQUENCE; Schema: realtime; Owner: -
--

ALTER TABLE realtime.subscription ALTER COLUMN id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME realtime.subscription_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: buckets; Type: TABLE; Schema: storage; Owner: -
--

CREATE TABLE storage.buckets (
    id text NOT NULL,
    name text NOT NULL,
    owner uuid,
    created_at timestamp with time zone DEFAULT now(),
    updated_at timestamp with time zone DEFAULT now(),
    public boolean DEFAULT false,
    avif_autodetection boolean DEFAULT false,
    file_size_limit bigint,
    allowed_mime_types text[],
    owner_id text,
    type storage.buckettype DEFAULT 'STANDARD'::storage.buckettype NOT NULL
);


--
-- Name: COLUMN buckets.owner; Type: COMMENT; Schema: storage; Owner: -
--

COMMENT ON COLUMN storage.buckets.owner IS 'Field is deprecated, use owner_id instead';


--
-- Name: buckets_analytics; Type: TABLE; Schema: storage; Owner: -
--

CREATE TABLE storage.buckets_analytics (
    name text NOT NULL,
    type storage.buckettype DEFAULT 'ANALYTICS'::storage.buckettype NOT NULL,
    format text DEFAULT 'ICEBERG'::text NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL,
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    deleted_at timestamp with time zone
);


--
-- Name: buckets_vectors; Type: TABLE; Schema: storage; Owner: -
--

CREATE TABLE storage.buckets_vectors (
    id text NOT NULL,
    type storage.buckettype DEFAULT 'VECTOR'::storage.buckettype NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL
);


--
-- Name: migrations; Type: TABLE; Schema: storage; Owner: -
--

CREATE TABLE storage.migrations (
    id integer NOT NULL,
    name character varying(100) NOT NULL,
    hash character varying(40) NOT NULL,
    executed_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP
);


--
-- Name: objects; Type: TABLE; Schema: storage; Owner: -
--

CREATE TABLE storage.objects (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    bucket_id text,
    name text,
    owner uuid,
    created_at timestamp with time zone DEFAULT now(),
    updated_at timestamp with time zone DEFAULT now(),
    last_accessed_at timestamp with time zone DEFAULT now(),
    metadata jsonb,
    path_tokens text[] GENERATED ALWAYS AS (string_to_array(name, '/'::text)) STORED,
    version text,
    owner_id text,
    user_metadata jsonb
);


--
-- Name: COLUMN objects.owner; Type: COMMENT; Schema: storage; Owner: -
--

COMMENT ON COLUMN storage.objects.owner IS 'Field is deprecated, use owner_id instead';


--
-- Name: s3_multipart_uploads; Type: TABLE; Schema: storage; Owner: -
--

CREATE TABLE storage.s3_multipart_uploads (
    id text NOT NULL,
    in_progress_size bigint DEFAULT 0 NOT NULL,
    upload_signature text NOT NULL,
    bucket_id text NOT NULL,
    key text NOT NULL COLLATE pg_catalog."C",
    version text NOT NULL,
    owner_id text,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    user_metadata jsonb,
    metadata jsonb
);


--
-- Name: s3_multipart_uploads_parts; Type: TABLE; Schema: storage; Owner: -
--

CREATE TABLE storage.s3_multipart_uploads_parts (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    upload_id text NOT NULL,
    size bigint DEFAULT 0 NOT NULL,
    part_number integer NOT NULL,
    bucket_id text NOT NULL,
    key text NOT NULL COLLATE pg_catalog."C",
    etag text NOT NULL,
    owner_id text,
    version text NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL
);


--
-- Name: vector_indexes; Type: TABLE; Schema: storage; Owner: -
--

CREATE TABLE storage.vector_indexes (
    id text DEFAULT gen_random_uuid() NOT NULL,
    name text NOT NULL COLLATE pg_catalog."C",
    bucket_id text NOT NULL,
    data_type text NOT NULL,
    dimension integer NOT NULL,
    distance_metric text NOT NULL,
    metadata_configuration jsonb,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL
);


--
-- Name: messages_2026_06_19; Type: TABLE ATTACH; Schema: realtime; Owner: -
--

ALTER TABLE ONLY realtime.messages ATTACH PARTITION realtime.messages_2026_06_19 FOR VALUES FROM ('2026-06-19 00:00:00') TO ('2026-06-20 00:00:00');


--
-- Name: messages_2026_06_20; Type: TABLE ATTACH; Schema: realtime; Owner: -
--

ALTER TABLE ONLY realtime.messages ATTACH PARTITION realtime.messages_2026_06_20 FOR VALUES FROM ('2026-06-20 00:00:00') TO ('2026-06-21 00:00:00');


--
-- Name: messages_2026_06_21; Type: TABLE ATTACH; Schema: realtime; Owner: -
--

ALTER TABLE ONLY realtime.messages ATTACH PARTITION realtime.messages_2026_06_21 FOR VALUES FROM ('2026-06-21 00:00:00') TO ('2026-06-22 00:00:00');


--
-- Name: messages_2026_06_22; Type: TABLE ATTACH; Schema: realtime; Owner: -
--

ALTER TABLE ONLY realtime.messages ATTACH PARTITION realtime.messages_2026_06_22 FOR VALUES FROM ('2026-06-22 00:00:00') TO ('2026-06-23 00:00:00');


--
-- Name: messages_2026_06_23; Type: TABLE ATTACH; Schema: realtime; Owner: -
--

ALTER TABLE ONLY realtime.messages ATTACH PARTITION realtime.messages_2026_06_23 FOR VALUES FROM ('2026-06-23 00:00:00') TO ('2026-06-24 00:00:00');


--
-- Name: messages_2026_06_24; Type: TABLE ATTACH; Schema: realtime; Owner: -
--

ALTER TABLE ONLY realtime.messages ATTACH PARTITION realtime.messages_2026_06_24 FOR VALUES FROM ('2026-06-24 00:00:00') TO ('2026-06-25 00:00:00');


--
-- Name: messages_2026_06_25; Type: TABLE ATTACH; Schema: realtime; Owner: -
--

ALTER TABLE ONLY realtime.messages ATTACH PARTITION realtime.messages_2026_06_25 FOR VALUES FROM ('2026-06-25 00:00:00') TO ('2026-06-26 00:00:00');


--
-- Name: refresh_tokens id; Type: DEFAULT; Schema: auth; Owner: -
--

ALTER TABLE ONLY auth.refresh_tokens ALTER COLUMN id SET DEFAULT nextval('auth.refresh_tokens_id_seq'::regclass);


--
-- Name: mfa_amr_claims amr_id_pk; Type: CONSTRAINT; Schema: auth; Owner: -
--

ALTER TABLE ONLY auth.mfa_amr_claims
    ADD CONSTRAINT amr_id_pk PRIMARY KEY (id);


--
-- Name: audit_log_entries audit_log_entries_pkey; Type: CONSTRAINT; Schema: auth; Owner: -
--

ALTER TABLE ONLY auth.audit_log_entries
    ADD CONSTRAINT audit_log_entries_pkey PRIMARY KEY (id);


--
-- Name: custom_oauth_providers custom_oauth_providers_identifier_key; Type: CONSTRAINT; Schema: auth; Owner: -
--

ALTER TABLE ONLY auth.custom_oauth_providers
    ADD CONSTRAINT custom_oauth_providers_identifier_key UNIQUE (identifier);


--
-- Name: custom_oauth_providers custom_oauth_providers_pkey; Type: CONSTRAINT; Schema: auth; Owner: -
--

ALTER TABLE ONLY auth.custom_oauth_providers
    ADD CONSTRAINT custom_oauth_providers_pkey PRIMARY KEY (id);


--
-- Name: flow_state flow_state_pkey; Type: CONSTRAINT; Schema: auth; Owner: -
--

ALTER TABLE ONLY auth.flow_state
    ADD CONSTRAINT flow_state_pkey PRIMARY KEY (id);


--
-- Name: identities identities_pkey; Type: CONSTRAINT; Schema: auth; Owner: -
--

ALTER TABLE ONLY auth.identities
    ADD CONSTRAINT identities_pkey PRIMARY KEY (id);


--
-- Name: identities identities_provider_id_provider_unique; Type: CONSTRAINT; Schema: auth; Owner: -
--

ALTER TABLE ONLY auth.identities
    ADD CONSTRAINT identities_provider_id_provider_unique UNIQUE (provider_id, provider);


--
-- Name: instances instances_pkey; Type: CONSTRAINT; Schema: auth; Owner: -
--

ALTER TABLE ONLY auth.instances
    ADD CONSTRAINT instances_pkey PRIMARY KEY (id);


--
-- Name: mfa_amr_claims mfa_amr_claims_session_id_authentication_method_pkey; Type: CONSTRAINT; Schema: auth; Owner: -
--

ALTER TABLE ONLY auth.mfa_amr_claims
    ADD CONSTRAINT mfa_amr_claims_session_id_authentication_method_pkey UNIQUE (session_id, authentication_method);


--
-- Name: mfa_challenges mfa_challenges_pkey; Type: CONSTRAINT; Schema: auth; Owner: -
--

ALTER TABLE ONLY auth.mfa_challenges
    ADD CONSTRAINT mfa_challenges_pkey PRIMARY KEY (id);


--
-- Name: mfa_factors mfa_factors_last_challenged_at_key; Type: CONSTRAINT; Schema: auth; Owner: -
--

ALTER TABLE ONLY auth.mfa_factors
    ADD CONSTRAINT mfa_factors_last_challenged_at_key UNIQUE (last_challenged_at);


--
-- Name: mfa_factors mfa_factors_pkey; Type: CONSTRAINT; Schema: auth; Owner: -
--

ALTER TABLE ONLY auth.mfa_factors
    ADD CONSTRAINT mfa_factors_pkey PRIMARY KEY (id);


--
-- Name: oauth_authorizations oauth_authorizations_authorization_code_key; Type: CONSTRAINT; Schema: auth; Owner: -
--

ALTER TABLE ONLY auth.oauth_authorizations
    ADD CONSTRAINT oauth_authorizations_authorization_code_key UNIQUE (authorization_code);


--
-- Name: oauth_authorizations oauth_authorizations_authorization_id_key; Type: CONSTRAINT; Schema: auth; Owner: -
--

ALTER TABLE ONLY auth.oauth_authorizations
    ADD CONSTRAINT oauth_authorizations_authorization_id_key UNIQUE (authorization_id);


--
-- Name: oauth_authorizations oauth_authorizations_pkey; Type: CONSTRAINT; Schema: auth; Owner: -
--

ALTER TABLE ONLY auth.oauth_authorizations
    ADD CONSTRAINT oauth_authorizations_pkey PRIMARY KEY (id);


--
-- Name: oauth_client_states oauth_client_states_pkey; Type: CONSTRAINT; Schema: auth; Owner: -
--

ALTER TABLE ONLY auth.oauth_client_states
    ADD CONSTRAINT oauth_client_states_pkey PRIMARY KEY (id);


--
-- Name: oauth_clients oauth_clients_pkey; Type: CONSTRAINT; Schema: auth; Owner: -
--

ALTER TABLE ONLY auth.oauth_clients
    ADD CONSTRAINT oauth_clients_pkey PRIMARY KEY (id);


--
-- Name: oauth_consents oauth_consents_pkey; Type: CONSTRAINT; Schema: auth; Owner: -
--

ALTER TABLE ONLY auth.oauth_consents
    ADD CONSTRAINT oauth_consents_pkey PRIMARY KEY (id);


--
-- Name: oauth_consents oauth_consents_user_client_unique; Type: CONSTRAINT; Schema: auth; Owner: -
--

ALTER TABLE ONLY auth.oauth_consents
    ADD CONSTRAINT oauth_consents_user_client_unique UNIQUE (user_id, client_id);


--
-- Name: one_time_tokens one_time_tokens_pkey; Type: CONSTRAINT; Schema: auth; Owner: -
--

ALTER TABLE ONLY auth.one_time_tokens
    ADD CONSTRAINT one_time_tokens_pkey PRIMARY KEY (id);


--
-- Name: refresh_tokens refresh_tokens_pkey; Type: CONSTRAINT; Schema: auth; Owner: -
--

ALTER TABLE ONLY auth.refresh_tokens
    ADD CONSTRAINT refresh_tokens_pkey PRIMARY KEY (id);


--
-- Name: refresh_tokens refresh_tokens_token_unique; Type: CONSTRAINT; Schema: auth; Owner: -
--

ALTER TABLE ONLY auth.refresh_tokens
    ADD CONSTRAINT refresh_tokens_token_unique UNIQUE (token);


--
-- Name: saml_providers saml_providers_entity_id_key; Type: CONSTRAINT; Schema: auth; Owner: -
--

ALTER TABLE ONLY auth.saml_providers
    ADD CONSTRAINT saml_providers_entity_id_key UNIQUE (entity_id);


--
-- Name: saml_providers saml_providers_pkey; Type: CONSTRAINT; Schema: auth; Owner: -
--

ALTER TABLE ONLY auth.saml_providers
    ADD CONSTRAINT saml_providers_pkey PRIMARY KEY (id);


--
-- Name: saml_relay_states saml_relay_states_pkey; Type: CONSTRAINT; Schema: auth; Owner: -
--

ALTER TABLE ONLY auth.saml_relay_states
    ADD CONSTRAINT saml_relay_states_pkey PRIMARY KEY (id);


--
-- Name: schema_migrations schema_migrations_pkey; Type: CONSTRAINT; Schema: auth; Owner: -
--

ALTER TABLE ONLY auth.schema_migrations
    ADD CONSTRAINT schema_migrations_pkey PRIMARY KEY (version);


--
-- Name: sessions sessions_pkey; Type: CONSTRAINT; Schema: auth; Owner: -
--

ALTER TABLE ONLY auth.sessions
    ADD CONSTRAINT sessions_pkey PRIMARY KEY (id);


--
-- Name: sso_domains sso_domains_pkey; Type: CONSTRAINT; Schema: auth; Owner: -
--

ALTER TABLE ONLY auth.sso_domains
    ADD CONSTRAINT sso_domains_pkey PRIMARY KEY (id);


--
-- Name: sso_providers sso_providers_pkey; Type: CONSTRAINT; Schema: auth; Owner: -
--

ALTER TABLE ONLY auth.sso_providers
    ADD CONSTRAINT sso_providers_pkey PRIMARY KEY (id);


--
-- Name: users users_phone_key; Type: CONSTRAINT; Schema: auth; Owner: -
--

ALTER TABLE ONLY auth.users
    ADD CONSTRAINT users_phone_key UNIQUE (phone);


--
-- Name: users users_pkey; Type: CONSTRAINT; Schema: auth; Owner: -
--

ALTER TABLE ONLY auth.users
    ADD CONSTRAINT users_pkey PRIMARY KEY (id);


--
-- Name: webauthn_challenges webauthn_challenges_pkey; Type: CONSTRAINT; Schema: auth; Owner: -
--

ALTER TABLE ONLY auth.webauthn_challenges
    ADD CONSTRAINT webauthn_challenges_pkey PRIMARY KEY (id);


--
-- Name: webauthn_credentials webauthn_credentials_pkey; Type: CONSTRAINT; Schema: auth; Owner: -
--

ALTER TABLE ONLY auth.webauthn_credentials
    ADD CONSTRAINT webauthn_credentials_pkey PRIMARY KEY (id);


--
-- Name: app_versions app_versions_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.app_versions
    ADD CONSTRAINT app_versions_pkey PRIMARY KEY (id);


--
-- Name: organization_bus_child_assignments bus_org_assignment_unique; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_bus_child_assignments
    ADD CONSTRAINT bus_org_assignment_unique UNIQUE (bus_id, organization_id);


--
-- Name: organization_parent_bus_staff_assignments bus_staff_unique; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_parent_bus_staff_assignments
    ADD CONSTRAINT bus_staff_unique UNIQUE (bus_id, staff_id);


--
-- Name: case_studies case_studies_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.case_studies
    ADD CONSTRAINT case_studies_pkey PRIMARY KEY (id);


--
-- Name: case_studies case_studies_slug_unique; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.case_studies
    ADD CONSTRAINT case_studies_slug_unique UNIQUE (slug);


--
-- Name: case_study_bookmarks case_study_bookmarks_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.case_study_bookmarks
    ADD CONSTRAINT case_study_bookmarks_pkey PRIMARY KEY (id);


--
-- Name: case_study_reactions case_study_reactions_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.case_study_reactions
    ADD CONSTRAINT case_study_reactions_pkey PRIMARY KEY (id);


--
-- Name: content_contributor_verifications ccv_unique_user; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.content_contributor_verifications
    ADD CONSTRAINT ccv_unique_user UNIQUE (user_id);


--
-- Name: content_contributor_verifications content_contributor_verifications_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.content_contributor_verifications
    ADD CONSTRAINT content_contributor_verifications_pkey PRIMARY KEY (id);


--
-- Name: case_study_bookmarks csb_unique_user_case_study; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.case_study_bookmarks
    ADD CONSTRAINT csb_unique_user_case_study UNIQUE (case_study_id, user_id);


--
-- Name: case_study_reactions csr_unique_user_case_study; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.case_study_reactions
    ADD CONSTRAINT csr_unique_user_case_study UNIQUE (case_study_id, user_id);


--
-- Name: experience_inspirations exp_insp_unique; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.experience_inspirations
    ADD CONSTRAINT exp_insp_unique UNIQUE (experience_id, user_id);


--
-- Name: experience_inspirations experience_inspirations_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.experience_inspirations
    ADD CONSTRAINT experience_inspirations_pkey PRIMARY KEY (id);


--
-- Name: experiences experiences_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.experiences
    ADD CONSTRAINT experiences_pkey PRIMARY KEY (id);


--
-- Name: global_areas global_areas_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.global_areas
    ADD CONSTRAINT global_areas_pkey PRIMARY KEY (id);


--
-- Name: global_attendance_status global_attendance_status_code_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.global_attendance_status
    ADD CONSTRAINT global_attendance_status_code_key UNIQUE (code);


--
-- Name: global_attendance_status global_attendance_status_name_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.global_attendance_status
    ADD CONSTRAINT global_attendance_status_name_key UNIQUE (name);


--
-- Name: global_attendance_status global_attendance_status_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.global_attendance_status
    ADD CONSTRAINT global_attendance_status_pkey PRIMARY KEY (id);


--
-- Name: global_blood_groups global_blood_groups_name_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.global_blood_groups
    ADD CONSTRAINT global_blood_groups_name_key UNIQUE (name);


--
-- Name: global_blood_groups global_blood_groups_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.global_blood_groups
    ADD CONSTRAINT global_blood_groups_pkey PRIMARY KEY (id);


--
-- Name: global_boards global_boards_code_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.global_boards
    ADD CONSTRAINT global_boards_code_key UNIQUE (code);


--
-- Name: global_boards global_boards_name_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.global_boards
    ADD CONSTRAINT global_boards_name_key UNIQUE (name);


--
-- Name: global_boards global_boards_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.global_boards
    ADD CONSTRAINT global_boards_pkey PRIMARY KEY (id);


--
-- Name: global_classes global_classes_code_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.global_classes
    ADD CONSTRAINT global_classes_code_key UNIQUE (code);


--
-- Name: global_classes global_classes_name_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.global_classes
    ADD CONSTRAINT global_classes_name_key UNIQUE (name);


--
-- Name: global_classes global_classes_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.global_classes
    ADD CONSTRAINT global_classes_pkey PRIMARY KEY (id);


--
-- Name: global_countries global_countries_code_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.global_countries
    ADD CONSTRAINT global_countries_code_key UNIQUE (code);


--
-- Name: global_countries global_countries_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.global_countries
    ADD CONSTRAINT global_countries_pkey PRIMARY KEY (id);


--
-- Name: global_districts global_districts_code_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.global_districts
    ADD CONSTRAINT global_districts_code_key UNIQUE (code);


--
-- Name: global_districts global_districts_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.global_districts
    ADD CONSTRAINT global_districts_pkey PRIMARY KEY (id);


--
-- Name: global_document_types global_document_types_code_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.global_document_types
    ADD CONSTRAINT global_document_types_code_key UNIQUE (code);


--
-- Name: global_document_types global_document_types_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.global_document_types
    ADD CONSTRAINT global_document_types_pkey PRIMARY KEY (id);


--
-- Name: global_exam_subject_rules global_exam_subject_rules_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.global_exam_subject_rules
    ADD CONSTRAINT global_exam_subject_rules_pkey PRIMARY KEY (id);


--
-- Name: global_exam_subject_rules global_exam_subject_rules_unique; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.global_exam_subject_rules
    ADD CONSTRAINT global_exam_subject_rules_unique UNIQUE (class_id, subject_id, exam_type_id);


--
-- Name: global_exam_types global_exam_types_code_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.global_exam_types
    ADD CONSTRAINT global_exam_types_code_key UNIQUE (code);


--
-- Name: global_exam_types global_exam_types_name_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.global_exam_types
    ADD CONSTRAINT global_exam_types_name_key UNIQUE (name);


--
-- Name: global_exam_types global_exam_types_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.global_exam_types
    ADD CONSTRAINT global_exam_types_pkey PRIMARY KEY (id);


--
-- Name: global_expense_types global_expense_types_code_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.global_expense_types
    ADD CONSTRAINT global_expense_types_code_key UNIQUE (code);


--
-- Name: global_expense_types global_expense_types_name_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.global_expense_types
    ADD CONSTRAINT global_expense_types_name_key UNIQUE (name);


--
-- Name: global_expense_types global_expense_types_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.global_expense_types
    ADD CONSTRAINT global_expense_types_pkey PRIMARY KEY (id);


--
-- Name: global_facilities global_facilities_code_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.global_facilities
    ADD CONSTRAINT global_facilities_code_key UNIQUE (code);


--
-- Name: global_facilities global_facilities_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.global_facilities
    ADD CONSTRAINT global_facilities_pkey PRIMARY KEY (id);


--
-- Name: global_fee_heads global_fee_heads_code_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.global_fee_heads
    ADD CONSTRAINT global_fee_heads_code_key UNIQUE (code);


--
-- Name: global_fee_heads global_fee_heads_name_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.global_fee_heads
    ADD CONSTRAINT global_fee_heads_name_key UNIQUE (name);


--
-- Name: global_fee_heads global_fee_heads_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.global_fee_heads
    ADD CONSTRAINT global_fee_heads_pkey PRIMARY KEY (id);


--
-- Name: global_journey_mcqs global_journey_mcqs_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.global_journey_mcqs
    ADD CONSTRAINT global_journey_mcqs_pkey PRIMARY KEY (id);


--
-- Name: global_journey_tasks global_journey_tasks_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.global_journey_tasks
    ADD CONSTRAINT global_journey_tasks_pkey PRIMARY KEY (id);


--
-- Name: global_journey_templates global_journey_templates_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.global_journey_templates
    ADD CONSTRAINT global_journey_templates_pkey PRIMARY KEY (id);


--
-- Name: global_languages global_languages_code_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.global_languages
    ADD CONSTRAINT global_languages_code_key UNIQUE (code);


--
-- Name: global_languages global_languages_name_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.global_languages
    ADD CONSTRAINT global_languages_name_key UNIQUE (name);


--
-- Name: global_languages global_languages_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.global_languages
    ADD CONSTRAINT global_languages_pkey PRIMARY KEY (id);


--
-- Name: global_marksheet_templates global_marksheet_templates_code_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.global_marksheet_templates
    ADD CONSTRAINT global_marksheet_templates_code_key UNIQUE (code);


--
-- Name: global_marksheet_templates global_marksheet_templates_name_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.global_marksheet_templates
    ADD CONSTRAINT global_marksheet_templates_name_key UNIQUE (name);


--
-- Name: global_marksheet_templates global_marksheet_templates_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.global_marksheet_templates
    ADD CONSTRAINT global_marksheet_templates_pkey PRIMARY KEY (id);


--
-- Name: global_mediums global_mediums_board_id_name_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.global_mediums
    ADD CONSTRAINT global_mediums_board_id_name_key UNIQUE (board_id, name);


--
-- Name: global_mediums global_mediums_code_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.global_mediums
    ADD CONSTRAINT global_mediums_code_key UNIQUE (code);


--
-- Name: global_mediums global_mediums_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.global_mediums
    ADD CONSTRAINT global_mediums_pkey PRIMARY KEY (id);


--
-- Name: global_organization_parent_types global_organization_parent_types_code_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.global_organization_parent_types
    ADD CONSTRAINT global_organization_parent_types_code_key UNIQUE (code);


--
-- Name: global_organization_parent_types global_organization_parent_types_name_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.global_organization_parent_types
    ADD CONSTRAINT global_organization_parent_types_name_key UNIQUE (name);


--
-- Name: global_organization_parent_types global_organization_parent_types_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.global_organization_parent_types
    ADD CONSTRAINT global_organization_parent_types_pkey PRIMARY KEY (id);


--
-- Name: global_organization_types global_organization_types_code_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.global_organization_types
    ADD CONSTRAINT global_organization_types_code_key UNIQUE (code);


--
-- Name: global_organization_types global_organization_types_name_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.global_organization_types
    ADD CONSTRAINT global_organization_types_name_key UNIQUE (name);


--
-- Name: global_organization_types global_organization_types_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.global_organization_types
    ADD CONSTRAINT global_organization_types_pkey PRIMARY KEY (id);


--
-- Name: global_panchayats global_panchayats_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.global_panchayats
    ADD CONSTRAINT global_panchayats_pkey PRIMARY KEY (id);


--
-- Name: global_relationship_types global_relationship_types_code_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.global_relationship_types
    ADD CONSTRAINT global_relationship_types_code_key UNIQUE (code);


--
-- Name: global_relationship_types global_relationship_types_name_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.global_relationship_types
    ADD CONSTRAINT global_relationship_types_name_key UNIQUE (name);


--
-- Name: global_relationship_types global_relationship_types_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.global_relationship_types
    ADD CONSTRAINT global_relationship_types_pkey PRIMARY KEY (id);


--
-- Name: global_result_status global_result_status_code_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.global_result_status
    ADD CONSTRAINT global_result_status_code_key UNIQUE (code);


--
-- Name: global_result_status global_result_status_name_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.global_result_status
    ADD CONSTRAINT global_result_status_name_key UNIQUE (name);


--
-- Name: global_result_status global_result_status_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.global_result_status
    ADD CONSTRAINT global_result_status_pkey PRIMARY KEY (id);


--
-- Name: global_sessions global_sessions_name_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.global_sessions
    ADD CONSTRAINT global_sessions_name_key UNIQUE (name);


--
-- Name: global_sessions global_sessions_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.global_sessions
    ADD CONSTRAINT global_sessions_pkey PRIMARY KEY (id);


--
-- Name: global_staff_roles global_staff_roles_code_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.global_staff_roles
    ADD CONSTRAINT global_staff_roles_code_key UNIQUE (code);


--
-- Name: global_staff_roles global_staff_roles_name_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.global_staff_roles
    ADD CONSTRAINT global_staff_roles_name_key UNIQUE (name);


--
-- Name: global_staff_roles global_staff_roles_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.global_staff_roles
    ADD CONSTRAINT global_staff_roles_pkey PRIMARY KEY (id);


--
-- Name: global_states global_states_code_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.global_states
    ADD CONSTRAINT global_states_code_key UNIQUE (code);


--
-- Name: global_states global_states_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.global_states
    ADD CONSTRAINT global_states_pkey PRIMARY KEY (id);


--
-- Name: global_student_categories global_student_categories_code_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.global_student_categories
    ADD CONSTRAINT global_student_categories_code_key UNIQUE (code);


--
-- Name: global_student_categories global_student_categories_name_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.global_student_categories
    ADD CONSTRAINT global_student_categories_name_key UNIQUE (name);


--
-- Name: global_student_categories global_student_categories_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.global_student_categories
    ADD CONSTRAINT global_student_categories_pkey PRIMARY KEY (id);


--
-- Name: global_student_status global_student_status_code_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.global_student_status
    ADD CONSTRAINT global_student_status_code_key UNIQUE (code);


--
-- Name: global_student_status global_student_status_name_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.global_student_status
    ADD CONSTRAINT global_student_status_name_key UNIQUE (name);


--
-- Name: global_student_status global_student_status_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.global_student_status
    ADD CONSTRAINT global_student_status_pkey PRIMARY KEY (id);


--
-- Name: global_subjects global_subjects_code_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.global_subjects
    ADD CONSTRAINT global_subjects_code_key UNIQUE (code);


--
-- Name: global_subjects global_subjects_name_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.global_subjects
    ADD CONSTRAINT global_subjects_name_key UNIQUE (name);


--
-- Name: global_subjects global_subjects_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.global_subjects
    ADD CONSTRAINT global_subjects_pkey PRIMARY KEY (id);


--
-- Name: global_tehsils global_tehsils_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.global_tehsils
    ADD CONSTRAINT global_tehsils_pkey PRIMARY KEY (id);


--
-- Name: organization_student_exam_marks marks_unique_student_exam_subject; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_student_exam_marks
    ADD CONSTRAINT marks_unique_student_exam_subject UNIQUE (exam_id, class_id, subject_id, student_id);


--
-- Name: messages messages_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.messages
    ADD CONSTRAINT messages_pkey PRIMARY KEY (id);


--
-- Name: moderation_settings moderation_settings_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.moderation_settings
    ADD CONSTRAINT moderation_settings_pkey PRIMARY KEY (id);


--
-- Name: organization_exam_subject_settings org_exam_class_subject_unique; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_exam_subject_settings
    ADD CONSTRAINT org_exam_class_subject_unique UNIQUE (organization_id, active_session_id, exam_id, class_id, subject_id);


--
-- Name: organization_student_fee_payments org_receipt_unique; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_student_fee_payments
    ADD CONSTRAINT org_receipt_unique UNIQUE (organization_id, receipt_number);


--
-- Name: organization_student_additional_details org_student_additional_details_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_student_additional_details
    ADD CONSTRAINT org_student_additional_details_pkey PRIMARY KEY (student_id);


--
-- Name: organization_student_subjects org_student_subjects_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_student_subjects
    ADD CONSTRAINT org_student_subjects_pkey PRIMARY KEY (id);


--
-- Name: organization_attendance_status organization_attendance_status_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_attendance_status
    ADD CONSTRAINT organization_attendance_status_pkey PRIMARY KEY (id);


--
-- Name: organization_boards organization_boards_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_boards
    ADD CONSTRAINT organization_boards_pkey PRIMARY KEY (id);


--
-- Name: organization_bus_child_assignments organization_bus_child_assignments_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_bus_child_assignments
    ADD CONSTRAINT organization_bus_child_assignments_pkey PRIMARY KEY (id);


--
-- Name: organization_classes organization_classes_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_classes
    ADD CONSTRAINT organization_classes_pkey PRIMARY KEY (id);


--
-- Name: organization_content_assignments organization_content_assignments_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_content_assignments
    ADD CONSTRAINT organization_content_assignments_pkey PRIMARY KEY (id);


--
-- Name: organization_contents organization_contents_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_contents
    ADD CONSTRAINT organization_contents_pkey PRIMARY KEY (id);


--
-- Name: organization_exam_subject_settings organization_exam_subject_settings_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_exam_subject_settings
    ADD CONSTRAINT organization_exam_subject_settings_pkey PRIMARY KEY (id);


--
-- Name: organization_exams organization_exams_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_exams
    ADD CONSTRAINT organization_exams_pkey PRIMARY KEY (id);


--
-- Name: organization_fee_assignments organization_fee_assignments_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_fee_assignments
    ADD CONSTRAINT organization_fee_assignments_pkey PRIMARY KEY (id);


--
-- Name: organization_guardian_user_links organization_guardian_user_links_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_guardian_user_links
    ADD CONSTRAINT organization_guardian_user_links_pkey PRIMARY KEY (id);


--
-- Name: organization_guardians organization_guardians_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_guardians
    ADD CONSTRAINT organization_guardians_pkey PRIMARY KEY (id);


--
-- Name: organization_holidays organization_holidays_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_holidays
    ADD CONSTRAINT organization_holidays_pkey PRIMARY KEY (id);


--
-- Name: organization_inquiries organization_inquiries_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_inquiries
    ADD CONSTRAINT organization_inquiries_pkey PRIMARY KEY (id);


--
-- Name: organization_journey_mcqs organization_journey_mcqs_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_journey_mcqs
    ADD CONSTRAINT organization_journey_mcqs_pkey PRIMARY KEY (id);


--
-- Name: organization_journey_tasks organization_journey_tasks_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_journey_tasks
    ADD CONSTRAINT organization_journey_tasks_pkey PRIMARY KEY (id);


--
-- Name: organization_journey_templates organization_journey_templates_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_journey_templates
    ADD CONSTRAINT organization_journey_templates_pkey PRIMARY KEY (id);


--
-- Name: organization_languages organization_languages_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_languages
    ADD CONSTRAINT organization_languages_pkey PRIMARY KEY (id);


--
-- Name: organization_leaves organization_leaves_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_leaves
    ADD CONSTRAINT organization_leaves_pkey PRIMARY KEY (id);


--
-- Name: organization_mediums organization_mediums_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_mediums
    ADD CONSTRAINT organization_mediums_pkey PRIMARY KEY (id);


--
-- Name: organization_parent_bus_live_locations organization_parent_bus_live_locations_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_parent_bus_live_locations
    ADD CONSTRAINT organization_parent_bus_live_locations_pkey PRIMARY KEY (bus_id);


--
-- Name: organization_parent_bus_staff_assignments organization_parent_bus_staff_assignments_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_parent_bus_staff_assignments
    ADD CONSTRAINT organization_parent_bus_staff_assignments_pkey PRIMARY KEY (id);


--
-- Name: organization_parent_buses organization_parent_buses_bus_number_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_parent_buses
    ADD CONSTRAINT organization_parent_buses_bus_number_key UNIQUE (bus_number);


--
-- Name: organization_parent_buses organization_parent_buses_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_parent_buses
    ADD CONSTRAINT organization_parent_buses_pkey PRIMARY KEY (id);


--
-- Name: organization_parent_expenses organization_parent_expenses_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_parent_expenses
    ADD CONSTRAINT organization_parent_expenses_pkey PRIMARY KEY (id);


--
-- Name: organization_parent_journey_mcqs organization_parent_journey_mcqs_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_parent_journey_mcqs
    ADD CONSTRAINT organization_parent_journey_mcqs_pkey PRIMARY KEY (id);


--
-- Name: organization_parent_journey_tasks organization_parent_journey_tasks_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_parent_journey_tasks
    ADD CONSTRAINT organization_parent_journey_tasks_pkey PRIMARY KEY (id);


--
-- Name: organization_parent_journey_templates organization_parent_journey_templates_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_parent_journey_templates
    ADD CONSTRAINT organization_parent_journey_templates_pkey PRIMARY KEY (id);


--
-- Name: organization_parent_staff_attendance organization_parent_staff_attendance_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_parent_staff_attendance
    ADD CONSTRAINT organization_parent_staff_attendance_pkey PRIMARY KEY (id);


--
-- Name: organization_parent_staff_bus_enrollments organization_parent_staff_bus_enrollments_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_parent_staff_bus_enrollments
    ADD CONSTRAINT organization_parent_staff_bus_enrollments_pkey PRIMARY KEY (id);


--
-- Name: organization_parent_staff_bus_fares organization_parent_staff_bus_fares_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_parent_staff_bus_fares
    ADD CONSTRAINT organization_parent_staff_bus_fares_pkey PRIMARY KEY (id);


--
-- Name: organization_parent_staff_leave_quotas organization_parent_staff_leave_quotas_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_parent_staff_leave_quotas
    ADD CONSTRAINT organization_parent_staff_leave_quotas_pkey PRIMARY KEY (id);


--
-- Name: organization_parent_staff organization_parent_staff_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_parent_staff
    ADD CONSTRAINT organization_parent_staff_pkey PRIMARY KEY (id);


--
-- Name: organization_parent_staff_salaries organization_parent_staff_salaries_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_parent_staff_salaries
    ADD CONSTRAINT organization_parent_staff_salaries_pkey PRIMARY KEY (id);


--
-- Name: organization_parent_staff_salary_payments organization_parent_staff_salary_payments_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_parent_staff_salary_payments
    ADD CONSTRAINT organization_parent_staff_salary_payments_pkey PRIMARY KEY (id);


--
-- Name: organization_parent_staff_salary_payouts organization_parent_staff_salary_payouts_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_parent_staff_salary_payouts
    ADD CONSTRAINT organization_parent_staff_salary_payouts_pkey PRIMARY KEY (id);


--
-- Name: organization_parent_staff_user_links organization_parent_staff_user_links_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_parent_staff_user_links
    ADD CONSTRAINT organization_parent_staff_user_links_pkey PRIMARY KEY (id);


--
-- Name: organization_parents organization_parents_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_parents
    ADD CONSTRAINT organization_parents_pkey PRIMARY KEY (id);


--
-- Name: organization_parents_profiles organization_parents_profiles_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_parents_profiles
    ADD CONSTRAINT organization_parents_profiles_pkey PRIMARY KEY (parent_organization_id);


--
-- Name: organization_parents_session_tokens organization_parents_session_tokens_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_parents_session_tokens
    ADD CONSTRAINT organization_parents_session_tokens_pkey PRIMARY KEY (id);


--
-- Name: organization_parents_session_tokens organization_parents_session_tokens_refresh_token_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_parents_session_tokens
    ADD CONSTRAINT organization_parents_session_tokens_refresh_token_key UNIQUE (refresh_token);


--
-- Name: organization_parents_session_tokens organization_parents_session_tokens_session_token_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_parents_session_tokens
    ADD CONSTRAINT organization_parents_session_tokens_session_token_key UNIQUE (session_token);


--
-- Name: organization_parents_users organization_parents_users_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_parents_users
    ADD CONSTRAINT organization_parents_users_pkey PRIMARY KEY (id);


--
-- Name: organization_periods organization_periods_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_periods
    ADD CONSTRAINT organization_periods_pkey PRIMARY KEY (id);


--
-- Name: organization_profiles organization_profiles_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_profiles
    ADD CONSTRAINT organization_profiles_pkey PRIMARY KEY (organization_id);


--
-- Name: organization_sections organization_sections_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_sections
    ADD CONSTRAINT organization_sections_pkey PRIMARY KEY (id);


--
-- Name: organization_session_classes organization_session_classes_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_session_classes
    ADD CONSTRAINT organization_session_classes_pkey PRIMARY KEY (id);


--
-- Name: organization_session_sections organization_session_sections_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_session_sections
    ADD CONSTRAINT organization_session_sections_pkey PRIMARY KEY (id);


--
-- Name: organization_session_subjects organization_session_subjects_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_session_subjects
    ADD CONSTRAINT organization_session_subjects_pkey PRIMARY KEY (id);


--
-- Name: organization_session_tokens organization_session_tokens_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_session_tokens
    ADD CONSTRAINT organization_session_tokens_pkey PRIMARY KEY (id);


--
-- Name: organization_session_tokens organization_session_tokens_refresh_token_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_session_tokens
    ADD CONSTRAINT organization_session_tokens_refresh_token_key UNIQUE (refresh_token);


--
-- Name: organization_session_tokens organization_session_tokens_session_token_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_session_tokens
    ADD CONSTRAINT organization_session_tokens_session_token_key UNIQUE (session_token);


--
-- Name: organization_student_additional_fees organization_student_additional_fees_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_student_additional_fees
    ADD CONSTRAINT organization_student_additional_fees_pkey PRIMARY KEY (id);


--
-- Name: organization_student_attendance organization_student_attendance_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_student_attendance
    ADD CONSTRAINT organization_student_attendance_pkey PRIMARY KEY (id);


--
-- Name: organization_student_bus_assignments organization_student_bus_assignments_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_student_bus_assignments
    ADD CONSTRAINT organization_student_bus_assignments_pkey PRIMARY KEY (id);


--
-- Name: organization_student_enrollments organization_student_enrollments_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_student_enrollments
    ADD CONSTRAINT organization_student_enrollments_pkey PRIMARY KEY (id);


--
-- Name: organization_student_exam_marks organization_student_exam_marks_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_student_exam_marks
    ADD CONSTRAINT organization_student_exam_marks_pkey PRIMARY KEY (id);


--
-- Name: organization_student_fee_payments organization_student_fee_payments_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_student_fee_payments
    ADD CONSTRAINT organization_student_fee_payments_pkey PRIMARY KEY (id);


--
-- Name: organization_student_homeworks organization_student_homeworks_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_student_homeworks
    ADD CONSTRAINT organization_student_homeworks_pkey PRIMARY KEY (id);


--
-- Name: organization_student_image_vectors organization_student_image_vectors_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_student_image_vectors
    ADD CONSTRAINT organization_student_image_vectors_pkey PRIMARY KEY (id);


--
-- Name: organization_student_marks organization_student_marks_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_student_marks
    ADD CONSTRAINT organization_student_marks_pkey PRIMARY KEY (id);


--
-- Name: organization_student_user_links organization_student_user_links_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_student_user_links
    ADD CONSTRAINT organization_student_user_links_pkey PRIMARY KEY (id);


--
-- Name: organization_students organization_students_enrollment_number_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_students
    ADD CONSTRAINT organization_students_enrollment_number_key UNIQUE (enrollment_number);


--
-- Name: organization_students organization_students_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_students
    ADD CONSTRAINT organization_students_pkey PRIMARY KEY (id);


--
-- Name: organization_timetable_schedules organization_timetable_schedules_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_timetable_schedules
    ADD CONSTRAINT organization_timetable_schedules_pkey PRIMARY KEY (id);


--
-- Name: organization_users organization_users_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_users
    ADD CONSTRAINT organization_users_pkey PRIMARY KEY (id);


--
-- Name: organization_website_announcements organization_website_announcements_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_website_announcements
    ADD CONSTRAINT organization_website_announcements_pkey PRIMARY KEY (id);


--
-- Name: organization_website_galleries organization_website_galleries_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_website_galleries
    ADD CONSTRAINT organization_website_galleries_pkey PRIMARY KEY (id);


--
-- Name: organizations organizations_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organizations
    ADD CONSTRAINT organizations_pkey PRIMARY KEY (id);


--
-- Name: parent_organization_website_facilities parent_organization_website_facilities_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.parent_organization_website_facilities
    ADD CONSTRAINT parent_organization_website_facilities_pkey PRIMARY KEY (id);


--
-- Name: parent_organization_websites parent_organization_websites_custom_domain_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.parent_organization_websites
    ADD CONSTRAINT parent_organization_websites_custom_domain_key UNIQUE (custom_domain);


--
-- Name: parent_organization_websites parent_organization_websites_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.parent_organization_websites
    ADD CONSTRAINT parent_organization_websites_pkey PRIMARY KEY (id);


--
-- Name: parent_organization_websites parent_organization_websites_subdomain_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.parent_organization_websites
    ADD CONSTRAINT parent_organization_websites_subdomain_key UNIQUE (subdomain);


--
-- Name: reports reports_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.reports
    ADD CONSTRAINT reports_pkey PRIMARY KEY (id);


--
-- Name: rooms rooms_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.rooms
    ADD CONSTRAINT rooms_pkey PRIMARY KEY (id);


--
-- Name: organization_parent_staff_attendance staff_attendance_date_unique; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_parent_staff_attendance
    ADD CONSTRAINT staff_attendance_date_unique UNIQUE (staff_id, attendance_date);


--
-- Name: organization_parent_staff_bus_enrollments staff_bus_enroll_unique; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_parent_staff_bus_enrollments
    ADD CONSTRAINT staff_bus_enroll_unique UNIQUE (staff_id, bus_id, active_session_id);


--
-- Name: organization_parent_staff_bus_fares staff_bus_session_unique; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_parent_staff_bus_fares
    ADD CONSTRAINT staff_bus_session_unique UNIQUE (staff_id, bus_id, active_session_id);


--
-- Name: organization_parent_staff_leave_quotas staff_leave_session_unique; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_parent_staff_leave_quotas
    ADD CONSTRAINT staff_leave_session_unique UNIQUE (staff_id, active_session_id);


--
-- Name: organization_parent_staff_salary_payouts staff_payout_month_year_unique; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_parent_staff_salary_payouts
    ADD CONSTRAINT staff_payout_month_year_unique UNIQUE (staff_id, payout_month, payout_year);


--
-- Name: organization_student_bus_assignments student_bus_session_unique; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_student_bus_assignments
    ADD CONSTRAINT student_bus_session_unique UNIQUE (student_id, bus_id, active_session_id);


--
-- Name: student_document_audit_logs student_document_audit_logs_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.student_document_audit_logs
    ADD CONSTRAINT student_document_audit_logs_pkey PRIMARY KEY (id);


--
-- Name: student_document_physical_locations student_document_physical_locations_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.student_document_physical_locations
    ADD CONSTRAINT student_document_physical_locations_pkey PRIMARY KEY (id);


--
-- Name: student_document_physical_locations student_document_physical_locations_registry_id_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.student_document_physical_locations
    ADD CONSTRAINT student_document_physical_locations_registry_id_key UNIQUE (registry_id);


--
-- Name: student_document_registry student_document_registry_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.student_document_registry
    ADD CONSTRAINT student_document_registry_pkey PRIMARY KEY (id);


--
-- Name: student_document_return_logs student_document_return_logs_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.student_document_return_logs
    ADD CONSTRAINT student_document_return_logs_pkey PRIMARY KEY (id);


--
-- Name: organization_student_marks student_exam_subject_unique; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_student_marks
    ADD CONSTRAINT student_exam_subject_unique UNIQUE (student_id, exam_subject_id);


--
-- Name: organization_student_additional_fees student_fee_session_unique; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_student_additional_fees
    ADD CONSTRAINT student_fee_session_unique UNIQUE (student_id, global_fee_head_id, active_session_id);


--
-- Name: organization_student_enrollments student_session_unique; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_student_enrollments
    ADD CONSTRAINT student_session_unique UNIQUE (student_id, active_session_id);


--
-- Name: organization_students student_sr_org_unique; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_students
    ADD CONSTRAINT student_sr_org_unique UNIQUE (organization_id, sr_number);


--
-- Name: organization_student_image_vectors unique_active_vector_per_person; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_student_image_vectors
    ADD CONSTRAINT unique_active_vector_per_person UNIQUE (student_id, person_type);


--
-- Name: organization_attendance_status unique_org_attendance_status; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_attendance_status
    ADD CONSTRAINT unique_org_attendance_status UNIQUE (organization_id, attendance_status_id);


--
-- Name: organization_boards unique_org_board; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_boards
    ADD CONSTRAINT unique_org_board UNIQUE (organization_id, board_id);


--
-- Name: organization_classes unique_org_class; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_classes
    ADD CONSTRAINT unique_org_class UNIQUE (organization_id, class_id);


--
-- Name: organization_languages unique_org_language; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_languages
    ADD CONSTRAINT unique_org_language UNIQUE (organization_id, language_id);


--
-- Name: organization_mediums unique_org_medium; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_mediums
    ADD CONSTRAINT unique_org_medium UNIQUE (organization_id, medium_id);


--
-- Name: organization_users unique_org_user; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_users
    ADD CONSTRAINT unique_org_user UNIQUE (organization_id, user_id);


--
-- Name: organization_parents_users unique_parent_org_user; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_parents_users
    ADD CONSTRAINT unique_parent_org_user UNIQUE (parent_organization_id, user_id);


--
-- Name: organization_sections unique_section_per_class; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_sections
    ADD CONSTRAINT unique_section_per_class UNIQUE (organization_class_id, name);


--
-- Name: organization_session_classes unique_session_class; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_session_classes
    ADD CONSTRAINT unique_session_class UNIQUE (session_id, organization_class_id);


--
-- Name: organization_session_sections unique_session_section; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_session_sections
    ADD CONSTRAINT unique_session_section UNIQUE (organization_session_class_id, organization_section_id);


--
-- Name: organization_session_subjects unique_session_subject; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_session_subjects
    ADD CONSTRAINT unique_session_subject UNIQUE (organization_session_class_id, subject_id);


--
-- Name: organization_student_attendance unique_student_attendance_date; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_student_attendance
    ADD CONSTRAINT unique_student_attendance_date UNIQUE (student_id, attendance_date);


--
-- Name: student_document_registry unique_student_document_type; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.student_document_registry
    ADD CONSTRAINT unique_student_document_type UNIQUE (student_id, document_type_id);


--
-- Name: user_documents unique_user_document_file; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.user_documents
    ADD CONSTRAINT unique_user_document_file UNIQUE (user_id, document_type_id, file_url);


--
-- Name: parent_organization_website_facilities unique_website_facility; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.parent_organization_website_facilities
    ADD CONSTRAINT unique_website_facility UNIQUE (website_id, facility_id);


--
-- Name: organization_content_assignments uq_content_target; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_content_assignments
    ADD CONSTRAINT uq_content_target UNIQUE (content_id, target_organization_id);


--
-- Name: user_sessions uq_user_device; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.user_sessions
    ADD CONSTRAINT uq_user_device UNIQUE (user_id, device_id);


--
-- Name: user_documents user_documents_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.user_documents
    ADD CONSTRAINT user_documents_pkey PRIMARY KEY (id);


--
-- Name: user_inspirations user_inspirations_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.user_inspirations
    ADD CONSTRAINT user_inspirations_pkey PRIMARY KEY (id);


--
-- Name: user_inspirations user_inspirations_unique; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.user_inspirations
    ADD CONSTRAINT user_inspirations_unique UNIQUE (inspired_user_id, inspiring_user_id);


--
-- Name: user_journey_mcq_progress user_journey_mcq_progress_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.user_journey_mcq_progress
    ADD CONSTRAINT user_journey_mcq_progress_pkey PRIMARY KEY (id);


--
-- Name: user_journey_task_progress user_journey_task_progress_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.user_journey_task_progress
    ADD CONSTRAINT user_journey_task_progress_pkey PRIMARY KEY (id);


--
-- Name: user_journeys user_journeys_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.user_journeys
    ADD CONSTRAINT user_journeys_pkey PRIMARY KEY (id);


--
-- Name: user_profiles user_profiles_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.user_profiles
    ADD CONSTRAINT user_profiles_pkey PRIMARY KEY (user_id);


--
-- Name: user_profiles user_profiles_username_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.user_profiles
    ADD CONSTRAINT user_profiles_username_key UNIQUE (username);


--
-- Name: user_sessions user_sessions_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.user_sessions
    ADD CONSTRAINT user_sessions_pkey PRIMARY KEY (id);


--
-- Name: user_sessions user_sessions_session_id_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.user_sessions
    ADD CONSTRAINT user_sessions_session_id_key UNIQUE (session_id);


--
-- Name: users users_auth_id_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_auth_id_key UNIQUE (auth_id);


--
-- Name: users users_email_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_email_key UNIQUE (email);


--
-- Name: users users_mobile_number_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_mobile_number_key UNIQUE (mobile_number);


--
-- Name: users users_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_pkey PRIMARY KEY (id);


--
-- Name: messages messages_pkey; Type: CONSTRAINT; Schema: realtime; Owner: -
--

ALTER TABLE ONLY realtime.messages
    ADD CONSTRAINT messages_pkey PRIMARY KEY (id, inserted_at);


--
-- Name: messages_2026_06_19 messages_2026_06_19_pkey; Type: CONSTRAINT; Schema: realtime; Owner: -
--

ALTER TABLE ONLY realtime.messages_2026_06_19
    ADD CONSTRAINT messages_2026_06_19_pkey PRIMARY KEY (id, inserted_at);


--
-- Name: messages_2026_06_20 messages_2026_06_20_pkey; Type: CONSTRAINT; Schema: realtime; Owner: -
--

ALTER TABLE ONLY realtime.messages_2026_06_20
    ADD CONSTRAINT messages_2026_06_20_pkey PRIMARY KEY (id, inserted_at);


--
-- Name: messages_2026_06_21 messages_2026_06_21_pkey; Type: CONSTRAINT; Schema: realtime; Owner: -
--

ALTER TABLE ONLY realtime.messages_2026_06_21
    ADD CONSTRAINT messages_2026_06_21_pkey PRIMARY KEY (id, inserted_at);


--
-- Name: messages_2026_06_22 messages_2026_06_22_pkey; Type: CONSTRAINT; Schema: realtime; Owner: -
--

ALTER TABLE ONLY realtime.messages_2026_06_22
    ADD CONSTRAINT messages_2026_06_22_pkey PRIMARY KEY (id, inserted_at);


--
-- Name: messages_2026_06_23 messages_2026_06_23_pkey; Type: CONSTRAINT; Schema: realtime; Owner: -
--

ALTER TABLE ONLY realtime.messages_2026_06_23
    ADD CONSTRAINT messages_2026_06_23_pkey PRIMARY KEY (id, inserted_at);


--
-- Name: messages_2026_06_24 messages_2026_06_24_pkey; Type: CONSTRAINT; Schema: realtime; Owner: -
--

ALTER TABLE ONLY realtime.messages_2026_06_24
    ADD CONSTRAINT messages_2026_06_24_pkey PRIMARY KEY (id, inserted_at);


--
-- Name: messages_2026_06_25 messages_2026_06_25_pkey; Type: CONSTRAINT; Schema: realtime; Owner: -
--

ALTER TABLE ONLY realtime.messages_2026_06_25
    ADD CONSTRAINT messages_2026_06_25_pkey PRIMARY KEY (id, inserted_at);


--
-- Name: messages messages_payload_exclusive; Type: CHECK CONSTRAINT; Schema: realtime; Owner: -
--

ALTER TABLE realtime.messages
    ADD CONSTRAINT messages_payload_exclusive CHECK (((payload IS NULL) OR (binary_payload IS NULL))) NOT VALID;


--
-- Name: subscription pk_subscription; Type: CONSTRAINT; Schema: realtime; Owner: -
--

ALTER TABLE ONLY realtime.subscription
    ADD CONSTRAINT pk_subscription PRIMARY KEY (id);


--
-- Name: schema_migrations schema_migrations_pkey; Type: CONSTRAINT; Schema: realtime; Owner: -
--

ALTER TABLE ONLY realtime.schema_migrations
    ADD CONSTRAINT schema_migrations_pkey PRIMARY KEY (version);


--
-- Name: buckets_analytics buckets_analytics_pkey; Type: CONSTRAINT; Schema: storage; Owner: -
--

ALTER TABLE ONLY storage.buckets_analytics
    ADD CONSTRAINT buckets_analytics_pkey PRIMARY KEY (id);


--
-- Name: buckets buckets_pkey; Type: CONSTRAINT; Schema: storage; Owner: -
--

ALTER TABLE ONLY storage.buckets
    ADD CONSTRAINT buckets_pkey PRIMARY KEY (id);


--
-- Name: buckets_vectors buckets_vectors_pkey; Type: CONSTRAINT; Schema: storage; Owner: -
--

ALTER TABLE ONLY storage.buckets_vectors
    ADD CONSTRAINT buckets_vectors_pkey PRIMARY KEY (id);


--
-- Name: migrations migrations_name_key; Type: CONSTRAINT; Schema: storage; Owner: -
--

ALTER TABLE ONLY storage.migrations
    ADD CONSTRAINT migrations_name_key UNIQUE (name);


--
-- Name: migrations migrations_pkey; Type: CONSTRAINT; Schema: storage; Owner: -
--

ALTER TABLE ONLY storage.migrations
    ADD CONSTRAINT migrations_pkey PRIMARY KEY (id);


--
-- Name: objects objects_pkey; Type: CONSTRAINT; Schema: storage; Owner: -
--

ALTER TABLE ONLY storage.objects
    ADD CONSTRAINT objects_pkey PRIMARY KEY (id);


--
-- Name: s3_multipart_uploads_parts s3_multipart_uploads_parts_pkey; Type: CONSTRAINT; Schema: storage; Owner: -
--

ALTER TABLE ONLY storage.s3_multipart_uploads_parts
    ADD CONSTRAINT s3_multipart_uploads_parts_pkey PRIMARY KEY (id);


--
-- Name: s3_multipart_uploads s3_multipart_uploads_pkey; Type: CONSTRAINT; Schema: storage; Owner: -
--

ALTER TABLE ONLY storage.s3_multipart_uploads
    ADD CONSTRAINT s3_multipart_uploads_pkey PRIMARY KEY (id);


--
-- Name: vector_indexes vector_indexes_pkey; Type: CONSTRAINT; Schema: storage; Owner: -
--

ALTER TABLE ONLY storage.vector_indexes
    ADD CONSTRAINT vector_indexes_pkey PRIMARY KEY (id);


--
-- Name: audit_logs_instance_id_idx; Type: INDEX; Schema: auth; Owner: -
--

CREATE INDEX audit_logs_instance_id_idx ON auth.audit_log_entries USING btree (instance_id);


--
-- Name: confirmation_token_idx; Type: INDEX; Schema: auth; Owner: -
--

CREATE UNIQUE INDEX confirmation_token_idx ON auth.users USING btree (confirmation_token) WHERE ((confirmation_token)::text !~ '^[0-9 ]*$'::text);


--
-- Name: custom_oauth_providers_created_at_idx; Type: INDEX; Schema: auth; Owner: -
--

CREATE INDEX custom_oauth_providers_created_at_idx ON auth.custom_oauth_providers USING btree (created_at);


--
-- Name: custom_oauth_providers_enabled_idx; Type: INDEX; Schema: auth; Owner: -
--

CREATE INDEX custom_oauth_providers_enabled_idx ON auth.custom_oauth_providers USING btree (enabled);


--
-- Name: custom_oauth_providers_identifier_idx; Type: INDEX; Schema: auth; Owner: -
--

CREATE INDEX custom_oauth_providers_identifier_idx ON auth.custom_oauth_providers USING btree (identifier);


--
-- Name: custom_oauth_providers_provider_type_idx; Type: INDEX; Schema: auth; Owner: -
--

CREATE INDEX custom_oauth_providers_provider_type_idx ON auth.custom_oauth_providers USING btree (provider_type);


--
-- Name: email_change_token_current_idx; Type: INDEX; Schema: auth; Owner: -
--

CREATE UNIQUE INDEX email_change_token_current_idx ON auth.users USING btree (email_change_token_current) WHERE ((email_change_token_current)::text !~ '^[0-9 ]*$'::text);


--
-- Name: email_change_token_new_idx; Type: INDEX; Schema: auth; Owner: -
--

CREATE UNIQUE INDEX email_change_token_new_idx ON auth.users USING btree (email_change_token_new) WHERE ((email_change_token_new)::text !~ '^[0-9 ]*$'::text);


--
-- Name: factor_id_created_at_idx; Type: INDEX; Schema: auth; Owner: -
--

CREATE INDEX factor_id_created_at_idx ON auth.mfa_factors USING btree (user_id, created_at);


--
-- Name: flow_state_created_at_idx; Type: INDEX; Schema: auth; Owner: -
--

CREATE INDEX flow_state_created_at_idx ON auth.flow_state USING btree (created_at DESC);


--
-- Name: identities_email_idx; Type: INDEX; Schema: auth; Owner: -
--

CREATE INDEX identities_email_idx ON auth.identities USING btree (email text_pattern_ops);


--
-- Name: INDEX identities_email_idx; Type: COMMENT; Schema: auth; Owner: -
--

COMMENT ON INDEX auth.identities_email_idx IS 'Auth: Ensures indexed queries on the email column';


--
-- Name: identities_user_id_idx; Type: INDEX; Schema: auth; Owner: -
--

CREATE INDEX identities_user_id_idx ON auth.identities USING btree (user_id);


--
-- Name: idx_auth_code; Type: INDEX; Schema: auth; Owner: -
--

CREATE INDEX idx_auth_code ON auth.flow_state USING btree (auth_code);


--
-- Name: idx_oauth_client_states_created_at; Type: INDEX; Schema: auth; Owner: -
--

CREATE INDEX idx_oauth_client_states_created_at ON auth.oauth_client_states USING btree (created_at);


--
-- Name: idx_user_id_auth_method; Type: INDEX; Schema: auth; Owner: -
--

CREATE INDEX idx_user_id_auth_method ON auth.flow_state USING btree (user_id, authentication_method);


--
-- Name: idx_users_created_at_desc; Type: INDEX; Schema: auth; Owner: -
--

CREATE INDEX idx_users_created_at_desc ON auth.users USING btree (created_at DESC);


--
-- Name: idx_users_email; Type: INDEX; Schema: auth; Owner: -
--

CREATE INDEX idx_users_email ON auth.users USING btree (email);


--
-- Name: idx_users_last_sign_in_at_desc; Type: INDEX; Schema: auth; Owner: -
--

CREATE INDEX idx_users_last_sign_in_at_desc ON auth.users USING btree (last_sign_in_at DESC);


--
-- Name: idx_users_name; Type: INDEX; Schema: auth; Owner: -
--

CREATE INDEX idx_users_name ON auth.users USING btree (((raw_user_meta_data ->> 'name'::text))) WHERE ((raw_user_meta_data ->> 'name'::text) IS NOT NULL);


--
-- Name: mfa_challenge_created_at_idx; Type: INDEX; Schema: auth; Owner: -
--

CREATE INDEX mfa_challenge_created_at_idx ON auth.mfa_challenges USING btree (created_at DESC);


--
-- Name: mfa_factors_user_friendly_name_unique; Type: INDEX; Schema: auth; Owner: -
--

CREATE UNIQUE INDEX mfa_factors_user_friendly_name_unique ON auth.mfa_factors USING btree (friendly_name, user_id) WHERE (TRIM(BOTH FROM friendly_name) <> ''::text);


--
-- Name: mfa_factors_user_id_idx; Type: INDEX; Schema: auth; Owner: -
--

CREATE INDEX mfa_factors_user_id_idx ON auth.mfa_factors USING btree (user_id);


--
-- Name: oauth_auth_pending_exp_idx; Type: INDEX; Schema: auth; Owner: -
--

CREATE INDEX oauth_auth_pending_exp_idx ON auth.oauth_authorizations USING btree (expires_at) WHERE (status = 'pending'::auth.oauth_authorization_status);


--
-- Name: oauth_clients_deleted_at_idx; Type: INDEX; Schema: auth; Owner: -
--

CREATE INDEX oauth_clients_deleted_at_idx ON auth.oauth_clients USING btree (deleted_at);


--
-- Name: oauth_consents_active_client_idx; Type: INDEX; Schema: auth; Owner: -
--

CREATE INDEX oauth_consents_active_client_idx ON auth.oauth_consents USING btree (client_id) WHERE (revoked_at IS NULL);


--
-- Name: oauth_consents_active_user_client_idx; Type: INDEX; Schema: auth; Owner: -
--

CREATE INDEX oauth_consents_active_user_client_idx ON auth.oauth_consents USING btree (user_id, client_id) WHERE (revoked_at IS NULL);


--
-- Name: oauth_consents_user_order_idx; Type: INDEX; Schema: auth; Owner: -
--

CREATE INDEX oauth_consents_user_order_idx ON auth.oauth_consents USING btree (user_id, granted_at DESC);


--
-- Name: one_time_tokens_relates_to_hash_idx; Type: INDEX; Schema: auth; Owner: -
--

CREATE INDEX one_time_tokens_relates_to_hash_idx ON auth.one_time_tokens USING hash (relates_to);


--
-- Name: one_time_tokens_token_hash_hash_idx; Type: INDEX; Schema: auth; Owner: -
--

CREATE INDEX one_time_tokens_token_hash_hash_idx ON auth.one_time_tokens USING hash (token_hash);


--
-- Name: one_time_tokens_user_id_token_type_key; Type: INDEX; Schema: auth; Owner: -
--

CREATE UNIQUE INDEX one_time_tokens_user_id_token_type_key ON auth.one_time_tokens USING btree (user_id, token_type);


--
-- Name: reauthentication_token_idx; Type: INDEX; Schema: auth; Owner: -
--

CREATE UNIQUE INDEX reauthentication_token_idx ON auth.users USING btree (reauthentication_token) WHERE ((reauthentication_token)::text !~ '^[0-9 ]*$'::text);


--
-- Name: recovery_token_idx; Type: INDEX; Schema: auth; Owner: -
--

CREATE UNIQUE INDEX recovery_token_idx ON auth.users USING btree (recovery_token) WHERE ((recovery_token)::text !~ '^[0-9 ]*$'::text);


--
-- Name: refresh_tokens_instance_id_idx; Type: INDEX; Schema: auth; Owner: -
--

CREATE INDEX refresh_tokens_instance_id_idx ON auth.refresh_tokens USING btree (instance_id);


--
-- Name: refresh_tokens_instance_id_user_id_idx; Type: INDEX; Schema: auth; Owner: -
--

CREATE INDEX refresh_tokens_instance_id_user_id_idx ON auth.refresh_tokens USING btree (instance_id, user_id);


--
-- Name: refresh_tokens_parent_idx; Type: INDEX; Schema: auth; Owner: -
--

CREATE INDEX refresh_tokens_parent_idx ON auth.refresh_tokens USING btree (parent);


--
-- Name: refresh_tokens_session_id_revoked_idx; Type: INDEX; Schema: auth; Owner: -
--

CREATE INDEX refresh_tokens_session_id_revoked_idx ON auth.refresh_tokens USING btree (session_id, revoked);


--
-- Name: refresh_tokens_updated_at_idx; Type: INDEX; Schema: auth; Owner: -
--

CREATE INDEX refresh_tokens_updated_at_idx ON auth.refresh_tokens USING btree (updated_at DESC);


--
-- Name: saml_providers_sso_provider_id_idx; Type: INDEX; Schema: auth; Owner: -
--

CREATE INDEX saml_providers_sso_provider_id_idx ON auth.saml_providers USING btree (sso_provider_id);


--
-- Name: saml_relay_states_created_at_idx; Type: INDEX; Schema: auth; Owner: -
--

CREATE INDEX saml_relay_states_created_at_idx ON auth.saml_relay_states USING btree (created_at DESC);


--
-- Name: saml_relay_states_for_email_idx; Type: INDEX; Schema: auth; Owner: -
--

CREATE INDEX saml_relay_states_for_email_idx ON auth.saml_relay_states USING btree (for_email);


--
-- Name: saml_relay_states_sso_provider_id_idx; Type: INDEX; Schema: auth; Owner: -
--

CREATE INDEX saml_relay_states_sso_provider_id_idx ON auth.saml_relay_states USING btree (sso_provider_id);


--
-- Name: sessions_not_after_idx; Type: INDEX; Schema: auth; Owner: -
--

CREATE INDEX sessions_not_after_idx ON auth.sessions USING btree (not_after DESC);


--
-- Name: sessions_oauth_client_id_idx; Type: INDEX; Schema: auth; Owner: -
--

CREATE INDEX sessions_oauth_client_id_idx ON auth.sessions USING btree (oauth_client_id);


--
-- Name: sessions_user_id_idx; Type: INDEX; Schema: auth; Owner: -
--

CREATE INDEX sessions_user_id_idx ON auth.sessions USING btree (user_id);


--
-- Name: sso_domains_domain_idx; Type: INDEX; Schema: auth; Owner: -
--

CREATE UNIQUE INDEX sso_domains_domain_idx ON auth.sso_domains USING btree (lower(domain));


--
-- Name: sso_domains_sso_provider_id_idx; Type: INDEX; Schema: auth; Owner: -
--

CREATE INDEX sso_domains_sso_provider_id_idx ON auth.sso_domains USING btree (sso_provider_id);


--
-- Name: sso_providers_resource_id_idx; Type: INDEX; Schema: auth; Owner: -
--

CREATE UNIQUE INDEX sso_providers_resource_id_idx ON auth.sso_providers USING btree (lower(resource_id));


--
-- Name: sso_providers_resource_id_pattern_idx; Type: INDEX; Schema: auth; Owner: -
--

CREATE INDEX sso_providers_resource_id_pattern_idx ON auth.sso_providers USING btree (resource_id text_pattern_ops);


--
-- Name: unique_phone_factor_per_user; Type: INDEX; Schema: auth; Owner: -
--

CREATE UNIQUE INDEX unique_phone_factor_per_user ON auth.mfa_factors USING btree (user_id, phone);


--
-- Name: user_id_created_at_idx; Type: INDEX; Schema: auth; Owner: -
--

CREATE INDEX user_id_created_at_idx ON auth.sessions USING btree (user_id, created_at);


--
-- Name: users_email_partial_key; Type: INDEX; Schema: auth; Owner: -
--

CREATE UNIQUE INDEX users_email_partial_key ON auth.users USING btree (email) WHERE (is_sso_user = false);


--
-- Name: INDEX users_email_partial_key; Type: COMMENT; Schema: auth; Owner: -
--

COMMENT ON INDEX auth.users_email_partial_key IS 'Auth: A partial unique index that applies only when is_sso_user is false';


--
-- Name: users_instance_id_email_idx; Type: INDEX; Schema: auth; Owner: -
--

CREATE INDEX users_instance_id_email_idx ON auth.users USING btree (instance_id, lower((email)::text));


--
-- Name: users_instance_id_idx; Type: INDEX; Schema: auth; Owner: -
--

CREATE INDEX users_instance_id_idx ON auth.users USING btree (instance_id);


--
-- Name: users_is_anonymous_idx; Type: INDEX; Schema: auth; Owner: -
--

CREATE INDEX users_is_anonymous_idx ON auth.users USING btree (is_anonymous);


--
-- Name: webauthn_challenges_expires_at_idx; Type: INDEX; Schema: auth; Owner: -
--

CREATE INDEX webauthn_challenges_expires_at_idx ON auth.webauthn_challenges USING btree (expires_at);


--
-- Name: webauthn_challenges_user_id_idx; Type: INDEX; Schema: auth; Owner: -
--

CREATE INDEX webauthn_challenges_user_id_idx ON auth.webauthn_challenges USING btree (user_id);


--
-- Name: webauthn_credentials_credential_id_key; Type: INDEX; Schema: auth; Owner: -
--

CREATE UNIQUE INDEX webauthn_credentials_credential_id_key ON auth.webauthn_credentials USING btree (credential_id);


--
-- Name: webauthn_credentials_user_id_idx; Type: INDEX; Schema: auth; Owner: -
--

CREATE INDEX webauthn_credentials_user_id_idx ON auth.webauthn_credentials USING btree (user_id);


--
-- Name: ccv_unique_org_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE UNIQUE INDEX ccv_unique_org_idx ON public.content_contributor_verifications USING btree (organization_id) WHERE (contributor_type = 'organization'::text);


--
-- Name: ccv_unique_parent_org_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE UNIQUE INDEX ccv_unique_parent_org_idx ON public.content_contributor_verifications USING btree (parent_organization_id) WHERE (contributor_type = 'parent_organization'::text);


--
-- Name: idx_app_versions_latest; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_app_versions_latest ON public.app_versions USING btree (platform, is_latest) WHERE (is_latest = true);


--
-- Name: idx_ccv_organization_id; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_ccv_organization_id ON public.content_contributor_verifications USING btree (organization_id);


--
-- Name: idx_ccv_parent_organization_id; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_ccv_parent_organization_id ON public.content_contributor_verifications USING btree (parent_organization_id);


--
-- Name: idx_ccv_status; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_ccv_status ON public.content_contributor_verifications USING btree (status);


--
-- Name: idx_ccv_user_id; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_ccv_user_id ON public.content_contributor_verifications USING btree (user_id);


--
-- Name: idx_cs_author_org; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_cs_author_org ON public.case_studies USING btree (author_organization_id);


--
-- Name: idx_cs_author_parent_org; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_cs_author_parent_org ON public.case_studies USING btree (author_parent_organization_id);


--
-- Name: idx_cs_author_user; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_cs_author_user ON public.case_studies USING btree (author_user_id);


--
-- Name: idx_cs_is_deleted; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_cs_is_deleted ON public.case_studies USING btree (is_deleted);


--
-- Name: idx_cs_language; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_cs_language ON public.case_studies USING btree (language);


--
-- Name: idx_cs_slug; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_cs_slug ON public.case_studies USING btree (slug);


--
-- Name: idx_cs_status_published; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_cs_status_published ON public.case_studies USING btree (status, published_at DESC) WHERE ((status = 'published'::text) AND (is_deleted = false));


--
-- Name: idx_cs_tags; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_cs_tags ON public.case_studies USING gin (tags);


--
-- Name: idx_csb_case_study_id; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_csb_case_study_id ON public.case_study_bookmarks USING btree (case_study_id);


--
-- Name: idx_csb_user_id; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_csb_user_id ON public.case_study_bookmarks USING btree (user_id);


--
-- Name: idx_csr_case_study_id; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_csr_case_study_id ON public.case_study_reactions USING btree (case_study_id);


--
-- Name: idx_csr_user_id; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_csr_user_id ON public.case_study_reactions USING btree (user_id);


--
-- Name: idx_global_areas_district; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_global_areas_district ON public.global_areas USING btree (district_id);


--
-- Name: idx_global_areas_state; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_global_areas_state ON public.global_areas USING btree (state_id);


--
-- Name: idx_global_areas_tehsil; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_global_areas_tehsil ON public.global_areas USING btree (tehsil_id);


--
-- Name: idx_global_districts_state; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_global_districts_state ON public.global_districts USING btree (state_id);


--
-- Name: idx_global_states_country; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_global_states_country ON public.global_states USING btree (country_id);


--
-- Name: idx_global_tehsils_district; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_global_tehsils_district ON public.global_tehsils USING btree (district_id);


--
-- Name: idx_global_tehsils_state; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_global_tehsils_state ON public.global_tehsils USING btree (state_id);


--
-- Name: idx_holidays_child_org_lookup; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_holidays_child_org_lookup ON public.organization_holidays USING btree (organization_id, active_session_id, is_active, is_deleted);


--
-- Name: idx_holidays_parent_org_lookup; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_holidays_parent_org_lookup ON public.organization_holidays USING btree (parent_organization_id, is_active, is_deleted);


--
-- Name: idx_leaves_dates; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_leaves_dates ON public.organization_leaves USING btree (start_date, end_date);


--
-- Name: idx_leaves_org; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_leaves_org ON public.organization_leaves USING btree (organization_id);


--
-- Name: idx_leaves_parent_org; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_leaves_parent_org ON public.organization_leaves USING btree (parent_organization_id);


--
-- Name: idx_leaves_staff; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_leaves_staff ON public.organization_leaves USING btree (staff_id) WHERE (staff_id IS NOT NULL);


--
-- Name: idx_leaves_student; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_leaves_student ON public.organization_leaves USING btree (student_id) WHERE (student_id IS NOT NULL);


--
-- Name: idx_org_assignments_lookup; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_org_assignments_lookup ON public.organization_content_assignments USING btree (target_organization_id);


--
-- Name: idx_org_contents_publisher_child; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_org_contents_publisher_child ON public.organization_contents USING btree (organization_id) WHERE (organization_id IS NOT NULL);


--
-- Name: idx_org_contents_publisher_parent; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_org_contents_publisher_parent ON public.organization_contents USING btree (parent_organization_id) WHERE (parent_organization_id IS NOT NULL);


--
-- Name: idx_org_contents_search_filters; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_org_contents_search_filters ON public.organization_contents USING btree (content_type, status, is_active, is_deleted);


--
-- Name: idx_org_contents_session_id; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_org_contents_session_id ON public.organization_contents USING btree (session_id);


--
-- Name: idx_org_parents_users_parent; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_org_parents_users_parent ON public.organization_parents_users USING btree (parent_organization_id) WHERE (is_deleted = false);


--
-- Name: idx_org_parents_users_user; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_org_parents_users_user ON public.organization_parents_users USING btree (user_id) WHERE (is_deleted = false);


--
-- Name: idx_org_users_org; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_org_users_org ON public.organization_users USING btree (organization_id) WHERE (is_deleted = false);


--
-- Name: idx_org_users_user; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_org_users_user ON public.organization_users USING btree (user_id) WHERE (is_deleted = false);


--
-- Name: idx_organizations_parent_id; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_organizations_parent_id ON public.organizations USING btree (parent_organization_id);


--
-- Name: idx_parent_session_tokens_user; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_parent_session_tokens_user ON public.organization_parents_session_tokens USING btree (parent_organization_user_id) WHERE (is_active = true);


--
-- Name: idx_sess_classes; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_sess_classes ON public.organization_session_classes USING btree (session_id);


--
-- Name: idx_sess_sections; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_sess_sections ON public.organization_session_sections USING btree (organization_session_class_id);


--
-- Name: idx_sess_subjects; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_sess_subjects ON public.organization_session_subjects USING btree (organization_session_class_id);


--
-- Name: idx_stud_subj_org; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_stud_subj_org ON public.organization_student_subjects USING btree (organization_id);


--
-- Name: idx_stud_subj_session; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_stud_subj_session ON public.organization_student_subjects USING btree (active_session_id);


--
-- Name: idx_stud_subj_student; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_stud_subj_student ON public.organization_student_subjects USING btree (student_id);


--
-- Name: idx_stud_subj_subject; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_stud_subj_subject ON public.organization_student_subjects USING btree (session_subject_id);


--
-- Name: idx_student_attendance_date; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_student_attendance_date ON public.organization_student_attendance USING btree (attendance_date);


--
-- Name: idx_student_attendance_org; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_student_attendance_org ON public.organization_student_attendance USING btree (organization_id);


--
-- Name: idx_student_attendance_student; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_student_attendance_student ON public.organization_student_attendance USING btree (student_id);


--
-- Name: idx_student_image_vectors_cosine; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_student_image_vectors_cosine ON public.organization_student_image_vectors USING hnsw (face_vector public.vector_cosine_ops);


--
-- Name: idx_timetable_class_section_lookup; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_timetable_class_section_lookup ON public.organization_timetable_schedules USING btree (organization_id, class_id, section_id, is_active, is_deleted);


--
-- Name: idx_timetable_teacher_lookup; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_timetable_teacher_lookup ON public.organization_timetable_schedules USING btree (teacher_id, is_active, is_deleted);


--
-- Name: idx_tokens_refresh; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_tokens_refresh ON public.organization_session_tokens USING btree (refresh_token) WHERE (is_active = true);


--
-- Name: idx_tokens_session; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_tokens_session ON public.organization_session_tokens USING btree (session_token) WHERE (is_active = true);


--
-- Name: idx_user_inspirations_inspired; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_user_inspirations_inspired ON public.user_inspirations USING btree (inspired_user_id) WHERE (is_deleted = false);


--
-- Name: idx_user_inspirations_inspiring; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_user_inspirations_inspiring ON public.user_inspirations USING btree (inspiring_user_id) WHERE (is_deleted = false);


--
-- Name: unique_active_student_session_subject; Type: INDEX; Schema: public; Owner: -
--

CREATE UNIQUE INDEX unique_active_student_session_subject ON public.organization_student_subjects USING btree (student_id, session_subject_id, active_session_id) WHERE (is_deleted = false);


--
-- Name: ix_realtime_subscription_entity; Type: INDEX; Schema: realtime; Owner: -
--

CREATE INDEX ix_realtime_subscription_entity ON realtime.subscription USING btree (entity);


--
-- Name: messages_inserted_at_topic_index; Type: INDEX; Schema: realtime; Owner: -
--

CREATE INDEX messages_inserted_at_topic_index ON ONLY realtime.messages USING btree (inserted_at DESC, topic) WHERE ((extension = 'broadcast'::text) AND (private IS TRUE));


--
-- Name: messages_2026_06_19_inserted_at_topic_idx; Type: INDEX; Schema: realtime; Owner: -
--

CREATE INDEX messages_2026_06_19_inserted_at_topic_idx ON realtime.messages_2026_06_19 USING btree (inserted_at DESC, topic) WHERE ((extension = 'broadcast'::text) AND (private IS TRUE));


--
-- Name: messages_2026_06_20_inserted_at_topic_idx; Type: INDEX; Schema: realtime; Owner: -
--

CREATE INDEX messages_2026_06_20_inserted_at_topic_idx ON realtime.messages_2026_06_20 USING btree (inserted_at DESC, topic) WHERE ((extension = 'broadcast'::text) AND (private IS TRUE));


--
-- Name: messages_2026_06_21_inserted_at_topic_idx; Type: INDEX; Schema: realtime; Owner: -
--

CREATE INDEX messages_2026_06_21_inserted_at_topic_idx ON realtime.messages_2026_06_21 USING btree (inserted_at DESC, topic) WHERE ((extension = 'broadcast'::text) AND (private IS TRUE));


--
-- Name: messages_2026_06_22_inserted_at_topic_idx; Type: INDEX; Schema: realtime; Owner: -
--

CREATE INDEX messages_2026_06_22_inserted_at_topic_idx ON realtime.messages_2026_06_22 USING btree (inserted_at DESC, topic) WHERE ((extension = 'broadcast'::text) AND (private IS TRUE));


--
-- Name: messages_2026_06_23_inserted_at_topic_idx; Type: INDEX; Schema: realtime; Owner: -
--

CREATE INDEX messages_2026_06_23_inserted_at_topic_idx ON realtime.messages_2026_06_23 USING btree (inserted_at DESC, topic) WHERE ((extension = 'broadcast'::text) AND (private IS TRUE));


--
-- Name: messages_2026_06_24_inserted_at_topic_idx; Type: INDEX; Schema: realtime; Owner: -
--

CREATE INDEX messages_2026_06_24_inserted_at_topic_idx ON realtime.messages_2026_06_24 USING btree (inserted_at DESC, topic) WHERE ((extension = 'broadcast'::text) AND (private IS TRUE));


--
-- Name: messages_2026_06_25_inserted_at_topic_idx; Type: INDEX; Schema: realtime; Owner: -
--

CREATE INDEX messages_2026_06_25_inserted_at_topic_idx ON realtime.messages_2026_06_25 USING btree (inserted_at DESC, topic) WHERE ((extension = 'broadcast'::text) AND (private IS TRUE));


--
-- Name: subscription_subscription_id_entity_filters_action_filter_selec; Type: INDEX; Schema: realtime; Owner: -
--

CREATE UNIQUE INDEX subscription_subscription_id_entity_filters_action_filter_selec ON realtime.subscription USING btree (subscription_id, entity, filters, action_filter, COALESCE(selected_columns, '{}'::text[]));


--
-- Name: bname; Type: INDEX; Schema: storage; Owner: -
--

CREATE UNIQUE INDEX bname ON storage.buckets USING btree (name);


--
-- Name: bucketid_objname; Type: INDEX; Schema: storage; Owner: -
--

CREATE UNIQUE INDEX bucketid_objname ON storage.objects USING btree (bucket_id, name);


--
-- Name: buckets_analytics_unique_name_idx; Type: INDEX; Schema: storage; Owner: -
--

CREATE UNIQUE INDEX buckets_analytics_unique_name_idx ON storage.buckets_analytics USING btree (name) WHERE (deleted_at IS NULL);


--
-- Name: idx_multipart_uploads_list; Type: INDEX; Schema: storage; Owner: -
--

CREATE INDEX idx_multipart_uploads_list ON storage.s3_multipart_uploads USING btree (bucket_id, key, created_at);


--
-- Name: idx_objects_bucket_id_name; Type: INDEX; Schema: storage; Owner: -
--

CREATE INDEX idx_objects_bucket_id_name ON storage.objects USING btree (bucket_id, name COLLATE "C");


--
-- Name: idx_objects_bucket_id_name_lower; Type: INDEX; Schema: storage; Owner: -
--

CREATE INDEX idx_objects_bucket_id_name_lower ON storage.objects USING btree (bucket_id, lower(name) COLLATE "C");


--
-- Name: name_prefix_search; Type: INDEX; Schema: storage; Owner: -
--

CREATE INDEX name_prefix_search ON storage.objects USING btree (name text_pattern_ops);


--
-- Name: vector_indexes_name_bucket_id_idx; Type: INDEX; Schema: storage; Owner: -
--

CREATE UNIQUE INDEX vector_indexes_name_bucket_id_idx ON storage.vector_indexes USING btree (name, bucket_id);


--
-- Name: messages_2026_06_19_inserted_at_topic_idx; Type: INDEX ATTACH; Schema: realtime; Owner: -
--

ALTER INDEX realtime.messages_inserted_at_topic_index ATTACH PARTITION realtime.messages_2026_06_19_inserted_at_topic_idx;


--
-- Name: messages_2026_06_19_pkey; Type: INDEX ATTACH; Schema: realtime; Owner: -
--

ALTER INDEX realtime.messages_pkey ATTACH PARTITION realtime.messages_2026_06_19_pkey;


--
-- Name: messages_2026_06_20_inserted_at_topic_idx; Type: INDEX ATTACH; Schema: realtime; Owner: -
--

ALTER INDEX realtime.messages_inserted_at_topic_index ATTACH PARTITION realtime.messages_2026_06_20_inserted_at_topic_idx;


--
-- Name: messages_2026_06_20_pkey; Type: INDEX ATTACH; Schema: realtime; Owner: -
--

ALTER INDEX realtime.messages_pkey ATTACH PARTITION realtime.messages_2026_06_20_pkey;


--
-- Name: messages_2026_06_21_inserted_at_topic_idx; Type: INDEX ATTACH; Schema: realtime; Owner: -
--

ALTER INDEX realtime.messages_inserted_at_topic_index ATTACH PARTITION realtime.messages_2026_06_21_inserted_at_topic_idx;


--
-- Name: messages_2026_06_21_pkey; Type: INDEX ATTACH; Schema: realtime; Owner: -
--

ALTER INDEX realtime.messages_pkey ATTACH PARTITION realtime.messages_2026_06_21_pkey;


--
-- Name: messages_2026_06_22_inserted_at_topic_idx; Type: INDEX ATTACH; Schema: realtime; Owner: -
--

ALTER INDEX realtime.messages_inserted_at_topic_index ATTACH PARTITION realtime.messages_2026_06_22_inserted_at_topic_idx;


--
-- Name: messages_2026_06_22_pkey; Type: INDEX ATTACH; Schema: realtime; Owner: -
--

ALTER INDEX realtime.messages_pkey ATTACH PARTITION realtime.messages_2026_06_22_pkey;


--
-- Name: messages_2026_06_23_inserted_at_topic_idx; Type: INDEX ATTACH; Schema: realtime; Owner: -
--

ALTER INDEX realtime.messages_inserted_at_topic_index ATTACH PARTITION realtime.messages_2026_06_23_inserted_at_topic_idx;


--
-- Name: messages_2026_06_23_pkey; Type: INDEX ATTACH; Schema: realtime; Owner: -
--

ALTER INDEX realtime.messages_pkey ATTACH PARTITION realtime.messages_2026_06_23_pkey;


--
-- Name: messages_2026_06_24_inserted_at_topic_idx; Type: INDEX ATTACH; Schema: realtime; Owner: -
--

ALTER INDEX realtime.messages_inserted_at_topic_index ATTACH PARTITION realtime.messages_2026_06_24_inserted_at_topic_idx;


--
-- Name: messages_2026_06_24_pkey; Type: INDEX ATTACH; Schema: realtime; Owner: -
--

ALTER INDEX realtime.messages_pkey ATTACH PARTITION realtime.messages_2026_06_24_pkey;


--
-- Name: messages_2026_06_25_inserted_at_topic_idx; Type: INDEX ATTACH; Schema: realtime; Owner: -
--

ALTER INDEX realtime.messages_inserted_at_topic_index ATTACH PARTITION realtime.messages_2026_06_25_inserted_at_topic_idx;


--
-- Name: messages_2026_06_25_pkey; Type: INDEX ATTACH; Schema: realtime; Owner: -
--

ALTER INDEX realtime.messages_pkey ATTACH PARTITION realtime.messages_2026_06_25_pkey;


--
-- Name: users on_auth_user_created; Type: TRIGGER; Schema: auth; Owner: -
--

CREATE TRIGGER on_auth_user_created AFTER INSERT ON auth.users FOR EACH ROW EXECUTE FUNCTION public.handle_new_user();


--
-- Name: organization_sections set_timestamp_organization_sections; Type: TRIGGER; Schema: public; Owner: -
--

CREATE TRIGGER set_timestamp_organization_sections BEFORE UPDATE ON public.organization_sections FOR EACH ROW EXECUTE FUNCTION public.handle_update_timestamp();


--
-- Name: global_boards tr_audit_global_boards; Type: TRIGGER; Schema: public; Owner: -
--

CREATE TRIGGER tr_audit_global_boards BEFORE INSERT OR UPDATE ON public.global_boards FOR EACH ROW EXECUTE FUNCTION public.set_audit_fields();


--
-- Name: global_classes tr_audit_global_classes; Type: TRIGGER; Schema: public; Owner: -
--

CREATE TRIGGER tr_audit_global_classes BEFORE INSERT OR UPDATE ON public.global_classes FOR EACH ROW EXECUTE FUNCTION public.set_audit_fields();


--
-- Name: global_exam_types tr_audit_global_exam_types; Type: TRIGGER; Schema: public; Owner: -
--

CREATE TRIGGER tr_audit_global_exam_types BEFORE INSERT OR UPDATE ON public.global_exam_types FOR EACH ROW EXECUTE FUNCTION public.set_audit_fields();


--
-- Name: global_fee_heads tr_audit_global_fee_heads; Type: TRIGGER; Schema: public; Owner: -
--

CREATE TRIGGER tr_audit_global_fee_heads BEFORE INSERT OR UPDATE ON public.global_fee_heads FOR EACH ROW EXECUTE FUNCTION public.set_audit_fields();


--
-- Name: global_journey_mcqs tr_audit_global_journey_mcqs; Type: TRIGGER; Schema: public; Owner: -
--

CREATE TRIGGER tr_audit_global_journey_mcqs BEFORE INSERT OR UPDATE ON public.global_journey_mcqs FOR EACH ROW EXECUTE FUNCTION public.set_audit_fields();


--
-- Name: global_journey_tasks tr_audit_global_journey_tasks; Type: TRIGGER; Schema: public; Owner: -
--

CREATE TRIGGER tr_audit_global_journey_tasks BEFORE INSERT OR UPDATE ON public.global_journey_tasks FOR EACH ROW EXECUTE FUNCTION public.set_audit_fields();


--
-- Name: global_journey_templates tr_audit_global_journey_templates; Type: TRIGGER; Schema: public; Owner: -
--

CREATE TRIGGER tr_audit_global_journey_templates BEFORE INSERT OR UPDATE ON public.global_journey_templates FOR EACH ROW EXECUTE FUNCTION public.set_audit_fields();


--
-- Name: global_marksheet_templates tr_audit_global_marksheet_templates; Type: TRIGGER; Schema: public; Owner: -
--

CREATE TRIGGER tr_audit_global_marksheet_templates BEFORE INSERT OR UPDATE ON public.global_marksheet_templates FOR EACH ROW EXECUTE FUNCTION public.set_audit_fields();


--
-- Name: global_mediums tr_audit_global_mediums; Type: TRIGGER; Schema: public; Owner: -
--

CREATE TRIGGER tr_audit_global_mediums BEFORE INSERT OR UPDATE ON public.global_mediums FOR EACH ROW EXECUTE FUNCTION public.set_audit_fields();


--
-- Name: global_subjects tr_audit_global_subjects; Type: TRIGGER; Schema: public; Owner: -
--

CREATE TRIGGER tr_audit_global_subjects BEFORE INSERT OR UPDATE ON public.global_subjects FOR EACH ROW EXECUTE FUNCTION public.set_audit_fields();


--
-- Name: organization_boards tr_audit_organization_boards; Type: TRIGGER; Schema: public; Owner: -
--

CREATE TRIGGER tr_audit_organization_boards BEFORE INSERT OR UPDATE ON public.organization_boards FOR EACH ROW EXECUTE FUNCTION public.set_audit_fields();


--
-- Name: organization_bus_child_assignments tr_audit_organization_bus_child_assignments; Type: TRIGGER; Schema: public; Owner: -
--

CREATE TRIGGER tr_audit_organization_bus_child_assignments BEFORE INSERT OR UPDATE ON public.organization_bus_child_assignments FOR EACH ROW EXECUTE FUNCTION public.set_audit_fields();


--
-- Name: organization_classes tr_audit_organization_classes; Type: TRIGGER; Schema: public; Owner: -
--

CREATE TRIGGER tr_audit_organization_classes BEFORE INSERT OR UPDATE ON public.organization_classes FOR EACH ROW EXECUTE FUNCTION public.set_audit_fields();


--
-- Name: organization_fee_assignments tr_audit_organization_fee_assignments; Type: TRIGGER; Schema: public; Owner: -
--

CREATE TRIGGER tr_audit_organization_fee_assignments BEFORE INSERT OR UPDATE ON public.organization_fee_assignments FOR EACH ROW EXECUTE FUNCTION public.set_audit_fields();


--
-- Name: organization_guardians tr_audit_organization_guardians; Type: TRIGGER; Schema: public; Owner: -
--

CREATE TRIGGER tr_audit_organization_guardians BEFORE INSERT OR UPDATE ON public.organization_guardians FOR EACH ROW EXECUTE FUNCTION public.set_audit_fields();


--
-- Name: organization_holidays tr_audit_organization_holidays; Type: TRIGGER; Schema: public; Owner: -
--

CREATE TRIGGER tr_audit_organization_holidays BEFORE INSERT OR UPDATE ON public.organization_holidays FOR EACH ROW EXECUTE FUNCTION public.set_audit_fields();


--
-- Name: organization_inquiries tr_audit_organization_inquiries; Type: TRIGGER; Schema: public; Owner: -
--

CREATE TRIGGER tr_audit_organization_inquiries BEFORE INSERT OR UPDATE ON public.organization_inquiries FOR EACH ROW EXECUTE FUNCTION public.set_audit_fields();


--
-- Name: organization_journey_mcqs tr_audit_organization_journey_mcqs; Type: TRIGGER; Schema: public; Owner: -
--

CREATE TRIGGER tr_audit_organization_journey_mcqs BEFORE INSERT OR UPDATE ON public.organization_journey_mcqs FOR EACH ROW EXECUTE FUNCTION public.set_audit_fields();


--
-- Name: organization_journey_tasks tr_audit_organization_journey_tasks; Type: TRIGGER; Schema: public; Owner: -
--

CREATE TRIGGER tr_audit_organization_journey_tasks BEFORE INSERT OR UPDATE ON public.organization_journey_tasks FOR EACH ROW EXECUTE FUNCTION public.set_audit_fields();


--
-- Name: organization_journey_templates tr_audit_organization_journey_templates; Type: TRIGGER; Schema: public; Owner: -
--

CREATE TRIGGER tr_audit_organization_journey_templates BEFORE INSERT OR UPDATE ON public.organization_journey_templates FOR EACH ROW EXECUTE FUNCTION public.set_audit_fields();


--
-- Name: organization_languages tr_audit_organization_languages; Type: TRIGGER; Schema: public; Owner: -
--

CREATE TRIGGER tr_audit_organization_languages BEFORE INSERT OR UPDATE ON public.organization_languages FOR EACH ROW EXECUTE FUNCTION public.set_audit_fields();


--
-- Name: organization_leaves tr_audit_organization_leaves; Type: TRIGGER; Schema: public; Owner: -
--

CREATE TRIGGER tr_audit_organization_leaves BEFORE INSERT OR UPDATE ON public.organization_leaves FOR EACH ROW EXECUTE FUNCTION public.set_audit_fields();


--
-- Name: organization_mediums tr_audit_organization_mediums; Type: TRIGGER; Schema: public; Owner: -
--

CREATE TRIGGER tr_audit_organization_mediums BEFORE INSERT OR UPDATE ON public.organization_mediums FOR EACH ROW EXECUTE FUNCTION public.set_audit_fields();


--
-- Name: organization_parent_bus_staff_assignments tr_audit_organization_parent_bus_staff_assignments; Type: TRIGGER; Schema: public; Owner: -
--

CREATE TRIGGER tr_audit_organization_parent_bus_staff_assignments BEFORE INSERT OR UPDATE ON public.organization_parent_bus_staff_assignments FOR EACH ROW EXECUTE FUNCTION public.set_audit_fields();


--
-- Name: organization_parent_buses tr_audit_organization_parent_buses; Type: TRIGGER; Schema: public; Owner: -
--

CREATE TRIGGER tr_audit_organization_parent_buses BEFORE INSERT OR UPDATE ON public.organization_parent_buses FOR EACH ROW EXECUTE FUNCTION public.set_audit_fields();


--
-- Name: organization_parent_expenses tr_audit_organization_parent_expenses; Type: TRIGGER; Schema: public; Owner: -
--

CREATE TRIGGER tr_audit_organization_parent_expenses BEFORE INSERT OR UPDATE ON public.organization_parent_expenses FOR EACH ROW EXECUTE FUNCTION public.set_audit_fields();


--
-- Name: organization_parent_journey_mcqs tr_audit_organization_parent_journey_mcqs; Type: TRIGGER; Schema: public; Owner: -
--

CREATE TRIGGER tr_audit_organization_parent_journey_mcqs BEFORE INSERT OR UPDATE ON public.organization_parent_journey_mcqs FOR EACH ROW EXECUTE FUNCTION public.set_audit_fields();


--
-- Name: organization_parent_journey_tasks tr_audit_organization_parent_journey_tasks; Type: TRIGGER; Schema: public; Owner: -
--

CREATE TRIGGER tr_audit_organization_parent_journey_tasks BEFORE INSERT OR UPDATE ON public.organization_parent_journey_tasks FOR EACH ROW EXECUTE FUNCTION public.set_audit_fields();


--
-- Name: organization_parent_journey_templates tr_audit_organization_parent_journey_templates; Type: TRIGGER; Schema: public; Owner: -
--

CREATE TRIGGER tr_audit_organization_parent_journey_templates BEFORE INSERT OR UPDATE ON public.organization_parent_journey_templates FOR EACH ROW EXECUTE FUNCTION public.set_audit_fields();


--
-- Name: organization_parent_staff tr_audit_organization_parent_staff; Type: TRIGGER; Schema: public; Owner: -
--

CREATE TRIGGER tr_audit_organization_parent_staff BEFORE INSERT OR UPDATE ON public.organization_parent_staff FOR EACH ROW EXECUTE FUNCTION public.set_audit_fields();


--
-- Name: organization_parent_staff_attendance tr_audit_organization_parent_staff_attendance; Type: TRIGGER; Schema: public; Owner: -
--

CREATE TRIGGER tr_audit_organization_parent_staff_attendance BEFORE INSERT OR UPDATE ON public.organization_parent_staff_attendance FOR EACH ROW EXECUTE FUNCTION public.set_audit_fields();


--
-- Name: organization_parent_staff_bus_enrollments tr_audit_organization_parent_staff_bus_enrollments; Type: TRIGGER; Schema: public; Owner: -
--

CREATE TRIGGER tr_audit_organization_parent_staff_bus_enrollments BEFORE INSERT OR UPDATE ON public.organization_parent_staff_bus_enrollments FOR EACH ROW EXECUTE FUNCTION public.set_audit_fields();


--
-- Name: organization_parent_staff_bus_fares tr_audit_organization_parent_staff_bus_fares; Type: TRIGGER; Schema: public; Owner: -
--

CREATE TRIGGER tr_audit_organization_parent_staff_bus_fares BEFORE INSERT OR UPDATE ON public.organization_parent_staff_bus_fares FOR EACH ROW EXECUTE FUNCTION public.set_audit_fields();


--
-- Name: organization_parent_staff_leave_quotas tr_audit_organization_parent_staff_leave_quotas; Type: TRIGGER; Schema: public; Owner: -
--

CREATE TRIGGER tr_audit_organization_parent_staff_leave_quotas BEFORE INSERT OR UPDATE ON public.organization_parent_staff_leave_quotas FOR EACH ROW EXECUTE FUNCTION public.set_audit_fields();


--
-- Name: organization_parent_staff_salaries tr_audit_organization_parent_staff_salaries; Type: TRIGGER; Schema: public; Owner: -
--

CREATE TRIGGER tr_audit_organization_parent_staff_salaries BEFORE INSERT OR UPDATE ON public.organization_parent_staff_salaries FOR EACH ROW EXECUTE FUNCTION public.set_audit_fields();


--
-- Name: organization_parent_staff_salary_payments tr_audit_organization_parent_staff_salary_payments; Type: TRIGGER; Schema: public; Owner: -
--

CREATE TRIGGER tr_audit_organization_parent_staff_salary_payments BEFORE INSERT OR UPDATE ON public.organization_parent_staff_salary_payments FOR EACH ROW EXECUTE FUNCTION public.set_audit_fields();


--
-- Name: organization_periods tr_audit_organization_periods; Type: TRIGGER; Schema: public; Owner: -
--

CREATE TRIGGER tr_audit_organization_periods BEFORE INSERT OR UPDATE ON public.organization_periods FOR EACH ROW EXECUTE FUNCTION public.set_audit_fields();


--
-- Name: organization_sections tr_audit_organization_sections; Type: TRIGGER; Schema: public; Owner: -
--

CREATE TRIGGER tr_audit_organization_sections BEFORE INSERT OR UPDATE ON public.organization_sections FOR EACH ROW EXECUTE FUNCTION public.set_audit_fields();


--
-- Name: organization_session_classes tr_audit_organization_session_classes; Type: TRIGGER; Schema: public; Owner: -
--

CREATE TRIGGER tr_audit_organization_session_classes BEFORE INSERT OR UPDATE ON public.organization_session_classes FOR EACH ROW EXECUTE FUNCTION public.set_audit_fields();


--
-- Name: organization_session_sections tr_audit_organization_session_sections; Type: TRIGGER; Schema: public; Owner: -
--

CREATE TRIGGER tr_audit_organization_session_sections BEFORE INSERT OR UPDATE ON public.organization_session_sections FOR EACH ROW EXECUTE FUNCTION public.set_audit_fields();


--
-- Name: organization_session_subjects tr_audit_organization_session_subjects; Type: TRIGGER; Schema: public; Owner: -
--

CREATE TRIGGER tr_audit_organization_session_subjects BEFORE INSERT OR UPDATE ON public.organization_session_subjects FOR EACH ROW EXECUTE FUNCTION public.set_audit_fields();


--
-- Name: organization_student_additional_details tr_audit_organization_student_additional_details; Type: TRIGGER; Schema: public; Owner: -
--

CREATE TRIGGER tr_audit_organization_student_additional_details BEFORE INSERT OR UPDATE ON public.organization_student_additional_details FOR EACH ROW EXECUTE FUNCTION public.set_audit_fields();


--
-- Name: organization_student_additional_fees tr_audit_organization_student_additional_fees; Type: TRIGGER; Schema: public; Owner: -
--

CREATE TRIGGER tr_audit_organization_student_additional_fees BEFORE INSERT OR UPDATE ON public.organization_student_additional_fees FOR EACH ROW EXECUTE FUNCTION public.set_audit_fields();


--
-- Name: organization_student_attendance tr_audit_organization_student_attendance; Type: TRIGGER; Schema: public; Owner: -
--

CREATE TRIGGER tr_audit_organization_student_attendance BEFORE INSERT OR UPDATE ON public.organization_student_attendance FOR EACH ROW EXECUTE FUNCTION public.set_audit_fields();


--
-- Name: organization_student_bus_assignments tr_audit_organization_student_bus_assignments; Type: TRIGGER; Schema: public; Owner: -
--

CREATE TRIGGER tr_audit_organization_student_bus_assignments BEFORE INSERT OR UPDATE ON public.organization_student_bus_assignments FOR EACH ROW EXECUTE FUNCTION public.set_audit_fields();


--
-- Name: organization_student_enrollments tr_audit_organization_student_enrollments; Type: TRIGGER; Schema: public; Owner: -
--

CREATE TRIGGER tr_audit_organization_student_enrollments BEFORE INSERT OR UPDATE ON public.organization_student_enrollments FOR EACH ROW EXECUTE FUNCTION public.set_audit_fields();


--
-- Name: organization_student_exam_marks tr_audit_organization_student_exam_marks; Type: TRIGGER; Schema: public; Owner: -
--

CREATE TRIGGER tr_audit_organization_student_exam_marks BEFORE INSERT OR UPDATE ON public.organization_student_exam_marks FOR EACH ROW EXECUTE FUNCTION public.set_audit_fields();


--
-- Name: organization_student_fee_payments tr_audit_organization_student_fee_payments; Type: TRIGGER; Schema: public; Owner: -
--

CREATE TRIGGER tr_audit_organization_student_fee_payments BEFORE INSERT OR UPDATE ON public.organization_student_fee_payments FOR EACH ROW EXECUTE FUNCTION public.set_audit_fields();


--
-- Name: organization_student_homeworks tr_audit_organization_student_homeworks; Type: TRIGGER; Schema: public; Owner: -
--

CREATE TRIGGER tr_audit_organization_student_homeworks BEFORE INSERT OR UPDATE ON public.organization_student_homeworks FOR EACH ROW EXECUTE FUNCTION public.set_audit_fields();


--
-- Name: organization_student_image_vectors tr_audit_organization_student_image_vectors; Type: TRIGGER; Schema: public; Owner: -
--

CREATE TRIGGER tr_audit_organization_student_image_vectors BEFORE INSERT OR UPDATE ON public.organization_student_image_vectors FOR EACH ROW EXECUTE FUNCTION public.set_audit_fields();


--
-- Name: organization_student_marks tr_audit_organization_student_marks; Type: TRIGGER; Schema: public; Owner: -
--

CREATE TRIGGER tr_audit_organization_student_marks BEFORE INSERT OR UPDATE ON public.organization_student_marks FOR EACH ROW EXECUTE FUNCTION public.set_audit_fields();


--
-- Name: organization_student_subjects tr_audit_organization_student_subjects; Type: TRIGGER; Schema: public; Owner: -
--

CREATE TRIGGER tr_audit_organization_student_subjects BEFORE INSERT OR UPDATE ON public.organization_student_subjects FOR EACH ROW EXECUTE FUNCTION public.set_audit_fields();


--
-- Name: organization_students tr_audit_organization_students; Type: TRIGGER; Schema: public; Owner: -
--

CREATE TRIGGER tr_audit_organization_students BEFORE INSERT OR UPDATE ON public.organization_students FOR EACH ROW EXECUTE FUNCTION public.set_audit_fields();


--
-- Name: organization_timetable_schedules tr_audit_organization_timetable_schedules; Type: TRIGGER; Schema: public; Owner: -
--

CREATE TRIGGER tr_audit_organization_timetable_schedules BEFORE INSERT OR UPDATE ON public.organization_timetable_schedules FOR EACH ROW EXECUTE FUNCTION public.set_audit_fields();


--
-- Name: organization_website_announcements tr_audit_organization_website_announcements; Type: TRIGGER; Schema: public; Owner: -
--

CREATE TRIGGER tr_audit_organization_website_announcements BEFORE INSERT OR UPDATE ON public.organization_website_announcements FOR EACH ROW EXECUTE FUNCTION public.set_audit_fields();


--
-- Name: organization_website_galleries tr_audit_organization_website_galleries; Type: TRIGGER; Schema: public; Owner: -
--

CREATE TRIGGER tr_audit_organization_website_galleries BEFORE INSERT OR UPDATE ON public.organization_website_galleries FOR EACH ROW EXECUTE FUNCTION public.set_audit_fields();


--
-- Name: parent_organization_website_facilities tr_audit_parent_organization_website_facilities; Type: TRIGGER; Schema: public; Owner: -
--

CREATE TRIGGER tr_audit_parent_organization_website_facilities BEFORE INSERT OR UPDATE ON public.parent_organization_website_facilities FOR EACH ROW EXECUTE FUNCTION public.set_audit_fields();


--
-- Name: parent_organization_websites tr_audit_parent_organization_websites; Type: TRIGGER; Schema: public; Owner: -
--

CREATE TRIGGER tr_audit_parent_organization_websites BEFORE INSERT OR UPDATE ON public.parent_organization_websites FOR EACH ROW EXECUTE FUNCTION public.set_audit_fields();


--
-- Name: student_document_physical_locations tr_audit_student_document_physical_locations; Type: TRIGGER; Schema: public; Owner: -
--

CREATE TRIGGER tr_audit_student_document_physical_locations BEFORE INSERT OR UPDATE ON public.student_document_physical_locations FOR EACH ROW EXECUTE FUNCTION public.set_audit_fields();


--
-- Name: student_document_registry tr_audit_student_document_registry; Type: TRIGGER; Schema: public; Owner: -
--

CREATE TRIGGER tr_audit_student_document_registry BEFORE INSERT OR UPDATE ON public.student_document_registry FOR EACH ROW EXECUTE FUNCTION public.set_audit_fields();


--
-- Name: messages trg_check_message_cooldown; Type: TRIGGER; Schema: public; Owner: -
--

CREATE TRIGGER trg_check_message_cooldown BEFORE INSERT ON public.messages FOR EACH ROW EXECUTE FUNCTION public.check_message_cooldown();


--
-- Name: messages trg_clean_old_messages; Type: TRIGGER; Schema: public; Owner: -
--

CREATE TRIGGER trg_clean_old_messages AFTER INSERT ON public.messages FOR EACH STATEMENT EXECUTE FUNCTION public.clean_old_messages_trigger();


--
-- Name: users trg_create_profile; Type: TRIGGER; Schema: public; Owner: -
--

CREATE TRIGGER trg_create_profile AFTER INSERT ON public.users FOR EACH ROW EXECUTE FUNCTION public.create_user_profile();


--
-- Name: user_profiles trg_generate_full_name; Type: TRIGGER; Schema: public; Owner: -
--

CREATE TRIGGER trg_generate_full_name BEFORE INSERT OR UPDATE ON public.user_profiles FOR EACH ROW EXECUTE FUNCTION public.generate_full_name();


--
-- Name: global_boards trg_global_boards_updated_at; Type: TRIGGER; Schema: public; Owner: -
--

CREATE TRIGGER trg_global_boards_updated_at BEFORE UPDATE ON public.global_boards FOR EACH ROW EXECUTE FUNCTION public.set_updated_at();


--
-- Name: global_classes trg_global_classes_updated_at; Type: TRIGGER; Schema: public; Owner: -
--

CREATE TRIGGER trg_global_classes_updated_at BEFORE UPDATE ON public.global_classes FOR EACH ROW EXECUTE FUNCTION public.set_updated_at();


--
-- Name: global_exam_types trg_global_exam_types_updated_at; Type: TRIGGER; Schema: public; Owner: -
--

CREATE TRIGGER trg_global_exam_types_updated_at BEFORE UPDATE ON public.global_exam_types FOR EACH ROW EXECUTE FUNCTION public.set_updated_at();


--
-- Name: global_fee_heads trg_global_fee_heads_updated_at; Type: TRIGGER; Schema: public; Owner: -
--

CREATE TRIGGER trg_global_fee_heads_updated_at BEFORE UPDATE ON public.global_fee_heads FOR EACH ROW EXECUTE FUNCTION public.set_updated_at();


--
-- Name: global_languages trg_global_languages_updated_at; Type: TRIGGER; Schema: public; Owner: -
--

CREATE TRIGGER trg_global_languages_updated_at BEFORE UPDATE ON public.global_languages FOR EACH ROW EXECUTE FUNCTION public.set_updated_at();


--
-- Name: global_marksheet_templates trg_global_marksheet_templates_updated_at; Type: TRIGGER; Schema: public; Owner: -
--

CREATE TRIGGER trg_global_marksheet_templates_updated_at BEFORE UPDATE ON public.global_marksheet_templates FOR EACH ROW EXECUTE FUNCTION public.set_updated_at();


--
-- Name: global_mediums trg_global_mediums_updated_at; Type: TRIGGER; Schema: public; Owner: -
--

CREATE TRIGGER trg_global_mediums_updated_at BEFORE UPDATE ON public.global_mediums FOR EACH ROW EXECUTE FUNCTION public.set_updated_at();


--
-- Name: global_subjects trg_global_subjects_updated_at; Type: TRIGGER; Schema: public; Owner: -
--

CREATE TRIGGER trg_global_subjects_updated_at BEFORE UPDATE ON public.global_subjects FOR EACH ROW EXECUTE FUNCTION public.set_updated_at();


--
-- Name: messages trg_update_messages_updated_at; Type: TRIGGER; Schema: public; Owner: -
--

CREATE TRIGGER trg_update_messages_updated_at BEFORE UPDATE ON public.messages FOR EACH ROW EXECUTE FUNCTION public.handle_updated_at();


--
-- Name: moderation_settings trg_update_moderation_updated_at; Type: TRIGGER; Schema: public; Owner: -
--

CREATE TRIGGER trg_update_moderation_updated_at BEFORE UPDATE ON public.moderation_settings FOR EACH ROW EXECUTE FUNCTION public.handle_updated_at();


--
-- Name: reports trg_update_reports_updated_at; Type: TRIGGER; Schema: public; Owner: -
--

CREATE TRIGGER trg_update_reports_updated_at BEFORE UPDATE ON public.reports FOR EACH ROW EXECUTE FUNCTION public.handle_updated_at();


--
-- Name: rooms trg_update_rooms_updated_at; Type: TRIGGER; Schema: public; Owner: -
--

CREATE TRIGGER trg_update_rooms_updated_at BEFORE UPDATE ON public.rooms FOR EACH ROW EXECUTE FUNCTION public.handle_updated_at();


--
-- Name: user_profiles trg_user_profiles_updated_at; Type: TRIGGER; Schema: public; Owner: -
--

CREATE TRIGGER trg_user_profiles_updated_at BEFORE UPDATE ON public.user_profiles FOR EACH ROW EXECUTE FUNCTION public.update_updated_at_column();


--
-- Name: users trg_users_updated_at; Type: TRIGGER; Schema: public; Owner: -
--

CREATE TRIGGER trg_users_updated_at BEFORE UPDATE ON public.users FOR EACH ROW EXECUTE FUNCTION public.update_updated_at_column();


--
-- Name: app_versions trigger_set_only_one_latest; Type: TRIGGER; Schema: public; Owner: -
--

CREATE TRIGGER trigger_set_only_one_latest BEFORE INSERT OR UPDATE OF is_latest ON public.app_versions FOR EACH ROW EXECUTE FUNCTION public.set_only_one_latest_version();


--
-- Name: subscription tr_check_filters; Type: TRIGGER; Schema: realtime; Owner: -
--

CREATE TRIGGER tr_check_filters BEFORE INSERT OR UPDATE ON realtime.subscription FOR EACH ROW EXECUTE FUNCTION realtime.subscription_check_filters();


--
-- Name: buckets enforce_bucket_name_length_trigger; Type: TRIGGER; Schema: storage; Owner: -
--

CREATE TRIGGER enforce_bucket_name_length_trigger BEFORE INSERT OR UPDATE OF name ON storage.buckets FOR EACH ROW EXECUTE FUNCTION storage.enforce_bucket_name_length();


--
-- Name: buckets protect_buckets_delete; Type: TRIGGER; Schema: storage; Owner: -
--

CREATE TRIGGER protect_buckets_delete BEFORE DELETE ON storage.buckets FOR EACH STATEMENT EXECUTE FUNCTION storage.protect_delete();


--
-- Name: objects protect_objects_delete; Type: TRIGGER; Schema: storage; Owner: -
--

CREATE TRIGGER protect_objects_delete BEFORE DELETE ON storage.objects FOR EACH STATEMENT EXECUTE FUNCTION storage.protect_delete();


--
-- Name: objects update_objects_updated_at; Type: TRIGGER; Schema: storage; Owner: -
--

CREATE TRIGGER update_objects_updated_at BEFORE UPDATE ON storage.objects FOR EACH ROW EXECUTE FUNCTION storage.update_updated_at_column();


--
-- Name: identities identities_user_id_fkey; Type: FK CONSTRAINT; Schema: auth; Owner: -
--

ALTER TABLE ONLY auth.identities
    ADD CONSTRAINT identities_user_id_fkey FOREIGN KEY (user_id) REFERENCES auth.users(id) ON DELETE CASCADE;


--
-- Name: mfa_amr_claims mfa_amr_claims_session_id_fkey; Type: FK CONSTRAINT; Schema: auth; Owner: -
--

ALTER TABLE ONLY auth.mfa_amr_claims
    ADD CONSTRAINT mfa_amr_claims_session_id_fkey FOREIGN KEY (session_id) REFERENCES auth.sessions(id) ON DELETE CASCADE;


--
-- Name: mfa_challenges mfa_challenges_auth_factor_id_fkey; Type: FK CONSTRAINT; Schema: auth; Owner: -
--

ALTER TABLE ONLY auth.mfa_challenges
    ADD CONSTRAINT mfa_challenges_auth_factor_id_fkey FOREIGN KEY (factor_id) REFERENCES auth.mfa_factors(id) ON DELETE CASCADE;


--
-- Name: mfa_factors mfa_factors_user_id_fkey; Type: FK CONSTRAINT; Schema: auth; Owner: -
--

ALTER TABLE ONLY auth.mfa_factors
    ADD CONSTRAINT mfa_factors_user_id_fkey FOREIGN KEY (user_id) REFERENCES auth.users(id) ON DELETE CASCADE;


--
-- Name: oauth_authorizations oauth_authorizations_client_id_fkey; Type: FK CONSTRAINT; Schema: auth; Owner: -
--

ALTER TABLE ONLY auth.oauth_authorizations
    ADD CONSTRAINT oauth_authorizations_client_id_fkey FOREIGN KEY (client_id) REFERENCES auth.oauth_clients(id) ON DELETE CASCADE;


--
-- Name: oauth_authorizations oauth_authorizations_user_id_fkey; Type: FK CONSTRAINT; Schema: auth; Owner: -
--

ALTER TABLE ONLY auth.oauth_authorizations
    ADD CONSTRAINT oauth_authorizations_user_id_fkey FOREIGN KEY (user_id) REFERENCES auth.users(id) ON DELETE CASCADE;


--
-- Name: oauth_consents oauth_consents_client_id_fkey; Type: FK CONSTRAINT; Schema: auth; Owner: -
--

ALTER TABLE ONLY auth.oauth_consents
    ADD CONSTRAINT oauth_consents_client_id_fkey FOREIGN KEY (client_id) REFERENCES auth.oauth_clients(id) ON DELETE CASCADE;


--
-- Name: oauth_consents oauth_consents_user_id_fkey; Type: FK CONSTRAINT; Schema: auth; Owner: -
--

ALTER TABLE ONLY auth.oauth_consents
    ADD CONSTRAINT oauth_consents_user_id_fkey FOREIGN KEY (user_id) REFERENCES auth.users(id) ON DELETE CASCADE;


--
-- Name: one_time_tokens one_time_tokens_user_id_fkey; Type: FK CONSTRAINT; Schema: auth; Owner: -
--

ALTER TABLE ONLY auth.one_time_tokens
    ADD CONSTRAINT one_time_tokens_user_id_fkey FOREIGN KEY (user_id) REFERENCES auth.users(id) ON DELETE CASCADE;


--
-- Name: refresh_tokens refresh_tokens_session_id_fkey; Type: FK CONSTRAINT; Schema: auth; Owner: -
--

ALTER TABLE ONLY auth.refresh_tokens
    ADD CONSTRAINT refresh_tokens_session_id_fkey FOREIGN KEY (session_id) REFERENCES auth.sessions(id) ON DELETE CASCADE;


--
-- Name: saml_providers saml_providers_sso_provider_id_fkey; Type: FK CONSTRAINT; Schema: auth; Owner: -
--

ALTER TABLE ONLY auth.saml_providers
    ADD CONSTRAINT saml_providers_sso_provider_id_fkey FOREIGN KEY (sso_provider_id) REFERENCES auth.sso_providers(id) ON DELETE CASCADE;


--
-- Name: saml_relay_states saml_relay_states_flow_state_id_fkey; Type: FK CONSTRAINT; Schema: auth; Owner: -
--

ALTER TABLE ONLY auth.saml_relay_states
    ADD CONSTRAINT saml_relay_states_flow_state_id_fkey FOREIGN KEY (flow_state_id) REFERENCES auth.flow_state(id) ON DELETE CASCADE;


--
-- Name: saml_relay_states saml_relay_states_sso_provider_id_fkey; Type: FK CONSTRAINT; Schema: auth; Owner: -
--

ALTER TABLE ONLY auth.saml_relay_states
    ADD CONSTRAINT saml_relay_states_sso_provider_id_fkey FOREIGN KEY (sso_provider_id) REFERENCES auth.sso_providers(id) ON DELETE CASCADE;


--
-- Name: sessions sessions_oauth_client_id_fkey; Type: FK CONSTRAINT; Schema: auth; Owner: -
--

ALTER TABLE ONLY auth.sessions
    ADD CONSTRAINT sessions_oauth_client_id_fkey FOREIGN KEY (oauth_client_id) REFERENCES auth.oauth_clients(id) ON DELETE CASCADE;


--
-- Name: sessions sessions_user_id_fkey; Type: FK CONSTRAINT; Schema: auth; Owner: -
--

ALTER TABLE ONLY auth.sessions
    ADD CONSTRAINT sessions_user_id_fkey FOREIGN KEY (user_id) REFERENCES auth.users(id) ON DELETE CASCADE;


--
-- Name: sso_domains sso_domains_sso_provider_id_fkey; Type: FK CONSTRAINT; Schema: auth; Owner: -
--

ALTER TABLE ONLY auth.sso_domains
    ADD CONSTRAINT sso_domains_sso_provider_id_fkey FOREIGN KEY (sso_provider_id) REFERENCES auth.sso_providers(id) ON DELETE CASCADE;


--
-- Name: webauthn_challenges webauthn_challenges_user_id_fkey; Type: FK CONSTRAINT; Schema: auth; Owner: -
--

ALTER TABLE ONLY auth.webauthn_challenges
    ADD CONSTRAINT webauthn_challenges_user_id_fkey FOREIGN KEY (user_id) REFERENCES auth.users(id) ON DELETE CASCADE;


--
-- Name: webauthn_credentials webauthn_credentials_user_id_fkey; Type: FK CONSTRAINT; Schema: auth; Owner: -
--

ALTER TABLE ONLY auth.webauthn_credentials
    ADD CONSTRAINT webauthn_credentials_user_id_fkey FOREIGN KEY (user_id) REFERENCES auth.users(id) ON DELETE CASCADE;


--
-- Name: student_document_audit_logs audit_logs_registry_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.student_document_audit_logs
    ADD CONSTRAINT audit_logs_registry_fkey FOREIGN KEY (registry_id) REFERENCES public.student_document_registry(id) ON DELETE SET NULL;


--
-- Name: student_document_audit_logs audit_logs_user_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.student_document_audit_logs
    ADD CONSTRAINT audit_logs_user_fkey FOREIGN KEY (performed_by_user_id) REFERENCES public.users(id) ON DELETE SET NULL;


--
-- Name: content_contributor_verifications ccv_organization_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.content_contributor_verifications
    ADD CONSTRAINT ccv_organization_id_fkey FOREIGN KEY (organization_id) REFERENCES public.organizations(id);


--
-- Name: content_contributor_verifications ccv_parent_organization_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.content_contributor_verifications
    ADD CONSTRAINT ccv_parent_organization_id_fkey FOREIGN KEY (parent_organization_id) REFERENCES public.organization_parents(id);


--
-- Name: content_contributor_verifications ccv_reviewed_by_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.content_contributor_verifications
    ADD CONSTRAINT ccv_reviewed_by_fkey FOREIGN KEY (reviewed_by) REFERENCES public.users(id);


--
-- Name: content_contributor_verifications ccv_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.content_contributor_verifications
    ADD CONSTRAINT ccv_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.users(id);


--
-- Name: case_studies cs_author_organization_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.case_studies
    ADD CONSTRAINT cs_author_organization_id_fkey FOREIGN KEY (author_organization_id) REFERENCES public.organizations(id);


--
-- Name: case_studies cs_author_parent_organization_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.case_studies
    ADD CONSTRAINT cs_author_parent_organization_id_fkey FOREIGN KEY (author_parent_organization_id) REFERENCES public.organization_parents(id);


--
-- Name: case_studies cs_author_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.case_studies
    ADD CONSTRAINT cs_author_user_id_fkey FOREIGN KEY (author_user_id) REFERENCES public.users(id);


--
-- Name: case_studies cs_moderated_by_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.case_studies
    ADD CONSTRAINT cs_moderated_by_fkey FOREIGN KEY (moderated_by) REFERENCES public.users(id);


--
-- Name: case_study_bookmarks csb_case_study_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.case_study_bookmarks
    ADD CONSTRAINT csb_case_study_id_fkey FOREIGN KEY (case_study_id) REFERENCES public.case_studies(id) ON DELETE CASCADE;


--
-- Name: case_study_bookmarks csb_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.case_study_bookmarks
    ADD CONSTRAINT csb_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.users(id) ON DELETE CASCADE;


--
-- Name: case_study_reactions csr_case_study_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.case_study_reactions
    ADD CONSTRAINT csr_case_study_id_fkey FOREIGN KEY (case_study_id) REFERENCES public.case_studies(id) ON DELETE CASCADE;


--
-- Name: case_study_reactions csr_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.case_study_reactions
    ADD CONSTRAINT csr_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.users(id) ON DELETE CASCADE;


--
-- Name: experience_inspirations exp_insp_experience_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.experience_inspirations
    ADD CONSTRAINT exp_insp_experience_id_fkey FOREIGN KEY (experience_id) REFERENCES public.experiences(id) ON DELETE CASCADE;


--
-- Name: experience_inspirations exp_insp_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.experience_inspirations
    ADD CONSTRAINT exp_insp_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.users(id) ON DELETE CASCADE;


--
-- Name: experiences experiences_author_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.experiences
    ADD CONSTRAINT experiences_author_user_id_fkey FOREIGN KEY (author_user_id) REFERENCES public.users(id) ON DELETE CASCADE;


--
-- Name: organization_student_subjects fk_active_session; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_student_subjects
    ADD CONSTRAINT fk_active_session FOREIGN KEY (active_session_id) REFERENCES public.global_sessions(id) ON DELETE RESTRICT;


--
-- Name: organization_content_assignments fk_assignment_content; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_content_assignments
    ADD CONSTRAINT fk_assignment_content FOREIGN KEY (content_id) REFERENCES public.organization_contents(id) ON DELETE CASCADE;


--
-- Name: organization_content_assignments fk_assignment_target_org; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_content_assignments
    ADD CONSTRAINT fk_assignment_target_org FOREIGN KEY (target_organization_id) REFERENCES public.organizations(id) ON DELETE CASCADE;


--
-- Name: global_exam_subject_rules fk_class; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.global_exam_subject_rules
    ADD CONSTRAINT fk_class FOREIGN KEY (class_id) REFERENCES public.global_classes(id);


--
-- Name: organization_contents fk_content_child_org; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_contents
    ADD CONSTRAINT fk_content_child_org FOREIGN KEY (organization_id) REFERENCES public.organizations(id) ON DELETE SET NULL;


--
-- Name: organization_contents fk_content_created_by; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_contents
    ADD CONSTRAINT fk_content_created_by FOREIGN KEY (created_by) REFERENCES public.users(id) ON DELETE SET NULL;


--
-- Name: organization_contents fk_content_parent_org; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_contents
    ADD CONSTRAINT fk_content_parent_org FOREIGN KEY (parent_organization_id) REFERENCES public.organization_parents(id) ON DELETE SET NULL;


--
-- Name: organization_contents fk_content_session; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_contents
    ADD CONSTRAINT fk_content_session FOREIGN KEY (session_id) REFERENCES public.global_sessions(id) ON DELETE CASCADE;


--
-- Name: organization_contents fk_content_updated_by; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_contents
    ADD CONSTRAINT fk_content_updated_by FOREIGN KEY (updated_by) REFERENCES public.users(id) ON DELETE SET NULL;


--
-- Name: organization_student_additional_details fk_created_by; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_student_additional_details
    ADD CONSTRAINT fk_created_by FOREIGN KEY (created_by) REFERENCES public.users(id) ON DELETE SET NULL;


--
-- Name: organization_student_subjects fk_created_by; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_student_subjects
    ADD CONSTRAINT fk_created_by FOREIGN KEY (created_by) REFERENCES public.users(id) ON DELETE SET NULL;


--
-- Name: global_exam_subject_rules fk_exam_type; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.global_exam_subject_rules
    ADD CONSTRAINT fk_exam_type FOREIGN KEY (exam_type_id) REFERENCES public.global_exam_types(id);


--
-- Name: organization_exams fk_exam_type; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_exams
    ADD CONSTRAINT fk_exam_type FOREIGN KEY (exam_type_id) REFERENCES public.global_exam_types(id);


--
-- Name: organization_holidays fk_holidays_child_org; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_holidays
    ADD CONSTRAINT fk_holidays_child_org FOREIGN KEY (organization_id) REFERENCES public.organizations(id) ON DELETE CASCADE;


--
-- Name: organization_holidays fk_holidays_created_by; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_holidays
    ADD CONSTRAINT fk_holidays_created_by FOREIGN KEY (created_by) REFERENCES public.users(id) ON DELETE SET NULL;


--
-- Name: organization_holidays fk_holidays_parent_org; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_holidays
    ADD CONSTRAINT fk_holidays_parent_org FOREIGN KEY (parent_organization_id) REFERENCES public.organization_parents(id) ON DELETE CASCADE;


--
-- Name: organization_holidays fk_holidays_session; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_holidays
    ADD CONSTRAINT fk_holidays_session FOREIGN KEY (active_session_id) REFERENCES public.global_sessions(id) ON DELETE CASCADE;


--
-- Name: organization_holidays fk_holidays_updated_by; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_holidays
    ADD CONSTRAINT fk_holidays_updated_by FOREIGN KEY (updated_by) REFERENCES public.users(id) ON DELETE SET NULL;


--
-- Name: organization_leaves fk_leaves_action_by; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_leaves
    ADD CONSTRAINT fk_leaves_action_by FOREIGN KEY (action_by) REFERENCES public.users(id);


--
-- Name: organization_leaves fk_leaves_child_org; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_leaves
    ADD CONSTRAINT fk_leaves_child_org FOREIGN KEY (organization_id) REFERENCES public.organizations(id);


--
-- Name: organization_leaves fk_leaves_created_by; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_leaves
    ADD CONSTRAINT fk_leaves_created_by FOREIGN KEY (created_by) REFERENCES public.users(id);


--
-- Name: organization_leaves fk_leaves_parent_org; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_leaves
    ADD CONSTRAINT fk_leaves_parent_org FOREIGN KEY (parent_organization_id) REFERENCES public.organization_parents(id);


--
-- Name: organization_leaves fk_leaves_session; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_leaves
    ADD CONSTRAINT fk_leaves_session FOREIGN KEY (active_session_id) REFERENCES public.global_sessions(id);


--
-- Name: organization_leaves fk_leaves_staff; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_leaves
    ADD CONSTRAINT fk_leaves_staff FOREIGN KEY (staff_id) REFERENCES public.organization_parent_staff(id) ON DELETE CASCADE;


--
-- Name: organization_leaves fk_leaves_student; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_leaves
    ADD CONSTRAINT fk_leaves_student FOREIGN KEY (student_id) REFERENCES public.organization_students(id) ON DELETE CASCADE;


--
-- Name: organization_leaves fk_leaves_updated_by; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_leaves
    ADD CONSTRAINT fk_leaves_updated_by FOREIGN KEY (updated_by) REFERENCES public.users(id);


--
-- Name: organization_parent_bus_live_locations fk_live_loc_bus; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_parent_bus_live_locations
    ADD CONSTRAINT fk_live_loc_bus FOREIGN KEY (bus_id) REFERENCES public.organization_parent_buses(id) ON DELETE CASCADE;


--
-- Name: organization_parent_bus_live_locations fk_live_loc_parent_org; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_parent_bus_live_locations
    ADD CONSTRAINT fk_live_loc_parent_org FOREIGN KEY (parent_organization_id) REFERENCES public.organization_parents(id) ON DELETE CASCADE;


--
-- Name: organization_parent_bus_live_locations fk_live_loc_session; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_parent_bus_live_locations
    ADD CONSTRAINT fk_live_loc_session FOREIGN KEY (active_session_id) REFERENCES public.global_sessions(id);


--
-- Name: organization_student_additional_details fk_mother_tongue; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_student_additional_details
    ADD CONSTRAINT fk_mother_tongue FOREIGN KEY (mother_tongue_id) REFERENCES public.global_languages(id) ON DELETE SET NULL;


--
-- Name: organization_exams fk_org; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_exams
    ADD CONSTRAINT fk_org FOREIGN KEY (organization_id) REFERENCES public.organizations(id);


--
-- Name: organization_student_subjects fk_organization; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_student_subjects
    ADD CONSTRAINT fk_organization FOREIGN KEY (organization_id) REFERENCES public.organizations(id) ON DELETE CASCADE;


--
-- Name: organization_student_subjects fk_parent_organization; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_student_subjects
    ADD CONSTRAINT fk_parent_organization FOREIGN KEY (parent_organization_id) REFERENCES public.organization_parents(id) ON DELETE SET NULL;


--
-- Name: organization_student_additional_details fk_permanent_area; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_student_additional_details
    ADD CONSTRAINT fk_permanent_area FOREIGN KEY (permanent_address_area_id) REFERENCES public.global_areas(id) ON DELETE SET NULL;


--
-- Name: organization_exams fk_session; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_exams
    ADD CONSTRAINT fk_session FOREIGN KEY (active_session_id) REFERENCES public.global_sessions(id);


--
-- Name: organization_student_subjects fk_session_subject; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_student_subjects
    ADD CONSTRAINT fk_session_subject FOREIGN KEY (session_subject_id) REFERENCES public.organization_session_subjects(id) ON DELETE CASCADE;


--
-- Name: organization_student_exam_marks fk_student; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_student_exam_marks
    ADD CONSTRAINT fk_student FOREIGN KEY (student_id) REFERENCES public.organization_students(id) ON DELETE CASCADE;


--
-- Name: organization_student_subjects fk_student; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_student_subjects
    ADD CONSTRAINT fk_student FOREIGN KEY (student_id) REFERENCES public.organization_students(id) ON DELETE CASCADE;


--
-- Name: organization_student_attendance fk_student_attendance_created_by; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_student_attendance
    ADD CONSTRAINT fk_student_attendance_created_by FOREIGN KEY (created_by) REFERENCES public.users(id);


--
-- Name: organization_student_attendance fk_student_attendance_org; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_student_attendance
    ADD CONSTRAINT fk_student_attendance_org FOREIGN KEY (organization_id) REFERENCES public.organizations(id);


--
-- Name: organization_student_attendance fk_student_attendance_session; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_student_attendance
    ADD CONSTRAINT fk_student_attendance_session FOREIGN KEY (active_session_id) REFERENCES public.global_sessions(id);


--
-- Name: organization_student_attendance fk_student_attendance_staff; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_student_attendance
    ADD CONSTRAINT fk_student_attendance_staff FOREIGN KEY (marked_by_staff_id) REFERENCES public.organization_parent_staff(id) ON DELETE SET NULL;


--
-- Name: organization_student_attendance fk_student_attendance_student; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_student_attendance
    ADD CONSTRAINT fk_student_attendance_student FOREIGN KEY (student_id) REFERENCES public.organization_students(id) ON DELETE CASCADE;


--
-- Name: organization_student_attendance fk_student_attendance_updated_by; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_student_attendance
    ADD CONSTRAINT fk_student_attendance_updated_by FOREIGN KEY (updated_by) REFERENCES public.users(id);


--
-- Name: organization_student_additional_details fk_student_id; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_student_additional_details
    ADD CONSTRAINT fk_student_id FOREIGN KEY (student_id) REFERENCES public.organization_students(id) ON DELETE CASCADE;


--
-- Name: global_exam_subject_rules fk_subject; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.global_exam_subject_rules
    ADD CONSTRAINT fk_subject FOREIGN KEY (subject_id) REFERENCES public.global_subjects(id);


--
-- Name: organization_timetable_schedules fk_timetable_child_org; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_timetable_schedules
    ADD CONSTRAINT fk_timetable_child_org FOREIGN KEY (organization_id) REFERENCES public.organizations(id) ON DELETE CASCADE;


--
-- Name: organization_timetable_schedules fk_timetable_class; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_timetable_schedules
    ADD CONSTRAINT fk_timetable_class FOREIGN KEY (class_id) REFERENCES public.organization_classes(id) ON DELETE CASCADE;


--
-- Name: organization_timetable_schedules fk_timetable_created_by; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_timetable_schedules
    ADD CONSTRAINT fk_timetable_created_by FOREIGN KEY (created_by) REFERENCES public.users(id) ON DELETE SET NULL;


--
-- Name: organization_timetable_schedules fk_timetable_parent_org; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_timetable_schedules
    ADD CONSTRAINT fk_timetable_parent_org FOREIGN KEY (parent_organization_id) REFERENCES public.organization_parents(id) ON DELETE CASCADE;


--
-- Name: organization_timetable_schedules fk_timetable_period; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_timetable_schedules
    ADD CONSTRAINT fk_timetable_period FOREIGN KEY (period_id) REFERENCES public.organization_periods(id) ON DELETE CASCADE;


--
-- Name: organization_timetable_schedules fk_timetable_section; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_timetable_schedules
    ADD CONSTRAINT fk_timetable_section FOREIGN KEY (section_id) REFERENCES public.organization_sections(id) ON DELETE CASCADE;


--
-- Name: organization_timetable_schedules fk_timetable_session; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_timetable_schedules
    ADD CONSTRAINT fk_timetable_session FOREIGN KEY (active_session_id) REFERENCES public.global_sessions(id) ON DELETE CASCADE;


--
-- Name: organization_timetable_schedules fk_timetable_subject; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_timetable_schedules
    ADD CONSTRAINT fk_timetable_subject FOREIGN KEY (subject_id) REFERENCES public.global_subjects(id) ON DELETE CASCADE;


--
-- Name: organization_timetable_schedules fk_timetable_teacher; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_timetable_schedules
    ADD CONSTRAINT fk_timetable_teacher FOREIGN KEY (teacher_id) REFERENCES public.organization_parent_staff(id) ON DELETE CASCADE;


--
-- Name: organization_timetable_schedules fk_timetable_updated_by; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_timetable_schedules
    ADD CONSTRAINT fk_timetable_updated_by FOREIGN KEY (updated_by) REFERENCES public.users(id) ON DELETE SET NULL;


--
-- Name: organization_student_additional_details fk_updated_by; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_student_additional_details
    ADD CONSTRAINT fk_updated_by FOREIGN KEY (updated_by) REFERENCES public.users(id) ON DELETE SET NULL;


--
-- Name: organization_student_subjects fk_updated_by; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_student_subjects
    ADD CONSTRAINT fk_updated_by FOREIGN KEY (updated_by) REFERENCES public.users(id) ON DELETE SET NULL;


--
-- Name: organization_student_image_vectors fk_vector_created_by; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_student_image_vectors
    ADD CONSTRAINT fk_vector_created_by FOREIGN KEY (created_by) REFERENCES public.users(id) ON DELETE SET NULL;


--
-- Name: organization_student_image_vectors fk_vector_org; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_student_image_vectors
    ADD CONSTRAINT fk_vector_org FOREIGN KEY (organization_id) REFERENCES public.organizations(id) ON DELETE CASCADE;


--
-- Name: organization_student_image_vectors fk_vector_student; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_student_image_vectors
    ADD CONSTRAINT fk_vector_student FOREIGN KEY (student_id) REFERENCES public.organization_students(id) ON DELETE CASCADE;


--
-- Name: organization_student_image_vectors fk_vector_updated_by; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_student_image_vectors
    ADD CONSTRAINT fk_vector_updated_by FOREIGN KEY (updated_by) REFERENCES public.users(id) ON DELETE SET NULL;


--
-- Name: global_areas global_areas_district_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.global_areas
    ADD CONSTRAINT global_areas_district_id_fkey FOREIGN KEY (district_id) REFERENCES public.global_districts(id) ON DELETE CASCADE;


--
-- Name: global_areas global_areas_panchayat_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.global_areas
    ADD CONSTRAINT global_areas_panchayat_id_fkey FOREIGN KEY (panchayat_id) REFERENCES public.global_panchayats(id) ON DELETE CASCADE;


--
-- Name: global_areas global_areas_state_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.global_areas
    ADD CONSTRAINT global_areas_state_id_fkey FOREIGN KEY (state_id) REFERENCES public.global_states(id) ON DELETE CASCADE;


--
-- Name: global_areas global_areas_tehsil_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.global_areas
    ADD CONSTRAINT global_areas_tehsil_id_fkey FOREIGN KEY (tehsil_id) REFERENCES public.global_tehsils(id) ON DELETE CASCADE;


--
-- Name: global_districts global_districts_state_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.global_districts
    ADD CONSTRAINT global_districts_state_id_fkey FOREIGN KEY (state_id) REFERENCES public.global_states(id) ON DELETE CASCADE;


--
-- Name: global_journey_mcqs global_journey_mcqs_template_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.global_journey_mcqs
    ADD CONSTRAINT global_journey_mcqs_template_fkey FOREIGN KEY (template_id) REFERENCES public.global_journey_templates(id) ON DELETE CASCADE;


--
-- Name: global_journey_tasks global_journey_tasks_template_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.global_journey_tasks
    ADD CONSTRAINT global_journey_tasks_template_fkey FOREIGN KEY (template_id) REFERENCES public.global_journey_templates(id) ON DELETE CASCADE;


--
-- Name: global_journey_templates global_journey_templates_created_by_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.global_journey_templates
    ADD CONSTRAINT global_journey_templates_created_by_fkey FOREIGN KEY (created_by) REFERENCES public.users(id);


--
-- Name: global_journey_templates global_journey_templates_updated_by_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.global_journey_templates
    ADD CONSTRAINT global_journey_templates_updated_by_fkey FOREIGN KEY (updated_by) REFERENCES public.users(id);


--
-- Name: global_marksheet_templates global_marksheet_templates_board_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.global_marksheet_templates
    ADD CONSTRAINT global_marksheet_templates_board_id_fkey FOREIGN KEY (board_id) REFERENCES public.global_boards(id) ON DELETE CASCADE;


--
-- Name: global_marksheet_templates global_marksheet_templates_class_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.global_marksheet_templates
    ADD CONSTRAINT global_marksheet_templates_class_id_fkey FOREIGN KEY (class_id) REFERENCES public.global_classes(id) ON DELETE CASCADE;


--
-- Name: global_mediums global_mediums_board_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.global_mediums
    ADD CONSTRAINT global_mediums_board_id_fkey FOREIGN KEY (board_id) REFERENCES public.global_boards(id) ON DELETE CASCADE;


--
-- Name: global_panchayats global_panchayats_district_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.global_panchayats
    ADD CONSTRAINT global_panchayats_district_id_fkey FOREIGN KEY (district_id) REFERENCES public.global_districts(id) ON DELETE CASCADE;


--
-- Name: global_panchayats global_panchayats_state_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.global_panchayats
    ADD CONSTRAINT global_panchayats_state_id_fkey FOREIGN KEY (state_id) REFERENCES public.global_states(id) ON DELETE CASCADE;


--
-- Name: global_panchayats global_panchayats_tehsil_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.global_panchayats
    ADD CONSTRAINT global_panchayats_tehsil_id_fkey FOREIGN KEY (tehsil_id) REFERENCES public.global_tehsils(id) ON DELETE CASCADE;


--
-- Name: global_states global_states_country_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.global_states
    ADD CONSTRAINT global_states_country_id_fkey FOREIGN KEY (country_id) REFERENCES public.global_countries(id) ON DELETE CASCADE;


--
-- Name: global_tehsils global_tehsils_district_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.global_tehsils
    ADD CONSTRAINT global_tehsils_district_id_fkey FOREIGN KEY (district_id) REFERENCES public.global_districts(id) ON DELETE CASCADE;


--
-- Name: global_tehsils global_tehsils_state_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.global_tehsils
    ADD CONSTRAINT global_tehsils_state_id_fkey FOREIGN KEY (state_id) REFERENCES public.global_states(id) ON DELETE CASCADE;


--
-- Name: messages messages_room_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.messages
    ADD CONSTRAINT messages_room_id_fkey FOREIGN KEY (room_id) REFERENCES public.rooms(id) ON DELETE CASCADE;


--
-- Name: messages messages_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.messages
    ADD CONSTRAINT messages_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.users(id) ON DELETE CASCADE;


--
-- Name: organization_journey_mcqs org_journey_mcqs_template_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_journey_mcqs
    ADD CONSTRAINT org_journey_mcqs_template_fkey FOREIGN KEY (template_id) REFERENCES public.organization_journey_templates(id) ON DELETE CASCADE;


--
-- Name: organization_journey_tasks org_journey_tasks_template_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_journey_tasks
    ADD CONSTRAINT org_journey_tasks_template_fkey FOREIGN KEY (template_id) REFERENCES public.organization_journey_templates(id) ON DELETE CASCADE;


--
-- Name: organization_journey_templates org_journey_templates_created_by_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_journey_templates
    ADD CONSTRAINT org_journey_templates_created_by_fkey FOREIGN KEY (created_by) REFERENCES public.users(id);


--
-- Name: organization_journey_templates org_journey_templates_org_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_journey_templates
    ADD CONSTRAINT org_journey_templates_org_fkey FOREIGN KEY (organization_id) REFERENCES public.organizations(id);


--
-- Name: organization_journey_templates org_journey_templates_updated_by_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_journey_templates
    ADD CONSTRAINT org_journey_templates_updated_by_fkey FOREIGN KEY (updated_by) REFERENCES public.users(id);


--
-- Name: organization_session_classes org_sess_classes_class_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_session_classes
    ADD CONSTRAINT org_sess_classes_class_fkey FOREIGN KEY (organization_class_id) REFERENCES public.organization_classes(id) ON DELETE RESTRICT;


--
-- Name: organization_session_classes org_sess_classes_org_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_session_classes
    ADD CONSTRAINT org_sess_classes_org_fkey FOREIGN KEY (organization_id) REFERENCES public.organizations(id) ON DELETE CASCADE;


--
-- Name: organization_session_classes org_sess_classes_sess_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_session_classes
    ADD CONSTRAINT org_sess_classes_sess_fkey FOREIGN KEY (session_id) REFERENCES public.global_sessions(id) ON DELETE RESTRICT;


--
-- Name: organization_session_sections org_sess_sect_class_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_session_sections
    ADD CONSTRAINT org_sess_sect_class_fkey FOREIGN KEY (organization_session_class_id) REFERENCES public.organization_session_classes(id) ON DELETE CASCADE;


--
-- Name: organization_session_sections org_sess_sect_sect_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_session_sections
    ADD CONSTRAINT org_sess_sect_sect_fkey FOREIGN KEY (organization_section_id) REFERENCES public.organization_sections(id) ON DELETE CASCADE;


--
-- Name: organization_session_sections org_sess_sect_teacher_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_session_sections
    ADD CONSTRAINT org_sess_sect_teacher_fkey FOREIGN KEY (class_teacher_id) REFERENCES public.organization_users(id) ON DELETE SET NULL;


--
-- Name: organization_session_subjects org_sess_subj_class_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_session_subjects
    ADD CONSTRAINT org_sess_subj_class_fkey FOREIGN KEY (organization_session_class_id) REFERENCES public.organization_session_classes(id) ON DELETE CASCADE;


--
-- Name: organization_session_subjects org_sess_subj_subj_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_session_subjects
    ADD CONSTRAINT org_sess_subj_subj_fkey FOREIGN KEY (subject_id) REFERENCES public.global_subjects(id) ON DELETE RESTRICT;


--
-- Name: organization_session_subjects org_sess_subj_teacher_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_session_subjects
    ADD CONSTRAINT org_sess_subj_teacher_fkey FOREIGN KEY (subject_teacher_id) REFERENCES public.organization_users(id) ON DELETE SET NULL;


--
-- Name: organization_attendance_status organization_attendance_status_org_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_attendance_status
    ADD CONSTRAINT organization_attendance_status_org_fkey FOREIGN KEY (organization_id) REFERENCES public.organizations(id) ON DELETE CASCADE;


--
-- Name: organization_attendance_status organization_attendance_status_status_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_attendance_status
    ADD CONSTRAINT organization_attendance_status_status_fkey FOREIGN KEY (attendance_status_id) REFERENCES public.global_attendance_status(id) ON DELETE CASCADE;


--
-- Name: organization_boards organization_boards_board_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_boards
    ADD CONSTRAINT organization_boards_board_fkey FOREIGN KEY (board_id) REFERENCES public.global_boards(id) ON DELETE CASCADE;


--
-- Name: organization_boards organization_boards_created_by_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_boards
    ADD CONSTRAINT organization_boards_created_by_fkey FOREIGN KEY (created_by) REFERENCES public.users(id);


--
-- Name: organization_boards organization_boards_org_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_boards
    ADD CONSTRAINT organization_boards_org_fkey FOREIGN KEY (organization_id) REFERENCES public.organizations(id) ON DELETE CASCADE;


--
-- Name: organization_boards organization_boards_updated_by_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_boards
    ADD CONSTRAINT organization_boards_updated_by_fkey FOREIGN KEY (updated_by) REFERENCES public.users(id);


--
-- Name: organization_bus_child_assignments organization_bus_child_assignments_active_session_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_bus_child_assignments
    ADD CONSTRAINT organization_bus_child_assignments_active_session_id_fkey FOREIGN KEY (active_session_id) REFERENCES public.global_sessions(id) ON DELETE CASCADE;


--
-- Name: organization_bus_child_assignments organization_bus_child_assignments_bus_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_bus_child_assignments
    ADD CONSTRAINT organization_bus_child_assignments_bus_id_fkey FOREIGN KEY (bus_id) REFERENCES public.organization_parent_buses(id) ON DELETE CASCADE;


--
-- Name: organization_bus_child_assignments organization_bus_child_assignments_created_by_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_bus_child_assignments
    ADD CONSTRAINT organization_bus_child_assignments_created_by_fkey FOREIGN KEY (created_by) REFERENCES public.users(id) ON DELETE SET NULL;


--
-- Name: organization_bus_child_assignments organization_bus_child_assignments_organization_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_bus_child_assignments
    ADD CONSTRAINT organization_bus_child_assignments_organization_id_fkey FOREIGN KEY (organization_id) REFERENCES public.organizations(id) ON DELETE CASCADE;


--
-- Name: organization_bus_child_assignments organization_bus_child_assignments_parent_organization_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_bus_child_assignments
    ADD CONSTRAINT organization_bus_child_assignments_parent_organization_id_fkey FOREIGN KEY (parent_organization_id) REFERENCES public.organization_parents(id) ON DELETE CASCADE;


--
-- Name: organization_bus_child_assignments organization_bus_child_assignments_updated_by_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_bus_child_assignments
    ADD CONSTRAINT organization_bus_child_assignments_updated_by_fkey FOREIGN KEY (updated_by) REFERENCES public.users(id) ON DELETE SET NULL;


--
-- Name: organization_classes organization_classes_class_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_classes
    ADD CONSTRAINT organization_classes_class_fkey FOREIGN KEY (class_id) REFERENCES public.global_classes(id) ON DELETE RESTRICT;


--
-- Name: organization_classes organization_classes_created_by_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_classes
    ADD CONSTRAINT organization_classes_created_by_fkey FOREIGN KEY (created_by) REFERENCES public.users(id);


--
-- Name: organization_classes organization_classes_org_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_classes
    ADD CONSTRAINT organization_classes_org_fkey FOREIGN KEY (organization_id) REFERENCES public.organizations(id) ON DELETE CASCADE;


--
-- Name: organization_classes organization_classes_promo_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_classes
    ADD CONSTRAINT organization_classes_promo_fkey FOREIGN KEY (next_promotion_class_id) REFERENCES public.organization_classes(id) ON DELETE SET NULL;


--
-- Name: organization_classes organization_classes_updated_by_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_classes
    ADD CONSTRAINT organization_classes_updated_by_fkey FOREIGN KEY (updated_by) REFERENCES public.users(id);


--
-- Name: organization_fee_assignments organization_fee_assignments_created_by_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_fee_assignments
    ADD CONSTRAINT organization_fee_assignments_created_by_fkey FOREIGN KEY (created_by) REFERENCES public.users(id) ON DELETE SET NULL;


--
-- Name: organization_fee_assignments organization_fee_assignments_fee_head_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_fee_assignments
    ADD CONSTRAINT organization_fee_assignments_fee_head_id_fkey FOREIGN KEY (fee_head_id) REFERENCES public.global_fee_heads(id) ON DELETE CASCADE;


--
-- Name: organization_fee_assignments organization_fee_assignments_organization_class_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_fee_assignments
    ADD CONSTRAINT organization_fee_assignments_organization_class_id_fkey FOREIGN KEY (organization_class_id) REFERENCES public.organization_classes(id) ON DELETE CASCADE;


--
-- Name: organization_fee_assignments organization_fee_assignments_organization_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_fee_assignments
    ADD CONSTRAINT organization_fee_assignments_organization_id_fkey FOREIGN KEY (organization_id) REFERENCES public.organizations(id) ON DELETE CASCADE;


--
-- Name: organization_fee_assignments organization_fee_assignments_session_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_fee_assignments
    ADD CONSTRAINT organization_fee_assignments_session_id_fkey FOREIGN KEY (session_id) REFERENCES public.global_sessions(id) ON DELETE CASCADE;


--
-- Name: organization_fee_assignments organization_fee_assignments_updated_by_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_fee_assignments
    ADD CONSTRAINT organization_fee_assignments_updated_by_fkey FOREIGN KEY (updated_by) REFERENCES public.users(id) ON DELETE SET NULL;


--
-- Name: organization_guardian_user_links organization_guardian_user_links_active_session_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_guardian_user_links
    ADD CONSTRAINT organization_guardian_user_links_active_session_id_fkey FOREIGN KEY (active_session_id) REFERENCES public.global_sessions(id) ON DELETE CASCADE;


--
-- Name: organization_guardian_user_links organization_guardian_user_links_approved_by_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_guardian_user_links
    ADD CONSTRAINT organization_guardian_user_links_approved_by_fkey FOREIGN KEY (approved_by) REFERENCES public.users(id) ON DELETE SET NULL;


--
-- Name: organization_guardian_user_links organization_guardian_user_links_guardian_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_guardian_user_links
    ADD CONSTRAINT organization_guardian_user_links_guardian_id_fkey FOREIGN KEY (guardian_id) REFERENCES public.organization_guardians(id) ON DELETE CASCADE;


--
-- Name: organization_guardian_user_links organization_guardian_user_links_organization_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_guardian_user_links
    ADD CONSTRAINT organization_guardian_user_links_organization_id_fkey FOREIGN KEY (organization_id) REFERENCES public.organizations(id) ON DELETE CASCADE;


--
-- Name: organization_guardian_user_links organization_guardian_user_links_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_guardian_user_links
    ADD CONSTRAINT organization_guardian_user_links_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.users(id) ON DELETE CASCADE;


--
-- Name: organization_guardians organization_guardians_active_session_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_guardians
    ADD CONSTRAINT organization_guardians_active_session_id_fkey FOREIGN KEY (active_session_id) REFERENCES public.global_sessions(id) ON DELETE CASCADE;


--
-- Name: organization_guardians organization_guardians_created_by_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_guardians
    ADD CONSTRAINT organization_guardians_created_by_fkey FOREIGN KEY (created_by) REFERENCES public.users(id) ON DELETE SET NULL;


--
-- Name: organization_guardians organization_guardians_organization_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_guardians
    ADD CONSTRAINT organization_guardians_organization_id_fkey FOREIGN KEY (organization_id) REFERENCES public.organizations(id) ON DELETE CASCADE;


--
-- Name: organization_guardians organization_guardians_relationship_type_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_guardians
    ADD CONSTRAINT organization_guardians_relationship_type_id_fkey FOREIGN KEY (relationship_type_id) REFERENCES public.global_relationship_types(id);


--
-- Name: organization_guardians organization_guardians_updated_by_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_guardians
    ADD CONSTRAINT organization_guardians_updated_by_fkey FOREIGN KEY (updated_by) REFERENCES public.users(id) ON DELETE SET NULL;


--
-- Name: organization_inquiries organization_inquiries_active_session_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_inquiries
    ADD CONSTRAINT organization_inquiries_active_session_id_fkey FOREIGN KEY (active_session_id) REFERENCES public.global_sessions(id) ON DELETE CASCADE;


--
-- Name: organization_inquiries organization_inquiries_class_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_inquiries
    ADD CONSTRAINT organization_inquiries_class_id_fkey FOREIGN KEY (class_id) REFERENCES public.organization_classes(id) ON DELETE SET NULL;


--
-- Name: organization_inquiries organization_inquiries_created_by_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_inquiries
    ADD CONSTRAINT organization_inquiries_created_by_fkey FOREIGN KEY (created_by) REFERENCES public.users(id) ON DELETE SET NULL;


--
-- Name: organization_inquiries organization_inquiries_organization_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_inquiries
    ADD CONSTRAINT organization_inquiries_organization_id_fkey FOREIGN KEY (organization_id) REFERENCES public.organizations(id) ON DELETE CASCADE;


--
-- Name: organization_inquiries organization_inquiries_updated_by_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_inquiries
    ADD CONSTRAINT organization_inquiries_updated_by_fkey FOREIGN KEY (updated_by) REFERENCES public.users(id) ON DELETE SET NULL;


--
-- Name: organization_languages organization_languages_created_by_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_languages
    ADD CONSTRAINT organization_languages_created_by_fkey FOREIGN KEY (created_by) REFERENCES public.users(id);


--
-- Name: organization_languages organization_languages_lang_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_languages
    ADD CONSTRAINT organization_languages_lang_fkey FOREIGN KEY (language_id) REFERENCES public.global_languages(id) ON DELETE CASCADE;


--
-- Name: organization_languages organization_languages_org_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_languages
    ADD CONSTRAINT organization_languages_org_fkey FOREIGN KEY (organization_id) REFERENCES public.organizations(id) ON DELETE CASCADE;


--
-- Name: organization_languages organization_languages_updated_by_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_languages
    ADD CONSTRAINT organization_languages_updated_by_fkey FOREIGN KEY (updated_by) REFERENCES public.users(id);


--
-- Name: organization_mediums organization_mediums_created_by_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_mediums
    ADD CONSTRAINT organization_mediums_created_by_fkey FOREIGN KEY (created_by) REFERENCES public.users(id);


--
-- Name: organization_mediums organization_mediums_medium_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_mediums
    ADD CONSTRAINT organization_mediums_medium_fkey FOREIGN KEY (medium_id) REFERENCES public.global_mediums(id) ON DELETE CASCADE;


--
-- Name: organization_mediums organization_mediums_org_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_mediums
    ADD CONSTRAINT organization_mediums_org_fkey FOREIGN KEY (organization_id) REFERENCES public.organizations(id) ON DELETE CASCADE;


--
-- Name: organization_mediums organization_mediums_updated_by_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_mediums
    ADD CONSTRAINT organization_mediums_updated_by_fkey FOREIGN KEY (updated_by) REFERENCES public.users(id);


--
-- Name: organization_parent_bus_staff_assignments organization_parent_bus_staff_assig_parent_organization_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_parent_bus_staff_assignments
    ADD CONSTRAINT organization_parent_bus_staff_assig_parent_organization_id_fkey FOREIGN KEY (parent_organization_id) REFERENCES public.organization_parents(id) ON DELETE CASCADE;


--
-- Name: organization_parent_bus_staff_assignments organization_parent_bus_staff_assignment_active_session_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_parent_bus_staff_assignments
    ADD CONSTRAINT organization_parent_bus_staff_assignment_active_session_id_fkey FOREIGN KEY (active_session_id) REFERENCES public.global_sessions(id) ON DELETE CASCADE;


--
-- Name: organization_parent_bus_staff_assignments organization_parent_bus_staff_assignments_bus_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_parent_bus_staff_assignments
    ADD CONSTRAINT organization_parent_bus_staff_assignments_bus_id_fkey FOREIGN KEY (bus_id) REFERENCES public.organization_parent_buses(id) ON DELETE CASCADE;


--
-- Name: organization_parent_bus_staff_assignments organization_parent_bus_staff_assignments_created_by_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_parent_bus_staff_assignments
    ADD CONSTRAINT organization_parent_bus_staff_assignments_created_by_fkey FOREIGN KEY (created_by) REFERENCES public.users(id) ON DELETE SET NULL;


--
-- Name: organization_parent_bus_staff_assignments organization_parent_bus_staff_assignments_staff_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_parent_bus_staff_assignments
    ADD CONSTRAINT organization_parent_bus_staff_assignments_staff_id_fkey FOREIGN KEY (staff_id) REFERENCES public.organization_parent_staff(id) ON DELETE CASCADE;


--
-- Name: organization_parent_bus_staff_assignments organization_parent_bus_staff_assignments_updated_by_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_parent_bus_staff_assignments
    ADD CONSTRAINT organization_parent_bus_staff_assignments_updated_by_fkey FOREIGN KEY (updated_by) REFERENCES public.users(id) ON DELETE SET NULL;


--
-- Name: organization_parent_buses organization_parent_buses_active_session_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_parent_buses
    ADD CONSTRAINT organization_parent_buses_active_session_id_fkey FOREIGN KEY (active_session_id) REFERENCES public.global_sessions(id) ON DELETE CASCADE;


--
-- Name: organization_parent_buses organization_parent_buses_created_by_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_parent_buses
    ADD CONSTRAINT organization_parent_buses_created_by_fkey FOREIGN KEY (created_by) REFERENCES public.users(id) ON DELETE SET NULL;


--
-- Name: organization_parent_buses organization_parent_buses_parent_organization_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_parent_buses
    ADD CONSTRAINT organization_parent_buses_parent_organization_id_fkey FOREIGN KEY (parent_organization_id) REFERENCES public.organization_parents(id) ON DELETE CASCADE;


--
-- Name: organization_parent_buses organization_parent_buses_updated_by_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_parent_buses
    ADD CONSTRAINT organization_parent_buses_updated_by_fkey FOREIGN KEY (updated_by) REFERENCES public.users(id) ON DELETE SET NULL;


--
-- Name: organization_parent_expenses organization_parent_expenses_active_session_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_parent_expenses
    ADD CONSTRAINT organization_parent_expenses_active_session_id_fkey FOREIGN KEY (active_session_id) REFERENCES public.global_sessions(id) ON DELETE CASCADE;


--
-- Name: organization_parent_expenses organization_parent_expenses_created_by_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_parent_expenses
    ADD CONSTRAINT organization_parent_expenses_created_by_fkey FOREIGN KEY (created_by) REFERENCES public.users(id) ON DELETE SET NULL;


--
-- Name: organization_parent_expenses organization_parent_expenses_expense_type_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_parent_expenses
    ADD CONSTRAINT organization_parent_expenses_expense_type_id_fkey FOREIGN KEY (expense_type_id) REFERENCES public.global_expense_types(id);


--
-- Name: organization_parent_expenses organization_parent_expenses_parent_organization_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_parent_expenses
    ADD CONSTRAINT organization_parent_expenses_parent_organization_id_fkey FOREIGN KEY (parent_organization_id) REFERENCES public.organization_parents(id) ON DELETE CASCADE;


--
-- Name: organization_parent_expenses organization_parent_expenses_updated_by_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_parent_expenses
    ADD CONSTRAINT organization_parent_expenses_updated_by_fkey FOREIGN KEY (updated_by) REFERENCES public.users(id) ON DELETE SET NULL;


--
-- Name: organization_parent_staff organization_parent_staff_active_session_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_parent_staff
    ADD CONSTRAINT organization_parent_staff_active_session_id_fkey FOREIGN KEY (active_session_id) REFERENCES public.global_sessions(id) ON DELETE CASCADE;


--
-- Name: organization_parent_staff organization_parent_staff_address_area_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_parent_staff
    ADD CONSTRAINT organization_parent_staff_address_area_id_fkey FOREIGN KEY (address_area_id) REFERENCES public.global_areas(id);


--
-- Name: organization_parent_staff_attendance organization_parent_staff_attendanc_parent_organization_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_parent_staff_attendance
    ADD CONSTRAINT organization_parent_staff_attendanc_parent_organization_id_fkey FOREIGN KEY (parent_organization_id) REFERENCES public.organization_parents(id) ON DELETE CASCADE;


--
-- Name: organization_parent_staff_attendance organization_parent_staff_attendance_active_session_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_parent_staff_attendance
    ADD CONSTRAINT organization_parent_staff_attendance_active_session_id_fkey FOREIGN KEY (active_session_id) REFERENCES public.global_sessions(id) ON DELETE CASCADE;


--
-- Name: organization_parent_staff_attendance organization_parent_staff_attendance_created_by_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_parent_staff_attendance
    ADD CONSTRAINT organization_parent_staff_attendance_created_by_fkey FOREIGN KEY (created_by) REFERENCES public.users(id) ON DELETE SET NULL;


--
-- Name: organization_parent_staff_attendance organization_parent_staff_attendance_staff_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_parent_staff_attendance
    ADD CONSTRAINT organization_parent_staff_attendance_staff_id_fkey FOREIGN KEY (staff_id) REFERENCES public.organization_parent_staff(id) ON DELETE CASCADE;


--
-- Name: organization_parent_staff_attendance organization_parent_staff_attendance_status_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_parent_staff_attendance
    ADD CONSTRAINT organization_parent_staff_attendance_status_id_fkey FOREIGN KEY (status_id) REFERENCES public.global_attendance_status(id);


--
-- Name: organization_parent_staff_attendance organization_parent_staff_attendance_updated_by_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_parent_staff_attendance
    ADD CONSTRAINT organization_parent_staff_attendance_updated_by_fkey FOREIGN KEY (updated_by) REFERENCES public.users(id) ON DELETE SET NULL;


--
-- Name: organization_parent_staff_bus_enrollments organization_parent_staff_bus_enrol_parent_organization_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_parent_staff_bus_enrollments
    ADD CONSTRAINT organization_parent_staff_bus_enrol_parent_organization_id_fkey FOREIGN KEY (parent_organization_id) REFERENCES public.organization_parents(id) ON DELETE CASCADE;


--
-- Name: organization_parent_staff_bus_enrollments organization_parent_staff_bus_enrollment_active_session_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_parent_staff_bus_enrollments
    ADD CONSTRAINT organization_parent_staff_bus_enrollment_active_session_id_fkey FOREIGN KEY (active_session_id) REFERENCES public.global_sessions(id) ON DELETE CASCADE;


--
-- Name: organization_parent_staff_bus_enrollments organization_parent_staff_bus_enrollments_bus_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_parent_staff_bus_enrollments
    ADD CONSTRAINT organization_parent_staff_bus_enrollments_bus_id_fkey FOREIGN KEY (bus_id) REFERENCES public.organization_parent_buses(id) ON DELETE CASCADE;


--
-- Name: organization_parent_staff_bus_enrollments organization_parent_staff_bus_enrollments_created_by_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_parent_staff_bus_enrollments
    ADD CONSTRAINT organization_parent_staff_bus_enrollments_created_by_fkey FOREIGN KEY (created_by) REFERENCES public.users(id) ON DELETE SET NULL;


--
-- Name: organization_parent_staff_bus_enrollments organization_parent_staff_bus_enrollments_organization_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_parent_staff_bus_enrollments
    ADD CONSTRAINT organization_parent_staff_bus_enrollments_organization_id_fkey FOREIGN KEY (organization_id) REFERENCES public.organizations(id) ON DELETE CASCADE;


--
-- Name: organization_parent_staff_bus_enrollments organization_parent_staff_bus_enrollments_staff_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_parent_staff_bus_enrollments
    ADD CONSTRAINT organization_parent_staff_bus_enrollments_staff_id_fkey FOREIGN KEY (staff_id) REFERENCES public.organization_parent_staff(id) ON DELETE CASCADE;


--
-- Name: organization_parent_staff_bus_enrollments organization_parent_staff_bus_enrollments_updated_by_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_parent_staff_bus_enrollments
    ADD CONSTRAINT organization_parent_staff_bus_enrollments_updated_by_fkey FOREIGN KEY (updated_by) REFERENCES public.users(id) ON DELETE SET NULL;


--
-- Name: organization_parent_staff_bus_fares organization_parent_staff_bus_fares_active_session_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_parent_staff_bus_fares
    ADD CONSTRAINT organization_parent_staff_bus_fares_active_session_id_fkey FOREIGN KEY (active_session_id) REFERENCES public.global_sessions(id) ON DELETE CASCADE;


--
-- Name: organization_parent_staff_bus_fares organization_parent_staff_bus_fares_bus_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_parent_staff_bus_fares
    ADD CONSTRAINT organization_parent_staff_bus_fares_bus_id_fkey FOREIGN KEY (bus_id) REFERENCES public.organization_parent_buses(id) ON DELETE CASCADE;


--
-- Name: organization_parent_staff_bus_fares organization_parent_staff_bus_fares_created_by_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_parent_staff_bus_fares
    ADD CONSTRAINT organization_parent_staff_bus_fares_created_by_fkey FOREIGN KEY (created_by) REFERENCES public.users(id) ON DELETE SET NULL;


--
-- Name: organization_parent_staff_bus_fares organization_parent_staff_bus_fares_parent_organization_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_parent_staff_bus_fares
    ADD CONSTRAINT organization_parent_staff_bus_fares_parent_organization_id_fkey FOREIGN KEY (parent_organization_id) REFERENCES public.organization_parents(id) ON DELETE CASCADE;


--
-- Name: organization_parent_staff_bus_fares organization_parent_staff_bus_fares_staff_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_parent_staff_bus_fares
    ADD CONSTRAINT organization_parent_staff_bus_fares_staff_id_fkey FOREIGN KEY (staff_id) REFERENCES public.organization_parent_staff(id) ON DELETE CASCADE;


--
-- Name: organization_parent_staff_bus_fares organization_parent_staff_bus_fares_updated_by_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_parent_staff_bus_fares
    ADD CONSTRAINT organization_parent_staff_bus_fares_updated_by_fkey FOREIGN KEY (updated_by) REFERENCES public.users(id) ON DELETE SET NULL;


--
-- Name: organization_parent_staff organization_parent_staff_bus_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_parent_staff
    ADD CONSTRAINT organization_parent_staff_bus_id_fkey FOREIGN KEY (bus_id) REFERENCES public.organization_parent_buses(id) ON DELETE SET NULL;


--
-- Name: organization_parent_staff organization_parent_staff_created_by_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_parent_staff
    ADD CONSTRAINT organization_parent_staff_created_by_fkey FOREIGN KEY (created_by) REFERENCES public.users(id) ON DELETE SET NULL;


--
-- Name: organization_parent_staff_leave_quotas organization_parent_staff_leave_quo_parent_organization_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_parent_staff_leave_quotas
    ADD CONSTRAINT organization_parent_staff_leave_quo_parent_organization_id_fkey FOREIGN KEY (parent_organization_id) REFERENCES public.organization_parents(id) ON DELETE CASCADE;


--
-- Name: organization_parent_staff_leave_quotas organization_parent_staff_leave_quotas_active_session_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_parent_staff_leave_quotas
    ADD CONSTRAINT organization_parent_staff_leave_quotas_active_session_id_fkey FOREIGN KEY (active_session_id) REFERENCES public.global_sessions(id) ON DELETE CASCADE;


--
-- Name: organization_parent_staff_leave_quotas organization_parent_staff_leave_quotas_created_by_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_parent_staff_leave_quotas
    ADD CONSTRAINT organization_parent_staff_leave_quotas_created_by_fkey FOREIGN KEY (created_by) REFERENCES public.users(id) ON DELETE SET NULL;


--
-- Name: organization_parent_staff_leave_quotas organization_parent_staff_leave_quotas_organization_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_parent_staff_leave_quotas
    ADD CONSTRAINT organization_parent_staff_leave_quotas_organization_id_fkey FOREIGN KEY (organization_id) REFERENCES public.organizations(id) ON DELETE CASCADE;


--
-- Name: organization_parent_staff_leave_quotas organization_parent_staff_leave_quotas_staff_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_parent_staff_leave_quotas
    ADD CONSTRAINT organization_parent_staff_leave_quotas_staff_id_fkey FOREIGN KEY (staff_id) REFERENCES public.organization_parent_staff(id) ON DELETE CASCADE;


--
-- Name: organization_parent_staff_leave_quotas organization_parent_staff_leave_quotas_updated_by_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_parent_staff_leave_quotas
    ADD CONSTRAINT organization_parent_staff_leave_quotas_updated_by_fkey FOREIGN KEY (updated_by) REFERENCES public.users(id) ON DELETE SET NULL;


--
-- Name: organization_parent_staff organization_parent_staff_parent_organization_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_parent_staff
    ADD CONSTRAINT organization_parent_staff_parent_organization_id_fkey FOREIGN KEY (parent_organization_id) REFERENCES public.organization_parents(id) ON DELETE CASCADE;


--
-- Name: organization_parent_staff organization_parent_staff_role_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_parent_staff
    ADD CONSTRAINT organization_parent_staff_role_id_fkey FOREIGN KEY (role_id) REFERENCES public.global_staff_roles(id);


--
-- Name: organization_parent_staff_salaries organization_parent_staff_salaries_active_session_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_parent_staff_salaries
    ADD CONSTRAINT organization_parent_staff_salaries_active_session_id_fkey FOREIGN KEY (active_session_id) REFERENCES public.global_sessions(id) ON DELETE CASCADE;


--
-- Name: organization_parent_staff_salaries organization_parent_staff_salaries_created_by_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_parent_staff_salaries
    ADD CONSTRAINT organization_parent_staff_salaries_created_by_fkey FOREIGN KEY (created_by) REFERENCES public.users(id) ON DELETE SET NULL;


--
-- Name: organization_parent_staff_salaries organization_parent_staff_salaries_parent_organization_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_parent_staff_salaries
    ADD CONSTRAINT organization_parent_staff_salaries_parent_organization_id_fkey FOREIGN KEY (parent_organization_id) REFERENCES public.organization_parents(id) ON DELETE CASCADE;


--
-- Name: organization_parent_staff_salaries organization_parent_staff_salaries_staff_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_parent_staff_salaries
    ADD CONSTRAINT organization_parent_staff_salaries_staff_id_fkey FOREIGN KEY (staff_id) REFERENCES public.organization_parent_staff(id) ON DELETE CASCADE;


--
-- Name: organization_parent_staff_salaries organization_parent_staff_salaries_updated_by_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_parent_staff_salaries
    ADD CONSTRAINT organization_parent_staff_salaries_updated_by_fkey FOREIGN KEY (updated_by) REFERENCES public.users(id) ON DELETE SET NULL;


--
-- Name: organization_parent_staff_salary_payments organization_parent_staff_salary_p_parent_organization_id_fkey1; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_parent_staff_salary_payments
    ADD CONSTRAINT organization_parent_staff_salary_p_parent_organization_id_fkey1 FOREIGN KEY (parent_organization_id) REFERENCES public.organization_parents(id) ON DELETE CASCADE;


--
-- Name: organization_parent_staff_salary_payouts organization_parent_staff_salary_pa_parent_organization_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_parent_staff_salary_payouts
    ADD CONSTRAINT organization_parent_staff_salary_pa_parent_organization_id_fkey FOREIGN KEY (parent_organization_id) REFERENCES public.organization_parents(id) ON DELETE CASCADE;


--
-- Name: organization_parent_staff_salary_payments organization_parent_staff_salary_paym_cash_paid_by_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_parent_staff_salary_payments
    ADD CONSTRAINT organization_parent_staff_salary_paym_cash_paid_by_user_id_fkey FOREIGN KEY (cash_paid_by_user_id) REFERENCES public.users(id) ON DELETE SET NULL;


--
-- Name: organization_parent_staff_salary_payments organization_parent_staff_salary_payment_active_session_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_parent_staff_salary_payments
    ADD CONSTRAINT organization_parent_staff_salary_payment_active_session_id_fkey FOREIGN KEY (active_session_id) REFERENCES public.global_sessions(id) ON DELETE CASCADE;


--
-- Name: organization_parent_staff_salary_payments organization_parent_staff_salary_payments_created_by_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_parent_staff_salary_payments
    ADD CONSTRAINT organization_parent_staff_salary_payments_created_by_fkey FOREIGN KEY (created_by) REFERENCES public.users(id) ON DELETE SET NULL;


--
-- Name: organization_parent_staff_salary_payments organization_parent_staff_salary_payments_salary_payout_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_parent_staff_salary_payments
    ADD CONSTRAINT organization_parent_staff_salary_payments_salary_payout_id_fkey FOREIGN KEY (salary_payout_id) REFERENCES public.organization_parent_staff_salary_payouts(id);


--
-- Name: organization_parent_staff_salary_payments organization_parent_staff_salary_payments_staff_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_parent_staff_salary_payments
    ADD CONSTRAINT organization_parent_staff_salary_payments_staff_id_fkey FOREIGN KEY (staff_id) REFERENCES public.organization_parent_staff(id) ON DELETE CASCADE;


--
-- Name: organization_parent_staff_salary_payments organization_parent_staff_salary_payments_updated_by_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_parent_staff_salary_payments
    ADD CONSTRAINT organization_parent_staff_salary_payments_updated_by_fkey FOREIGN KEY (updated_by) REFERENCES public.users(id) ON DELETE SET NULL;


--
-- Name: organization_parent_staff_salary_payouts organization_parent_staff_salary_payouts_active_session_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_parent_staff_salary_payouts
    ADD CONSTRAINT organization_parent_staff_salary_payouts_active_session_id_fkey FOREIGN KEY (active_session_id) REFERENCES public.global_sessions(id) ON DELETE CASCADE;


--
-- Name: organization_parent_staff_salary_payouts organization_parent_staff_salary_payouts_locked_by_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_parent_staff_salary_payouts
    ADD CONSTRAINT organization_parent_staff_salary_payouts_locked_by_fkey FOREIGN KEY (locked_by) REFERENCES public.users(id) ON DELETE SET NULL;


--
-- Name: organization_parent_staff_salary_payouts organization_parent_staff_salary_payouts_staff_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_parent_staff_salary_payouts
    ADD CONSTRAINT organization_parent_staff_salary_payouts_staff_id_fkey FOREIGN KEY (staff_id) REFERENCES public.organization_parent_staff(id) ON DELETE CASCADE;


--
-- Name: organization_parent_staff organization_parent_staff_subject_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_parent_staff
    ADD CONSTRAINT organization_parent_staff_subject_id_fkey FOREIGN KEY (subject_id) REFERENCES public.global_subjects(id);


--
-- Name: organization_parent_staff organization_parent_staff_updated_by_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_parent_staff
    ADD CONSTRAINT organization_parent_staff_updated_by_fkey FOREIGN KEY (updated_by) REFERENCES public.users(id) ON DELETE SET NULL;


--
-- Name: organization_parent_staff_user_links organization_parent_staff_user_link_parent_organization_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_parent_staff_user_links
    ADD CONSTRAINT organization_parent_staff_user_link_parent_organization_id_fkey FOREIGN KEY (parent_organization_id) REFERENCES public.organization_parents(id) ON DELETE CASCADE;


--
-- Name: organization_parent_staff_user_links organization_parent_staff_user_links_active_session_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_parent_staff_user_links
    ADD CONSTRAINT organization_parent_staff_user_links_active_session_id_fkey FOREIGN KEY (active_session_id) REFERENCES public.global_sessions(id) ON DELETE CASCADE;


--
-- Name: organization_parent_staff_user_links organization_parent_staff_user_links_approved_by_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_parent_staff_user_links
    ADD CONSTRAINT organization_parent_staff_user_links_approved_by_fkey FOREIGN KEY (approved_by) REFERENCES public.users(id) ON DELETE SET NULL;


--
-- Name: organization_parent_staff_user_links organization_parent_staff_user_links_staff_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_parent_staff_user_links
    ADD CONSTRAINT organization_parent_staff_user_links_staff_id_fkey FOREIGN KEY (staff_id) REFERENCES public.organization_parent_staff(id) ON DELETE CASCADE;


--
-- Name: organization_parent_staff_user_links organization_parent_staff_user_links_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_parent_staff_user_links
    ADD CONSTRAINT organization_parent_staff_user_links_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.users(id) ON DELETE CASCADE;


--
-- Name: organization_parents_profiles organization_parents_profiles_parent_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_parents_profiles
    ADD CONSTRAINT organization_parents_profiles_parent_id_fkey FOREIGN KEY (parent_organization_id) REFERENCES public.organization_parents(id) ON DELETE CASCADE;


--
-- Name: organization_parents_profiles organization_parents_profiles_session_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_parents_profiles
    ADD CONSTRAINT organization_parents_profiles_session_fkey FOREIGN KEY (active_session_id) REFERENCES public.global_sessions(id) ON DELETE SET NULL;


--
-- Name: organization_parents_session_tokens organization_parents_session_tokens_user_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_parents_session_tokens
    ADD CONSTRAINT organization_parents_session_tokens_user_fkey FOREIGN KEY (parent_organization_user_id) REFERENCES public.organization_parents_users(id) ON DELETE CASCADE;


--
-- Name: organization_parents organization_parents_type_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_parents
    ADD CONSTRAINT organization_parents_type_id_fkey FOREIGN KEY (type_id) REFERENCES public.global_organization_parent_types(id);


--
-- Name: organization_parents_users organization_parents_users_parent_organization_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_parents_users
    ADD CONSTRAINT organization_parents_users_parent_organization_id_fkey FOREIGN KEY (parent_organization_id) REFERENCES public.organization_parents(id) ON DELETE CASCADE;


--
-- Name: organization_parents_users organization_parents_users_role_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_parents_users
    ADD CONSTRAINT organization_parents_users_role_id_fkey FOREIGN KEY (role_id) REFERENCES public.global_staff_roles(id) ON DELETE SET NULL;


--
-- Name: organization_parents_users organization_parents_users_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_parents_users
    ADD CONSTRAINT organization_parents_users_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.users(id) ON DELETE CASCADE;


--
-- Name: organization_periods organization_periods_created_by_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_periods
    ADD CONSTRAINT organization_periods_created_by_fkey FOREIGN KEY (created_by) REFERENCES public.users(id) ON DELETE SET NULL;


--
-- Name: organization_periods organization_periods_organization_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_periods
    ADD CONSTRAINT organization_periods_organization_id_fkey FOREIGN KEY (organization_id) REFERENCES public.organizations(id) ON DELETE CASCADE;


--
-- Name: organization_periods organization_periods_session_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_periods
    ADD CONSTRAINT organization_periods_session_id_fkey FOREIGN KEY (session_id) REFERENCES public.global_sessions(id) ON DELETE CASCADE;


--
-- Name: organization_periods organization_periods_updated_by_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_periods
    ADD CONSTRAINT organization_periods_updated_by_fkey FOREIGN KEY (updated_by) REFERENCES public.users(id) ON DELETE SET NULL;


--
-- Name: organization_profiles organization_profiles_org_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_profiles
    ADD CONSTRAINT organization_profiles_org_id_fkey FOREIGN KEY (organization_id) REFERENCES public.organizations(id) ON DELETE CASCADE;


--
-- Name: organization_profiles organization_profiles_session_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_profiles
    ADD CONSTRAINT organization_profiles_session_fkey FOREIGN KEY (active_session_id) REFERENCES public.global_sessions(id) ON DELETE SET NULL;


--
-- Name: organization_sections organization_sections_class_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_sections
    ADD CONSTRAINT organization_sections_class_fkey FOREIGN KEY (organization_class_id) REFERENCES public.organization_classes(id) ON DELETE CASCADE;


--
-- Name: organization_sections organization_sections_created_by_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_sections
    ADD CONSTRAINT organization_sections_created_by_fkey FOREIGN KEY (created_by) REFERENCES public.users(id);


--
-- Name: organization_sections organization_sections_updated_by_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_sections
    ADD CONSTRAINT organization_sections_updated_by_fkey FOREIGN KEY (updated_by) REFERENCES public.users(id);


--
-- Name: organization_session_classes organization_session_classes_created_by_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_session_classes
    ADD CONSTRAINT organization_session_classes_created_by_fkey FOREIGN KEY (created_by) REFERENCES public.users(id);


--
-- Name: organization_session_classes organization_session_classes_updated_by_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_session_classes
    ADD CONSTRAINT organization_session_classes_updated_by_fkey FOREIGN KEY (updated_by) REFERENCES public.users(id);


--
-- Name: organization_session_sections organization_session_sections_created_by_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_session_sections
    ADD CONSTRAINT organization_session_sections_created_by_fkey FOREIGN KEY (created_by) REFERENCES public.users(id);


--
-- Name: organization_session_sections organization_session_sections_updated_by_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_session_sections
    ADD CONSTRAINT organization_session_sections_updated_by_fkey FOREIGN KEY (updated_by) REFERENCES public.users(id);


--
-- Name: organization_session_subjects organization_session_subjects_created_by_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_session_subjects
    ADD CONSTRAINT organization_session_subjects_created_by_fkey FOREIGN KEY (created_by) REFERENCES public.users(id);


--
-- Name: organization_session_subjects organization_session_subjects_updated_by_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_session_subjects
    ADD CONSTRAINT organization_session_subjects_updated_by_fkey FOREIGN KEY (updated_by) REFERENCES public.users(id);


--
-- Name: organization_session_tokens organization_session_tokens_org_user_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_session_tokens
    ADD CONSTRAINT organization_session_tokens_org_user_fkey FOREIGN KEY (organization_user_id) REFERENCES public.organization_users(id) ON DELETE CASCADE;


--
-- Name: organization_student_additional_fees organization_student_additional_fees_active_session_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_student_additional_fees
    ADD CONSTRAINT organization_student_additional_fees_active_session_id_fkey FOREIGN KEY (active_session_id) REFERENCES public.global_sessions(id) ON DELETE CASCADE;


--
-- Name: organization_student_additional_fees organization_student_additional_fees_created_by_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_student_additional_fees
    ADD CONSTRAINT organization_student_additional_fees_created_by_fkey FOREIGN KEY (created_by) REFERENCES public.users(id) ON DELETE SET NULL;


--
-- Name: organization_student_additional_fees organization_student_additional_fees_global_fee_head_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_student_additional_fees
    ADD CONSTRAINT organization_student_additional_fees_global_fee_head_id_fkey FOREIGN KEY (global_fee_head_id) REFERENCES public.global_fee_heads(id);


--
-- Name: organization_student_additional_fees organization_student_additional_fees_organization_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_student_additional_fees
    ADD CONSTRAINT organization_student_additional_fees_organization_id_fkey FOREIGN KEY (organization_id) REFERENCES public.organizations(id) ON DELETE CASCADE;


--
-- Name: organization_student_additional_fees organization_student_additional_fees_student_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_student_additional_fees
    ADD CONSTRAINT organization_student_additional_fees_student_id_fkey FOREIGN KEY (student_id) REFERENCES public.organization_students(id) ON DELETE CASCADE;


--
-- Name: organization_student_additional_fees organization_student_additional_fees_updated_by_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_student_additional_fees
    ADD CONSTRAINT organization_student_additional_fees_updated_by_fkey FOREIGN KEY (updated_by) REFERENCES public.users(id) ON DELETE SET NULL;


--
-- Name: organization_student_bus_assignments organization_student_bus_assignments_active_session_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_student_bus_assignments
    ADD CONSTRAINT organization_student_bus_assignments_active_session_id_fkey FOREIGN KEY (active_session_id) REFERENCES public.global_sessions(id) ON DELETE CASCADE;


--
-- Name: organization_student_bus_assignments organization_student_bus_assignments_bus_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_student_bus_assignments
    ADD CONSTRAINT organization_student_bus_assignments_bus_id_fkey FOREIGN KEY (bus_id) REFERENCES public.organization_parent_buses(id) ON DELETE CASCADE;


--
-- Name: organization_student_bus_assignments organization_student_bus_assignments_created_by_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_student_bus_assignments
    ADD CONSTRAINT organization_student_bus_assignments_created_by_fkey FOREIGN KEY (created_by) REFERENCES public.users(id) ON DELETE SET NULL;


--
-- Name: organization_student_bus_assignments organization_student_bus_assignments_organization_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_student_bus_assignments
    ADD CONSTRAINT organization_student_bus_assignments_organization_id_fkey FOREIGN KEY (organization_id) REFERENCES public.organizations(id) ON DELETE CASCADE;


--
-- Name: organization_student_bus_assignments organization_student_bus_assignments_student_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_student_bus_assignments
    ADD CONSTRAINT organization_student_bus_assignments_student_id_fkey FOREIGN KEY (student_id) REFERENCES public.organization_students(id) ON DELETE CASCADE;


--
-- Name: organization_student_bus_assignments organization_student_bus_assignments_updated_by_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_student_bus_assignments
    ADD CONSTRAINT organization_student_bus_assignments_updated_by_fkey FOREIGN KEY (updated_by) REFERENCES public.users(id) ON DELETE SET NULL;


--
-- Name: organization_student_enrollments organization_student_enrollments_active_session_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_student_enrollments
    ADD CONSTRAINT organization_student_enrollments_active_session_id_fkey FOREIGN KEY (active_session_id) REFERENCES public.global_sessions(id) ON DELETE CASCADE;


--
-- Name: organization_student_enrollments organization_student_enrollments_class_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_student_enrollments
    ADD CONSTRAINT organization_student_enrollments_class_id_fkey FOREIGN KEY (class_id) REFERENCES public.organization_classes(id);


--
-- Name: organization_student_enrollments organization_student_enrollments_created_by_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_student_enrollments
    ADD CONSTRAINT organization_student_enrollments_created_by_fkey FOREIGN KEY (created_by) REFERENCES public.users(id) ON DELETE SET NULL;


--
-- Name: organization_student_enrollments organization_student_enrollments_organization_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_student_enrollments
    ADD CONSTRAINT organization_student_enrollments_organization_id_fkey FOREIGN KEY (organization_id) REFERENCES public.organizations(id) ON DELETE CASCADE;


--
-- Name: organization_student_enrollments organization_student_enrollments_result_status_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_student_enrollments
    ADD CONSTRAINT organization_student_enrollments_result_status_id_fkey FOREIGN KEY (result_status_id) REFERENCES public.global_result_status(id);


--
-- Name: organization_student_enrollments organization_student_enrollments_section_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_student_enrollments
    ADD CONSTRAINT organization_student_enrollments_section_id_fkey FOREIGN KEY (section_id) REFERENCES public.organization_sections(id);


--
-- Name: organization_student_enrollments organization_student_enrollments_student_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_student_enrollments
    ADD CONSTRAINT organization_student_enrollments_student_id_fkey FOREIGN KEY (student_id) REFERENCES public.organization_students(id) ON DELETE CASCADE;


--
-- Name: organization_student_enrollments organization_student_enrollments_updated_by_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_student_enrollments
    ADD CONSTRAINT organization_student_enrollments_updated_by_fkey FOREIGN KEY (updated_by) REFERENCES public.users(id) ON DELETE SET NULL;


--
-- Name: organization_student_fee_payments organization_student_fee_payments_active_session_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_student_fee_payments
    ADD CONSTRAINT organization_student_fee_payments_active_session_id_fkey FOREIGN KEY (active_session_id) REFERENCES public.global_sessions(id) ON DELETE CASCADE;


--
-- Name: organization_student_fee_payments organization_student_fee_payments_cash_received_by_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_student_fee_payments
    ADD CONSTRAINT organization_student_fee_payments_cash_received_by_user_id_fkey FOREIGN KEY (cash_received_by_user_id) REFERENCES public.users(id) ON DELETE SET NULL;


--
-- Name: organization_student_fee_payments organization_student_fee_payments_created_by_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_student_fee_payments
    ADD CONSTRAINT organization_student_fee_payments_created_by_fkey FOREIGN KEY (created_by) REFERENCES public.users(id) ON DELETE SET NULL;


--
-- Name: organization_student_fee_payments organization_student_fee_payments_organization_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_student_fee_payments
    ADD CONSTRAINT organization_student_fee_payments_organization_id_fkey FOREIGN KEY (organization_id) REFERENCES public.organizations(id) ON DELETE CASCADE;


--
-- Name: organization_student_fee_payments organization_student_fee_payments_student_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_student_fee_payments
    ADD CONSTRAINT organization_student_fee_payments_student_id_fkey FOREIGN KEY (student_id) REFERENCES public.organization_students(id) ON DELETE CASCADE;


--
-- Name: organization_student_fee_payments organization_student_fee_payments_updated_by_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_student_fee_payments
    ADD CONSTRAINT organization_student_fee_payments_updated_by_fkey FOREIGN KEY (updated_by) REFERENCES public.users(id) ON DELETE SET NULL;


--
-- Name: organization_student_homeworks organization_student_homeworks_active_session_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_student_homeworks
    ADD CONSTRAINT organization_student_homeworks_active_session_id_fkey FOREIGN KEY (active_session_id) REFERENCES public.global_sessions(id) ON DELETE CASCADE;


--
-- Name: organization_student_homeworks organization_student_homeworks_class_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_student_homeworks
    ADD CONSTRAINT organization_student_homeworks_class_id_fkey FOREIGN KEY (class_id) REFERENCES public.organization_classes(id) ON DELETE CASCADE;


--
-- Name: organization_student_homeworks organization_student_homeworks_created_by_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_student_homeworks
    ADD CONSTRAINT organization_student_homeworks_created_by_fkey FOREIGN KEY (created_by) REFERENCES public.users(id) ON DELETE SET NULL;


--
-- Name: organization_student_homeworks organization_student_homeworks_organization_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_student_homeworks
    ADD CONSTRAINT organization_student_homeworks_organization_id_fkey FOREIGN KEY (organization_id) REFERENCES public.organizations(id) ON DELETE CASCADE;


--
-- Name: organization_student_homeworks organization_student_homeworks_section_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_student_homeworks
    ADD CONSTRAINT organization_student_homeworks_section_id_fkey FOREIGN KEY (section_id) REFERENCES public.organization_sections(id) ON DELETE CASCADE;


--
-- Name: organization_student_homeworks organization_student_homeworks_subject_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_student_homeworks
    ADD CONSTRAINT organization_student_homeworks_subject_id_fkey FOREIGN KEY (subject_id) REFERENCES public.global_subjects(id);


--
-- Name: organization_student_homeworks organization_student_homeworks_updated_by_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_student_homeworks
    ADD CONSTRAINT organization_student_homeworks_updated_by_fkey FOREIGN KEY (updated_by) REFERENCES public.users(id) ON DELETE SET NULL;


--
-- Name: organization_student_marks organization_student_marks_active_session_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_student_marks
    ADD CONSTRAINT organization_student_marks_active_session_id_fkey FOREIGN KEY (active_session_id) REFERENCES public.global_sessions(id) ON DELETE CASCADE;


--
-- Name: organization_student_marks organization_student_marks_class_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_student_marks
    ADD CONSTRAINT organization_student_marks_class_id_fkey FOREIGN KEY (class_id) REFERENCES public.organization_classes(id) ON DELETE CASCADE;


--
-- Name: organization_student_marks organization_student_marks_created_by_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_student_marks
    ADD CONSTRAINT organization_student_marks_created_by_fkey FOREIGN KEY (created_by) REFERENCES public.users(id) ON DELETE SET NULL;


--
-- Name: organization_student_marks organization_student_marks_organization_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_student_marks
    ADD CONSTRAINT organization_student_marks_organization_id_fkey FOREIGN KEY (organization_id) REFERENCES public.organizations(id) ON DELETE CASCADE;


--
-- Name: organization_student_marks organization_student_marks_student_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_student_marks
    ADD CONSTRAINT organization_student_marks_student_id_fkey FOREIGN KEY (student_id) REFERENCES public.organization_students(id) ON DELETE CASCADE;


--
-- Name: organization_student_marks organization_student_marks_updated_by_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_student_marks
    ADD CONSTRAINT organization_student_marks_updated_by_fkey FOREIGN KEY (updated_by) REFERENCES public.users(id) ON DELETE SET NULL;


--
-- Name: organization_student_user_links organization_student_user_links_active_session_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_student_user_links
    ADD CONSTRAINT organization_student_user_links_active_session_id_fkey FOREIGN KEY (active_session_id) REFERENCES public.global_sessions(id) ON DELETE CASCADE;


--
-- Name: organization_student_user_links organization_student_user_links_approved_by_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_student_user_links
    ADD CONSTRAINT organization_student_user_links_approved_by_fkey FOREIGN KEY (approved_by) REFERENCES public.users(id) ON DELETE SET NULL;


--
-- Name: organization_student_user_links organization_student_user_links_organization_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_student_user_links
    ADD CONSTRAINT organization_student_user_links_organization_id_fkey FOREIGN KEY (organization_id) REFERENCES public.organizations(id) ON DELETE CASCADE;


--
-- Name: organization_student_user_links organization_student_user_links_student_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_student_user_links
    ADD CONSTRAINT organization_student_user_links_student_id_fkey FOREIGN KEY (student_id) REFERENCES public.organization_students(id) ON DELETE CASCADE;


--
-- Name: organization_student_user_links organization_student_user_links_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_student_user_links
    ADD CONSTRAINT organization_student_user_links_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.users(id) ON DELETE CASCADE;


--
-- Name: organization_students organization_students_active_session_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_students
    ADD CONSTRAINT organization_students_active_session_id_fkey FOREIGN KEY (active_session_id) REFERENCES public.global_sessions(id) ON DELETE CASCADE;


--
-- Name: organization_students organization_students_address_area_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_students
    ADD CONSTRAINT organization_students_address_area_id_fkey FOREIGN KEY (address_area_id) REFERENCES public.global_areas(id);


--
-- Name: organization_students organization_students_blood_group_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_students
    ADD CONSTRAINT organization_students_blood_group_id_fkey FOREIGN KEY (blood_group_id) REFERENCES public.global_blood_groups(id);


--
-- Name: organization_students organization_students_category_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_students
    ADD CONSTRAINT organization_students_category_id_fkey FOREIGN KEY (category_id) REFERENCES public.global_student_categories(id);


--
-- Name: organization_students organization_students_created_by_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_students
    ADD CONSTRAINT organization_students_created_by_fkey FOREIGN KEY (created_by) REFERENCES public.users(id) ON DELETE SET NULL;


--
-- Name: organization_students organization_students_guardian_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_students
    ADD CONSTRAINT organization_students_guardian_id_fkey FOREIGN KEY (guardian_id) REFERENCES public.organization_guardians(id) ON DELETE CASCADE;


--
-- Name: organization_students organization_students_organization_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_students
    ADD CONSTRAINT organization_students_organization_id_fkey FOREIGN KEY (organization_id) REFERENCES public.organizations(id) ON DELETE CASCADE;


--
-- Name: organization_students organization_students_student_status_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_students
    ADD CONSTRAINT organization_students_student_status_id_fkey FOREIGN KEY (student_status_id) REFERENCES public.global_student_status(id);


--
-- Name: organization_students organization_students_updated_by_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_students
    ADD CONSTRAINT organization_students_updated_by_fkey FOREIGN KEY (updated_by) REFERENCES public.users(id) ON DELETE SET NULL;


--
-- Name: organization_users organization_users_organization_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_users
    ADD CONSTRAINT organization_users_organization_id_fkey FOREIGN KEY (organization_id) REFERENCES public.organizations(id) ON DELETE CASCADE;


--
-- Name: organization_users organization_users_role_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_users
    ADD CONSTRAINT organization_users_role_id_fkey FOREIGN KEY (role_id) REFERENCES public.global_staff_roles(id) ON DELETE SET NULL;


--
-- Name: organization_users organization_users_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_users
    ADD CONSTRAINT organization_users_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.users(id) ON DELETE CASCADE;


--
-- Name: organization_website_announcements organization_website_announcements_created_by_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_website_announcements
    ADD CONSTRAINT organization_website_announcements_created_by_fkey FOREIGN KEY (created_by) REFERENCES public.users(id) ON DELETE SET NULL;


--
-- Name: organization_website_announcements organization_website_announcements_updated_by_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_website_announcements
    ADD CONSTRAINT organization_website_announcements_updated_by_fkey FOREIGN KEY (updated_by) REFERENCES public.users(id) ON DELETE SET NULL;


--
-- Name: organization_website_announcements organization_website_announcements_website_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_website_announcements
    ADD CONSTRAINT organization_website_announcements_website_id_fkey FOREIGN KEY (website_id) REFERENCES public.parent_organization_websites(id) ON DELETE CASCADE;


--
-- Name: organization_website_galleries organization_website_galleries_created_by_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_website_galleries
    ADD CONSTRAINT organization_website_galleries_created_by_fkey FOREIGN KEY (created_by) REFERENCES public.users(id) ON DELETE SET NULL;


--
-- Name: organization_website_galleries organization_website_galleries_updated_by_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_website_galleries
    ADD CONSTRAINT organization_website_galleries_updated_by_fkey FOREIGN KEY (updated_by) REFERENCES public.users(id) ON DELETE SET NULL;


--
-- Name: organization_website_galleries organization_website_galleries_website_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_website_galleries
    ADD CONSTRAINT organization_website_galleries_website_id_fkey FOREIGN KEY (website_id) REFERENCES public.parent_organization_websites(id) ON DELETE CASCADE;


--
-- Name: organizations organizations_parent_organization_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organizations
    ADD CONSTRAINT organizations_parent_organization_id_fkey FOREIGN KEY (parent_organization_id) REFERENCES public.organization_parents(id) ON DELETE RESTRICT;


--
-- Name: organizations organizations_type_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organizations
    ADD CONSTRAINT organizations_type_id_fkey FOREIGN KEY (type_id) REFERENCES public.global_organization_types(id) ON DELETE RESTRICT;


--
-- Name: organization_parent_journey_mcqs parent_org_journey_mcqs_template_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_parent_journey_mcqs
    ADD CONSTRAINT parent_org_journey_mcqs_template_fkey FOREIGN KEY (template_id) REFERENCES public.organization_parent_journey_templates(id) ON DELETE CASCADE;


--
-- Name: organization_parent_journey_tasks parent_org_journey_tasks_template_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_parent_journey_tasks
    ADD CONSTRAINT parent_org_journey_tasks_template_fkey FOREIGN KEY (template_id) REFERENCES public.organization_parent_journey_templates(id) ON DELETE CASCADE;


--
-- Name: organization_parent_journey_templates parent_org_journey_templates_created_by_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_parent_journey_templates
    ADD CONSTRAINT parent_org_journey_templates_created_by_fkey FOREIGN KEY (created_by) REFERENCES public.users(id);


--
-- Name: organization_parent_journey_templates parent_org_journey_templates_parent_org_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_parent_journey_templates
    ADD CONSTRAINT parent_org_journey_templates_parent_org_fkey FOREIGN KEY (parent_organization_id) REFERENCES public.organization_parents(id);


--
-- Name: organization_parent_journey_templates parent_org_journey_templates_updated_by_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organization_parent_journey_templates
    ADD CONSTRAINT parent_org_journey_templates_updated_by_fkey FOREIGN KEY (updated_by) REFERENCES public.users(id);


--
-- Name: parent_organization_website_facilities parent_organization_website_facilities_created_by_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.parent_organization_website_facilities
    ADD CONSTRAINT parent_organization_website_facilities_created_by_fkey FOREIGN KEY (created_by) REFERENCES public.users(id) ON DELETE SET NULL;


--
-- Name: parent_organization_website_facilities parent_organization_website_facilities_facility_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.parent_organization_website_facilities
    ADD CONSTRAINT parent_organization_website_facilities_facility_id_fkey FOREIGN KEY (facility_id) REFERENCES public.global_facilities(id) ON DELETE RESTRICT;


--
-- Name: parent_organization_website_facilities parent_organization_website_facilities_updated_by_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.parent_organization_website_facilities
    ADD CONSTRAINT parent_organization_website_facilities_updated_by_fkey FOREIGN KEY (updated_by) REFERENCES public.users(id) ON DELETE SET NULL;


--
-- Name: parent_organization_website_facilities parent_organization_website_facilities_website_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.parent_organization_website_facilities
    ADD CONSTRAINT parent_organization_website_facilities_website_id_fkey FOREIGN KEY (website_id) REFERENCES public.parent_organization_websites(id) ON DELETE CASCADE;


--
-- Name: parent_organization_websites parent_organization_websites_created_by_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.parent_organization_websites
    ADD CONSTRAINT parent_organization_websites_created_by_fkey FOREIGN KEY (created_by) REFERENCES public.users(id) ON DELETE SET NULL;


--
-- Name: parent_organization_websites parent_organization_websites_parent_organization_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.parent_organization_websites
    ADD CONSTRAINT parent_organization_websites_parent_organization_id_fkey FOREIGN KEY (parent_organization_id) REFERENCES public.organization_parents(id) ON DELETE CASCADE;


--
-- Name: parent_organization_websites parent_organization_websites_updated_by_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.parent_organization_websites
    ADD CONSTRAINT parent_organization_websites_updated_by_fkey FOREIGN KEY (updated_by) REFERENCES public.users(id) ON DELETE SET NULL;


--
-- Name: student_document_physical_locations physical_locations_created_by_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.student_document_physical_locations
    ADD CONSTRAINT physical_locations_created_by_fkey FOREIGN KEY (created_by) REFERENCES public.users(id) ON DELETE SET NULL;


--
-- Name: student_document_physical_locations physical_locations_registry_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.student_document_physical_locations
    ADD CONSTRAINT physical_locations_registry_fkey FOREIGN KEY (registry_id) REFERENCES public.student_document_registry(id) ON DELETE CASCADE;


--
-- Name: student_document_physical_locations physical_locations_staff_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.student_document_physical_locations
    ADD CONSTRAINT physical_locations_staff_fkey FOREIGN KEY (received_by_staff_id) REFERENCES public.organization_users(id) ON DELETE SET NULL;


--
-- Name: student_document_physical_locations physical_locations_updated_by_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.student_document_physical_locations
    ADD CONSTRAINT physical_locations_updated_by_fkey FOREIGN KEY (updated_by) REFERENCES public.users(id) ON DELETE SET NULL;


--
-- Name: reports reports_message_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.reports
    ADD CONSTRAINT reports_message_id_fkey FOREIGN KEY (message_id) REFERENCES public.messages(id) ON DELETE CASCADE;


--
-- Name: reports reports_reporter_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.reports
    ADD CONSTRAINT reports_reporter_user_id_fkey FOREIGN KEY (reporter_user_id) REFERENCES public.users(id) ON DELETE CASCADE;


--
-- Name: student_document_return_logs return_logs_approver_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.student_document_return_logs
    ADD CONSTRAINT return_logs_approver_fkey FOREIGN KEY (approved_by_user_id) REFERENCES public.users(id) ON DELETE SET NULL;


--
-- Name: student_document_return_logs return_logs_registry_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.student_document_return_logs
    ADD CONSTRAINT return_logs_registry_fkey FOREIGN KEY (registry_id) REFERENCES public.student_document_registry(id) ON DELETE CASCADE;


--
-- Name: student_document_return_logs return_logs_staff_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.student_document_return_logs
    ADD CONSTRAINT return_logs_staff_fkey FOREIGN KEY (returned_by_staff_id) REFERENCES public.organization_users(id) ON DELETE SET NULL;


--
-- Name: student_document_registry student_document_registry_created_by_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.student_document_registry
    ADD CONSTRAINT student_document_registry_created_by_fkey FOREIGN KEY (created_by) REFERENCES public.users(id) ON DELETE SET NULL;


--
-- Name: student_document_registry student_document_registry_doc_type_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.student_document_registry
    ADD CONSTRAINT student_document_registry_doc_type_fkey FOREIGN KEY (document_type_id) REFERENCES public.global_document_types(id);


--
-- Name: student_document_registry student_document_registry_org_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.student_document_registry
    ADD CONSTRAINT student_document_registry_org_fkey FOREIGN KEY (organization_id) REFERENCES public.organizations(id) ON DELETE CASCADE;


--
-- Name: student_document_registry student_document_registry_parent_org_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.student_document_registry
    ADD CONSTRAINT student_document_registry_parent_org_fkey FOREIGN KEY (parent_organization_id) REFERENCES public.organization_parents(id) ON DELETE CASCADE;


--
-- Name: student_document_registry student_document_registry_session_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.student_document_registry
    ADD CONSTRAINT student_document_registry_session_fkey FOREIGN KEY (active_session_id) REFERENCES public.global_sessions(id) ON DELETE CASCADE;


--
-- Name: student_document_registry student_document_registry_student_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.student_document_registry
    ADD CONSTRAINT student_document_registry_student_fkey FOREIGN KEY (student_id) REFERENCES public.organization_students(id) ON DELETE CASCADE;


--
-- Name: student_document_registry student_document_registry_updated_by_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.student_document_registry
    ADD CONSTRAINT student_document_registry_updated_by_fkey FOREIGN KEY (updated_by) REFERENCES public.users(id) ON DELETE SET NULL;


--
-- Name: user_documents user_documents_doc_type_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.user_documents
    ADD CONSTRAINT user_documents_doc_type_fkey FOREIGN KEY (document_type_id) REFERENCES public.global_document_types(id);


--
-- Name: user_documents user_documents_user_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.user_documents
    ADD CONSTRAINT user_documents_user_fkey FOREIGN KEY (user_id) REFERENCES public.users(id) ON DELETE CASCADE;


--
-- Name: user_inspirations user_inspirations_inspired_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.user_inspirations
    ADD CONSTRAINT user_inspirations_inspired_user_id_fkey FOREIGN KEY (inspired_user_id) REFERENCES public.users(id) ON DELETE CASCADE;


--
-- Name: user_inspirations user_inspirations_inspiring_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.user_inspirations
    ADD CONSTRAINT user_inspirations_inspiring_user_id_fkey FOREIGN KEY (inspiring_user_id) REFERENCES public.users(id) ON DELETE CASCADE;


--
-- Name: user_journey_mcq_progress user_journey_mcq_progress_journey_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.user_journey_mcq_progress
    ADD CONSTRAINT user_journey_mcq_progress_journey_fkey FOREIGN KEY (user_journey_id) REFERENCES public.user_journeys(id) ON DELETE CASCADE;


--
-- Name: user_journey_task_progress user_journey_task_progress_journey_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.user_journey_task_progress
    ADD CONSTRAINT user_journey_task_progress_journey_fkey FOREIGN KEY (user_journey_id) REFERENCES public.user_journeys(id) ON DELETE CASCADE;


--
-- Name: user_journeys user_journeys_global_temp_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.user_journeys
    ADD CONSTRAINT user_journeys_global_temp_fkey FOREIGN KEY (global_template_id) REFERENCES public.global_journey_templates(id);


--
-- Name: user_journeys user_journeys_org_temp_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.user_journeys
    ADD CONSTRAINT user_journeys_org_temp_fkey FOREIGN KEY (organization_template_id) REFERENCES public.organization_journey_templates(id);


--
-- Name: user_journeys user_journeys_parent_org_temp_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.user_journeys
    ADD CONSTRAINT user_journeys_parent_org_temp_fkey FOREIGN KEY (organization_parent_template_id) REFERENCES public.organization_parent_journey_templates(id);


--
-- Name: user_journeys user_journeys_user_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.user_journeys
    ADD CONSTRAINT user_journeys_user_fkey FOREIGN KEY (user_id) REFERENCES public.users(id);


--
-- Name: user_profiles user_profiles_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.user_profiles
    ADD CONSTRAINT user_profiles_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.users(id) ON DELETE CASCADE;


--
-- Name: user_sessions user_sessions_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.user_sessions
    ADD CONSTRAINT user_sessions_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.users(id) ON DELETE CASCADE;


--
-- Name: users users_auth_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_auth_id_fkey FOREIGN KEY (auth_id) REFERENCES auth.users(id) ON DELETE CASCADE;


--
-- Name: objects objects_bucketId_fkey; Type: FK CONSTRAINT; Schema: storage; Owner: -
--

ALTER TABLE ONLY storage.objects
    ADD CONSTRAINT "objects_bucketId_fkey" FOREIGN KEY (bucket_id) REFERENCES storage.buckets(id);


--
-- Name: s3_multipart_uploads s3_multipart_uploads_bucket_id_fkey; Type: FK CONSTRAINT; Schema: storage; Owner: -
--

ALTER TABLE ONLY storage.s3_multipart_uploads
    ADD CONSTRAINT s3_multipart_uploads_bucket_id_fkey FOREIGN KEY (bucket_id) REFERENCES storage.buckets(id);


--
-- Name: s3_multipart_uploads_parts s3_multipart_uploads_parts_bucket_id_fkey; Type: FK CONSTRAINT; Schema: storage; Owner: -
--

ALTER TABLE ONLY storage.s3_multipart_uploads_parts
    ADD CONSTRAINT s3_multipart_uploads_parts_bucket_id_fkey FOREIGN KEY (bucket_id) REFERENCES storage.buckets(id);


--
-- Name: s3_multipart_uploads_parts s3_multipart_uploads_parts_upload_id_fkey; Type: FK CONSTRAINT; Schema: storage; Owner: -
--

ALTER TABLE ONLY storage.s3_multipart_uploads_parts
    ADD CONSTRAINT s3_multipart_uploads_parts_upload_id_fkey FOREIGN KEY (upload_id) REFERENCES storage.s3_multipart_uploads(id) ON DELETE CASCADE;


--
-- Name: vector_indexes vector_indexes_bucket_id_fkey; Type: FK CONSTRAINT; Schema: storage; Owner: -
--

ALTER TABLE ONLY storage.vector_indexes
    ADD CONSTRAINT vector_indexes_bucket_id_fkey FOREIGN KEY (bucket_id) REFERENCES storage.buckets_vectors(id);


--
-- Name: audit_log_entries; Type: ROW SECURITY; Schema: auth; Owner: -
--

ALTER TABLE auth.audit_log_entries ENABLE ROW LEVEL SECURITY;

--
-- Name: flow_state; Type: ROW SECURITY; Schema: auth; Owner: -
--

ALTER TABLE auth.flow_state ENABLE ROW LEVEL SECURITY;

--
-- Name: identities; Type: ROW SECURITY; Schema: auth; Owner: -
--

ALTER TABLE auth.identities ENABLE ROW LEVEL SECURITY;

--
-- Name: instances; Type: ROW SECURITY; Schema: auth; Owner: -
--

ALTER TABLE auth.instances ENABLE ROW LEVEL SECURITY;

--
-- Name: mfa_amr_claims; Type: ROW SECURITY; Schema: auth; Owner: -
--

ALTER TABLE auth.mfa_amr_claims ENABLE ROW LEVEL SECURITY;

--
-- Name: mfa_challenges; Type: ROW SECURITY; Schema: auth; Owner: -
--

ALTER TABLE auth.mfa_challenges ENABLE ROW LEVEL SECURITY;

--
-- Name: mfa_factors; Type: ROW SECURITY; Schema: auth; Owner: -
--

ALTER TABLE auth.mfa_factors ENABLE ROW LEVEL SECURITY;

--
-- Name: one_time_tokens; Type: ROW SECURITY; Schema: auth; Owner: -
--

ALTER TABLE auth.one_time_tokens ENABLE ROW LEVEL SECURITY;

--
-- Name: refresh_tokens; Type: ROW SECURITY; Schema: auth; Owner: -
--

ALTER TABLE auth.refresh_tokens ENABLE ROW LEVEL SECURITY;

--
-- Name: saml_providers; Type: ROW SECURITY; Schema: auth; Owner: -
--

ALTER TABLE auth.saml_providers ENABLE ROW LEVEL SECURITY;

--
-- Name: saml_relay_states; Type: ROW SECURITY; Schema: auth; Owner: -
--

ALTER TABLE auth.saml_relay_states ENABLE ROW LEVEL SECURITY;

--
-- Name: schema_migrations; Type: ROW SECURITY; Schema: auth; Owner: -
--

ALTER TABLE auth.schema_migrations ENABLE ROW LEVEL SECURITY;

--
-- Name: sessions; Type: ROW SECURITY; Schema: auth; Owner: -
--

ALTER TABLE auth.sessions ENABLE ROW LEVEL SECURITY;

--
-- Name: sso_domains; Type: ROW SECURITY; Schema: auth; Owner: -
--

ALTER TABLE auth.sso_domains ENABLE ROW LEVEL SECURITY;

--
-- Name: sso_providers; Type: ROW SECURITY; Schema: auth; Owner: -
--

ALTER TABLE auth.sso_providers ENABLE ROW LEVEL SECURITY;

--
-- Name: users; Type: ROW SECURITY; Schema: auth; Owner: -
--

ALTER TABLE auth.users ENABLE ROW LEVEL SECURITY;

--
-- Name: organization_session_tokens Allow all for all; Type: POLICY; Schema: public; Owner: -
--

CREATE POLICY "Allow all for all" ON public.organization_session_tokens USING (true) WITH CHECK (true);


--
-- Name: app_versions Allow public read access; Type: POLICY; Schema: public; Owner: -
--

CREATE POLICY "Allow public read access" ON public.app_versions FOR SELECT TO authenticated, anon USING (true);


--
-- Name: global_areas Allow public read access; Type: POLICY; Schema: public; Owner: -
--

CREATE POLICY "Allow public read access" ON public.global_areas FOR SELECT USING (true);


--
-- Name: global_countries Allow public read access; Type: POLICY; Schema: public; Owner: -
--

CREATE POLICY "Allow public read access" ON public.global_countries FOR SELECT USING (true);


--
-- Name: global_districts Allow public read access; Type: POLICY; Schema: public; Owner: -
--

CREATE POLICY "Allow public read access" ON public.global_districts FOR SELECT USING (true);


--
-- Name: global_states Allow public read access; Type: POLICY; Schema: public; Owner: -
--

CREATE POLICY "Allow public read access" ON public.global_states FOR SELECT USING (true);


--
-- Name: global_tehsils Allow public read access; Type: POLICY; Schema: public; Owner: -
--

CREATE POLICY "Allow public read access" ON public.global_tehsils FOR SELECT USING (true);


--
-- Name: organization_users Allow select for all; Type: POLICY; Schema: public; Owner: -
--

CREATE POLICY "Allow select for all" ON public.organization_users FOR SELECT USING (true);


--
-- Name: users Allow select for all; Type: POLICY; Schema: public; Owner: -
--

CREATE POLICY "Allow select for all" ON public.users FOR SELECT USING (true);


--
-- Name: app_versions Enable read access for all users; Type: POLICY; Schema: public; Owner: -
--

CREATE POLICY "Enable read access for all users" ON public.app_versions FOR SELECT USING (true);


--
-- Name: content_contributor_verifications ccv_insert_own; Type: POLICY; Schema: public; Owner: -
--

CREATE POLICY ccv_insert_own ON public.content_contributor_verifications FOR INSERT WITH CHECK (((user_id = ( SELECT users.id
   FROM public.users
  WHERE (users.auth_id = auth.uid()))) AND (contributor_type = 'user'::text)));


--
-- Name: content_contributor_verifications ccv_select_own; Type: POLICY; Schema: public; Owner: -
--

CREATE POLICY ccv_select_own ON public.content_contributor_verifications FOR SELECT USING ((user_id = ( SELECT users.id
   FROM public.users
  WHERE (users.auth_id = auth.uid()))));


--
-- Name: case_studies cs_insert_verified_user; Type: POLICY; Schema: public; Owner: -
--

CREATE POLICY cs_insert_verified_user ON public.case_studies FOR INSERT WITH CHECK (((author_type = 'user'::text) AND (author_user_id = ( SELECT users.id
   FROM public.users
  WHERE (users.auth_id = auth.uid()))) AND (EXISTS ( SELECT 1
   FROM public.content_contributor_verifications
  WHERE ((content_contributor_verifications.user_id = case_studies.author_user_id) AND (content_contributor_verifications.status = 'approved'::text))))));


--
-- Name: case_studies cs_select_own_drafts; Type: POLICY; Schema: public; Owner: -
--

CREATE POLICY cs_select_own_drafts ON public.case_studies FOR SELECT USING ((author_user_id = ( SELECT users.id
   FROM public.users
  WHERE (users.auth_id = auth.uid()))));


--
-- Name: case_studies cs_select_published; Type: POLICY; Schema: public; Owner: -
--

CREATE POLICY cs_select_published ON public.case_studies FOR SELECT USING (((status = 'published'::text) AND (is_deleted = false)));


--
-- Name: case_studies cs_update_own; Type: POLICY; Schema: public; Owner: -
--

CREATE POLICY cs_update_own ON public.case_studies FOR UPDATE USING (((author_user_id = ( SELECT users.id
   FROM public.users
  WHERE (users.auth_id = auth.uid()))) AND (status = ANY (ARRAY['draft'::text, 'rejected'::text]))));


--
-- Name: case_study_bookmarks csb_delete_own; Type: POLICY; Schema: public; Owner: -
--

CREATE POLICY csb_delete_own ON public.case_study_bookmarks FOR DELETE USING ((user_id = ( SELECT users.id
   FROM public.users
  WHERE (users.auth_id = auth.uid()))));


--
-- Name: case_study_bookmarks csb_insert_own; Type: POLICY; Schema: public; Owner: -
--

CREATE POLICY csb_insert_own ON public.case_study_bookmarks FOR INSERT WITH CHECK ((user_id = ( SELECT users.id
   FROM public.users
  WHERE (users.auth_id = auth.uid()))));


--
-- Name: case_study_bookmarks csb_select_own; Type: POLICY; Schema: public; Owner: -
--

CREATE POLICY csb_select_own ON public.case_study_bookmarks FOR SELECT USING ((user_id = ( SELECT users.id
   FROM public.users
  WHERE (users.auth_id = auth.uid()))));


--
-- Name: case_study_reactions csr_delete_own; Type: POLICY; Schema: public; Owner: -
--

CREATE POLICY csr_delete_own ON public.case_study_reactions FOR DELETE USING ((user_id = ( SELECT users.id
   FROM public.users
  WHERE (users.auth_id = auth.uid()))));


--
-- Name: case_study_reactions csr_insert_own; Type: POLICY; Schema: public; Owner: -
--

CREATE POLICY csr_insert_own ON public.case_study_reactions FOR INSERT WITH CHECK ((user_id = ( SELECT users.id
   FROM public.users
  WHERE (users.auth_id = auth.uid()))));


--
-- Name: case_study_reactions csr_select_all; Type: POLICY; Schema: public; Owner: -
--

CREATE POLICY csr_select_all ON public.case_study_reactions FOR SELECT USING ((auth.role() = 'authenticated'::text));


--
-- Name: case_study_reactions csr_update_own; Type: POLICY; Schema: public; Owner: -
--

CREATE POLICY csr_update_own ON public.case_study_reactions FOR UPDATE USING ((user_id = ( SELECT users.id
   FROM public.users
  WHERE (users.auth_id = auth.uid()))));


--
-- Name: messages; Type: ROW SECURITY; Schema: realtime; Owner: -
--

ALTER TABLE realtime.messages ENABLE ROW LEVEL SECURITY;

--
-- Name: objects Allow authenticated users to delete website assets; Type: POLICY; Schema: storage; Owner: -
--

CREATE POLICY "Allow authenticated users to delete website assets" ON storage.objects FOR DELETE TO authenticated USING ((bucket_id = 'website'::text));


--
-- Name: objects Allow authenticated users to update website assets; Type: POLICY; Schema: storage; Owner: -
--

CREATE POLICY "Allow authenticated users to update website assets" ON storage.objects FOR UPDATE TO authenticated USING ((bucket_id = 'website'::text));


--
-- Name: objects Allow authenticated users to upload to website; Type: POLICY; Schema: storage; Owner: -
--

CREATE POLICY "Allow authenticated users to upload to website" ON storage.objects FOR INSERT TO authenticated WITH CHECK ((bucket_id = 'website'::text));


--
-- Name: objects Allow open access to student-images for testing; Type: POLICY; Schema: storage; Owner: -
--

CREATE POLICY "Allow open access to student-images for testing" ON storage.objects USING ((bucket_id = 'student-images'::text)) WITH CHECK ((bucket_id = 'student-images'::text));


--
-- Name: objects Allow public read access on transport documents; Type: POLICY; Schema: storage; Owner: -
--

CREATE POLICY "Allow public read access on transport documents" ON storage.objects FOR SELECT USING ((bucket_id = 'transport-documents'::text));


--
-- Name: objects Allow public read access to website bucket; Type: POLICY; Schema: storage; Owner: -
--

CREATE POLICY "Allow public read access to website bucket" ON storage.objects FOR SELECT USING ((bucket_id = 'website'::text));


--
-- Name: objects Allow upload access on transport documents; Type: POLICY; Schema: storage; Owner: -
--

CREATE POLICY "Allow upload access on transport documents" ON storage.objects FOR INSERT WITH CHECK ((bucket_id = 'transport-documents'::text));


--
-- Name: objects Authenticated Upload Access; Type: POLICY; Schema: storage; Owner: -
--

CREATE POLICY "Authenticated Upload Access" ON storage.objects FOR INSERT TO authenticated WITH CHECK ((bucket_id = 'case-study-images'::text));


--
-- Name: objects Public Read Access; Type: POLICY; Schema: storage; Owner: -
--

CREATE POLICY "Public Read Access" ON storage.objects FOR SELECT USING ((bucket_id = 'case-study-images'::text));


--
-- Name: buckets; Type: ROW SECURITY; Schema: storage; Owner: -
--

ALTER TABLE storage.buckets ENABLE ROW LEVEL SECURITY;

--
-- Name: buckets_analytics; Type: ROW SECURITY; Schema: storage; Owner: -
--

ALTER TABLE storage.buckets_analytics ENABLE ROW LEVEL SECURITY;

--
-- Name: buckets_vectors; Type: ROW SECURITY; Schema: storage; Owner: -
--

ALTER TABLE storage.buckets_vectors ENABLE ROW LEVEL SECURITY;

--
-- Name: migrations; Type: ROW SECURITY; Schema: storage; Owner: -
--

ALTER TABLE storage.migrations ENABLE ROW LEVEL SECURITY;

--
-- Name: objects; Type: ROW SECURITY; Schema: storage; Owner: -
--

ALTER TABLE storage.objects ENABLE ROW LEVEL SECURITY;

--
-- Name: s3_multipart_uploads; Type: ROW SECURITY; Schema: storage; Owner: -
--

ALTER TABLE storage.s3_multipart_uploads ENABLE ROW LEVEL SECURITY;

--
-- Name: s3_multipart_uploads_parts; Type: ROW SECURITY; Schema: storage; Owner: -
--

ALTER TABLE storage.s3_multipart_uploads_parts ENABLE ROW LEVEL SECURITY;

--
-- Name: vector_indexes; Type: ROW SECURITY; Schema: storage; Owner: -
--

ALTER TABLE storage.vector_indexes ENABLE ROW LEVEL SECURITY;

--
-- Name: supabase_realtime; Type: PUBLICATION; Schema: -; Owner: -
--

CREATE PUBLICATION supabase_realtime WITH (publish = 'insert, update, delete, truncate');


--
-- Name: supabase_realtime_messages_publication; Type: PUBLICATION; Schema: -; Owner: -
--

CREATE PUBLICATION supabase_realtime_messages_publication WITH (publish = 'insert, update, delete, truncate');


--
-- Name: supabase_realtime messages; Type: PUBLICATION TABLE; Schema: public; Owner: -
--

ALTER PUBLICATION supabase_realtime ADD TABLE ONLY public.messages;


--
-- Name: supabase_realtime organization_parent_bus_live_locations; Type: PUBLICATION TABLE; Schema: public; Owner: -
--

ALTER PUBLICATION supabase_realtime ADD TABLE ONLY public.organization_parent_bus_live_locations;


--
-- Name: supabase_realtime rooms; Type: PUBLICATION TABLE; Schema: public; Owner: -
--

ALTER PUBLICATION supabase_realtime ADD TABLE ONLY public.rooms;


--
-- Name: supabase_realtime_messages_publication messages; Type: PUBLICATION TABLE; Schema: realtime; Owner: -
--

ALTER PUBLICATION supabase_realtime_messages_publication ADD TABLE ONLY realtime.messages;


--
-- Name: ensure_rls; Type: EVENT TRIGGER; Schema: -; Owner: -
--

CREATE EVENT TRIGGER ensure_rls ON ddl_command_end
         WHEN TAG IN ('CREATE TABLE', 'CREATE TABLE AS', 'SELECT INTO')
   EXECUTE FUNCTION public.rls_auto_enable();


--
-- Name: issue_graphql_placeholder; Type: EVENT TRIGGER; Schema: -; Owner: -
--

CREATE EVENT TRIGGER issue_graphql_placeholder ON sql_drop
         WHEN TAG IN ('DROP EXTENSION')
   EXECUTE FUNCTION extensions.set_graphql_placeholder();


--
-- Name: issue_pg_cron_access; Type: EVENT TRIGGER; Schema: -; Owner: -
--

CREATE EVENT TRIGGER issue_pg_cron_access ON ddl_command_end
         WHEN TAG IN ('CREATE EXTENSION')
   EXECUTE FUNCTION extensions.grant_pg_cron_access();


--
-- Name: issue_pg_graphql_access; Type: EVENT TRIGGER; Schema: -; Owner: -
--

CREATE EVENT TRIGGER issue_pg_graphql_access ON ddl_command_end
         WHEN TAG IN ('CREATE EXTENSION')
   EXECUTE FUNCTION extensions.grant_pg_graphql_access();


--
-- Name: issue_pg_net_access; Type: EVENT TRIGGER; Schema: -; Owner: -
--

CREATE EVENT TRIGGER issue_pg_net_access ON ddl_command_end
         WHEN TAG IN ('CREATE EXTENSION')
   EXECUTE FUNCTION extensions.grant_pg_net_access();


--
-- Name: pgrst_ddl_watch; Type: EVENT TRIGGER; Schema: -; Owner: -
--

CREATE EVENT TRIGGER pgrst_ddl_watch ON ddl_command_end
   EXECUTE FUNCTION extensions.pgrst_ddl_watch();


--
-- Name: pgrst_drop_watch; Type: EVENT TRIGGER; Schema: -; Owner: -
--

CREATE EVENT TRIGGER pgrst_drop_watch ON sql_drop
   EXECUTE FUNCTION extensions.pgrst_drop_watch();


--
-- PostgreSQL database dump complete
--

\unrestrict OuXNc0eLarZi23fMfkVitcnVYOhjBhpcnTNMxVLr7ohoZbudTMPPlm0oK9aZ9XC

