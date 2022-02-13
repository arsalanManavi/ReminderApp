package com.example.reminderapp.viewmodel;

import android.app.Application;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.reminderapp.model.Note;
import com.example.reminderapp.repository.NoteRepository;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MainViewModel extends AndroidViewModel {
    private NoteRepository noteRepository;
    private LiveData<List<Note>> allnotes;
    private MutableLiveData<List<Note>> AllNotes;
   static Calendar myCalendar = Calendar.getInstance();
    String dummydata="These fields must be completed";
    public MainViewModel( Application application){
        super(application);
        noteRepository = new NoteRepository(application);
        allnotes = noteRepository.getAllnotes();

    }


    public void insertNote(Note note){
        noteRepository.insert(note);
    }
    public void deletNote(Note note){
        noteRepository.delete(note);
    }
    public void updateNote(Note note){
        noteRepository.update(note);
    }

    public boolean validate(EditText title, EditText desc, EditText date, EditText time, EditText event){
        if (title.getText().toString().equalsIgnoreCase("")) {
            Toast.makeText(getApplication(), dummydata, Toast.LENGTH_SHORT).show();
            return false;
        } else if (desc.getText().toString().equalsIgnoreCase(""))  {
            Toast.makeText(getApplication(), dummydata, Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (date.getText().toString().equalsIgnoreCase("")){
            Toast.makeText(getApplication(), dummydata, Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (time.getText().toString().equalsIgnoreCase("")){
            Toast.makeText(getApplication(), dummydata, Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (event.getText().toString().equalsIgnoreCase("")){
            Toast.makeText(getApplication() , dummydata, Toast.LENGTH_SHORT).show();
            return false;
        }else {
            return true;
        }
    }
    public void setallnotes (String title,String desc,String date,String time,String event){
        Note note = new Note();
        note.setTitle(title);
        note.setDescription(desc);
        note.setDate(date);
        note.setTime(time);
        note.setEvent(event);
        setmonth(note);
        setDay(note);
        setNumberOfMonth(note);
        insertNote(note);
    }

    public void setNumberOfMonth(Note note){
        String format ="MM";
        SimpleDateFormat formatMonth = new SimpleDateFormat(format, Locale.US);
        note.setDd(formatMonth.format(myCalendar.getTime()));
    }
    public static void setmonth(Note note){
        String format ="MMM";
        SimpleDateFormat formatMonth = new SimpleDateFormat(format,Locale.US);
        note.setM(formatMonth.format(myCalendar.getTime()));
    }

    public void setDay(Note note){
        String format ="EE";
        SimpleDateFormat formatMonth = new SimpleDateFormat(format,Locale.US);
        note.setD(formatMonth.format(myCalendar.getTime()));
    }


    public LiveData<List<Note>> getAllnotes() {
        return allnotes;
    }
}
