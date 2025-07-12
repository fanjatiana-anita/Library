package model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "penalisation")
public class Penalisation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idPenalisation;

    @ManyToOne
    @JoinColumn(name = "idAdherent")
    private Adherent adherent;

    @ManyToOne
    @JoinColumn(name = "idPret")
    private Pret pret;

    private LocalDate dateDebutPenalisation;

    private LocalDate dateFinPenalisation;


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
