# PROJET DU JEU DU UNO 

# 🎴 Jeu UNO Réseau en Java

Un projet complet de **jeu UNO multijoueur** développé en **Java**, avec une architecture **client-serveur** et une gestion des utilisateurs via **base de données**.  
Ce projet a été conçu dans un cadre académique pour mettre en œuvre la **programmation orientée objet**, la **communication réseau**, la **persistence des données**, et les **tests unitaires**.

---

## 🧩 Description du projet

Le jeu UNO permet à plusieurs joueurs connectés au même serveur de participer à une partie en réseau.  
Chaque joueur peut se connecter, jouer ses cartes et interagir avec les autres en temps réel via une interface console.

Le projet se divise en plusieurs couches logiques :
- **Serveur :** gère les connexions, les échanges entre clients et la logique globale des parties.
- **Client :** interface console permettant de jouer et d’échanger des messages.
- **Métier (Logique de jeu) :** définit le fonctionnement des cartes, joueurs, pioches et effets spéciaux.
- **Base de données :** enregistre les utilisateurs et gère la connexion à la BD.

---

## 🧱 Architecture du projet
src/
├── BaseDeDonnees/
│ ├── ConnexionBD.java
│ ├── UtilisateurDAO.java
│ └── TestConnexion.java
│
├── Metier/
│ ├── LogiqueDeJeu/
│ │ ├── Carte.java
│ │ ├── Joueur.java
│ │ ├── Pioche.java
│ │ ├── Defausse.java
│ │ └── Partiedejeu.java
│ ├── Exceptions/
│ │ ├── PartieException.java
│ │ ├── PiocheException.java
│ │ └── UnoException.java
│ └── Tests/
│ ├── PartiedejeuTest.java
│ ├── PartiedejeuTestCartePlusDeux.java
│ ├── PartiedejeuTestCartePlusQuatre.java
│ ├── PartiedejeuTestUno.java
│
├── Client/
│ └── src/main/java/tp/client/
│ ├── AppClient.java
│ ├── ClientChat.java
│ └── ThreadConsole.java
│
├── serveur/
│ ├── app/appServeur.java
│ ├── reseau/
│ │ ├── ThreadAcceptConnexion.java
│ │ ├── ThreadConnexion.java
│ │ └── Utilisateur.java
│ └── serveurMetier/
│ ├── ServeurUno.java
│ └── ServeurExceptions.java
│
└── Main.java

## ⚙️ Technologies utilisées

- **Langage :** Java 17  
- **Architecture :** Client / Serveur  
- **Communication :** Jeu UNO développé en Java, entièrement jouable en local, sans réseau (telnet pour les connexions au localHost).
- **Base de données :** JDBC  
- **Tests unitaires :** JUnit 5  
- **IDE recommandé :** IntelliJ IDEA ou Eclipse  


Auteur
-------------------------------
Youva Kaced
Étudiant en informatique — passionné par la programmation l'administration réseau et sécurté des systéme informatique.

📄 Licence

 libre à l’étude, à l’utilisation et à la modification à des fins éducatives ou personnelles.

- J'ai ajouté un README complet et modulaire à la racine du dépôt.
- Prochaine étape recommandée : compléter les sections TODO (description du projet, fonctionnalités exactes, commandes de build exactes et licence souhaitée).
