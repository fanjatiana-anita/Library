package service;

     import model.Personne;
     import org.springframework.beans.factory.annotation.Autowired;
     import org.springframework.stereotype.Service;
     import repository.PersonneRepository;

     @Service
     public class PersonneService {

         @Autowired
         private PersonneRepository personneRepository;

         public Personne save(Personne personne) {
             return personneRepository.save(personne);
         }
     }