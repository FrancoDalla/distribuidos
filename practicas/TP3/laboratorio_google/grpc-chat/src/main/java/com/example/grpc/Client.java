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
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;

public class Client {
    private ManagedChannel channel;
    private StreamObserver<ChatServiceOuterClass.Message> chatObserver;
    private final CountDownLatch finishLatch = new CountDownLatch(1);
    private volatile boolean isConnected = true;

    public static void main(String[] args) {
        new Client().start();
    }

    public void start() {
        try {
            // Configurar el canal gRPC
            channel = ManagedChannelBuilder.forAddress("localhost", 9090)
                    .usePlaintext()
                    .build();

            // Crear el stub asíncrono
            ChatServiceGrpc.ChatServiceStub chatService = ChatServiceGrpc.newStub(channel);
            
            System.out.println("Conectando al servidor de chat...");
            System.out.println("Escribe 'exit' para salir");

            // Solicitar nombre de usuario
            Scanner scanner = new Scanner(System.in);
            System.out.print("Ingresa tu nombre: ");
            String userName = scanner.nextLine();

            // Configurar el stream observer para recibir mensajes
            chatObserver = chatService.chatServiceOuterClass(new StreamObserver<Chat.ChatMessageFromServer>() {
                @Override
                public void onNext(Chat.ChatMessageFromServer value) {
                    // Mostrar mensajes entrantes en la consola
                    System.out.println(value.getMessage().getFrom() + ": " + value.getMessage().getMessage());
                }

                @Override
                public void onError(Throwable t) {
                    System.err.println("Error en la conexión: " + t.getMessage());
                    isConnected = false;
                    finishLatch.countDown();
                }

                @Override
                public void onCompleted() {
                    System.out.println("Conexión finalizada por el servidor");
                    isConnected = false;
                    finishLatch.countDown();
                }
            });

            System.out.println("Conectado! Escribe tus mensajes:");

            // Loop principal para enviar mensajes
            while (isConnected) {
                String input = scanner.nextLine();
                
                if ("exit".equalsIgnoreCase(input.trim())) {
                    break;
                }
                
                if (!input.trim().isEmpty() && isConnected) {
                    Chat.ChatMessage message = Chat.ChatMessage.newBuilder()
                            .setFrom(userName)
                            .setMessage(input)
                            .build();
                    chatObserver.onNext(message);
                }
            }

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        } finally {
            shutdown();
        }
    }

    private void shutdown() {
        System.out.println("Desconectando...");
        
        if (chatObserver != null) {
            chatObserver.onCompleted();
        }
        
        if (channel != null) {
            channel.shutdown();
            try {
                if (!channel.awaitTermination(5, TimeUnit.SECONDS)) {
                    channel.shutdownNow();
                }
            } catch (InterruptedException e) {
                channel.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
        
        finishLatch.countDown();
        System.out.println("Cliente cerrado");
    }
}


