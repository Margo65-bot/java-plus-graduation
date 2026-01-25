package ru.practicum.model.mapper;

import ru.practicum.dto.comment.CommentDto;
import ru.practicum.dto.comment.CommentState;
import ru.practicum.dto.comment.NewCommentDto;
import ru.practicum.dto.comment.StateCommentDto;
import ru.practicum.model.Comment;

import java.time.LocalDateTime;

public class CommentMapper {
    public static Comment mapToComment(NewCommentDto commentDto, Long authorId, Long eventId, CommentState state) {
        return Comment.builder()
                .authorId(authorId)
                .eventId(eventId)
                .text(commentDto.text())
                .state(state)
                .created(LocalDateTime.now())
                .build();
    }

    public static CommentDto mapToCommentDto(Comment comment, String author) {
        return new CommentDto(comment.getId(), author, comment.getText());
    }

    public static StateCommentDto mapToAdminDto(Comment comment, String author) {
        return new StateCommentDto(comment.getId(), author, comment.getText(), comment.getState());
    }
}