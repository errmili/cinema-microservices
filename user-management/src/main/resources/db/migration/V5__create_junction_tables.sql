-- Cr¨¦ation des tables de liaison dans le sch¨¦ma auth_db

-- D¨¦finir le sch¨¦ma de recherche
--SET search_path TO auth_db, public;
--
---- Table de liaison User-Role
--CREATE TABLE user_roles (
--    user_id UUID REFERENCES users(id) ON DELETE CASCADE,
--    role_id UUID REFERENCES roles(id) ON DELETE CASCADE,
--    assigned_at TIMESTAMP DEFAULT NOW(),
--    PRIMARY KEY (user_id, role_id)
--);
--
---- Table de liaison Role-Permission
--CREATE TABLE role_permissions (
--    role_id UUID REFERENCES roles(id) ON DELETE CASCADE,
--    permission_id UUID REFERENCES permissions(id) ON DELETE CASCADE,
--    PRIMARY KEY (role_id, permission_id)
--);

-- Table de liaison User-Role
CREATE TABLE user_roles (
    user_id UUID REFERENCES users(id) ON DELETE CASCADE,
    role_id UUID REFERENCES roles(id) ON DELETE CASCADE,
    assigned_at TIMESTAMP DEFAULT NOW(),
    PRIMARY KEY (user_id, role_id)
);

-- Table de liaison Role-Permission
CREATE TABLE role_permissions (
    role_id UUID REFERENCES roles(id) ON DELETE CASCADE,
    permission_id UUID REFERENCES permissions(id) ON DELETE CASCADE,
    PRIMARY KEY (role_id, permission_id)
);