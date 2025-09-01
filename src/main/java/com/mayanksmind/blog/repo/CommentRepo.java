package com.mayanksmind.blog.repo;

import com.mayanksmind.blog.models.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommentRepo extends JpaRepository<Comment,Long> {
    Optional<Comment> findByUserIdAndConfessionId(Long userId, Long confessionId);

}
