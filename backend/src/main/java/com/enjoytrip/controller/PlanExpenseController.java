package com.enjoytrip.controller;

import com.enjoytrip.dto.ResponseDto;
import com.enjoytrip.model.dto.FileDTO;
import com.enjoytrip.model.dto.PlanExpenseDTO;
import com.enjoytrip.service.PlanExpenseService;
import com.enjoytrip.util.S3UploaderUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/plan-expense")
@Slf4j
public class PlanExpenseController {
    private final PlanExpenseService planExpenseService;
    private final ResponseDto responseDto;
    private final S3UploaderUtil s3UploaderUtil;
    @PostMapping
    public ResponseEntity<?> addExpense(@RequestBody PlanExpenseDTO planExpensedto){
        try{
            System.out.println("planId:" + planExpensedto.getPlanId());
            planExpenseService.addExpense(planExpensedto);
            planExpensedto = planExpenseService.getExpenseById(planExpensedto.getExpId());
            return responseDto.success(planExpensedto);
        }catch (Exception e){
            e.printStackTrace();
            log.debug("addExpense error occurred!");
            return responseDto.fail("server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping
    public ResponseEntity<?> getExpenses(@RequestParam("planId") int planId) {
        try {
            List<PlanExpenseDTO> expenses = planExpenseService.getExpensesByPlanId(planId);
            return responseDto.success(expenses);
        } catch (Exception e) {
            e.printStackTrace();
            log.debug("getExpenses error occurred!");
            return responseDto.fail("server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{expId}")
    public ResponseEntity<?> deleteExpense(@PathVariable("expId") int expId) {
        try {
            planExpenseService.deleteExpenseById(expId);
            return responseDto.success("삭제 완료");
        } catch (Exception e) {
            e.printStackTrace();
            log.debug("deleteExpense error occurred!");
            return responseDto.fail("server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/{expId}/receipt")
    public ResponseEntity<?> uploadReceipt(@PathVariable int expId, @RequestParam MultipartFile file) {
        try {
            FileDTO uploaded = s3UploaderUtil.uploadFile(file, "expense-receipt");
            planExpenseService.updateReceiptPath(expId, uploaded.getPath());
            return responseDto.success(uploaded.getPath());
        } catch (Exception e) {
            return responseDto.fail("업로드 실패", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{expId}/receipt")
    public ResponseEntity<?> deleteReceipt(@PathVariable int expId) {
        try {
            String path = planExpenseService.getReceiptPath(expId);
            if (path != null) s3UploaderUtil.deleteFile(path);
            planExpenseService.updateReceiptPath(expId, null);
            return responseDto.success("Receipt deleted successfully.");
        } catch (Exception e) {
            e.printStackTrace();
            return responseDto.fail("Delete failed.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{expId}/receipt")
    public ResponseEntity<?> downloadReceipt(@PathVariable int expId) {
        try {
            String url = planExpenseService.getReceiptPath(expId);
            if (url == null || url.isEmpty()) {
                return responseDto.fail("No receipt found.", HttpStatus.NOT_FOUND);
            }

            // 파일명 추출
            String fileName = url.substring(url.lastIndexOf('/') + 1);

            // S3에서 파일 바이트 가져오기
            Optional<byte[]> fileData = s3UploaderUtil.downloadFile(fileName);
            if (fileData.isEmpty()) {
                return responseDto.fail("File not found in S3.", HttpStatus.NOT_FOUND);
            }

            // 파일 응답 구성
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentLength(fileData.get().length);
            headers.setContentDispositionFormData("attachment", fileName);

            return new ResponseEntity<>(fileData.get(), headers, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return responseDto.fail("Download failed.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
