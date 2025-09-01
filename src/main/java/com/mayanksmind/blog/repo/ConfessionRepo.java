package com.mayanksmind.blog.repo;

import com.mayanksmind.blog.models.Confession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ConfessionRepo extends JpaRepository<Confession,Long> {
    Page<Confession> findByUserId(Long userId, Pageable pageable);

    int countByUserId(Long userId);

    Page<Confession> findAllByMaskedContentContainingIgnoreCase(String title,Pageable pageable);

    Page<Confession> findAllByLikesUserId(Long user_id, Pageable pageable);

    @Query("""
    SELECT c FROM Confession c 
    LEFT JOIN c.likes l 
    LEFT JOIN c.comments cm 
    WHERE c.created_at >= :fromDate
    GROUP BY c
    ORDER BY (COUNT(l) + COUNT(cm)) DESC
""")
    Page<Confession> findTrendingConfessions(@Param("fromDate") LocalDateTime fromDate, Pageable pageable);


}
