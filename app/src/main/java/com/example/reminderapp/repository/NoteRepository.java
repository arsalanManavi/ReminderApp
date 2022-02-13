package com.example.reminderapp.repository;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.example.reminderapp.dao.NoteDao;
import com.example.reminderapp.database.NoteDatabase;
import com.example.reminderapp.model.Note;

import java.util.List;

public class NoteRepository {

    NoteDao noteDao;
    LiveData<List<Note>> allnotes;

    public NoteRepository (Application application){
        NoteDatabase noteDatabase = NoteDatabase.getInstance(application);
        noteDao = noteDatabase.noteDao();
        allnotes = noteDao.getAllNotes();
    }

    public void insert(Note note){
        new InsertOperation(noteDao).execute(note);
    }

    public void delete(Note note){
        new DeleteOperation(noteDao).execute(note);
    }

    public void update(Note note){
        new UpdateOperation(noteDao).execute(note);
    }

    public LiveData<List<Note>> getAllnotes() {
        return allnotes;
    }

    private static class InsertOperation extends AsyncTask<Note,Void,Void>{

        private NoteDao noteDao;
        private InsertOperation(NoteDao noteDao){
            this.noteDao = noteDao;
        }

        @Override
        protected Void doInBackground(Note... notes) {
            noteDao.insertNote(notes[0]);
            return null;
        }
    }

    private static class DeleteOperation extends AsyncTask<Note,Void,Void>{

        private NoteDao noteDao;
        private DeleteOperation(NoteDao noteDao){
            this.noteDao = noteDao;
        }

        @Override
        protected Void doInBackground(Note... notes) {
            noteDao.deletNote(notes[0]);
            return null;
        }
    }

    private static class UpdateOperation extends AsyncTask<Note,Void,Void>{

        private NoteDao noteDao;
        private UpdateOperation(NoteDao noteDao){
            this.noteDao = noteDao;
        }

        @Override
        protected Void doInBackground(Note... notes) {
            noteDao.updateNote(notes[0]);
            return null;
        }
    }

}

