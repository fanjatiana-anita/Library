DROP DATABASE IF EXISTS bibliotheque_mvc;

CREATE DATABASE bibliotheque_mvc;
\connect bibliotheque_mvc;

CREATE TYPE statut_exemplaire_enum AS ENUM ('DISPONIBLE', 'RESERVE', 'EN_PRET');
CREATE TYPE statut_reservation_enum AS ENUM ('EN_ATTENTE', 'VALIDE', 'REFUSE');
CREATE TYPE statut_prolongement_enum AS ENUM ('EN_ATTENTE', 'VALIDE', 'REFUSE');
CREATE TYPE type_jour_enum AS ENUM ('FERIE', 'HEBDOMADAIRE', 'EXCEPTIONNEL');
CREATE TYPE entite_enum AS ENUM ('PRET', 'RESERVATION', 'PROLONGEMENT', 'LECTURE_SUR_PLACE');

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
    profil VARCHAR(50) NOT NULL,
    montantCotisation NUMERIC(10, 2),
    quotaMaxPret INTEGER NOT NULL, 
    quotaMaxReservation INTEGER NOT NULL, 
    quotaMaxProlongement INTEGER NOT NULL, 
    dureePenalite INTEGER NOT NULL, 
    dureeMaxPret INTEGER NOT NULL 
);

CREATE TABLE Adherent (
    idAdherent SERIAL PRIMARY KEY,
    idUserAccount INTEGER UNIQUE REFERENCES UserAccount(idUserAccount) ON DELETE CASCADE,
    idProfil INTEGER REFERENCES Profil(idProfil) ON DELETE RESTRICT
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
    statutExemplaire statut_exemplaire_enum NOT NULL
);

CREATE TABLE RestrictionProfilLivre (
    idRestrictionProfilLivre SERIAL PRIMARY KEY,
    idLivre INTEGER REFERENCES Livre(idLivre) ON DELETE CASCADE,
    ageMinRequis INTEGER, -- Âge minimum requis pour accéder au livre
    idProfil INTEGER REFERENCES Profil(idProfil) ON DELETE RESTRICT
);

CREATE TABLE Pret (
    idPret SERIAL PRIMARY KEY,
    idAdherent INTEGER REFERENCES Adherent(idAdherent) ON DELETE CASCADE,
    idExemplaire INTEGER REFERENCES Exemplaire(idExemplaire) ON DELETE RESTRICT,
    dateDuPret DATE NOT NULL,
    dateDeRetourPrevue DATE NOT NULL,
    dateDeRetourReelle DATE
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
    idAdherent INTEGER REFERENCES Adherent(idAdherent) ON DELETE CASCADE,
    idExemplaire INTEGER REFERENCES Exemplaire(idExemplaire) ON DELETE RESTRICT,
    dateDeReservation DATE NOT NULL,
    dateDuPretPrevue DATE,
    statutReservation statut_reservation_enum NOT NULL
);

CREATE TABLE Prolongement (
    idProlongement SERIAL PRIMARY KEY,
    idPret INTEGER REFERENCES Pret(idPret) ON DELETE CASCADE,
    dateDeDemande DATE NOT NULL,
    statutProlongement statut_prolongement_enum NOT NULL
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
    entite entite_enum NOT NULL,
    id_entite INTEGER NOT NULL, 
    date_changement TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    etat_avant VARCHAR(100), 
    etat_apres VARCHAR(100) 
);

CREATE TABLE JourNonOuvrable (
    idJourNonOuvrable SERIAL PRIMARY KEY,
    type type_jour_enum NOT NULL,
    jourSemaine SMALLINT CHECK (jourSemaine BETWEEN 1 AND 7),
    dateFerie DATE,
    description VARCHAR(150)
);

ALTER TABLE JourNonOuvrable
ADD CONSTRAINT check_jour_non_ouvrable
CHECK (
    (type = 'HEBDOMADAIRE' AND jourSemaine IS NOT NULL AND dateFerie IS NULL) OR
    (type IN ('FERIE', 'EXCEPTIONNEL') AND dateFerie IS NOT NULL AND jourSemaine IS NULL)
);