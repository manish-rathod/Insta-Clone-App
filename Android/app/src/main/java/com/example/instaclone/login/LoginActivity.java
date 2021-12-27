package com.example.instaclone.login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.instaclone.Authentication.MyApiCalls;
import com.example.instaclone.Constants;
import com.example.instaclone.Models.LoginRequestModel;
import com.example.instaclone.Models.LoginResponseModel;
import com.example.instaclone.Models.UserModel;
import com.example.instaclone.R;
import com.example.instaclone.example.RetrofitSingleton;
import com.example.instaclone.homepage.HomepageActivity;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity {

    SharedPreferences preferences;
    Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        preferences = getApplicationContext().getSharedPreferences(Constants.PREFERENCE_NAME, Context.MODE_PRIVATE);

        Button loginButton = findViewById(R.id.login);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login(getUserId(), getPassword());
            }
        });

        Button signUpButton = findViewById(R.id.signUp);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
                startActivity(intent);

            }
        });

    }

    private String getUserId(){
        EditText userId = findViewById(R.id.userId);
        return userId.getText().toString();
    }

    private String getPassword(){
        EditText password = findViewById(R.id.password);
        return password.getText().toString();
    }

    private void login(String userId, String password){
        LoginRequestModel reqBody = new LoginRequestModel();
        reqBody.setUserId(userId);
        reqBody.setPassword(password);


        Call<LoginResponseModel> call = RetrofitSingleton.getInstance().loginUser(reqBody);

        call.enqueue(new Callback<LoginResponseModel>() {
            @Override
            public void onResponse(Call<LoginResponseModel> call, Response<LoginResponseModel> response) {
                if(response.code() == 200){
                    Log.d("TAG", "onResponse: Login success"+ response.body().getToken());
                    Toast toast = Toast.makeText(getApplicationContext(), "Login Success", Toast.LENGTH_SHORT);
                    toast.show();
                    String token = "Bearer "+response.body().getToken();
                    preferences.edit().putString(Constants.PREFERENCE_TOKEN_DETAILS, token).apply();
                    UserModel user = new UserModel();
                    user.setUserId(response.body().getUserId());
                    user.setUserName(response.body().getUserName());
                    user.setId(response.body().get_id());
                    gson = new Gson();

                    preferences.edit().putString(Constants.PREFERENCE_USER, gson.toJson(user)).apply();
                    navigateToHome();
                }else{
                    Log.d("TAG", "onResponse: Login failed");
                }
            }

            @Override
            public void onFailure(Call<LoginResponseModel> call, Throwable t) {
                Log.d("TAG", "onFailure: connection error"+ t);
            }
        });
    }

    private void navigateToHome(){
        Intent intent = new Intent(getApplicationContext(), HomepageActivity.class);
        startActivity(intent);
    }
}