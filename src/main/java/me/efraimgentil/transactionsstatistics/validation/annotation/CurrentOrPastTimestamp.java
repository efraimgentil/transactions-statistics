package me.efraimgentil.transactionsstatistics.validation.annotation;

import me.efraimgentil.transactionsstatistics.validation.CurrentOrPastValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = CurrentOrPastValidator.class)
@Target({ ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface CurrentOrPastTimestamp {

    String message() default "timestamp in the future";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

}
