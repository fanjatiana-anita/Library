package model;

import jakarta.persistence.*;

@Entity
public class RestrictionProfilLivre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idRestrictionProfilLivre;

    @ManyToOne
    @JoinColumn(name = "idLivre")
    private Livre livre;

    private Integer ageMinRequis;

    @ManyToOne
    @JoinColumn(name = "idProfil")
    private Profil profil;

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

    public Integer getIdProfil() {
        return profil != null ? profil.getIdProfil() : null;
    }
}