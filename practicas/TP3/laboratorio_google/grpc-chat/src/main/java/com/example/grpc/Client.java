package com.example.grpc;

import io.grpc.*;
import io.grpc.stub.*;

public class Client {
	public static void main(String[] args) throws Exception{
		int intentos = 5;
		int retryDelay = 1000;
		
		for(int intento = 1; intento <= intentos; intento++) {
			if(connectToServer()) {
				return;
			}
			if(intento < intentos) {
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
			ConnectionServiceGrpc.ConnectionServiceStub stub = ConnectionServiceGrpc.newStub(channel);
			
			ChatServer.ConnectionRequest request = 
					com.example.grpc.ChatServer.ConnectionRequest.newBuilder()
						.setAuthor("carlos")
						.build();

			
			stub.connection(request, new StreamObserver<ConnectionServiceOuterClass.connection>() {
				public void onNext(ConnectionServiceOuterClass.ServerMessage serverMessage) {
					System.out.println("Te conectaste: " + serverMessage);
					success[0] = true;
					synchronized (lock) {
						lock.notifyAll();
					}
					channel.shutdown();
				}
				
				public void onError(Throwable t) {
					System.err.println("Error: "+ t.getMessage());
					synchronized(lock) {
						lock.notifyAll();
					}
				}
				
				public void onCompleted() {
					success[0] = true;
					synchronized(lock) {
						lock.notifyAll();
					}
					channel.shutdownNow();
				}
			});
			
			synchronized(lock) {
				try {
					lock.wait(5000);
				}catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
			}
			
			return success[0];
		} finally {
			if(!success[0]) {
				channel.shutdownNow();
			}
		}
	}
}
