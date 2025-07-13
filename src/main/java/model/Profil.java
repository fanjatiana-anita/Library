package model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "Profil")
public class Profil {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idProfil")
    private Integer idProfil;

    @Column(name = "profil")
    private String profil;

    @Column(name = "montantCotisation")
    private BigDecimal montantCotisation;

    @Column(name = "quotaMaxPret")
    private Integer quotaMaxPret;

    @Column(name = "quotaMaxReservation")
    private Integer quotaMaxReservation;

    @Column(name = "quotaMaxProlongement")
    private Integer quotaMaxProlongement;

    @Column(name = "dureePenalite")
    private Integer dureePenalite;

    @Column(name = "dureeMaxPret")
    private Integer dureeMaxPret;

    @Column(name = "dureeAbonnement")
    private Integer dureeAbonnement;

    public Integer getIdProfil() {
        return idProfil;
    }

    public void setIdProfil(Integer idProfil) {
        this.idProfil = idProfil;
    }

    public String getProfil() {
        return profil;
    }

    public void setProfil(String profil) {
        this.profil = profil;
    }

    public BigDecimal getMontantCotisation() {
        return montantCotisation;
    }

    public void setMontantCotisation(BigDecimal montantCotisation) {
        this.montantCotisation = montantCotisation;
    }

    public Integer getQuotaMaxPret() {
        return quotaMaxPret;
    }

    public void setQuotaMaxPret(Integer quotaMaxPret) {
        this.quotaMaxPret = quotaMaxPret;
    }

    public Integer getQuotaMaxReservation() {
        return quotaMaxReservation;
    }

    public void setQuotaMaxReservation(Integer quotaMaxReservation) {
        this.quotaMaxReservation = quotaMaxReservation;
    }

    public Integer getQuotaMaxProlongement() {
        return quotaMaxProlongement;
    }

    public void setQuotaMaxProlongement(Integer quotaMaxProlongement) {
        this.quotaMaxProlongement = quotaMaxProlongement;
    }

    public Integer getDureePenalite() {
        return dureePenalite;
    }

    public void setDureePenalite(Integer dureePenalite) {
        this.dureePenalite = dureePenalite;
    }

    public Integer getDureeMaxPret() {
        return dureeMaxPret;
    }

    public void setDureeMaxPret(Integer dureeMaxPret) {
        this.dureeMaxPret = dureeMaxPret;
    }

    public Integer getDureeAbonnement() {
        return dureeAbonnement;
    }

    public void setDureeAbonnement(Integer dureeAbonnement) {
        this.dureeAbonnement = dureeAbonnement;
    }
}