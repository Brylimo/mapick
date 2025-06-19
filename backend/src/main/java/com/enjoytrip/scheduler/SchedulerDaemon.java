package com.enjoytrip.scheduler;

import com.enjoytrip.model.dto.BoardDTO;
import com.enjoytrip.model.dto.GugunsDataDTO;
import com.enjoytrip.model.dto.SidosDataDTO;
import com.enjoytrip.service.ContentTypeService;
import com.enjoytrip.service.GugunsService;
import com.enjoytrip.service.ReservedBoardService;
import com.enjoytrip.service.SidosService;
import com.enjoytrip.util.SimpleDelayQueue;
import com.enjoytrip.util.TourUtil;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class SchedulerDaemon {

    private final TourUtil tourUtil;
    private final ContentTypeService contentTypeService;
    private final SidosService sidosService;
    private final GugunsService gugunsService;

    @PostConstruct
    public void init() {
        // fetchData();
    }

    public void fetchData() {

        try {
            // 처음에 예약 보드를 모두 불러와서 delayed queue에 집어넣음
            /*List<BoardDTO> list = reservedBoardService.getAllReservedBoards();
            for (BoardDTO reservedBoardDto : list) {
                BoardDTO taskBoard = new BoardDTO(reservedBoardDto);

                if (taskBoard.getReservedTime() != null) {
                    simpleDelayQueue.add(new SimpleDelayQueue.DelayedTask(Duration.between(LocalDateTime.now(), taskBoard.getReservedTime()).toMillis(), () -> {
                        try {
                            reservedBoardService.publishReservedPost(taskBoard);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }));
                }
            }*/

            // 콘텐츠 타입을 저장
            contentTypeService.insertAllContentType();

            int index = 0;
            ArrayList<Integer> codeList = new ArrayList<>();
            while (true) { // 시도 정보를 저장
                List<SidosDataDTO> sidoDataDTOList = tourUtil.getSidosInfo(index++);

                if (sidoDataDTOList == null) break;
                sidosService.insertSidoList(sidoDataDTOList);

                for (SidosDataDTO dto : sidoDataDTOList) { // 시도 코드를 모두 저장
                    codeList.add(dto.getCode());
                }
            }

            for (int code : codeList) { // 시도 코드를 순회하며 구군 정보를 저장
                index = 0;
                while (true) {
                    List<GugunsDataDTO> gugunDataDTOList = tourUtil.getGugunsInfo(code, index++);
                    if (gugunDataDTOList == null) break;

                    gugunDataDTOList.stream().forEach(dto -> {
                        dto.setSidoCode(code);
                    });
                    gugunsService.insertGugunList(gugunDataDTOList);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
