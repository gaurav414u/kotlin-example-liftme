package com.example.liftmelib

enum class Direction {UP, DOWN, UNKNOWN}

/**
 * LiftController implementation
 */
class LiftController private constructor(
  private val floors: Int,
  val liftCars: List<LiftCar>,
  private val requestHandler: RequestHandler
) : ILiftController {

  val floorList: MutableList<Floor> = mutableListOf()

  init {
    for (i in 0 until this.floors) {
      val floor = Floor(i, this.requestHandler)
      floorList.add(floor)
    }
  }

  /**
   * Builder to create {@link LiftController}
   */
  class Builder(private val floors: Int) {
    private val liftCars: MutableList<LiftCar> = mutableListOf()
    private val requestHandler : RequestHandler = SimpleRequestHandler(liftCars)

    fun addLiftCar() : Builder {
      this.liftCars.add(LiftCar(this.floors, requestHandler))
      return this
    }

    fun build() : LiftController {
      return LiftController(this.floors, this.liftCars.toList(), requestHandler)
    }
  }

  /**
   * Ticks all the components
   */
  override fun tick() {
    this.liftCars[0].tick()
  }
}

