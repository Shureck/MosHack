package com.shureck.moshack;

import java.util.Date;
import java.util.List;

public class HardPostModel {
    Integer postId;
    String name;
    String title;
    String text;
    String jpgUrl;
    List<Preview> previews;
    List<CommentDto> commentDtos;
    Integer like;
    Date date;
}

class CommentDto {
    String message;
    String name;
}