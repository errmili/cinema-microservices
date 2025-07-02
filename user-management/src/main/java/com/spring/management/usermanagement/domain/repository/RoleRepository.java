package com.spring.management.usermanagement.domain.repository;

import com.spring.management.usermanagement.domain.entity.Role;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

    public interface RoleRepository extends JpaRepository<Role, Long> {
        Optional<Role> findByName(String name);

        boolean existsByName(String name);
    }