# Amenouzume

Compose Multiplatform を使用したメディア管理アプリケーション。Android と Desktop (JVM)がターゲット。

## Commands

### Build & Run

```shell
# Desktop (JVM)
./gradlew :composeApp:run

# Android
./gradlew :composeApp:assembleDebug
```

### Format

```shell
./gradlew ktlintFormat
```

### Unit Test

```shell
./gradlew :composeApp:jvmUnitTest
```

### Screenshot Test

```shell
# Verify
./gradlew :composeApp:verifyRoborazziJvm

# Record
./gradlew :composeApp:recordRoborazziJvm
```

### E2E Test

```shell
./gradlew :composeApp:jvmE2ETest
```
