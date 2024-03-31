package com.example.web_app;

import java.util.Date;
import java.sql.Timestamp;
public class Signalement {
    long CIP13; // Changement du type de données de int à long
    String designation;
    Date Date;
    boolean traité;

    // Constructeur par défaut sans arguments
    public Signalement() {
        // Nécessaire pour Firestore
    }

    // Autre constructeur
    public Signalement(long CIP13, String designation, Date Date) {
        this.CIP13 = CIP13;
        this.designation = designation;
        this.Date = Date;
        this.traité = traité;
    }

    // Getters et setters
    public long getCIP13() {
        return CIP13;
    }

    public void setCIP13(long CIP13) {
        this.CIP13 = CIP13;
    }

    public boolean gettraité() {
        return traité;
    }

    public void settraité(boolean traité) {
        this.traité = traité;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public Date getDate() {
        return Date;
    }

    public void setDate(Date Date) {
        this.Date = Date;
    }
}
