package com.gxdxx.shop.repository;

import com.gxdxx.shop.dto.BoardListDto;
import com.gxdxx.shop.dto.BoardSearchDto;
import com.gxdxx.shop.entity.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BoardRepositoryCustom {

    Page<BoardListDto> getBoardPage(BoardSearchDto boardSearchDto, Pageable pageable);

}