package com.mayanksmind.blog.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
public class CommentDTO {
    private Long id;
    private Long user_id;
    private String content;
    private LocalDateTime createdAt = LocalDateTime.now();
}
