package br.com.bmo.grpctaskmanager.taskmanager.server;

import br.com.bmo.proto.taskamanager.*;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.DeleteResult;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.time.LocalDateTime;
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
            Task task = parseDocToTask(result.get());

            responseObserver.onNext(
                    ReadTaskResponse.newBuilder()
                            .setTask(task)
                            .build()
            );
            responseObserver.onCompleted();
        }

    }

    @Override
    public void updateTask(UpdateTaskRequest request, StreamObserver<UpdateTaskResponse> responseObserver) {

        Task task = request.getTask();

        Document result = null;

        try {
            result = collection.find(eq("_id", new ObjectId(task.getId()))).first();
        } catch (Exception e) {
            responseObserver.onError(
                    Status.NOT_FOUND
                            .withDescription("Task with the corresponding id was not found. Id = " + task.getId())
                            .augmentDescription(e.getLocalizedMessage())
                            .asRuntimeException()
            );
        }

        if (result != null) {
            Document updatedTask = new Document()
                    .append("_id", new ObjectId(task.getId()))
                    .append("description", task.getDescription())
                    .append("details", task.getDetails())
                    .append("status", task.getStatus())
                    .append("created_at", task.getCreatedAt())
                    .append("updated_at", LocalDateTime.now().toString());

            System.out.println("Updating Task...");

            collection.replaceOne(eq("_id", result.getObjectId("_id")), updatedTask);

            System.out.println("Task updated!");
            System.out.println(updatedTask.toString());
            responseObserver.onNext(
                    UpdateTaskResponse.newBuilder()
                            .setTask(parseDocToTask(updatedTask))
                            .build()
            );
            responseObserver.onCompleted();
        } else {
            responseObserver.onError(
                    Status.NOT_FOUND
                            .withDescription("Task with the corresponding id was not found. Id = " + task.getId())
                            .asRuntimeException()
            );
        }
    }

    @Override
    public void deleteTask(DeleteTaskRequest request, StreamObserver<DeleteTaskResponse> responseObserver) {
        System.out.println("Received Delete Task");

        String taskId = request.getTaskId();
        DeleteResult result = null;

        try {
            System.out.println("Deleting Task...");
            result = collection.deleteOne(eq("_id", new ObjectId(taskId)));
        } catch (Exception e) {
            responseObserver.onError(
                    Status.NOT_FOUND
                            .withDescription("The task with the corresponding id was not found")
                            .augmentDescription(e.getLocalizedMessage())
                            .asRuntimeException()
            );
        }

        if (result.getDeletedCount() == 0) {
            responseObserver.onError(
                    Status.NOT_FOUND
                            .withDescription("No tasks were deleted with the corresponding value")
                            .augmentDescription("task_id = " + taskId)
                            .asRuntimeException()
            );
        } else {
            System.out.println("Task #" + taskId + " successfully deleted");
            responseObserver.onNext(
                    DeleteTaskResponse.newBuilder()
                            .setTaskId(taskId)
                            .build()
            );
            responseObserver.onCompleted();
        }
    }

    private Task parseDocToTask(Document result) {
        return Task.newBuilder()
                .setDescription(result.getString("description"))
                .setDetails(result.getString("details"))
                .setStatus(result.getString("status"))
                .setCreatedAt(result.getString("created_at"))
                .setUpdatedAt(result.getString("updated_at"))
                .setId(result.getObjectId("_id").toString())
                .build();
    }
}
