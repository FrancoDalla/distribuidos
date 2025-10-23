package com.example.grpc;

import com.google.protobuf.Timestamp;
import io.grpc.stub.StreamObserver;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


public class ChatServiceImpl  extends ChatServiceGrpc.ChatServiceImplBase{
	private static Set<StreamObserver<ChatServer.ServerMessage>> observers = ConcurrentHashMap.newKeySet();
	
	@Override
	public StreamObserver<ChatServer.Message> chat(StreamObserver<ChatServer.ServerMessage> responseObserver){
		observers.add(responseObserver);
		
		return new StreamObserver<ChatServer.Message>() {
			@Override
			public void onNext(ChatServer.Message value) {
				System.out.println(value);
				ChatServer.ServerMessage message = ChatServer.ServerMessage.newBuilder()
					.setTimestamp(Timestamp.newBuilder().setSeconds(System.currentTimeMillis() / 1000))
					.build();
				
				for (StreamObserver<ChatServer.ServerMessage> observer : observers) {
					observer.onNext(message);
				}
			}
			
			@Override
			public void onError(Throwable t) {
				
			}
			
			@Override
			public void onCompleted() {
				observers.remove(responseObserver);
			}
		};
	}

}
