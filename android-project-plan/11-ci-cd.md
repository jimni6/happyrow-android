# Phase 10 — CI/CD et Release

Pipeline d'intégration continue, build automatique, et publication sur le Google Play Store.

---

## Tâches

### T-10.1 : GitHub Actions — Build & Test

- [ ] Créer `.github/workflows/android-ci.yml`

```yaml
name: Android CI

on:
  push:
    branches: [main, develop]
  pull_request:
    branches: [main]

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4

      - name: Set up JDK 17
        uses: actions/setup-java@v4
        with:
          java-version: '17'
          distribution: 'temurin'

      - name: Cache Gradle
        uses: actions/cache@v4
        with:
          path: |
            ~/.gradle/caches
            ~/.gradle/wrapper
          key: gradle-${{ hashFiles('**/*.gradle*', '**/gradle-wrapper.properties') }}

      - name: Grant execute permission
        run: chmod +x gradlew

      - name: Create local.properties
        run: |
          echo "SUPABASE_URL=${{ secrets.SUPABASE_URL }}" >> local.properties
          echo "SUPABASE_ANON_KEY=${{ secrets.SUPABASE_ANON_KEY }}" >> local.properties
          echo "API_BASE_URL=${{ secrets.API_BASE_URL }}" >> local.properties

      - name: Build debug
        run: ./gradlew assembleDebug

      - name: Run unit tests
        run: ./gradlew testDebugUnitTest

      - name: Upload test results
        if: always()
        uses: actions/upload-artifact@v4
        with:
          name: test-results
          path: app/build/reports/tests/

  lint:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v4
        with:
          java-version: '17'
          distribution: 'temurin'
      - run: chmod +x gradlew
      - run: ./gradlew lint
      - uses: actions/upload-artifact@v4
        if: always()
        with:
          name: lint-results
          path: app/build/reports/lint-results-debug.html
```

### T-10.2 : GitHub Actions — Tests instrumentés (optionnel)

- [ ] Ajouter un job avec Android Emulator pour les tests Espresso

```yaml
  instrumented-tests:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v4
        with:
          java-version: '17'
          distribution: 'temurin'
      - name: Run instrumented tests
        uses: reactivecircus/android-emulator-runner@v2
        with:
          api-level: 30
          script: ./gradlew connectedDebugAndroidTest
```

### T-10.3 : Signing configuration

- [ ] Générer un keystore pour la release :
  ```bash
  keytool -genkey -v -keystore happyrow-release.jks -keyalg RSA -keysize 2048 -validity 10000 -alias happyrow
  ```

- [ ] Configurer dans `build.gradle.kts` :

```kotlin
android {
    signingConfigs {
        create("release") {
            storeFile = file(project.findProperty("KEYSTORE_PATH") ?: "happyrow-release.jks")
            storePassword = project.findProperty("KEYSTORE_PASSWORD")?.toString() ?: ""
            keyAlias = project.findProperty("KEY_ALIAS")?.toString() ?: "happyrow"
            keyPassword = project.findProperty("KEY_PASSWORD")?.toString() ?: ""
        }
    }
    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            signingConfig = signingConfigs.getByName("release")
        }
    }
}
```

- [ ] Stocker le keystore et les mots de passe dans les GitHub Secrets

### T-10.4 : ProGuard / R8 rules

- [ ] Configurer `proguard-rules.pro` pour :
  - Kotlinx Serialization (garder les data classes annotées `@Serializable`)
  - Retrofit (garder les interfaces API)
  - Supabase SDK
  - OkHttp

```proguard
# Kotlinx Serialization
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt
-keepclassmembers class kotlinx.serialization.json.** { *** Companion; }
-keepclasseswithmembers class com.happyrow.android.data.remote.dto.** { *; }

# Retrofit
-keepattributes Signature, InnerClasses, EnclosingMethod
-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}

# OkHttp
-dontwarn okhttp3.**
-dontwarn okio.**
```

### T-10.5 : GitHub Actions — Build Release APK/AAB

- [ ] Créer `.github/workflows/android-release.yml`

```yaml
name: Android Release

on:
  push:
    tags:
      - 'v*'

jobs:
  release:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v4
        with:
          java-version: '17'
          distribution: 'temurin'

      - name: Decode keystore
        run: echo "${{ secrets.KEYSTORE_BASE64 }}" | base64 --decode > app/happyrow-release.jks

      - name: Build release AAB
        run: ./gradlew bundleRelease
        env:
          KEYSTORE_PATH: happyrow-release.jks
          KEYSTORE_PASSWORD: ${{ secrets.KEYSTORE_PASSWORD }}
          KEY_ALIAS: ${{ secrets.KEY_ALIAS }}
          KEY_PASSWORD: ${{ secrets.KEY_PASSWORD }}

      - name: Upload AAB
        uses: actions/upload-artifact@v4
        with:
          name: release-aab
          path: app/build/outputs/bundle/release/*.aab

      - name: Create GitHub Release
        uses: softprops/action-gh-release@v1
        with:
          files: app/build/outputs/bundle/release/*.aab
```

### T-10.6 : Versioning automatique

- [ ] Utiliser les tags Git pour la version :

```kotlin
// build.gradle.kts
val versionFromTag = providers.exec {
    commandLine("git", "describe", "--tags", "--abbrev=0")
}.standardOutput.asText.get().trim().removePrefix("v")

android {
    defaultConfig {
        versionName = versionFromTag.ifEmpty { "1.0.0" }
        versionCode = /* dériver du tag ou du CI build number */
    }
}
```

### T-10.7 : Google Play Store — Première publication

- [ ] Créer un compte développeur Google Play ($25 one-time)
- [ ] Créer la fiche de l'application
- [ ] Configurer les screenshots, description, icône
- [ ] Uploader le premier AAB en test interne
- [ ] Optionnel : automatiser avec `r8-rules` et `upload-google-play` GitHub Action

### T-10.8 : Fastlane (optionnel — automatisation avancée)

- [ ] Installer Fastlane
- [ ] Configurer `Fastfile` pour :
  - `fastlane beta` → upload vers test interne
  - `fastlane release` → upload vers production
- [ ] Intégrer dans le workflow CI

---

## Secrets GitHub nécessaires

| Secret | Description |
|--------|-------------|
| `SUPABASE_URL` | URL Supabase |
| `SUPABASE_ANON_KEY` | Clé anonyme Supabase |
| `API_BASE_URL` | URL de l'API backend |
| `KEYSTORE_BASE64` | Keystore encodé en base64 |
| `KEYSTORE_PASSWORD` | Mot de passe du keystore |
| `KEY_ALIAS` | Alias de la clé |
| `KEY_PASSWORD` | Mot de passe de la clé |

---

## Critères de validation

- Le build CI passe sur chaque PR (build + tests + lint)
- Le build release génère un AAB signé
- Les tags `v*` déclenchent automatiquement un build release
- ProGuard/R8 ne casse pas l'app en release
- L'AAB peut être uploadé sur le Play Store
