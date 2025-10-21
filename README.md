# ThomasMind

ThomasMind is a Kotlin-based local assistant built with Spring Boot. It processes user input through an LLM client, stores and recalls memories, and can interact with external services such as Bluesky to listen for or post updates. The project is structured as a modular command-line Spring Boot application.

Key features
- LLM-driven reflection engine (via `LLMClient` and `Reflector`).
- Memory management (working and archival memory) via `MemoryService`.
- Tool/strategy pattern for actions (save/get memory, post output to Bluesky).
- Bluesky integration (login, poll, post) in `BlueSkyService`.
- A command-line runner (`ReflectorRunner`) that starts a Bluesky listener thread and presents an interactive prompt.

Repo layout (important files)
- `app/src/main/kotlin/hawk0120/Reflector.kt` - Reflection engine and `ReflectorRunner` (CLI entrypoint).
- `app/src/main/kotlin/hawk0120/services/BlueSkyService.kt` - Bluesky client (login, poll, post).
- `app/src/main/kotlin/hawk0120/tools/PostOutputStrategy.kt` - Strategy to post reflection output to Bluesky.
- `app/src/main/resources/application.properties` - Default application configuration (datasource and Bluesky credentials).
- `gradlew`, `gradle/` - Gradle wrapper and configuration.

Prerequisites
- Java 17+ (or the JDK required by your Gradle config).
- Gradle (you can use the included wrapper `./gradlew`).
- Network access for Bluesky integration if you plan to use live posting/listening.
- PostgreSQL if you want to use the bundled datasource configuration (or update the JDBC URL to your DB).

Configuration
- Default runtime properties are in `app/src/main/resources/application.properties`.
- Important properties to configure:
  - `spring.datasource.url` - JDBC URL for the project's database.
  - `spring.datasource.username` and `spring.datasource.password` - database credentials.
  - `bluesky.username` and `bluesky.password` - credentials used by `BlueSkyService`.

Security note: do not commit secrets to source control. The repo currently contains `application.properties` with sample credentials. Prefer one of these approaches in production:
- Use environment variables: set `BLUESKY_USERNAME` and `BLUESKY_PASSWORD` in your environment and modify the project to prefer env vars or externalized config.
- Use an external config file not tracked by VCS.
- Use a secrets manager.

Running the project
1. Build the project:

```bash
./gradlew build
```

2. Run the application (from the repository root):

```bash
./gradlew :app:bootRun
```

3. The app runs a CLI prompt. Typical interaction is:
- The Bluesky listener thread will be started if `BlueSkyService` can login (using the configured `bluesky.*` properties).
- Use the prompt `User:>>> ` to type a message and observe the `Reflector` response.
- To exit, type `.exit`.

Enable debug startup info

```bash
./gradlew :app:bootRun --debug
```

Troubleshooting
- Crash on startup with `kotlin.UninitializedPropertyAccessException: lateinit property username has not been initialized`
  - Cause: a `BlueSkyService` instance was being created with `BlueSkyService()` instead of using the Spring-managed bean, so `@Value` injection never ran. This has been corrected by using constructor injection for `PostOutputStrategy` so Spring provides the `BlueSkyService` bean.
- If Bluesky login fails:
  - Verify `bluesky.username` and `bluesky.password` in `application.properties` or provide them via environment/external config.
  - Check network connectivity and Bluesky API changes (endpoints may evolve).

Development notes & suggestions
- Prefer constructor injection for services and configuration objects. Consider switching `BlueSkyService` to take credentials via constructor parameters or `@ConfigurationProperties` for better testability.
- Consider removing the dotenv usage or centralizing configuration handling.
- Add unit tests for `PostOutputStrategy` and `BlueSkyService` (mock network calls). A simple test should verify that `PostOutputStrategy` calls the injected `BlueSkyService`.
- Consider adding a small integration test that starts the Spring context and validates wiring.

Common commands
- Build: `./gradlew build`
- Run app: `./gradlew :app:bootRun`
- Run with debug: `./gradlew :app:bootRun --debug`
- Display problems report: `build/reports/problems/problems-report.html` after a build

Contributing
- Open an issue or a pull request. Keep changes small and focused. Use feature branches.

License
- (No license specified) Add a LICENSE file to indicate the project license.

Contact / Maintainer
- Thomas (project maintainer). Update the README with a contact or repository URL if you publish this project.

If you'd like, I can:
- Move Bluesky credentials to environment variable support and update the code to read those env vars safely.
- Add a small unit test for `PostOutputStrategy` using a mocked `BlueSkyService`.
- Add a `README` section with example prompts and sample output from the reflection engine.


