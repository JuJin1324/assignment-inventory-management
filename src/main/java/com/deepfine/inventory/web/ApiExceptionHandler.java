package com.deepfine.inventory.web;

import com.deepfine.inventory.service.StockNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

/**
 * 출고 흐름이 던지는 예외를 일관된 HTTP 에러 응답으로 매핑한다 — 대상 재고 없음은 404,
 * 잘못된 입력(빈 productId·0·음수 수량)은 400. 부족은 예외가 아닌 정상 분기라 여기서 다루지
 * 않고 컨트롤러가 인라인 처리한다. 필요한 두 매핑만 두고 전역 예외 처리로 과설계하지 않는다.
 */
@RestControllerAdvice
public class ApiExceptionHandler {

	@ExceptionHandler(StockNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleStockNotFound(StockNotFoundException ex) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND)
				.body(new ErrorResponse(HttpStatus.NOT_FOUND.value(), ex.getMessage()));
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
		String message = ex.getBindingResult().getFieldErrors().stream()
				.map(error -> error.getField() + " " + error.getDefaultMessage())
				.collect(Collectors.joining(", "));
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), message));
	}
}
