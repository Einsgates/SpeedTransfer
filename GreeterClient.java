import io.grpc.Channel;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import java.util.concurrent.TimeUnit;

// Assuming the package name from the protobuf generation is com.example
import com.example.GreeterGrpc;
import com.example.HelloRequest;
import com.example.HelloResponse;

public class GreeterClient {
    private final GreeterGrpc.GreeterBlockingStub blockingStub;

    public GreeterClient(Channel channel) {
        // 'GreeterGrpc.newBlockingStub' statically imported to create a stub for the client
        blockingStub = GreeterGrpc.newBlockingStub(channel);
    }

    public void greet(String name) {
        HelloRequest request = HelloRequest.newBuilder().setName(name).build();
        try {
            HelloResponse response = blockingStub.sayHello(request);
            System.out.println("Greeting: " + response.getMessage());
        } catch (StatusRuntimeException e) {
            System.err.println("RPC failed: " + e.getStatus());
        }
    }

    public static void main(String[] args) {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 8080)
            .usePlaintext()
            .build();
        try {
            GreeterClient client = new GreeterClient(channel);
            client.greet("World");
        } finally {
            try {
                channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
