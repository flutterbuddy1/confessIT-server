package com.mayanksmind.blog.services.impl;

import com.mayanksmind.blog.dto.CommentDTO;
import com.mayanksmind.blog.dto.ConfessionDTO;
import com.mayanksmind.blog.dto.GeminiResponseDto;
import com.mayanksmind.blog.dto.LikesDTO;
import com.mayanksmind.blog.enums.Sentiment;
import com.mayanksmind.blog.models.*;
import com.mayanksmind.blog.repo.CommentRepo;
import com.mayanksmind.blog.repo.ConfessionRepo;
import com.mayanksmind.blog.repo.LikeRepo;
import com.mayanksmind.blog.repo.UserRepo;
import com.mayanksmind.blog.services.ConfessionService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ConfessionServiceImpl implements ConfessionService {

    @Autowired
    ConfessionRepo repo;

    @Autowired
    UserRepo userRepo;

    @Autowired
    LikeRepo likeRepo;


    @Autowired
    CommentRepo commentRepo;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    GeminiRestService geminiRestService;

    @Override
    public ApiResponse createConfession(ConfessionDTO confession) {
        Optional<User> user = userRepo.findById(confession.getUser_id());
        if (user.isPresent()) {
            Confession confess = modelMapper.map(confession, Confession.class);
            GeminiResponseDto geminiResponse = geminiRestService.analyzeConfession(confess.getContent());
            confess.setUser(user.get());
            confess.setMaskedContent(geminiResponse.getCleanedText());
            confess.setSentiment(
                    geminiResponse.getSentiment().equals("Positive") ?
                            Sentiment.positive :
                            geminiResponse.getSentiment().equals("Negative")
                                    ? Sentiment.negative :
                                    Sentiment.neutral);
            return new ApiResponse(true, repo.save(confess));
        }else{
            return new ApiResponse(
                    false,
                    "User not found"
            );
        }
    }

    @Override
    public ApiResponse toggleConfessionLike(Long confessionId, Long userId) {
        Optional<Likes> alreadyLikes = likeRepo.findByUserIdAndConfessionId(userId, confessionId);
        if (alreadyLikes.isPresent()) {
            likeRepo.delete(alreadyLikes.get());
            return new ApiResponse(true, "Already liked (so removed)");
        }

        Optional<User> user = userRepo.findById(userId);
        Optional<Confession> confession = repo.findById(confessionId);

        if (user.isPresent() && confession.isPresent()) {
            Likes likes = new Likes();
            likes.setUser(user.get());
            likes.setConfession(confession.get());

            Likes saved = likeRepo.save(likes);

            // 🔑 Convert to DTO instead of returning entity
            LikesDTO likesDTO = new LikesDTO();
            likesDTO.setId(saved.getId());
            likesDTO.setUser_id(saved.getUser().getId());

            return new ApiResponse(true, likesDTO);
        } else {
            return new ApiResponse(false, "User or Confession not found");
        }
    }

    @Override
    public ApiResponse findConfessionById(Long id) {
        Optional<Confession> confess = repo.findById(id);
        return confess.map(confession -> new ApiResponse(true, confession)).orElse(new ApiResponse(false,"Confession not found"));
    }

    @Override
    public ApiResponse updateConfession(ConfessionDTO confession) {
        Optional<Confession> confess = repo.findById(confession.getId());
        if (confess.isPresent()) {
            Confession confess1 = modelMapper.map(confession, Confession.class);
            confess1.setId(confession.getId());
            return new ApiResponse(true, repo.save(confess1));
        }
        return new ApiResponse(false,"Confession not found");
    }

    @Override
    public ApiResponse deleteConfession(Long id) {
        repo.deleteById(id);
        return new ApiResponse(true,"Confession deleted");
    }
    @Override
    public ApiResponse findAllConfessions(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        Page<Confession> confessionPage = repo.findAll(pageable);

        return getApiResponse(confessionPage);
    }

    @Override
    public ApiResponse findAllTrendingConfessions(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        LocalDateTime last7Days = LocalDateTime.now().minusDays(7);
        Page<Confession> confessionPage = repo.findTrendingConfessions(last7Days, pageable);

        return getApiResponse(confessionPage);
    }

    private ApiResponse getApiResponse(Page<Confession> confessionPage) {
        List<ConfessionDTO> confessionDTOList = confessionPage.getContent()
                .stream()
                .map(confession -> {
                    ConfessionDTO dto = new ConfessionDTO();
                    dto.setId(confession.getId());
                    dto.setContent(confession.getContent());
                    dto.setMasked_content(confession.getMaskedContent());
                    dto.setSentiment(confession.getSentiment());
                    dto.setCreated_at(confession.getCreated_at());
                    dto.setUpdated_at(confession.getUpdated_at());
                    dto.setUser_id(confession.getUser().getId());
                    // Explicit mapping (likes & comments)
                    dto.setLikes(
                            confession.getLikes().stream()
                                    .map(like -> {
                                        LikesDTO likesDTO = new LikesDTO();
                                        likesDTO.setId(like.getId());
                                        likesDTO.setUser_id(like.getUser().getId());
                                        return likesDTO;
                                    })
                                    .toList()
                    );

                    dto.setComments(
                            confession.getComments().stream()
                                    .map(comment -> {
                                        CommentDTO commentDTO = new CommentDTO();
                                        commentDTO.setId(comment.getId());
                                        commentDTO.setContent(comment.getContent());
                                        commentDTO.setUser_id(comment.getUser().getId());
                                        commentDTO.setCreatedAt(comment.getCreatedAt());
                                        return commentDTO;
                                    })
                                    .toList()
                    );

                    return dto;
                })
                .toList();

        return new ApiResponse(
                true,
                Map.of(
                        "confessions", confessionDTOList,
                        "currentPage", confessionPage.getNumber(),
                        "totalPages", confessionPage.getTotalPages(),
                        "totalElements", confessionPage.getTotalElements(),
                        "pageSize", confessionPage.getSize(),
                        "isLastPage", confessionPage.isLast()
                )
        );
    }

    @Override
    public ApiResponse findAllConfessionsByUserID(Long user_id,int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        Page<Confession> confessionPage = repo.findByUserId(user_id, pageable);
        return getApiResponse(confessionPage);
    }

    @Override
    public ApiResponse addCommentToConfession(Long confessionId, Long userId, String comment) {
        Optional<User> user = userRepo.findById(userId);
        Optional<Confession> confession = repo.findById(confessionId);

        if (user.isPresent() && confession.isPresent()) {
            Comment commentData = new Comment();
            commentData.setConfession(confession.get());
            commentData.setContent(comment);
            commentData.setUser(user.get());

            Comment saved = commentRepo.save(commentData);
            // 🔑 Convert to DTO instead of returning entity
            CommentDTO commentDTO = new CommentDTO();
            commentDTO.setId(saved.getId());
            commentDTO.setContent(saved.getContent());
            commentDTO.setUser_id(saved.getUser().getId());

            return new ApiResponse(true, commentDTO);
        } else {
            return new ApiResponse(false, "User or Confession not found");
        }
    }

    @Override
    public ApiResponse deleteCommentFromConfession(Long commentId) {
        commentRepo.deleteById(commentId);
        return new ApiResponse(true,"Comment Deleted!");
    }

    @Override
    public ApiResponse findAllLikedConfessions(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        Page<Confession> confessionPage = repo.findAllByLikesUserId(userId, pageable);
        return getApiResponse(confessionPage);
    }

    @Override
    public ApiResponse getStats(Long userId) {
        int confessionCount = repo.countByUserId(userId);
        int likesCount = likeRepo.countByConfessionUserId(userId);
        return new ApiResponse(true,Map.of(
                "confessionCount",confessionCount,
                "likeCount",likesCount
        ));
    }

    @Override
    public ApiResponse searchConfession(String query, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        Page<Confession> confessionPage = repo.findAllByMaskedContentContainingIgnoreCase(query, pageable);
        return getApiResponse(confessionPage);
    }
}
