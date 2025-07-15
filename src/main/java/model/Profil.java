package model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
public class Profil {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idProfil;

    @Column(nullable = false)
    private String profil;

    @Column
    private BigDecimal montantCotisation;

    @Column(nullable = false)
    private Integer quotaMaxPret;

    @Column(nullable = false)
    private Integer quotaMaxReservation;

    @Column(nullable = false)
    private Integer quotaMaxProlongement;

    @Column(nullable = false)
    private Integer dureePenalite;

    @Column(nullable = false)
    private Integer dureeMaxPret;

    @Column(nullable = false)
    private Integer delaiSupplementaireReservation = 2;

    @Column(nullable = false)
    private Integer dureeAbonnement;

    // Getters and setters
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

    public Integer getDelaiSupplementaireReservation() {
        return delaiSupplementaireReservation;
    }

    public void setDelaiSupplementaireReservation(Integer delaiSupplementaireReservation) {
        this.delaiSupplementaireReservation = delaiSupplementaireReservation;
    }

    public Integer getDureeAbonnement() {
        return dureeAbonnement;
    }

    public void setDureeAbonnement(Integer dureeAbonnement) {
        this.dureeAbonnement = dureeAbonnement;
    }
}
