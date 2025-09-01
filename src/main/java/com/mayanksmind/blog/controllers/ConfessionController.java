package com.mayanksmind.blog.controllers;

import com.mayanksmind.blog.dto.CommentRequest;
import com.mayanksmind.blog.dto.ConfessionDTO;
import com.mayanksmind.blog.models.ApiResponse;
import com.mayanksmind.blog.models.Confession;
import com.mayanksmind.blog.services.impl.ConfessionServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/confession")
public class ConfessionController {

    @Autowired
    private ConfessionServiceImpl confessionService;


    @GetMapping
    public ApiResponse getConfessions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return confessionService.findAllConfessions(page, size);
    }

    @GetMapping("/search")
    public ApiResponse getConfessions(
            @RequestParam("query") String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return confessionService.searchConfession(query, page, size);
    }

    @GetMapping("/user/stats/{user_id}")
    public ApiResponse getConfessionStats(@PathVariable long user_id) {
        return confessionService.getStats(user_id);
    }

    @GetMapping("/trending")
    public ApiResponse getTrendingConfessions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return confessionService.findAllTrendingConfessions(page,size);
    }

    @PostMapping("/{confession_id}/like/{user_id}")
    public ApiResponse toggleConfessionLike(@PathVariable Long confession_id, @PathVariable long user_id) {
        return confessionService.toggleConfessionLike(confession_id,user_id);
    }

    @PostMapping("/{confession_id}/comment/{user_id}")
    public ApiResponse addCommentToConfession(@RequestBody CommentRequest comment, @PathVariable Long confession_id, @PathVariable Long user_id) {
        return confessionService.addCommentToConfession(confession_id,user_id,comment.getContent());
    }

    @GetMapping("/user/{user_id}")
    public ApiResponse getUserConfessions(@PathVariable Long user_id,
                                          @RequestParam(defaultValue = "0") int page,
                                          @RequestParam(defaultValue = "20") int size) {
        return confessionService.findAllConfessionsByUserID(user_id, page, size);
    }

    @GetMapping("/user/liked/{user_id}")
    public ApiResponse getUserLikedConfessions(@PathVariable Long user_id,
                                               @RequestParam(defaultValue = "0") int page,
                                               @RequestParam(defaultValue = "20") int size) {
        return confessionService.findAllLikedConfessions(user_id, page, size);
    }

    @PostMapping
    public ApiResponse addConfession(@RequestBody ConfessionDTO confession) {
        return confessionService.createConfession(confession);
    }

    @PutMapping
    public ApiResponse updateConfession(@RequestBody ConfessionDTO confession) {
        return confessionService.updateConfession(confession);
    }

    @DeleteMapping("/{confession_id}")
    public ApiResponse deleteConfession(@PathVariable Long confession_id) {
        return confessionService.deleteConfession(confession_id);
    }

    @DeleteMapping("/comment/{comment_id}")
    public ApiResponse deleteComment(@PathVariable Long comment_id) {
        return confessionService.deleteCommentFromConfession(comment_id);
    }


}
