# Workout Tracker

A personal full-stack workout tracker for recording training sessions, monitoring body weight, visualizing progress, and generating concise AI-assisted training insights.

## Features

### Workout tracking

- Create exercises and associate them with one or more muscles.
- Classify exercises as bodyweight, machine, cable, or free-weight movements.
- Record workouts with dated sets, reps, and weight.
- Track body-weight entries over time.
- View workout history by exercise.
- Export and import workout data as JSON.

### Analytics

- Estimated one-rep max, average weight per rep, and total volume by exercise.
- Relative strength based on estimated 1RM and body weight.
- Body-weight trends.
- Workout and set distributions by muscle group.
- Strongest exercise for a muscle or muscle group.
- Weekly training-volume comparisons for either a muscle group or an individual muscle.
- Weekly estimated 1RM comparisons for each exercise associated with an individual muscle.
- A combined weekly strength index that normalizes each exercise against its first logged session.
- Configurable week and month lookback periods.

### AI insights

Google Gemini provides analysis for:

- Individual exercise progression.
- Weekly training-volume changes.
- Weekly estimated 1RM changes.
- Workout-count balance across muscle groups.
- Set-count balance across muscle groups.

Chart data and AI analysis use separate requests. This allows charts and statistics to render immediately while the AI card continues loading. The newer analytics prompts are limited to five short sentences and one actionable recommendation.

## Technology stack

### Frontend

- React 19 and TypeScript 5.9
- Vite 7
- Tailwind CSS 4 and DaisyUI 5
- TanStack Query 5 and Axios
- Recharts 3
- pnpm

### Backend

- Java 21
- Spring Boot 4.0.1
- Spring Web MVC and Spring Security
- Spring Data JPA and PostgreSQL
- Google Gen AI SDK 1.36
- Jackson and Lombok
- Maven

## Project structure

```text
workout-tracker/
|-- compose.yml                 # PostgreSQL, API, and frontend services
|-- tracker-app/                # React/Vite frontend
|   |-- src/api/                # Axios request functions
|   |-- src/hooks/              # TanStack Query hooks
|   |-- src/components/         # Forms, charts, cards, and tables
|   `-- src/lib/                # Shared TypeScript DTOs
`-- workout-tracker/            # Spring Boot backend
    `-- src/main/java/com/pxbzi/workout_tracker/
        |-- analytics/          # Focused progress, strength, volume, and AI insight services
        |-- data_transfers/
        |-- exercises/
        |-- gemini/
        |-- muscles/
        |-- weights/
        |-- workouts/
        `-- workout_sets/
```

The backend is organized by domain. Controllers delegate to services, services own business logic and repository access, and API responses use dedicated DTOs. The frontend separates API functions, query hooks, DTO types, and rendering components.

Within the analytics domain, `AnalyticsService` is a compatibility facade over focused exercise-progress, strength, volume, and AI-insight services. Fitness formulas live in a pure calculation helper, while `GeminiService` is limited to provider communication and response mapping.

## Quick start with Docker Compose

### Prerequisites

- Docker Desktop with Docker Compose
- A Google Gemini API key

Create `workout-tracker/.env`:

```env
GEMINI_API_KEY=your_api_key_here
```

Start the complete stack from the repository root:

```bash
docker compose up --build
```

Or run it in the background:

```bash
docker compose up --build -d
```

Services are exposed at:

- Frontend: http://localhost:5173
- Backend API: http://localhost:8080
- PostgreSQL: `localhost:5432`

Useful commands:

```bash
docker compose logs -f
docker compose down
```

Database data is stored in the `postgres_data` Docker volume. Running `docker compose down -v` also deletes that persisted database data.

## Manual development

### Backend

Requirements: Java 21, Maven, PostgreSQL, and a Gemini API key.

```bash
cd workout-tracker
```

Configure the environment for your PostgreSQL instance and Gemini key. For example:

```env
GEMINI_API_KEY=your_api_key_here
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/workout_tracker
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=password
SPRING_JPA_HIBERNATE_DDL_AUTO=update
```

Then run:

```bash
./mvnw spring-boot:run
```

On Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

### Frontend

Requirements: Node.js 22 or later and pnpm.

```bash
cd tracker-app
pnpm install
pnpm dev
```

The Axios client currently targets `http://localhost:8080`.

## API overview

All application endpoints are under `/v1`.

### Core resources

| Resource | Base path | Capabilities |
| --- | --- | --- |
| Workouts | `/v1/workouts` | CRUD, bulk creation, date filtering, exercise filtering |
| Exercises | `/v1/exercises` | CRUD and bulk creation |
| Muscles | `/v1/muscles` | CRUD and bulk creation |
| Weights | `/v1/weights` | CRUD and date-range filtering |
| Data transfer | `/v1/data-transfers` | JSON import/export through request bodies or files |
| Gemini | `/v1/gemini/query` | Direct Gemini query endpoint |

### Analytics endpoints

| Endpoint | Description |
| --- | --- |
| `GET /v1/analytics/progress/exercise` | Estimated 1RM, average weight per rep, and volume for an exercise |
| `GET /v1/analytics/progress/relative-strength` | Exercise strength relative to body weight |
| `GET /v1/analytics/progress/ai-analysis` | Detailed individual-exercise AI analysis |
| `GET /v1/analytics/progress/workouts-breakdown` | Workout counts by muscle group |
| `GET /v1/analytics/progress/workouts-breakdown/ai-analysis` | AI analysis of workout balance |
| `GET /v1/analytics/progress/sets-breakdown` | Set counts by muscle group |
| `GET /v1/analytics/progress/sets-breakdown/ai-analysis` | AI analysis of set balance |
| `GET /v1/analytics/progress/volume-breakdown` | Total volume aggregated by month |
| `GET /v1/analytics/progress/weekly-volume` | Week-over-week volume data |
| `GET /v1/analytics/progress/weekly-volume/ai-analysis` | Concise AI analysis of weekly volume |
| `GET /v1/analytics/progress/weekly-one-rep-max` | Exercise-specific weekly estimated 1RM data for a muscle |
| `GET /v1/analytics/progress/weekly-one-rep-max/ai-analysis` | Concise AI analysis of weekly estimated 1RM |
| `GET /v1/analytics/progress/normalized-strength` | Combined weekly baseline strength index for a muscle group or individual muscle |
| `GET /v1/analytics/strongest-exercises/muscle-groups/{muscleGroup}` | Strongest exercise in a muscle group |
| `GET /v1/analytics/strongest-exercises/muscles/{muscleId}` | Strongest exercise for an individual muscle |
| `GET /v1/analytics/strongest-exercises` | Table-ready strongest exercises for all muscles and muscle groups |
| `GET /v1/analytics/strongest-exercises/ai-analysis` | Concise AI analysis of strongest-exercise coverage |

### Weekly volume examples

Select either `muscleId` or `muscleGroup`, but not both. `numWeeksBack` defaults to `5`; `date` identifies the final comparison week.

```http
GET /v1/analytics/progress/weekly-volume?muscleGroup=BACK&date=2026-07-22
```

```http
GET /v1/analytics/progress/weekly-volume?muscleId=12&date=2026-07-22&numWeeksBack=8
```

### Weekly estimated 1RM example

Weekly estimated 1RM analysis requires an individual muscle. Only exercises that primarily target that muscle are included, exercises are compared only against themselves, and weeks without an exercise are represented as missing data rather than zero strength.

```http
GET /v1/analytics/progress/weekly-one-rep-max?muscleId=9&date=2026-07-22&numWeeksBack=5
```

The response also includes the configured age and latest body-weight entry used as context for AI analysis and bodyweight exercises.

### Normalized strength example

Normalized strength accepts either `muscleGroup` or `muscleId`, but not both. General groups such as `BACK`, `CHEST`, and `LEGS` can be queried directly. Because biceps and triceps both belong to `ARMS`, an `ARMS` request must use the specific biceps or triceps `muscleId`. Each exercise's first session is its `100` baseline. Sessions are averaged per exercise and week before the exercise averages are combined, so frequently performed exercises do not receive extra weight. Weeks without sessions are omitted.

```http
GET /v1/analytics/progress/normalized-strength?muscleGroup=BACK&date=2026-08-05&numWeeksBack=8
```

```http
GET /v1/analytics/progress/normalized-strength?muscleId=9&date=2026-08-05&numWeeksBack=8
```

## Calculations

- Estimated 1RM uses the Epley formula: `weight * (1 + reps / 30)`.
- Total volume is the sum of `weight * reps` across sets.
- Bodyweight movements add the latest recorded body weight to any external load.
- Relative strength compares estimated 1RM with the nearest available body-weight data in the selected period.

## Data model

- `Muscle`: an individual muscle assigned to a muscle group.
- `Exercise`: a movement with an exercise type, one required primary muscle, and one or more muscles worked.
- `ExerciseMuscle`: the join entity connecting exercises and muscles.
- `Workout`: a dated performance of one exercise.
- `WorkoutSet`: reps and weight belonging to a workout.
- `Weight`: a dated body-weight entry.

Muscle-specific and muscle-group analytics use an exercise's primary muscle, so secondary involvement from compound movements does not distort rankings or trends. New exercises require the primary muscle to also appear in their muscles-worked list.

Workout sets cascade with their workout, and removing a set from a workout removes the orphaned database record. Frequently queried dates, exercise IDs, muscle IDs, primary muscles, names, and types are indexed.

## Security and error handling

- The API currently permits all routes and is intended for personal/local use.
- CORS permits the frontend at `http://localhost:5173`.
- CSRF is disabled for the REST API.
- Gemini credentials are loaded from `GEMINI_API_KEY`; never commit `.env` files.
- A global exception handler extends Spring's `ResponseEntityExceptionHandler` so standard MVC errors retain their correct HTTP status codes.

## Verification

Frontend checks:

```bash
cd tracker-app
pnpm run lint
pnpm run build
```

Backend checks:

```bash
cd workout-tracker
./mvnw test
```

The Spring context test requires a configured datasource.

## Author

Built by Argel Hernandez Amaya.
