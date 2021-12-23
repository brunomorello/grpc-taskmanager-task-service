# Taskmanager gRPC

### pull mongodb image
> docker pull mongo

### create mongoDB container

> docker run --name taskmanagerMongoDB -e MONGO_INITDB_ROOT_USERNAME=root -e MONGO_INITDB_ROOT_PASSWORD=dqm50vnc -p 27017:27017 -d mongo

### download mongodb GUI (Robo 3T)

> https://robomongo.org/

### add mongodb-driver-sync dependency

> implementation 'org.mongodb:mongodb-driver-sync:4.2.3'

### create a proto file

> src/proto/taskmanager/taskmanager.proto

### run gradle > generateProto

### create packages

> src/main/java/taskmanager/client
>   TaskManagerClient
> src/main/java/taskmanager/server
>   TaskManagerServer
>   TaskManagerServiceImpl

Check git logs on branch feature/task-crud-with-mongodb.
