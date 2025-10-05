package com.example.grpc;

import io.grpc.*;
import io.grpc.stub.*;

public class Client
{
    public static void main( String[] args ) throws Exception
    {
      int intentos = 5;
      int retryDelay = 1000;
      
      for(int intento = 1; intento <= intentos; intento ++) {
          if (connectToServer()) {
              return; // 
          }
          
          if (intento < intentos) {
              Thread.sleep(retryDelay);
          }
      }
    }
    
    private static boolean connectToServer() {
        final ManagedChannel channel = ManagedChannelBuilder.forTarget("localhost:8080")
            .usePlaintext()
            .build();
        
        final boolean[] success = {false};
        final Object lock = new Object();
        
        try {
            GreetingServiceGrpc.GreetingServiceStub stub = GreetingServiceGrpc.newStub(channel);
            
            GreetingServiceOuterClass.HelloRequest request =
                GreetingServiceOuterClass.HelloRequest.newBuilder()
                    .setName("Ray")
                    .build();
            
            stub.greeting(request, new StreamObserver<GreetingServiceOuterClass.HelloResponse>() {
                public void onNext(GreetingServiceOuterClass.HelloResponse response) {
                    System.out.println("Success: " + response);
                    success[0] = true;
                    synchronized (lock) {
                        lock.notifyAll();
                    }
                    channel.shutdownNow();
                }
                
                public void onError(Throwable t) {
                    System.err.println("Connection error: " + t.getMessage());
                    synchronized (lock) {
                        lock.notifyAll();
                    }
                }
                
                public void onCompleted() {
                    success[0] = true;
                    synchronized (lock) {
                        lock.notifyAll();
                    }
                    channel.shutdownNow();
                }
            });
            
            // Esperar por la respuesta (máximo 5 segundos)
            synchronized (lock) {
                try {
                    lock.wait(5000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            
            return success[0];
            
        } finally {
            if (!success[0]) {
                channel.shutdownNow();
            }
        }
    }
}