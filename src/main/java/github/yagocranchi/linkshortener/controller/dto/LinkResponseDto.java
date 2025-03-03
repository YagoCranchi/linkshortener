package github.yagocranchi.linkshortener.controller.dto;

public class LinkResponseDto {
    private Long id;
    private String content;

    public LinkResponseDto(Long id, String content) {
        this.id = id;
        this.content = content;
    }

    public Long getId() {
        return id;
    }

    public String getContent() {
        return content;
    }
}
