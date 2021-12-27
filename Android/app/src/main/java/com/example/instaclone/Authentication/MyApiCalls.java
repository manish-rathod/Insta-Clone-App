package com.example.instaclone.Authentication;

import com.example.instaclone.Models.LikePostResponse;
import com.example.instaclone.Models.LoginRequestModel;
import com.example.instaclone.Models.LoginResponseModel;
import com.example.instaclone.Models.PostImageResponse;
import com.example.instaclone.Models.PostRequest;
import com.example.instaclone.Models.PostResponse;
import com.example.instaclone.Models.SignUpRequestModel;

import java.io.File;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface MyApiCalls {

    @Headers({"Content-Type: application/json", "Cache-Control: max-age=640000"})
    @POST("user/login")
    Call<LoginResponseModel> loginUser(@Body LoginRequestModel body);

    @Headers({"Content-Type: application/json", "Cache-Control: max-age=640000"})
    @POST("user")
    Call<LoginResponseModel> SignUpUser(@Body SignUpRequestModel body);


    @GET("post")
    Call<PostResponse[]> getPosts(@Header("Authorization") String authorization);

    @POST("like")
    Call<LikePostResponse> addLike(@Header("Authorization") String authorization, @Query("postId") String postId);

    @GET("image")
    Call<PostImageResponse> getPostImage(@Header("Authorization") String authorization,@Query("imageId") String imageId);

    @Multipart
    @POST("post")
    Call<Boolean> createPost(@Header("Authorization") String authorization, @Part("body") PostRequest body, @Part MultipartBody.Part myImage);
}
