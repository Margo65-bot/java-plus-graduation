package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.api.comment.PrivateCommentApi;
import ru.practicum.dto.comment.CommentDto;
import ru.practicum.dto.comment.NewCommentDto;
import ru.practicum.dto.comment.UpdateCommentDto;
import ru.practicum.service.CommentService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class PrivateCommentController implements PrivateCommentApi {
    private final CommentService commentService;

    @Override
    public List<CommentDto> getComments(long userId) {
        log.info("Private: Method launched (getComments({}))", userId);
        return commentService.getComments(userId);
    }

    @Override
    public CommentDto createComment(long userId, NewCommentDto commentDto) {
        log.info("Private: Method launched (createComment({}, {}))", userId, commentDto);
        return commentService.createComment(userId, commentDto);
    }

    @Override
    public void deleteComment(long userId, long comId) {
        log.info("Private: Method launched (deleteComment({}, {}))", userId, comId);
        commentService.deleteComment(userId, comId);
    }

    @Override
    public CommentDto updateComment(long userId, UpdateCommentDto commentDto) {
        log.info("Private: Method launched (updateComment({}, {}))", userId, commentDto);
        return commentService.updateComment(userId, commentDto);
    }
}