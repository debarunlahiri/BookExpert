# BookExpert

A modern Android application for managing books and items, with features for PDF viewing, image handling, and more.

## Project Structure

The project follows a clean architecture with MVVM (Model-View-ViewModel) pattern:

```
app/src/main/java/com/debarunlahiri/bookexpert/
├── api/                  # API service interfaces
├── data/                 # Data layer
│   ├── db/               # Room database implementation
│   ├── model/            # Data models/entities
│   └── PreferenceDataStore.kt  # DataStore preferences
├── repository/           # Repository layer (connects data sources with ViewModels)
├── ui/                   # UI layer
│   ├── components/       # Reusable UI components
│   ├── navigation/       # Navigation related code
│   ├── screens/          # App screens organized by feature
│   │   ├── auth/         # Authentication screens
│   │   ├── home/         # Home screen
│   │   ├── images/       # Image handling screens
│   │   ├── items/        # Item management screens
│   │   ├── pdf/          # PDF viewer screens
│   │   └── settings/     # Settings screens
│   └── theme/            # Theme and styling
├── viewmodel/            # ViewModels
└── MainActivity.kt       # Main entry point
```

## Architecture

The app follows MVVM architecture with the following components:

1. **UI Layer (View)**: Jetpack Compose UI components (Screens and Components)
2. **ViewModel Layer**: Manages UI-related data and business logic
3. **Repository Layer**: Single source of truth, manages data operations
4. **Data Layer**: Local database (Room) and remote data sources (API)

## Features

- **Item Management**: View, add, update, and delete items with dynamic properties
- **PDF Viewer**: View PDF documents
- **Image Handler**: Process and view images
- **Authentication**: User sign-in functionality
- **Settings**: Application preferences using DataStore

## Technologies Used

- Kotlin
- Jetpack Compose for UI
- Coroutines and Flow for asynchronous operations
- Room for local database
- Retrofit for API communication
- Firebase Authentication
- Material 3 Design

## Setup Instructions

1. Clone the repository
2. Open the project in Android Studio
3. Sync Gradle files
4. Run the application on an emulator or physical device

## Contribution

Feel free to submit issues or pull requests to improve the project. 