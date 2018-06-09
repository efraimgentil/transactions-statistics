package me.efraimgentil.transactionsstatistics.validation;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.validation.ConstraintValidatorContext;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class CurrentOrPastValidatorTest {

    CurrentOrPastValidator validator;

    @Mock ConstraintValidatorContext constraintValidatorContext;

    @Before
    public void setUp(){
        validator = new CurrentOrPastValidator();
    }

    @Test
    public void shouldReturnTrueIfValueIsNull(){
        boolean result = validator.isValid(null, constraintValidatorContext);

        assertThat(result).isTrue();
    }

    @Test
    public void shouldReturnTrueIfValueIsCurrentTime(){

        boolean result = validator.isValid(Instant.now().toEpochMilli(), constraintValidatorContext);

        assertThat(result).isTrue();
    }

    @Test
    public void shouldReturnTrueIfValueIsInPast(){

        boolean result = validator.isValid(Instant.now().minusSeconds(2).toEpochMilli(), constraintValidatorContext);

        assertThat(result).isTrue();
    }

    @Test
    public void shouldReturnFalseIfValueIsInFuture(){

        boolean result = validator.isValid(Instant.now().plusSeconds(2).toEpochMilli(), constraintValidatorContext);

        assertThat(result).isFalse();
    }


}
