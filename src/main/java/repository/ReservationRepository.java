package repository;

import model.Adherent;
import model.Exemplaire;
import model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Integer> {
    List<Reservation> findByAdherentAndStatutReservationIn(Adherent adherent, List<Reservation.StatutReservationEnum> statuts);
    List<Reservation> findByExemplaireAndStatutReservation(Exemplaire exemplaire, Reservation.StatutReservationEnum statut);
    List<Reservation> findByAdherent(Adherent adherent);
    List<Reservation> findByStatutReservation(Reservation.StatutReservationEnum statut);
}
