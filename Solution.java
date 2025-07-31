
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

public class Solution {

    private static final int START_INDEX = 0;
    private static final int SMALLEST_PRIME_NUMBER = 2;

    private int maxValue;
    private int inputSize;
    private int goalIndex;

    private boolean[] primes;
    private Map<Integer, List<Integer>> valueToIndexes;

    public int minJumps(int[] input) {
        inputSize = input.length;
        goalIndex = input.length - 1;
        maxValue = Arrays.stream(input).max().getAsInt();

        primes = createSieveOfEratosthenes();
        valueToIndexes = createMapValueToIndexes(input);

        return findMinJumpsFromStartToGoal(input);
    }

    private int findMinJumpsFromStartToGoal(int[] input) {
        Queue<Integer> queue = new LinkedList<>();
        queue.add(START_INDEX);

        boolean[] visited = new boolean[inputSize];
        visited[START_INDEX] = true;

        boolean goalReached = false;
        int minJumpsFromStartToGoal = 0;

        while (!queue.isEmpty()) {
            int currentRoundOfSteps = queue.size();

            while (currentRoundOfSteps > 0) {
                int index = queue.poll();
                if (index == goalIndex) {
                    goalReached = true;
                    break;
                }
                handleJumpToAdjacentIndexes(index, queue, visited);
                handlePrimeTeleportation(index, input, queue, visited);
                --currentRoundOfSteps;
            }

            if (goalReached) {
                break;
            }
            ++minJumpsFromStartToGoal;
        }

        return minJumpsFromStartToGoal;
    }

    private boolean[] createSieveOfEratosthenes() {
        boolean[] primes = new boolean[maxValue + 1];
        Arrays.fill(primes, true);
        for (int value = 0; value < Math.min(maxValue, SMALLEST_PRIME_NUMBER); ++value) {
            primes[value] = false;
        }

        int upperLimit = (int) Math.sqrt(maxValue);
        for (int value = SMALLEST_PRIME_NUMBER; value <= upperLimit; ++value) {
            if (!primes[value]) {
                continue;
            }

            long nextValue = (long) value * value;
            while (nextValue <= maxValue) {
                primes[(int) nextValue] = false;
                nextValue += value;
            }
        }
        return primes;
    }

    private Map<Integer, List<Integer>> createMapValueToIndexes(int[] input) {
        Map<Integer, List<Integer>> valueToIndexes = new HashMap<>();
        for (int i = 0; i < inputSize; ++i) {
            valueToIndexes.putIfAbsent(input[i], new ArrayList<>());
            valueToIndexes.get(input[i]).add(i);
        }
        return valueToIndexes;
    }

    private void handleJumpToAdjacentIndexes(int index, Queue<Integer> queue, boolean[] visited) {
        if (index - 1 >= 0 && !visited[index - 1]) {
            queue.add(index - 1);
            visited[index - 1] = true;
        }

        if (index + 1 < inputSize && !visited[index + 1]) {
            queue.add(index + 1);
            visited[index + 1] = true;
        }
    }

    private void handlePrimeTeleportation(int index, int[] input, Queue<Integer> queue, boolean[] visited) {
        if (!primes[input[index]]) {
            return;
        }

        int currentValue = input[index];
        while (currentValue <= maxValue) {

            if (valueToIndexes.containsKey(currentValue)) {
                List<Integer> indexes = valueToIndexes.remove(currentValue);

                for (int i : indexes) {
                    if (!visited[i]) {
                        queue.add(i);
                        visited[i] = true;
                    }
                }
            }
            currentValue += input[index];
        }
    }
}
