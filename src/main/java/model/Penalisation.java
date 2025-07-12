package model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "Penalisation")
public class Penalisation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idPenalisation")
    private Integer idPenalisation;

    @ManyToOne
    @JoinColumn(name = "idAdherent", referencedColumnName = "idAdherent")
    private Adherent adherent;

    @ManyToOne
    @JoinColumn(name = "idPret", referencedColumnName = "idPret")
    private Pret pret;

    @Column(name = "dateDebutPenalisation", nullable = false)
    private LocalDate dateDebutPenalisation;

    @Column(name = "dateFinPenalisation", nullable = false)
    private LocalDate dateFinPenalisation;

    // Getters and Setters
    public Integer getIdPenalisation() {
        return idPenalisation;
    }

    public void setIdPenalisation(Integer idPenalisation) {
        this.idPenalisation = idPenalisation;
    }

    public Adherent getAdherent() {
        return adherent;
    }

    public void setAdherent(Adherent adherent) {
        this.adherent = adherent;
    }

    public Pret getPret() {
        return pret;
    }

    public void setPret(Pret pret) {
        this.pret = pret;
    }

    public LocalDate getDateDebutPenalisation() {
        return dateDebutPenalisation;
    }

    public void setDateDebutPenalisation(LocalDate dateDebutPenalisation) {
        this.dateDebutPenalisation = dateDebutPenalisation;
    }

    public LocalDate getDateFinPenalisation() {
        return dateFinPenalisation;
    }

    public void setDateFinPenalisation(LocalDate dateFinPenalisation) {
        this.dateFinPenalisation = dateFinPenalisation;
    }
}