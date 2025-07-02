package com.cinema.booking.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cinema.booking.domain.models.Ticket;

public interface TicketRepository extends JpaRepository<Ticket, Long> {

    boolean existsByPlaceIdAndReserveTrue(Long placeId);

//    boolean existsByProjectionIdAndPlaceIdAndStatut(Long projectionId, Long placeId, String statut);

    boolean existsByProjectionIdAndPlaceIdAndReserveTrue(Long projectionId, Long placeId);
    /*
     Optimisation des Performances :
     L'optimisation des performances est cruciale, surtout quand ton application commence à traiter des volumes de données importants.

    Exemples d'optimisation :
    Utilisation de @Query avec pagination : Si tu dois récupérer beaucoup d'éléments, la pagination est une bonne solution.
     Spring Data JPA offre des moyens pour le faire facilement.

    public interface TicketRepository extends JpaRepository<Ticket, Long> {
        @Query("SELECT t FROM Ticket t WHERE t.salle.id = :salleId")
        Page<Ticket> findTicketsBySalle(@Param("salleId") Long salleId, Pageable pageable);
    }

    Indexation des bases de données : Assure-toi que les requêtes fréquemment utilisées sont optimisées par des index
    dans ta base de données.

    Utilisation de Cache : Si tu as des données qui ne changent pas fréquemment (par exemple, les informations de la salle),
    tu peux les mettre en cache pour éviter de refaire des requêtes coûteuses. Voici un exemple d'utilisation avec Spring Cache :

    @Cacheable("tickets")
    public TicketDTO getTicketById(Long id) {
        return ticketRepository.findById(id).map(TicketDTO::fromEntity).orElseThrow();
    }
     */
}
