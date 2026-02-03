package ru.practicum.service;

import ru.practicum.dto.comment.CommentDto;
import ru.practicum.dto.comment.DateSort;
import ru.practicum.dto.comment.NewCommentDto;
import ru.practicum.dto.comment.StateCommentDto;
import ru.practicum.dto.comment.UpdateCommentDto;

import java.util.List;

public interface CommentService {
    CommentDto createComment(long userId, NewCommentDto commentDto);

    List<CommentDto> getComments(long userId);

    CommentDto updateComment(long userId, UpdateCommentDto commentDto);

    void deleteComment(long userId, long comId);

    List<StateCommentDto> getComments(String text, DateSort sort);

    StateCommentDto reviewComment(long comId, boolean approved);

    void deleteComment(long comId);
}