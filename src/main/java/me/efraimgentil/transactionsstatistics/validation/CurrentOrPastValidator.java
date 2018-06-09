package me.efraimgentil.transactionsstatistics.validation;

import me.efraimgentil.transactionsstatistics.validation.annotation.CurrentOrPastTimestamp;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.Instant;

@Component
public class CurrentOrPastValidator implements ConstraintValidator<CurrentOrPastTimestamp, Long> {

    public CurrentOrPastValidator() {
    }

    @Override
    public boolean isValid(Long value, ConstraintValidatorContext context) {
        if(value != null){
            long l = Instant.ofEpochMilli(value).toEpochMilli();
            if( l > Instant.now().toEpochMilli()) {
                return false;
            }
        }
        return true;
    }

}
