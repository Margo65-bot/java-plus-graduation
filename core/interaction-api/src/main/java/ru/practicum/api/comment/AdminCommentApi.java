package ru.practicum.api.comment;

import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.practicum.dto.comment.DateSort;
import ru.practicum.dto.comment.StateCommentDto;

import java.util.List;

@Validated
@RequestMapping("/admin/comments")
public interface AdminCommentApi {
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    List<StateCommentDto> getComments(
            @RequestParam(required = false) String text,
            @RequestParam(defaultValue = "ASC") DateSort sort
    );

    @DeleteMapping("/{comId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteComment(
            @PathVariable @Positive Long comId
    );

    @PatchMapping("/{comId}")
    @ResponseStatus(HttpStatus.OK)
    StateCommentDto reviewComment(
            @PathVariable @Positive long comId,
            @RequestParam boolean approved
    );
}
