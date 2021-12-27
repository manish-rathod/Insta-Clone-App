package com.example.instaclone.login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.instaclone.Authentication.MyApiCalls;
import com.example.instaclone.Constants;
import com.example.instaclone.Models.LoginResponseModel;
import com.example.instaclone.Models.SignUpRequestModel;
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

public class SignUpActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        sharedPreferences = getSharedPreferences(Constants.PREFERENCE_NAME, MODE_PRIVATE);

        Button singUpButton = findViewById(R.id.signUpButton);
        singUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getPassword().equals("Invalid")){
                    TextView passError = findViewById(R.id.passError);
                    passError.setVisibility(View.VISIBLE);
                }else{
                    TextView passError = findViewById(R.id.passError);
                    passError.setVisibility(View.GONE);
                    signUp();

                }
            }
        });
    }

    private String getUserName(){
        EditText userName = findViewById(R.id.userName);
        return userName.getText().toString();
    }

    private String getUserId(){
        EditText userId = findViewById(R.id.userId);
        return userId.getText().toString();
    }

    private String getPassword(){
        EditText pass1 = findViewById(R.id.password1);
        EditText pass2 = findViewById(R.id.password2);

        if(pass1.getText().toString().equals(pass2.getText().toString())){
            return pass1.getText().toString();
        }else{
            return "Invalid";
        }
    }

    private void signUp(){
        SignUpRequestModel reqBody = new SignUpRequestModel();
        reqBody.setUserId(getUserId());
        reqBody.setUserName(getUserName());
        reqBody.setPassword(getPassword());

        Call<LoginResponseModel> call = RetrofitSingleton.getInstance().SignUpUser(reqBody);

        call.enqueue(new Callback<LoginResponseModel>() {
            @Override
            public void onResponse(Call<LoginResponseModel> call, Response<LoginResponseModel> response) {
                if(response.code() == 200){
                    Log.d("TAG", "onResponse: success ");
                    Toast toast = Toast.makeText(getApplicationContext(), "Account Created", Toast.LENGTH_SHORT);
                    toast.show();
                    String token = "Bearer "+response.body().getToken();
                    sharedPreferences.edit().putString(Constants.PREFERENCE_TOKEN_DETAILS, token).apply();
                    UserModel user = new UserModel();
                    user.setUserId(response.body().getUserId());
                    user.setUserName(response.body().getUserName());
                    user.setId(response.body().get_id());
                    gson = new Gson();

                    sharedPreferences.edit().putString(Constants.PREFERENCE_USER, gson.toJson(user)).apply();
                    navigateToHome();

                }else{
                    Log.d("TAG", "onResponse: Failed " + response.message());
                }
            }

            @Override
            public void onFailure(Call<LoginResponseModel> call, Throwable t) {
                Log.d("TAG", "onFailure: " + t);
            }
        });
    }

    private void navigateToHome(){
        Intent intent = new Intent(getApplicationContext(), HomepageActivity.class);
        startActivity(intent);
    }
}