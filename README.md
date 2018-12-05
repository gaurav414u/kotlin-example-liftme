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

#### LiftController
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
- LiftCar takes care of processing the request using takeRequest() which gets called by a higher order container, which in our case is `LiftController` whenever a `LiftRequest` is raised.
- LiftCar initialises the array of floor buttons to be used in that LiftCar.


#### RequestHandler

There can be many types of RequestHandler, for now we have [SimpleRequestHandler](https://github.com/gaurav414u/kotlin-example-liftme/blob/master/liftmelib/src/main/kotlin/com/example/liftmelib/SimpleRequestHandler.kt) which assigns every request to the first LiftCar.
`RequestHandler` assigns the request to a lift by calling `liftCar.takeRequest()`.

#### Button
A Button has a `RequestHandler` object and the `floor` number for/on which this button is meant to be.

#### LiftRequest
A LiftRequest represents the request object and contains:
1. Reference to the source `LiftButton` - can be removed and instead a observer/listener pattern can be used
2. The floor number of the request
3. The RequestState

```kotlin
enum class RequestState {INITIATED, COMPLETED}
```


### Patterns used
#### Factory Method Pattern in Request creation
`Button` uses a factory method pattern to create a `LiftRequest`.

```
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

// LiftButton creates a `ExitRequest`

class LiftButton: Button {
  constructor(floor: Int, requestHandler: RequestHandler) :super(floor, requestHandler)

  override fun createRequest(): LiftRequest {
    return ExitRequest(floor, this)
  }
}

// FloorButton creates a EnterRequest

class FloorButton(floor: Int, private val direction: Direction, requestHandler: RequestHandler) : Button(floor, requestHandler) {

  override fun createRequest(): LiftRequest {
    return EnterRequest(floor, direction, this)
  }
}
```
