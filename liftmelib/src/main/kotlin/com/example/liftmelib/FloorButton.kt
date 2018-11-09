package com.example.liftmelib

class FloorButton(floor: Int, val direction: Direction, requestHandler: RequestHandler) : Button(floor, requestHandler) {

  override fun createRequest(): LiftRequest {
    return EnterRequest(floor, direction, this)
  }
}