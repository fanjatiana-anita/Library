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

    @Enumerated(EnumType.STRING)
    @Column(name = "entite", columnDefinition = "entite_enum")
    private EntiteEnum entite;

    @Column(name = "idEntite")
    private Integer idEntite;

    @Column(name = "statut")
    private String statut;

    @Column(name = "dateStatut")
    private LocalDateTime dateStatut;

    // Getters and Setters
    public Integer getIdHistoriqueEtat() {
        return idHistoriqueEtat;
    }

    public void setIdHistoriqueEtat(Integer idHistoriqueEtat) {
        this.idHistoriqueEtat = idHistoriqueEtat;
    }

    public EntiteEnum getEntite() {
        return entite;
    }

    public void setEntite(EntiteEnum entite) {
        this.entite = entite;
    }

    public Integer getIdEntite() {
        return idEntite;
    }

    public void setIdEntite(Integer idEntite) {
        this.idEntite = idEntite;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public LocalDateTime getDateStatut() {
        return dateStatut;
    }

    public void setDateStatut(LocalDateTime dateStatut) {
        this.dateStatut = dateStatut;
    }
}

enum EntiteEnum {
    PRET, RESERVATION, PROLONGEMENT
}