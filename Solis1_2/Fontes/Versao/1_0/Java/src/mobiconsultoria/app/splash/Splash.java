package mobiconsultoria.app.splash;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class Splash extends Activity implements Runnable {
    
    // 5 segundos será o tempo que a Splash ficará sendo exibida
    private final int splashTime = 5000;   
     
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
         
        Handler h = new Handler();
        h.postDelayed(this, splashTime);
    }
     
    public void run(){     
        // Sua próxima Activity é iniciada
        startActivity(new Intent(".MainActivity"));
         
        // A Splash é finalizada
        finish();
    }  
}