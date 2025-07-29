import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("org.jetbrains.kotlin.plugin.compose") version "2.0.0"
    id("com.google.gms.google-services")
}

val localProperties = Properties().apply {
    val file = rootProject.file("local.properties")
    if (file.exists()) {
        FileInputStream(file).use { load(it) }
    }
}

android {
    namespace = "com.motgolla"
    compileSdk = 36

    defaultConfig {
        buildConfigField("String", "BASE_URL", "\"${localProperties["BASE_URL"]}\"")
        buildConfigField("String", "KAKAO_APP_KEY", "\"${localProperties["KAKAO_APP_KEY"]}\"")
        manifestPlaceholders["kakaoScheme"] = "kakao${localProperties["KAKAO_APP_KEY"]}"
        applicationId = "com.motgolla"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
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
    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures {
        buildConfig = true
        viewBinding = true
        compose = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.play.services.location)
//    implementation(libs.androidx.navigation.compose.jvmstubs)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // 시간
    implementation("com.jakewharton.threetenabp:threetenabp:1.4.5")

    // retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.9.3")

    // gson
    implementation("com.google.code.gson:gson:2.9.0")

    // Compose BOM (버전 통합 관리)
    val composeBom = platform("androidx.compose:compose-bom:2024.05.00")
    implementation(composeBom)

    // Jetpack Compose UI
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.activity:activity-compose")
    implementation("androidx.navigation:navigation-compose:2.7.5")
    implementation("io.coil-kt:coil-compose:2.5.0") // 최신 버전 확인
    implementation("io.coil-kt:coil-gif:2.5.0")

    // Kakao
    implementation("com.kakao.sdk:v2-all:2.15.0") // 전체 모듈 설치, 2.11.0 버전부터 지원

    // 아이콘
    implementation("androidx.compose.material:material-icons-extended")

    //OkHttp (optional)
    implementation("com.squareup.okhttp3:okhttp:4.11.0")

    // Debug 전용 - Compose Preview용
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    //스플래시 화면 - Lottie
    implementation("com.airbnb.android:lottie-compose:6.4.0")
    implementation("androidx.core:core-splashscreen:1.0.0-beta01") //기본 스플래시 삭제
    implementation("io.coil-kt:coil-compose:2.4.0")
    implementation("io.coil-kt:coil-gif:2.4.0")

    //아이콘
    implementation("androidx.compose.material:material-icons-extended")

    // URL 또는 URI 이미지 로더
    implementation("io.coil-kt:coil-compose:2.4.0")

    // 화면이동
    implementation("androidx.navigation:navigation-compose:2.7.7")

    // Fragment에서 ViewModel을 안전하고 간단하게 생성
    implementation("androidx.fragment:fragment-ktx:1.6.2")

    // 바코드 스캔
    implementation("com.google.mlkit:barcode-scanning:17.2.0")
    implementation("com.google.mlkit:vision-common:17.3.0")

    //슬라이드 이미지
    implementation("com.google.accompanist:accompanist-pager:0.30.1")
    implementation("com.google.accompanist:accompanist-pager-indicators:0.30.1")

    //Firebase
    implementation(platform("com.google.firebase:firebase-bom:34.0.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-messaging-ktx:23.4.1")


    // slider
    implementation("androidx.compose.foundation:foundation:1.6.0")

    implementation("com.kizitonwose.calendar:core:2.7.0")
    implementation("com.kizitonwose.calendar:compose:2.7.0")
}