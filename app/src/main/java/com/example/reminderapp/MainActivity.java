package com.example.reminderapp;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.reminderapp.Receiver.AlarmReciever;
import com.example.reminderapp.adapter.MainAdapter;
import com.example.reminderapp.model.Note;
import com.example.reminderapp.viewmodel.MainViewModel;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.timepicker.MaterialTimePicker;

import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    int Year;
    int Month;
    int Day_Of_Month;

    public static String KEY_TITLE = "title";
    public static String KEY_DESC = "desc";
    public static String KEY_DATE = "date";
    public static String KEY_TIME = "time";
    int count = 0;
    TextView hellotextview, emptyalarmtextview;
    MaterialTimePicker materialTimePicker;
    RecyclerView recyclerViewmain;
    MainViewModel viewModel;
    Button buttonCreateTask;
    EditText desc, time, event;
    EditText title;
    EditText datey;
    Calendar myCalendar;
    LinearLayout layout;
    AlarmManager alarmManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        layout = findViewById(R.id.layoutt);
        emptyalarmtextview = findViewById(R.id.emptyalarmTextView);
        // hellotextview = findViewById(R.id.helloTextview);
        buttonCreateTask = findViewById(R.id.materialButton);

        ComponentName reciever = new ComponentName(this, AlarmReciever.class);
        PackageManager pm = getPackageManager();
        pm.setComponentEnabledSetting(reciever, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
        recyclerViewmain = findViewById(R.id.mainRecyclerView);
        MainAdapter adapter = new MainAdapter(MainActivity.this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(MainActivity.this, LinearLayoutManager.VERTICAL, false);
        recyclerViewmain.setLayoutManager(layoutManager);
        recyclerViewmain.setAdapter(adapter);
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        viewModel.getAllnotes().observe(this, new Observer<List<Note>>() {
            @Override
            public void onChanged(List<Note> notes) {
                adapter.checkSizeOfList(emptyalarmtextview, notes);
                adapter.setNotes(notes);
            }
        });


        myCalendar = Calendar.getInstance();

        buttonCreateTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonCreateTask.setEnabled(false);
                Timer buttonTimer = new Timer();
                buttonTimer.schedule(new TimerTask() {

                    @Override
                    public void run() {

                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                buttonCreateTask.setEnabled(true);
                            }
                        });
                    }
                }, 500);
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(MainActivity.this, R.style.ModalBottomSheetDialog);
                View sheetView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.bottom_sheet, layout);
                Button createTask = sheetView.findViewById(R.id.buttonCreateTask);
                title = sheetView.findViewById(R.id.editTextTitle);
                desc = sheetView.findViewById(R.id.editTextDescription);
                datey = sheetView.findViewById(R.id.editTextDate);
                time = sheetView.findViewById(R.id.editTextTime);
                event = sheetView.findViewById(R.id.editTextEvent);
                datey.setFocusable(false);
                time.setFocusable(false);


                datey.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDatePicker();
                    }
                });
                time.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showTimePicker(time);
                    }
                });

                createTask.setOnClickListener(new View.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onClick(View v) {
                        String noteTitle = title.getText().toString();
                        String noteDesc = desc.getText().toString();
                        String noteDate = datey.getText().toString();
                        String noteTime = time.getText().toString();
                        String noteEvent = event.getText().toString();

                        if (viewModel.validate(title, desc, datey, time, event)) {
                            viewModel.setallnotes(noteTitle, noteDesc, noteDate, noteTime, noteEvent);
                            createAlarm();
                            bottomSheetDialog.dismiss();
                        }
                    }
                });
                bottomSheetDialog.setContentView(sheetView);
                bottomSheetDialog.show();

            }

        });


    }

    private void showTimePicker(EditText time) {
        Calendar calendar = Calendar.getInstance();
        TimePickerDialog.OnTimeSetListener listener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);
                time.setText(String.format("%02d:%02d", hourOfDay, minute));
                //                 time.setText(hourOfDay+":"+minute);

            }
        };
        TimePickerDialog timePickerDialog = new TimePickerDialog(MainActivity.this, listener, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false);

        timePickerDialog.show();

    }

    private void showDatePicker() {
        Year = myCalendar.get(Calendar.YEAR);
        Month = myCalendar.get(Calendar.MONTH);
        Day_Of_Month = myCalendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                datey.setText(dayOfMonth + "-" + (month + 1) + "-" + year);


            }
        }, Year, Month, Day_Of_Month);
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        datePickerDialog.show();

    }

/*    private void updateLabel() {
        String myFormat = "MM/dd/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        datey.setText(sdf.format(myCalendar.getTime()));
    }*/

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void createAlarm() {


        try {
            Calendar calendar = Calendar.getInstance();

                String[] itemTime = time.getText().toString().split(":");
                String hour = itemTime[0];
                String minute = itemTime[1];
                String[] itemDate = datey.getText().toString().split("-");
                String date = itemDate[0];

                calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hour));
                calendar.set(Calendar.MINUTE, Integer.parseInt(minute));
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
                calendar.set(Calendar.DATE, Integer.parseInt(date));

                alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

                Intent intent = new Intent(MainActivity.this, AlarmReciever.class);
                intent.putExtra(KEY_TITLE, title.getText().toString());
                intent.putExtra(KEY_DESC, desc.getText().toString());
                intent.putExtra(KEY_DATE, datey.getText().toString());
                intent.putExtra(KEY_TIME, time.getText().toString());
                PendingIntent pendingIntent = PendingIntent.getBroadcast(this, count, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis() - 1000, pendingIntent);

                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis() - 1000, pendingIntent);

                } else {
                    alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis() - 1000, pendingIntent);
                }
                count++;








        } catch (Exception e) {
            e.printStackTrace();
        }


    }




}
