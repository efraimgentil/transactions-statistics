package me.efraimgentil.transactionsstatistics.controller;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import me.efraimgentil.transactionsstatistics.service.exception.OldTransactionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
public class Adivicer {

    private static final Logger logger = LoggerFactory.getLogger(Adivicer.class);

    @ExceptionHandler(OldTransactionException.class)
    public ResponseEntity handleError(HttpServletRequest req, OldTransactionException ex) {
        logger.warn("Transaction was rejected for the statistic because is too old. Transaction: {}" , ex.getTransaction());
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler({ MethodArgumentNotValidException.class })
    public ResponseEntity handleR(MethodArgumentNotValidException ex , ServletWebRequest req ) {
        BindingResult bindingResult = ex.getBindingResult();
        logger.warn("Constraint violation on Request '{}-{}' with body '{}'", req.getHttpMethod()
                ,  req.getDescription(false) , bindingResult.getTarget());
        return ResponseEntity.badRequest().body(new ConstraintViolation(ex));
    }

    private class ConstraintViolation {

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime timestamp;
        @JsonProperty
        private int status;
        @JsonProperty
        private String message;
        @JsonProperty
        private List<FieldViolation> errors;

        public ConstraintViolation(MethodArgumentNotValidException ex){
            this.timestamp = LocalDateTime.now();
            this.status = HttpStatus.BAD_REQUEST.value();
            this.message = "Constraint violation";
            this.errors = ex.getBindingResult().getAllErrors().stream().map(oe -> (FieldError) oe).map(
                    fe -> new FieldViolation(fe.getField() , fe.getDefaultMessage()) ).collect(Collectors.toList());
        }

        private class FieldViolation {
            @JsonProperty
            private String field;
            @JsonProperty
            private String violation;

            public FieldViolation(String field, String violation) {
                this.field = field;
                this.violation = violation;
            }
        }

    }


}
