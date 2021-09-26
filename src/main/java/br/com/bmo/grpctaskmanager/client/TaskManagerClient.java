package br.com.bmo.grpctaskmanager.client;

import br.com.bmo.proto.dummy.DummyServiceGrpc;
import br.com.bmo.proto.greet.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class TaskManagerClient {
    public static void main(String[] args) {
        System.out.println("gRPC Client");

        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50051)
                .usePlaintext()
                .build();

        System.out.println("creating stub");

        // created a greet service client (blocking stub - synchronous)
        GreetServiceGrpc.GreetServiceBlockingStub greetClient = GreetServiceGrpc.newBlockingStub(channel);

        Greeting greeting = Greeting.newBuilder()
                .setFirstName("Bruno")
                .setLastName("Moreno")
                .build();


          // Unary Impl
//        GreetRequest greetRequest = GreetRequest.newBuilder()
//                .setGreeting(greeting)
//                .build();
//
//        // call the RPC and get back a GreetResponse (protocol buffers)
//        GreetResponse greetResponse = greetClient.greet(greetRequest);

//        System.out.println(greetResponse.getResult());

        // Streaming Impl
        GreetManyTimesRequest request = GreetManyTimesRequest.newBuilder()
            .setGreeting(greeting).build();

        greetClient.greetManyTimes(request)
            .forEachRemaining(greetManyTimesResponse -> {
                System.out.println(greetManyTimesResponse.getResult());
            });

        System.out.println("shutting down channel");
        channel.shutdown();
    }
}
