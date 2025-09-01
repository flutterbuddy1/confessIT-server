package com.mayanksmind.blog.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GeminiResponseDto {
    private String sentiment;
    private String cleanedText;
}
