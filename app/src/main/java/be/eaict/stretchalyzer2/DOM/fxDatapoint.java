package be.eaict.stretchalyzer2.DOM;

import java.util.Date;

/**
 * Created by Kevin-Laptop on 24/03/2018.
 */

public class fxDatapoint {
    private int id;
    private int timestamp;
    private int fxPoint;
    private Date datum;


    public fxDatapoint(int id, int timestamp, int fxPoint, Date datum) {
        this.id = id;
        this.timestamp = timestamp;
        this.fxPoint = fxPoint;
        this.datum = datum;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    public Date getDatum() {
        return datum;
    }

    public void setDatum(Date datum) {
        this.datum = datum;
    }
}
