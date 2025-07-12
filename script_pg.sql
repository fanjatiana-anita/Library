-- Supprimer la base de données si elle existe
DROP DATABASE IF EXISTS library_mvc;

-- Créer la base de données
CREATE DATABASE library_mvc;
\connect library_mvc;

-- ENUMS nécessaires
CREATE TYPE statut_exemplaire_enum AS ENUM ('DISPONIBLE', 'RESERVE', 'EN_PRET');
CREATE TYPE statut_reservation_enum AS ENUM ('EN_ATTENTE', 'VALIDE', 'REFUSE');
CREATE TYPE statut_prolongement_enum AS ENUM ('EN_ATTENTE', 'VALIDE', 'REFUSE');
CREATE TYPE type_jour_enum AS ENUM ('FERIE', 'HEBDOMADAIRE', 'EXCEPTIONNEL');
CREATE TYPE entite_enum AS ENUM ('PRET', 'RESERVATION', 'PROLONGEMENT', 'LECTURE_SUR_PLACE');
CREATE TYPE statut_adherent_enum AS ENUM ('ACTIF', 'SUSPENDU', 'INACTIF');

-- Table Personne : Informations personnelles des membres et bibliothécaires
CREATE TABLE Personne (
    idPersonne SERIAL PRIMARY KEY,
    nomPersonne VARCHAR(100) NOT NULL,
    dateDeNaissance DATE NOT NULL,
    sexe VARCHAR(10),
    adresse TEXT
);

-- Table UserAccount : Gestion des comptes pour l'authentification
CREATE TABLE UserAccount (
    idUserAccount SERIAL PRIMARY KEY,
    idPersonne INTEGER REFERENCES Personne(idPersonne) ON DELETE CASCADE,
    login VARCHAR(20) UNIQUE NOT NULL, -- Numéro de membre (ex: MBR-001)
    motDePasse VARCHAR(100) NOT NULL, -- Mot de passe en clair
    role VARCHAR(20) NOT NULL CHECK (role IN ('MEMBRE', 'BIBLIOTHECAIRE'))
);

-- Table Profil : Paramètres des types de membres
CREATE TABLE Profil (
    idProfil SERIAL PRIMARY KEY,
    profil VARCHAR(50) NOT NULL,
    montantCotisation NUMERIC(10, 2),
    quotaMaxPret INTEGER NOT NULL,
    quotaMaxReservation INTEGER NOT NULL,
    quotaMaxProlongement INTEGER NOT NULL,
    dureePenalite INTEGER NOT NULL,
    dureeMaxPret INTEGER NOT NULL
);

-- Table Adherent : Membres de la bibliothèque
CREATE TABLE Adherent (
    idAdherent SERIAL PRIMARY KEY,
    idUserAccount INTEGER UNIQUE REFERENCES UserAccount(idUserAccount) ON DELETE CASCADE,
    idProfil INTEGER REFERENCES Profil(idProfil) ON DELETE RESTRICT,
    statutAdherent statut_adherent_enum NOT NULL,
    dateAdhesion DATE NOT NULL
);

-- Table Bibliothecaire : Bibliothécaires ayant accès au backoffice
CREATE TABLE Bibliothecaire (
    idBibliothecaire SERIAL PRIMARY KEY,
    idUserAccount INTEGER UNIQUE REFERENCES UserAccount(idUserAccount) ON DELETE CASCADE
);

-- Table Abonnement : Gestion des abonnements des membres
CREATE TABLE Abonnement (
    idAbonnement SERIAL PRIMARY KEY,
    idAdherent INTEGER REFERENCES Adherent(idAdherent) ON DELETE CASCADE,
    dateDebut DATE NOT NULL,
    dateFin DATE NOT NULL
);

-- Table Auteur : Auteurs des livres
CREATE TABLE Auteur (
    idAuteur SERIAL PRIMARY KEY,
    idPersonne INTEGER REFERENCES Personne(idPersonne) ON DELETE CASCADE
);

-- Table Livre : Livres de la bibliothèque
CREATE TABLE Livre (
    idLivre SERIAL PRIMARY KEY,
    titreLivre VARCHAR(200) NOT NULL,
    idAuteur INTEGER REFERENCES Auteur(idAuteur) ON DELETE RESTRICT
);

-- Table Exemplaire : Exemplaires physiques des livres
CREATE TABLE Exemplaire (
    idExemplaire SERIAL PRIMARY KEY,
    idLivre INTEGER REFERENCES Livre(idLivre) ON DELETE CASCADE,
    statutExemplaire statut_exemplaire_enum NOT NULL
);

-- Table RestrictionProfilLivre : Restrictions d'âge ou de profil pour les livres
CREATE TABLE RestrictionProfilLivre (
    idRestrictionProfilLivre SERIAL PRIMARY KEY,
    idLivre INTEGER REFERENCES Livre(idLivre) ON DELETE CASCADE,
    ageMinRequis INTEGER,
    idProfil INTEGER REFERENCES Profil(idProfil) ON DELETE RESTRICT
);

-- Table Pret : Gestion des prêts
CREATE TABLE Pret (
    idPret SERIAL PRIMARY KEY,
    idAdherent INTEGER REFERENCES Adherent(idAdherent) ON DELETE CASCADE,
    idExemplaire INTEGER REFERENCES Exemplaire(idExemplaire) ON DELETE RESTRICT,
    dateDuPret DATE NOT NULL,
    dateDeRetourPrevue DATE NOT NULL,
    dateDeRetourReelle DATE
);

-- Table ListeLecture : Gestion des lectures sur place
CREATE TABLE ListeLecture (
    idListeLecture SERIAL PRIMARY KEY,
    idAdherent INTEGER REFERENCES Adherent(idAdherent) ON DELETE CASCADE,
    idExemplaire INTEGER REFERENCES Exemplaire(idExemplaire) ON DELETE RESTRICT,
    debutLecture TIMESTAMP NOT NULL,
    finLecture TIMESTAMP
);

-- Table Reservation : Gestion des réservations
CREATE TABLE Reservation (
    idReservation SERIAL PRIMARY KEY,
    idAdherent INTEGER REFERENCES Adherent(idAdherent) ON DELETE CASCADE,
    idExemplaire INTEGER REFERENCES Exemplaire(idExemplaire) ON DELETE RESTRICT,
    dateDeReservation DATE NOT NULL,
    dateDuPretPrevue DATE,
    statutReservation statut_reservation_enum NOT NULL
);

-- Table Prolongement : Gestion des demandes de prolongation
CREATE TABLE Prolongement (
    idProlongement SERIAL PRIMARY KEY,
    idPret INTEGER REFERENCES Pret(idPret) ON DELETE CASCADE,
    dateDeDemande DATE NOT NULL,
    statutProlongement statut_prolongement_enum NOT NULL
);

-- Table Penalisation : Gestion des pénalités
CREATE TABLE Penalisation (
    idPenalisation SERIAL PRIMARY KEY,
    idAdherent INTEGER REFERENCES Adherent(idAdherent) ON DELETE CASCADE,
    idPret INTEGER REFERENCES Pret(idPret) ON DELETE CASCADE,
    dateDebutPenalisation DATE NOT NULL,
    dateFinPenalisation DATE NOT NULL
);

-- Table HistoriqueEtat : Suivi des changements d'état
CREATE TABLE HistoriqueEtat (
    idHistoriqueEtat SERIAL PRIMARY KEY,
    entite entite_enum NOT NULL,
    id_entite INTEGER NOT NULL,
    date_changement TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    etat_avant VARCHAR(100),
    etat_apres VARCHAR(100)
);


-- Table JourNonOuvrable : Gestion des jours fériés ou non ouvrables
CREATE TABLE JourNonOuvrable (
    idJourNonOuvrable SERIAL PRIMARY KEY,
    type type_jour_enum NOT NULL,
    jourSemaine SMALLINT CHECK (jourSemaine BETWEEN 1 AND 7),
    dateFerie DATE,
    description VARCHAR(150)
);

-- Contrainte pour JourNonOuvrable
ALTER TABLE JourNonOuvrable
ADD CONSTRAINT check_jour_non_ouvrable
CHECK (
    (type = 'HEBDOMADAIRE' AND jourSemaine IS NOT NULL AND dateFerie IS NULL) OR
    (type IN ('FERIE', 'EXCEPTIONNEL') AND dateFerie IS NOT NULL AND jourSemaine IS NULL)
);

-- Insérer des données dans Personne
INSERT INTO Personne (nomPersonne, dateDeNaissance, sexe, adresse) VALUES
('Jean Dupont', '1990-05-15', 'M', '123 Rue Principale, Ville'),
('Marie Bibliothecaire', '1985-03-22', 'F', '456 Avenue Centrale, Ville');

-- Insérer des profils
INSERT INTO Profil (profil, montantCotisation, quotaMaxPret, quotaMaxReservation, quotaMaxProlongement, dureePenalite, dureeMaxPret) VALUES
('Etudiant', 10.00, 3, 2, 1, 7, 14),
('Bibliothecaire', 0.00, 5, 3, 2, 0, 30);

-- Insérer des comptes utilisateur (mots de passe en clair)
INSERT INTO UserAccount (idPersonne, login, motDePasse, role) VALUES
(1, 'MBR-001', 'password123', 'MEMBRE'),
(2, 'BIB-001', 'admin456', 'BIBLIOTHECAIRE');

-- Insérer un adhérent
INSERT INTO Adherent (idUserAccount, idProfil, statutAdherent, dateAdhesion) VALUES
(1, 1, 'ACTIF', '2025-07-01');

-- Insérer un bibliothécaire
INSERT INTO Bibliothecaire (idUserAccount) VALUES
(2);