package model;

  import jakarta.persistence.*;
  import java.time.LocalDate;

  @Entity
  @Table(name = "Abonnement")
  public class Abonnement {

      @Id
      @GeneratedValue(strategy = GenerationType.IDENTITY)
      @Column(name = "idAbonnement")
      private Integer idAbonnement;

      @ManyToOne
      @JoinColumn(name = "idAdherent", referencedColumnName = "idAdherent")
      private Adherent adherent;

      @Column(name = "dateDebut", nullable = false)
      private LocalDate dateDebut;

      @Column(name = "dateFin", nullable = false)
      private LocalDate dateFin;

      // Getters and Setters
      public Integer getIdAbonnement() {
          return idAbonnement;
      }

      public void setIdAbonnement(Integer idAbonnement) {
          this.idAbonnement = idAbonnement;
      }

      public Adherent getAdherent() {
          return adherent;
      }

      public void setAdherent(Adherent adherent) {
          this.adherent = adherent;
      }

      public LocalDate getDateDebut() {
          return dateDebut;
      }

      public void setDateDebut(LocalDate dateDebut) {
          this.dateDebut = dateDebut;
      }

      public LocalDate getDateFin() {
          return dateFin;
      }

      public void setDateFin(LocalDate dateFin) {
          this.dateFin = dateFin;
      }
  }