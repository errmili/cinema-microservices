SELECT 'CREATE DATABASE cinema' WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'cinema')\gexec
SELECT 'CREATE DATABASE movie' WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'movie')\gexec
SELECT 'CREATE DATABASE booking' WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'booking')\gexec
SELECT 'CREATE DATABASE user_management' WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'user_management')\gexec

GRANT ALL PRIVILEGES ON DATABASE cinema TO postgres;
GRANT ALL PRIVILEGES ON DATABASE movie TO postgres;
GRANT ALL PRIVILEGES ON DATABASE booking TO postgres;
GRANT ALL PRIVILEGES ON DATABASE user_management TO postgres;