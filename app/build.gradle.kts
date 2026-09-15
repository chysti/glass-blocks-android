plugins { id("com.android.application") }

android {
    namespace = "com.stakan.glassblocks"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.stakan.glassblocks"
        minSdk = 23
        targetSdk = 35
        versionCode = 3
        versionName = "1.0.2"
    }

    signingConfigs {
        create("releaseUpload") {
            storeFile = rootProject.file("glass-blocks-upload.jks")
            storePassword = rootProject.file("upload-key-password.txt").readText().trim()
            keyAlias = "glass-blocks-upload"
            keyPassword = storePassword
        }
    }

    buildTypes {
        release {
            signingConfig = signingConfigs.getByName("releaseUpload")
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
}

dependencies {
    implementation("com.google.android.gms:play-services-ads:23.6.0")
}
