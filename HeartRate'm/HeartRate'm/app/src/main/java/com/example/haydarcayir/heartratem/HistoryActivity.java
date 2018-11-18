package com.example.haydarcayir.heartratem;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by haydarcayir on 12.03.2018.
 */

public class HistoryActivity extends AppCompatActivity {

    private String[] name = {"hadyar","kerem","azra"};
    private String[] result = {"58","68","72"};
    private String[] date = {"26.04.1994","12.21.2131","13.41.1233"};


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        List<HashMap<String,String>> aList = new ArrayList<HashMap<String,String>>();

        for(int i=0;i<3;i++){
            HashMap<String, String> hm = new HashMap<String,String>();
            hm.put("isim",  name[i]);
            hm.put("sonuc", result[i]);
            hm.put("tarih", date[i]);
            aList.add(hm);
        }

        String[] from = { "isim","sonuc","tarih"};

        int[] to = { R.id.detail_name,R.id.detail_heart_rate_result,R.id.detail_date};

        ListView listview = (ListView)findViewById(R.id.history_list_view);

        SimpleAdapter veriAdaptoru=new SimpleAdapter(this,aList,R.layout.heart_rate_detail, from, to);

        listview.setAdapter(veriAdaptoru);


    }
}
