package github.yagocranchi.linkshortener.controller.dto;

import java.util.UUID;

public class AdminLinkResponseDto {
    private Long id;
    private String content;
    private UUID userId;

    public AdminLinkResponseDto(Long id, String content, UUID userId) {
        this.id = id;
        this.content = content;
        this.userId = userId;
    }

    public Long getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public UUID getUserId() {
        return userId;
    }
}
