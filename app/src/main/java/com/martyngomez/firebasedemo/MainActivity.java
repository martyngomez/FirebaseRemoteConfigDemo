package com.martyngomez.firebasedemo;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

public class MainActivity extends AppCompatActivity {

    private FirebaseRemoteConfig remoteConfig;

    private LinearLayout linearLayout;
    private ImageView imvLogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        linearLayout = (LinearLayout) findViewById(R.id.activity_main);
        imvLogo = (ImageView) findViewById(R.id.imvLogo);

        remoteConfig = FirebaseRemoteConfig.getInstance(); // Inicializa

        FirebaseRemoteConfigSettings remoteConfigSettings
                = new FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(true) //Proyecto de debug
                .build();

        //Lee las keys por default
        remoteConfig.setConfigSettings(remoteConfigSettings);
        remoteConfig.setDefaults(R.xml.remote_config_defaults);

        setConfigurationView();

    }

    //Metodo que asigna valores
    private void setConfigurationView(){
        linearLayout.setBackgroundColor(Color.parseColor(remoteConfig.getString("color_background"))); //Busca el valor y lo establece
        Log.w("NAME", "name: " + remoteConfig.getString("image_background"));

        int imageResource= getResources().getIdentifier(remoteConfig.getString("image_background"),  "drawable", getPackageName());
        imvLogo.setImageResource(imageResource);

        /*if (remoteConfig.getString("image_background").equals("happyface")){ //Compara el valor de Firebase con el actual establecido y establece valor segun haga falta
            imvLogo.setImageResource(R.drawable.happyface);
        }else if(remoteConfig.getString("image_background").equals("pikachuchistmas")){
            imvLogo.setImageResource(R.drawable.pikachuchistmas);
        }else if(remoteConfig.getString("image_background").equals("valentinesday")){
            imvLogo.setImageResource(R.drawable.valentinesday);
        }*/
    }

    //Metodo que sincroniza con Firebase
    public void syncroinizeData(View view){
        long cacheExpiration = 3600; // Duracion del cache, milisegundos.

        if (remoteConfig.getInfo().getConfigSettings().isDeveloperModeEnabled()){ // Sin cache en modo desarrollo
            cacheExpiration = 0;
        }

        // Va a Firebase y trae los datos de las llaves configuradas
        remoteConfig.fetch(cacheExpiration).addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(MainActivity.this, "Syncronize Done", Toast.LENGTH_SHORT).show();
                    remoteConfig.activateFetched();
                }else{
                    Toast.makeText(MainActivity.this, "Syncronize Fail", Toast.LENGTH_SHORT).show();
                }

                setConfigurationView();
            }
        });

    }
}
