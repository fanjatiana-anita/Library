package model;

import jakarta.persistence.*;

@Entity
@Table(name = "useraccount")
public class UserAccount {

    public enum Role {
        BIBLIOTHECAIRE,
        ADHERENT
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idUserAccount;

    @ManyToOne
    @JoinColumn(name = "idpersonne")
    private Personne personne;

    @Column(nullable = false, unique = true)
    private String login;

    @Column(nullable = false)
    private String motDePasse;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    private boolean estActif = true;

    @Column(nullable = false)
    private boolean doitChangerMotDePasse = true;

    // Getters et setters
    public Integer getIdUserAccount() {
        return idUserAccount;
    }

    public void setIdUserAccount(Integer id) {
        this.idUserAccount = id;
    }

    public Personne getPersonne() {
        return personne;
    }

    public void setPersonne(Personne personne) {
        this.personne = personne;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getMotDePasse() {
        return motDePasse;
    }

    public void setMotDePasse(String motDePasse) {
        this.motDePasse = motDePasse;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public boolean isEstActif() {
        return estActif;
    }

    public void setEstActif(boolean estActif) {
        this.estActif = estActif;
    }

    public boolean isDoitChangerMotDePasse() {
        return doitChangerMotDePasse;
    }

    public void setDoitChangerMotDePasse(boolean doitChangerMotDePasse) {
        this.doitChangerMotDePasse = doitChangerMotDePasse;
    }
}