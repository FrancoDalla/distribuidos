package com.example.grpc;

import io.grpc.*;

import java.io.IOException;

public class App 
{
    public static void main( String[] args ) throws Exception
    {
    	Server server = ServerBuilder.forPort(8080)
    			.addService(new ChatServiceImpl())
    			.build();
    	
    	server.start();
    	
    	System.out.println("Server started");
    	
    	server.awaitTermination();
    	
    }
}
