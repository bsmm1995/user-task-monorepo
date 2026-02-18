-- This script runs automatically on first container initialization.
-- It creates only the two databases needed by the apps.

\echo 'Creating databases user_db and task_db (if missing)'

-- CREATE DATABASE can't run inside DO/functions.
-- Use psql's \gexec to execute dynamically generated statements.

SELECT format('CREATE DATABASE %I;', 'user_db')
WHERE NOT EXISTS (SELECT 1 FROM pg_database WHERE datname = 'user_db')
\gexec

SELECT format('CREATE DATABASE %I;', 'task_db')
WHERE NOT EXISTS (SELECT 1 FROM pg_database WHERE datname = 'task_db')
\gexec
