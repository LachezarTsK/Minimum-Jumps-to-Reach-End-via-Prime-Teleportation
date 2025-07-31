
package main
import (
    "math"
    "slices"
)

const START_INDEX = 0
const SMALLEST_PRIME_NUMBER = 2

var maxValue int = 0
var inputSize int = 0
var goalIndex int = 0

var primes []bool
var valueToIndexes map[int][]int

func minJumps(input []int) int {
    inputSize = len(input)
    goalIndex = len(input) - 1
    maxValue = slices.Max(input)

    primes = createSieveOfEratosthenes()
    valueToIndexes = createMapValueToIndexes(input)

    return findMinJumpsFromStartToGoal(input)
}

func findMinJumpsFromStartToGoal(input []int) int {
    queue := make([]int, 0, inputSize)
    queue = append(queue, START_INDEX)

    visited := make([]bool, inputSize)
    visited[START_INDEX] = true

    goalReached := false
    minJumpsFromStartToGoal := 0

    for len(queue) > 0 {
        currentRoundOfSteps := len(queue)

        for currentRoundOfSteps > 0 {
            index := queue[0]
            queue = queue[1:]
            if index == goalIndex {
                goalReached = true
                break
            }
            handleJumpToAdjacentIndexes(index, &queue, visited)
            handlePrimeTeleportation(index, input, &queue, visited)
            currentRoundOfSteps--
        }

        if goalReached {
            break
        }
        minJumpsFromStartToGoal++
    }

    return minJumpsFromStartToGoal
}

func createSieveOfEratosthenes() []bool {
    primes := make([]bool, maxValue + 1)
    for i := range primes {
        primes[i] = true
    }

    for value := range min(maxValue, SMALLEST_PRIME_NUMBER) {
        primes[value] = false
    }

    upperLimit := int(math.Sqrt(float64(maxValue)))
    for value := SMALLEST_PRIME_NUMBER; value <= upperLimit; value++ {
        if !primes[value] {
            continue
        }

        nextValue := int64(value) * int64(value)
        for nextValue <= int64(maxValue) {
            primes[nextValue] = false
            nextValue += int64(value)
        }
    }
    return primes
}

func createMapValueToIndexes(input []int) map[int][]int {
    valueToIndexes := map[int][]int{}
    for i := range inputSize {
        if _, has := valueToIndexes[input[i]]; !has {
            valueToIndexes[input[i]] = []int{}
        }

        valueToIndexes[input[i]] = append(valueToIndexes[input[i]], i)
    }
    return valueToIndexes
}

func handleJumpToAdjacentIndexes(index int, queue *[]int, visited []bool) {
    if index - 1 >= 0 && !visited[index - 1] {
        *queue = append(*queue, (index - 1))
        visited[index - 1] = true
    }

    if index+1 < inputSize && !visited[index + 1] {
        *queue = append(*queue, (index + 1))
        visited[index + 1] = true
    }
}

func handlePrimeTeleportation(index int, input []int, queue *[]int, visited []bool) {
    if !primes[input[index]] {
        return
    }

    currentValue := input[index]
    for currentValue <= maxValue {

        if _, has := valueToIndexes[currentValue]; has {
            indexes := valueToIndexes[currentValue]

            for _, i := range indexes {
                if !visited[i] {
                    *queue = append(*queue, i)
                    visited[i] = true
                }
            }
            delete(valueToIndexes, currentValue)
        }
        currentValue += input[index]
    }
}
