package com.example.reminderapp.adapter;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Application;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.example.reminderapp.MainActivity;
import com.example.reminderapp.R;
import com.example.reminderapp.Receiver.AlarmReciever;
import com.example.reminderapp.model.Note;
import com.example.reminderapp.repository.NoteRepository;
import com.example.reminderapp.viewmodel.MainViewModel;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.ViewHolder>{
        int Year,Month,Day_Of_Month;
        int count = 0;
        MainViewModel viewModel;
        Context context;
        List<Note> notes = new ArrayList<>();
        Calendar myCalendar = Calendar.getInstance();
    public MainAdapter(Context context) {
        viewModel =new ViewModelProvider((ViewModelStoreOwner) context).get(MainViewModel.class);
        this.context = context;
    }


    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.main_item_recyclerview,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {

        Note currentNote = notes.get(position);

        holder.textviewTitleText.setText(currentNote.getTitle());
        holder.textviewDesctext.setText(currentNote.getDescription());
        holder.textviewClockText.setText(currentNote.getTime());
        holder.textviewMonthText.setText(currentNote.getM());
        holder.textviewNumberOfMonthText.setText(currentNote.getDd());
        holder.DayOfMonthText.setText(currentNote.getD());
        holder.Options.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu menu = new PopupMenu(context,v);
                MenuInflater inflater = menu.getMenuInflater();
                inflater.inflate(R.menu.options,menu.getMenu());
                menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.Completed:
                                AlertDialog.Builder builder = new AlertDialog.Builder(context,R.style.AppTheme_Dialog);
                                builder.setTitle("Complete The Task!").setMessage("Are You Sure You Want Complete This Task?").
                                        setPositiveButton("yes", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                    completeFun(currentNote,position);
                                            }
                                        }).
                                        setNegativeButton("No", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.cancel();
                                            }
                                        }).show();

                                break;
                            case R.id.Updated:
                                updateFun(v,currentNote);
                                holder.textviewMonthText.setText(currentNote.getM());
                                holder.textviewNumberOfMonthText.setText(currentNote.getDd());
                                holder.DayOfMonthText.setText(currentNote.getD());
                                break;
                            case R.id.Deleted:
                                deleteFun(currentNote,position);
                                break;
                        }
                        return true;
                    }
                });
                menu.show();
            }
        });


    }

    private void deleteFun(Note note,int position) {
        viewModel.deletNote(note);
        notes.remove(note);
        notifyItemRemoved(position);
    }

    private void updateFun(View view,Note note) {
        LinearLayout linearLayout = view.findViewById(R.id.layoutt);
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context, R.style.ModalBottomSheetDialog);
        View sheetView = LayoutInflater.from(context).inflate(R.layout.bottom_sheet, linearLayout);
        bottomSheetDialog.setContentView(sheetView);
        Button createTask = sheetView.findViewById(R.id.buttonCreateTask);
        EditText title = sheetView.findViewById(R.id.editTextTitle);
        EditText desc = sheetView.findViewById(R.id.editTextDescription);
        EditText datey = sheetView.findViewById(R.id.editTextDate);
        EditText time = sheetView.findViewById(R.id.editTextTime);
        EditText event = sheetView.findViewById(R.id.editTextEvent);
        datey.setFocusable(false);
        time.setFocusable(false);

        title.setText(note.getTitle());
        desc.setText(note.getDescription());
        datey.setText(note.getDate());
        time.setText(note.getTime());
        event.setText(note.getEvent());
        bottomSheetDialog.show();

        datey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker(datey);
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
                note.setTitle(noteTitle);
                note.setDescription(noteDesc);
                note.setDate(noteDate);
                note.setTime(noteTime);
                note.setEvent(noteEvent);
                setDay(note);
                setmonth(note);
                setNumberOfMonth(note);
                if (viewModel.validate(title, desc, datey, time, event)) {
                    viewModel.updateNote(note);
                    createAlarm(time,datey,title,desc);
                    bottomSheetDialog.dismiss();

                }
            }
        });
    }

    private void showTimePicker(EditText time){

        TimePickerDialog.OnTimeSetListener listener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                myCalendar.set(Calendar.HOUR_OF_DAY,hourOfDay);
                myCalendar.set(Calendar.MINUTE,minute);
                time.setText(String.format("%02d:%02d",hourOfDay,minute));
                //                 time.setText(hourOfDay+":"+minute);
            }
        };
        TimePickerDialog timePickerDialog = new  TimePickerDialog(context,listener,myCalendar.get(Calendar.HOUR_OF_DAY),myCalendar.get(Calendar.MINUTE),false);

        timePickerDialog.show();
    }

    private void showDatePicker(EditText datey) {

        Year = myCalendar.get(Calendar.YEAR);
        Month = myCalendar.get(Calendar.MONTH);
        Day_Of_Month = myCalendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                datey.setText((month+1) + "-" + dayOfMonth + "-" + year);
            }
        },Year,Month,Day_Of_Month);

        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        datePickerDialog.show();
    }


    private void setmonth(Note note) {
        String format ="MMM";
        SimpleDateFormat formatMonth = new SimpleDateFormat(format,Locale.US);
        note.setM(formatMonth.format(myCalendar.getTime()));
    }

    private void setDay(Note note){
        String format ="EE";
        SimpleDateFormat formatMonth = new SimpleDateFormat(format,Locale.US);
        note.setD(formatMonth.format(myCalendar.getTime()));
    }
    private void setNumberOfMonth(Note note){
        String format ="MM";
        SimpleDateFormat formatMonth = new SimpleDateFormat(format,Locale.US);
        note.setDd(formatMonth.format(myCalendar.getTime()));
    }
    private void completeFun(Note note,int position) {

        Dialog dialog = new Dialog(context,R.style.AppTheme_Dialog);
        dialog.setContentView(R.layout.complete_dialog);
        MaterialButton completedButtonClose = dialog.findViewById(R.id.completedButtonClose);
        completedButtonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewModel.deletNote(note);
                dialog.dismiss();
            }
        });
        dialog.show();
        notes.remove(note);
        notifyItemRemoved(position);

    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    public void setNotes(List<Note> notes){
        this.notes = notes;
        notifyDataSetChanged();
    }
    public void checkSizeOfList(TextView textView,List<Note> notes){
        if (notes.size()<=0){
            textView.setVisibility(View.VISIBLE);
        }else
            textView.setVisibility(View.INVISIBLE);
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void createAlarm(EditText time,EditText datey,EditText title,EditText desc) {

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        try {
            Calendar calendar = new GregorianCalendar();
            calendar.setTimeInMillis(System.currentTimeMillis());
            String[] itemTime = time.getText().toString().split(":");
            String hour = itemTime[0];
            String minute = itemTime[1];
            calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hour));
            calendar.set(Calendar.MINUTE, Integer.parseInt(minute));
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);

            String[] itemDate = datey.getText().toString().split("-");
            String date = itemDate[0];
            calendar.set(Calendar.DATE, Integer.parseInt(date));

            Intent intent = new Intent(context, AlarmReciever.class);
            intent.putExtra(MainActivity.KEY_TITLE, title.getText().toString());
            intent.putExtra(MainActivity.KEY_DESC, desc.getText().toString());
            intent.putExtra(MainActivity.KEY_DATE, datey.getText().toString());
            intent.putExtra(MainActivity.KEY_TIME, time.getText().toString());

            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, count, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);

            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);

            } else {
                alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            }
            count++;
        } catch (Exception e) {
            e.printStackTrace();
        }
}

class ViewHolder extends RecyclerView.ViewHolder {

    TextView DayOfMonthText, textviewNumberOfMonthText, textviewMonthText, textviewTitleText, textviewDesctext, textviewClockText;
    ImageView Options;


    public ViewHolder(@NonNull @NotNull View itemView) {
        super(itemView);
        DayOfMonthText = itemView.findViewById(R.id.textviewDayOfMonthText);
        textviewNumberOfMonthText = itemView.findViewById(R.id.textviewNumberOfmonthtext);
        textviewMonthText = itemView.findViewById(R.id.textviewMonthText);
        textviewTitleText = itemView.findViewById(R.id.textviewTitleText);
        textviewDesctext = itemView.findViewById(R.id.textviewDescText);
        textviewClockText = itemView.findViewById(R.id.textviewClockText);
        Options = itemView.findViewById(R.id.three_Dots);
        textviewDesctext.setMaxLines(1);
        textviewDesctext.setEllipsize(TextUtils.TruncateAt.END);


    }


}

}
