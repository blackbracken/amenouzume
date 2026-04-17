import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
  alias(libs.plugins.kotlinMultiplatform)
  alias(libs.plugins.androidApplication)
  alias(libs.plugins.composeMultiplatform)
  alias(libs.plugins.composeCompiler)
  alias(libs.plugins.composeHotReload)
  alias(libs.plugins.kotlinSerialization)
  alias(libs.plugins.ktlint)
  alias(libs.plugins.metro)
  alias(libs.plugins.roborazzi)
  alias(libs.plugins.sqldelight)
}

kotlin {
  compilerOptions {
    freeCompilerArgs.addAll(
      "-Xcontext-parameters",
      "-Xexpect-actual-classes",
    )
  }

  androidTarget {
    compilerOptions {
      jvmTarget.set(JvmTarget.JVM_11)
    }
  }

  jvm()

  sourceSets {
    androidMain.dependencies {
      implementation(libs.androidx.activity.compose)
      implementation(libs.sqldelight.driver.android)
    }
    commonMain.dependencies {
      implementation(compose.runtime)
      implementation(compose.foundation)
      implementation(compose.material3)
      implementation(compose.materialIconsExtended)
      implementation(compose.ui)
      implementation(compose.components.resources)
      implementation(compose.preview)
      implementation(libs.androidx.lifecycle.viewmodelCompose)
      implementation(libs.androidx.lifecycle.runtimeCompose)
      implementation(libs.coil.compose)
      implementation(libs.metro.runtime)
      implementation(libs.metro.viewmodel)
      implementation(libs.metro.viewmodel.compose)
      implementation(libs.kotlinx.coroutines.core)
      implementation(libs.kotlinx.datetime)
      implementation(libs.kotlinx.serialization.core)
      implementation(libs.material3.adaptive)
      implementation(libs.molecule.runtime)
      implementation(libs.navigation3.runtime)
      implementation(libs.sqldelight.coroutines)
      implementation(libs.store5)
    }
    commonTest.dependencies {
      implementation(libs.kotlin.test)
      implementation(libs.kotlinx.coroutines.core)
      implementation(libs.turbine)
    }
    jvmMain.dependencies {
      implementation(compose.desktop.currentOs)
      implementation(libs.kotlinx.coroutinesSwing)
      implementation(libs.sqldelight.driver.jvm)
    }
    jvmTest.dependencies {
      implementation(libs.kotlinx.coroutines.test)
      implementation(libs.sqldelight.driver.jvm)
      implementation(libs.roborazzi.compose.desktop)
      @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
      implementation(compose.uiTest)
    }
  }
}

android {
  namespace = "black.bracken.amenouzume"
  compileSdk =
    libs.versions.android.compileSdk
      .get()
      .toInt()

  defaultConfig {
    applicationId = "black.bracken.amenouzume"
    minSdk =
      libs.versions.android.minSdk
        .get()
        .toInt()
    targetSdk =
      libs.versions.android.targetSdk
        .get()
        .toInt()
    versionCode = 1
    versionName = "1.0"
  }
  packaging {
    resources {
      excludes += "/META-INF/{AL2.0,LGPL2.1}"
    }
  }
  buildTypes {
    getByName("release") {
      isMinifyEnabled = false
    }
  }
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
  }
}

dependencies {
  debugImplementation(compose.uiTooling)
}

compose.desktop {
  application {
    mainClass = "black.bracken.amenouzume.MainKt"

    nativeDistributions {
      targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
      packageName = "black.bracken.amenouzume"
      packageVersion = "1.0.0"
    }
  }
}

sqldelight {
  databases {
    create("AppDatabase") {
      packageName.set("black.bracken.amenouzume.db")
    }
  }
}

ktlint {
  android.set(true)
  ignoreFailures.set(false)
  filter {
    exclude { it.file.absolutePath.contains("/build/") }
    exclude("**/util/Catching.kt") // for Context Parameter
  }
}

tasks.register<Test>("jvmUnitTest") {
  group = "verification"
  description = "Runs JVM unit tests (excludes E2E and screenshot tests)."

  val jvmTestTask = tasks.named<Test>("jvmTest").get()
  testClassesDirs = jvmTestTask.testClassesDirs
  classpath = jvmTestTask.classpath
  useJUnit()
  dependsOn("jvmTestClasses")

  filter {
    excludeTestsMatching("black.bracken.amenouzume.e2e.*")
    excludeTestsMatching("*ScreenshotTest")
  }
}

tasks.register<Test>("jvmE2ETest") {
  group = "verification"
  description = "Runs JVM E2E tests."

  val jvmTestTask = tasks.named<Test>("jvmTest").get()
  testClassesDirs = jvmTestTask.testClassesDirs
  classpath = jvmTestTask.classpath
  useJUnit()
  dependsOn("jvmTestClasses")

  filter {
    includeTestsMatching("black.bracken.amenouzume.e2e.*")
  }
}
