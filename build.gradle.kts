import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.compose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.sqldelight)
}

group = "com.homepantry"
version = "1.0.0"

repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

dependencies {
    // Compose Desktop
    implementation(compose.desktop.currentOs)
    implementation(compose.material3)
    implementation(compose.materialIconsExtended)

    // SQLDelight
    implementation(libs.sqldelight.jdbc.driver)
    implementation(libs.sqldelight.coroutines)
    implementation(libs.sqlite.jdbc)

    // Koin DI
    implementation(libs.koin.core)
    implementation(libs.koin.compose)

    // Voyager Navigation
    implementation(libs.voyager.navigator)
    implementation(libs.voyager.screenmodel)
    implementation(libs.voyager.transitions)
    implementation(libs.voyager.koin)
    implementation(libs.voyager.tab.navigator)

    // Kotlinx
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.datetime)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.swing)

    // Testing
    testImplementation(libs.kotlin.test)
    testImplementation(libs.kotlinx.coroutines.test)
}

sqldelight {
    databases {
        create("PantryDatabase") {
            packageName.set("com.homepantry.db")
        }
    }
}

compose.desktop {
    application {
        mainClass = "com.homepantry.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Msi, TargetFormat.Exe)
            packageName = "WatsFit"
            packageVersion = "1.0.0"
            description = "WatsFit - Food Inventory, Recipes & Workout Manager"
            vendor = "WatsFit"

            windows {
                menuGroup = "WatsFit"
                perUserInstall = true
                dirChooser = true
            }
        }
    }
}
