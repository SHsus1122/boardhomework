package com.sparta.boardhomework.service;

import com.sparta.boardhomework.dto.*;
import com.sparta.boardhomework.entity.*;
import com.sparta.boardhomework.exception.CustomException;
import com.sparta.boardhomework.exception.ErrorCode;
import com.sparta.boardhomework.repository.BoardRepository;
import com.sparta.boardhomework.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

// 참고링크 : https://velog.io/@developerjun0615/Spring-RequiredArgsConstructor-%EC%96%B4%EB%85%B8%ED%85%8C%EC%9D%B4%EC%85%98%EC%9D%84-%EC%82%AC%EC%9A%A9%ED%95%9C-%EC%83%9D%EC%84%B1%EC%9E%90-%EC%A3%BC%EC%9E%85
// @RequiredArgsConstructor 은 lombok 을 사용하여 생성자 주입을 더 간단하게 해준다.
// final 이 붙거나 @NotNull 이 붙은 필드의 생성자를 자동 생성해주는 롬복 어노테이션
@Service
@RequiredArgsConstructor
public class BoardService {
    // 참고 링크 : https://velog.io/@kdhyo/JavaTransactional-Annotation-%EC%95%8C%EA%B3%A0-%EC%93%B0%EC%9E%90-26her30h
    // @Transactional 데이터베이스의 상태를 변경하는 작업 또는 한번에 수행되어야 하는 연산
    // 가장 큰 장점은 예외 발생시 rollback 처리를 자동으로 해준다.

    // @RequiredArgsConstructor 덕분에 생성자 자동 생성
    private final BoardRepository boardRepository;
    // 위 Repo 를 상수로 지정하는 이유
    // 전체 프로젝트에서 중복되서 생성되면 X 그래서 싱글톤으로 사용
    private final UserRepository userRepository;


    // 글 작성 부분
/*    @Transactional
    public BoardResponseDto createBoard(BoardRequestDto requestDto, HttpServletRequest request) {
        // Request 에서 Token 가져오기
        // resolveToken 는 컨트롤러 단에서 토큰 검증을 거쳐서 가져오는 메서드
        // Claims 는 사용자에 대한 프로퍼티나 속성 즉, 토큰 자체가 정보를 가지고있는 방식
        // JWT 는 이것에 JSON 을 이용해서 정의한다. 이녀석이 페이로드 영역을 생성한다.
        String token = jwtUtil.resolveToken(request);
        Claims claims;

        if (token != null) {
            // validateToken 토큰 유효성 검사 함수
            if (jwtUtil.validateToken(token)) {
                // 유효성 확인 되면 getUserInfoFromToken 을 통해 사용자 정보 가져오기
                claims = jwtUtil.getUserInfoFromToken(token);
            } else {
                throw new IllegalArgumentException("Token Error");
            }
            // 토큰에서 가져온 사용자 정보를 사용하여 DB 조회
            User user = userRepository.findByUsername(claims.getSubject()).orElseThrow(
                    () -> new IllegalArgumentException("사용자 정보가 존재하지 않습니다.")
            );

            // 요청받은 DTO 로 DB 에 저장할 객체 만들기
            Board board = boardRepository.save(new Board(requestDto, user.getUsername(), user.getPassword(), user.getId()));

            return new BoardResponseDto(board);
        } else
            return null;
    }*/
    @Transactional
    public BoardResponseDto createBoard(BoardRequestDto requestDto, User user) {

        Board board = boardRepository.saveAndFlush(new Board(requestDto,
                user.getUsername(),
                user.getPassword(),
                user));

        return new BoardResponseDto(board);

    }


    // 모든 글 조회
    // 람다식을 이용해서 처리 (Stream) 반복 시작
    // 아래의 코드에서 사용된 .map 은 요소들을 특정조건에 해당하는 값으로 변환해 준다. 함수 실행
    // .map 중간 연산자, collect 최종연산자
    // .collect 는 연산이 끝나고 반환해준다는 의미이다.
    // Collectors.toList 는 반환해 줄 때 리스트 타입으로 반환해준다는 의미이다.
    // 참고링크 : https://codechacha.com/ko/java8-convert-stream-to-list/
    // 참고링크 : https://dpdpwl.tistory.com/81
    public BoardListResponseDto getBoards() {
        BoardListResponseDto boardListResponseDto = new BoardListResponseDto();

        List<Board> ListBoard = boardRepository.findAllByOrderByModifiedAtDesc();

        // (타입 변수명 : 배열)
        // ListBoard 가 3 사이즈의 배열이라고 가정하면 5번을 돈다. (도는 횟수 -> ListBoard.size())
        for (Board board : ListBoard) {

            List<CommentResponseDto> commentList = new ArrayList<>();

            for (Comment comment : board.getComments()) {
                commentList.add(new CommentResponseDto(comment));
            }

            boardListResponseDto.addBoard(new BoardResponseDto(board, commentList));
        }

        return boardListResponseDto;
//        return ListBoard.stream()
//                .map(board -> new BoardResponseDto(board))
//                .collect(Collectors.toList());
    }
/*    @Transactional
    public List<BoardResponseDto> getBoards() {
        List<Board> boardList = boardRepository.findAllByOrderByModifiedAtDesc();
        List<BoardResponseDto> postResponseDto = new ArrayList<>();
        for (Board board : boardList) {
            BoardResponseDto boadrDto = new BoardResponseDto(board);
            postResponseDto.add(boadrDto);
        }
        return postResponseDto;
    }*/


    // 게시글 하나 조회하는 부분
/*    @Transactional
    public Optional<Board> readBoard(Long id) {
        Board board = boardRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("존재하지 않는 게시글 입니다.")
        );
        return boardRepository.findById(id);
    }*/
    /*public BoardResponseDto readBoard(Long id) {
        Board board = boardRepository.findByBoardId(id).orElseThrow(
                () -> new NullPointerException("존재하지 않는 게시글 입니다.")
        );

        return new BoardResponseDto(board);
    }*/
    public BoardResponseDto readBoard(Long id) {
        Board board = boardRepository.findByBoardId(id).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_BOARD)
        );

        List<CommentResponseDto> dtos = new ArrayList<>();
        for (Comment commentList : board.getComments()) {
            dtos.add(new CommentResponseDto(commentList));
        }
        return new BoardResponseDto(board, dtos);
    }


    // 게시글 수정
    @Transactional
    public BoardResponseDto update(Long id, BoardRequestDto requestDto, User user) {

        // 유효성 검사 db 에 접근
        // 정규식의 이유는 DB 에 접근하지 않고 우선 걸러주기 위해
        user = userRepository.findByUsername(user.getUsername()).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_USER)
        );

        Board board = boardRepository.findByBoardId(id).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_BOARD)
        );

        if (board.getUsername().equals(user.getUsername()) || user.getRole() == UserRoleEnum.ADMIN) {
            board.update(requestDto, user.getUsername(), user.getPassword());
        } else {
            throw new CustomException(ErrorCode.INVALID_AUTH_BOARD);
        }

        return new BoardResponseDto(board);

    }
/*
    // 수정전
    public BoardResponseDto update(Long id, BoardRequestDto requestDto) {
        Board board = boardRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("존재하지 않는 게시글 입니다.")
        );
        if (board.getPassword().equals(requestDto.getPassword())) {
            board.update(requestDto);
            boardRepository.save(board);
        }
        return new BoardResponseDto(board);
    }
    */
/*    @Transactional
    public String update(Long id ,BoardRequestDto requestDto) {
        String str = "";
        Board board = boardRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("존재하지 않는 게시글 입니다.")
        );
        if (!board.getPassword().equals(requestDto.getPassword())) {
            return str = "비밀번호 틀림";
        } else {
            board.update(requestDto);
            return str = "수정 완료";
        }
    }*/

    // 게시글 삭제
    @Transactional
    public PassResponseDto deleteBoard(Long id, User user) {
        // boardRepository 를 예로
        // db 에 들어가서 정보를 찾는데 해당 정보가 존재하지 않는 경우 예외 처리라고 한다.
        // 예외 발생에 대처를 한다는 개념
        Board board = boardRepository.findByBoardId(id).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_BOARD)
        );

        // 유효성 검사
        // 데이터가 올바른지 확인하는 개념
        if (user.getUsername().equals(board.getUsername()) || user.getRole() == UserRoleEnum.ADMIN) {
            boardRepository.delete(board);
        } else {
            throw new CustomException(ErrorCode.INVALID_AUTH_BOARD);
        }

        return new PassResponseDto(HttpStatus.OK.value(), "삭제 성공");
    }
/*    @Transactional
    public ResponseEntity<Board> deleteBoard(Long id, HttpServletRequest request) {
        String token = jwtUtil.resolveToken(request);
        Claims claims;

        if (token != null) {
            if (jwtUtil.validateToken(token)) {
                claims = jwtUtil.getUserInfoFromToken(token);
            } else {
                throw new IllegalArgumentException("Token Error");
            }

            Board board = boardRepository.findById(id).orElseThrow(
                    () -> new IllegalArgumentException("존재하지 않는 게시글 입니다.")
            );

            boardRepository.delete(board);

            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return responseDto;
        }
    }*/
}
