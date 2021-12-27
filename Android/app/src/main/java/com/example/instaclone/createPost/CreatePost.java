package com.example.instaclone.createPost;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.instaclone.Constants;
import com.example.instaclone.Models.PostRequest;
import com.example.instaclone.Models.PostResponse;
import com.example.instaclone.R;
import com.example.instaclone.example.RetrofitSingleton;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreatePost extends AppCompatActivity {
    String[] allAccess = { "public", "private"};
    String access = "";
    int RESULT_LOAD_KEY = 10;
    ImageView postImage ;
    String picturePath = null;
    SharedPreferences preferences;
    String token;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);
        preferences = getApplicationContext().getSharedPreferences(Constants.PREFERENCE_NAME, Context.MODE_PRIVATE);
        token = preferences.getString(Constants.PREFERENCE_TOKEN_DETAILS, null);
        EditText postContent = findViewById(R.id.newPostContent);
        Button imageButton = findViewById(R.id.addImageButton);
        Button createPost = findViewById(R.id.createPost);
        postImage= findViewById(R.id.newPostImage);


        Spinner accessSpinner = (Spinner) findViewById(R.id.accessSpinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, allAccess);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        accessSpinner.setAdapter(adapter);

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, RESULT_LOAD_KEY);
            }
        });

        createPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("TAG", "onClick: access "+accessSpinner.getSelectedItem().toString());
                Log.d("TAG", "onClick: post Content " + postContent.getText().toString());
                Log.d("TAG", "onClick: post image "+ picturePath);
                createPost(accessSpinner.getSelectedItem().toString(), postContent.getText().toString(), picturePath);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("TAG", "onActivityResult: "+resultCode);
        if(requestCode == RESULT_LOAD_KEY && resultCode==RESULT_OK && data != null){
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };
            Cursor cursor = getContentResolver().query(selectedImage,filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            picturePath = cursor.getString(columnIndex);
            cursor.close();
            Log.d("TAG", "onActivityResult: imagePath"+picturePath);

            postImage.setImageBitmap(BitmapFactory.decodeFile(picturePath));
            postImage.setVisibility(View.VISIBLE);
        }
    }

    private Boolean createPost(String access, String content, String imagePath){
        String temp = content.trim();
        Log.d("TAG", "createPost: temp"+temp.isEmpty());
        if(temp.isEmpty()){
            return false;
        }else{
            PostRequest postRequest = new PostRequest();
            postRequest.setContent(temp);
            postRequest.setAccess(access);

            Call<Boolean> call;
            if(imagePath != null){
                File file = new File(imagePath);
                RequestBody requestFile =
                        RequestBody.create(MediaType.parse("multipart/form-data"), file);

// MultipartBody.Part is used to send also the actual file name
                MultipartBody.Part myImage =
                        MultipartBody.Part.createFormData("myImage", file.getName(), requestFile);
                call = RetrofitSingleton.getInstance().createPost(token,postRequest, myImage);
            }else{
                call = RetrofitSingleton.getInstance().createPost(token,postRequest, null);

            }
            call.enqueue(new Callback<Boolean>() {
                @Override
                public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                    Log.d("TAG", "onResponse: response code "+response.code());
                    if(response.code() == 200){
                        Toast toast = Toast.makeText(getApplicationContext(), "Post created", Toast.LENGTH_SHORT);
                        toast.show();
                        finish();
                    }else{
                        Log.d("TAG", "onResponse: "+response.body());
                    }
                }

                @Override
                public void onFailure(Call<Boolean> call, Throwable t) {
                    Log.d("TAG", "onFailure: failed "+t);
                }
            });


        }
        return true;

    }
}
