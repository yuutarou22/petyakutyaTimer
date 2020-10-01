package com.example.yutaro0224.petyakutyatimer;

import android.content.Intent;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    private Button startButton,stopButton;
    private TextView timerText;

    private Timer timer;
    private CountUpTimerTask timerTask = null;
    private Handler handler = new Handler();
    private long count = 0;
    private boolean scount = false;
    private long stock = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        timerText = (TextView)findViewById(R.id.timer);
        //timerText.setText("00:00.0");
        timerText.setText("00:00");

        startButton = (Button)findViewById(R.id.start_Button);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //タイマーが動いている時にタップされた場合
                if (null != timer) { //初期状態ではnull(見てみろまだ代入されてないぞ)
                    timer.cancel();
                    timer = null;
                }

                //Timerインスタンスを生成
                timer = new Timer();
                //TimerTaskインスタンスを生成
                timerTask = new CountUpTimerTask();
                timer.schedule(timerTask, 0, 1000);//1秒間隔で実行

                //カウンター
                count = 0;
                timerText.setText("00:00");
            }
        });

        ImageButton helpButton = (ImageButton)findViewById(R.id.help_button);
        helpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplication(), HelpActivity.class);
                startActivity(intent);
            }
        });

        //タイマー終了
        stopButton = (Button)findViewById(R.id.stop_Button);
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(null != timer){
                    timer.cancel();
                    timer = null;
                    timerText.setText("00:00");
                }
            }
        });
    }

    //タイマーが動いている間の処理（TimerTaskを継承して新たにCountUpTimerTaskクラスを作成）
    class CountUpTimerTask extends TimerTask{
        @Override
        public void run(){
            //handlerを使って処理をキューイングする
            handler.post(new Runnable(){
                @Override
                public void run() {
                    long mm = count/60;
                    long ss = count%60;
                    timerText.setText(String.format("%1$02d:%2$02d", mm, ss));
                    if(count==0){ //0秒の時バイブしないよう
                    }else if(count%15==0 && scount==false) { //スライドが変わる5秒前
                        Vibrator vibrator = (Vibrator)getSystemService(VIBRATOR_SERVICE);
                        long[] pattern = {0, 300}; //OFF/ON/OFF/ON
                        vibrator.vibrate(pattern, -1);
                        scount=true;
                        stock = count;
                    }else if(count==stock+20){ //スライドが変わる5秒前
                        Vibrator vibrator = (Vibrator)getSystemService(VIBRATOR_SERVICE);
                        long[] pattern = {0, 300}; //OFF/ON/OFF/ON
                        vibrator.vibrate(pattern, -1);
                        stock = count;
                    }else if(count==400){
                        Vibrator vibrator = (Vibrator)getSystemService(VIBRATOR_SERVICE);
                        long[] pattern = {0, 300, 200, 300, 200, 300, 200, 300, 200, 400}; //OFF/ON/OFF/ON...
                        vibrator.vibrate(pattern, -1);

                        timer.cancel();
                        timer = null;
                        timerText.setText("00:00");
                    }else if(count%20==0){
                        Vibrator vibrator = (Vibrator)getSystemService(VIBRATOR_SERVICE);
                        long[] pattern = {0, 300, 200, 300}; //OFF/ON/OFF/ON
                        vibrator.vibrate(pattern, -1);
                    }
                    count++;
                }
            });
        }
    }
}