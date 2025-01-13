plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.hemaya.mssdemo"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.hemaya.mssdemo"
        minSdk = 24
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    implementation (files("aars/QRCodeScannerSDK.aar"))
    implementation (files("aars/SecureStorageSDK.aar"))
    implementation (files("aars/SecureMessagingSDKClient.aar"))
    implementation (files("aars/UtilitiesSDK.aar"))
    implementation (files("aars/DSAPP_Client.aar"))
    implementation (files("aars/DigipassSDK.aar"))
    implementation (files("aars/DeviceBindingSDK.aar"))
    implementation (files("aars/BiometricSensorSDK.aar"))

    implementation(libs.biometric)
    implementation(libs.bcprov.jdk15on)
    implementation(libs.kryo)
    implementation(libs.androidx.lifecycle.runtime.ktx)


    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.ccp)
    implementation(libs.glide)
    implementation(libs.phonelib)
    annotationProcessor(libs.glidecompiler)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    implementation (libs.retrofit)
    implementation (libs.converter.gson )// For JSON parsing
    implementation (libs.logging.interceptor )// Optional logging
}