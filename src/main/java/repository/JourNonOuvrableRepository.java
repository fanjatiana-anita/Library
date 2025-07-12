package repository;

import model.JourNonOuvrable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface JourNonOuvrableRepository extends JpaRepository<JourNonOuvrable, Integer> {
    // List<JourNonOuvrable> findByDateFerie(LocalDate date);
}