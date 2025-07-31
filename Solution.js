
// const {Queue} = require('@datastructures-js/queue');
/*
 Queue is internally included in the solution file on leetcode.
 When running the code on leetcode it should stay commented out. 
 It is mentioned here just for information about the external library 
 that is applied for this data structure.
 */

/**
 * @param {number[]} input
 * @return {number}
 */
var minJumps = function (input) {
    const util = new Util(input);
    return findMinJumpsFromStartToGoal(input, util);
};

/**
 * @param {number[]} input
 * @param {Util} util 
 * @return {number}
 */
function findMinJumpsFromStartToGoal(input, util) {
    const queue = new Queue();
    queue.enqueue(Util.START_INDEX);

    const visited = new Array(util.inputSize).fill(false);
    visited[Util.START_INDEX] = true;

    let goalReached = false;
    let minJumpsFromStartToGoal = 0;

    while (!queue.isEmpty()) {
        let currentRoundOfSteps = queue.size();

        while (currentRoundOfSteps > 0) {
            const index = queue.dequeue();
            if (index === util.goalIndex) {
                goalReached = true;
                break;
            }
            handleJumpToAdjacentIndexes(index, queue, visited, util);
            handlePrimeTeleportation(index, input, queue, visited, util);
            --currentRoundOfSteps;
        }

        if (goalReached) {
            break;
        }
        ++minJumpsFromStartToGoal;
    }

    return minJumpsFromStartToGoal;
}

/**
 * @param {number} index
 * @param {Queue<number>} queue
 * @param {boolean[]} visited  
 * @param {Util} util 
 * @return {void}
 */
function  handleJumpToAdjacentIndexes(index, queue, visited, util) {
    if (index - 1 >= 0 && !visited[index - 1]) {
        queue.enqueue(index - 1);
        visited[index - 1] = true;
    }

    if (index + 1 < util.inputSize && !visited[index + 1]) {
        queue.enqueue(index + 1);
        visited[index + 1] = true;
    }
}

/**
 * @param {number} index
 * @param {number[]} input 
 * @param {Queue<number>} queue
 * @param {boolean[]} visited  
 * @param {Util} util 
 * @return {void}
 */
function handlePrimeTeleportation(index, input, queue, visited, util) {
    if (!util.primes[input[index]]) {
        return;
    }

    let currentValue = input[index];
    while (currentValue <= util.maxValue) {

        if (util.valueToIndexes.has(currentValue)) {
            const indexes = util.valueToIndexes.get(currentValue);

            for (let i of indexes) {
                if (!visited[i]) {
                    queue.enqueue(i);
                    visited[i] = true;
                }
            }
            util.valueToIndexes.delete(currentValue);
        }
        currentValue += input[index];
    }
}

class Util {

    static  START_INDEX = 0;
    static SMALLEST_PRIME_NUMBER = 2;

    /**
     * @param {number[]} input
     */
    constructor(input) {
        this.inputSize = input.length;
        this.goalIndex = input.length - 1;
        this.maxValue = Math.max(...input);

        this.primes = this.createSieveOfEratosthenes();
        this.valueToIndexes = this.createMapValueToIndexes(input);
    }

    /**
     * @return {boolean[]}
     */
    createSieveOfEratosthenes() {
        const primes = new Array(this.maxValue + 1).fill(true);
        for (let value = 0; value < Math.min(this.maxValue, Util.SMALLEST_PRIME_NUMBER); ++value) {
            primes[value] = false;
        }

        const upperLimit = Math.sqrt(this.maxValue);
        for (let value = Util.SMALLEST_PRIME_NUMBER; value <= upperLimit; ++value) {
            if (!primes[value]) {
                continue;
            }

            let nextValue = value * value;
            while (nextValue <= this.maxValue) {
                primes[nextValue] = false;
                nextValue += value;
            }
        }
        return primes;
    }

    /**
     * @param {number[]} input
     * @return {Map<number, Array<number>>}
     */
    createMapValueToIndexes(input) {
        const valueToIndexes = new Map();
        for (let i = 0; i < this.inputSize; ++i) {
            if (!valueToIndexes.has(input[i])) {
                valueToIndexes.set(input[i], new Array());
            }
            valueToIndexes.get(input[i]).push(i);
        }
        return valueToIndexes;
    }
}
