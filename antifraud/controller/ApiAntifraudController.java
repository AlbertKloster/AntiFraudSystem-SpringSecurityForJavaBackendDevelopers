package antifraud.controller;

import antifraud.dto.PostApiAntifraudTransactionResponseDto;
import antifraud.dto.ResultTypes;
import antifraud.dto.PostApiAntifraudTransactionRequestDto;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/antifraud")
public class ApiAntifraudController {

    @PostMapping("/transaction")
    public ResponseEntity postTransaction(@RequestBody PostApiAntifraudTransactionRequestDto transaction) {
        if (transaction == null)
            return ResponseEntity.badRequest().build();
        if (transaction.amount() == null)
            return ResponseEntity.badRequest().build();
        if (transaction.amount() <= 0)
            return ResponseEntity.badRequest().build();
        if (transaction.amount() <= 200)
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new PostApiAntifraudTransactionResponseDto(ResultTypes.ALLOWED.name()));
        if (transaction.amount() <= 1500)
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new PostApiAntifraudTransactionResponseDto(ResultTypes.MANUAL_PROCESSING.name()));
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(new PostApiAntifraudTransactionResponseDto(ResultTypes.PROHIBITED.name()));
    }

}