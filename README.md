# ✈️ Airport Management System

A full-stack **Airport Management System** built using **Spring Boot (Java), React.js, MySQL, TailwindCSS, and Eureka for microservices discovery**.  

---

## 🚀 Features
- **Authentication Module**: User login & registration (Spring Security + JWT).
- **Dashboard**: Admin dashboard to navigate between modules.
- **Plane Module**:  
  - Add plane details with owner validation (new or existing).  
  - Search planes by plane number / owner email.  
  - Edit and delete planes with cascade rules for owner/address.  
  - Display all planes with their owner & admin details.  
- **Plane Owner Module**:  
  - Manage plane owner records with address details.  
  - Edit owner information by email.  
  - Search owner by email.  
- **Eureka Server**: Service discovery for modular microservice expansion.  

---

## 🛠️ Tech Stack
- **Frontend**: React.js, TailwindCSS, Axios, Vite  
- **Backend**: Spring Boot, Spring Security, Eureka Server, REST APIs  
- **Database**: MySQL  
- **Tools**: Git, Postman, Maven  


---

## 📸 Screenshots
(Add a few screenshots of your app here for better presentation)

---

## ⚡ Getting Started
### 1. Clone the Repository
```bash
git clone https://github.com/Steive-URK21CS1188/airport-management-system.git
cd airport-management-system
```
### 2. Backend Setup
```bash
cd backend
mvn clean install
mvn spring-boot:run
```
Backend runs at: http://localhost:8081
### 3. Frontend Setup
```bash
cd frontend
npm install
npm run dev
```
Frontend runs at: http://localhost:5173
### 4. Eureka Server
```bash
cd eureka-server
mvn spring-boot:run
```
Eureka Dashboard: http://localhost:8761/
