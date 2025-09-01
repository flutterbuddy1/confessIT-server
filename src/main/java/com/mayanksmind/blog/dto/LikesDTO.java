package com.mayanksmind.blog.dto;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LikesDTO {

    private Long id;
    private Long user_id;
    private LocalDateTime createdAt = LocalDateTime.now();
}
