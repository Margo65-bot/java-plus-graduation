package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.api.comment.AdminCommentApi;
import ru.practicum.dto.comment.DateSort;
import ru.practicum.dto.comment.StateCommentDto;
import ru.practicum.service.CommentService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class AdminCommentController implements AdminCommentApi {
    private final CommentService commentService;

    @Override
    public List<StateCommentDto> getComments(String text, DateSort sort) {
        log.info("Admin: Method launched (getComments(text = {}, sort = {}))", text, sort);
        return commentService.getComments(text, sort);
    }

    @Override
    public void deleteComment(Long comId) {
        log.info("Admin: Method launched (deleteComment(comId = {}))", comId);
        commentService.deleteComment(comId);
    }

    @Override
    public StateCommentDto reviewComment(long comId, boolean approved) {
        log.info("Admin: Method launched (reviewComment(comId = {}, approved = {}))", comId, approved);
        return commentService.reviewComment(comId, approved);
    }
}
