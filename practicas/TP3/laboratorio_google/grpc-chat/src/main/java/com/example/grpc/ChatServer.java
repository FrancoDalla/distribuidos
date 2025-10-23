package com.example.grpc;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import java.io.IOException;

public class ChatServer {
	private static final int SERVER_PORT = 9090;
	
	public static void main (String[] args) throws InterruptedException, IOException{
		
		Server server = ServerBuilder.forPort(SERVER_PORT)
				.addService(new ChatServiceImpl())
				.build();
		
		System.out.println("Comenzando en el puerto: " + SERVER_PORT);
		server.start();
		
		System.out.println("Server iniciado");
		server.awaitTermination();
	}
}
