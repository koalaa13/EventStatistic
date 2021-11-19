package statistic;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import timestamp.ChangeableClock;

import java.time.Instant;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EventStatisticImplTest {
    private Instant startTime;
    private ChangeableClock clock;
    private EventStatistic eventStatistic;

    @BeforeEach
    public void beforeEach() {
        startTime = Instant.now();
        clock = new ChangeableClock(startTime);
        eventStatistic = new EventStatisticImpl(clock);
    }

    @Test
    public void simpleTest() {
        Map<String, Double> rightAnswers = Map.of("event1", 1.0 / 60.0, "event2", 10.0 / 60.0);
        eventStatistic.incEvent("event1");
        for (int i = 0; i < 10; ++i) {
            clock.setTimestamp(startTime.plusSeconds(i * 100));
            eventStatistic.incEvent("event2");
        }
        assertEquals(rightAnswers.get("event1"), eventStatistic.getEventStatisticByName("event1"));
        assertEquals(rightAnswers.get("event2"), eventStatistic.getEventStatisticByName("event2"));
        var fullInfo = eventStatistic.getAllEventStatistic();
        assertEquals(2, fullInfo.size());
        for (var eventInfo : fullInfo.entrySet()) {
            assertTrue(rightAnswers.containsKey(eventInfo.getKey()));
            assertEquals(rightAnswers.get(eventInfo.getKey()), eventInfo.getValue());
        }
    }

    @Test
    public void UnexistingEventTest() {
        assertEquals(0, eventStatistic.getEventStatisticByName("event"));
    }

    @Test
    public void OneEventTest() {
        eventStatistic.incEvent("event");
        var fullInfo = eventStatistic.getAllEventStatistic();
        assertEquals(1, fullInfo.size());
        for (var eventInfo : fullInfo.entrySet()) {
            assertEquals("event", eventInfo.getKey());
            assertEquals(1.0 / 60.0, eventInfo.getValue());
        }
    }

    @Test
    public void ShouldNotGetOldEvent() {
        eventStatistic.incEvent("event");
        clock.setTimestamp(startTime.plusSeconds(4000)); // more than a hour
        assertEquals(0, eventStatistic.getEventStatisticByName("event"));
        var fullInfo = eventStatistic.getAllEventStatistic();
        assertTrue(fullInfo.isEmpty());
    }

    @Test
    public void printStatisticExample() {
        eventStatistic.incEvent("event1");
        eventStatistic.incEvent("event2");
        clock.setTimestamp(startTime.plusSeconds(3600)); // 1 hour
        eventStatistic.incEvent("event2");
        eventStatistic.printStatistic();
    }
}
