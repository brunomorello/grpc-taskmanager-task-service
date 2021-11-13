package br.com.bmo.grpctaskmanager.taskmanager.client;

import br.com.bmo.proto.taskamanager.CreateTaskRequest;
import br.com.bmo.proto.taskamanager.CreateTaskResponse;
import br.com.bmo.proto.taskamanager.Task;
import br.com.bmo.proto.taskamanager.TaskManagerServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.time.LocalDateTime;

public class TaskManagerClient {
    public static void main(String[] args) {
        System.out.println("gRPC Client for TaskManager");
        TaskManagerClient main = new TaskManagerClient();
        main.run();
    }

    private void run() {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50051)
                .usePlaintext()
                .build();

        TaskManagerServiceGrpc.TaskManagerServiceBlockingStub client = TaskManagerServiceGrpc.newBlockingStub(channel);

        Task task = Task.newBuilder()
                .setDescription("Test Java Client")
                .setDetails("testing...")
                .setStatus("CLOSED")
                .setCreatedAt(LocalDateTime.now().toString())
                .setUpdatedAt(LocalDateTime.now().toString())
                .build();

        CreateTaskResponse response = client.createTask(
                CreateTaskRequest.newBuilder()
                        .setTask(task)
                        .build()
        );

        System.out.println("Received create task response");
        System.out.println(response.toString());

    }
}
