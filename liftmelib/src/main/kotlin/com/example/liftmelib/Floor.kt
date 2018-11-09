package com.example.liftmelib

class Floor(
  floor: Int,
  requestHandler : RequestHandler) {

  val upButton: FloorButton = FloorButton(floor, Direction.UP, requestHandler)
  val downButton: FloorButton = FloorButton(floor, Direction.DOWN, requestHandler)
}