package model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "reservation")
public class Reservation {

    public enum StatutReservation {
        VALIDE,
        EN_ATTENTE_DE_VALIDATION
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idReservation;

    @ManyToOne
    @JoinColumn(name = "idAdherent")
    private Adherent adherent;

    @ManyToOne
    @JoinColumn(name = "idExemplaire")
    private Exemplaire exemplaire;

    private LocalDate dateDeReservation;

    private LocalDate dateDuPretPrevue;

    @Enumerated(EnumType.STRING)
    private StatutReservation statutReservation;

  

    public Integer getIdReservation() {
        return idReservation;
    }

    public void setIdReservation(Integer idReservation) {
        this.idReservation = idReservation;
    }

    public Adherent getAdherent() {
        return adherent;
    }

    public void setAdherent(Adherent adherent) {
        this.adherent = adherent;
    }

    public Exemplaire getExemplaire() {
        return exemplaire;
    }

    public void setExemplaire(Exemplaire exemplaire) {
        this.exemplaire = exemplaire;
    }

    public LocalDate getDateDeReservation() {
        return dateDeReservation;
    }

    public void setDateDeReservation(LocalDate dateDeReservation) {
        this.dateDeReservation = dateDeReservation;
    }

    public LocalDate getDateDuPretPrevue() {
        return dateDuPretPrevue;
    }

    public void setDateDuPretPrevue(LocalDate dateDuPretPrevue) {
        this.dateDuPretPrevue = dateDuPretPrevue;
    }

    public StatutReservation getStatutReservation() {
        return statutReservation;
    }

    public void setStatutReservation(StatutReservation statutReservation) {
        this.statutReservation = statutReservation;
    }
}
