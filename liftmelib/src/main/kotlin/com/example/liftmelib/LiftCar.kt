package com.example.liftmelib

import java.lang.IllegalArgumentException
import java.lang.IllegalStateException

enum class LiftState {MOVING,STOPPED }
enum class DoorState {CLOSED,OPEN,CLOSING,OPENING }

class LiftCar : ILiftCar {
  val buttonsGrid: ArrayList<LiftButton>
  private val floors: Int
  private var requestHandler: RequestHandler
  private var position: Int
  private var doorState = DoorState.CLOSED
  private var state = LiftState.STOPPED
  private var direction = Direction.UNKNOWN
  private val requestMap: Array<MutableList<LiftRequest>>
  private var tl: Int = 0  /** target lower */
  private var tu: Int = 0  /** target upper */
  private var stopTime = 5

  constructor(floors: Int, requestHandler: RequestHandler, position: Int = 0) {
    this.floors = floors
    this.requestHandler = requestHandler
    this.position = position
    this.requestMap = Array(floors) { mutableListOf<LiftRequest>() }
    this.buttonsGrid = arrayListOf<LiftButton>()
    for (i in 0 until this.floors) {
      val newButton = LiftButton(i, this.requestHandler)
      buttonsGrid.add(newButton)

      // Initialise requestMap for every floor
      requestMap[i] = mutableListOf()
    }
  }

  @Synchronized
  override fun takeRequest(request: LiftRequest) {
    requestMap[request.floor].add(request)

    if (request is EnterRequest) {
      // case I, the lift is at rest and serving no request
      // set the direction, in the direction of the request, if on the same floor
      // else
      if (state == LiftState.STOPPED && direction == Direction.UNKNOWN) {
        if (position == request.floor * 10) {
          direction = request.direction
        } else {
          direction = getDirectionOfFloor(request.floor)
        }
      }
    }

    // Modify edges, whenever new request comes in
    if (request.floor > tu) {
      tu = request.floor
    } else if (request.floor < tl) {
      tl = request.floor
    }
  }

  @Synchronized
  override fun tick() {
    if (state == LiftState.MOVING) {
      if (direction == Direction.UP) {
        this.position += 1
      }
      if (direction == Direction.DOWN) {
        this.position -= 1
      }
      if (needToBeStopped()) {
        stopLiftAndMarkRequests()
        System.out.println("door open")
        doorState = DoorState.OPEN
        // Get new direction
        this.direction = getNewDirectionWhenStopped()
      }
    }

    System.out.println("Lift at $position")

    if (state == LiftState.STOPPED) {
      stopTime -= 1
      if (stopTime <= 0) {
        // Keep retrying if can't start moving
        val newDirection = getDirection()
        if (newDirection == Direction.UNKNOWN) {
          stopTime = 1
        } else {
          // Start moving
          invalidateEdge(newDirection)
          startMoving(newDirection)
          doorState = DoorState.CLOSED
          System.out.println("door closed")
        }
      }
    }
  }

  override fun getCurrentFloor(): Float {
    return position.toFloat()/10
  }

  /**
   * Assumes that the LiftCar is stopped right now
   * @return {@link Direction.UNKNOWN} if the lift is at the edge, it was going to
   */
  private fun getNewDirectionWhenStopped() = when (direction) {
    Direction.UP -> if (tu * 10 == position) Direction.UNKNOWN else direction
    Direction.DOWN -> if (tl * 10 == position) Direction.UNKNOWN else direction
    else -> throw IllegalStateException("Direction can't be unkown when getting a new direction")
  }

  private fun invalidateEdge(direction: Direction) {
    if (direction == Direction.DOWN) {
      for (floor in floors-1 downTo 0) {
        if (requestMap[floor].size > 0) {
          tl = floor
          return
        }
      }
    }

    if (direction == Direction.UP) {
      for (floor in 0 until floors) {
        if (requestMap[floor].size > 0) {
          tu = floor
        }
      }
    }
  }

  /**
   * Will try to move into the direction if its decided
   *  else will pick a target floor and move towards it
   * @return true if started moving else false
   */
  private fun getDirection() : Direction {
    // If the direction is known, then start moving in that direction
    if (direction != Direction.UNKNOWN) {
      return direction
    }

    // The direction is unkown
    // Select a request to be processed and calculate the direction
    for (floor in floors-1 downTo 0) {
      if (requestMap[floor].size > 0) {
        return getDirectionOfFloor(floor)
      }
    }
    return Direction.UNKNOWN
  }

  private fun startMoving(direction: Direction) {
    if (direction == Direction.UNKNOWN) {
      throw IllegalArgumentException("Cannot move to unkown direction")
    }
    this.state = LiftState.MOVING
    this.direction = direction
    stopTime = 5
  }

  private fun getDirectionOfFloor(floor: Int) = when {
    floor * 10 < position -> Direction.DOWN
    floor * 10 > position -> Direction.UP
    else ->  Direction.UNKNOWN
  }


  private fun stopLiftAndMarkRequests() {
    state = LiftState.STOPPED
    for (req in requestMap[this.position/10]) {
      req.completed()
    }
    requestMap[this.position/10].clear()
  }

  private fun needToBeStopped() : Boolean {
    if (this.position % 10 != 0) {
      return false
    }
    if (requestMap[this.position / 10].size == 0) {
      return false
    }
    // Stop if its an edge in the direction
    if (direction == Direction.UP && position / 10 == tu) {
      return true
    }
    if (direction == Direction.DOWN && position / 10 == tl) {
      return true
    }

    for (req in requestMap[this.position / 10]) {
      if (req is EnterRequest) {
        // Stop only if in same direction
        if (req.direction == this.direction) {
          return true
        }
      }

      if (req  is ExitRequest) {
        // Stop always, if its an exit request
        return true
      }
    }
    return false
  }
}
