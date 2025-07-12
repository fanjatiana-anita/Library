package model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "listelecture")
public class ListeLecture {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idListeLecture;

    @ManyToOne
    @JoinColumn(name = "idAdherent")
    private Adherent adherent;

    @ManyToOne
    @JoinColumn(name = "idExemplaire")
    private Exemplaire exemplaire;

    private LocalDateTime debutLecture;

    private LocalDateTime finLecture;


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
