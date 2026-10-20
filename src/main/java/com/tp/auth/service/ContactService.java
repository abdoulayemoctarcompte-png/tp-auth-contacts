package com.tp.auth.service;

import java.util.List;
import com.tp.auth.dao.ContactDao;
import com.tp.auth.model.Contact;

public class ContactService {
    private final ContactDao contactDao = new ContactDao();

    public List<Contact> getContactsByUser(int userId) throws Exception {
        return contactDao.findByUser(userId);
    }

    // Valide et enregistre un contact (retourne null si succès, ou un message d'erreur)
    public String saveContact(Contact contact) throws Exception {
        // 1. Nom obligatoire
        if (contact.getNom() == null || contact.getNom().trim().isEmpty()) {
            return "Le nom est obligatoire";
        }
        
        // 2. Prénom obligatoire
        if (contact.getPrenom() == null || contact.getPrenom().trim().isEmpty()) {
            return "Le prénom est obligatoire";
        }

        // 3. Email facultatif mais valide si renseigné
        String email = contact.getEmail();
        if (email != null && !email.trim().isEmpty()) {
            if (!email.contains("@") || !email.contains(".")) {
                return "Email invalide";
            }
        } else {
            contact.setEmail(null);
        }

        // 4. Téléphone facultatif mais valide si renseigné
        String tel = contact.getTelephone();
        if (tel != null && !tel.trim().isEmpty()) {
            if (!tel.matches("^\\+?[0-9\\s]+$")) {
                return "Téléphone invalide";
            }
        } else {
            contact.setTelephone(null);
        }

        // Enregistrement (si id == 0 c'est un ajout, sinon une modification)
        if (contact.getId() == 0) {
            contactDao.insert(contact);
        } else {
            contactDao.update(contact);
        }
        
        return null;
    }

    public void deleteContact(int id, int userId) throws Exception {
        contactDao.delete(id, userId);
    }

    public List<Contact> searchContacts(int userId, String text) throws Exception {
        if (text == null || text.trim().isEmpty()) {
            return contactDao.findByUser(userId);
        }
        return contactDao.search(userId, text.trim());
    }
}