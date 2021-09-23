package br.com.bmo.grpctaskmanager.client;

import br.com.bmo.proto.dummy.DummyServiceGrpc;
import br.com.bmo.proto.greet.GreetRequest;
import br.com.bmo.proto.greet.GreetResponse;
import br.com.bmo.proto.greet.GreetServiceGrpc;
import br.com.bmo.proto.greet.Greeting;
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

        // created a protocol buffer greeting message
        Greeting greeting = Greeting.newBuilder()
                .setFirstName("Bruno")
                .setLastName("Moreno")
                .build();

        // do the same for a GreetRequest
        GreetRequest greetRequest = GreetRequest.newBuilder()
                .setGreeting(greeting)
                .build();

        // call the RPC and get back a GreetResponse (protocol buffers)
        GreetResponse greetResponse = greetClient.greet(greetRequest);

        System.out.println(greetResponse.getResult());

        System.out.println("shutting down channel");
        channel.shutdown();
    }
}
