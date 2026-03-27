# Movie Review Service♨️📽️

A RESTful backend API built with **Spring Boot** that allows users to manage a movie catalog and post reviews. Built using Spring Data JPA with an H2 in-memory database.

---

## Table of Contents🌿

- [Project Overview](#project-overview🏗️)
- [Technologies Used](#technologies-used)
- [Project Structure](#project-structure)
- [Architecture](#architecture)
- [Data Models](#data-models)
- [API Endpoints](#api-endpoints)
- [Request & Response Flow](#request--response-flow)
- [Postman API Testing](#postman-api-testing)
- [Error Handling](#error-handling)
- [Database](#database)
- [Running the Project](#running-the-project)
- [Running Tests](#running-tests)

---

## Project Overview🏗️

The Movie Review Service is a backend REST API that enables:

- Adding movies to a catalog
- Retrieving all movies with their average rating and total reviews
- Posting reviews (rating 1–10 + comment) for any movie
- Retrieving all reviews for a specific movie
- Deleting a review by its ID

On startup, the application automatically seeds **3 movies** and **5 reviews** via `DataInitializer` so the API is immediately usable.

---

## Technologies Used💡

| Technology | Version | Purpose |
|---|---|---|
| Java | 17 | Programming language |
| Spring Boot | 3.2.0 | Application framework, auto-configuration |
| Spring Web (MVC) | 3.2.0 | REST controllers, HTTP handling |
| Spring Data JPA | 3.2.0 | ORM, repository abstraction |
| Hibernate | 6.x | JPA implementation, SQL generation |
| H2 Database | Runtime | In-memory relational database |
| Spring Validation | 3.2.0 | Request body validation (`@Valid`, `@NotBlank`, `@Min`, `@Max`) |
| Lombok | Latest | Boilerplate reduction (`@Getter`, `@Setter`, `@Builder`) |
| JUnit 5 | 5.x | Unit testing framework |
| Mockito | 5.x | Mocking dependencies in tests |
| MockMvc | 3.2.0 | Controller integration testing |
| Maven | 3.x | Build and dependency management |

---

## Project Structure🌿

```
movie-review-service/
├── pom.xml
└── src/
    ├── main/
    │   ├── java/com/moviereview/
    │   │   ├── MovieReviewApplication.java        # Main entry point (@SpringBootApplication)
    │   │   ├── DataInitializer.java               # Seeds sample data on startup
    │   │   ├── model/
    │   │   │   ├── Movie.java                     # JPA Entity — movies table
    │   │   │   └── Review.java                    # JPA Entity — reviews table
    │   │   ├── dto/
    │   │   │   ├── MovieRequestDTO.java            # Incoming movie payload
    │   │   │   ├── MovieResponseDTO.java           # Outgoing movie payload
    │   │   │   ├── ReviewRequestDTO.java           # Incoming review payload
    │   │   │   └── ReviewResponseDTO.java          # Outgoing review payload
    │   │   ├── repository/
    │   │   │   ├── MovieRepository.java            # JpaRepository<Movie, Long>
    │   │   │   └── ReviewRepository.java           # JpaRepository<Review, Long>
    │   │   ├── service/
    │   │   │   ├── MovieService.java               # Business logic for movies
    │   │   │   └── ReviewService.java              # Business logic for reviews
    │   │   ├── controller/
    │   │   │   ├── MovieController.java            # REST endpoints for /movies
    │   │   │   └── ReviewController.java           # REST endpoints for /reviews
    │   │   └── exception/
    │   │       ├── MovieNotFoundException.java     # Thrown when movie ID not found
    │   │       ├── ReviewNotFoundException.java    # Thrown when review ID not found
    │   │       └── GlobalExceptionHandler.java     # @RestControllerAdvice — central error handler
    │   └── resources/
    │       └── application.properties             # App config, H2 datasource, JPA settings
    └── test/
        └── java/com/moviereview/
            ├── service/
            │   ├── MovieServiceTest.java           # Unit tests for MovieService
            │   └── ReviewServiceTest.java          # Unit tests for ReviewService
            └── controller/
                └── ControllerIntegrationTest.java  # @WebMvcTest controller tests
```

---

## Architecture

The project follows a strict **4-layer architecture**. Each layer only communicates with the layer directly below it.

```
HTTP Client (Postman / Browser / Mobile App)
        │
        ▼  JSON over HTTP
┌─────────────────────────────┐
│      Controller Layer       │  Receives requests, validates input, returns responses
│  MovieController            │  @RestController, @Valid, ResponseEntity
│  ReviewController           │
└──────────────┬──────────────┘
               │ calls
               ▼
┌─────────────────────────────┐
│       Service Layer         │  Business logic, exception throwing, DTO mapping
│  MovieService               │  @Service, @Transactional
│  ReviewService              │
└──────────────┬──────────────┘
               │ calls
               ▼
┌─────────────────────────────┐
│     Repository Layer        │  Database access only — no business logic
│  MovieRepository            │  JpaRepository<Movie, Long>
│  ReviewRepository           │  JpaRepository<Review, Long>
└──────────────┬──────────────┘
               │ managed by Hibernate
               ▼
┌─────────────────────────────┐
│    H2 In-Memory Database    │  movies table + reviews table
│    (via JPA / Hibernate)    │
└─────────────────────────────┘
```

---

## Data Models

### Movie Entity

```java
@Entity
@Table(name = "movies")
public class Movie {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String name;

    @NotBlank
    private String genre;

    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews = new ArrayList<>();
}
```

### Review Entity

```java
@Entity
@Table(name = "reviews")
public class Review {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reviewId;

    @Min(1) @Max(10)
    private Integer rating;

    @NotBlank
    private String comment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id", nullable = false)
    private Movie movie;
}
```

### Relationship — `@OneToMany` / `@ManyToOne`

```
MOVIES table          REVIEWS table
─────────────         ──────────────────────────────────
id  | name            review_id | movie_id (FK) | rating | comment
────|──────           ──────────|───────────────|────────|────────
 1  | Inception              1  |      1        |   9    | "Mind-bending!"
 2  | Interstellar           2  |      1        |   8    | "Great film!"
 3  | The Dark Knight        3  |      2        |  10    | "Emotional!"
                             4  |      2        |   9    | "Stunning visuals!"
                             5  |      3        |  10    | "Ledger is legendary!"
```

One Movie → Many Reviews. The `movie_id` foreign key in the reviews table links them. Deleting a movie automatically deletes all its reviews (`CascadeType.ALL`).

---

## API Endpoints

| Method | URL | Description | Request Body | Response |
|---|---|---|---|---|
| `POST` | `/movies` | Add a new movie | `MovieRequestDTO` | `201` MovieResponseDTO |
| `GET` | `/movies` | Get all movies | None | `200` List\<MovieResponseDTO\> |
| `POST` | `/reviews/{movieId}` | Post a review | `ReviewRequestDTO` | `201` ReviewResponseDTO |
| `GET` | `/reviews/{movieId}` | Get all reviews for a movie | None | `200` List\<ReviewResponseDTO\> |
| `DELETE` | `/reviews/{reviewId}` | Delete a review | None | `204` No Content |

---

## Request & Response Flow

### Full trace — `POST /reviews/1`

```
1.  Client sends:  POST http://localhost:8080/reviews/1
                   Body: { "rating": 9, "comment": "Amazing!" }

2.  ReviewController.addReview(movieId=1, dto)
    └── @Valid checks dto — if rating > 10 or comment blank → 400 Bad Request

3.  ReviewService.addReview(1L, dto)
    └── calls MovieService.getMovieEntityById(1L)
        └── if not found → throws MovieNotFoundException → 404 Not Found

4.  Review entity built:
    review.setRating(9)
    review.setComment("Amazing!")
    review.setMovie(movie)   ← links to Inception

5.  ReviewRepository.save(review)
    └── Hibernate runs: INSERT INTO reviews (rating, comment, movie_id)
                        VALUES (9, 'Amazing!', 1)

6.  Entity mapped to ReviewResponseDTO

7.  Controller returns: ResponseEntity.status(201).body(responseDTO)

8.  Spring serializes to JSON → sent back to client
```

---

## Postman API Testing

**Base URL:** `http://localhost:8080`

> Make sure the Spring Boot app is running before testing.

---

### 1. POST /movies — Add a movie

**Request**
```
Method:  POST
URL:     http://localhost:8080/movies
Headers: Content-Type: application/json
```
```json
{
    "name": "Tanhaji-The Unsung Worrior",
    "genre": "Historical"
}
```

**Response — 201 Created**
```json
{
    "id": 4,
    "name": "Tanhaji-The Unsung Worrior",
    "genre": "Historical",
    "totalReviews": 0,
    "averageRating": null
}
```

---

### 2. GET /movies — Get all movies

**Request**
```
Method:  GET
URL:     http://localhost:8080/movies
Headers: (none required)
```

**Response — 200 OK**
```json
[
    {
        "id": 1,
        "name": "Inception",
        "genre": "Sci-Fi",
        "totalReviews": 2,
        "averageRating": 8.5
    },
    {
        "id": 2,
        "name": "Interstellar",
        "genre": "Sci-Fi",
        "totalReviews": 2,
        "averageRating": 9.5
    },
    {
        "id": 3,
        "name": "The Dark Knight",
        "genre": "Action",
        "totalReviews": 1,
        "averageRating": 10.0
    },
    {
        "id": 4,
        "name": "Tanhaji-The Unsung Worrior",
        "genre": "Historical",
        "totalReviews": 0,
        "averageRating": null
    }
]
```

> Movies 1–3 exist because DataInitializer seeds them on startup automatically.

---

### 3. POST /reviews/{movieId} — Post a review

**Request**
```
Method:  POST
URL:     http://localhost:8080/reviews/1
Headers: Content-Type: application/json
```
```json
{
    "rating": 9,
    "comment": "Absolutely mind-blowing experience!"
}
```

**Response — 201 Created**
```json
{
    "reviewId": 6,
    "movieId": 1,
    "movieName": "Inception",
    "rating": 9,
    "comment": "Absolutely mind-blowing experience!"
}
```

---

### 4. GET /reviews/{movieId} — Get all reviews for a movie

**Request**
```
Method:  GET
URL:     http://localhost:8080/reviews/1
Headers: (none required)
```

**Response — 200 OK**
```json
[
    {
        "reviewId": 1,
        "movieId": 1,
        "movieName": "Inception",
        "rating": 9,
        "comment": "Mind-bending plot, absolutely loved it!"
    },
    {
        "reviewId": 2,
        "movieId": 1,
        "movieName": "Inception",
        "rating": 8,
        "comment": "Great film but requires full attention."
    },
    {
        "reviewId": 6,
        "movieId": 1,
        "movieName": "Inception",
        "rating": 9,
        "comment": "Absolutely mind-blowing experience!"
    }
]
```

---

### 5. DELETE /reviews/{reviewId} — Delete a review

**Request**
```
Method:  DELETE
URL:     http://localhost:8080/reviews/6
Headers: (none required)
Body:    (none)
```

**Response — 204 No Content**
```
(empty body — just the 204 status code)
```

---

## Error Handling

All errors return a consistent JSON structure via `GlobalExceptionHandler` (`@RestControllerAdvice`).

### Error Response Shape

```json
{
    "timestamp": "2026-03-23T10:15:30",
    "status": 404,
    "error": "Not Found",
    "message": "Movie not found with ID: 999"
}
```

---

### 400 — Validation Failed (missing required field)

**Request**
```
POST http://localhost:8080/movies
Body: { "genre": "Action" }
```

**Response — 400 Bad Request**
```json
{
    "timestamp": "2026-03-23T10:15:30",
    "status": 400,
    "error": "Bad Request",
    "message": "Validation failed",
    "details": {
        "name": "Movie name is required"
    }
}
```

---

### 400 — Validation Failed (rating out of range)

**Request**
```
POST http://localhost:8080/reviews/1
Body: { "rating": 15, "comment": "Good" }
```

**Response — 400 Bad Request**
```json
{
    "timestamp": "2026-03-23T10:15:30",
    "status": 400,
    "error": "Bad Request",
    "message": "Validation failed",
    "details": {
        "rating": "Rating must be at most 10"
    }
}
```

---

### 404 — Movie Not Found

**Request**
```
GET http://localhost:8080/reviews/999
```

**Response — 404 Not Found**
```json
{
    "timestamp": "2026-03-23T10:15:30",
    "status": 404,
    "error": "Not Found",
    "message": "Movie not found with ID: 999"
}
```

---

### 404 — Review Not Found

**Request**
```
DELETE http://localhost:8080/reviews/999
```

**Response — 404 Not Found**
```json
{
    "timestamp": "2026-03-23T10:15:30",
    "status": 404,
    "error": "Not Found",
    "message": "Review not found with ID: 999"
}
```

---

## Database

The project uses **H2 in-memory database**. Data is stored in RAM and reset on every restart.

### Configuration (`application.properties`)

```properties
spring.datasource.url=jdbc:h2:mem:moviedb
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

spring.h2.console.enabled=true
spring.h2.console.path=/h2-console
```

### H2 Console

Access the live database in your browser:

```
URL:       http://localhost:8080/h2-console
JDBC URL:  jdbc:h2:mem:moviedb
Username:  sa
Password:  (leave blank)
```

### Seeded Data (on every startup)

| Table | Records |
|---|---|
| movies | Inception (Sci-Fi), Interstellar (Sci-Fi), The Dark Knight (Action) |
| reviews | 2 reviews for Inception, 2 for Interstellar, 1 for The Dark Knight |

---

## Running the Project

### Prerequisites

- Java 17+
- Maven (or use the included `mvnw` wrapper)
- Eclipse / IntelliJ IDEA (optional)

### From Command Line

```bash
# Clone the repository
git clone https://github.com/Harshal-25C/movie-review-service.git
cd movie-review-service

# Run using Maven wrapper (no Maven install needed)
.\mvnw spring-boot:run          # Windows
./mvnw spring-boot:run          # Mac / Linux
```

### From Eclipse

```
Right-click MovieReviewApplication.java
→ Run As → Spring Boot App
```

### Verify it's running

```
http://localhost:8080/movies
```

You should see the 3 seeded movies in JSON format.

---

## Running Tests

```bash
# Run all tests
.\mvnw test

# Run with detailed output
.\mvnw test -Dtest=MovieServiceTest
.\mvnw test -Dtest=ReviewServiceTest
.\mvnw test -Dtest=ControllerIntegrationTest
```

### Test Coverage

| Test Class | Type | What it tests |
|---|---|---|
| `MovieServiceTest` | Unit (Mockito) | addMovie, getAllMovies, getMovieEntityById, MovieNotFoundException |
| `ReviewServiceTest` | Unit (Mockito) | addReview, getReviewsByMovieId, deleteReview, ReviewNotFoundException |
| `ControllerIntegrationTest` | Integration (@WebMvcTest) | All 5 endpoints — happy path + validation errors |

---

## Branch Strategy

```
main
└── dev
    ├── feature/project-setup
    ├── feature/movie-crud
    ├── feature/review-crud
    ├── feature/exception-handling
    ├── feature/dto-layer
    ├── feature/data-seeding
    └── feature/tests
```

Each feature is developed on its own branch, merged into `develop` via `--no-ff` merge, and then `develop` is merged into `main` for release.

---

### Author👨‍💻

[Harshal Choudhary](https://github.com/Harshal-25C) - Software Developer👨‍💻 | Cloud Enthusiast            
B.Tech - `[Computer Science & Engineering]`         
Java | Maven | OOPs | Spring Boot | Spring Data JPA | Clean Architecture 
