package com.example.haydarcayir.heartratem;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

/**
 * Created by haydarcayir on 11.03.2018.
 */

public class WarningActivity extends AppCompatActivity implements View.OnClickListener{

    Button warningStartButton;
    ImageView backArrow;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_warning);

        backArrow=(ImageView)findViewById(R.id.warning_back);
        warningStartButton=(Button)findViewById(R.id.warning_start_button);

        warningStartButton.setOnClickListener(this);
        backArrow.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        if(v.getId() == R.id.warning_back){
            Intent intent = new Intent(WarningActivity.this,MainActivity.class);
            startActivity(intent);
            finish();

        }
        if(v.getId() == R.id.warning_start_button){
            Intent intent = new Intent(WarningActivity.this,HeartRate.class);
            startActivity(intent);
            finish();

        }

    }
}

