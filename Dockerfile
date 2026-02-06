# --- Build Stage ---
# 使用一個包含 Maven 和 Eclipse Temurin JDK 17 的官方基礎映像
FROM maven:3.9.6-eclipse-temurin-17 AS build

# 將工作目錄設定為 /app
WORKDIR /app

# 複製 pom.xml 並下載所有依賴
# 這樣可以利用 Docker 的層快取，只有在 pom.xml 變更時才重新下載依賴
COPY pom.xml .
RUN mvn dependency:go-offline --quiet

# 複製所有原始碼
COPY src ./src

# 執行打包指令，跳過測試
RUN mvn package -DskipTests --quiet

# --- Run Stage ---
# 使用一個非常輕量的、僅包含 Eclipse Temurin JRE 17 的基礎映像
FROM eclipse-temurin:17-jre-jammy

# 將工作目錄設定為 /app
WORKDIR /app

# 從 build stage 將打包好的 JAR 檔複製过来，並重新命名為 app.jar
COPY --from=build /app/target/*.jar app.jar

# 暴露 8080 端口，讓 Cloud Run 可以將流量路由到這裡
EXPOSE 8080

# 設定容器的啟動指令
ENTRYPOINT ["java", "-jar", "app.jar"]
