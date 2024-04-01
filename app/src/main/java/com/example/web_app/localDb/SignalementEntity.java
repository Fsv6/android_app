package com.example.web_app.localDb;


import androidx.room.Entity;
import androidx.room.PrimaryKey;
import java.util.Date;

@Entity(tableName = "signalements")
public class SignalementEntity {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private long CIP;
    private String description;
    private boolean traite;

    private Date date;

    private String denomination;

    private String UserId;


    public SignalementEntity() {

    }

    // Getters et setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getCIP() {
        return CIP;
    }

    public void setCIP(long CIP) {
        this.CIP = CIP;
    }

    public boolean isTraite() {
        return traite;
    }

    public void setTraite(boolean traite) {
        this.traite = traite;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String designation) {
        this.description = designation;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getDenomination() {
        return denomination;
    }

    public void setDenomination(String denomination) {
        this.denomination = denomination;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String user_id) {
        this.UserId = user_id;
    }



}
