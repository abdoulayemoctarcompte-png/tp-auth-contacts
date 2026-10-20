# Réponses aux questions de réflexion - TP2 Carnet de contacts

## Question 1 (Partie 01)
**Pourquoi la colonne `user_id` est-elle indispensable pour respecter la règle « un utilisateur ne voit que ses contacts » ?**
La colonne `user_id` joue le rôle de clé étrangère (FK). Elle associe chaque contact de manière univoque à l'identifiant unique (PK) de l'utilisateur qui l'a créé. Sans cette colonne, la base de données ne conserverait aucune information sur le propriétaire du contact, rendant impossible le filtrage des requêtes SQL (`WHERE user_id = ?`) selon l'utilisateur connecté.

## Question 2 (Partie 01)
**Que se passerait-il, sans `ON DELETE CASCADE`, si on supprimait un utilisateur qui a des contacts ?**
Sans la clause `ON DELETE CASCADE`, le moteur InnoDB de MySQL bloquerait la suppression de l'utilisateur en renvoyant une erreur de contrainte de clé étrangère (`Foreign Key Constraint Violation`), car des lignes "orphelines" dans la table `contacts` pointeraient vers un `user_id` inexistant. Avec `ON DELETE CASCADE`, MySQL supprime automatiquement tous les contacts associés dès que leur propriétaire est supprimé.

## Question 3 (Partie 02)
**Pourquoi `update` et `delete` doivent-ils aussi vérifier `user_id` dans leur WHERE, alors que l'id du contact suffit à l'identifier ?**
Vérifier simultanément l'id du contact et le `user_id` de l'utilisateur connecté protège l'application contre les attaques de type IDOR (Insecure Direct Object References / Contrôle d'accès défaillant)[cite: 5]. Si on se contentait de `WHERE id = ?`, un utilisateur malveillant pourrait modifier ou supprimer les contacts d'un autre utilisateur simplement en devinant ou en incrémentant l'ID numérique dans la requête[cite: 9].