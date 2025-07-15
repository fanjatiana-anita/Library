    DROP DATABASE IF EXISTS library_mvc;
    CREATE DATABASE library_mvc;
    \c library_mvc;

    -- Création des tables
    CREATE TABLE Personne (
        idPersonne SERIAL PRIMARY KEY,
        nomPersonne VARCHAR(100) NOT NULL,
        dateDeNaissance DATE NOT NULL,
        sexe VARCHAR(10),
        adresse TEXT
    );

    CREATE TABLE UserAccount (
        idUserAccount SERIAL PRIMARY KEY,
        idPersonne INTEGER REFERENCES Personne(idPersonne) ON DELETE CASCADE,
        login VARCHAR(20) UNIQUE NOT NULL,
        motDePasse VARCHAR(100) NOT NULL,
        role VARCHAR(20) NOT NULL CHECK (role IN ('MEMBRE', 'BIBLIOTHECAIRE'))
    );

    CREATE TABLE Profil (
        idProfil SERIAL PRIMARY KEY,
        profil VARCHAR(255) NOT NULL,
        montantCotisation NUMERIC(10,2),
        quotaMaxPret INTEGER NOT NULL,
        quotaMaxReservation INTEGER NOT NULL,
        quotaMaxProlongement INTEGER NOT NULL,
        dureePenalite INTEGER NOT NULL,
        dureeMaxPret INTEGER NOT NULL,
        delaiSupplementaireReservation INTEGER NOT NULL DEFAULT 2,
        dureeAbonnement INTEGER NOT NULL
    );

    CREATE TABLE Adherent (
        idAdherent SERIAL PRIMARY KEY,
        idUserAccount INTEGER UNIQUE REFERENCES UserAccount(idUserAccount) ON DELETE CASCADE,
        idProfil INTEGER REFERENCES Profil(idProfil) ON DELETE RESTRICT,
        statutAdherent VARCHAR(20) NOT NULL CHECK (statutAdherent IN ('ACTIF', 'SUSPENDU', 'INACTIF')),
        dateAdhesion DATE NOT NULL
    );

    CREATE TABLE Bibliothecaire (
        idBibliothecaire SERIAL PRIMARY KEY,
        idUserAccount INTEGER UNIQUE REFERENCES UserAccount(idUserAccount) ON DELETE CASCADE
    );

    CREATE TABLE Abonnement (
        idAbonnement SERIAL PRIMARY KEY,
        idAdherent INTEGER REFERENCES Adherent(idAdherent) ON DELETE CASCADE,
        dateDebut DATE NOT NULL,
        dateFin DATE NOT NULL
    );

    CREATE TABLE HistoriquePaiement (
        idPaiement SERIAL PRIMARY KEY,
        idAdherent INTEGER REFERENCES Adherent(idAdherent) ON DELETE CASCADE,
        datePaiement DATE NOT NULL,
        montantCotisation NUMERIC(10, 2) NOT NULL
    );

    CREATE TABLE Auteur (
        idAuteur SERIAL PRIMARY KEY,
        idPersonne INTEGER REFERENCES Personne(idPersonne) ON DELETE CASCADE
    );

    CREATE TABLE Livre (
        idLivre SERIAL PRIMARY KEY,
        titreLivre VARCHAR(200) NOT NULL,
        idAuteur INTEGER REFERENCES Auteur(idAuteur) ON DELETE RESTRICT
    );

    CREATE TABLE Exemplaire (
        idExemplaire SERIAL PRIMARY KEY,
        idLivre INTEGER REFERENCES Livre(idLivre) ON DELETE CASCADE,
        statutExemplaire VARCHAR(20) NOT NULL CHECK (statutExemplaire IN ('DISPONIBLE', 'EN_PRET', 'RESERVE'))
    );

    CREATE TABLE RestrictionProfilLivre (
        idRestrictionProfilLivre SERIAL PRIMARY KEY,
        idLivre INTEGER REFERENCES Livre(idLivre) ON DELETE CASCADE,
        ageMinRequis INTEGER,
        idProfil INTEGER REFERENCES Profil(idProfil) ON DELETE RESTRICT
    );

    CREATE TABLE Pret (
        idPret SERIAL PRIMARY KEY,
        idAdherent INTEGER REFERENCES Adherent(idAdherent) ON DELETE CASCADE,
        idExemplaire INTEGER REFERENCES Exemplaire(idExemplaire) ON DELETE RESTRICT,
        dateDuPret DATE NOT NULL,
        dateDeRetourPrevue DATE NOT NULL,
        dateDeRetourReelle DATE,
        nombreProlongement INTEGER NOT NULL DEFAULT 0
    );

    CREATE TABLE ListeLecture (
        idListeLecture SERIAL PRIMARY KEY,
        idAdherent INTEGER REFERENCES Adherent(idAdherent) ON DELETE CASCADE,
        idExemplaire INTEGER REFERENCES Exemplaire(idExemplaire) ON DELETE RESTRICT,
        debutLecture TIMESTAMP NOT NULL,
        finLecture TIMESTAMP
    );

    CREATE TABLE Reservation (
        idReservation SERIAL PRIMARY KEY,
        idAdherent INTEGER NOT NULL REFERENCES Adherent(idAdherent),
        idExemplaire INTEGER NOT NULL REFERENCES Exemplaire(idExemplaire),
        dateDeReservation DATE NOT NULL,
        dateDuPretPrevue DATE NOT NULL,
        dateValidation DATE,
        dateLimiteRecuperation DATE,
        statutReservation VARCHAR(50) NOT NULL CHECK (statutReservation IN ('EN_ATTENTE', 'VALIDE', 'REFUSEE', 'EXPIREE', 'COMPLETEE'))
    );

    CREATE TABLE Prolongement (
        idProlongement SERIAL PRIMARY KEY,
        idPret INTEGER NOT NULL REFERENCES Pret(idPret),
        idAdherent INTEGER NOT NULL REFERENCES Adherent(idAdherent),
        dateDemandeProlongement DATE NOT NULL,
        dateRetourPrevueApresProlongement DATE NOT NULL,
        dateValidation DATE,
        statutProlongement VARCHAR(20) NOT NULL CHECK (statutProlongement IN ('EN_ATTENTE', 'VALIDE', 'REFUSE'))
    );

    CREATE TABLE Penalisation (
        idPenalisation SERIAL PRIMARY KEY,
        idAdherent INTEGER REFERENCES Adherent(idAdherent) ON DELETE CASCADE,
        idPret INTEGER REFERENCES Pret(idPret) ON DELETE CASCADE,
        dateDebutPenalisation DATE NOT NULL,
        dateFinPenalisation DATE NOT NULL
    );

    CREATE TABLE HistoriqueEtat (
        idHistoriqueEtat SERIAL PRIMARY KEY,
        entite VARCHAR(50) NOT NULL CHECK (entite IN ('PRET', 'RESERVATION', 'PROLONGEMENT', 'LECTURE_SUR_PLACE')),
        id_entite INTEGER NOT NULL,
        date_changement TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
        etat_avant VARCHAR(100),
        etat_apres VARCHAR(100)
    );

    CREATE TABLE JourNonOuvrable (
        idJourNonOuvrable SERIAL PRIMARY KEY,
        type VARCHAR(20) NOT NULL CHECK (type IN ('FERIE', 'HEBDOMADAIRE', 'EXCEPTIONNEL')),
        jourSemaine INTEGER CHECK (jourSemaine BETWEEN 1 AND 7),
        dateFerie DATE,
        description VARCHAR(150)
    );

    ALTER TABLE JourNonOuvrable
    ADD CONSTRAINT check_jour_non_ouvrable
    CHECK (
        (type = 'HEBDOMADAIRE' AND jourSemaine IS NOT NULL AND dateFerie IS NULL) OR
        (type IN ('FERIE', 'EXCEPTIONNEL') AND dateFerie IS NOT NULL AND jourSemaine IS NULL)
    );

-- Insertion des données
INSERT INTO Personne (nomPersonne, dateDeNaissance, sexe, adresse) VALUES
('Jean Dupont', '1990-05-15', 'M', '123 Rue Principale, Ville'),
('Marie Bibliothecaire', '1985-03-22', 'F', '456 Avenue Centrale, Ville'),
('Victor Hugo', '1802-02-26', 'M', 'Paris, France'),
('Jane Austen', '1775-12-16', 'F', 'Hampshire, Angleterre'),
('Alice Martin', '2010-08-10', 'F', '789 Boulevard, Ville'),
('Bob Durand', '2000-03-15', 'M', '101 Rue Secondaire, Ville'),
('Clara Dupont', '1995-11-22', 'F', '202 Avenue, Ville');

INSERT INTO Profil (profil, montantCotisation, quotaMaxPret, quotaMaxReservation, quotaMaxProlongement, dureePenalite, dureeMaxPret, dureeAbonnement) VALUES
('Etudiant', 10.00, 3, 2, 1, 7, 14, 365),
('Professeur', 20.00, 5, 3, 2, 7, 21, 730),
('Personnel', 15.00, 4, 2, 2, 7, 21, 180);

INSERT INTO UserAccount (idPersonne, login, motDePasse, role) VALUES
(1, 'membre1', 'membre1', 'MEMBRE'),
(2, 'admin', 'admin', 'BIBLIOTHECAIRE'),
(5, 'membre2', 'membre2', 'MEMBRE'),
(6, 'membre3', 'membre3', 'MEMBRE'),
(7, 'membre4', 'membre4', 'MEMBRE');

INSERT INTO Adherent (idUserAccount, idProfil, statutAdherent, dateAdhesion) VALUES
(1, 1, 'ACTIF', '2025-07-01'),
(3, 1, 'ACTIF', '2025-07-01'),
(4, 2, 'ACTIF', '2025-07-01'),
(5, 3, 'ACTIF', '2025-07-01');

INSERT INTO Bibliothecaire (idUserAccount) VALUES
(2);

INSERT INTO Abonnement (idAdherent, dateDebut, dateFin) VALUES
(1, '2025-07-01', '2026-07-01'),
(2, '2025-07-01', '2026-07-01'),
(3, '2025-07-01', '2027-07-01'),
(4, '2025-07-01', '2026-01-01');

INSERT INTO HistoriquePaiement (idAdherent, datePaiement, montantCotisation) VALUES
(1, '2025-07-01', 10.00),
(2, '2025-07-01', 10.00),
(3, '2025-07-01', 20.00),
(4, '2025-07-01', 15.00);

INSERT INTO Auteur (idPersonne) VALUES
(3),
(4);

INSERT INTO Livre (titreLivre, idAuteur) VALUES
('Les Misérables', 1),
('Notre-Dame de Paris', 1),
('Pride and Prejudice', 2);

INSERT INTO Exemplaire (idLivre, statutExemplaire) VALUES
(1, 'DISPONIBLE'),
(1, 'DISPONIBLE'),
(2, 'EN_PRET'),
(3, 'DISPONIBLE'),
(2, 'DISPONIBLE'),
(3, 'DISPONIBLE'),
(3, 'DISPONIBLE');

INSERT INTO JourNonOuvrable (type, jourSemaine, dateFerie, description) VALUES
('HEBDOMADAIRE', 7, NULL, 'Dimanche'),
('FERIE', NULL, '2025-12-25', 'Noël'),
('EXCEPTIONNEL', NULL, '2025-07-20', 'Fermeture exceptionnelle'),
('EXCEPTIONNEL', NULL, '2025-07-27', 'Test jour non ouvrable');

INSERT INTO RestrictionProfilLivre (idLivre, ageMinRequis, idProfil) VALUES
(1, 18, 1),
(1, 16, 2),
(1, 16, 3),
(2, 12, 1),
(2, 12, 2),
(2, 12, 3),
(3, 16, 1),
(3, 14, 2),
(3, 14, 3);

INSERT INTO Pret (idAdherent, idExemplaire, dateDuPret, dateDeRetourPrevue, dateDeRetourReelle, nombreProlongement) VALUES
(1, 3, '2025-07-01', '2025-07-15', NULL, 0);

UPDATE Profil SET dureePenalite = 7 WHERE idProfil = 1;

ALTER TABLE JourNonOuvrable ALTER COLUMN jourSemaine TYPE INTEGER;

-- http://localhost:8080/spring_library_mvc/frontoffice/livres/1/exemplaires`