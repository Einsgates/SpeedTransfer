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
