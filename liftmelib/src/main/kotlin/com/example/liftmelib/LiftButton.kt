package com.example.liftmelib

class LiftButton: Button {
  constructor(floor: Int, requestHandler: RequestHandler) :super(floor, requestHandler)

  override fun createRequest(): LiftRequest {
    return ExitRequest(floor, this)
  }
}
