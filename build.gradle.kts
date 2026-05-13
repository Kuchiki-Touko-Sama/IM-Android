// Top-level build file where you can add configuration options common to all subprojects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    //id("com.google.devtools.ksp") version "2.2.0" apply false
    id("androidx.room3") version "3.0.0-alpha04" apply false
    id("com.google.devtools.ksp") version "2.3.4" apply false
}