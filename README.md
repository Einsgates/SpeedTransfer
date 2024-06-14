
# SpeedTransfer

```xml
<build>
    <extensions>
        <extension>
            <groupId>kr.motd.maven</groupId>
            <artifactId>os-maven-plugin</artifactId>
            <version>1.6.2</version>
        </extension>
    </extensions>
    <plugins>
        <plugin>
            <groupId>org.xolstice.maven.plugins</groupId>
            <artifactId>protobuf-maven-plugin</artifactId>
            <version>0.6.1</version>
            <configuration>
                <protocArtifact>com.google.protobuf:protoc:3.21.1:exe:${os.detected.classifier}</protocArtifact>
                <pluginId>grpc-java</pluginId>
                <pluginArtifact>io.grpc:protoc-gen-grpc-java:1.45.1:exe:${os.detected.classifier}</pluginArtifact>
                <protoSourceRoot>${project.basedir}/src/main/proto</protoSourceRoot>
            </configuration>
            <executions>
                <execution>
                    <goals>
                        <goal>compile</goal>
                        <goal>compile-custom</goal>
                    </goals>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```
To send gRPC requests from a Java Maven project, you will need to include the necessary dependencies and write the client code. Here's a step-by-step guide to help you set this up.

### Step 1: Add Dependencies to `pom.xml`
Add the gRPC and Protocol Buffers dependencies to your `pom.xml` file.

```xml
<dependencies>
    <!-- gRPC dependencies -->
    <dependency>
        <groupId>io.grpc</groupId>
        <artifactId>grpc-netty</artifactId>
        <version>1.45.1</version>
    </dependency>
    <dependency>
        <groupId>io.grpc</groupId>
        <artifactId>grpc-protobuf</artifactId>
        <version>1.45.1</version>
    </dependency>
    <dependency>
        <groupId>io.grpc</groupId>
        <artifactId>grpc-stub</artifactId>
        <version>1.45.1</version>
    </dependency>
    <dependency>
        <groupId>com.google.protobuf</groupId>
        <artifactId>protobuf-java</artifactId>
        <version>3.21.1</version>
    </dependency>
</dependencies>
```

### Step 2: Generate gRPC Stubs from `.proto` File
You need to have a `.proto` file defining the gRPC service. For example, if your `.proto` file looks like this:

```proto
syntax = "proto3";

option java_package = "com.example.grpc";
option java_outer_classname = "MyServiceProto";

service MyService {
  rpc SayHello (HelloRequest) returns (HelloResponse);
}

message HelloRequest {
  string name = 1;
}

message HelloResponse {
  string message = 1;
}
```

Configure the `protobuf-maven-plugin` to generate Java classes from your `.proto` file.

```xml
<build>
    <extensions>
        <extension>
            <groupId>kr.motd.maven</groupId>
            <artifactId>os-maven-plugin</artifactId>
            <version>1.6.2</version>
        </extension>
    </extensions>
    <plugins>
        <plugin>
            <groupId>org.xolstice.maven.plugins</groupId>
            <artifactId>protobuf-maven-plugin</artifactId>
            <version>0.6.1</version>
            <configuration>
                <protocArtifact>com.google.protobuf:protoc:3.21.1:exe:${os.detected.classifier}</protocArtifact>
                <pluginId>grpc-java</pluginId>
                <pluginArtifact>io.grpc:protoc-gen-grpc-java:1.45.1:exe:${os.detected.classifier}</pluginArtifact>
            </configuration>
            <executions>
                <execution>
                    <goals>
                        <goal>compile</goal>
                        <goal>compile-custom</goal>
                    </goals>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```

### Step 3: Write the gRPC Client Code
Create a client to interact with your gRPC service.

```java
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import com.example.grpc.MyServiceGrpc;
import com.example.grpc.HelloRequest;
import com.example.grpc.HelloResponse;

public class GrpcClient {
    public static void main(String[] args) {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50051)
                                                      .usePlaintext()
                                                      .build();
        
        MyServiceGrpc.MyServiceBlockingStub stub = MyServiceGrpc.newBlockingStub(channel);
        
        HelloRequest request = HelloRequest.newBuilder()
                                           .setName("World")
                                           .build();
        
        HelloResponse response = stub.sayHello(request);
        
        System.out.println("Response from server: " + response.getMessage());
        
        channel.shutdown();
    }
}
```

### Step 4: Use Postman for gRPC Requests
1. Open Postman.
2. Create a new request and select the gRPC protocol.
3. Enter the server URL and port (e.g., `localhost:50051`).
4. Select the `.proto` file or paste the gRPC endpoint and request details.
5. Send the request and view the response.

This setup allows you to send gRPC requests from your Java Maven project. If you have any specific questions or need further customization, feel free to ask!


import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

import java.io.IOException;

public class GrpcServer {
    public static void main(String[] args) throws IOException, InterruptedException {
        // Create a new server to listen on port 50051
        Server server = ServerBuilder.forPort(50051)
                .addService(new MyServiceImpl())
                .build();

        // Start the server
        server.start();
        System.out.println("Server started, listening on " + server.getPort());

        // Await termination on the main thread
        server.awaitTermination();
    }

    // Implement the service defined in the .proto file
    static class MyServiceImpl extends MyServiceGrpc.MyServiceImplBase {
        @Override
        public void sayHello(HelloRequest request, StreamObserver<HelloResponse> responseObserver) {
            // Create a response
            HelloResponse response = HelloResponse.newBuilder()
                    .setMessage("Hello, " + request.getName())
                    .build();

            // Send the response back to the client
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
    }
}
