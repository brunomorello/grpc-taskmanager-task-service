package br.com.bmo.grpctaskmanager.calculator.server;

import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.File;
import java.io.IOException;

public class CalculatorServer {
    public static void main(String[] args) throws IOException, InterruptedException {

        // plain text server
//        Server server = ServerBuilder.forPort(50052)
//                .addService(new CalculatorServiceImpl())
//                .build();

        Server server = ServerBuilder.forPort(50052)
                        .addService(new CalculatorServiceImpl())
                        .useTransportSecurity(
                                new File("ssl/server.crt"),
                                new File("ssl/server.pem")
                        )
                        .build();

        server.start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Received Shutdown Request");
            server.shutdown();
            System.out.println("Successfully Stopped Server");
        }));

        server.awaitTermination();
    }
}
