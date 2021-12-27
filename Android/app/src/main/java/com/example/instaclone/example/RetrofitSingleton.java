package com.example.instaclone.example;

import android.content.Context;

import com.example.instaclone.Authentication.MyApiCalls;
import com.example.instaclone.Constants;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttp;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitSingleton {

    private static Retrofit retrofit;
    private static MyApiCalls myApiCalls;
    private RetrofitSingleton(){

    }
    private Context context;
    private RetrofitSingleton(Context context){
        this.context = context;

    }

    public static MyApiCalls getInstance(){
        if(retrofit == null){
            final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(3,TimeUnit.MINUTES)
                    .readTimeout(3, TimeUnit.MINUTES)
                    .writeTimeout(3, TimeUnit.MINUTES)
                    .callTimeout(3, TimeUnit.MINUTES)
                    .build();
            retrofit = new Retrofit.Builder().baseUrl(Constants.BASE_URL).client(okHttpClient).addConverterFactory(GsonConverterFactory.create()).build();
            myApiCalls = retrofit.create(MyApiCalls.class);
        }
        return myApiCalls;
    }

//    public static MyApiCalls getInstance(Context context){
//        if(retrofit == null){
//            retrofit = new Retrofit.Builder().baseUrl(Constants.BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
//            myApiCalls = retrofit.create(MyApiCalls.class);
//        }
//        return myApiCalls;
//    }

//    public void setContext(Context context){
//        this.context = context;
//    }
}
