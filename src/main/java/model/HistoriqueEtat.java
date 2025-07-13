package model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "HistoriqueEtat")
public class HistoriqueEtat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idHistoriqueEtat")
    private Integer idHistoriqueEtat;

    @Column(name = "entite", nullable = false)
    private String entite;

    @Column(name = "id_entite", nullable = false)
    private Integer idEntite;

    @Column(name = "date_changement", nullable = false)
    private LocalDateTime dateChangement;

    @Column(name = "etat_avant", length = 100)
    private String etatAvant;

    @Column(name = "etat_apres", length = 100)
    private String etatApres;

    // Getters and Setters
    public Integer getIdHistoriqueEtat() {
        return idHistoriqueEtat;
    }

    public void setIdHistoriqueEtat(Integer idHistoriqueEtat) {
        this.idHistoriqueEtat = idHistoriqueEtat;
    }

    public String getEntite() {
        return entite;
    }

    public void setEntite(String entite) {
        this.entite = entite;
    }

    public Integer getIdEntite() {
        return idEntite;
    }

    public void setIdEntite(Integer idEntite) {
        this.idEntite = idEntite;
    }

    public LocalDateTime getDateChangement() {
        return dateChangement;
    }

    public void setDateChangement(LocalDateTime dateChangement) {
        this.dateChangement = dateChangement;
    }

    public String getEtatAvant() {
        return etatAvant;
    }

    public void setEtatAvant(String etatAvant) {
        this.etatAvant = etatAvant;
    }

    public String getEtatApres() {
        return etatApres;
    }

    public void setEtatApres(String etatApres) {
        this.etatApres = etatApres;
    }
}
