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

### Test

```shell
./gradlew :composeApp:jvmTest
```

### Screenshot Test

```shell
# Record
./gradlew :composeApp:recordRoborazziJvm

# Verify
./gradlew :composeApp:verifyRoborazziJvm
```
