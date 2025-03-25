import io.izzel.taboolib.gradle.*
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    `maven-publish`
    id("io.izzel.taboolib") version "2.0.22"
    id("org.jetbrains.kotlin.jvm") version "1.8.22"
}

taboolib {
    subproject = true
    env {
        // 安装模块
        install(Basic, Bukkit, BukkitNMS, BukkitUI, BukkitNMSUtil, BukkitNMSItemTag, BukkitUtil, BukkitFakeOp)
        install(MinecraftChat)
        install(AlkaidRedis, Database)
    }
    version {
        taboolib = "6.2.2"
    }
}

repositories {
    mavenCentral()
    mavenLocal()
    maven("https://raw.githubusercontent.com/Duckfox/maven-repository/master/")
    maven("https://jitpack.io")
    maven("https://oss.sonatype.org/content/repositories/snapshots")
    maven("https://repo.xenondevs.xyz/releases")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    maven("https://repo.codemc.io/repository/maven-public/")
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.worldboot.net/releases")
}

dependencies {
    compileOnly("org.catmc:catserversrg:11c12cd")
    compileOnly("com.pixelmonmod:pixelmon:8.4.3")
    compileOnly("com.google.guava:guava:31.0.1-jre")
    compileOnly("com.google.code.gson:gson:2.8.0")
    compileOnly("com.github.MilkBowl:VaultAPI:1.7.1")
    compileOnly("me.clip:placeholderapi:2.11.3")
    compileOnly("ink.ptms.core:v11200:11200")
    compileOnly("ink.ptms.core:v11600:11600")
    compileOnly("ink.ptms.core:v11300:11300")
    compileOnly("ink.ptms.core:v12101:12101:universal")
    compileOnly("net.md-5:bungeecord-api:1.20-R0.1-SNAPSHOT")
    compileOnly("com.velocitypowered:velocity-api:3.1.1")
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

        maven {
            name = "liangbai"

            val snapshotsUrl = uri("https://repo.worldboot.net/snapshots")
            val releasesUrl = uri("https://repo.worldboot.net/releases")
            url = if (version.toString().endsWith("SNAPSHOT")) snapshotsUrl else releasesUrl
            credentials {
                username = System.getenv("NEXUS_USERNAME")
                password = System.getenv("NEXUS_PASSWORD")
            }
        }
    }
    publications {
        create<MavenPublication>("maven") {
            group = project.group
            artifactId = project.name.toLowerCase()
            version = project.version.toString()

            from(components["java"])
        }
    }
}