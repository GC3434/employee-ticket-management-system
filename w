name: empmngmntandtktingsys
services:
  api-gateway:
    container_name: api-gateway
    depends_on:
      config-server:
        condition: service_started
        required: true
      discovery-server:
        condition: service_started
        required: true
    environment:
      SPRING_CONFIG_IMPORT: optional:configserver:http://config-server:8888
    image: api-gateway
    networks:
      employee-network: null
    ports:
      - mode: ingress
        target: 8080
        published: "8080"
        protocol: tcp
  audit-service:
    container_name: audit-service
    depends_on:
      config-server:
        condition: service_started
        required: true
      discovery-server:
        condition: service_started
        required: true
      kafka:
        condition: service_started
        required: true
      mysql:
        condition: service_started
        required: true
    environment:
      SPRING_CONFIG_IMPORT: optional:configserver:http://config-server:8888
    image: audit-service
    networks:
      employee-network: null
    ports:
      - mode: ingress
        target: 8084
        published: "8084"
        protocol: tcp
  config-server:
    container_name: config-server
    environment:
      SPRING_CONFIG_IMPORT: optional:configserver:http://config-server:8888
    image: config-server
    networks:
      employee-network: null
    ports:
      - mode: ingress
        target: 8888
        published: "8888"
        protocol: tcp
  discovery-server:
    container_name: discovery-server
    depends_on:
      config-server:
        condition: service_started
        required: true
    environment:
      SPRING_CONFIG_IMPORT: optional:configserver:http://config-server:8888
    image: discovery-server
    networks:
      employee-network: null
    ports:
      - mode: ingress
        target: 8761
        published: "8761"
        protocol: tcp
  elasticsearch:
    container_name: elasticsearch
    environment:
      ES_JAVA_OPTS: -Xms256m -Xmx256m
      discovery.type: single-node
      xpack.security.enabled: "false"
    image: docker.elastic.co/elasticsearch/elasticsearch:9.1.0
    networks:
      employee-network: null
    ports:
      - mode: ingress
        target: 9200
        published: "9200"
        protocol: tcp
    volumes:
      - type: volume
        source: elasticsearch-data
        target: /usr/share/elasticsearch/data
        volume: {}
  email-service:
    container_name: email-service
    depends_on:
      config-server:
        condition: service_started
        required: true
      discovery-server:
        condition: service_started
        required: true
      kafka:
        condition: service_started
        required: true
      redis:
        condition: service_started
        required: true
    environment:
      SPRING_CONFIG_IMPORT: optional:configserver:http://config-server:8888
    image: email-service
    networks:
      employee-network: null
    ports:
      - mode: ingress
        target: 8083
        published: "8083"
        protocol: tcp
  fluent-bit:
    container_name: fluent-bit
    depends_on:
      elasticsearch:
        condition: service_started
        required: true
    image: fluent/fluent-bit:4.1
    networks:
      employee-network: null
    volumes:
      - type: bind
        source: /Users/abhishek/SpringBoot Project/EmpMngmntAndTktingSystem/EmpMngmntAndTktingSys/monitoring/fluent-bit.conf
        target: /fluent-bit/etc/fluent-bit.conf
        bind: {}
      - type: bind
        source: /Users/abhishek/SpringBoot Project/EmpMngmntAndTktingSystem/EmpMngmntAndTktingSys/ticket-service/logs
        target: /logs
        bind: {}
  grafana:
    container_name: grafana
    depends_on:
      prometheus:
        condition: service_started
        required: true
    image: grafana/grafana
    networks:
      employee-network: null
    ports:
      - mode: ingress
        target: 3000
        published: "3000"
        protocol: tcp
    volumes:
      - type: volume
        source: grafana-data
        target: /var/lib/grafana
        volume: {}
  jaeger:
    container_name: jaeger
    image: jaegertracing/all-in-one:latest
    networks:
      employee-network: null
    ports:
      - mode: ingress
        target: 16686
        published: "16686"
        protocol: tcp
      - mode: ingress
        target: 4317
        published: "4317"
        protocol: tcp
      - mode: ingress
        target: 4318
        published: "4318"
        protocol: tcp
  kafka:
    container_name: kafka
    environment:
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://kafka:9092
      KAFKA_CONTROLLER_LISTENER_NAMES: CONTROLLER
      KAFKA_CONTROLLER_QUORUM_VOTERS: 1@kafka:9093
      KAFKA_LISTENER_SECURITY_PROTOCOL_MAP: CONTROLLER:PLAINTEXT,PLAINTEXT:PLAINTEXT
      KAFKA_LISTENERS: PLAINTEXT://:9092,CONTROLLER://:9093
      KAFKA_NODE_ID: "1"
      KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: "1"
      KAFKA_PROCESS_ROLES: broker,controller
    image: apache/kafka:4.0.0
    networks:
      employee-network: null
    ports:
      - mode: ingress
        target: 9092
        published: "9092"
        protocol: tcp
    volumes:
      - type: volume
        source: kafka-data
        target: /var/lib/kafka/data
        volume: {}
      - type: volume
        source: kafka-secrets
        target: /etc/kafka/secrets
        volume: {}
      - type: volume
        source: kafka-config
        target: /mnt/shared/config
        volume: {}
  kibana:
    container_name: kibana
    depends_on:
      elasticsearch:
        condition: service_started
        required: true
    image: docker.elastic.co/kibana/kibana:9.1.0
    networks:
      employee-network: null
    ports:
      - mode: ingress
        target: 5601
        published: "5601"
        protocol: tcp
  mysql:
    container_name: mysql
    environment:
      MYSQL_DATABASE: empNtkt
      MYSQL_ROOT_PASSWORD: "12345678"
    image: mysql:8.4
    networks:
      employee-network: null
    ports:
      - mode: ingress
        target: 3306
        published: "3306"
        protocol: tcp
    volumes:
      - type: volume
        source: mysql-data
        target: /var/lib/mysql
        volume: {}
  prometheus:
    container_name: prometheus
    depends_on:
      api-gateway:
        condition: service_started
        required: true
      audit-service:
        condition: service_started
        required: true
      email-service:
        condition: service_started
        required: true
      ticket-service:
        condition: service_started
        required: true
      user-service:
        condition: service_started
        required: true
    image: prom/prometheus
    networks:
      employee-network: null
    ports:
      - mode: ingress
        target: 9090
        published: "9090"
        protocol: tcp
    volumes:
      - type: bind
        source: /Users/abhishek/SpringBoot Project/EmpMngmntAndTktingSystem/EmpMngmntAndTktingSys/monitoring/prometheus/prometheus.yml
        target: /etc/prometheus/prometheus.yml
        bind: {}
  redis:
    container_name: redis
    image: redis:7
    networks:
      employee-network: null
    ports:
      - mode: ingress
        target: 6379
        published: "6379"
        protocol: tcp
    volumes:
      - type: volume
        source: redis-data
        target: /data
        volume: {}
  ticket-service:
    container_name: ticket-service
    depends_on:
      config-server:
        condition: service_started
        required: true
      discovery-server:
        condition: service_started
        required: true
      kafka:
        condition: service_started
        required: true
      mysql:
        condition: service_started
        required: true
      redis:
        condition: service_started
        required: true
    environment:
      SPRING_CONFIG_IMPORT: optional:configserver:http://config-server:8888
    image: ticket-service
    networks:
      employee-network: null
    ports:
      - mode: ingress
        target: 8082
        published: "8082"
        protocol: tcp
  user-service:
    container_name: user-service
    depends_on:
      config-server:
        condition: service_started
        required: true
      discovery-server:
        condition: service_started
        required: true
      mysql:
        condition: service_started
        required: true
      redis:
        condition: service_started
        required: true
    environment:
      SPRING_CONFIG_IMPORT: optional:configserver:http://config-server:8888
    image: user-service
    networks:
      employee-network: null
    ports:
      - mode: ingress
        target: 8081
        published: "8081"
        protocol: tcp
networks:
  employee-network:
    name: employee-network
    external: true
volumes:
  elasticsearch-data:
    name: elasticsearch-data
    external: true
  grafana-data:
    name: grafana-data
    external: true
  kafka-config:
    name: 92b417d848d67ac2694115ae3934462ca9b88224fec7834074f45d8ad13e6369
    external: true
  kafka-data:
    name: ebb16046033881bba5a1b1520f79e8e979ae3faa8d5f327f4bca423bdf3541ff
    external: true
  kafka-secrets:
    name: 42b4de5e12cc3958b043427b52c839a2c83cabeef8a74f5cd1081fb5f74e025f
    external: true
  mysql-data:
    name: 0580a54b55a45d26360d71f693291e433080754d8743ae75c9fe8e5a94e016b8
    external: true
  redis-data:
    name: 816285ce2abdcec87ba1fc693fc635b1b7202a65020c01e59ef8389a9fce132d
    external: true
