package net.dustley.crystal.api.iterator
import org.joml.Vector2i
import kotlin.math.abs

class SpiralIterator private constructor(
private val startPosition: Vector2i,
private val condition: (Int, Vector2i) -> Boolean
) : Iterator<Vector2i> {

    private var steps = 0
    private var directionIndex = 0
    private var stepChangeCount = 0
    private var currentPosition = Vector2i(0, 0) // Start at the center (0,0)
    private var stepSize = 1
    private var currentStepInDirection = 0

    private val directions = listOf(
        Vector2i(0, 1),  // right
        Vector2i(1, 0),  // down
        Vector2i(0, -1), // left
        Vector2i(-1, 0)  // up
    )

    init {
        // Calculate the initial state for starting at startPosition
        val layer = maxOf(abs(startPosition.x), abs(startPosition.y))
        stepSize = 2 * layer
        stepChangeCount = 2 * layer - 1
        directionIndex = when {
            startPosition.y == layer -> 0 // Right
            startPosition.x == layer -> 1 // Down
            startPosition.y == -layer -> 2 // Left
            else -> 3 // Up
        }
        currentStepInDirection = when (directionIndex) {
            0 -> layer + startPosition.x
            1 -> layer + startPosition.y
            2 -> layer - startPosition.x
            else -> layer - startPosition.y
        }
        currentPosition = Vector2i(startPosition)
    }

    override fun hasNext(): Boolean {
        return condition(steps, currentPosition)
    }

    override fun next(): Vector2i {
        if (!hasNext()) throw NoSuchElementException("No more elements in the iterator")

        val result = Vector2i(currentPosition)

        if (currentStepInDirection < stepSize) {
            currentStepInDirection++
        } else {
            directionIndex = (directionIndex + 1) % 4
            currentStepInDirection = 1
            stepChangeCount++

            if (stepChangeCount % 2 == 0) {
                stepSize++
            }
        }

        currentPosition.add(directions[directionIndex])

        steps++
        return result
    }

    companion object {

        fun fromStartToEnd(start: Vector2i, end: Vector2i): SpiralIterator {
            return SpiralIterator(start) { _, pos -> !(pos.x == end.x && pos.y == end.y) }
        }

        fun fromStartWithMaxCount(start: Vector2i, maxCount: Int): SpiralIterator {
            return SpiralIterator(start) { steps, _ -> steps < maxCount }
        }

        fun fromStartOnly(start: Vector2i): SpiralIterator {
            return SpiralIterator(start) { _, _ -> true }
        }

        fun fromStartWithCondition(start: Vector2i, condition: (Vector2i) -> Boolean): SpiralIterator {
            return SpiralIterator(start) { _, pos -> condition(pos) }
        }
    }
}

// TESTING
fun main() {
    // Iterator from start to end position
    println("iterator1")
    val iterator1 = SpiralIterator.fromStartToEnd(Vector2i(0, 0), Vector2i(2, 2))
    var iterator1Steps = 0
    while (iterator1.hasNext() && iterator1Steps <= 1024) {
        println(iterator1.next())
        iterator1Steps++
    }

    // Iterator with start position and max count
    println("iterator2")
    val iterator2 = SpiralIterator.fromStartWithMaxCount(Vector2i(0, 0), 10)
    var iterator2Steps = 0
    while (iterator2.hasNext() && iterator2Steps <= 1024) {
        println(iterator2.next())
        iterator2Steps++
    }

    // Iterator with start position only (infinite)
    println("iterator3")
    val iterator3 = SpiralIterator.fromStartOnly(Vector2i(1, 1))
    repeat(15) {
        println(iterator3.next())
    }

    // Iterator with custom condition
    println("iterator4")
    var iterator4Steps = 0
    val iterator4 = SpiralIterator.fromStartWithCondition(Vector2i(0, 0)) { pos ->
        pos.x >= 0 && pos.y >= 0 && pos.x + pos.y <= 4
    }
    while (iterator4.hasNext() && iterator4Steps <= 1024) {
        println(iterator4.next())
        iterator4Steps++
    }
}
