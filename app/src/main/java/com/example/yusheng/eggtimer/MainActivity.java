package com.example.yusheng.eggtimer;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    SeekBar timerSeekBar;
    TextView timerTextView;
    Button startPauseButton;
    boolean isRunning = false;
    int progressSaver;
    CountDownTimer timer;
    int secondsLeft;
    MediaPlayer mPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        timerSeekBar = (SeekBar) findViewById(R.id.timerSeekBar);
        timerSeekBar.setMax(600);//10 minutes
        timerSeekBar.setProgress(30);//30 seconds on default

        timerTextView = (TextView) findViewById(R.id.timerTextView);


        timerSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                secondsLeft = seekBar.getProgress();
                updateTimer(secondsLeft);
                seekBar.setProgress(secondsLeft);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                pauseTimer();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if(timer != null){
                    timer.cancel();
                }
                startPauseButton.setText("Start");
                updateTimer(seekBar.getProgress());
            }
        });

        startPauseButton = (Button) findViewById(R.id.startPauseButton);
        mPlayer = MediaPlayer.create(this, R.raw.beep);
        mPlayer.start();
    }


    public String processTimeToString(int timeInt){
        String retString = Integer.toString(timeInt);
        if(retString.length() == 1){
            retString = "0"+retString;
        }
        return retString;
    }

    public void updateTimer(int secondsLeft){
        if(secondsLeft>=0){
            int minutes = (int) secondsLeft/60; //round down when cast to int
            int seconds = secondsLeft - minutes*60;
            Log.i("time.minutes: ",Integer.toString(minutes));
            Log.i("time.seconds: ", Integer.toString(seconds));

            timerTextView.setText(processTimeToString(minutes) +":"+processTimeToString(seconds));
            timerSeekBar.setProgress(secondsLeft);
        }
    }

    //click the stop button
    public void stopTimer(View view){
        timerTextView.setText("00:00");
        timerSeekBar.setProgress(0);
        isRunning = false;
        if(timer != null){
            timer.cancel();
        }
        startPauseButton.setText("Start");
        mPlayer.stop();
    }

    public void pauseTimer(){
        //run --> pause
        isRunning = false;
        startPauseButton.setText("Start");
        if(timer != null){
            timer.cancel();
        }
    }

    //click the start button
    public void controlTimer(View view){
        mPlayer.stop();
        if(isRunning){
            pauseTimer();
        }else{
            //pause ---> run
            isRunning = true;
            startPauseButton.setText("Pause");
            Log.i("start", "clicked");
            timer = new CountDownTimer(timerSeekBar.getProgress()*1000 + 100, 1000){
                @Override
                public void onTick(long millisUntilFinished){
                    //Log.i("start", "progress:"+Integer.toString(timerSeekBar.getProgress()));
                    updateTimer(timerSeekBar.getProgress()-1);
                }

                @Override
                public void onFinish(){
                    timerSeekBar.setProgress(0);
                    isRunning = false;

                    mPlayer.start();
                    startPauseButton.setText("Start");
                }
            }.start();
        }

    }

}
