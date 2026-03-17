CREATE SCHEMA IF NOT EXISTS syst;

DO $$
BEGIN
    IF to_regclass('public.batch_job_seq') IS NOT NULL
        AND to_regclass('public.batch_job_instance_seq') IS NULL THEN
        ALTER SEQUENCE public.batch_job_seq RENAME TO batch_job_instance_seq;
    END IF;
END $$;

DO $$
BEGIN
    IF to_regclass('public.batch_job_instance') IS NOT NULL THEN
        ALTER TABLE public.batch_job_instance SET SCHEMA syst;
    END IF;
END $$;

DO $$
BEGIN
    IF to_regclass('public.batch_job_execution') IS NOT NULL THEN
        ALTER TABLE public.batch_job_execution SET SCHEMA syst;
    END IF;
END $$;

DO $$
BEGIN
    IF to_regclass('public.batch_job_execution_params') IS NOT NULL THEN
        ALTER TABLE public.batch_job_execution_params SET SCHEMA syst;
    END IF;
END $$;

DO $$
BEGIN
    IF to_regclass('public.batch_step_execution') IS NOT NULL THEN
        ALTER TABLE public.batch_step_execution SET SCHEMA syst;
    END IF;
END $$;

DO $$
BEGIN
    IF to_regclass('public.batch_step_execution_context') IS NOT NULL THEN
        ALTER TABLE public.batch_step_execution_context SET SCHEMA syst;
    END IF;
END $$;

DO $$
BEGIN
    IF to_regclass('public.batch_job_execution_context') IS NOT NULL THEN
        ALTER TABLE public.batch_job_execution_context SET SCHEMA syst;
    END IF;
END $$;

DO $$
BEGIN
    IF to_regclass('public.batch_step_execution_seq') IS NOT NULL THEN
        ALTER SEQUENCE public.batch_step_execution_seq SET SCHEMA syst;
    END IF;
END $$;

DO $$
BEGIN
    IF to_regclass('public.batch_job_execution_seq') IS NOT NULL THEN
        ALTER SEQUENCE public.batch_job_execution_seq SET SCHEMA syst;
    END IF;
END $$;

DO $$
BEGIN
    IF to_regclass('public.batch_job_instance_seq') IS NOT NULL THEN
        ALTER SEQUENCE public.batch_job_instance_seq SET SCHEMA syst;
    END IF;
END $$;
