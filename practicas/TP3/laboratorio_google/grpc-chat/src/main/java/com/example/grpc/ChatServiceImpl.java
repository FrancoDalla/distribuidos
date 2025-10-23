package com.example.grpc;


import com.google.protobuf.Timestamp;
import io.grpc.stub.StreamObserver;
import com.example.grpc.ChatServiceGrpc;
import com.example.grpc.ChatServiceOuterClass;

import java.util.LinkedHashSet;

public class ChatServiceImpl extends ChatServiceGrpc.ChatServiceImplBase {
	private static LinkedHashSet<StreamObserver<ChatServiceOuterClass.ServerMessage>>
		observers = new LinkedHashSet<>();
	

	public StreamObserver<ChatServiceOuterClass.Message> sendChatMessage(StreamObserver<ChatServiceOuterClass.ServerMessage> responseObserver){
		observers.add(responseObserver);
		
		return new StreamObserver<ChatServiceOuterClass.Message>() {
			
			@Override
			public void onNext(ChatServiceOuterClass.Message value) {
				System.out.println(value);
				
				Timestamp timestamp = Timestamp.newBuilder()
						.setSeconds(System.currentTimeMillis())
						.build();
				
				ChatServiceOuterClass.ServerMessage message = ChatServiceOuterClass.ServerMessage
						.newBuilder()
						.setMessage(value)
						.setTimestamp(timestamp)
						.build();
				
				for(StreamObserver<ChatServiceOuterClass.ServerMessage> observer : observers) {
					System.out.println("Enviado a : " + observer.toString());
					observer.onNext(message);
				}
			}
			
			@Override
			public void onError(Throwable t) {
				t.getMessage();
				observers.remove(responseObserver);
			}
			
			public void onCompleted() {
				System.out.println("Completao'");
			}
		};
	}
}
