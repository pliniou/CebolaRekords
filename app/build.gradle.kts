plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    // REFINAMENTO: Migração de kapt para KSP para melhor performance de compilação.
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
}

android {
    namespace = "com.cebola.rekords"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.cebola.rekords"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            // AVISO: A configuração de assinatura de release deve ser gerenciada de forma segura,
            // por exemplo, com variáveis de ambiente ou um arquivo keystore.properties não versionado.
            signingConfig = signingConfigs.getByName("debug")
        }
        debug {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        // REFINAMENTO: Atualizado para Java 17 para alinhar com práticas modernas.
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        // REFINAMENTO: Atualizado para JVM 17.
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.14"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // REFINAMENTO: Todas as dependências agora usam o version catalog (libs) para centralização e consistência.
    // Core & Appcompat
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.splashscreen)

    // Compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.bundles.compose) // Bundle para agrupar dependências de compose
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.hilt.navigation.compose)
    implementation(libs.androidx.compose.ui.tooling.preview)
    debugImplementation(libs.androidx.compose.ui.tooling)

    // Media3 (ExoPlayer)
    implementation(libs.bundles.media3) // Bundle para agrupar dependências de media3

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler) // MUDANÇA: de kapt para ksp

    // Room
    implementation(libs.bundles.room) // Bundle para agrupar dependências de room
    ksp(libs.room.compiler) // MUDANÇA: de kapt para ksp

    // DataStore
    implementation(libs.androidx.datastore.preferences)

    // Coil (Image Loading)
    implementation(libs.coil.compose)

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}