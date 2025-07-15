package model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class Prolongement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idProlongement;

    @ManyToOne
    @JoinColumn(name = "idPret")
    private Pret pret;

    @ManyToOne
    @JoinColumn(name = "idAdherent")
    private Adherent adherent;

    private LocalDate dateDemandeProlongement;

    private LocalDate dateRetourPrevueApresProlongement;

    @Enumerated(EnumType.STRING)
    private StatutProlongementEnum statutProlongement;

    public enum StatutProlongementEnum {
        EN_ATTENTE,
        VALIDE,
        REFUSE
    }

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

    public Adherent getAdherent() {
        return adherent;
    }

    public void setAdherent(Adherent adherent) {
        this.adherent = adherent;
    }

    public LocalDate getDateDemandeProlongement() {
        return dateDemandeProlongement;
    }

    public void setDateDemandeProlongement(LocalDate dateDemandeProlongement) {
        this.dateDemandeProlongement = dateDemandeProlongement;
    }

    public LocalDate getDateRetourPrevueApresProlongement() {
        return dateRetourPrevueApresProlongement;
    }

    public void setDateRetourPrevueApresProlongement(LocalDate dateRetourPrevueApresProlongement) {
        this.dateRetourPrevueApresProlongement = dateRetourPrevueApresProlongement;
    }

    public StatutProlongementEnum getStatutProlongement() {
        return statutProlongement;
    }

    public void setStatutProlongement(StatutProlongementEnum statutProlongement) {
        this.statutProlongement = statutProlongement;
    }
}