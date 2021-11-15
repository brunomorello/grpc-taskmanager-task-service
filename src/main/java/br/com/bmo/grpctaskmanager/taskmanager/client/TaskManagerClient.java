package br.com.bmo.grpctaskmanager.taskmanager.client;

import br.com.bmo.proto.taskamanager.*;
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

        createTask(client);
//        updateTask(client);
    }

    private void createTask(TaskManagerServiceGrpc.TaskManagerServiceBlockingStub client) {
        Task task = Task.newBuilder()
                .setDescription("Test Java Client - Create Task")
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

    private void updateTask(TaskManagerServiceGrpc.TaskManagerServiceBlockingStub client) {
        Task task = Task.newBuilder()
                .setDescription("Test Java Client 2")
                .setDetails("testing...")
                .setStatus("CLOSED")
                .setCreatedAt(LocalDateTime.now().toString())
                .setUpdatedAt(LocalDateTime.now().toString())
                .setId("618fbc85ddb4132907697051")
                .build();

        UpdateTaskResponse response = client.updateTask(UpdateTaskRequest.newBuilder()
                .setTask(task)
                .build());

        System.out.println("Received update task response");
        System.out.println(response.toString());
    }
}
