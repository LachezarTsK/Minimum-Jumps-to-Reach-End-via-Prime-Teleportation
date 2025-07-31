
using System;
using System.Collections.Generic;

public class Solution
{
    private static readonly int START_INDEX = 0;
    private static readonly int SMALLEST_PRIME_NUMBER = 2;

    private int maxValue;
    private int inputSize;
    private int goalIndex;

    private bool[]? primes;
    private Dictionary<int, List<int>>? valueToIndexes;

    public int MinJumps(int[] input)
    {
        inputSize = input.Length;
        goalIndex = input.Length - 1;
        maxValue = input.Max();

        primes = CreateSieveOfEratosthenes();
        valueToIndexes = CreateMapValueToIndexes(input);

        return FindMinJumpsFromStartToGoal(input);
    }

    private int FindMinJumpsFromStartToGoal(int[] input)
    {
        Queue<int> queue = [];
        queue.Enqueue(START_INDEX);

        bool[] visited = new bool[inputSize];
        visited[START_INDEX] = true;

        bool goalReached = false;
        int minJumpsFromStartToGoal = 0;

        while (queue.Count > 0)
        {
            int currentRoundOfSteps = queue.Count;

            while (currentRoundOfSteps > 0)
            {
                int index = queue.Dequeue();
                if (index == goalIndex)
                {
                    goalReached = true;
                    break;
                }
                HandleJumpToAdjacentIndexes(index, queue, visited);
                HandlePrimeTeleportation(index, input, queue, visited);
                --currentRoundOfSteps;
            }

            if (goalReached)
            {
                break;
            }
            ++minJumpsFromStartToGoal;
        }

        return minJumpsFromStartToGoal;
    }

    private bool[] CreateSieveOfEratosthenes()
    {
        bool[] primes = new bool[maxValue + 1];
        Array.Fill(primes, true);
        for (int value = 0; value < Math.Min(maxValue, SMALLEST_PRIME_NUMBER); ++value)
        {
            primes[value] = false;
        }

        int upperLimit = (int)Math.Sqrt(maxValue);
        for (int value = SMALLEST_PRIME_NUMBER; value <= upperLimit; ++value)
        {
            if (!primes[value])
            {
                continue;
            }

            long nextValue = (long)value * value;
            while (nextValue <= maxValue)
            {
                primes[(int)nextValue] = false;
                nextValue += value;
            }
        }
        return primes;
    }

    private Dictionary<int, List<int>> CreateMapValueToIndexes(int[] input)
    {
        Dictionary<int, List<int>> valueToIndexes = [];
        for (int i = 0; i < inputSize; ++i)
        {
            valueToIndexes.TryAdd(input[i], new List<int>());
            valueToIndexes[input[i]].Add(i);
        }
        return valueToIndexes;
    }

    private void HandleJumpToAdjacentIndexes(int index, Queue<int> queue, bool[] visited)
    {
        if (index - 1 >= 0 && !visited[index - 1])
        {
            queue.Enqueue(index - 1);
            visited[index - 1] = true;
        }

        if (index + 1 < inputSize && !visited[index + 1])
        {
            queue.Enqueue(index + 1);
            visited[index + 1] = true;
        }
    }

    private void HandlePrimeTeleportation(int index, int[] input, Queue<int> queue, bool[] visited)
    {
        if (!primes[input[index]])
        {
            return;
        }

        int currentValue = input[index];
        while (currentValue <= maxValue)
        {

            if (valueToIndexes.ContainsKey(currentValue))
            {
                List<int> indexes = valueToIndexes[currentValue];

                foreach (int i in indexes)
                {
                    if (!visited[i])
                    {
                        queue.Enqueue(i);
                        visited[i] = true;
                    }
                }
                valueToIndexes.Remove(currentValue);
            }
            currentValue += input[index];
        }
    }
}
