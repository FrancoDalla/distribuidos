package com.example.grpc;

import io.grpc.*;

public class App 
{
    public static void main( String[] args ) throws Exception
    {
    	Server server = ServerBuilder.forPort(8080)
    			.addService(new GreetingServiceImpl())
    			.build();
    	
    	server.start();
    	
    	System.out.println("Server started");
    	
    	server.awaitTermination();
    	
    }
}
