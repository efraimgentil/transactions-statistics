package me.efraimgentil.transactionsstatistics.service.statistic;

import me.efraimgentil.transactionsstatistics.domain.Statistic;
import me.efraimgentil.transactionsstatistics.domain.Transaction;
import org.assertj.core.data.TemporalOffset;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.scheduling.TaskScheduler;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.withPrecision;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TransactionCacheServiceTest {

    TransactionCacheService service;
    @Mock
    TaskScheduler scheduler;
    private final Integer rangeInSeconds = 60;


    @Before
    public void setUp(){
        service = new TransactionCacheService(scheduler , rangeInSeconds);
    }


    @Test
    public void shouldUpdateTheTransactionWithNewZerosValues(){
        TreeMap<Long, List<Transaction>> map = new TreeMap<>();

        service.updateStatistic(map);
        Statistic statistic = service.getStatistic();

        assertThat(statistic).isNotNull();
        assertThat(statistic.getSum()).isEqualTo(0.0);
        assertThat(statistic.getAvg()).isEqualTo(0.0);
        assertThat(statistic.getMax()).isEqualTo(0.0);
        assertThat(statistic.getMin()).isEqualTo(0.0);
        assertThat(statistic.getCount()).isEqualTo(0);
    }

    @Test
    public void shouldUpdateTheTransactionWithNewValues(){
        TreeMap<Long, List<Transaction>> map = new TreeMap<>();
        long now = Instant.now().toEpochMilli();
        map.put(now , Arrays.asList(
                new Transaction( 10.0 , now ),
                new Transaction( 15.55 , now )
        ));

        service.updateStatistic(map);
        Statistic statistic = service.getStatistic();

        assertThat(statistic).isNotNull();
        assertThat(statistic.getSum()).isEqualTo(25.55);
        assertThat(statistic.getAvg()).isCloseTo(12.77 , withPrecision(0.1));
        assertThat(statistic.getMax()).isEqualTo(15.55);
        assertThat(statistic.getMin()).isEqualTo(10.0);
        assertThat(statistic.getCount()).isEqualTo(2);
    }


    @Test
    public void shouldUpdateTheTransactionWithNewValuesConsideringAllTimes(){
        TreeMap<Long, List<Transaction>> map = new TreeMap<>();
        long now = Instant.now().toEpochMilli();
        map.put(now , Arrays.asList(
                new Transaction( 10.0 , now ),
                new Transaction( 15.55 , now )
        ));
        long tenSecondsAgo = Instant.now().minusSeconds(10).toEpochMilli();
        map.put(tenSecondsAgo , Arrays.asList(
                new Transaction( 234.67 , tenSecondsAgo )
        ));
        long fiftySecondsAgo = Instant.now().minusSeconds(50).toEpochMilli();
        map.put(fiftySecondsAgo , Arrays.asList(
                new Transaction( 8345.95 , fiftySecondsAgo )
        ));
        long fiftyNineSecondsAgo = Instant.now().minusSeconds(59).toEpochMilli();
        map.put(fiftyNineSecondsAgo , Arrays.asList(
                new Transaction( 1.99 , fiftyNineSecondsAgo )
        ));

        service.updateStatistic(map);
        Statistic statistic = service.getStatistic();

        assertThat(statistic).isNotNull();
        assertThat(statistic.getSum()).isEqualTo(8608.16);
        assertThat(statistic.getAvg()).isCloseTo(1721.63 , withPrecision(0.1));
        assertThat(statistic.getMax()).isEqualTo(8345.95);
        assertThat(statistic.getMin()).isEqualTo(1.99);
        assertThat(statistic.getCount()).isEqualTo(5 );
    }

    @Test
    public void shouldReturnTheExpirationTimeForTheTransaction(){
        Instant now = Instant.now();
        Transaction transaction = new Transaction(0.0 , now.toEpochMilli() );

        long expirationTime = service.getExpirationTime(transaction);


        ChronoUnit chronoUnit = ChronoUnit.SECONDS;
        assertThat( chronoUnit.between(now , Instant.ofEpochMilli(expirationTime)) ).isEqualTo(60);
        assertThat( now.plusSeconds(60).toEpochMilli()).isEqualTo( expirationTime );
    }

    @Test
    public void shouldReturnTheExpirationTimeForTheTransaction2(){
        Instant now = Instant.now();
        Instant past = Instant.now().minusSeconds(59);
        Transaction transaction = new Transaction(0.0 , past.toEpochMilli() );

        long expirationTime = service.getExpirationTime(transaction);

        ChronoUnit chronoUnit = ChronoUnit.SECONDS;
        assertThat( chronoUnit.between( now , Instant.ofEpochMilli(expirationTime) ) ).isEqualTo(1);
        System.out.println(past);
        System.out.println(Instant.ofEpochMilli(expirationTime));
    }



}
