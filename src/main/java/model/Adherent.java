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
    @JoinColumn(name = "idUserAccount", referencedColumnName = "idUserAccount")
    private UserAccount userAccount;

    @ManyToOne
    @JoinColumn(name = "idProfil", referencedColumnName = "idProfil")
    private Profil profil;

    @Enumerated(EnumType.STRING)
    @Column(name = "statutAdherent")
    private StatutAdherentEnum statutAdherent;

    @Column(name = "dateAdhesion")
    private LocalDate dateAdhesion;

    // Getters and Setters
    public Integer getIdAdherent() {
        return idAdherent;
    }

    public void setIdAdherent(Integer idAdherent) {
        this.idAdherent = idAdherent;
    }

    public UserAccount getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(UserAccount userAccount) {
        this.userAccount = userAccount;
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
}

enum StatutAdherentEnum {
    ACTIF, SUSPENDU, INACTIF
}