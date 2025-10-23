package com.example.grpc;

public class HelloWorldClient {
	public void greet(String name) {
		HelloRequest request = HelloRequest.newBuilder().setName(name).build();
		HelloReply response;
		
		try {
			response = blockingStub.sayHello(request);
		} catch (StatusRuntimeException e) {
			return;
		}
		
		try {
			response = blockingStub.sayHelloAgain(request);
		} catch (StatusRuntimeException e) {
			return;
		}
	}
}
