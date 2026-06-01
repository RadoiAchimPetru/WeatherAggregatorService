# Weather Aggregator Service

A robust, full-stack weather aggregator service built with Java (Quarkus) for the backend and React for the user interface. The application fetches current weather conditions from the public Open-Meteo API based on a city name, maps World Meteorological Organization (WMO) codes to human-readable text strings, persists the metrics into a database, and exposes clean REST endpoints for clients to query stored logs.

## 🏗️ Architectural Decisions & Tradeoffs

Hexagonal Architecture (Ports & Adapters)
The backend architecture strictly adheres to Hexagonal Architecture principles to separate core business rules from infrastructure details:

Core Domain: The core packages (com.weather.domain) maintain zero dependencies on external frameworks, database persistence libraries, or HTTP network clients. It encapsulates pure business logic, exceptions (CityNotFoundException, WeatherFetchException), and domain models (WeatherReading).

Ports: Inbound (WeatherUseCase) and Outbound (WeatherProvider, WeatherRepository) ports act as explicit contracts defining how data enters and leaves the application domain boundaries.

Adapters: Infrastructure layers are thoroughly decoupled into pluggable adapters:

in.rest.WeatherResource (REST API Input Channel handling HTTP requests)

out.db.DatabaseAdapter (PostgreSQL Persistence via Hibernate and Panache entities)

out.http.OpenMeteoAdapter (External Weather Client targeting the Open-Meteo API services)

Architectural Tradeoffs
Boilerplate Mapping vs. Domain Purity: Utilizing separate mapping objects for the database persistence layer (WeatherEntity) and the core domain engine (WeatherReading) introduces additional data translation layers. However, this tradeoff shields domain models from being contaminated with framework-specific annotations (JPA/Hibernate), making changes to infrastructure safer and simpler.

Real Databases for Testing vs. Execution Speed: We explicitly chose Testcontainers over an in-memory database configuration (like H2) for our integration test setup. Spinning up a real Dockerized PostgreSQL instance adds a few extra seconds to the pipeline execution, but it guarantees that database scripts, constraints, and index behaviors match the production environment exactly.

## ⚙️ How to Run the Application Locally
Prerequisites
Java 17 installed and configured

Node.js (v18+) installed

Docker Desktop running locally (critical for running automated test containers and development services)

### 1. Start the Backend (Quarkus)
Navigate to the backend directory and launch Quarkus in development mode:

```
cd backend
./mvnw quarkus:dev
```

Note: Quarkus automatically provisions a live Dev Services PostgreSQL container using your local Docker engine, meaning no manual database setup or configuration is required.

### 2. Start the Frontend (React)
Open a separate terminal window, navigate to the frontend directory, install the required node modules, and boot up the development server:

```
cd frontend
npm install
npm start
``` 

The React application will launch instantly and can be accessed inside your browser at http://localhost:3000.

## 🧪 How to Run Each Test Suite Separately
As mandated by the challenge constraints, all test layers are isolated and executable via explicit commands.

### 1. Execute All Backend Tests Together
To run every single backend verification suite (Unit, Contract, Integration, and BDD Scenarios) in one unified monolithic execution command:

```
cd backend
./mvnw clean test
```

### 2. Pure Unit Tests (Domain & Service Isolation)
Validates core service components (WeatherServiceTest) using lightweight Mockito stubs for database and network ports, running isolated code paths without bootstrapping heavy application contexts:


```
cd backend
./mvnw test -Dtest=WeatherServiceTest
```

### 3. Consumer-Driven Contract Tests (Pact)
Ensures that communication schemas match expectations between our infrastructure adapter (OpenMeteoAdapter) and the targeted external payloads. It outputs a local Pact contract file and verifies it against a local stub engine:


```
cd backend
./mvnw test -Dtest=OpenMeteoContractTest
```

### 4. Database Integration Tests (Testcontainers)
Bootstraps an authentic PostgreSQL instance via Testcontainers inside Docker to test database read/write actions end-to-end against the DatabaseAdapter layer without utilizing mock objects:


```
cd backend
./mvnw test -Dtest=DatabaseAdapterIT
```

### 5. Behavior-Driven Development (BDD Scenarios via Cucumber)
Executes user-facing acceptance workflows detailed in Gherkin syntax (weather.feature). These tests run against a compiled application utilizing WireMock to mimic the external weather API payloads while sharing the identical Testcontainers base setup:


```
cd backend
./mvnw test -Dtest=CucumberRunnerTest
```

### 6. Frontend Component Tests (React Testing Library)
Validates UI rendering, input handling, and mock element clicks inside the React ecosystem using React Testing Library:


```
cd frontend
npm test -- --watchAll=false
```
