package com.example.liftmelib

interface RequestHandler {
  fun onNewRequest(request: LiftRequest)
}