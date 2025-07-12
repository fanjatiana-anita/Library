package model;

import jakarta.persistence.*;

@Entity
@Table(name = "UserAccount")
public class UserAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idUserAccount")
    private Integer idUserAccount;

    @OneToOne
    @JoinColumn(name = "idPersonne", referencedColumnName = "idPersonne")
    private Personne personne;

    @Column(name = "login", nullable = false, unique = true)
    private String login;

    @Column(name = "motDePasse", nullable = false)
    private String motDePasse;

    @Column(name = "role", nullable = false)
    private String role;

    // Getters and Setters
    public Integer getIdUserAccount() {
        return idUserAccount;
    }

    public void setIdUserAccount(Integer idUserAccount) {
        this.idUserAccount = idUserAccount;
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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}