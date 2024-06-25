import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.export.BatchSpanProcessor;
import io.opentelemetry.exporter.otlp.trace.OtlpGrpcSpanExporter;

public class Main {
    private static final Tracer tracer = GlobalOpenTelemetry.getTracer("example-service");

    public static void main(String[] args) {
        // Initialize the OpenTelemetry SDK with OTLP exporter
        OtlpGrpcSpanExporter spanExporter = OtlpGrpcSpanExporter.builder()
            .setEndpoint("http://localhost:4317") // Endpoint of the OpenTelemetry Collector or server
            .build();

        SdkTracerProvider tracerProvider = SdkTracerProvider.builder()
            .addSpanProcessor(BatchSpanProcessor.builder(spanExporter).build())
            .build();

        OpenTelemetrySdk.builder()
            .setTracerProvider(tracerProvider)
            .buildAndRegisterGlobal();

        // Create a span
        Span span = tracer.spanBuilder("HTTP GET /api/resource").startSpan();
        try {
            // Simulate some work
            span.setAttribute("http.method", "GET");
            span.setAttribute("http.status_code", 200);
            span.setAttribute("user.id", "12345");

            // Simulate more work
            Thread.sleep(100);
        } catch (InterruptedException e) {
            span.recordException(e);
        } finally {
            span.end();
        }

        // Shutdown the SDK and flush the exporter
        tracerProvider.shutdown();
    }
}