package com.example.reminderapp.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.reminderapp.model.Note;

import java.util.List;

@Dao
public interface NoteDao {


    @Insert
    void insertNote(Note note);
    @Delete
    void deletNote(Note note);
    @Update
    void updateNote(Note note);
    @Query("select * from Note")
    LiveData<List<Note>> getAllNotes();


}
