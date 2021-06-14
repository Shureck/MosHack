package com.shureck.moshack;

import java.util.List;

public class BigJson {
    int memberId;
    String name;
    String avatarUrl;
    String text;
    int coutWishers;
    int get_like;
    String memberName;
    String password;
    Role role;
    List<Subscriptions> subscriptions;
    List<Posts> posts;
}

class Role{
    int roleId;
    String name;
}

class Subscriptions{
    int id;
    String sphere;
    int memberId;
}

class Posts{
    int postId;
    String title;
    String text;
    String jpgUrl;
    List<IdItemForPosts> idItemForPosts;
    List<Coments> coments;
    int get_likes;
    String name;
    String date;
}

class IdItemForPosts{
    int id;
    int itemId;
}

class Coments{
    int id;
    String message;
    String userName;
}