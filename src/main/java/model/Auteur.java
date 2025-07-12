package model;

import jakarta.persistence.*;

@Entity
@Table(name = "Auteur")
public class Auteur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idAuteur")
    private Integer idAuteur;

    @OneToOne
    @JoinColumn(name = "idPersonne", referencedColumnName = "idPersonne")
    private Personne personne;

    // Getters and Setters
    public Integer getIdAuteur() {
        return idAuteur;
    }

    public void setIdAuteur(Integer idAuteur) {
        this.idAuteur = idAuteur;
    }

    public Personne getPersonne() {
        return personne;
    }

    public void setPersonne(Personne personne) {
        this.personne = personne;
    }
}