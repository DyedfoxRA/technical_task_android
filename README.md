# Sliide Android developer challenge 
## Congratulations, you have reached the next stage which is solving a Sliide practical test.
We’d like to you to write simple Android application for managing users.

### Description
When we have reviewed your test, and any accompanying documents you feel necessary, if we like what we see, we’ll invite you to join us for a video conversation during which we’ll ask you to go through your test, explaining any decisions that you made.

### Implementation
For implementation we use https://gorest.co.in/ public API

### Functional requirement
Feel free to use whatever flare you can to show off your skills.

You shouldn't spend more than 1 day on implementation, but if you need more time to show the best quality, feel free to use it. We prefer finished, clean, production ready implementation with unit tests, rather than half done solution.

#### 1 Displaying list of users
- After app is open list of users is displayed (only users from last page of the endpoint)
- Each entry contains name, email address and creation time (relative to now)
- Loading and error state are welcome

#### 2 Adding new user
- After + button is clicked pop up dialog is displayed with name and email entries
- After confirmation and successful user creation (201 response code) item is added to the list

#### 3 Removing existing user
- After item long press pop up dialog is displayed with question “Are you sure you want to remove this user?“
- After OK is clicked and user is removed (204 response code) item is deleted from the list

### Technical requirements
- Application must be developed in Kotlin with minimum Android SDK version of 21
- You are free to use whatever frameworks or tools you see fit
- UI preferable in Jetpack Compose
- Application needs to support device rotation
- Design should follow Material design guidelines
- Coroutines
- Architecture MVVM or MVI
- Dependency injection with Dagger 2 or Hilt
- Unit tests

### Evaluation Criteria
- You create testable code
- You pay attention to detail
- Code should be production ready

### Deliverables
- The forked version of this repo

# Implementation Details

## Architecture
The application follows the MVVM (Model-View-ViewModel) architecture pattern. This helps in separating the business logic from the UI and makes the codebase more modular and testable.

## Dependency Injection
Hilt is used for dependency injection to manage the creation and lifecycle of dependencies. This simplifies the code and makes it easier to manage dependencies.

## Data Handling
The app uses Retrofit to interact with the GoRest API and fetch user data. Coroutines are used to handle asynchronous operations.

## UI
Jetpack Compose is used for building the UI. This modern toolkit simplifies UI development and makes the UI code more readable and maintainable.

## Error Handling and Loading States
The app handles loading and error states using Compose’s state management. Appropriate UI feedback is provided to the user during these states.

## Device Rotation
The app supports device rotation, ensuring a seamless user experience across different orientations.

## Added Features and Usage

### Refresh Logic
- **Pull to Refresh**: The app includes a pull-to-refresh feature that allows users to refresh the list of users. Pulling down on the list will fetch the latest data from the server.
- **Load More Users**: When scrolling to the bottom of the list, the app automatically loads more users if available. This ensures that the user can see all users without any additional actions.

### How to Use

- **Display Users**: Open the app to see a list of users from the last page of the endpoint.
- **Add User**:
  - Click the + button.
  - Enter the name and email in the dialog.
  - Click "Add" to create the user. The user will appear in the list if the creation is successful.
- **Remove User**:
  - Long press on a user item.
  - Confirm the deletion in the dialog.
  - The user will be removed from the list if the deletion is successful.
- **Refresh Users**:
  - Pull down on the list to refresh the users. New users, if any, will be added to the top.
  - Scroll to the bottom to load more users if available.

### Running the Project

To run the project, follow these steps:

1. Clone the repository.
2. Open the project in Android Studio.
3. Sync the project to download the dependencies.
4. Run the app on an emulator or physical device.

### Conclusion

This project demonstrates the use of modern Android development practices to create a simple, yet functional, user management app. The use of MVVM architecture, Hilt for dependency injection, and Jetpack Compose for the UI makes the codebase clean, modular, and easy to maintain.



