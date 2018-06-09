package me.efraimgentil.transactionsstatistics.service;

import me.efraimgentil.transactionsstatistics.domain.Transaction;
import me.efraimgentil.transactionsstatistics.exception.OldTransactionException;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.springframework.stereotype.Service;

import java.time.*;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;


public class TransactionServiceTest {


    TransactionService service;

    @Before
    public void setUp(){
        service = new TransactionService(60);
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

        service.addTransactionStatistic( new Transaction( 0.0 , timestamp ) );
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

    @Test
    public void testClock(){
        ZoneId.getAvailableZoneIds().forEach((String s) -> {
            System.out.println(s);
            Instant now = Instant.now();
            System.out.println("Zona : " + now.atZone(ZoneId.of(s)).toInstant().toEpochMilli()  );
            System.out.println("UTC 1: " + now.toEpochMilli());
            System.out.println("UTC 2: " + now.atZone(ZoneOffset.UTC) );
        });
        Instant timeStamp= Instant.now();
        System.out.println("Machine Time Now:" + timeStamp);

        //timeStamp in zone - "America/Los_Angeles"
        ZonedDateTime LAZone= timeStamp.atZone(ZoneId.of("America/Los_Angeles"));
        System.out.println("In Los Angeles(America) Time Zone:"+ LAZone);

        //timeStamp in zone - "GMT+01:00"
        ZonedDateTime timestampAtGMTPlus1= timeStamp.atZone(ZoneId.of("GMT+01:00"));
        System.out.println("In 'GMT+01:00' Time Zone:"+ timestampAtGMTPlus1);
    }

}
