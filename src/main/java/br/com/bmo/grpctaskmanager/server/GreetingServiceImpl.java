package br.com.bmo.grpctaskmanager.server;

import br.com.bmo.proto.greet.GreetRequest;
import br.com.bmo.proto.greet.GreetResponse;
import br.com.bmo.proto.greet.GreetServiceGrpc;
import br.com.bmo.proto.greet.Greeting;
import io.grpc.stub.StreamObserver;

public class GreetingServiceImpl extends GreetServiceGrpc.GreetServiceImplBase {

    @Override
    public void greet(GreetRequest request, StreamObserver<GreetResponse> responseObserver) {

        Greeting greeting = request.getGreeting();
        String firstName = greeting.getFirstName();

        String result = "Hello " + firstName;
        GreetResponse response = GreetResponse.newBuilder()
                .setResult(result)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
        //        super.greet(request, responseObserver);
    }
}
