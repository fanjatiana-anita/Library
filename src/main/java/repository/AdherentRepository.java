package repository;

     import model.Adherent;
     import org.springframework.data.jpa.repository.JpaRepository;

     public interface AdherentRepository extends JpaRepository<Adherent, Integer> {
     }