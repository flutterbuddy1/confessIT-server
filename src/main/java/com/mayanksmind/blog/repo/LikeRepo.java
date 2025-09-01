package com.mayanksmind.blog.repo;

import com.mayanksmind.blog.models.Likes;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikeRepo extends JpaRepository<Likes,Long> {
    Optional<Likes> findByUserIdAndConfessionId(Long userId, Long confessionId);
    int countByConfessionUserId(Long userId);
}
