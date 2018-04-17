package be.eaict.stretchalyzer2.DOM;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;

/**
 * Created by Kevin-Laptop on 24/03/2018.
 */

public class fxDatapoint {
    private String id;
    private int timestamp;
    private int fxPoint;
    private String datum;
    String user;


    public fxDatapoint(String id, int timestamp, int fxPoint, String datum, String user) {
        this.id = id;
        this.timestamp = timestamp;
        this.fxPoint = fxPoint;
        this.datum = datum;
        this.user = user;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }

    public int getFxPoint() {
        return fxPoint;
    }

    public void setFxPoint(int fxPoint) {
        this.fxPoint = fxPoint;
    }

    public String getDatum() {
        return datum;
    }

    public void setDatum(String datum) {
        this.datum = datum;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

}
