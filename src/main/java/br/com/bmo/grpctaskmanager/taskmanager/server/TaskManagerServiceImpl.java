package br.com.bmo.grpctaskmanager.taskmanager.server;

import br.com.bmo.proto.taskamanager.*;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.Optional;

import static com.mongodb.client.model.Filters.eq;

public class TaskManagerServiceImpl extends TaskManagerServiceGrpc.TaskManagerServiceImplBase {

    private MongoClient mongoClient = MongoClients.create("mongodb://root:dqm50vnc@localhost:27017");
    private MongoDatabase db = mongoClient.getDatabase("taskmanager");
    private MongoCollection<Document> collection = db.getCollection("task");

    @Override
    public void createTask(CreateTaskRequest request, StreamObserver<CreateTaskResponse> responseObserver) {
        System.out.println("received Create Task Request");

        Task task = request.getTask();

        Document doc = new Document()
                .append("description", task.getDescription())
                .append("details", task.getDetails())
                .append("status", task.getStatus())
                .append("created_at", task.getCreatedAt())
                .append("updated_at", task.getUpdatedAt());

        System.out.println("inserting new task...");

        collection.insertOne(doc);
        String id = doc.getObjectId("_id").toString();

        System.out.println("task created #" + id);

        CreateTaskResponse response = CreateTaskResponse.newBuilder()
                .setTask(task.toBuilder().setId(id).build())
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void readTask(ReadTaskRequest request, StreamObserver<ReadTaskResponse> responseObserver) {
        String taskId = request.getTaskId();

        Optional<Document> result = Optional.ofNullable(collection.find(eq("_id", new ObjectId(taskId))).first());

        if (result.isEmpty()) {
            responseObserver.onError(
                    Status.NOT_FOUND
                            .withDescription("Task with the corresponding id " + taskId + " was not found")
                            .asRuntimeException()
            );
        } else {
            Task task = Task.newBuilder()
                    .setDescription(result.get().getString("description"))
                    .setDetails(result.get().getString("details"))
                    .setStatus(result.get().getString("status"))
                    .setCreatedAt(result.get().getString("created_at"))
                    .setUpdatedAt(result.get().getString("updated_at"))
                    .setId(taskId)
                    .build();

            responseObserver.onNext(
                    ReadTaskResponse.newBuilder()
                            .setTask(task)
                            .build()
            );
            responseObserver.onCompleted();
        }

    }
}
