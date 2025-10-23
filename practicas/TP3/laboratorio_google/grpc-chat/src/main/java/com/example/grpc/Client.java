package com.example.grpc;
import com.google.protobuf.Timestamp;
import com.example.grpc.ChatServiceGrpc;
import com.example.grpc.ChatServiceOuterClass;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;


import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class Client {

    private static final String HOST = "localhost";
    private static final int PORT = 9090;


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        setupAndShowPrimaryStage(primaryStage);

        // Create a channel
        ManagedChannel channel = ManagedChannelBuilder.forAddress(HOST, PORT)
                .usePlaintext()
                .build();

        // Create an async stub with the channel
        ChatServiceGrpc.ChatServiceStub chatService = ChatServiceGrpc.newStub(channel);

        // Open a connection to the server
        StreamObserver<ChatServiceOuterClass.Message> chat =
                chatService.sendChatMessage(new StreamObserver<ChatServiceOuterClass.ServerMessage>() {

            // Handler for messages from the server
            @Override
            public void onNext(ChatServiceOuterClass.ServerMessage value) {
                // Display the message
            	
            }

            private LocalDateTime getMessageTimestampAsLocalDateTime(ChatServiceOuterClass.ServerMessage value) {
                Timestamp timestamp = value.getTimestamp();
                long timestampSeconds = timestamp.getSeconds();
                Instant messageTimestampAsInstant = Instant.ofEpochSecond(timestampSeconds);
                return LocalDateTime.ofInstant(messageTimestampAsInstant, ZoneOffset.UTC);
            }


            @Override
            public void onError(Throwable t) {
                System.out.println("Disconnected due to error: " + t.getMessage());
            }

            @Override
            public void onCompleted() {
                System.out.println("Disconnected");
            }
        });

    private void setupAndShowPrimaryStage(Stage primaryStage) {
        messagesView.setItems(messages);

        send.setText("Send");

        BorderPane pane = new BorderPane();
        pane.setLeft(name);
        pane.setCenter(message);
        pane.setRight(send);

        BorderPane root = new BorderPane();
        root.setCenter(messagesView);
        root.setBottom(pane);

        primaryStage.setTitle("gRPC Chat");
        primaryStage.setScene(new Scene(root, 480, 320));

        primaryStage.show();
    }
}
