package com.gxdxx.shop.controller;

import com.gxdxx.shop.dto.*;
import com.gxdxx.shop.service.BoardService;
import com.gxdxx.shop.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;
    private final CommentService commentService;

    @GetMapping(value = "/board/new")   // 게시글 form
    public String boardForm(Model model) {
        model.addAttribute("boardFormDto", new BoardFormDto());
        return "board/boardForm";
    }

    @PostMapping(value = "/board/new")  // 글쓰기
    public String boardNew(@Valid BoardFormDto boardFormDto, BindingResult bindingResult,
                           Principal principal, Model model) {

        if (bindingResult.hasErrors()) {    // 필수값이 들어있는지 검사
            return "board/boardForm";
        }

        String email = principal.getName();
        boardService.saveBoard(email, boardFormDto);

        return "redirect:/boards";
    }

    @GetMapping(value = "/board/{boardId}/edit")    // 글 수정 페이지
    public String boardEdit(@PathVariable("boardId") Long boardId, Principal principal, Model model) {

        if (!boardService.validateBoard(boardId, principal.getName())) {
            return "redirect:/boards";
        }

        BoardFormDto boardFormDto = boardService.getBoardForm(boardId);
        model.addAttribute("boardFormDto", boardFormDto);

        return "board/boardForm";
    }

    @PostMapping(value = "/board/{boardId}")    // 글 수정
    public String boardUpdate(@Valid BoardFormDto boardFormDto, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return "board/boardForm";
        }

        boardService.updateBoard(boardFormDto);

        return "redirect:/board/{boardId}";
    }

    @GetMapping(value = "/board/{boardId}") // 글 보기
    public String boardDtl(Model model, @PathVariable("boardId") Long boardId) {

        boardService.hitsCount(boardId);

        BoardDetailDto boardDetailDto = boardService.getBoardDetail(boardId);
        List<CommentFormDto> commentFormDtos = commentService.getComments(boardId);

        model.addAttribute("board", boardDetailDto);
        model.addAttribute("comments", commentFormDtos);
        return "board/boardDtl";
    }

    @DeleteMapping(value = "/board/{boardId}")  // 글 삭제
    public @ResponseBody ResponseEntity deleteBoard(
            @PathVariable("boardId") Long boardId, Principal principal) {

        if (!boardService.validateBoard(boardId, principal.getName())) {
            return new ResponseEntity<String>("삭제 권한이 없습니다.", HttpStatus.FORBIDDEN);
        }

        boardService.deleteBoard(boardId);
        return new ResponseEntity<Long>(boardId, HttpStatus.OK);
    }

    @GetMapping(value = {"/boards", "/boards/{page}"})
    public String boardList(BoardSearchDto boardSearchDto, @PathVariable("page") Optional<Integer> page, Model model) {

        Pageable pageable = PageRequest.of(page.isPresent() ? page.get() : 0, 10);

        Page<BoardListDto> boards = boardService.getBoardPage(boardSearchDto, pageable);

        model.addAttribute("boards", boards);
        model.addAttribute("boardSearchDto", boardSearchDto);
        model.addAttribute("maxPage", 5);

        return "board/boardList";
    }

}