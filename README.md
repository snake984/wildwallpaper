# README #

##Qu’est ce que WildWallpaper ?##
WildWallpaper permet de voyager et de découvrir de nouveaux paysages seulement en consultant votre smartphone !
WW vous met à disposition une bibliothèque d’images de nature exclusivement (aucune présence humaine) qui propose de nouveaux contenus chaque jour! Vous serez alors en mesure de choisir le paysage de votre pays favori et de le contempler lorsque vous le souhaitez!

WW vous permet de redécouvrir les paysages de votre région en vous proposant une recherche personnalisée. Vous pourez alors partager ces photos avec vos amis sur facebook et définir vos prochaines vacances !

WW s’appui sur la communauté « reddit » pour renouveler sa bibliothèque d’images. C’est une communauté très active.  

##Librairies utilisées##
RedditLib (https://github.com/achan/android-reddit)
Picasso version 2.3.4
Android Support-v4 version 21.0.0
Play services version 6.1.71
Android Support Appcompat-v7 version 21.0.0

##Difficultés rencontrées##
Les principales difficultés rencontrées durant l’implémentation ont eu pour origine notre manque d’expérience en programmation mobile. Nous avons souvent sous estimé le temps de travail pour ajouter de nouvelles fonctionnalités et on a du réduire le scope plusieurs fois. 
A l’origine, notre application devait proposer à l’utilisateur de pouvoir définir plusieurs images pour son fond d’écran Android et faire défiler ses fonds d’écrans avec un intervalle de temps qu’il aurait défini. Malheureusement c’est par manque d’expérience et de temps que cette fonctionnalité n’a (pas encore) vu le jour. 

Une autre erreur de notre part a été de commencer à travailler sur notre application sans avoir défini d’architecture technique. Nous avions réalisé une maquette de l’UI mais nous nous sommes lancés dans le code trop rapidement. Cela nous a couter plusieurs heures de travail uniquement pour restructuré l’application…

Enfin, le fait de vouloir utiliser un DrawerLayout pour une meilleure expérience utilisateur a compliqué beaucoup de chose pour nous. 
La première a été la complication de la gestion des fragments puisque c’était nouveau d’utiliser des fragment par l’intermédiaire du DrawerLayout. 
La deuxième et la plus importante a été de gérer la compatibilité avec les tablettes : le drawerLayout n’étant pas activé en mode tablette, il a fallut changer le layout tout en gardant le container et en changeant le fragment. On perd alors les intéractions avec la barre d’action puisqu’elle était géré de pair avec le drawerLayout.