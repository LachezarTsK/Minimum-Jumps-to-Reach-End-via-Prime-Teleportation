
import java.util.*
import kotlin.math.min
import kotlin.math.sqrt

class Solution {

    private companion object {
        const val START_INDEX = 0
        const val SMALLEST_PRIME_NUMBER = 2
    }

    private var maxValue: Int = 0
    private var inputSize: Int = 0
    private var goalIndex: Int = 0

    private lateinit var primes: BooleanArray
    private lateinit var valueToIndexes: MutableMap<Int, MutableList<Int>>

    fun minJumps(input: IntArray): Int {
        inputSize = input.size
        goalIndex = input.size - 1
        maxValue = input.max()

        primes = createSieveOfEratosthenes()
        valueToIndexes = createMapValueToIndexes(input)

        return findMinJumpsFromStartToGoal(input)
    }

    private fun findMinJumpsFromStartToGoal(input: IntArray): Int {
        val queue = LinkedList<Int>()
        queue.add(START_INDEX)

        val visited = BooleanArray(inputSize)
        visited[START_INDEX] = true

        var goalReached = false
        var minJumpsFromStartToGoal = 0

        while (!queue.isEmpty()) {
            var currentRoundOfSteps = queue.size

            while (currentRoundOfSteps > 0) {
                val index = queue.poll()
                if (index == goalIndex) {
                    goalReached = true
                    break
                }
                handleJumpToAdjacentIndexes(index, queue, visited)
                handlePrimeTeleportation(index, input, queue, visited)
                --currentRoundOfSteps
            }

            if (goalReached) {
                break
            }
            ++minJumpsFromStartToGoal
        }

        return minJumpsFromStartToGoal
    }

    private fun createSieveOfEratosthenes(): BooleanArray {
        val primes = BooleanArray(maxValue + 1) { true }
        for (value in 0..<min(maxValue, SMALLEST_PRIME_NUMBER)) {
            primes[value] = false
        }

        val upperLimit = sqrt(maxValue.toDouble()).toInt()
        for (value in SMALLEST_PRIME_NUMBER..upperLimit) {
            if (!primes[value]) {
                continue
            }

            var nextValue: Long = value.toLong() * value
            while (nextValue <= maxValue) {
                primes[nextValue.toInt()] = false
                nextValue += value
            }
        }
        return primes
    }

    private fun createMapValueToIndexes(input: IntArray): MutableMap<Int, MutableList<Int>> {
        val valueToIndexes = mutableMapOf<Int, MutableList<Int>>()
        for (i in 0..<inputSize) {
            valueToIndexes.putIfAbsent(input[i], mutableListOf())
            valueToIndexes[input[i]]!!.add(i)
        }
        return valueToIndexes
    }

    private fun handleJumpToAdjacentIndexes(index: Int, queue: Queue<Int>, visited: BooleanArray) {
        if (index - 1 >= 0 && !visited[index - 1]) {
            queue.add(index - 1)
            visited[index - 1] = true
        }

        if (index + 1 < inputSize && !visited[index + 1]) {
            queue.add(index + 1)
            visited[index + 1] = true
        }
    }

    private fun handlePrimeTeleportation(index: Int, input: IntArray, queue: Queue<Int>, visited: BooleanArray) {
        if (!primes[input[index]]) {
            return
        }

        var currentValue = input[index]
        while (currentValue <= maxValue) {

            if (valueToIndexes.containsKey(currentValue)) {
                val indexes: MutableList<Int> = valueToIndexes.remove(currentValue)!!

                for (i in indexes) {
                    if (!visited[i]) {
                        queue.add(i)
                        visited[i] = true
                    }
                }
            }
            currentValue += input[index]
        }
    }
}
