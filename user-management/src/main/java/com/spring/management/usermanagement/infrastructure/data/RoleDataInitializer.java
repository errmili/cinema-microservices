package com.spring.management.usermanagement.infrastructure.data;


import jakarta.annotation.PostConstruct;

import com.spring.management.usermanagement.domain.entity.Role;
import com.spring.management.usermanagement.domain.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


@Component
@Transactional
@Profile("docker")  // SEULEMENT en Docker !
public class RoleDataInitializer {

    @Autowired
    private RoleRepository roleRepository;

    @PostConstruct
    public void initRoles() {
        System.out.println("🔧 [RoleInitializer] Vérification des rôles...");

        createRoleIfNotExists("ROLE_USER");
        createRoleIfNotExists("ROLE_ADMIN");
        createRoleIfNotExists("ROLE_MODERATOR");
        createRoleIfNotExists("ROLE_MANAGER");
        createRoleIfNotExists("ROLE_SUPER_ADMIN");

        System.out.println("✅ [RoleInitializer] Tous les rôles sont prêts !");
    }

    private void createRoleIfNotExists(String roleName) {
        if (!roleRepository.existsByName(roleName)) {
            Role role = new Role();
            role.setName(roleName);
            roleRepository.save(role);
            System.out.println("✅ [RoleInitializer] Rôle créé: " + roleName);
        } else {
            System.out.println("ℹ️ [RoleInitializer] Rôle existe déjà: " + roleName);
        }
    }
}