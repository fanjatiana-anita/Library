package repository;

import model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Integer> {
    List<Reservation> findByAdherentAndStatutReservationIn(Adherent adherent, List<Reservation.StatutReservationEnum> statuts);
    List<Reservation> findByExemplaireAndStatutReservation(Exemplaire exemplaire, Reservation.StatutReservationEnum statut);
}
