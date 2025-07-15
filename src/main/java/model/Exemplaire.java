package model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

@Entity
@Table(name = "Exemplaire")
public class Exemplaire {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idExemplaire")
    private Integer idExemplaire;

    @ManyToOne
    @JoinColumn(name = "idLivre", referencedColumnName = "idLivre")
    private Livre livre;

    @Enumerated(EnumType.STRING)
    @Column(name = "statutExemplaire", nullable = false)
    private StatutExemplaireEnum statutExemplaire;

    public enum StatutExemplaireEnum {
        DISPONIBLE, RESERVE, EN_PRET
    }

    @Transient
    @JsonProperty("livreId")
    public Integer getLivreId() {
        return livre != null ? livre.getIdLivre() : null;
    }

    @Transient
    @JsonProperty("titreLivre")
    public String getTitreLivre() {
        return livre != null ? livre.getTitreLivre() : null;
    }

    // Getters and Setters
    public Integer getIdExemplaire() {
        return idExemplaire;
    }

    public void setIdExemplaire(Integer idExemplaire) {
        this.idExemplaire = idExemplaire;
    }

    public Livre getLivre() {
        return livre;
    }

    public void setLivre(Livre livre) {
        this.livre = livre;
    }

    public StatutExemplaireEnum getStatutExemplaire() {
        return statutExemplaire;
    }

    public void setStatutExemplaire(StatutExemplaireEnum statutExemplaire) {
        this.statutExemplaire = statutExemplaire;
    }
}