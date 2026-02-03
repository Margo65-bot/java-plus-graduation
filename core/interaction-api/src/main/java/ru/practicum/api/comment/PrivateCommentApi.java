package ru.practicum.api.comment;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.practicum.dto.comment.CommentDto;
import ru.practicum.dto.comment.NewCommentDto;
import ru.practicum.dto.comment.UpdateCommentDto;

import java.util.List;

@Validated
@RequestMapping("/users/{userId}/comments")
public interface PrivateCommentApi {
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    List<CommentDto> getComments(
            @PathVariable @Positive long userId
    );

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    CommentDto createComment(
            @PathVariable @Positive long userId,
            @RequestBody @Valid NewCommentDto commentDto
    );

    @DeleteMapping("/{comId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteComment(
            @PathVariable @Positive long userId,
            @PathVariable @Positive long comId
    );

    @PatchMapping
    @ResponseStatus(HttpStatus.OK)
    CommentDto updateComment(
            @PathVariable @Positive long userId,
            @RequestBody @Valid UpdateCommentDto commentDto
    );
}
