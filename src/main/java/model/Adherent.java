package model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "Adherent")
public class Adherent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idAdherent")
    private Integer idAdherent;

    @OneToOne
    @JoinColumn(name = "idPersonne", referencedColumnName = "idPersonne")
    private Personne personne;

    @ManyToOne
    @JoinColumn(name = "idProfil", referencedColumnName = "idProfil")
    private Profil profil;

    @Enumerated(EnumType.STRING)
    @Column(name = "statutAdherent", columnDefinition = "statut_adherent_enum")
    private StatutAdherentEnum statutAdherent;

    @Column(name = "dateAdhesion")
    private LocalDate dateAdhesion;

    @OneToOne(mappedBy = "adherent")
    private UserAccount userAccount;

    // Getters and Setters
    public Integer getIdAdherent() {
        return idAdherent;
    }

    public void setIdAdherent(Integer idAdherent) {
        this.idAdherent = idAdherent;
    }

    public Personne getPersonne() {
        return personne;
    }

    public void setPersonne(Personne personne) {
        this.personne = personne;
    }

    public Profil getProfil() {
        return profil;
    }

    public void setProfil(Profil profil) {
        this.profil = profil;
    }

    public StatutAdherentEnum getStatutAdherent() {
        return statutAdherent;
    }

    public void setStatutAdherent(StatutAdherentEnum statutAdherent) {
        this.statutAdherent = statutAdherent;
    }

    public LocalDate getDateAdhesion() {
        return dateAdhesion;
    }

    public void setDateAdhesion(LocalDate dateAdhesion) {
        this.dateAdhesion = dateAdhesion;
    }

    public UserAccount getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(UserAccount userAccount) {
        this.userAccount = userAccount;
    }
}

enum StatutAdherentEnum {
    ACTIF, SUSPENDU, INACTIF
}