package model;

import jakarta.persistence.*;

@Entity
@Table(name = "RestrictionProfilLivre")
public class RestrictionProfilLivre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idRestrictionProfilLivre")
    private Integer idRestrictionProfilLivre;

    @ManyToOne
    @JoinColumn(name = "idLivre", referencedColumnName = "idLivre")
    private Livre livre;

    @Column(name = "ageMinRequis")
    private Integer ageMinRequis;

    @ManyToOne
    @JoinColumn(name = "idProfil", referencedColumnName = "idProfil")
    private Profil profil;

    // Getters and Setters
    public Integer getIdRestrictionProfilLivre() {
        return idRestrictionProfilLivre;
    }

    public void setIdRestrictionProfilLivre(Integer idRestrictionProfilLivre) {
        this.idRestrictionProfilLivre = idRestrictionProfilLivre;
    }

    public Livre getLivre() {
        return livre;
    }

    public void setLivre(Livre livre) {
        this.livre = livre;
    }

    public Integer getAgeMinRequis() {
        return ageMinRequis;
    }

    public void setAgeMinRequis(Integer ageMinRequis) {
        this.ageMinRequis = ageMinRequis;
    }

    public Profil getProfil() {
        return profil;
    }

    public void setProfil(Profil profil) {
        this.profil = profil;
    }
}