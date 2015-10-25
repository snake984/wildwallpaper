# README #

##Qu�est ce que WildWallpaper ?##
WildWallpaper permet de voyager et de d�couvrir de nouveaux paysages seulement en consultant votre smartphone !
WW vous met � disposition une biblioth�que d�images de nature exclusivement (aucune pr�sence humaine) qui propose de nouveaux contenus chaque jour! Vous serez alors en mesure de choisir le paysage de votre pays favori et de le contempler lorsque vous le souhaitez!

WW vous permet de red�couvrir les paysages de votre r�gion en vous proposant une recherche personnalis�e. Vous pourez alors partager ces photos avec vos amis sur facebook et d�finir vos prochaines vacances !

WW s�appui sur la communaut� � reddit � pour renouveler sa biblioth�que d�images. C�est une communaut� tr�s active.  

##Librairies utilis�es##
RedditLib (https://github.com/achan/android-reddit)
Picasso version 2.3.4
Android Support-v4 version 21.0.0
Play services version 6.1.71
Android Support Appcompat-v7 version 21.0.0

##Difficult�s rencontr�es##
Les principales difficult�s rencontr�es durant l�impl�mentation ont eu pour origine notre manque d�exp�rience en programmation mobile. Nous avons souvent sous estim� le temps de travail pour ajouter de nouvelles fonctionnalit�s et on a du r�duire le scope plusieurs fois. 
A l�origine, notre application devait proposer � l�utilisateur de pouvoir d�finir plusieurs images pour son fond d��cran Android et faire d�filer ses fonds d��crans avec un intervalle de temps qu�il aurait d�fini. Malheureusement c�est par manque d�exp�rience et de temps que cette fonctionnalit� n�a (pas encore) vu le jour. 

Une autre erreur de notre part a �t� de commencer � travailler sur notre application sans avoir d�fini d�architecture technique. Nous avions r�alis� une maquette de l�UI mais nous nous sommes lanc�s dans le code trop rapidement. Cela nous a couter plusieurs heures de travail uniquement pour restructur� l�application�

Enfin, le fait de vouloir utiliser un DrawerLayout pour une meilleure exp�rience utilisateur a compliqu� beaucoup de chose pour nous. 
La premi�re a �t� la complication de la gestion des fragments puisque c��tait nouveau d�utiliser des fragment par l�interm�diaire du DrawerLayout. 
La deuxi�me et la plus importante a �t� de g�rer la compatibilit� avec les tablettes : le drawerLayout n��tant pas activ� en mode tablette, il a fallut changer le layout tout en gardant le container et en changeant le fragment. On perd alors les int�ractions avec la barre d�action puisqu�elle �tait g�r� de pair avec le drawerLayout.?