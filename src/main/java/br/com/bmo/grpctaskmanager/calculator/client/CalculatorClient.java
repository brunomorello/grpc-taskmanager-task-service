package br.com.bmo.grpctaskmanager.calculator.client;

import br.com.bmo.proto.calculator.*;
import br.com.bmo.proto.greet.Greeting;
import io.grpc.Channel;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class CalculatorClient {
    public static void main(String[] args) {
        System.out.println("Calculator - gRPC Client");
        CalculatorClient calcClient = new CalculatorClient();
        calcClient.run();
    }

    public void run() {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50052)
                .usePlaintext().build();

//        doUnaryCall(channel);
//        doServerStreammingCall(channel);
//        doClientStreammingCall(channel);
        doBiDiSreammingCall(channel);
        channel.shutdown();
    }

    private void doUnaryCall(Channel channel) {
        CalculatorServiceGrpc.CalculatorServiceBlockingStub stub = CalculatorServiceGrpc.newBlockingStub(channel);
        SumRequest request = SumRequest.newBuilder()
                .setFirstNumber(5)
                .setSecondNumber(5)
                .build();
        SumResponse response = stub.sum(request);
        System.out.println(request.getFirstNumber() + "+" + request.getSecondNumber() + " = " + response.getSumResult());
    }

    private void doServerStreammingCall(Channel channel) {
        CalculatorServiceGrpc.CalculatorServiceBlockingStub stub = CalculatorServiceGrpc.newBlockingStub(channel);

        Integer number = 567890304;

        stub.primeNumberDecomposition(PrimerNumberDecompositionRequest.newBuilder()
                    .setNumber(number)
                    .build())
        .forEachRemaining(primeNumberDecompositionResponse -> {
            System.out.println("primefactor = " + primeNumberDecompositionResponse.getPrimeFactor());
        });
    }

    private void doClientStreammingCall(Channel channel) {
        CalculatorServiceGrpc.CalculatorServiceStub asyncClient = CalculatorServiceGrpc.newStub(channel);

        CountDownLatch latch = new CountDownLatch(1);

        StreamObserver<ComputeAverageRequest> requestStreamObserver = asyncClient.computeAverage(new StreamObserver<ComputeAverageResponse>() {
            @Override
            public void onNext(ComputeAverageResponse value) {
                System.out.println("Received a response from server");
                System.out.println("Result=" + value.getAverage());
            }

            @Override
            public void onError(Throwable t) {
                // todo
            }

            @Override
            public void onCompleted() {
                System.out.println("Server has completed sending us something");
                latch.countDown();
                // onCompleted will be called right after onNext()
            }
        });

        for (int i = 0; i < 10000; i++) {
            System.out.println("Sending request message #" + i);
            requestStreamObserver.onNext(ComputeAverageRequest.newBuilder()
                    .setNumber(i)
                    .build());
        }
        requestStreamObserver.onCompleted();

        try {
            latch.await(10L, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void doBiDiSreammingCall(Channel channel) {
        CalculatorServiceGrpc.CalculatorServiceStub asyncClient = CalculatorServiceGrpc.newStub(channel);
        CountDownLatch latch = new CountDownLatch(1);

        StreamObserver<FindMaximumRequest> requestStreamObserver = asyncClient.findMaximum(new StreamObserver<FindMaximumResponse>() {
            @Override
            public void onNext(FindMaximumResponse findMaximumResponse) {
                System.out.println("got new maximum from the server" + findMaximumResponse.getMaximum());
            }

            @Override
            public void onError(Throwable throwable) {
                latch.countDown();
            }

            @Override
            public void onCompleted() {
                System.out.println("server is done sending messages");
            }
        });

        // List of random numbers
        Arrays.asList(3, 5, 17, 9, 8, 30, 12).forEach(number -> {
            System.out.println("sending number " + number);
            requestStreamObserver.onNext(FindMaximumRequest.newBuilder()
                    .setNumber(number)
                    .build()
            );

            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        requestStreamObserver.onCompleted();

        try {
            latch.await(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
