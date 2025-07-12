package model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "ListeLecture")
public class ListeLecture {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idListeLecture")
    private Integer idListeLecture;

    @ManyToOne
    @JoinColumn(name = "idAdherent", referencedColumnName = "idAdherent")
    private Adherent adherent;

    @ManyToOne
    @JoinColumn(name = "idExemplaire", referencedColumnName = "idExemplaire")
    private Exemplaire exemplaire;

    @Column(name = "debutLecture", nullable = false)
    private LocalDateTime debutLecture;

    @Column(name = "finLecture")
    private LocalDateTime finLecture;

    // Getters and Setters
    public Integer getIdListeLecture() {
        return idListeLecture;
    }

    public void setIdListeLecture(Integer idListeLecture) {
        this.idListeLecture = idListeLecture;
    }

    public Adherent getAdherent() {
        return adherent;
    }

    public void setAdherent(Adherent adherent) {
        this.adherent = adherent;
    }

    public Exemplaire getExemplaire() {
        return exemplaire;
    }

    public void setExemplaire(Exemplaire exemplaire) {
        this.exemplaire = exemplaire;
    }

    public LocalDateTime getDebutLecture() {
        return debutLecture;
    }

    public void setDebutLecture(LocalDateTime debutLecture) {
        this.debutLecture = debutLecture;
    }

    public LocalDateTime getFinLecture() {
        return finLecture;
    }

    public void setFinLecture(LocalDateTime finLecture) {
        this.finLecture = finLecture;
    }
}