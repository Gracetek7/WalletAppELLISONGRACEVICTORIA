plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.ieschabas.pmdm.walletapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.ieschabas.pmdm.walletapp"
        minSdk = 21
        maxSdk = 32
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
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
    }
    viewBinding {
        enable = true
    }
    dataBinding {
        enable = true
    }
}

dependencies {

    implementation("androidx.fragment:fragment-ktx:1.6.2")
    //AppCompat
    implementation ("androidx.appcompat:appcompat:1.6.1")

    //RecyclerView
    implementation ("androidx.recyclerview:recyclerview:1.3.2")

    // Picasso
    implementation ("com.squareup.picasso:picasso:2.71828")

    // Glide img
    implementation ("com.github.bumptech.glide:glide:4.12.0")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.12.0")

    // Retrofit para hacer peticiones HTTP
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")

    // Convertidor Gson para Retrofit
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")

    // Biblioteca de log para Retrofit (depuración)
    implementation ("com.squareup.okhttp3:logging-interceptor:4.9.1")

    //Inicio sesion Room DataBase
    implementation ("androidx.room:room-runtime:2.6.1")
    implementation("androidx.test.ext:junit-ktx:1.1.5")
    annotationProcessor ("androidx.room:room-compiler:2.6.1")

    //Login dependencia FireBase Authentication
    implementation(platform("com.google.firebase:firebase-bom:32.7.3"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-auth-ktx:22.3.1")

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.7")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.7")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}