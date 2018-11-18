package com.example.haydarcayir.heartratem;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.robinhood.spark.SparkAdapter;
import com.robinhood.spark.SparkView;

/**
 * Created by haydarcayir on 16.04.2018.
 */

public class SignalChart extends Activity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signal_chart);

        SparkView sparkView = (SparkView) findViewById(R.id.sparkview);
        SparkView sparkView1 = (SparkView) findViewById(R.id.sparkview1);


        float[] greenArray = getIntent().getFloatArrayExtra("ppgChart");
        sparkView.setAdapter(new MyAdapter(greenArray));

        float[] fourierArray = getIntent().getFloatArrayExtra("fourierChart");
        sparkView1.setAdapter(new MyAdapter(fourierArray));

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
