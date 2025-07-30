package com.elhg;


import lombok.extern.java.Log;

@Log
public class Main {
     public static void main(String[] args) {
         //Publishers
         final ReactiveStream<String> streamString = new ReactiveStream<>();
         final ReactiveStream<Integer> streamInteger = new ReactiveStream<>();

         //Subscribers
         final Subscriber<String> subscriber1 = new SubscriberImpl<String, String>(String::toUpperCase, "Subscriber 1");
         final Subscriber<String> subscriber2 = new SubscriberImpl<String, String>(String::toLowerCase, "Subscriber 2");
         final Subscriber<Integer> subscriber3 = new SubscriberImpl<Integer, String>(i -> "Number: " + i, "Subscriber 3");
         final Subscriber<Integer> subscriber4 = new SubscriberImpl<Integer, String>(i -> "Square: " + (i * i), "Subscriber 4");


         //Subscription
         streamString.subscribe(subscriber1)
                 .subscribe(subscriber2);

         streamInteger.subscribe(subscriber3)
                 .subscribe(subscriber4);

         //Emitting values
         streamString.emit("Hello");
         streamString.emit("World");
         streamInteger.emit(5);
         streamInteger.emit(10);

         //Unsubscribe
         streamString.unsubscribe(subscriber1);
         streamInteger.unsubscribe(subscriber3);

     //log.info("End of Reactive Stream Example");
    }
}