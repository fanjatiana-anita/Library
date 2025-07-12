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

    @Column(name = "profil", nullable = false)
    private String profil;

    @Column(name = "montantCotisation", precision = 10, scale = 2)
    private BigDecimal montantCotisation;

    @Column(name = "quotaMaxPret", nullable = false)
    private Integer quotaMaxPret;

    @Column(name = "quotaMaxReservation", nullable = false)
    private Integer quotaMaxReservation;

    @Column(name = "quotaMaxProlongement", nullable = false)
    private Integer quotaMaxProlongement;

    @Column(name = "dureePenalite", nullable = false)
    private Integer dureePenalite;

    @Column(name = "dureeMaxPret", nullable = false)
    private Integer dureeMaxPret;

    // Getters and Setters
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
}