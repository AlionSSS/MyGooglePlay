#安全卫士

这是一个Android项目

----------

**名称：电子市场**

**开发环境：Android Studio**

**基于：黑马Android项目-谷歌电子市场**

----------

##详细

本项目基于黑马Android项目-谷歌电子市场，采用-Android studio-进行逐步开发，做了部分修改，并加注了详细标注，方便了部分**Android Studio初学者**的学习与使用。

##build.gradle
	apply plugin: 'com.android.application'
	
	android {
	    compileSdkVersion 23
	    buildToolsVersion "24.0.1"
	
	    defaultConfig {
	        applicationId "com.skey.mobilesafe"
	        minSdkVersion 15
	        targetSdkVersion 23
	        versionCode 1
	        versionName "1.0.0"
	    }
	    buildTypes {
	        release {
	            minifyEnabled false
	            proguardFiles getDefaultProguardFile('proguard-android.txt'), 'proguard-rules.pro'
	        }
	    }
	}
	
	dependencies {
	    compile fileTree(include: ['*.jar'], dir: 'libs')
	    testCompile 'junit:junit:4.12'
	    compile 'com.android.support:appcompat-v7:23.4.0'
            compile 'com.android.support:design:24.2.1'
	}
