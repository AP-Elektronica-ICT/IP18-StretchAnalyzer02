package be.eaict.stretchalyzer2.DOM;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;
import java.util.List;

/**
 * Created by Kevin-Laptop on 24/03/2018.
 */

public class fxDatapoint {
    private String id;
    private double timestamp;
    private List<Double> angles;
    private String datum;
    String user;



    public fxDatapoint(String id, double timestamp, List<Double> angles, String datum, String user) {
        this.id = id;
        this.timestamp = timestamp;
        this.angles = angles;
        this.datum = datum;
        this.user = user;
    }

    public fxDatapoint() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(double timestamp) {
        this.timestamp = timestamp;
    }

    public List<Double> getAngles() {
        return angles;
    }

    public void setAngles(List<Double> angles) {
        this.angles = angles;
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
