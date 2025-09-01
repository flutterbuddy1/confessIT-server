package com.mayanksmind.blog.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
public class LikesDTO {

    private Long id;
    private Long user_id;
    private LocalDateTime createdAt = LocalDateTime.now();
}
