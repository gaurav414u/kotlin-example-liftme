package com.example.liftmelib;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Main {
  public static void main(String[] args) {
    final LiftController liftManager = new LiftController.Builder(23)
        .addLiftCar()
        .build();


    // Start ticking
    Thread t = new Thread(new Runnable() {
      @Override
      public void run() {
        while (true) {
          try {
            liftManager.tick();
            Thread.sleep(10);
          } catch (InterruptedException e) {
            break;
          }
        }
      }
    });
    t.start();


    ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
    executor.schedule(new Runnable() {
      @Override
      public void run() {
        liftManager.getFloorList().get(1).getUpButton().press();
        liftManager.getFloorList().get(4).getDownButton().press();
      }
    }, 1, TimeUnit.MILLISECONDS);

    executor.schedule(new Runnable() {
      @Override
      public void run() {
        liftManager.getLiftCars().get(0).getButtonsGrid().get(2).press();
        liftManager.getLiftCars().get(0).getButtonsGrid().get(3).press();
      }
    }, 300, TimeUnit.MILLISECONDS);

  }
}
