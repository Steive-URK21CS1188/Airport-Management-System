# ✈️ Airport Management System  

A **Full-stack web application** designed to streamline airport operations by managing planes, pilots, hangars, and allocations built using **Spring Boot (Java), React.js, MySQL, TailwindCSS, and Eureka for microservices discovery**.  

---

## 🛠️ Tech Stack

- **Frontend**: React.js, TailwindCSS, Axios, Vite  
- **Backend**: Spring Boot, Spring Security, Eureka Server, REST APIs  
- **Database**: MySQL  
- **Tools**: Git, Postman, Maven, VS Code, Eclipse  

---

## 📌 Features by Modules  

### 🛩️ Plane Module  
- Add plane details with **owner validation** (new or existing).  
- Search planes by **plane number**.  
- Edit and delete planes with **cascade rules** (auto-remove owner/address if no longer linked).  
- Display all planes with their **owner & admin details**.  

---

### 👨‍✈️ Pilot Module  
- Add new pilots with license, experience, and assigned plane details.  
- Search pilots by **pilot ID or name**.  
- Edit pilot details (license, assigned plane).  
- Delete pilots (only if unassigned from planes).  
- View all pilots.  

---

### 🏠 Hangar Module  
- Create and manage hangar records (**capacity, location, maintenance status**). 
- Delete hangars (only if no planes are allocated).  

---

### 🔄 Plane Allocation Module (Pilot ↔ Plane)  
- Allocate a **pilot to a plane** ensuring a plane is not assigned to multiple pilots at once.  
- Re-allocate pilots when planes change or become unavailable.  
- Remove pilot allocation when pilot retires or plane is grounded.  
- View all allocations with details: **Pilot Name, Plane Number, Status**.  

---

### 🏠 Hangar Status Module (Plane ↔ Hangar)  
- Allocate planes to hangars with **capacity and availability checks**.  
- Re-allocate planes when hangar availability changes (repairs/maintenance).  
- Display all hangars with status overview: **Available, Occupied, Under Maintenance**.  

---

- **Eureka Server**: Service discovery for modular microservice expansion.  

---

## 📸 Screenshots

### Homepage

![homepage](https://github.com/user-attachments/assets/dc9ffba6-6ef0-4767-99a0-addaabfe90e6)

### Registration/Login

![register](https://github.com/user-attachments/assets/87197ac7-d096-42f7-ab4d-64ba0d5cf423)

![login](https://github.com/user-attachments/assets/1e5c4eb9-1d13-4839-a22d-18063bf04f72)

### Plane Module

![plane](https://github.com/user-attachments/assets/4160721a-49a4-4e61-8c4b-9fc2c187bd5e)

### Pilot Module

![pilot](https://github.com/user-attachments/assets/153716c1-3d23-4eda-b3b7-f97b3b431da5)

### Hangar Module

![hangar](https://github.com/user-attachments/assets/fd38215c-0d15-4c13-ba48-6f465aa96ab1)

### Plane Allocation Module

![plane alloc](https://github.com/user-attachments/assets/752bafe4-3723-4521-98f4-bb404cee544f)

### Hangar Allocation Module

![hangar alloc](https://github.com/user-attachments/assets/2a492176-50b6-4122-baaf-ca82985221b4)

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
