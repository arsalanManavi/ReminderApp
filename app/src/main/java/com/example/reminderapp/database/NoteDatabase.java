package com.example.reminderapp.database;


import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.reminderapp.dao.NoteDao;
import com.example.reminderapp.model.Note;

import org.jetbrains.annotations.NotNull;

@Database(entities = Note.class,version = 2,exportSchema = false)
public abstract class NoteDatabase extends RoomDatabase {
    public static final String noteDatabaseName = "noteDatabase";
    private static NoteDatabase instance;
    public static synchronized NoteDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context, NoteDatabase.class, noteDatabaseName)
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }




    public abstract NoteDao noteDao();

}
