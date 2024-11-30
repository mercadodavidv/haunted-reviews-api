# Haunted Reviews
Review site for spooky haunted locations.

## Getting started
1. Make a copy of `.env.example` and name it to `.env.local`. Place in the root directory.

2. OPTIONAL: Set up third-party OAuth providers. You may skip this step and run the application without issues, but you will not be able to use the social login features.

   In `.env.local`, set the client ID and client secret for the OAuth providers that are listed. You will need to obtain your own client credentials for each of these providers. Check the OAuth documentation from the respective providers. The Spring Authorization Server documentation is also useful for this step.

3. If you're setting up your development environment without an IDE:

   The required Java version is defined in the Gradle build scripts in each module&mdash;currently Java 22. Ensure that the JAVA_HOME environment variable points to your installed JDK. Then, from the project directory, run the given command for each service listed below.

## Modules
Each module has a Gradle build script (`build.gradle`), and each service is set up to run independently.

### Service: user-manager
```shell
./gradlew :user-manager:bootRun
```
Handles users and social login. Responsibilities:
- OpenID Provider for Haunted Reviews, utilizing Spring Authorization Server.
- Resource server for user profiles and user account settings.

At present, users must sign in with a social provider to create an account. Additionally, the component [CreateLocalDebugUsers](user-manager/src/main/java/com/mercadodavidv/hauntedreviews/auth/CreateLocalDebugUsers.java) preloads the local in-memory database with some users that you can use to sign in with email and password.

### Service: place-manager
```shell
./gradlew :place-manager:bootRun
```
Resource server for places and reviews.

### Library: data-model
Common data structures and configuration.
