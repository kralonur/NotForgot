# NotForgot
[![Codacy Badge](https://app.codacy.com/project/badge/Grade/c8861184643946b5a775306277390b63)](https://www.codacy.com/gh/kralonur/NotForgot/dashboard?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=kralonur/NotForgot&amp;utm_campaign=Badge_Grade)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=kralonur_NotForgot&metric=sqale_rating)](https://sonarcloud.io/dashboard?id=kralonur_NotForgot)
[![API](https://img.shields.io/badge/API-23%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=23)

#### NotForgot - is note taking application. NotForgot saves your notest locally and it can update your notes to cloud.

PS: This application written purely for fun project purposes please do NOT use it to save your notes which contains valuable information.

## Interface
<img width="235" height="500" alt="register-screen" src="https://user-images.githubusercontent.com/18505576/96735188-f979ed80-13c3-11eb-8985-fe575bf8f0c0.png"> <img width="235" height="500" alt="login-screen" src="https://user-images.githubusercontent.com/18505576/96735194-faab1a80-13c3-11eb-81c9-3600a68e465e.png">

<img src="https://user-images.githubusercontent.com/18505576/96733475-2af1b980-13c2-11eb-8777-91938352663d.png" width="235" height="500"> <img src="https://user-images.githubusercontent.com/18505576/96734071-d0a52880-13c2-11eb-9702-b43f764a6701.png" width="235" height="500"> <img width="235" height="500" alt="main-screen-upload" src="https://user-images.githubusercontent.com/18505576/96734876-a6079f80-13c3-11eb-94ca-a199d373cb74.png">

<img width="235" height="500" alt="create-task-screen" src="https://user-images.githubusercontent.com/18505576/96735174-f7179380-13c3-11eb-849f-d2eecdf9ff26.png"> <img width="235" height="500" alt="task-view-screen" src="https://user-images.githubusercontent.com/18505576/96735184-f8e15700-13c3-11eb-9a80-bf867c1b91df.png">

## Project setup

Clone the repo, open the project in Android Studio, hit "Run". Done!

## Architecture
Based on mvvm architecture and repository pattern.

![Architecture diagram](https://user-images.githubusercontent.com/18505576/96736485-54601480-13c5-11eb-8bd1-2308f224d58b.png)

## Tech Stack
- Minimum SDK 23
- MVVM Architecture
- DataBinding
- Written 100% on kotlin language
- Architecture Components (Lifecycle, LiveData, ViewModel, Room Persistence, Navigation)
- [**API**](https://app.swaggerhub.com/apis-docs/LidiaIvanova/NotForgotRestAPI/1.0#/) for synchronization of task(note)
- [**Material Design**](https://material.io/develop/android/docs/getting-started) for implementing material design
- [**Kotlin Coroutines**](https://github.com/Kotlin/kotlinx.coroutines) for threading operations
- [**Jetpack Security**](https://developer.android.com/jetpack/androidx/releases/security) for token encryption
- [**Swipe Refresh Layout**](https://developer.android.com/jetpack/androidx/releases/swiperefreshlayout) for swipe to sync funtion
- [**Retrofit 2**](https://github.com/square/retrofit) for constructing the REST API
- [**Moshi**](https://github.com/square/moshi) for parsing JSON
- [**OkHttp3**](https://github.com/square/okhttp) for implementing interceptor, logging
- [**Lottie**](https://github.com/airbnb/lottie-android) for showing animations
- [**Timber**](https://github.com/JakeWharton/timber) for logging