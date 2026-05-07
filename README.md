# Starto V3 Ecosystem 🚀

Starto is a real-time startup ecosystem platform where Founders, Talent, and Investors connect. It features a unified subscription model, AI-powered matching, and real-time networking.

## 🏗️ Project Structure

This is a monorepo containing:
- **`starto-api`**: Spring Boot 3.3 Backend (Java 21)
- **`starto-web`**: Next.js 14 Frontend (React + Tailwind)
- **`starto-android`**: Native Android App (Jetpack Compose)

---

## 🛠️ Prerequisites

Before you begin, ensure you have the following installed:

### 1. Development Environment
- **Java**: 21 (LTS)
- **Spring Boot**: 3.3.4
- **Next.js**: 14.x
- **Node.js**: 20.x (LTS)
- **Maven**: 3.9.6
- **Android Studio**: Hedgehog+ (2023.1.1)

### 2. Infrastructure
- **PostgreSQL**: 15.x or 16.x
- **Redis**: 7.x
- **Firebase Admin SDK**: 9.x
- **Firebase Project**:
  - Authentication (Email/Password + Social Auth).
  - Firestore (enabled for real-time messaging).
  - A Service Account JSON file.

---

## 🚀 Backend Setup (`starto-api`)

### 1. Database Initialization
Create your database and run the schema:
```powershell
# Create database
psql -U postgres -c "CREATE DATABASE starto;"
# Enable PostGIS
psql -U postgres -d starto -c "CREATE EXTENSION postgis;"
# Import Schema
psql -U postgres -d starto -f ./schema.sql
```

### 2. Configuration (`.env.local`)
Create a `starto-web/.env.local` file (the backend runner reads from here) and provide:
- `DB_URL`: `jdbc:postgresql://localhost:5432/starto`
- `DB_USERNAME` / `DB_PASSWORD`
- `MAIL_PASSWORD`: Your Google App Password for emails.
- `FIREBASE_CONFIG_BASE64`: Your Firebase Service Account JSON encoded in Base64.
  - *How to encode:* `[Convert]::ToBase64String([IO.File]::ReadAllBytes("service-account.json"))`

### 3. Running the Backend
Use the provided automation script:
```powershell
.\run-backend.ps1
```
*Note: The backend runs on port **9090** by default.*

---

## 🌐 Frontend Setup (`starto-web`)

### 1. Install Dependencies
```bash
cd starto-web
npm install
```

### 2. Running the Frontend
```bash
npm run dev
```
*Note: The frontend runs on **http://localhost:3000**.*

---

## 📱 Android Setup (`starto-android`)

1. Open the `starto-android` folder in **Android Studio Hedgehog+**.
2. Place your `google-services.json` in the `app/` directory.
3. Update `local.properties`:
   ```properties
   API_BASE_URL=http://10.0.2.2:9090
   ```
4. Build and run on an emulator or physical device.

---

## 🐳 Docker Deployment (Optional)

To spin up the entire ecosystem (DB, Redis, API, Web) using Docker:
1. Ensure `.env` is populated.
2. Run:
   ```bash
   docker compose up -d --build
   ```

---

## 🧪 Common Troubleshooting

- **Port 9090 already in use**: 
  - Find process: `netstat -ano | findstr :9090`
  - Kill process: `taskkill /F /PID <PID> /T`
- **PostGIS missing**: Ensure `CREATE EXTENSION postgis;` was run in the correct database.
- **Mail failures**: Verify `MAIL_PASSWORD` is a 16-character App Password, not your standard Gmail password.

---

© 2026 Team Starto ❤️
