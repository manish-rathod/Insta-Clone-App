package com.example.instaclone;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.example.instaclone.homepage.HomepageActivity;
import com.example.instaclone.login.LoginActivity;

public class MainActivity extends AppCompatActivity {

    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent;
        preferences = getSharedPreferences(Constants.PREFERENCE_NAME, MODE_PRIVATE);
        if(preferences.getString(Constants.PREFERENCE_USER,null)!= null){
             intent = new Intent(this, HomepageActivity.class);
        }else {
            intent = new Intent(this, LoginActivity.class);
        }

        startActivity(intent);
    }
}