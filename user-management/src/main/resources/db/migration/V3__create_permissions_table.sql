-- Cr¨¦ation de la table permissions dans le sch¨¦ma auth_db

-- D¨¦finir le sch¨¦ma de recherche
--SET search_path TO auth_db, public;
--
--CREATE TABLE permissions (
--    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
--    name VARCHAR(100) UNIQUE NOT NULL,
--    description TEXT,
--    resource VARCHAR(100),
--    action VARCHAR(50)
--);
--
---- Index pour optimiser les recherches
--CREATE INDEX idx_permissions_name ON permissions(name);
--CREATE INDEX idx_permissions_resource ON permissions(resource);

CREATE TABLE permissions (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(100) UNIQUE NOT NULL,
    description TEXT,
    resource VARCHAR(100),
    action VARCHAR(50)
);

-- Index pour optimiser les recherches
CREATE INDEX idx_permissions_name ON permissions(name);
CREATE INDEX idx_permissions_resource ON permissions(resource);