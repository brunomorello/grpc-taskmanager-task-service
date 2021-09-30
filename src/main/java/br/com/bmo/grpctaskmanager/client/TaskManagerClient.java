package br.com.bmo.grpctaskmanager.client;

import br.com.bmo.proto.dummy.DummyServiceGrpc;
import br.com.bmo.proto.greet.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class TaskManagerClient {

    public static void main(String[] args) {
        System.out.println("gRPC Client");

        TaskManagerClient main = new TaskManagerClient();
        main.run();
    }

    private void run() {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50051)
                .usePlaintext()
                .build();

//        doUnaryCall(channel);
//        doServerStreamingCall(channel);
        doClientStreamingCall(channel);

        System.out.println("shutting down channel");
        channel.shutdown();
    }

    private void doServerStreamingCall(ManagedChannel channel) {
        System.out.println("creating stub");
        GreetServiceGrpc.GreetServiceBlockingStub syncClient = GreetServiceGrpc.newBlockingStub(channel);

        Greeting greeting = Greeting.newBuilder()
                .setFirstName("Bruno")
                .setLastName("Moreno")
                .build();

        GreetManyTimesRequest request = GreetManyTimesRequest.newBuilder()
                .setGreeting(greeting)
                .build();

        syncClient.greetManyTimes(request)
                .forEachRemaining(greetManyTimesResponse -> {
                    System.out.println("Streaming Response= " + greetManyTimesResponse.getResult());
                });
    }

    private void doUnaryCall(ManagedChannel channel) {
        System.out.println("creating stub");
        GreetServiceGrpc.GreetServiceBlockingStub syncClient = GreetServiceGrpc.newBlockingStub(channel);

        Greeting greeting = Greeting.newBuilder()
                .setFirstName("Bruno")
                .setLastName("Moreno")
                .build();

        GreetRequest greetRequest = GreetRequest.newBuilder()
                .setGreeting(greeting)
                .build();

        GreetResponse greetResponse = syncClient.greet(greetRequest);

        System.out.println("Unary call response= " + greetResponse.getResult());
    }

    private void doClientStreamingCall(ManagedChannel channel) {
        GreetServiceGrpc.GreetServiceStub asyncClient = GreetServiceGrpc.newStub(channel);

        CountDownLatch latch = new CountDownLatch(1);

        StreamObserver<LongGreetRequest> requestStreamObserver = asyncClient.longGreet(new StreamObserver<LongGreetResponse>() {
            @Override
            public void onNext(LongGreetResponse value) {
                //we get a response from the server
                System.out.println("Received a reponse from the server");
                System.out.println(value.getResult());
                //onNext will be called only once
            }

            @Override
            public void onError(Throwable t) {
                // we get an error from the server
            }

            @Override
            public void onCompleted() {
                // server is done sending us data
                // onCompleted will be called right after onNext()
                System.out.println("Server has completed sending us something");
                latch.countDown();
            }
        });

        // streaming message #1
        System.out.println("sending message #1");
        requestStreamObserver.onNext(LongGreetRequest.newBuilder()
                .setGreeting(Greeting.newBuilder()
                        .setFirstName("Bruno")
                        .build())
                .build());

        // streaming message #2
        System.out.println("sending message #2");
        requestStreamObserver.onNext(LongGreetRequest.newBuilder()
                .setGreeting(Greeting.newBuilder()
                        .setFirstName("Jade")
                        .build())
                .build());

        // streaming message #3
        System.out.println("sending message #3");
        requestStreamObserver.onNext(LongGreetRequest.newBuilder()
                .setGreeting(Greeting.newBuilder()
                        .setFirstName("Luna")
                        .build())
                .build());

        // streaming message #4
        System.out.println("sending message #4");
        requestStreamObserver.onNext(LongGreetRequest.newBuilder()
                .setGreeting(Greeting.newBuilder()
                        .setFirstName("Izzy")
                        .build())
                .build());

        // we tell the server that the client is done sending data
        requestStreamObserver.onCompleted();

        try {
            latch.await(10L, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
