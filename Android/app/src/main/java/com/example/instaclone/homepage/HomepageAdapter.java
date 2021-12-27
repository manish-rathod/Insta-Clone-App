package com.example.instaclone.homepage;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.instaclone.Authentication.MyApiCalls;
import com.example.instaclone.Constants;
import com.example.instaclone.Models.LikePostResponse;
import com.example.instaclone.Models.PostImageResponse;
import com.example.instaclone.Models.PostResponse;
import com.example.instaclone.R;
import com.example.instaclone.example.RetrofitSingleton;
import com.google.android.material.button.MaterialButton;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HomepageAdapter extends RecyclerView.Adapter<HomepageAdapter.ViewHolder> {

    PostResponse postResponse[] = {};

    Date date;
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    SimpleDateFormat formatter = new SimpleDateFormat("E dd-MMM-yy HH:mm");
    Context context;
    String token;
    String root = Environment.getExternalStorageDirectory().toString();
    public HomepageAdapter(Context context, String token){
        this.context =context;
        this.token = token;
    }


    public void setData(PostResponse[] postResponses){
        this.postResponse = postResponses;
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.posts,parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        viewHolder.userName = view.findViewById(R.id.userName);
        viewHolder.content = view.findViewById(R.id.content);
        viewHolder.likes = view.findViewById(R.id.likes);
        viewHolder.comments = view.findViewById(R.id.comments);
        viewHolder.createdAt = view.findViewById(R.id.createdAt);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PostResponse response = postResponse[position];
        holder.userName.setText(response.userName);
        holder.content.setText(response.content);
        holder.likes.setText(response.likes);
        if(response.likedByMe){
            holder.likes.setIconTint(ColorStateList.valueOf(context.getResources().getColor(R.color.design_default_color_primary)));
        }else{
            holder.likes.setIconTint(ColorStateList.valueOf(context.getResources().getColor(R.color.grey)));
        }

        Log.d("TAG", "onBindViewHolder: createdAt "+response.createdAt);
        try {
            date = format.parse(response.createdAt);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        holder.createdAt.setText(formatter.format(date));

        if(response.imageId != null){
            Bitmap decodedByte;
            File file = new File(root+Constants.ROOT_DIR+"/"+ response.imageId+".jpg");
            if(file.exists()){
                Log.d("TAG", "onBindViewHolder: image in storage");
                decodedByte = BitmapFactory.decodeFile(file.getAbsolutePath());
                holder.postImage.setVisibility(View.VISIBLE);
                Glide.with(context)
                        .load(decodedByte)
                        .into(holder.postImage);

            }else{
                holder.postImage.setVisibility(View.GONE);
                getPostImage(response, holder.getAdapterPosition());


//            holder.postImage.setImageBitmap(decodedByte);

            }

//            Log.d("TAG", "onBindViewHolder: imagestr"+response.imageStr);


        }else{
            holder.postImage.setVisibility(View.GONE);
        }



        holder.likes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                likePost(postResponse[holder.getAdapterPosition()],holder.getAdapterPosition());
            }
        });

    }

    @Override
    public int getItemCount() {
        return postResponse.length;
    }


    public static class ViewHolder extends  RecyclerView.ViewHolder{
        TextView userName, content,createdAt;
        MaterialButton likes, comments;
        ImageView postImage;
        public ViewHolder(View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.userName);
            content = itemView.findViewById(R.id.content);
            likes = itemView.findViewById(R.id.likes);
            comments = itemView.findViewById(R.id.comments);
            createdAt = itemView.findViewById(R.id.createdAt);
            postImage = itemView.findViewById(R.id.postImage);
            return;
        }

        public void setData(PostResponse response){

        }
    }

    public void getPostImage(PostResponse postResponse, int pos){
        Call<PostImageResponse> call = RetrofitSingleton.getInstance().getPostImage(token,postResponse.imageId);
        call.enqueue(new Callback<PostImageResponse>() {
            @Override
            public void onResponse(Call<PostImageResponse> call, Response<PostImageResponse> response) {
                if(response.code() == 200){
                    postResponse.imageStr = response.body().imageString;
                    Log.d("TAG", "onResponse: found image");
                    saveImage(postResponse.imageId, postResponse.imageStr);
                    notifyItemChanged(pos);
                }
            }

            @Override
            public void onFailure(Call<PostImageResponse> call, Throwable t) {
                Log.e("TAG", "onFailure: ", t);

            }
        });
    }


    public void likePost(PostResponse postResponse,int pos){

        Call<LikePostResponse> call = RetrofitSingleton.getInstance().addLike(token,postResponse._id);

        call.enqueue(new Callback<LikePostResponse>() {
            @Override
            public void onResponse(Call<LikePostResponse> call, Response<LikePostResponse> response) {
                if(response.code() ==200){
                    postResponse.likes = response.body().likes;
                    if(response.body().likedByMe){
                        System.out.println(".onResponse liked by me");
                        postResponse.likedByMe = true;
                    }else {
                        System.out.println(".onResponse not liked by me");

                        postResponse.likedByMe = false;
                    }
                    notifyItemChanged(pos);

                }

            }

            @Override
            public void onFailure(Call<LikePostResponse> call, Throwable t) {

            }
        });

    }

    private void saveImage(String imageId, String imageStr){


        File myDir = new File(root + Constants.ROOT_DIR);
        Log.d("TAG", "onBindViewHolder: myDir1 "+myDir.getAbsolutePath());
        if(!myDir.exists()){
            Log.d("TAG", "onBindViewHolder: myDir2 "+myDir.getAbsolutePath());
            Boolean exist = myDir.mkdirs();
            Log.d("TAG", "onBindViewHolder: myDir3 "+myDir.getAbsolutePath());
            Log.d("TAG", "onBindViewHolder: myDir exists "+exist);
        }

        File file = new File(myDir,imageId+".jpg");
        if(!file.exists()){
            try {
                String[] arrOfStr = imageStr.split(",", 2);
                byte[] decodedString = Base64.decode(arrOfStr[1], Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                FileOutputStream out = new FileOutputStream(file);
                decodedByte.compress(Bitmap.CompressFormat.JPEG, 100, out);
                out.flush();
                out.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}


