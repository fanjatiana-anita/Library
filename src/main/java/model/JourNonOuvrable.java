package model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "JourNonOuvrable")
public class JourNonOuvrable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idJourNonOuvrable")
    private Integer idJourNonOuvrable;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", columnDefinition = "type_jour_enum")
    private TypeJourEnum type;

    @Column(name = "jourSemaine")
    private Short jourSemaine;

    @Column(name = "dateFerie")
    private LocalDate dateFerie;

    @Column(name = "description")
    private String description;

    // Getters and Setters
    public Integer getIdJourNonOuvrable() {
        return idJourNonOuvrable;
    }

    public void setIdJourNonOuvrable(Integer idJourNonOuvrable) {
        this.idJourNonOuvrable = idJourNonOuvrable;
    }

    public TypeJourEnum getType() {
        return type;
    }

    public void setType(TypeJourEnum type) {
        this.type = type;
    }

    public Short getJourSemaine() {
        return jourSemaine;
    }

    public void setJourSemaine(Short jourSemaine) {
        this.jourSemaine = jourSemaine;
    }

    public LocalDate getDateFerie() {
        return dateFerie;
    }

    public void setDateFerie(LocalDate dateFerie) {
        this.dateFerie = dateFerie;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

enum TypeJourEnum {
    FERIE, HEBDOMADAIRE, EXCEPTIONNEL
}