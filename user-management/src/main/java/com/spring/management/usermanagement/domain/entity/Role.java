package com.spring.management.usermanagement.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "roles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name; // Exemple : ROLE_USER, ROLE_ADMIN

    /*
    pas de user ici :
    C’est ce qu’on appelle une relation bidirectionnelle quand tu mets @ManyToMany des deux côtés.
Mais ici, on a choisi une relation unidirectionnelle (seulement du côté User).

Pourquoi ne pas la mettre côté Role ?
Simplicité :
Si l'application n'a pas besoin d'accéder aux utilisateurs d’un rôle, alors ça ne sert à rien de charger ce mapping.

Performance & surcharge :
Charger un Role avec des centaines/milliers d’utilisateurs ? Mauvaise idée sauf si nécessaire.

Respect de DDD :
Le User est l’agrégat racine ici. Donc c’est lui qui possède la relation. Le Role est juste une valeur ou entité secondaire.
     */
}