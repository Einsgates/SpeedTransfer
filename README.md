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
