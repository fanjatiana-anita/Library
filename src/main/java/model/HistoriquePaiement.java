package model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "HistoriquePaiement")
public class HistoriquePaiement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idPaiement")
    private Integer idPaiement;

    @ManyToOne
    @JoinColumn(name = "idAdherent")
    private Adherent adherent;

    @Column(name = "datePaiement")
    private LocalDate datePaiement;

    @Column(name = "montantCotisation")
    private BigDecimal montantCotisation;

    public Integer getIdPaiement() {
        return idPaiement;
    }

    public void setIdPaiement(Integer idPaiement) {
        this.idPaiement = idPaiement;
    }

    public Adherent getAdherent() {
        return adherent;
    }

    public void setAdherent(Adherent adherent) {
        this.adherent = adherent;
    }

    public LocalDate getDatePaiement() {
        return datePaiement;
    }

    public void setDatePaiement(LocalDate datePaiement) {
        this.datePaiement = datePaiement;
    }

    public BigDecimal getMontantCotisation() {
        return montantCotisation;
    }

    public void setMontantCotisation(BigDecimal montantCotisation) {
        this.montantCotisation = montantCotisation;
    }
}