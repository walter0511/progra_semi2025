// Top-level build file where you can add configuration options common to all sub-projects/modules.
// build.gradle.kts (Project: tu_proyecto)

plugins {
    alias(libs.plugins.android.application) apply false
    // La sintaxis correcta para Kotlin DSL es id("plugin.id").version("X.Y.Z").apply(false)

    id("com.android.library") version "8.8.0" apply false
    id("org.jetbrains.kotlin.android") version "1.8.10" apply false // Usa tu versión actual de Kotlin
    // AGREGAR esta línea
    id("com.google.gms.google-services") version "4.4.1" apply false // Asegúrate de esta versión
}


// En Kotlin DSL, no se usa un bloque buildscript{} de esta manera si usas el bloque plugins{} de arriba.
// Las dependencias se definen en los módulos individuales.
