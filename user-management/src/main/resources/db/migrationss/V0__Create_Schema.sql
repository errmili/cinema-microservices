-- Cr¨¦er le sch¨¦ma s'il n'existe pas
CREATE SCHEMA IF NOT EXISTS auth_db;

-- Cr¨¦er l'extension UUID dans le sch¨¦ma auth_db
CREATE EXTENSION IF NOT EXISTS "uuid-ossp" SCHEMA auth_db;