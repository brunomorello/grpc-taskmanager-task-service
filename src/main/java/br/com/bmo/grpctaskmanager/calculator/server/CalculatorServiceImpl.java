package br.com.bmo.grpctaskmanager.calculator.server;

import br.com.bmo.proto.calculator.CalculatorServiceGrpc;
import br.com.bmo.proto.calculator.SumRequest;
import br.com.bmo.proto.calculator.SumResponse;
import io.grpc.stub.StreamObserver;

public class CalculatorServiceImpl extends CalculatorServiceGrpc.CalculatorServiceImplBase {

    @Override
    public void sum(SumRequest request, StreamObserver<SumResponse> responseObserver) {
        SumResponse sumResponse = SumResponse.newBuilder()
                .setSumResult(request.getFirstNumber() + request.getSecondNumber())
                .build();
        responseObserver.onNext(sumResponse);
        responseObserver.onCompleted();
    }
}
