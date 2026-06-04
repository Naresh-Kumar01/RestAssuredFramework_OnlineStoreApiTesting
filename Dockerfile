FROM maven:3.9.6-eclipse-temurin-17-alpine
WORKDIR /app

# 1. Pehle pom.xml aur testng.xml copy karein
COPY pom.xml .
COPY testng.xml .

# Agar aapke project root par config.properties ya extent-config.xml jaisi files hain, 
# toh aap 'COPY config.properties .' bhi add kar sakte hain.

# 2. Source files copy karein
COPY src ./src

# 3. Dependencies offline download karein
RUN mvn dependency:go-offline -B

# 4. Tests execute karein
CMD ["mvn", "test"]