package be.eaict.stretchalyzer2.DOM;

import com.google.firebase.auth.FirebaseUser;

/**
 * Created by Kevin-Laptop on 14/04/2018.
 */

public class GlobalData {
    public static FirebaseUser currentUser;
    public static Boolean Sensor = true;


    public static Boolean getSensor() {
        return Sensor;
    }

    public static void setSensor(Boolean sensor) {
        Sensor = sensor;
    }

    public static FirebaseUser getCurrentUser() {
        return currentUser;
    }

    public static void setCurrentUser(FirebaseUser currentUser) {
        GlobalData.currentUser = currentUser;
    }
}
