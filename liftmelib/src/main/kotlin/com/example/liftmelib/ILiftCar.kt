package com.example.liftmelib

interface ILiftCar {
  fun takeRequest(request: LiftRequest)
  fun tick()
  fun getCurrentFloor() : Float
}