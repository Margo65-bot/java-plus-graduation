package ru.practicum.dto.comment;

public record StateCommentDto(
        Long id,
        String author,
        String text,
        CommentState state
) {
}
