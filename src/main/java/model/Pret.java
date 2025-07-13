package model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "Pret")
public class Pret {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idPret")
    private Integer idPret;

    @ManyToOne
    @JoinColumn(name = "idAdherent", nullable = false)
    private Adherent adherent;

    @ManyToOne
    @JoinColumn(name = "idExemplaire", nullable = false)
    private Exemplaire exemplaire;

    @Column(name = "dateDuPret", nullable = false)
    private LocalDate dateDuPret;

    @Column(name = "dateDeRetourPrevue", nullable = false)
    private LocalDate dateDeRetourPrevue;

    @Column(name = "dateDeRetourReelle")
    private LocalDate dateDeRetourReelle;

    // Getters and Setters
    public Integer getIdPret() {
        return idPret;
    }

    public void setIdPret(Integer idPret) {
        this.idPret = idPret;
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

    public LocalDate getDateDuPret() {
        return dateDuPret;
    }

    public void setDateDuPret(LocalDate dateDuPret) {
        this.dateDuPret = dateDuPret;
    }

    public LocalDate getDateDeRetourPrevue() {
        return dateDeRetourPrevue;
    }

    public void setDateDeRetourPrevue(LocalDate dateDeRetourPrevue) {
        this.dateDeRetourPrevue = dateDeRetourPrevue;
    }

    public LocalDate getDateDeRetourReelle() {
        return dateDeRetourReelle;
    }

    public void setDateDeRetourReelle(LocalDate dateDeRetourReelle) {
        this.dateDeRetourReelle = dateDeRetourReelle;
    }
}
