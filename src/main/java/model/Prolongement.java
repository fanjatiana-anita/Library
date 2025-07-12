package model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "prolongement")
public class Prolongement {

    public enum StatutProlongement {
        VALIDE,
        EN_ATTENTE_DE_VALIDATION
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idProlongement;

    @ManyToOne
    @JoinColumn(name = "idPret")
    private Pret pret;

    private LocalDate dateDeDemande;

    @Enumerated(EnumType.STRING)
    private StatutProlongement statutProlongement;



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

    public StatutProlongement getStatutProlongement() {
        return statutProlongement;
    }

    public void setStatutProlongement(StatutProlongement statutProlongement) {
        this.statutProlongement = statutProlongement;
    }
}
