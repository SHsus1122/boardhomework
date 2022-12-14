package com.sparta.boardhomework.dto;

import com.sparta.boardhomework.entity.Board;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
public class BoardResponseDto {
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private Long id;
    private String boardName;
    private String username;
    private String contents;
    private List<CommentResponseDto> comments = new ArrayList<>();
    private Long likeCount;

    public BoardResponseDto(Board board) {
        this.id = board.getBoardId();
        this.createdAt = board.getCreatedAt();
        this.modifiedAt = board.getModifiedAt();
        this.boardName = board.getBoardName();
        this.username = board.getUsername();
        this.contents = board.getContents();
        this.likeCount = (long) board.getBoardLikes().size();
    }

    public BoardResponseDto(Board board, List<CommentResponseDto> comments) {
        this.id = board.getBoardId();
        this.createdAt = board.getCreatedAt();
        this.modifiedAt = board.getModifiedAt();
        this.boardName = board.getBoardName();
        this.username = board.getUsername();
        this.contents = board.getContents();
        this.comments = comments;
        this.likeCount = (long) board.getBoardLikes().size();
    }
}