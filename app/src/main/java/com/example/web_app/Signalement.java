package com.example.web_app;

import java.util.Date;

public class Signalement {
    long CIP13;
    String denomination;
    Date Date;
    boolean traité;

    public Signalement() {
        // Nécessaire pour Firestore
    }

    // Autre constructeur
    public Signalement(long CIP13, String denomination, Date Date) {
        this.CIP13 = CIP13;
        this.denomination = denomination;
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

    public String getDenomination() {
        return denomination;
    }

    public void setDenomination(String denomination) {
        this.denomination = denomination;
    }

    public Date getDate() {
        return Date;
    }

    public void setDate(Date Date) {
        this.Date = Date;
    }
}
