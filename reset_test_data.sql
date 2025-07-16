SET session_replication_role = 'replica';
TRUNCATE TABLE HistoriqueEtat RESTART IDENTITY CASCADE;
TRUNCATE TABLE Penalisation RESTART IDENTITY CASCADE;
TRUNCATE TABLE Prolongement RESTART IDENTITY CASCADE;
TRUNCATE TABLE Reservation RESTART IDENTITY CASCADE;
TRUNCATE TABLE ListeLecture RESTART IDENTITY CASCADE;
TRUNCATE TABLE Pret RESTART IDENTITY CASCADE;
TRUNCATE TABLE RestrictionProfilLivre RESTART IDENTITY CASCADE;
TRUNCATE TABLE Exemplaire RESTART IDENTITY CASCADE;
TRUNCATE TABLE Livre RESTART IDENTITY CASCADE;
TRUNCATE TABLE Auteur RESTART IDENTITY CASCADE;
TRUNCATE TABLE HistoriquePaiement RESTART IDENTITY CASCADE;
TRUNCATE TABLE Abonnement RESTART IDENTITY CASCADE;
TRUNCATE TABLE Bibliothecaire RESTART IDENTITY CASCADE;
TRUNCATE TABLE Adherent RESTART IDENTITY CASCADE;
TRUNCATE TABLE UserAccount RESTART IDENTITY CASCADE;
TRUNCATE TABLE Personne RESTART IDENTITY CASCADE;
TRUNCATE TABLE Profil RESTART IDENTITY CASCADE;
TRUNCATE TABLE JourNonOuvrable RESTART IDENTITY CASCADE;
SET session_replication_role = 'origin';

INSERT INTO Profil (profil, montantCotisation, quotaMaxPret, quotaMaxReservation, quotaMaxProlongement, dureePenalite, dureeMaxPret, delaiSupplementaireReservation, dureeAbonnement) VALUES
('Etudiant', 10.00, 3, 2, 1, 7, 14, 2, 365),
('Professeur', 20.00, 5, 3, 2, 7, 21, 2, 730),
('Personnel', 15.00, 4, 2, 2, 7, 21, 2, 180),
('Jeune', 5.00, 2, 1, 1, 5, 10, 2, 180);

INSERT INTO Personne (nomPersonne, dateDeNaissance, sexe, adresse) VALUES
('Jean Dupont', '1990-05-15', 'M', '123 Rue Principale'),
('Alice Martin', '2010-08-10', 'F', '789 Boulevard'),
('Paul Laurent', '1980-01-01', 'M', 'Rue du Test'),
('Sophie Bernard', '2005-07-01', 'F', 'Rue Jeune'),
('Marie Bibliothecaire', '1985-03-22', 'F', '456 Avenue'),
('Victor Hugo', '1802-02-26', 'M', 'Paris'),
('Jane Austen', '1775-12-16', 'F', 'Hampshire');

INSERT INTO UserAccount (idPersonne, login, motDePasse, role) VALUES
(1, 'membre1', 'membre1', 'MEMBRE'),
(2, 'membre2', 'membre2', 'MEMBRE'),
(3, 'membre3', 'membre3', 'MEMBRE'),
(4, 'membre4', 'membre4', 'MEMBRE'),
(5, 'admin', 'admin', 'BIBLIOTHECAIRE');

-- Insertion d'adhérents
INSERT INTO Adherent (idUserAccount, idProfil, statutAdherent, dateAdhesion) VALUES
(1, 1, 'ACTIF', '2025-07-01'), -- Jean Dupont, Etudiant
(2, 2, 'ACTIF', '2025-07-01'), -- Alice Martin, Professeur
(3, 3, 'INACTIF', '2024-01-01'), -- Paul Laurent, Personnel (abonnement expiré)
(4, 4, 'ACTIF', '2025-07-01'); -- Sophie Bernard, Jeune

-- Insertion de bibliothécaire
INSERT INTO Bibliothecaire (idUserAccount) VALUES (5);

-- Insertion d'abonnements (cas actif, expiré, renouvelé)
INSERT INTO Abonnement (idAdherent, dateDebut, dateFin) VALUES
(1, '2025-07-01', '2026-07-01'), -- Actif
(2, '2025-07-01', '2027-07-01'), -- Actif
(3, '2024-01-01', '2025-01-01'), -- Expiré
(4, '2025-07-01', '2026-01-01'); -- Actif

-- Insertion d'auteurs et livres
INSERT INTO Auteur (idPersonne) VALUES (6), (7);
INSERT INTO Livre (titreLivre, idAuteur) VALUES
('Les Misérables', 1),
('Notre-Dame de Paris', 1),
('Pride and Prejudice', 2),
('Germinal', 1),
('Sense and Sensibility', 2);

-- Insertion d'exemplaires (disponible, en prêt, réservé)
INSERT INTO Exemplaire (idLivre, statutExemplaire) VALUES
(1, 'DISPONIBLE'), (1, 'DISPONIBLE'), (2, 'EN_PRET'), (3, 'DISPONIBLE'), (2, 'DISPONIBLE'), (3, 'DISPONIBLE'), (4, 'DISPONIBLE'), (4, 'EN_PRET'), (5, 'DISPONIBLE'), (5, 'RESERVE');

-- Insertion de restrictions d'âge
INSERT INTO RestrictionProfilLivre (idLivre, ageMinRequis, idProfil) VALUES
(1, 18, 1), (1, 16, 2), (1, 16, 3), (2, 12, 1), (2, 12, 2), (2, 12, 3), (3, 16, 1), (3, 14, 2), (3, 14, 3), (4, 12, 4), (5, 14, 1), (5, 12, 2), (5, 12, 3), (5, 10, 4);

-- Insertion de jours non ouvrables (samedi, dimanche, férié, exceptionnel)
INSERT INTO JourNonOuvrable (type, jourSemaine, dateFerie, description) VALUES
('HEBDOMADAIRE', 6, NULL, 'Samedi'),
('HEBDOMADAIRE', 7, NULL, 'Dimanche'),
('FERIE', NULL, '2025-07-14', 'Fête nationale'),
('FERIE', NULL, '2025-08-15', 'Assomption'),
('EXCEPTIONNEL', NULL, '2025-07-29', 'Fermeture exceptionnelle');

-- Insertion de prêts (cas normal, en retard, prolongé, issu de réservation, etc.)
INSERT INTO Pret (idAdherent, idExemplaire, dateDuPret, dateDeRetourPrevue, dateDeRetourReelle, nombreProlongement) VALUES
(1, 3, '2025-07-01', '2025-07-19', NULL, 0), -- Retour prévu un samedi (ajusté à 21/07/2025)
(4, 8, '2025-07-01', '2025-07-14', NULL, 0), -- Retour prévu un jour férié (ajusté à 15/07/2025)
(2, 6, '2025-07-10', '2025-07-20', NULL, 0), -- Retour prévu un dimanche (ajusté à 21/07/2025)
(1, 2, '2025-07-01', '2025-07-15', NULL, 1), -- Prêt avec un prolongement
(2, 4, '2025-07-01', '2025-07-15', '2025-07-20', 0); -- Prêt retourné en retard (pénalisation)

-- Insertion de réservations (en attente, validée, refusée, avec conflit de dates)
INSERT INTO Reservation (idAdherent, idExemplaire, dateDeReservation, dateDuPretPrevue, statutReservation) VALUES
(1, 3, '2025-07-10', '2025-07-16', 'EN_ATTENTE'), -- Réservation conflictuelle
(2, 6, '2025-07-10', '2025-07-21', 'EN_ATTENTE'), -- Réservation pour conflit prolongement
(1, 2, '2025-07-01', '2025-07-15', 'VALIDE'), -- Réservation validée
(3, 5, '2025-07-01', '2025-07-10', 'REFUSEE');

-- Insertion de prolongements (en attente, validé, refusé)
INSERT INTO Prolongement (idPret, idAdherent, dateDemandeProlongement, dateRetourPrevueApresProlongement, statutProlongement) VALUES
(5, 1, '2025-07-10', '2025-07-29', 'EN_ATTENTE'), -- Demande en attente
(5, 1, '2025-07-05', '2025-07-22', 'VALIDE'), -- Demande validée
(1, 1, '2025-07-10', '2025-08-02', 'REFUSE'); -- Demande refusée

-- Insertion de pénalisations (cas adhérent déjà pénalisé, nouveau pénalisé)
INSERT INTO Penalisation (idAdherent, idPret, dateDebutPenalisation, dateFinPenalisation) VALUES
(4, 4, '2025-07-20', '2025-07-27'); -- Sophie Bernard pénalisée

-- Insertion d'historique d'états (pour suivi)
INSERT INTO HistoriqueEtat (entite, id_entite, date_changement, etat_avant, etat_apres) VALUES
('PRET', 1, '2025-07-01 10:00:00', 'DISPONIBLE', 'EN_PRET'),
('PRET', 2, '2025-07-01 10:00:00', 'DISPONIBLE', 'EN_PRET'),
('PRET', 3, '2025-07-10 10:00:00', 'DISPONIBLE', 'EN_PRET'),
('PRET', 4, '2025-07-20 10:00:00', 'EN_PRET', 'DISPONIBLE'),
('PROLONGEMENT', 1, '2025-07-10 10:00:00', 'AUCUN', 'EN_ATTENTE'),
('PROLONGEMENT', 2, '2025-07-05 10:00:00', 'AUCUN', 'VALIDE'),
('PROLONGEMENT', 3, '2025-07-10 10:00:00', 'AUCUN', 'REFUSE');

-- Insertion de paiements
INSERT INTO HistoriquePaiement (idAdherent, datePaiement, montantCotisation) VALUES
(1, '2025-07-01', 10.00),
(2, '2025-07-01', 20.00),
(3, '2025-07-01', 15.00),
(4, '2025-07-01', 5.00);
