---- Cr¨¦ation de la table roles dans le sch¨¦ma auth_db
--
---- D¨¦finir le sch¨¦ma de recherche
--SET search_path TO auth_db, public;
--
--CREATE TABLE roles (
--    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
--    tenant_id UUID NOT NULL REFERENCES tenants(id) ON DELETE CASCADE,
--    name VARCHAR(100) NOT NULL,
--    description TEXT,
--    is_system BOOLEAN DEFAULT FALSE,
--    created_at TIMESTAMP DEFAULT NOW(),
--    CONSTRAINT unique_tenant_role UNIQUE(tenant_id, name)
--);
--
---- Index pour optimiser les recherches
--CREATE INDEX idx_roles_tenant_id ON roles(tenant_id);
--CREATE INDEX idx_roles_name ON roles(name);

CREATE TABLE roles (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    tenant_id UUID NOT NULL REFERENCES tenants(id) ON DELETE CASCADE,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    is_system BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT NOW(),
    CONSTRAINT unique_tenant_role UNIQUE(tenant_id, name)
);

-- Index pour optimiser les recherches
CREATE INDEX idx_roles_tenant_id ON roles(tenant_id);
CREATE INDEX idx_roles_name ON roles(name);