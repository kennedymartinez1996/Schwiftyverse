# Schwiftyverse - A Rick and Morty Character Explorer

<img width="575" height="194" alt="image" src="https://github.com/user-attachments/assets/53a019b5-c324-4a59-bb38-dd4586052dd0" />


*Schwiftyverse* is a modern Android application built to showcase a robust, offline-first architecture for browsing characters from the Rick and Morty TV show. Developed as a technical demonstration for senior-level Android positions, this project emphasizes clean architecture, reactive programming, and a high-quality user experience.

---

## ✨ Core Features

This app is more than just a list. It's a demonstration of modern Android development best practices.

* **Offline-First Architecture**: The app is fully functional without an internet connection after the initial data sync. It uses a local Room database as the **Single Source of Truth**.
* **Background Synchronization**: Utilizes `WorkManager` to perform a comprehensive, one-time data synchronization in the background. The sync process includes a full data reconciliation:
    * ✅ **Adds** new characters from the API.
    * 🔄 **Updates** existing characters if their data has changed.
    * ❌ **Deletes** local characters that are no longer present in the API.
* **Proactive Image Caching**: All character images are proactively downloaded and cached by Coil during the background sync, ensuring a seamless offline experience for both data and visuals.
* **Reactive UI with Jetpack Compose**: The entire UI is built with Jetpack Compose. The `CharacterListViewModel` uses `combine` on multiple `StateFlows` to reactively observe the database and user-input filters, automatically producing a new UI state.
* **Advanced Filtering System**: Users can filter the character list by multiple criteria simultaneously:
    * Name (Search query)
    * Status (Alive, Dead, Unknown)
    * Gender (Male, Female, Genderless)
    * Species & Type (Free-form text)
    * All filters operate instantly on the local database for a fluid UX.
* **Dynamic UI & UX**:
    * **Collapsing Header**: The header, including the search bar and filters, collapses on scroll-down and reappears on scroll-up to maximize screen space.
    * **Real-time Sync Status**: A discreet banner at the bottom of the screen informs the user about the current data synchronization status (Syncing, Synced, Failed, Offline), including a manual refresh button.
    * **Robust Error Handling**: The app gracefully handles scenarios like launching for the first time without an internet connection by showing a themed error message after a timeout, preventing an infinite loading screen.

---

## 🏗️ Architectural Overview

The project follows the principles of **Clean Architecture**, separating concerns into three distinct modules for scalability and maintainability.



### Modules

* **:app**: The presentation layer. Contains all UI-related code (Jetpack Compose), `ViewModels`, and navigation. This module depends on the `:domain` module.
* **:domain**: The business logic layer. Contains the core models, repository interfaces, and use cases. It is a pure Kotlin module with no Android dependencies.
* **:data**: The data layer. Contains the implementation of the repositories, network data sources (Retrofit), local database (`Room`), and the `WorkManager` synchronization logic. This module depends on the `:domain` module.

### Data Flow

The data flows from the data sources inwards to the UI, and UI events flow outwards to the data sources.

**`UI (Compose)` -> `ViewModel` -> `Repository` -> `(WorkManager / Room / Retrofit)`**

---

## 🛠️ Tech Stack & Libraries

This project utilizes a modern tech stack to build a high-performance, maintainable application.

* **Core:**
    * [Kotlin](https://kotlinlang.org/) + [Coroutines](https://kotlinlang.org/docs/coroutines-overview.html) & [Flow](https://kotlinlang.org/docs/flow.html) for asynchronous programming.
* **UI:**
    * [Jetpack Compose](https://developer.android.com/jetpack/compose) for building the entire UI declaratively.
    * [Coil](https://coil-kt.github.io/coil/) for efficient image loading and caching.
* **Architecture & DI:**
    * [MVVM (Model-View-ViewModel)](https://developer.android.com/topic/architecture)
    * [Hilt](https://developer.android.com/training/dependency-injection/hilt-android) for dependency injection.
* **Data:**
    * [Retrofit](https://square.github.io/retrofit/) & [Moshi](https://github.com/square/moshi) for networking.
    * [Room](https://developer.android.com/training/data-storage/room) for local database persistence.
    * [WorkManager](https://developer.android.com/topic/libraries/architecture/workmanager) for reliable background synchronization.
* **Testing:**
    * [JUnit 4](https://junit.org/junit4/) for the testing framework.
    * [MockK](https://mockk.io/) for creating mocks and fakes.
    * [Turbine](https://github.com/cashapp/turbine) for testing Kotlin Flows.

---

## 🧪 Testing Strategy

The project includes a multi-layered testing strategy to ensure code quality and stability, following the principles of the testing pyramid.

* **Unit Tests (`/test`):**
    * The `CharacterListViewModel` is thoroughly tested to verify all filtering logic, combinations of filters, reset functionality, and edge cases like "no results found" and timeout errors.
* **Integration Tests (`/androidTest`):**
    * The `CharacterDao` is tested using an in-memory Room database to validate all core database operations (`upsert`, `delete`, updates on conflict) and to perform a stress test with a large dataset to ensure efficiency.

---

## 🚀 How to Build

1.  Clone the repository: `git clone https://github.com/your-username/schwiftyverse.git`
2.  Open the project in the latest version of Android Studio.
3.  Let Gradle sync and build the project.
4.  Run the `app` configuration on an emulator or a physical device.

---

## 🔮 Future Improvements

While this project meets the MVP requirements, here are some potential senior-level improvements:

* **Delegate Filtering to Database:** Refactor the `Repository` and `DAO` to accept filter parameters. This would move the filtering logic from the `ViewModel`'s memory into a more efficient `WHERE` clause in the Room query, significantly improving performance with very large datasets.
* **Character Detail Screen:** Build out the full, visually rich character detail screen as per the design mockups.
* **UI Testing with Hilt:** Resolve the environment-specific configuration issues with Hilt and MockK in `androidTest` to add a suite of UI tests that verify user interactions and screen state changes.
* **"Stale-While-Revalidate" Strategy:** Implement an automatic data refresh mechanism that triggers a background sync periodically or on app launch, ensuring the user sees fresh data without sacrificing the instant load times of the offline-first approach.
