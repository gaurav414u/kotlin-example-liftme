# kotlin-example-liftme
A scratch kotlin app which simulates a Lift system using Kotlin

## Design

### Overview
- The primary classes are `LiftCar` and `LiftController`. The LiftController manages all the LiftCars and the Floors.

- Every `Floor` has an up FloorButton and a down FloorButton.

- Every `LiftCar` has an array of `LiftButton`s.

- `LiftButton` and `FloorButton` are the places from where a `LiftRequest` originates.

- `RequestHandler` decides how to handle the requests originating from the liftbuttons and floorbuttons and which lift to assign that request to.

### Details

### LiftController
Initialises LiftCars and RequestHandler and takes care of the tick of the system.

#### LiftCar
A `LiftCar` looks like this
```kotlin
interface ILiftCar {
  fun takeRequest(request: LiftRequest)
  fun tick()
  fun getCurrentFloor() : Float
}
```
`RequestHandler` assigns the request to a lift using `takeRequest()`.

### RequestHandler

There can be many types of RequestHandler, for now we have SimpleRequestHandler which assigns every request to the first LiftCar.
