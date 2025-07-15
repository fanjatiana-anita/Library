package model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.Date;

@Entity
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idReservation;

    @ManyToOne
    @JoinColumn(name = "idAdherent", nullable = false)
    private Adherent adherent;

    @ManyToOne
    @JoinColumn(name = "idExemplaire", nullable = false)
    private Exemplaire exemplaire;

    @Column(nullable = false)
    private LocalDate dateDeReservation;

    @Column(nullable = false)
    private LocalDate dateDuPretPrevue;

    private LocalDate dateValidation;

    private LocalDate dateLimiteRecuperation;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutReservationEnum statutReservation;

    @Transient
    private boolean isLateValidation;

    @Transient
    private Date dateLimiteRecuperationAsDate;

    public enum StatutReservationEnum {
        EN_ATTENTE, VALIDE, REFUSEE, EXPIREE, COMPLETEE
    }

    // Getters and setters
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

    public LocalDate getDateValidation() {
        return dateValidation;
    }

    public void setDateValidation(LocalDate dateValidation) {
        this.dateValidation = dateValidation;
    }

    public LocalDate getDateLimiteRecuperation() {
        return dateLimiteRecuperation;
    }

    public void setDateLimiteRecuperation(LocalDate dateLimiteRecuperation) {
        this.dateLimiteRecuperation = dateLimiteRecuperation;
    }

    public StatutReservationEnum getStatutReservation() {
        return statutReservation;
    }

    public void setStatutReservation(StatutReservationEnum statutReservation) {
        this.statutReservation = statutReservation;
    }

    public boolean getIsLateValidation() {
        return isLateValidation;
    }

    public void setLateValidation(boolean lateValidation) {
        this.isLateValidation = lateValidation;
    }

    public Date getDateLimiteRecuperationAsDate() {
        return dateLimiteRecuperationAsDate;
    }

    public void setDateLimiteRecuperationAsDate(Date dateLimiteRecuperationAsDate) {
        this.dateLimiteRecuperationAsDate = dateLimiteRecuperationAsDate;
    }
}
