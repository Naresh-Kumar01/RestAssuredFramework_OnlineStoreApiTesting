FROM maven:3.9.6-eclipse-temurin-17-alpine
WORKDIR /app

# 1. Root configuration files copy karein
COPY pom.xml .
COPY testng.xml .
COPY testng-full.xml . 

# 2. Test Data folder copy karein (CRITICAL FIX)
COPY testdata ./testdata

# 3. Poora source code copy karein
COPY src ./src

# 4. Dependencies ko offline cache karein
RUN mvn dependency:go-offline -B

# 5. Default command execute karein
CMD ["mvn", "test", "-DsuiteXmlFile=testng-full.xml"]