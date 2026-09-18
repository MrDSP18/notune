val sqliteTmpDir = layout.buildDirectory.dir("tmp").get().asFile.apply { mkdirs() }
System.setProperty("org.sqlite.tmpdir", sqliteTmpDir.absolutePath)

plugins {
    id("com.android.library")
    id("com.google.devtools.ksp")
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.music.echo.core"
    compileSdk = 36
    defaultConfig { minSdk = 26 }

    testOptions {
        unitTests.isIncludeAndroidResources = true
    }

    sourceSets {
        getByName("test") {
            resources.srcDirs("schemas")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
        isCoreLibraryDesugaringEnabled = true
    }
}
kotlin { jvmToolchain(21) }
dependencies {
    api(libs.datastore)
    api(libs.compose.runtime)
    api(libs.compose.ui)
    api(libs.compose.animation)
    api(libs.room.runtime)
    api(libs.room.ktx)
    api(libs.timber)
    api(libs.ktor.client.core)

    api(libs.apache.lang3)
    api("javax.inject:javax.inject:1")

    api("androidx.core:core-ktx:1.13.1")

    ksp(libs.room.compiler)

    api(libs.media3)
    api(libs.media3.session)
    api(project(":innertube"))
    api(libs.ktor.serialization.json)
    api(libs.protobuf.javalite)
    coreLibraryDesugaring(libs.desugaring)

    testImplementation(libs.room.testing)
    testImplementation(libs.androidx.test.monitor)
    testImplementation(libs.androidx.test.ext.junit)
    testImplementation(libs.robolectric)
    testImplementation(libs.mockk)
    testImplementation(libs.sqlite.jdbc)
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.jetbrains.kotlin:kotlin-test")
}

ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
}
