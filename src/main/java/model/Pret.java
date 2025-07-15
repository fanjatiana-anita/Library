package model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
public class Pret {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idPret;

    @ManyToOne
    @JoinColumn(name = "idAdherent")
    private Adherent adherent;

    @ManyToOne
    @JoinColumn(name = "idExemplaire")
    private Exemplaire exemplaire;

    private LocalDate dateDuPret;

    private LocalDate dateDeRetourPrevue;

    private LocalDate dateDeRetourReelle;

    private Integer nombreProlongement;

    @OneToMany(mappedBy = "pret", cascade = CascadeType.ALL)
    private List<Prolongement> prolongements;

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

    public Integer getNombreProlongement() {
        return nombreProlongement;
    }

    public void setNombreProlongement(Integer nombreProlongement) {
        this.nombreProlongement = nombreProlongement;
    }

    public List<Prolongement> getProlongements() {
        return prolongements;
    }

    public void setProlongements(List<Prolongement> prolongements) {
        this.prolongements = prolongements;
    }
}