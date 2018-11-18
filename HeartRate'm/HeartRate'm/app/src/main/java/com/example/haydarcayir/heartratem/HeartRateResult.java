package com.example.haydarcayir.heartratem;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.robinhood.spark.SparkAdapter;
import com.robinhood.spark.SparkView;

/**
 * Created by haydarcayir on 11.03.2018.
 */

public class HeartRateResult extends AppCompatActivity implements View.OnClickListener {

    ImageView home;
    TextView heartRateResult;
    Button storeButton;
    TextView seeSignal,allResult;
    float[] greenArray,fourierArray;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heart_rate_result);
        home = (ImageView)findViewById(R.id.result_home);
        heartRateResult = (TextView)findViewById(R.id.heart_rate_result);
        storeButton = (Button) findViewById(R.id.result_button_store);
        seeSignal = (TextView)findViewById(R.id.see_ppg_signal);
        allResult = (TextView)findViewById(R.id.tum_sonuclar);

        Intent intent = getIntent();
        double heartRate = intent.getDoubleExtra("sonuc",0);
        String allRes = intent.getStringExtra("allresult");
        heartRateResult.setText(""+(int)heartRate);
        allResult.setText(allRes);



        greenArray = getIntent().getFloatArrayExtra("greenFloatArray");
        fourierArray  = getIntent().getFloatArrayExtra("fourierArray");

        storeButton.setOnClickListener(this);
        home.setOnClickListener(this);
        seeSignal.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.result_home){
            Intent intent = new Intent(HeartRateResult.this,MainActivity.class);
            startActivity(intent);
            finish();
        }
        if(v.getId() == R.id.see_ppg_signal){
            Intent intent = new Intent(HeartRateResult.this,SignalChart.class);
            intent.putExtra("ppgChart",greenArray);
            intent.putExtra("fourierChart",fourierArray);
            startActivity(intent);
        }


    }
    public class MyAdapter extends SparkAdapter {
        private float[] yData;

        public MyAdapter(float[] yData) {
            this.yData = yData;
        }

        @Override
        public int getCount() {
            return yData.length;
        }

        @Override
        public Object getItem(int index) {
            return yData[index];
        }

        @Override
        public float getY(int index) {
            return yData[index];
        }
    }
}
