package model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "Prolongement")
public class Prolongement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idProlongement")
    private Integer idProlongement;

    @ManyToOne
    @JoinColumn(name = "idPret", referencedColumnName = "idPret")
    private Pret pret;

    @Column(name = "dateDeDemande", nullable = false)
    private LocalDate dateDeDemande;

    @Enumerated(EnumType.STRING)
    @Column(name = "statutProlongement", nullable = false)
    private StatutProlongementEnum statutProlongement;

    // Getters and Setters
    public Integer getIdProlongement() {
        return idProlongement;
    }

    public void setIdProlongement(Integer idProlongement) {
        this.idProlongement = idProlongement;
    }

    public Pret getPret() {
        return pret;
    }

    public void setPret(Pret pret) {
        this.pret = pret;
    }

    public LocalDate getDateDeDemande() {
        return dateDeDemande;
    }

    public void setDateDeDemande(LocalDate dateDeDemande) {
        this.dateDeDemande = dateDeDemande;
    }

    public StatutProlongementEnum getStatutProlongement() {
        return statutProlongement;
    }

    public void setStatutProlongement(StatutProlongementEnum statutProlongement) {
        this.statutProlongement = statutProlongement;
    }
}

enum StatutProlongementEnum {
    EN_ATTENTE, VALIDE, REFUSE
}