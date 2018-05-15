# XsbProcessor
注解布局和标题
在跟布局中添加
buildscript {
    ext.kotlin_version = '1.1.51'

    repositories {
        google()
        jcenter()
        maven { url "http://10.100.62.98:8086/nexus/content/groups/public" }// 添加Maven的本地依赖
    }
    dependencies {
        classpath 'com.android.tools.build:gradle:3.0.1'
        classpath 'org.jetbrains.kotlin:kotlin-gradle-plugin:1.2.21'
        classpath 'com.github.dcendents:android-maven-gradle-plugin:2.0' // Add this line

        classpath 'com.xsb.plugin:xsb-ann-plugin:1.0.1'//自定义的geadle插件

        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
        classpath "org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin_version"
    }
}

allprojects {
    repositories {
        google()
        jcenter()
    }
}

task clean(type: Delete) {
    delete rootProject.buildDir
}
在使用的项目中
apply plugin: 'com.xsb.plugin'
