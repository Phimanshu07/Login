package com.internshala.loginform;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import static java.lang.Thread.sleep;

public class SplashActivity extends AppCompatActivity {

    private ImageView splashscreen;
    private int SLEEP_TIMER=3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );

        requestWindowFeature( Window.FEATURE_NO_TITLE );
        getWindow().setFlags( WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN );
        setContentView( R.layout.activity_splash );

        getSupportActionBar().hide();



        splashscreen=findViewById( R.id.SplashScreen );

        LogoLauncher logoLauncher =new LogoLauncher();
        logoLauncher.start();
    }

    private  class LogoLauncher extends Thread{
        public void run(){
            try {
                sleep( 1000*SLEEP_TIMER );
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            startActivity( new Intent( SplashActivity.this,MainActivity.class ) );
            SplashActivity.this.finish();

        }
    }
}