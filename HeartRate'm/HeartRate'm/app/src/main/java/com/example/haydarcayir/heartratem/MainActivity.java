package com.example.haydarcayir.heartratem;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import org.apache.commons.math3.complex.Complex;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button heartRate,history,about,video;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        heartRate = (Button)findViewById(R.id.heart_rate_button);
        history = (Button)findViewById(R.id.history_button);
        about = (Button)findViewById(R.id.about_button);
        video = (Button)findViewById(R.id.video_button);


        video.setOnClickListener(this);
        heartRate.setOnClickListener(this);
        history.setOnClickListener(this);
        about.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        if(v.getId() == R.id.heart_rate_button){
            Intent intent = new Intent(MainActivity.this,WarningActivity.class);
            startActivity(intent);
        }
        else if(v.getId() == R.id.history_button){
            Intent intent = new Intent(MainActivity.this,HistoryActivity.class);
            startActivity(intent);
        }
        else if(v.getId() == R.id.video_button){
            Intent intent = new Intent(MainActivity.this,GalleryVideoPickerActivity.class);
            startActivity(intent);
        }
        else{
            Intent intent = new Intent(MainActivity.this,AboutActivity.class);
            startActivity(intent);

        }


    }
}
