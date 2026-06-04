
# 1. Base image standard Maven aur OpenJDK 17 ke sath
FROM maven:3.9.6-eclipse-temurin-17-alpine

# 2. Container ke andar working directory set karein
WORKDIR /app

# 3. Pehle pom.xml aur src folder ko copy karein
COPY pom.xml .
COPY src ./src

# 4. Saari dependencies download karne ke liye command
RUN mvn dependency:go-offline -B

# 5. Container run hote hi test cases execute karne ke liye command
CMD ["mvn", "test"]