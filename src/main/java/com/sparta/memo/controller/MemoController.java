package com.sparta.memo.controller;

import com.sparta.memo.dto.MemoRequestDto;
import com.sparta.memo.dto.MemoResponseDto;
import com.sparta.memo.entity.Memo;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController // 클라이언트는 ajax를 통해 요청해서 우리는 json 데이터만 던져주면 됨
// html 따로 반환하지 않음
@RequestMapping("/api")
public class MemoController {

    private final Map<Long, Memo> memoList = new HashMap<>();
    // key -> Memo의 Id를 넣긴 위해 Long
    // value -> 실제 데이터 Memo
    @PostMapping("/memos")
    public MemoResponseDto createMemo(@RequestBody MemoRequestDto requestDto){
        // RequestDto => Entity
        Memo memo = new Memo(requestDto);

        // Memo Max ID Check -> id값으로 메모를 구분
        // 중복이 되면 안 되니 데베에 가장 마지막 값에 +1을 하면 Max ID
        long maxId = memoList.size() > 0 ? Collections.max(memoList.keySet()) +1 : 1;
        memo.setId(maxId);

        // DB 저장
        memoList.put(memo.getId(), memo);

        // Entity -> ResponseDto 변환
        MemoResponseDto memoresponseDto = new MemoResponseDto(memo);

        return memoresponseDto;
    }
        @GetMapping("/memos")
        // Memo가 여러 개 일수 있으니 List 형식으로 반환
        public List<MemoResponseDto> getMemos(){
            //Map To List
            List<MemoResponseDto> responseList = memoList.values().stream()
                    .map(MemoResponseDto::new).toList();

            return responseList;
        }

        @PutMapping("/memos/{id}")
        public long updateMemo(@PathVariable long id, @RequestBody MemoRequestDto requestDto){
            // memo가 데베에 존재하는지 확인
            if(memoList.containsKey(id)){
                // 해당 메모 가져오기
                Memo memo = memoList.get(id);

                // 메모 수정
                memo.update(requestDto);
                return memo.getId();
            } else {
                throw new IllegalArgumentException("선택한 메모는 존재하지 않습니다.");
            }

        }

        @DeleteMapping("/memos/{id}")
        public long deleteMemo(@PathVariable long id){
            // 해당 메모가 데이터베이스에 존재하는지
            if(memoList.containsKey(id)){
                memoList.remove(id);
                return id;
            }
            else {
                throw new IllegalArgumentException("메모 없음");
            }
        }

}
