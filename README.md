# Dicoding Event

Dicoding Event is an Android application developed as part of the Dicoding Belajar Fundamental Android course submission. The app follows the MVVM architecture and utilizes the Dicoding Event API to display upcoming and finished events. Users can favorite events, toggle dark mode, and manage notification settings. This app is designed with performance, best practices, and user experience in mind

## Screenshots

![eventapp-screenshot](https://github.com/user-attachments/assets/a26e6355-2478-4900-8a6e-1d6fa0ba6dd4)

## Table of Contents
- [Features](https://github.com/zyrridian/dicoding-book-app/edit/master/README.md#features)
- [Technologies Used](https://github.com/zyrridian/dicoding-book-app/edit/master/README.md#technologies-used)
- [Architecture](https://github.com/zyrridian/dicoding-book-app/edit/master/README.md#architecture)
- [Getting Started](https://github.com/zyrridian/dicoding-book-app/edit/master/README.md#getting-started)
  - [Prerequisites](https://github.com/zyrridian/dicoding-book-app/edit/master/README.md#prerequisites)
  - [Installation](https://github.com/zyrridian/dicoding-book-app/edit/master/README.md#installation)
- [App Structure](https://github.com/zyrridian/dicoding-book-app/edit/master/README.md#app-structure)
- [Usage](https://github.com/zyrridian/dicoding-book-app/edit/master/README.md#usage)
- [Contributing](https://github.com/zyrridian/dicoding-book-app/edit/master/README.md#contributing)
- [License](https://github.com/zyrridian/dicoding-book-app/edit/master/README.md#license)
- [Contact](https://github.com/zyrridian/dicoding-book-app/edit/master/README.md#contact)

## Features
- Home Page: Displays upcoming and finished events.
- Event Detail Page: Shows detailed event information and links to the event’s webpage.
- Favorite Events: Save and manage favorite events using Room Database.
- Dark Mode: Toggle between light and dark themes in settings.
- Notification Settings: Manage event notifications in settings.
- Internet Connectivity Checker: Displays real-time internet status (online/offline).

## Technologies Used
- Language: Kotlin
- Architecture: MVVM (Model-View-ViewModel)
- Data Binding: LiveData, ViewModel
- Networking: Retrofit, Coroutine, Dicoding Event API
- Local Storage: Room Database
- UI: RecyclerView, Material Components, View Binding
- Others: AndroidX, Navigation Component, Dark Mode, SharedPreferences

## Architecture
This app follows the MVVM (Model-View-ViewModel) architecture pattern, which helps in maintaining a clean separation of concerns, easy testing, and better organization of code:
- Model: Responsible for managing the data (e.g., fetching event data from API and handling Room Database operations).
- View: Displays the data on the UI, observes changes from ViewModel, and reflects them.
- ViewModel: Acts as a bridge between the View and the Model, managing UI-related data and business logic.

## Getting Started
To get a local copy of the project up and running on your machine, follow these steps:

### Prerequisites
Ensure you have the following software installed:
- Android Studio (latest stable version recommended)
- Git (for cloning the repository)

### Installation
1. Clone the repository:
   Open a terminal and run the following command to clone the repository:
    ```git clone https://github.com/zyrridian/dicoding-event-app.git```

2. Open the project in Android Studio:
    - Open Android Studio.
    - Select Open an `existing Android Studio project`.
    - Navigate to the cloned repository and select it.

3. Build the project:
    - Android Studio will automatically build the project.
    - If the build does not start automatically, select `Build > Make Project` from the menu.

4. Run the app:
    - Connect your Android device or use an emulator.
    - Click the green play button to run the app on the device.
  
## App Structure
- Home: Displays two categories of events (Upcoming & Finished) using RecyclerView.
- Detail Page: Displays detailed event information (name, date, description), and provides a link to the event’s webpage.
- Favorites: Users can save events to a favorites list, which is stored using Room Database for persistence.
- Settings:
    - Dark Mode: Users can toggle between light and dark modes.
    - Notification Settings: Manage event notifications (enabled/disabled).
    - Network Connectivity Check: The app checks for internet status and displays a message if the device is offline.

## Usage
Once the app is running, you can:
- Browse Events: Navigate between upcoming and finished events on the home screen.
- View Event Details: Click on any event to see more details, including a link to the event’s webpage.
- Favorite Events: Add events to your favorites list and view them later.
- Customize Settings: Enable dark mode and control notifications from the settings page.
- Offline Handling: The app detects if the device is offline and notifies the user.

## Contributing
Contributions are welcome! If you would like to contribute to the project, please follow these steps:
1. Fork the repository.
2. Create a new branch (`git checkout -b feature/your-feature`).
3. Make your changes.
4. Commit your changes (`git commit -m 'Add some feature'`).
5. Push to the branch (`git push origin feature/your-feature`).
6. Create a pull request.
Please ensure your code follows the existing code style and includes appropriate documentation.

## License
This project is licensed under the MIT License. See the LICENSE file for details.

## Contact
- Author: Rezky Aditia Fauzan
- Email: rezky246@gmail.com
- GitHub: https://github.com/zyrridian
