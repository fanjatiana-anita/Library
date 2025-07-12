Personne
idPersonne
nomPersonne
dateDeNaissance
sexe
adresse

UserAccount
idUserAccount
idPersonne
login
motDePasse role ENUM('BIBLIOTHECAIRE','ADHERENT')
estActif

Adherent
idAdherent
idUserAccount
idProfile

Bibliothecaire
idBibliothecaire
idUserAccount

Profil
idProfil
profil
montantCotisation
quotaMax
quotaProlongement
dureeMaxProlongement
dureePenalite

Abonnement
idAbonnement
idAdherent
dateDebut
dateFin

Auteur
idAuteur
idPersonne

Theme
idTheme
theme 

Theme_Livre 
idTheme
idLivre

Livre
idLivre
titreLivre
idAuteur

Exemplaire
idExemplaire
idLivre
statutExemplaire ------ disponible / reserve / en pret

RestrictionProfilLivre
idRestrictionProfilLivre
idLivre
ageMinRequis
idProfil -- nullable

Pret
idPret
idAdherent
idExemplaire
dateDuPret
dateDeRetourPrevue
dateDeRetourReelle


Reservation
idReservation 
idAdherent
idExemplaire
dateDeReservation
dateDuPretPrevue
statutReservation ---- valide / en attente de validation

HistoriqueEtat
idHistoriqueEtat
entite ENUM('PRET','RESERVATION','PROLONGEMENT')
id_entite
date_changement
etat_avant
etat_apres


Prolongement
idProlongement
idPret
dateDeDemande
dureeJourProlongement --nullable
statutProlongement ----- valide / en attente de validation


Penalisation
idPenalisation
idAdherent
idPret
dateDebutPenalisation
dateFinPenalisation


JourNonOuvrable
idJourNonOuvrable
type ----------- ferie / hebdomadaire /exceptionnel not null
jourSemaine ------- nullable
dateFerie ------------- nullable
description










