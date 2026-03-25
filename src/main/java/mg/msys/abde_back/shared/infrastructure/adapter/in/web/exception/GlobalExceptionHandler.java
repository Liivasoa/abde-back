package mg.msys.abde_back.shared.infrastructure.adapter.in.web.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import mg.msys.abde_back.shared.infrastructure.adapter.in.web.dto.ErrorResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

        @ExceptionHandler(HttpMessageNotReadableException.class)
        public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(
                        HttpMessageNotReadableException ex) {

                ErrorResponse errorResponse = new ErrorResponse(
                                "Invalid request format",
                                "Request body must be valid JSON");

                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }

        @ExceptionHandler(IllegalArgumentException.class)
        public ResponseEntity<ErrorResponse> handleIllegalArgumentException(
                        IllegalArgumentException ex) {

                ErrorResponse errorResponse = new ErrorResponse(
                                "Invalid language data",
                                ex.getMessage());

                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }

        @ExceptionHandler(Exception.class)
        public ResponseEntity<ErrorResponse> handleGeneralException(
                        Exception ex) {

                ErrorResponse errorResponse = new ErrorResponse(
                                "Internal server error",
                                "An unexpected error occurred");

                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(errorResponse);
        }
}
