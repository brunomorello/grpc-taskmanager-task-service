package br.com.bmo.grpctaskmanager.calculator.client;

import br.com.bmo.proto.calculator.*;
import br.com.bmo.proto.greet.Greeting;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class CalculatorClient {
    public static void main(String[] args) {
        System.out.println("Calculator - gRPC Client");

        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50052)
                .usePlaintext().build();

        CalculatorServiceGrpc.CalculatorServiceBlockingStub stub = CalculatorServiceGrpc.newBlockingStub(channel);

//        Unary
//        SumRequest request = SumRequest.newBuilder()
//                .setFirstNumber(5)
//                .setSecondNumber(5)
//                .build();
//        SumResponse response = stub.sum(request);
//        System.out.println(request.getFirstNumber() + "+" + request.getSecondNumber() + " = " + response.getSumResult());

//        Streaming Server
        Integer number = 567890304;

        stub.primeNumberDecomposition(PrimerNumberDecompositionRequest.newBuilder()
                .setNumber(number)
                .build())
                .forEachRemaining(primeNumberDecompositionResponse -> {
                    System.out.println("primefactor = " + primeNumberDecompositionResponse.getPrimeFactor());
                });

        channel.shutdown();
    }
}
