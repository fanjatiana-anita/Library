package model;

import jakarta.persistence.*;

@Entity
@Table(name = "Bibliothecaire")
public class Bibliothecaire {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idBibliothecaire")
    private Integer idBibliothecaire;

    @OneToOne
    @JoinColumn(name = "idUserAccount", referencedColumnName = "idUserAccount")
    private UserAccount userAccount;

    // Getters and Setters
    public Integer getIdBibliothecaire() {
        return idBibliothecaire;
    }

    public void setIdBibliothecaire(Integer idBibliothecaire) {
        this.idBibliothecaire = idBibliothecaire;
    }

    public UserAccount getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(UserAccount userAccount) {
        this.userAccount = userAccount;
    }
}