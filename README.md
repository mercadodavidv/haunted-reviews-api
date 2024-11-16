# Haunted Reviews
Review site for spooky haunted locations.

## Getting started
1. Make a copy of `.env.example` and name it to `.env.local`. Place in the root directory.

2. OPTIONAL: Set up third-party OAuth providers. You may skip this step and run the application without issues, but you will not be able to use the social login features.

    In `.env.local`, set the client ID and client secret for the OAuth providers that are listed. You will need to obtain your own client credentials for each of these providers. Check the OAuth documentation from the respective providers. The Spring Authorization Server documentation is also useful for this step.

## Modules
Each module has a Gradle build script (`build.gradle`), and each service is set up to run independently.
### Service: user-manager
Spring Authorization Server that acts as the OpenID Provider for Haunted Reviews, handling user registration and social login. Also serves user data such as profiles and account settings.
### Service: place-manager
Resource server that manages places and reviews.
### Library: data-model
Common data structures and configuration.
