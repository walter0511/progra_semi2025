package com.example.miprimeraaplicacion;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {
    private static int DURACION_SPLASH = 3000; // 3 segundos

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash); // Tu layout XML

        ImageView logo = findViewById(R.id.logo);
        TextView texto = findViewById(R.id.loadingText);

        Animation animacion = AnimationUtils.loadAnimation(this, R.anim.splash_anim);
        logo.startAnimation(animacion);
        texto.startAnimation(animacion);

        new Handler().postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this, SeleccionLoginActivity.class);
            startActivity(intent);
            finish();
        }, DURACION_SPLASH);
    }
}
