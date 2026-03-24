# Phase 0 — Setup du projet Android

## Prérequis

- Android Studio Ladybug (2024.2+) ou plus récent
- JDK 17+
- Kotlin 2.0+

---

## Tâches

### T-0.1 : Créer le projet Android Studio

- [ ] Nouveau projet : **Empty Compose Activity**
- [ ] Package : `com.happyrow.android`
- [ ] Min SDK : 26 (Android 8.0)
- [ ] Target SDK : 35
- [ ] Langage : Kotlin
- [ ] Build system : Gradle Kotlin DSL (`.kts`)

### T-0.2 : Configurer le `build.gradle.kts` (module app)

- [ ] Activer Compose
- [ ] Activer Kotlinx Serialization
- [ ] Activer Hilt via kapt (ou KSP)
- [ ] Configurer BuildConfig pour les variables d'env

```kotlin
android {
    namespace = "com.happyrow.android"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.happyrow.android"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0.0"

        // Variables d'environnement via local.properties ou CI
        buildConfigField("String", "SUPABASE_URL", "\"${project.findProperty("SUPABASE_URL") ?: ""}\"")
        buildConfigField("String", "SUPABASE_ANON_KEY", "\"${project.findProperty("SUPABASE_ANON_KEY") ?: ""}\"")
        buildConfigField("String", "API_BASE_URL", "\"${project.findProperty("API_BASE_URL") ?: "https://happyrow-core.onrender.com/event/configuration/api/v1"}\"")
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }
}
```

### T-0.3 : Ajouter les dépendances Gradle

- [ ] Ajouter dans `build.gradle.kts` (app) :

```kotlin
dependencies {
    // Compose BOM
    val composeBom = platform("androidx.compose:compose-bom:2024.10.00")
    implementation(composeBom)
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.activity:activity-compose:1.9.0")
    debugImplementation("androidx.compose.ui:ui-tooling")

    // Navigation
    implementation("androidx.navigation:navigation-compose:2.8.0")

    // Lifecycle + ViewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.0")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.0")

    // Hilt
    implementation("com.google.dagger:hilt-android:2.51")
    kapt("com.google.dagger:hilt-compiler:2.51")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")

    // Retrofit + OkHttp
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // Kotlinx Serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.0")
    implementation("com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter:1.0.0")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.9.0")

    // Supabase Kotlin SDK
    implementation("io.github.jan-tennert.supabase:gotrue-kt:3.0.0")
    implementation("io.ktor:ktor-client-android:3.0.0")

    // Tests
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.2")
    testImplementation("io.mockk:mockk:1.13.10")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.9.0")
    androidTestImplementation(composeBom)
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("com.google.dagger:hilt-android-testing:2.51")
    kaptAndroidTest("com.google.dagger:hilt-compiler:2.51")
}
```

### T-0.4 : Configurer les plugins Gradle

- [ ] Dans `build.gradle.kts` (app) :

```kotlin
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("com.google.dagger.hilt.android")
    kotlin("kapt")
}
```

- [ ] Dans `build.gradle.kts` (project) :

```kotlin
plugins {
    id("com.android.application") version "8.6.0" apply false
    id("org.jetbrains.kotlin.android") version "2.0.20" apply false
    id("org.jetbrains.kotlin.plugin.compose") version "2.0.20" apply false
    id("org.jetbrains.kotlin.plugin.serialization") version "2.0.20" apply false
    id("com.google.dagger.hilt.android") version "2.51" apply false
}
```

### T-0.5 : Créer la structure des packages

- [ ] Créer l'arborescence sous `app/src/main/java/com/happyrow/android/` :

```
di/
domain/model/
domain/repository/
data/remote/api/
data/remote/dto/
data/remote/mapper/
data/repository/
usecases/auth/
usecases/events/
usecases/participants/
usecases/resources/
usecases/contributions/
ui/theme/
ui/navigation/
ui/components/
ui/auth/
ui/home/
ui/events/
ui/profile/
```

### T-0.6 : Configurer Hilt — Application class

- [ ] Créer `HappyRowApplication.kt` :

```kotlin
package com.happyrow.android

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class HappyRowApplication : Application()
```

- [ ] Déclarer dans `AndroidManifest.xml` :

```xml
<application
    android:name=".HappyRowApplication"
    ... >
```

### T-0.7 : Configurer `local.properties`

- [ ] Ajouter au `.gitignore` les secrets
- [ ] Documenter les variables nécessaires :

```properties
SUPABASE_URL=https://xxx.supabase.co
SUPABASE_ANON_KEY=eyJ...
API_BASE_URL=https://happyrow-core.onrender.com/event/configuration/api/v1
```

### T-0.8 : Configurer `MainActivity.kt` de base

- [ ] Setup minimal avec Hilt et Compose :

```kotlin
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HappyRowTheme {
                // Navigation sera ajoutée en phase 5
                Text("HappyRow Android")
            }
        }
    }
}
```

### T-0.9 : Vérifier que le projet compile et s'exécute

- [ ] Build sans erreur
- [ ] L'app s'affiche sur émulateur/device
- [ ] Hilt est correctement initialisé (pas de crash au démarrage)

---

## Critères de validation

- Le projet compile sans erreur
- Toutes les dépendances se résolvent
- L'app démarre et affiche un texte de base
- La structure des packages est en place
- BuildConfig expose les 3 variables d'environnement
