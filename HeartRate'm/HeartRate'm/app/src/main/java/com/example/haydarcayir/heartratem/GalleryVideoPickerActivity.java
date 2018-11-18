package com.example.haydarcayir.heartratem;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;

import static java.lang.Math.ceil;

/**
 * Created by haydarcayir on 1.05.2018.
 */

public class GalleryVideoPickerActivity extends Activity  {
    private static final int SELECT_VIDEO = 1;

    private long startTime,currentTime;
    private String selectedVideoPath;
    ArrayList<Double> arrayGreen = new ArrayList<Double>();
    double avgGreen;
    private  double max=0;
    private int peak=0;
    private double time;
    HeartRate heart = new HeartRate();


    @ Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, SELECT_VIDEO);
    }

    @ Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_VIDEO) {
                selectedVideoPath = getPath(data.getData());
                    if(selectedVideoPath == null) {
                  //      Log.e("selected video path = null!");
                        finish();
                    } else {

                        startTime= System.currentTimeMillis();

                        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();

                        mediaMetadataRetriever.setDataSource(selectedVideoPath);
                        Bitmap bmFrame = null; //unit in microsecond


                        boolean bool = true;
                        for(int i =0;i<250;i++){
                            bmFrame = mediaMetadataRetriever.getFrameAtTime(40*i);
                            int size = bmFrame.getRowBytes() * bmFrame.getHeight();
                            ByteBuffer byteBuffer = ByteBuffer.allocate(size);
                            bmFrame.copyPixelsToBuffer(byteBuffer);
                            byte[] byteArray = byteBuffer.array();
                            avgGreen = ImageProcessing.decodeYUV420SPtoRedBlueGreenAvg(byteArray.clone(),bmFrame.getWidth(),bmFrame.getHeight(),3);
                            arrayGreen.add(avgGreen);
                            Log.d("readed frames",""+ byteArray);
                            Log.d("avgGreen","" + avgGreen);
                            currentTime = System.currentTimeMillis();
                            if((currentTime-startTime)/1000d >= 10 && bool ){
                                bool=false;
                                Double[] gFreq = arrayGreen.toArray(new Double[arrayGreen.size()]);
                                double[] greenArray = new double[gFreq.length];

                                for (int j =0 ; j<(gFreq.length) ; j++){
                                    greenArray[j] = (gFreq[j]);
                                }
                                time = (currentTime-startTime)/1000d;
                                greenArray =  heart.HanningWindow(greenArray,0,i);
                                double freq = F_transform(greenArray,(i/time),0,i);
                                Log.d("aklım","gider");
                                double result_g = (int)ceil(freq*60);
                                Log.d("resssssss",""+result_g);

                            }


                        }



                    }


            }
        }

            finish();
    }

    public String getPath(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        if(cursor!=null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }
        else return null;
    }

    public double F_transform(double [] in , double sampFreq , int type , int frameCount){

        int i = 0;
        double[] data = new double[2*frameCount];

        Arrays.fill(data,0);

        int k = frameCount;

        while (k>0){
            k--;
            data[i] = in[i];
            i++;
        }

        DoubleFft1d FFT = new DoubleFft1d(frameCount);
        FFT.realForward(data);



        int j = 0;
        while(j<data.length){
            data[j] = Math.abs(data[j]);
            j++;
        }


        for (int l = 16;l<40;l++){
            Log.d("asdasd",""+data[l]);
            if(data[l]> max){
                max = data[l];
                peak = l;
            }

        }
        Log.d("asdasd","asdasdasdasd");

        Log.d("max",""+max);
        Log.d("peak",""+peak);
        Log.d("bpm",""+peak/(2*time));

        return peak*sampFreq/(2*frameCount);

    }

}


