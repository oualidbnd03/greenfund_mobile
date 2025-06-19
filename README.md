# CrowdfundPro Android App

A modern Android application for crowdfunding projects, built with Java and following MVVM architecture.

## Features

- **User Authentication**: Secure login and registration system
- **Project Management**: Browse, create, and manage crowdfunding projects
- **Investment System**: Invest in projects with secure payment processing
- **Social Features**: Comments, likes, and user profiles
- **Real-time Notifications**: Firebase-powered push notifications
- **Dashboard**: Comprehensive overview of investments and activities

## Tech Stack

- **Language**: Java 17
- **Architecture**: MVVM (Model-View-ViewModel)
- **Database**: Room Database (Local) + Retrofit (Remote)
- **UI**: Material Design Components
- **Networking**: Retrofit + OkHttp
- **Image Loading**: Glide
- **Payments**: Stripe Integration
- **Notifications**: Firebase Cloud Messaging
- **Build System**: Gradle

## Getting Started

### Prerequisites

- Android Studio Arctic Fox or later
- Android SDK API 33
- Java 17

### Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/yourusername/crowdfundpro-android.git
   ```

2. Open the project in Android Studio

3. Sync Gradle files and download dependencies

4. Run the app on your device or emulator

## Building APK

### Automatic Build (GitHub Actions)

This repository includes GitHub Actions that automatically build APK files on every push to main/master branch.

1. Push your code to GitHub
2. Go to the Actions tab in your repository
3. Download the APK from the latest workflow run

### Manual Build

```bash
# Debug APK
./gradlew assembleDebug

# Release APK
./gradlew assembleRelease
```

## Project Structure

```
app/
├── src/main/
│   ├── java/
│   │   ├── activities/     # Android Activities
│   │   ├── adapters/       # RecyclerView Adapters
│   │   ├── api/           # API Services
│   │   ├── dao/           # Database Access Objects
│   │   ├── models/        # Data Models
│   │   ├── repositories/  # Repository Pattern
│   │   ├── utils/         # Utility Classes
│   │   └── viewmodels/    # ViewModels
│   └── res/
│       ├── layout/        # UI Layouts
│       ├── values/        # Resources
│       └── drawable/      # Images and Drawables
```

## Configuration

### API Configuration

Update the API base URL in `app/build.gradle`:
```gradle
buildConfigField "String", "API_BASE_URL", "\"https://your-api-url.com/api/\""
```

### Firebase Setup

1. Add your `google-services.json` file to the `app/` directory
2. Update Firebase configuration in the Firebase Console

### Stripe Configuration

Update Stripe keys in `app/build.gradle`:
```gradle
buildConfigField "String", "STRIPE_PUBLISHABLE_KEY", "\"pk_test_your_key\""
```

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Support

For support, email support@crowdfundpro.com or create an issue in this repository.

## Screenshots

[Add screenshots of your app here]

## Download

Download the latest APK from the [Releases](https://github.com/yourusername/crowdfundpro-android/releases) page or from the [Actions](https://github.com/yourusername/crowdfundpro-android/actions) tab. 