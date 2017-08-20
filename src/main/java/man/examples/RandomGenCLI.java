package man.examples;

import man.RandomGen;

import java.util.stream.IntStream;

import static java.lang.System.out;

public class RandomGenCLI {

    public static void main(String[] args) {
        int[] numbers = new int[] {-1, 0, 1, 2, 3};
        float[] probabilities = new float[] {0.01f, 0.3f, 0.58f, 0.1f, 0.01f};
        int size = 10;
        RandomGen random = RandomGen.getDefault(numbers, probabilities);
        IntStream.generate(random::nextNum).limit(size).forEach(out::println);
    }
}