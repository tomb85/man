package man;

import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.function.Supplier;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;

public class RandomGen {

    private static final double EPSILON = 0.00000001;

    private final int[] randomNums;
    private final float[] probabilities;
    private final Supplier<Float> sampler;
    private final List<Float> cumulativeProbabilities;

    /**
     * @param randomNums Array of possible numbers that can be returned by calling {@link #nextNum()}
     * @param probabilities Array of probabilities associated with possible random numbers
     * @param sampler Supplies uniformly distributed float values in the range from 0.0 to 1.0
     */

    public RandomGen(int[] randomNums, float[] probabilities, Supplier<Float> sampler) {
        this.randomNums = Objects.requireNonNull(randomNums);
        this.probabilities = Objects.requireNonNull(probabilities);
        this.sampler = Objects.requireNonNull(sampler);
        if (randomNums.length == 0) {
            throw new IllegalStateException("Random number list cannot be empty");
        }
        if (probabilities.length == 0) {
            throw new IllegalStateException("Probabilities list cannot be empty");
        }
        if (randomNums.length != probabilities.length) {
            throw new IllegalStateException("Size of random numbers and probabilities must be the same");
        }
        cumulativeProbabilities = calculateCumulativeProbabilities();
        verify();
    }

    public static RandomGen getDefault(int[] randomNums, float[] probabilities) {
        return new RandomGen(randomNums, probabilities, new Random()::nextFloat);
    }

    private void verify() {
        int last = cumulativeProbabilities.size() - 1;
        double value = cumulativeProbabilities.get(last);
        if (value < 1.0 - EPSILON || value > 1.0 + EPSILON) {
            throw new IllegalStateException("Upper bound " + value + " is not equal to 1.0");
        }
    }

    private List<Float> calculateCumulativeProbabilities() {
        CumulativeAccumulator accumulator = new CumulativeAccumulator();
        return IntStream.range(0, probabilities.length).mapToObj(index -> probabilities[index]).map(accumulator::accumulate).collect(toList());
    }

    /**
     * @return One of the possible random numbers based on the associated probability distribution
     */

    public int nextNum() {
        float x = sampler.get();
        int index = getIndex(x, 0, cumulativeProbabilities.size() - 1);
        return randomNums[index];
    }

    private int getIndex(float x, int start, int end) {
        if (start == end) {
            return start;
        }
        int mid = (start + end) / 2;
        float value = cumulativeProbabilities.get(mid);
        if (x <= value) {
            return getIndex(x, start, mid);
        } else {
            return getIndex(x, mid + 1, end);
        }
    }

    class CumulativeAccumulator {

        float total = 0.0f;

        float accumulate(float value) {
            total += value;
            return total;
        }
    }
}