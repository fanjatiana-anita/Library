package model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "Reservation")
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idReservation")
    private Integer idReservation;

    @ManyToOne
    @JoinColumn(name = "idAdherent", referencedColumnName = "idAdherent")
    private Adherent adherent;

    @ManyToOne
    @JoinColumn(name = "idExemplaire", referencedColumnName = "idExemplaire")
    private Exemplaire exemplaire;

    @Column(name = "dateDeReservation")
    private LocalDate dateDeReservation;

    @Column(name = "dateDuPretPrevue")
    private LocalDate dateDuPretPrevue;

    @Enumerated(EnumType.STRING)
    @Column(name = "statutReservation")
    private StatutReservationEnum statutReservation;

    public enum StatutReservationEnum {
        EN_ATTENTE, VALIDE, REFUSE
    }

    // Getters and Setters
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

    public StatutReservationEnum getStatutReservation() {
        return statutReservation;
    }

    public void setStatutReservation(StatutReservationEnum statutReservation) {
        this.statutReservation = statutReservation;
    }
}