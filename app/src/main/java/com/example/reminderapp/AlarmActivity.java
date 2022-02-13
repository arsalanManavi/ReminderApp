package com.example.reminderapp;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.reminderapp.Receiver.AlarmReciever;
import com.google.android.material.button.MaterialButton;

public class AlarmActivity extends AppCompatActivity {

    TextView title, desc, date;
    MaterialButton closeButton;
    ImageView alarmAnimation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        title = findViewById(R.id.alarmTitle);
        desc = findViewById(R.id.alarmDesc);
        date = findViewById(R.id.alarmTime_Date);
        closeButton = findViewById(R.id.closeButton);
        alarmAnimation = findViewById(R.id.alarmImageView);

            if (getIntent().getExtras()!=null && !getIntent().getExtras().isEmpty()){
                title.setText(getIntent().getStringExtra(AlarmReciever.KEY_TITLE));
                desc.setText(getIntent().getStringExtra(AlarmReciever.KEY_DESC));
                date.setText(getIntent().getStringExtra(AlarmReciever.KEY_DATE) + " , " + getIntent().getStringExtra(AlarmReciever.KEY_TIME));

            }

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }


}