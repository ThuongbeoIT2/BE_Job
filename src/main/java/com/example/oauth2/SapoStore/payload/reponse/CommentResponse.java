package com.example.oauth2.SapoStore.payload.reponse;

import com.example.oauth2.SapoStore.model.Comment;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
@Getter
@Setter
public class CommentResponse {
    private long cmtId;
    private String description;
    private String avatar_user;
    private String email_user;
    private Date createdAt;
    private int evaluate;
    private String urlImage;
    private boolean isDisplay;
    public static CommentResponse cloneCommentResponse(Comment comment) {
        CommentResponse response = new CommentResponse();
        response.setCmtId(comment.getCmtId());
        response.setDescription(comment.getDescription());
        response.setAvatar_user(comment.getAvatar_user());
        response.setEmail_user(comment.getEmail_user());
        response.setCreatedAt(comment.getCreatedAt());
        response.setEvaluate(comment.getEvaluate());
        response.setUrlImage(comment.getUrlImage());
        response.setDisplay(comment.isDisplay());
        return response;
    }

}
