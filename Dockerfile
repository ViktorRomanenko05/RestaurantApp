FROM azul/zulu-openjdk:17

WORKDIR /app

COPY .mvn/ .mvn
COPY mvnw pom.xml ./

RUN chmod +x mvnw

RUN ./mvnw dependency:go-offline

COPY src ./src
RUN ./mvnw package -DskipTests

CMD ["java", "-jar", "target/RestaurantApp-0.0.1-SNAPSHOT.jar"]