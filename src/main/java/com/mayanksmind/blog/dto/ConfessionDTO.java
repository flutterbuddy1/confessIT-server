package com.mayanksmind.blog.dto;

import com.mayanksmind.blog.enums.Sentiment;
import com.mayanksmind.blog.models.User;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class ConfessionDTO {

    private Long id;
    private String content;
    private String masked_content;
    private Sentiment sentiment;
    private LocalDateTime created_at;
    private LocalDateTime updated_at;
    private Long user_id;
    private List<LikesDTO> likes = new ArrayList<>();
    private List<CommentDTO> comments = new ArrayList<>();

}
