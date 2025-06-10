plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services") // CRUCIAL para Firebase
}

android {
    namespace = "com.example.miprimeraaplicacion"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.miprimeraaplicacion"
        minSdk = 22
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.activity:activity:1.8.0")
    implementation("com.google.android.gms:play-services-location:21.0.1")
    implementation("com.itextpdf:itext7-core:8.0.4") // iText 7

    // Firebase BOM (Bill of Materials) - ¡ESTA ES LA CLAVE!
    // Esto asegura que todas tus librerías de Firebase usen versiones compatibles
    implementation(platform("com.google.firebase:firebase-bom:33.1.0")) // <-- ¡Usa la última versión estable! (Actualizado a 33.1.0 a principios de junio de 2025)

    // Dependencia para Firebase Firestore (Kotlin extensions)
    // NO ESPECIFIQUES la versión aquí, el BoM se encarga de ello.
    implementation("com.google.firebase:firebase-firestore-ktx")

    // Si REALMENTE necesitaras Realtime Database (que no parece ser el caso para Conductores):
    // implementation("com.google.firebase:firebase-database-ktx") // Sin especificar versión si usas BoM

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    // Importa la Firebase BoM (Bill of Materials) - ¡Esto es CLAVE!
    // Asegúrate de que la versión sea la última estable (revisa firebase.google.com/docs/android/setup)
    implementation(platform("com.google.firebase:firebase-bom:33.1.0")) // <-- Usa la última versión actual

    // Dependencia para Firebase Realtime Database (KTX para extensiones de Kotlin)
    implementation("com.google.firebase:firebase-database-ktx")

    // Dependencia para Firebase Firestore (si también la sigues usando)
    implementation("com.google.firebase:firebase-firestore-ktx")

}
