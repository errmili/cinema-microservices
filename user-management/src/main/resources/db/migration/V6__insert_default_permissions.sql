-- Insertion des permissions par défaut dans le schéma auth_db

-- Définir le schéma de recherche
--SET search_path TO auth_db, public;
--
---- Permissions de base pour le système
---- Vérifier si les permissions existent déjà avant de les insérer
--INSERT INTO permissions (name, description, resource, action)
--SELECT * FROM (VALUES
--    ('USER_CREATE', 'Create new users', 'user', 'create'),
--    ('USER_READ', 'View user information', 'user', 'read'),
--    ('USER_UPDATE', 'Update user information', 'user', 'update'),
--    ('USER_DELETE', 'Delete users', 'user', 'delete'),
--
--    -- Role Management
--    ('ROLE_CREATE', 'Create new roles', 'role', 'create'),
--    ('ROLE_READ', 'View role information', 'role', 'read'),
--    ('ROLE_UPDATE', 'Update role information', 'role', 'update'),
--    ('ROLE_DELETE', 'Delete roles', 'role', 'delete'),
--
--    -- Project Management
--    ('PROJECT_CREATE', 'Create new projects', 'project', 'create'),
--    ('PROJECT_READ', 'View project information', 'project', 'read'),
--    ('PROJECT_UPDATE', 'Update project information', 'project', 'update'),
--    ('PROJECT_DELETE', 'Delete projects', 'project', 'delete'),
--
--    -- Task Management
--    ('TASK_CREATE', 'Create new tasks', 'task', 'create'),
--    ('TASK_READ', 'View task information', 'task', 'read'),
--    ('TASK_UPDATE', 'Update task information', 'task', 'update'),
--    ('TASK_DELETE', 'Delete tasks', 'task', 'delete'),
--    ('TASK_ASSIGN', 'Assign tasks to users', 'task', 'assign'),
--
--    -- Admin permissions
--    ('ADMIN_FULL_ACCESS', 'Full administrative access', 'admin', 'all'),
--    ('TENANT_MANAGE', 'Manage tenant settings', 'tenant', 'manage')
--) AS temp_permissions (name, description, resource, action)
--WHERE NOT EXISTS (
--    SELECT 1 FROM permissions p WHERE p.name = temp_permissions.name
--);


-- V6__insert_default_permissions.sql
-- Permissions de base pour le système

-- Vérifier si les permissions existent déjà avant de les insérer
INSERT INTO permissions (name, description, resource, action)
SELECT * FROM (VALUES
    ('USER_CREATE', 'Create new users', 'user', 'create'),
    ('USER_READ', 'View user information', 'user', 'read'),
    ('USER_UPDATE', 'Update user information', 'user', 'update'),
    ('USER_DELETE', 'Delete users', 'user', 'delete'),

    -- Role Management
    ('ROLE_CREATE', 'Create new roles', 'role', 'create'),
    ('ROLE_READ', 'View role information', 'role', 'read'),
    ('ROLE_UPDATE', 'Update role information', 'role', 'update'),
    ('ROLE_DELETE', 'Delete roles', 'role', 'delete'),

    -- Project Management
    ('PROJECT_CREATE', 'Create new projects', 'project', 'create'),
    ('PROJECT_READ', 'View project information', 'project', 'read'),
    ('PROJECT_UPDATE', 'Update project information', 'project', 'update'),
    ('PROJECT_DELETE', 'Delete projects', 'project', 'delete'),

    -- Task Management
    ('TASK_CREATE', 'Create new tasks', 'task', 'create'),
    ('TASK_READ', 'View task information', 'task', 'read'),
    ('TASK_UPDATE', 'Update task information', 'task', 'update'),
    ('TASK_DELETE', 'Delete tasks', 'task', 'delete'),
    ('TASK_ASSIGN', 'Assign tasks to users', 'task', 'assign'),

    -- Admin permissions
    ('ADMIN_FULL_ACCESS', 'Full administrative access', 'admin', 'all'),
    ('TENANT_MANAGE', 'Manage tenant settings', 'tenant', 'manage')
) AS temp_permissions (name, description, resource, action)
WHERE NOT EXISTS (
    SELECT 1 FROM permissions p WHERE p.name = temp_permissions.name
);