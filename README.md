# Workout Tracker

A full-stack application for tracking gym progress, exercises, and visualizing workout analytics. This application helps you monitor your fitness journey by recording exercises, sets, reps, weights, and body weight over time, with **AI-powered progress analysis** using Google's Gemini.

## 📋 Overview

Workout Tracker is a comprehensive fitness tracking solution built with a modern tech stack. It consists of two main components:
- **Backend API**: A Spring Boot REST API that handles data persistence, business logic, and AI-powered analytics
- **Frontend App**: A React-based web interface for data entry and visualization

## 🏗️ Architecture

### Backend (`workout-tracker`)
- **Framework**: Spring Boot 4.0.1
- **Language**: Java 21
- **Database**: PostgreSQL
- **ORM**: Spring Data JPA
- **Security**: Spring Security
- **AI Integration**: Google Gemini SDK
- **Build Tool**: Maven

#### Key Features:
- RESTful API endpoints for all workout-related operations
- Entity relationships for exercises, muscles, workouts, and sets
- Analytics service for workout insights
- **AI-powered exercise progression analysis using Google Gemini**
- PostgreSQL database with optimized indexing
- Docker support for containerized deployment

#### API Endpoints:
- `/v1/workouts` - Manage workout sessions
  - `GET /dates` - Get workouts by date
  - `GET /exercises` - Get workouts by exercise
  - `POST /bulk` - Bulk create workouts
- `/v1/exercises` - CRUD operations for exercises
  - `POST /bulk` - Bulk create exercises
- `/v1/muscles` - Manage muscle groups and individual muscles
  - `POST /bulk` - Bulk create muscles
- `/v1/weights` - Track body weight over time
  - `GET /dates` - Get weights by date range
- `/v1/analytics` - Retrieve workout analytics and statistics
  - `GET /progress/exercise` - Exercise-specific analytics (ORM, avg weight, volume)
  - `GET /progress/relative-strength` - Relative strength over time
  - `GET /progress/ai-analysis` - AI-powered progression analysis
  - `GET /progress/workouts-breakdown` - Workout distribution by muscle group
  - `GET /strongest-exercises/muscle-groups/{muscleGroup}` - Strongest exercise by muscle group
  - `GET /strongest-exercises/muscles/{muscleId}` - Strongest exercise by muscle
- `/v1/gemini` - AI chat interface
  - `POST /query` - Send queries to Gemini AI

### Frontend (`tracker-app`)
- **Framework**: React 19.2.0 with TypeScript
- **Build Tool**: Vite 7.2
- **Styling**: Tailwind CSS 4.1 + DaisyUI 5.5
- **Data Fetching**: TanStack Query (React Query) + Axios
- **Charts**: Recharts 3.7

#### Key Features:
- **Data Entry Forms**:
  - Add new workouts with sets, reps, and weights
  - Create custom exercises with muscle group mappings
  - Define new muscle groups
  - Track body weight entries

- **Analytics Dashboard**:
  - **One Rep Max (ORM)** tracking over time
  - **Average Weight Per Rep** analysis
  - **Total Volume** (weight × reps) tracking
  - **Relative Strength** visualization (strength relative to body weight)
  - **Body Weight** tracking with line charts
  - **Workout Breakdown** by muscle group (pie chart)
  - Configurable time range (months back)
  - Exercise-specific filtering

- **AI-Powered Insights**:
  - Automatic exercise progression analysis
  - Personalized feedback on workout performance
  - AI analysis cards in workout history view

- **Data Visualization**:
  - Interactive line charts for progress tracking
  - Pie charts for workout distribution
  - Tabular data display with smart formatting
  - View workout history by exercise

- **User Interface**:
  - Clean, modern UI with DaisyUI components
  - Responsive design
  - Real-time data updates with React Query
  - Dynamic exercise filtering by muscle group
  - Three main views: Data Entry, Analytics, Tables

## 🚀 Getting Started

### Quick Start with Docker Compose (Recommended)

The easiest way to run the entire application stack is using Docker Compose:

```bash
docker compose up
```

This single command will:
- Spin up a PostgreSQL database container
- Build and start the Spring Boot API
- Build and start the React frontend
- Configure all networking and environment variables automatically

**Services will be available at:**
- Frontend: `http://localhost:5173`
- Backend API: `http://localhost:8080`
- PostgreSQL: `localhost:5432`

To stop all services:
```bash
docker compose down
```

To rebuild after code changes:
```bash
docker compose up --build
```

### Manual Setup

If you prefer to run the services individually without Docker:

#### Prerequisites
- Java 21 or higher
- Node.js 21 or higher
- PostgreSQL database
- Maven (for backend)
- pnpm (for frontend)
- **Google Gemini API Key** (for AI features)

#### Backend Setup

1. Navigate to the backend directory:
```bash
cd workout-tracker/workout-tracker
```

2. Configure your PostgreSQL database connection in `src/main/resources/application.properties`

3. **Set up your Gemini API Key**:
   Create a `.env` file in the `workout-tracker` directory with:
   ```
   GEMINI_API_KEY=your_api_key_here
   ```
   > ⚠️ **Security Note**: Never commit your API key to version control. The `.env` file should be in `.gitignore`.

4. Build and run the application:
```bash
mvn clean install
mvn spring-boot:run
```

The API will be available at `http://localhost:8080`

#### Frontend Setup

1. Navigate to the frontend directory:
```bash
cd tracker-app
```

2. Install dependencies:
```bash
pnpm install
```

3. Start the development server:
```bash
pnpm dev
```

The app will be available at `http://localhost:5173`

## 🐳 Docker Deployment

### Docker Compose (Recommended)

The project includes a `compose.yml` file in the root directory that orchestrates all services:

**Services:**
- **db**: PostgreSQL 16 database with persistent volume storage
- **api**: Spring Boot backend (auto-built from Dockerfile)
- **app**: React frontend (auto-built from Dockerfile)

**Features:**
- Automatic service dependency management
- Health checks to ensure database is ready before starting API
- Persistent data storage with Docker volumes
- Environment variable configuration (including Gemini API key via `.env` file)
- Automatic network creation for service communication

```bash
# Start all services
docker compose up

# Start in detached mode
docker compose up -d

# View logs
docker compose logs -f

# Stop all services
docker compose down

# Remove volumes (deletes data)
docker compose down -v
```

### Individual Docker Builds

If you prefer to build and run containers individually:

#### Backend:
```bash
cd workout-tracker/workout-tracker
docker build -t workout-tracker-api .
docker run -p 8080:8080 -e GEMINI_API_KEY=your_key workout-tracker-api
```

#### Frontend:
```bash
cd tracker-app
docker build -t workout-tracker-app .
docker run -p 5173:5173 workout-tracker-app
```

## 📊 Data Model

### Core Entities:
- **Muscle**: Individual muscles and muscle groups (Chest, Back, Shoulders, Legs, Arms, Core)
- **Exercise**: Workout exercises linked to primary muscle groups and multiple muscles worked
- **Workout**: Individual workout sessions linked to specific exercises
- **WorkoutSet**: Sets within a workout (weight + reps)
- **Weight**: Body weight tracking entries with timestamps

### Relationships:
- Exercises can target multiple muscles (many-to-many)
- Each exercise belongs to one primary muscle group
- Workouts are associated with one exercise
- Workouts contain multiple sets
- Weight entries are tracked independently with timestamps

## 🛠️ Technology Stack

### Backend
- Spring Boot 4.0.1
- Spring Data JPA
- Spring Security
- Spring Web MVC
- PostgreSQL Driver
- Google Gemini SDK 1.36.0
- Jackson JSR310 (Date/Time serialization)
- Lombok
- Maven

### Frontend
- React 19.2.0
- TypeScript 5.9
- Vite 7.2
- Tailwind CSS 4.1
- DaisyUI 5.5
- TanStack Query 5.90
- Axios 1.13
- Recharts 3.7

## 📝 Development

### Backend Development
- Hot reload enabled with Spring Boot DevTools
- Lombok annotations for boilerplate reduction
- Service layer pattern for business logic
- Repository pattern with Spring Data JPA
- Modular package structure (analytics, exercises, gemini, muscles, weights, workouts)

### Frontend Development
- TypeScript for type safety
- Custom hooks for data fetching
- Component-based architecture
- Form state management with React hooks
- API client configuration with Axios
- Reusable chart components (LineGraph, PieGraph)

## 🔒 Security

The backend includes Spring Security for authentication and authorization (configuration details in the security config).

**API Key Security**: The Gemini API key is loaded from environment variables and should never be hardcoded or committed to version control.

## 📈 Future Enhancements

- Export workout data (CSV/PDF)
- Mobile-responsive improvements
- User authentication and multi-user support? (maybe)
- Workout templates and routines

## 👤 Author

Built by Argel Hernandez Amaya
