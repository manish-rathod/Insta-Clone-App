package com.example.instaclone.homepage;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.instaclone.Authentication.MyApiCalls;
import com.example.instaclone.Constants;
import com.example.instaclone.Models.PostResponse;
import com.example.instaclone.R;
import com.example.instaclone.createPost.CreatePost;
import com.example.instaclone.example.RetrofitSingleton;
import com.example.instaclone.login.LoginActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomepageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomepageFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomepageFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     @return A new instance of fragment HomepageFragment.
     */
    // TODO: Rename and change types and number of parameters

    RecyclerView.LayoutManager layoutManager;
    RecyclerView recyclerView;
    HomepageAdapter adapter;
    SharedPreferences preferences;
    SwipeRefreshLayout swipeRefreshLayout;
    String token;

    public static HomepageFragment newInstance() {
        HomepageFragment fragment = new HomepageFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_homepage, container, false);
        // Inflate the layout for this fragment
        preferences = getContext().getSharedPreferences(Constants.PREFERENCE_NAME, Context.MODE_PRIVATE);
        token = preferences.getString(Constants.PREFERENCE_TOKEN_DETAILS, null);
        recyclerView = view.findViewById(R.id.postsList);
        swipeRefreshLayout = view.findViewById(R.id.refreshLayout);
        swipeRefreshLayout.setRefreshing(true);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new HomepageAdapter(getContext(),token);
        recyclerView.setAdapter(adapter);
        swipeRefreshLayout.setRefreshing(true);
        getPosts();

        Button addPostButton = view.findViewById(R.id.addPostButton);
        addPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), CreatePost.class);
                startActivity(intent);
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getPosts();
            }
        });

        return view;
    }

    private void getPosts(){

        if(token != null){
            Log.d("TAG", "getPosts: token  + " +token);
            Call<PostResponse[]> call = RetrofitSingleton.getInstance().getPosts(token);

            call.enqueue(new Callback<PostResponse[]>() {
                @Override
                public void onResponse(Call<PostResponse[]> call, Response<PostResponse[]> response) {
                    if(response.code() == 200){
                        Log.d("TAG", "onResponse: get posts success " +response.body().length);
                        adapter.setData(response.body());
                        adapter.notifyDataSetChanged();
                    }else{
                        Log.d("TAG", "onResponse: get posts failed "+response.code());
                        if(response.code() == 401){
                            preferences.edit().putString(Constants.PREFERENCE_TOKEN_DETAILS, null);
                            Intent intent = new Intent(getContext(), LoginActivity.class);
                            startActivity(intent);
                        }
                    }
                }

                @Override
                public void onFailure(Call<PostResponse[]> call, Throwable t) {
                    Log.d("TAG", "onFailure: "+t);
                }
            });

            swipeRefreshLayout.setRefreshing(false);
        }

    }
}