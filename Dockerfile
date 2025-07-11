# Use an official OpenJDK runtime as a parent image
FROM openjdk:17.0.2-jdk-oracle

# Set the working directory
WORKDIR /app

# Copy the application's jar file into the container
COPY finished_builds/streamlinecloud-main/streamlinecloud_NODE-alpha-1.0-SNAPSHOT.jar /app/streamlinecloud_NODE-alpha-1.0-SNAPSHOT.jar

ENV LANG=EN
ENV CREATE_DEFAULT_SERVER=false
ENV JAVA_PATH=/usr/bin/jdk-17/bin/java

# Run the jar file
ENTRYPOINT ["java", "-jar", "streamlinecloud_NODE-alpha-1.0-SNAPSHOT.jar"]
