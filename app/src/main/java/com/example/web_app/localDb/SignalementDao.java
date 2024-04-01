package com.example.web_app.localDb;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.web_app.localDb.SignalementEntity;

import java.util.List;

@Dao

public interface SignalementDao {
    @Insert
    void insert(SignalementEntity signalement);

    @Query("SELECT * FROM signalements")
    List<SignalementEntity> getAll();

    @Delete
    void delete(SignalementEntity signalement);

}

