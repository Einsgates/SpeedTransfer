# SpeedTransfer

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
