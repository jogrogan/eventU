package com.eventu;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Collection of static functions to update user database information
 */
class UserDatabaseUpdater {

    /**
     * Update the given user's favorites field
     */
    static void updateFavorites(UserInfo user) {
        DocumentReference doc
                = FirebaseFirestore.getInstance().collection(
                "universities")
                .document(user.getSchoolName()).collection("Users")
                .document(user.getUserID());
        doc.update("favorites", user.getFavorites());
    }
}
