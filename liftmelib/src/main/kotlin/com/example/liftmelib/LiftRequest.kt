package com.example.liftmelib

enum class RequestState {INITIATED, COMPLETED}

abstract class LiftRequest(val floor: Int, val sourceButton: Button, var requestState: RequestState = RequestState.INITIATED) {
  fun completed() {
    this.sourceButton.buttonState = ButtonState.OFF
    this.requestState = RequestState.COMPLETED
  }
}

class EnterRequest(floor: Int, val  direction: Direction, sourceButton: Button) : LiftRequest(floor, sourceButton )
class ExitRequest(floor: Int, sourceButton: Button) : LiftRequest(floor, sourceButton)