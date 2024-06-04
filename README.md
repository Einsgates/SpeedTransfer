# SpeedTransfer

```json
receivers:
  otlp:
    protocols:
      grpc:
      http:
        endpoint: "0.0.0.0:4318" # Listen for HTTP/1.1 data

exporters:
  otlp:
    endpoint: "10.212.35.27:4317" # Your VM's address and OTLP port
    tls:
      insecure: true

processors:
  batch:

service:
  pipelines:
    traces:
      receivers: [otlp]
      processors: [batch]
      exporters: [otlp]
    metrics:
      receivers: [otlp]
      processors: [batch]
      exporters: [otlp]
    logs:
      receivers: [otlp]
      processors: [batch]
      exporters: [otlp]

```

```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-jar-plugin</artifactId>
            <version>3.2.0</version> <!-- Use the latest version available -->
            <configuration>
                <archive>
                    <manifest>
                        <addClasspath>true</addClasspath>
                        <classpathPrefix>libs/</classpathPrefix> <!-- Only necessary if you have external JARs in a lib folder -->
                        <mainClass>fully.qualified.MainClass</mainClass> <!-- Replace with your main class -->
                    </manifest>
                </archive>
            </configuration>
        </plugin>
    </plugins>
</build>

```

```xml
<dependencies>
    <!-- OpenTelemetry API -->
    <dependency>
        <groupId>io.opentelemetry</groupId>
        <artifactId>opentelemetry-api</artifactId>
        <version>1.15.0</version>
    </dependency>

    <!-- OpenTelemetry SDK -->
    <dependency>
        <groupId>io.opentelemetry</groupId>
        <artifactId>opentelemetry-sdk</artifactId>
        <version>1.15.0</version>
    </dependency>

    <!-- OpenTelemetry OTLP Exporter -->
    <dependency>
        <groupId>io.opentelemetry</groupId>
        <artifactId>opentelemetry-exporter-otlp</artifactId>
        <version>1.15.0</version>
    </dependency>

    <!-- Required for OTLP -->
    <dependency>
        <groupId>io.grpc</groupId>
        <artifactId>grpc-netty-shaded</artifactId>
        <version>1.42.1</version>
    </dependency>
</dependencies>
```

```java
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.export.BatchSpanProcessor;
import io.opentelemetry.exporter.otlp.trace.OtlpGrpcSpanExporter;

public class TelemetryGenerator {

    private static final Tracer tracer = setupOpenTelemetry().getTracer("io.example.TelemetryGenerator");

    public static void main(String[] args) {
        Span span = tracer.spanBuilder("start-example-span").startSpan();
        try (Scope scope = span.makeCurrent()) {
            simulateWork();
        } finally {
            span.end();
        }
    }

    private static OpenTelemetry setupOpenTelemetry() {
        OtlpGrpcSpanExporter spanExporter = OtlpGrpcSpanExporter.builder()
            .setEndpoint("http://localhost:4317") // Change this to your Collector endpoint
            .build();

        BatchSpanProcessor spanProcessor = BatchSpanProcessor.builder(spanExporter)
            .build();

        SdkTracerProvider tracerProvider = SdkTracerProvider.builder()
            .addSpanProcessor(spanProcessor)
            .build();

        return OpenTelemetrySdk.builder()
            .setTracerProvider(tracerProvider)
            .build();
    }

    private static void simulateWork() {
        try {
            Thread.sleep(1000); // Simulate some work being done
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
```
### OpenTelemetryConfiguration
```java
import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.export.BatchSpanProcessor;
import io.opentelemetry.exporter.otlp.trace.OtlpGrpcSpanExporter;

public class OpenTelemetryConfiguration {

    public static void initializeOpenTelemetry() {
        System.out.println("Initializing OpenTelemetry...");

        // Configure the OTLP exporter
        OtlpGrpcSpanExporter spanExporter = OtlpGrpcSpanExporter.builder()
                .setEndpoint("http://10.212.35.27:4317")
                .build();
        System.out.println("OTLP Exporter configured with endpoint: http://10.212.35.27:4317");

        // Configure the SDK tracer provider with a batch span processor
        SdkTracerProvider tracerProvider = SdkTracerProvider.builder()
                .addSpanProcessor(BatchSpanProcessor.builder(spanExporter).build())
                .build();
        System.out.println("SDK Tracer Provider configured with BatchSpanProcessor");

        // Set the global OpenTelemetry instance
        OpenTelemetrySdk.builder()
                .setTracerProvider(tracerProvider)
                .buildAndRegisterGlobal();
        System.out.println("Global OpenTelemetry configured.");
    }

    public static void main(String[] args) {
        System.out.println("Application started.");
        initializeOpenTelemetry();
        Tracer tracer = GlobalOpenTelemetry.getTracer("exampleTracer");

        // Start a span
        System.out.println("Starting span...");
        Span span = tracer.spanBuilder("exampleSpan")
                .setSpanKind(Span.Kind.INTERNAL)
                .startSpan();
        System.out.println("Span 'exampleSpan' started.");

        // Add attributes
        span.setAttribute("exampleKey", "exampleValue");
        System.out.println("Attribute 'exampleKey' added to span.");

        // Add events
        span.addEvent("Event 0");
        System.out.println("Event 0 added to span.");
        try {
            // Simulate work
            Thread.sleep(1000);
            span.addEvent("Event 1");
            System.out.println("Event 1 added after 1 second.");
        } catch (InterruptedException e) {
            span.setStatus(io.opentelemetry.api.trace.StatusCode.ERROR, "Interrupted!");
            Thread.currentThread().interrupt();
            System.out.println("Span interrupted and error status set.");
        } finally {
            // End the span
            span.end();
            System.out.println("Span ended.");
        }
        System.out.println("Application ended.");
    }
}
```
```xml
<build>
    <plugins>
        <plugin>
            <artifactId>maven-assembly-plugin</artifactId>
            <version>3.3.0</version> <!-- Use the latest version available -->
            <configuration>
                <archive>
                    <manifest>
                        <mainClass>org.example.OpenTelemetryConfiguration</mainClass>
                    </manifest>
                </archive>
                <descriptorRefs>
                    <descriptorRef>jar-with-dependencies</descriptorRef>
                </descriptorRefs>
                <appendAssemblyId>false</appendAssemblyId>
            </configuration>
            <executions>
                <execution>
                    <id>make-assembly</id> <!-- This can be any name -->
                    <phase>package</phase> <!-- Bind to the packaging phase -->
                    <goals>
                        <goal>single</goal>
                    </goals>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```
