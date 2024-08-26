import io.izzel.taboolib.gradle.*
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    `maven-publish`
    id("io.izzel.taboolib") version "2.0.11"
    id("org.jetbrains.kotlin.jvm") version "1.8.22"
}

taboolib {
    env {
        // 安装模块
        install(UNIVERSAL, DATABASE, NMS_UTIL, UI, CHAT)
        install(EXPANSION_PLAYER_DATABASE)
        install(BUKKIT_ALL, VELOCITY)
    }
    version {
        taboolib = "6.1.2-beta10"
    }
}

repositories {
    mavenCentral()
    maven("https://raw.githubusercontent.com/Duckfox/maven-repository/master/")
    maven("https://jitpack.io")
    maven("https://oss.sonatype.org/content/repositories/snapshots")
    maven("https://papermc.io/repo/repository/maven-public/")
    maven("https://repo.xenondevs.xyz/releases")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    maven("https://repo.codemc.io/repository/maven-public/")
}

dependencies {
    compileOnly("org.catmc:catserversrg:11c12cd")
    compileOnly("com.pixelmonmod:pixelmon:8.4.3")
    compileOnly("com.google.guava:guava:31.0.1-jre")
    compileOnly("com.google.code.gson:gson:2.9.1")
    compileOnly("com.github.MilkBowl:VaultAPI:1.7.1")
    compileOnly("me.clip:placeholderapi:2.11.3")
    compileOnly("ink.ptms.core:v11200:11200")
    compileOnly("ink.ptms.core:v11600:11600")
    compileOnly("ink.ptms.core:v11300:11300")
    compileOnly(kotlin("stdlib"))
    compileOnly(fileTree("libs"))
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "1.8"
        freeCompilerArgs = listOf("-Xjvm-default=all")
    }
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

publishing {
    repositories {
        mavenLocal()
    }
    publications {
        create<MavenPublication>("maven") {
            artifactId = "lbapi"
            groupId = "site.liangbai"
            version = project.version.toString()

            artifact(File("build/libs/${rootProject.name}-${project.version}.jar"))
        }
    }
}