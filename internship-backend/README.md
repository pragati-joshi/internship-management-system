# Internship & Placement Management System - Backend

A clean Java Servlet (Jakarta 6.0) + JDBC + PostgreSQL REST backend built for an Internship & Placement Management System.

---

## 🛠️ System Requirements
- **Java:** JDK 17+
- **Database:** PostgreSQL 13+
- **Server:** Apache Tomcat 10.1+
- **Build Tool:** Apache Maven 3.8+

---

## ⚙️ Setup Instructions

### 1. Database Setup
1. Open PostgreSQL (`psql` or pgAdmin).
2. Create the target database:
   ```sql
   CREATE DATABASE internship_management;
   ```
3. Run the schema creation & seed script located in `database/schema.sql`:
   ```bash
   psql -U postgres -d internship_management -f database/schema.sql
   ```
4. Verify/update database credentials in `src/main/resources/db.properties`:
   ```properties
   db.url=jdbc:postgresql://localhost:5432/internship_management
   db.username=postgres
   db.password=YOUR_POSTGRES_PASSWORD
   ```

---

### 2. Maven Build & Deployment
1. Navigate to the `internship-backend` folder:
   ```bash
   cd C:\Users\hp\.gemini\antigravity\scratch\internship-backend
   ```
2. Build the `.war` deployment file:
   ```bash
   mvn clean package
   ```
3. Copy the generated WAR file from `target/internship-backend.war` to your Tomcat `webapps` directory:
   ```bash
   cp target/internship-backend.war <TOMCAT_DIR>/webapps/
   ```
4. Start Apache Tomcat:
   ```bash
   <TOMCAT_DIR>/bin/startup.sh   # (Linux/macOS)
   <TOMCAT_DIR>\bin\startup.bat  # (Windows)
   ```
5. The API will be live at `http://localhost:8080/internship-backend/api` (or `http://localhost:8080/api/v1` if deployed as root context).

---

## 📡 API Endpoints Documentation

### 🔐 1. Authentication APIs (`/api/auth/*`)
- **`POST /api/auth/register`**: Register a new user (Student/Mentor/Admin).
  - Body:
    ```json
    {
      "name": "John Doe",
      "email": "john@example.com",
      "password": "password123",
      "role": "STUDENT",
      "rollNumber": "CS101",
      "branch": "Computer Science",
      "year": 3,
      "cgpa": 8.5,
      "phone": "9876543210"
    }
    ```
- **`POST /api/auth/login`**: Authenticate & retrieve JWT Bearer token.
  - Body:
    ```json
    {
      "email": "john@example.com",
      "password": "password123"
    }
    ```

---

### 💼 2. Internship APIs (`/api/internships/*`)
- **`GET /api/internships`**: List all posted internships (Public/Authenticated).
- **`GET /api/internships/{id}`**: Get specific internship details.
- **`POST /api/internships`**: Create a new internship posting *(Admin Only)*.
- **`PUT /api/internships/{id}`**: Update an existing posting *(Admin Only)*.
- **`DELETE /api/internships/{id}`**: Delete a posting *(Admin Only)*.

---

### 📝 3. Application APIs (`/api/applications/*`)
- **`POST /api/applications`**: Apply for an internship *(Student)*.
- **`GET /api/applications/student/{studentId}`**: View all applications by a specific student.
- **`GET /api/applications/internship/{internshipId}`**: View all applicants for an internship.
- **`PUT /api/applications/{id}/status`**: Update application status (e.g. `SHORTLISTED`, `SELECTED`, `REJECTED`) *(Admin/Mentor)*.
- **`DELETE /api/applications/{id}`**: Withdraw an application *(Student Owner)*.

---

### 🎓 4. Placement APIs (`/api/placements/*`)
- **`GET /api/placements`**: Get all confirmed placements.
- **`POST /api/placements`**: Record a student placement offer *(Admin Only)*.
- **`PUT /api/placements/{id}`**: Edit placement record *(Admin Only)*.
- **`DELETE /api/placements/{id}`**: Delete placement record *(Admin Only)*.

---

### ⭐ 5. Evaluation APIs (`/api/evaluations/*`)
- **`POST /api/evaluations`**: Submit performance rating (1-5) and text feedback for a student *(Mentor Only)*.
- **`GET /api/evaluations/student/{studentId}`**: View evaluations for a specific student.
