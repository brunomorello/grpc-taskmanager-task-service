package br.com.bmo.grpctaskmanager.client;

import br.com.bmo.proto.dummy.DummyServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class TaskManagerClient {
    public static void main(String[] args) {
        System.out.println("gRPC Client");

        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50051)
                .usePlaintext()
                .build();

        System.out.println("creating stub");
        DummyServiceGrpc.DummyServiceBlockingStub syncClient = DummyServiceGrpc.newBlockingStub(channel);

        // do something syncClient.?

        System.out.println("shutting down channel");
        channel.shutdown();
    }
}
