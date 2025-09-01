package com.mayanksmind.blog.services;

import com.mayanksmind.blog.dto.ConfessionDTO;
import com.mayanksmind.blog.models.ApiResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ConfessionService {
    ApiResponse createConfession(ConfessionDTO confession);

    ApiResponse toggleConfessionLike(Long confessionId,Long userId);

    ApiResponse findConfessionById(Long id);

    ApiResponse updateConfession(ConfessionDTO confession);

    ApiResponse deleteConfession(Long id);

    ApiResponse findAllConfessions(int page, int size);

    ApiResponse findAllTrendingConfessions(int page, int size);

    ApiResponse findAllConfessionsByUserID(Long id,int page, int size);

    ApiResponse addCommentToConfession(Long confessionId,Long userId,String comment);

    ApiResponse deleteCommentFromConfession(Long userId);

    ApiResponse findAllLikedConfessions(Long userId, int page, int size);

    ApiResponse getStats(Long userId);

    ApiResponse searchConfession(String query, int page, int size);
}
