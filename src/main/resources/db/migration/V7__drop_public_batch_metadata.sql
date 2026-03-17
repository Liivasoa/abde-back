-- Cleanup migration: remove legacy Spring Batch metadata objects from public schema.
-- Active metadata must now live in syst.*

DROP TABLE IF EXISTS public.batch_step_execution_context;
DROP TABLE IF EXISTS public.batch_job_execution_context;
DROP TABLE IF EXISTS public.batch_step_execution;
DROP TABLE IF EXISTS public.batch_job_execution_params;
DROP TABLE IF EXISTS public.batch_job_execution;
DROP TABLE IF EXISTS public.batch_job_instance;

DROP SEQUENCE IF EXISTS public.batch_step_execution_seq;
DROP SEQUENCE IF EXISTS public.batch_job_execution_seq;
DROP SEQUENCE IF EXISTS public.batch_job_seq;
DROP SEQUENCE IF EXISTS public.batch_job_instance_seq;
