package BlogSystem.BlogSystem.dto.requests;

import lombok.Data;

@Data
public class UpdatePostRequest {

    private Long userId;

    private Long commentId;

    private Long categoryId;

    private Long postId;

    private String title;

    private String content;

    private Long viewCount;

    private Boolean isPublished;
}
