FROM maven:3.9.6-eclipse-temurin-17-alpine
WORKDIR /app

# 1. Sabhi important configurations aur XML files copy karein
COPY pom.xml .
COPY testng.xml .
COPY testng-full.xml . 
COPY config.properties .

# 2. Source code copy karein
COPY src ./src

# 3. Dependencies ko offline cache karein
RUN mvn dependency:go-offline -B

# 4. default command ko testng-full.xml chalane ke liye set karein
CMD ["mvn", "test", "-DsuiteXmlFile=testng-full.xml"]