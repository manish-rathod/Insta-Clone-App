package com.example.instaclone.Models;

import java.util.Set;

public class LikePostResponse {
    public String _id;
    public String postId;
    public String likes;
    public Set<String> likedByUsers;
    public Boolean likedByMe;
}
