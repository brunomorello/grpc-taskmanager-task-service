package br.com.bmo.grpctaskmanager.taskmanager.server;

import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;

public class TaskManagerServer {
    public static void main(String[] args) throws InterruptedException, IOException {
        Server server = ServerBuilder.forPort(50051)
                .addService(new TaskManagerServiceImpl())
                .build();
        server.start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Received shutdown request");
            server.shutdown();
            System.out.println("Successfully stopped server");
        }));

        server.awaitTermination();
    }
}
