package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.comment.CommentDto;
import ru.practicum.dto.comment.CommentState;
import ru.practicum.dto.comment.DateSort;
import ru.practicum.dto.comment.NewCommentDto;
import ru.practicum.dto.comment.StateCommentDto;
import ru.practicum.dto.comment.UpdateCommentDto;
import ru.practicum.dto.user.UserShortDto;
import ru.practicum.exception.AccessDeniedException;
import ru.practicum.exception.CommentStateException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.feign.client.EventClient;
import ru.practicum.feign.client.UserClient;
import ru.practicum.model.Comment;
import ru.practicum.model.mapper.CommentMapper;
import ru.practicum.repository.CommentRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final UserClient userClient;
    private final EventClient eventClient;

    @Override
    public List<CommentDto> getComments(long userId) {
        UserShortDto user = userClient.findById(userId);
        List<Comment> comments = commentRepository.findAllByAuthorId(userId);

        return comments.stream()
                .map(comment -> CommentMapper.mapToCommentDto(comment, user.name()))
                .toList();
    }

    @Override
    @Transactional
    public CommentDto createComment(long userId, NewCommentDto commentDto) {
        UserShortDto user = userClient.findById(userId);

        Long eventId = commentDto.event();

        eventClient.validateEvent(eventId);

        Comment comment = commentRepository.save(
                CommentMapper.mapToComment(commentDto, userId, eventId, CommentState.WAITING)
        );
        return CommentMapper.mapToCommentDto(comment, user.name());
    }

    @Override
    @Transactional
    public CommentDto updateComment(long userId, UpdateCommentDto commentDto) {
        UserShortDto user = userClient.findById(userId);

        Comment comment = commentRepository.findById(commentDto.id())
                .orElseThrow(() -> new NotFoundException("Комментария с id " + commentDto.id() + " не найдено"));

        if (!comment.getAuthorId().equals(user.id())) {
            throw new AccessDeniedException("Редактировать может только автор комментария");
        }
        comment.setText(commentDto.text());
        return CommentMapper.mapToCommentDto(comment, user.name());
    }

    @Override
    @Transactional
    public void deleteComment(long userId, long comId) {
        Comment comment = commentRepository.findById(comId)
                .orElseThrow(() -> new NotFoundException("Комментария с id " + comId + " не найдено"));
        if (comment.getAuthorId() == userId) {
            commentRepository.delete(comment);
        } else {
            throw new AccessDeniedException("Удалять комментарий может только автор");
        }
    }

    @Override
    public List<StateCommentDto> getComments(String text, DateSort sort) {
        Iterable<Comment> comments = commentRepository.findAll(
                CommentRepository.Predicate.textFilter(text),
                getSortDate(sort)
        );

        List<Comment> commentList = StreamSupport.stream(comments.spliterator(), false)
                .toList();

        Set<Long> authorIds = commentList.stream()
                .map(Comment::getAuthorId)
                .collect(Collectors.toSet());

        Map<Long, UserShortDto> users = userClient.findAllByIds(new ArrayList<>(authorIds));

        return commentList.stream()
                .map(comment -> CommentMapper.mapToAdminDto(
                        comment,
                        users.get(comment.getAuthorId()).name()
                ))
                .toList();
    }

    @Override
    @Transactional
    public StateCommentDto reviewComment(long comId, boolean approved) {
        Comment comment = commentRepository.findById(comId)
                .orElseThrow(() -> new NotFoundException("Комментария с id " + comId + " не найдено"));

        UserShortDto user = userClient.findById(comment.getAuthorId());

        if (!comment.getState().equals(CommentState.WAITING)) {
            throw new CommentStateException("Подтверждение комментария может осуществляться только если статус равен WAITING");
        }

        if (approved) {
            comment.setState(CommentState.APPROVED);
        } else {
            comment.setState(CommentState.REJECTED);
        }

        return CommentMapper.mapToAdminDto(comment, user.name());
    }

    @Override
    @Transactional
    public void deleteComment(long comId) {
        Comment comment = commentRepository.findById(comId)
                .orElseThrow(() -> new NotFoundException("Комментария с id " + comId + " не найдено"));
        commentRepository.delete(comment);
    }

    private Sort getSortDate(DateSort sort) {
        return (sort == DateSort.DESC) ?
                Sort.by("created").descending() : Sort.by("created").ascending();
    }
}