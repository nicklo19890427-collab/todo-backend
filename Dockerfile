# --- 第一階段：使用 Maven 建置 (Build Stage) ---
# 我們使用官方的 Maven 映像檔來編譯你的程式碼
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY . .
# 開始打包 (跳過測試以節省時間)
RUN mvn clean package -DskipTests

# --- 第二階段：執行環境 (Run Stage) ---
# 為了讓映像檔很小，我們只拿執行需要的 JRE
FROM eclipse-temurin:21-jdk
WORKDIR /app
# 從第一階段把打包好的 jar 檔複製過來，並改名為 app.jar
COPY --from=build /app/target/*.jar app.jar

# 開放 8080 埠口
EXPOSE 8080

# 啟動指令
ENTRYPOINT ["java", "-jar", "app.jar"]