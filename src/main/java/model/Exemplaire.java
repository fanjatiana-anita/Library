package model;

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
    @Column(name = "statutExemplaire", columnDefinition = "statut_exemplaire_enum")
    private StatutExemplaire statutExemplaire;

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

    public StatutExemplaire getStatutExemplaire() {
        return statutExemplaire;
    }

    public void setStatutExemplaire(StatutExemplaire statutExemplaire) {
        this.statutExemplaire = statutExemplaire;
    }
}

enum StatutExemplaire {
    DISPONIBLE, RESERVE, EN_PRET
}
