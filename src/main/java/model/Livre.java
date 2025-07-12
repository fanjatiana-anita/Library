package model;

import jakarta.persistence.*;

@Entity
@Table(name = "Livre")
public class Livre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idLivre")
    private Integer idLivre;

    @Column(name = "titreLivre", nullable = false)
    private String titreLivre;

    @ManyToOne
    @JoinColumn(name = "idAuteur", referencedColumnName = "idAuteur")
    private Auteur auteur;

    // Getters and Setters
    public Integer getIdLivre() {
        return idLivre;
    }

    public void setIdLivre(Integer idLivre) {
        this.idLivre = idLivre;
    }

    public String getTitreLivre() {
        return titreLivre;
    }

    public void setTitreLivre(String titreLivre) {
        this.titreLivre = titreLivre;
    }

    public Auteur getAuteur() {
        return auteur;
    }

    public void setAuteur(Auteur auteur) {
        this.auteur = auteur;
    }
}