# Venga Dashboard

Dashboard 100 % hors ligne pour Kia Venga 2010 1.4 CRDi ISG Active 90 ch.
Vue 3D interactive (purement visuelle, aucune connexion CAN/OBD), fiche technique, voyants, suivi d'entretien local.

## Nouveautés
- **Tout est personnalisable** (onglet *Réglages*, boutons ✎ Modifier partout) : thèmes + couleurs, taille du texte, nom du véhicule, couleur de la carrosserie 3D, voyants, boutons d'infos, fiche technique, cartes d'info, contrôles d'entretien, intervalle d'entretien, noms/visibilité des onglets, raccourcis.
- **Voyants** avec symboles de tableau de bord (SVG, colorés rouge / orange / vert / bleu).
- **Raccourci Auto Link** sur l'onglet *Auto* : détecte automatiquement l'appli (« Auto Link », « Android Auto »…) ou se choisit dans la liste des applis installées (Réglages → Gérer les raccourcis). Autant de raccourcis que voulu.
- **Sauvegarde / restauration** des réglages par copier-coller (Réglages → Sauvegarde).
- Mise en page paysage pour écran d'autoradio.

## v4 – nouveautés (100 % hors ligne, sans CAN-bus)
- Nouveau design : barre latérale, cartes, accueil avec révision / échéance / consommation.
- **Trajet** : compteur de vitesse GPS (sans internet), cap, altitude, deux trajets A/B, alerte de vitesse.
- **Carburant** : pleins, consommation L/100, prix moyen, coût aux 100 km, autonomie, graphique.
- **Entretien** : carnet d'entretien avec coûts, échéances (CT, assurance…), bouton « Révision faite ».
- **Infos** : fiche technique, « Mon véhicule » (plaque, VIN, assurance…), guides « Que faire si… ».
- Thème clair le jour / thème choisi la nuit (automatique).
- Le GPS demande l'autorisation de localisation au premier lancement.

## Obtenir l'APK
1. Poussez ce dépôt sur GitHub (branche `main`).
2. Onglet **Actions** → *Build APK* → téléchargez l'artefact `venga-dashboard-apk`.
3. Installez `app-debug.apk` (autoriser les sources inconnues).

## Logo officiel
Le logo Kia est intégré à `index.html` (recoloré selon le thème). La photo de la voiture est `app/src/main/assets/venga.png` : remplaçable par votre propre image (PNG transparent).

## Données
Valeurs indicatives : vérifiez toujours avec le manuel du véhicule.
