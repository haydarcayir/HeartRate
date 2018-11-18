package com.example.haydarcayir.heartratem;

import android.content.Intent;
import android.graphics.Color;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.robinhood.spark.SparkAdapter;
import com.robinhood.spark.SparkView;

import java.util.ArrayList;
import java.util.Arrays;

import static java.lang.Math.ceil;

/**
 * Created by haydarcayir on 11.03.2018.
 */

public class HeartRate extends AppCompatActivity {

    private Handler handler = new Handler();
    private boolean flag = false;

    private  double max=0;
    private int peak=0;


    private boolean isFirst=true;
    private int progressStatus = 0;
    private int frameCounter=0;
    private double frequencyData;
    private double time;


    private TextView timeText;
    private TextView calculatingText;
    private ProgressBar progressBar;
    private SurfaceView surface;
    private long startTime,currentTime;
    private double avgGreen;
    private double avgRed;
    private double avgBlue;

    private SparkView sparkview;
    Intent intent;



    ArrayList<Double> arrayGreen = new ArrayList<Double>();
    ArrayList<Double> arrayRed = new ArrayList<Double>();
    ArrayList<Double> arrayBlue = new ArrayList<Double>();

    private static SurfaceHolder surfaceHolder = null;//????????
    private static Camera camera=null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heart_rate);

        /*   sparkview = (SparkView)findViewById(R.id.sparkview);
        sparkview.setAdapter(new MyAdapter(array));*/
        surface = (SurfaceView)findViewById(R.id.surface);
        surfaceHolder = surface.getHolder();
        surfaceHolder.addCallback(surfaceCallback);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        intent = new Intent(HeartRate.this,HeartRateResult.class);

        calculatingText = (TextView)findViewById(R.id.calculating_text);
        timeText = (TextView)findViewById(R.id.time_text);
        progressBar = (ProgressBar)findViewById(R.id.progressBar1);
        progressBar.setMax(25);
        progressBar.setIndeterminate(false);
        initValues();
    }


    @Override
    protected void onResume() {
        super.onResume();
        setKronometre().start();
        camera = Camera.open();
        camera.setDisplayOrientation(90);
        startTime= System.currentTimeMillis();


    }

    @Override
    protected void onPause() {
        super.onPause();
        camera.setPreviewCallback(null);
        camera.stopPreview();
        camera.release();
        camera =null;
    }


    private Camera.PreviewCallback previewCallback = new Camera.PreviewCallback(){
        @Override
        public void onPreviewFrame(byte[] data, Camera camera) {

            Camera.Size camSize = camera.getParameters().getPreviewSize();


            frameCounter++;
            Log.d("aynalı beşik","" + data );
            Log.d("frameCounter",""+frameCounter);

            avgGreen = ImageProcessing.decodeYUV420SPtoRedBlueGreenAvg(data.clone(),camSize.width,camSize.height,3);
            avgRed = ImageProcessing.decodeYUV420SPtoRedBlueGreenAvg(data.clone(),camSize.width,camSize.height,1);
            avgBlue = ImageProcessing.decodeYUV420SPtoRedBlueGreenAvg(data.clone(),camSize.width,camSize.height,2);
            arrayGreen.add(avgGreen);
            arrayRed.add(avgRed);
            arrayBlue.add(avgBlue);

            Log.d("haydarar","" + camSize.width + " " + camSize.height);
            Log.d("avgGreen","" + avgGreen);
            Log.d("avgRed", "" + avgRed);
            Log.d("avgBlue", "" + avgBlue);
            currentTime = System.currentTimeMillis();

            if(avgRed<200){
                frameCounter=0;
                startTime= System.currentTimeMillis();
                initValues();
                flag = true;
            }else{
                if(flag == true){
                    initValues();
                    setKronometre().start();
                    flag=false;
                }

            }


            if((currentTime-startTime)/1000d >= 25 ){

                if(isFirst == true) {
                    isFirst=false;

                    time =((currentTime-startTime)/1000d);
                    frequencyData = frameCounter / time ;
                    Log.d("frequencyData",""+ frequencyData);
                    Log.d("framesayısı",""+ frameCounter);

                    Double[] gFreq = arrayGreen.toArray(new Double[arrayGreen.size()]);
                    Double[] rFreq = arrayRed.toArray(new Double[arrayRed.size()]);
                    Double[] bFreq = arrayBlue.toArray(new Double[arrayBlue.size()]);



                    double[] greenArray = new double[gFreq.length];
                    double[] blueArray = new double[bFreq.length];
                    double[] redArray = new double[rFreq.length];


                    for (int i =0 ; i<(gFreq.length) ; i++){
                        greenArray[i] = (gFreq[i]);
                    }
                    for (int i =0 ; i<(rFreq.length) ; i++){
                        redArray[i] = (rFreq[i]);
                    }
                    for (int i =0 ; i<(bFreq.length) ; i++){
                        blueArray[i] = (bFreq[i]);
                    }

                    greenArray=HanningWindow(greenArray,0,frameCounter);
                    blueArray=HanningWindow(blueArray,0,frameCounter);
                    redArray=HanningWindow(redArray,0,frameCounter);




                    boolean var=false;
                    float[] greenFloatArray = new float[gFreq.length];

                    for (int i = 0; i<(gFreq.length) ; i++){

                        if((i>4) && (greenArray[i]>greenArray[i-1])){
                                 var = true;
                                greenArray[i]+=10;
                            }
                        greenFloatArray[i] = (float)greenArray[i] ;
                        if(var == true){
                            greenArray[i]-=10;
                            var=false;
                        }

                    }

                    double freq = F_transform(greenArray,frequencyData,1);
                    double result_g = (int)ceil(freq*60);
                    peak=0;
                    max=0;
                    double freq1 = F_transform(redArray,frequencyData,2);
                    double result_r = (int)ceil(freq1*60);
                    peak=0;
                    max=0;
                    double freq2 = F_transform(blueArray,frequencyData,3);
                    double result_b = (int)ceil(freq2*60);

                    double g_r = Math.abs(result_g-result_r);
                    double g_b = Math.abs(result_g-result_b);
                    double b_r = Math.abs(result_b-result_r);

                    Log.d("Blue res",""+result_b);
                    Log.d("Red res",""+result_r);
                    Log.d("Green res",""+result_g);


                    String s = "G:" + result_g + " R:" + result_r + " B:" + result_b;


                    if(((!(result_g < 40 || result_g > 160 )) && (!(result_r < 40 || result_r > 160 )) && (!(result_b < 40 || result_b > 160 )))){
                        if(g_r<=10 && g_b<=10){
                            result_g = (result_g + result_b + result_r) / 3;
                            intent.putExtra("sonuc",result_g);
                            intent.putExtra("greenFloatArray",greenFloatArray);
                            intent.putExtra("allresult",s);
                            startActivity(intent);
                        }
                        else if(g_r <= 10){
                            result_g = (result_g + result_r) / 2;
                            intent.putExtra("sonuc",result_g);
                            intent.putExtra("greenFloatArray",greenFloatArray);
                            intent.putExtra("allresult",s);
                            startActivity(intent);

                        }
                        else if(g_b <= 10){
                            result_g = (result_g + result_b) / 2;
                            intent.putExtra("sonuc",result_g);
                            intent.putExtra("greenFloatArray",greenFloatArray);
                            intent.putExtra("allresult",s);
                            startActivity(intent);

                        }
                        else{
                            flag=true;
                            Toast.makeText(getApplicationContext(),"!!FAULTY MEASUREMENT!!",Toast.LENGTH_SHORT).show();
                            frameCounter=0;
                            startTime= System.currentTimeMillis();
                            initValues();
                        }


                    }else{
                        flag=true;
                        Toast.makeText(getApplicationContext(),"!!FAULTY MEASUREMENT!!",Toast.LENGTH_SHORT).show();
                        frameCounter=0;
                        startTime= System.currentTimeMillis();
                        initValues();
                    }


                }
            }

        }
    };




    private  SurfaceHolder.Callback surfaceCallback = new SurfaceHolder.Callback() {

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            try {
                camera.setPreviewDisplay(surfaceHolder);
                camera.setPreviewCallback(previewCallback);
            } catch (Throwable t) {
               // Log.e("PreviewDemo-surfaceCallback", "Exception in setPreviewDisplay()", t);
            }
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            Camera.Parameters parameters = camera.getParameters();
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);

            Camera.Size size = getSmallestPreviewSize(width, height, parameters);
            if (size != null) {
                parameters.setPreviewSize(size.width, size.height);
    //            Log.d(TAG, "Using width=" + size.width + " height=" + size.height);
            }

            camera.setParameters(parameters);
            camera.startPreview();
        }


        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
        }
    };

    private static Camera.Size getSmallestPreviewSize(int width, int height, Camera.Parameters parameters) {
        Camera.Size result = null;

        for (Camera.Size size : parameters.getSupportedPreviewSizes()) {
            if (size.width <= width && size.height <= height) {
                if (result == null) {
                    result = size;
                } else {
                    int resultArea = result.width * result.height;
                    int newArea = size.width * size.height;
                    if (newArea < resultArea) result = size;
                }
            }
        }

        return result;
    }


    public double F_transform(double [] in , double sampFreq , int type){

        int i = 0;
        double[] data = new double[2*frameCounter];
        float[] fourierArray = new float[frameCounter];

        Arrays.fill(data,0);

        int k = frameCounter;

        while (k>0){
            k--;
            data[i] = in[i];
            i++;
        }

        DoubleFft1d FFT = new DoubleFft1d(frameCounter);
        FFT.realForward(data);



        int j = 0;
        while(j<data.length){
            data[j] = Math.abs(data[j]);
            j++;
        }


        for (int l = 39;l<115;l++){
            Log.d("asdasd",""+data[l]);
            if(data[l]> max){
                max = data[l];
                peak = l;
            }

        }
        Log.d("asdasd","asdasdasdasd");

        if(type == 1){
            int l = 39;
            while ( data[l] != 0.0){
                fourierArray[l] = (float)data[l];
                l++;
            }
            for(int t=0;t<39;t++){
                int w = 200;
                fourierArray[t]=(float)data[peak + w];
                w++;
            }
            intent.putExtra("fourierArray",fourierArray);

        }



        Log.d("max",""+max);
        Log.d("peak",""+peak);
        Log.d("bpm",""+peak/(2*time));

        return peak*sampFreq/(2*frameCounter);

    }


    public void initValues(){
        progressStatus=0;
        isFirst=true;
        flag = true;
        progressBar.setProgress(progressStatus);
        timeText.setText("0sn / 25sn");

    }
    private Thread setKronometre(){
        return new Thread(new Runnable() {
            public void run() {
                while (progressStatus < 25) {
                    if(flag == true)
                        break;
                    progressStatus += 1;
                    // yeni değeri ekranda göster ve progressBar'a set et.
                    handler.post(new Runnable() {
                        public void run() {
                            progressBar.setProgress(progressStatus);
                            timeText.setText(progressStatus + "sn / "
                                    + progressBar.getMax()+"sn");
                        }
                    });
                    try {
                        // Sleep for 1 second.
                        // Just to display the progress slowly
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public double[] HanningWindow(double[] signal_in, int pos, int size)
    {
        for (int i = pos; i < pos + size; i++)
        {
            int j = i - pos; // j = index into Hann window function
            signal_in[i] = (signal_in[i] * 0.5 * (1.0 - Math.cos(2.0 * Math.PI * j / (size-1))));
        }
        return signal_in;
    }


}
