package com.example.liftmelib

class SimpleRequestHandler(private val liftCars: List<LiftCar>? = null) : RequestHandler {
  /**
   * Handles the request originating from floors and elevators
   *
   * @param request A new request object that needs to be processed
   */
  override fun onNewRequest(request: LiftRequest) {
    // Process the request and assign to liftCars using some logic
    // For now directing all the requests to 1st LiftCar
    if (this.liftCars != null) {
      this.liftCars[0].takeRequest(request)
    }
  }
}