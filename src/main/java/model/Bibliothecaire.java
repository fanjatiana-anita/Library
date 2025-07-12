package model;

import jakarta.persistence.*;

@Entity
@Table(name = "bibliothecaire")
public class Bibliothecaire {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idBibliothecaire;

    @ManyToOne
    @JoinColumn(name = "idUserAccount")
    private UserAccount userAccount;


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
