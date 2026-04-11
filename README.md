# Android + Supabase App

![Android](https://img.shields.io/badge/Platform-Android-3DDC84?style=flat-square&logo=android&logoColor=white)
![Java](https://img.shields.io/badge/Language-Java-ED8B00?style=flat-square&logo=java&logoColor=white)
![Supabase](https://img.shields.io/badge/Backend-Supabase-1D9E75?style=flat-square&logo=supabase&logoColor=white)

---

## 01 — Overview

A mobile application built on Android using Java and XML, backed entirely by Supabase as a Backend-as-a-Service (BaaS). The architecture removes the need for a custom server — authentication, database operations, file storage, and API access are all handled by Supabase out of the box.

---

## 02 — Architecture

<img src="SystemArchitecture.jpg" alt="System Architecture Diagram" width="1200" />

---

## 03 — Tech Stack

### Frontend — Android (Java + XML)
Client interface. Handles UI rendering and sends HTTP requests (`GET`, `POST`, `PATCH`, `DELETE`) to the Supabase API.

### Authentication — Supabase Auth
Manages user registration, login, and session state. Returns a `JWT` token used to authorize all subsequent API requests.

### Backend API — Supabase REST API
Auto-generated REST layer over PostgreSQL. No custom server needed. All endpoints are JWT-secured and map directly to database tables.

### Database — Supabase PostgreSQL
Stores structured application data — user records, app metadata, and image URL references pointing to storage bucket paths.

### File Storage — Supabase Storage (Buckets)
Stores unstructured binary data such as images. Files are organized into named buckets. The public or signed URL is written back to the database after upload, and retrieved by the app on demand.

---

## 04 — Data Flow

1. User interacts with the Android app — triggers a UI action (login, view, upload, etc.)
2. App sends an authentication request to Supabase Auth with user credentials
3. Supabase Auth validates the credentials and returns a signed `JWT` token
4. App attaches the `JWT` to the `Authorization` header of subsequent requests
5. Supabase API processes the request — enforces row-level security using the JWT identity
6. Structured data (records, metadata) is read from or written to the PostgreSQL database
7. Image files are uploaded to a Storage bucket; the returned URL is saved in the database
8. App retrieves and renders data — images are fetched via their public or signed bucket URL

---

## 05 — Design Rationale

Supabase consolidates what would otherwise require separate services — auth server, REST API, relational database, and file storage — into a single managed platform. For a mobile-first project, this means the Android app connects directly to Supabase endpoints with no intermediate backend layer to build or maintain. Scalability, security (row-level policies, JWT enforcement), and real-time capabilities are available without writing server-side code.

**Key benefits:**
- No custom server to build or maintain
- Built-in authentication with JWT session management
- Auto-generated REST API directly from the database schema
- Row-level security enforced at the database level
- File storage and relational database under one platform
