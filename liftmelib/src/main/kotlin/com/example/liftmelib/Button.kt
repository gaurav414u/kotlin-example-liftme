package com.example.liftmelib

enum class ButtonState {ON, OFF}

abstract class Button(
  val floor: Int,
  private var requestHandler: RequestHandler,
  var buttonState : ButtonState = ButtonState.OFF) {

  abstract fun createRequest() : LiftRequest

  open fun press() {
    if (buttonState == ButtonState.ON)  {
      // Do nothing
      return
    }
    buttonState = ButtonState.ON

    val request = createRequest()
    this.requestHandler.onNewRequest(request)
  }

}