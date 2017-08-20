package man;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.function.Supplier;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RandomGenTest {

    private int[] numbers;
    private float[] probabilities;
    private RandomGen randomGen;

    @Test
    public void shouldReturnNextNumber() {
        numbers = new int[] {1, 2, 3};
        probabilities = new float[] {0.2f, 0.7f, 0.1f};
        Supplier<Float> sampler = mock(Supplier.class);
        when(sampler.get()).thenReturn(0.33f).thenReturn(0.15f).thenReturn(0.95f);
        randomGen = new RandomGen(numbers, probabilities, sampler);
        assertThat(randomGen.nextNum(), is(equalTo(2)));
        assertThat(randomGen.nextNum(), is(equalTo(1)));
        assertThat(randomGen.nextNum(), is(equalTo(3)));
    }

    @Test
    public void shouldReturnCorrectFrequencies() {
        numbers = new int[] {-1, 0, 1, 2, 3};
        probabilities = new float[] {0.01f, 0.3f, 0.58f, 0.1f, 0.01f};
        randomGen = new RandomGen(numbers, probabilities, new Random(42)::nextFloat);
        Map<Integer, Long> result = IntStream.generate(randomGen::nextNum).limit(100000).boxed().collect(groupingBy(o -> o, counting()));
        assertThat(result, containsExpectedFrequencies());
    }

    @Test(expected = IllegalStateException.class)
    public void shouldFailToInitIfProbabilitiesDoNotAddUpToUnity() {
        numbers = new int[] {1, 2, 3};
        probabilities = new float[] {0.8f, 0.7f, 0.1f};
        randomGen = new RandomGen(numbers, probabilities, () -> 0.33f);
    }

    private Matcher<Map<Integer, Long>> containsExpectedFrequencies() {

        return new BaseMatcher<Map<Integer, Long>>() {
            @Override
            public boolean matches(Object o) {
                Map<Integer, Long> expected = new HashMap<>(5);
                expected.put(-1, 1023L);
                expected.put(0, 29972L);
                expected.put(1, 57901L);
                expected.put(2, 10060L);
                expected.put(3, 1044L);
                Map<Integer, Long> result = (Map<Integer, Long>) o;
                for (Map.Entry<Integer, Long> entry : expected.entrySet()) {
                    Long actual = result.get(entry.getKey());
                    if (actual.longValue() != entry.getValue().longValue()) {
                        return false;
                    }
                }
                return true;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("Result does not contain expected frequencies");
            }
        };
    }
}