package me.efraimgentil.transactionsstatistics.service;

import me.efraimgentil.transactionsstatistics.domain.Transaction;
import me.efraimgentil.transactionsstatistics.service.exception.OldTransactionException;
import me.efraimgentil.transactionsstatistics.service.statistic.TransactionStatistics;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.Instant;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.verify;


@RunWith(MockitoJUnitRunner.class)
public class TransactionServiceTest {

    TransactionService service;
    @Mock
    TransactionStatistics statistics;

    @Before
    public void setUp(){
        service = new TransactionService(60 , statistics);
    }

    @Test(expected = OldTransactionException.class)
    public void shouldRejectTransactionIfOlderThanThePastSixtySecondsFormNow(){
        //Instant uses the UTC time as default
        long timestamp = Instant.now().minusSeconds(60).toEpochMilli();

        service.addTransactionStatistic( new Transaction( 0.0 , timestamp ) );
    }

    @Test
    public void shouldAddTheTransactionToTheStatistics(){
        //Instant uses the UTC time as default
        long timestamp = Instant.now().toEpochMilli();
        Transaction transaction = new Transaction(0.0, timestamp);

        service.addTransactionStatistic( transaction );

        verify(statistics).addToStatistic(transaction);
    }

    @Test
    public void shouldReturnFalseIfTheTransactionTimeIsTooOld(){
        long timestamp = Instant.now().minusSeconds(60).toEpochMilli();

        boolean inStatisticRange = service.isInStatisticRange(new Transaction(0.0, timestamp));

        assertThat(inStatisticRange).isFalse();
    }

    @Test
    public void shouldReturnTrueIfTheTransactionTimeIsInTheRange(){
        long timestamp = Instant.now().minusSeconds(59).toEpochMilli();

        boolean inStatisticRange = service.isInStatisticRange(new Transaction(0.0, timestamp));

        assertThat(inStatisticRange).isTrue();
    }

}
