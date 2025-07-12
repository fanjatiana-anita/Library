package model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "Profil")
public class Profil {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idProfil")
    private Integer idProfil; // Supposé Integer basé sur SERIAL dans schema.sql

    @Column(name = "profil")
    private String profil;

    @Column(name = "montantCotisation")
    private BigDecimal montantCotisation; // Changé de Double à BigDecimal

    @Column(name = "quotaMax")
    private Integer quotaMax;

    @Column(name = "quotaProlongement")
    private Integer quotaProlongement;

    @Column(name = "quotaReservation")
    private Integer quotaReservation;

    @Column(name = "dureePenalite")
    private Integer dureePenalite;

    @Column(name = "dureeMaxPret")
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

    public Integer getQuotaMax() {
        return quotaMax;
    }

    public void setQuotaMax(Integer quotaMax) {
        this.quotaMax = quotaMax;
    }

    public Integer getQuotaProlongement() {
        return quotaProlongement;
    }

    public void setQuotaProlongement(Integer quotaProlongement) {
        this.quotaProlongement = quotaProlongement;
    }

    public Integer getQuotaReservation() {
        return quotaReservation;
    }

    public void setQuotaReservation(Integer quotaReservation) {
        this.quotaReservation = quotaReservation;
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