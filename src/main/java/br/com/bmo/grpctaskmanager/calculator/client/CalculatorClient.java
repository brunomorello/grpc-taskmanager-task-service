package br.com.bmo.grpctaskmanager.calculator.client;

import br.com.bmo.proto.calculator.Calculator;
import br.com.bmo.proto.calculator.CalculatorServiceGrpc;
import br.com.bmo.proto.calculator.SumRequest;
import br.com.bmo.proto.calculator.SumResponse;
import br.com.bmo.proto.greet.Greeting;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class CalculatorClient {
    public static void main(String[] args) {
        System.out.println("Calculator - gRPC Client");

        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50052)
                .usePlaintext().build();

        CalculatorServiceGrpc.CalculatorServiceBlockingStub stub = CalculatorServiceGrpc.newBlockingStub(channel);

        SumRequest request = SumRequest.newBuilder()
                .setFirstNumber(5)
                .setSecondNumber(5)
                .build();
        SumResponse response = stub.sum(request);

        System.out.println(request.getFirstNumber() + "+" + request.getSecondNumber() + " = " + response.getSumResult());

        channel.shutdown();
    }
}
