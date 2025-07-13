package model;

import jakarta.persistence.*;

@Entity
public class JourNonOuvrable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idJourNonOuvrable;

    @Column(nullable = false)
    private String type;

    @Column
    private Integer jourSemaine;

    @Column
    private java.time.LocalDate dateFerie;

    @Column(length = 150)
    private String description;

    // Getters and Setters
    public Integer getIdJourNonOuvrable() {
        return idJourNonOuvrable;
    }

    public void setIdJourNonOuvrable(Integer idJourNonOuvrable) {
        this.idJourNonOuvrable = idJourNonOuvrable;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getJourSemaine() {
        return jourSemaine;
    }

    public void setJourSemaine(Integer jourSemaine) {
        this.jourSemaine = jourSemaine;
    }

    public java.time.LocalDate getDateFerie() {
        return dateFerie;
    }

    public void setDateFerie(java.time.LocalDate dateFerie) {
        this.dateFerie = dateFerie;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
