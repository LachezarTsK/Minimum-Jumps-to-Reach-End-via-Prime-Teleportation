
#include <span>
#include <cmath>
#include <queue>
#include <ranges>
#include <vector>
#include <algorithm>
#include <unordered_map>
using namespace std;


class Solution {

    inline static const int START_INDEX = 0;
    inline static const int SMALLEST_PRIME_NUMBER = 2;

    int maxValue{};
    int inputSize{};
    int goalIndex{};

    vector<bool> primes;
    unordered_map<int, vector<int>> valueToIndexes;

public:
    int minJumps(vector<int>& input) {
        inputSize = input.size();
        goalIndex = input.size() - 1;
        maxValue = *ranges::max_element(input);

        primes = createSieveOfEratosthenes();
        valueToIndexes = createMapValueToIndexes(input);

        return findMinJumpsFromStartToGoal(input);
    }

private:
    int findMinJumpsFromStartToGoal(span<const int> input) {
        queue<int> queue;
        queue.push(START_INDEX);

        vector<bool> visited(inputSize);
        visited[START_INDEX] = true;

        bool goalReached = false;
        int minJumpsFromStartToGoal = 0;

        while (!queue.empty()) {
            int currentRoundOfSteps = queue.size();

            while (currentRoundOfSteps > 0) {
                int index = queue.front();
                queue.pop();

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

    vector<bool> createSieveOfEratosthenes() {
        vector<bool> primes(maxValue + 1, true);
        for (int value = 0; value < min(maxValue, SMALLEST_PRIME_NUMBER); ++value) {
            primes[value] = false;
        }

        int upperLimit = sqrt(maxValue);
        for (int value = SMALLEST_PRIME_NUMBER; value <= upperLimit; ++value) {
            if (!primes[value]) {
                continue;
            }

            long long nextValue = static_cast<long long>(value) * value;
            while (nextValue <= maxValue) {
                primes[nextValue] = false;
                nextValue += value;
            }
        }
        return primes;
    }

    unordered_map<int, vector<int>> createMapValueToIndexes(span<const int> input) {
        unordered_map<int, vector<int>> valueToIndexes;
        for (int i = 0; i < inputSize; ++i) {
            valueToIndexes[input[i]].push_back(i);
        }
        return valueToIndexes;
    }

    void handleJumpToAdjacentIndexes(int index, queue<int>& queue, vector<bool>& visited) {
        if (index - 1 >= 0 && !visited[index - 1]) {
            queue.push(index - 1);
            visited[index - 1] = true;
        }

        if (index + 1 < inputSize && !visited[index + 1]) {
            queue.push(index + 1);
            visited[index + 1] = true;
        }
    }

    void handlePrimeTeleportation(int index, span<const int> input, queue<int>& queue, vector<bool>& visited) {
        if (!primes[input[index]]) {
            return;
        }

        int currentValue = input[index];
        while (currentValue <= maxValue) {

            if (valueToIndexes.contains(currentValue)) {
                vector<int>& indexes = valueToIndexes[currentValue];

                for (const auto& i : indexes) {
                    if (!visited[i]) {
                        queue.push(i);
                        visited[i] = true;
                    }
                }
                valueToIndexes.erase(currentValue);
            }
            currentValue += input[index];
        }
    }
};
