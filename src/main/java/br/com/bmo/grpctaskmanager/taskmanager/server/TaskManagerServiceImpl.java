package br.com.bmo.grpctaskmanager.taskmanager.server;

import br.com.bmo.proto.taskamanager.CreateTaskRequest;
import br.com.bmo.proto.taskamanager.CreateTaskResponse;
import br.com.bmo.proto.taskamanager.Task;
import br.com.bmo.proto.taskamanager.TaskManagerServiceGrpc;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import io.grpc.stub.StreamObserver;
import org.bson.Document;

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
}
