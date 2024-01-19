import com.github.jengelman.gradle.plugins.shadow.relocation.SimpleRelocator
import org.apache.commons.lang3.time.DateFormatUtils
import java.util.*

plugins {
    id("java")
    id("com.gtnewhorizons.retrofuturagradle") version "1.3.25"
    id("com.github.johnrengelman.shadow") version ("8.+")
    id("eclipse")
    id("maven-publish")
}

val minecraftVersion = properties["self.minecraft.version"] as String
val minecraftUsername = properties["self.minecraft.username"] as String

val modId = properties["self.minecraftforge.mod.id"] as String
val modName = properties["self.minecraftforge.mod.name"] as String
val modVersion = properties["self.minecraftforge.mod.version"] as String
val modAuthors = properties["self.minecraftforge.mod.authors"] as String

val fmlCorePluginClass = properties["self.minecraftforge.coremod.class"] as String

val localMavenRepository = properties["self.maven.repositories.local"] as String

val useCharset = "utf8"

version = modVersion
group = "team.idealstate.minecraftforge"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(8))
        // Azul covers the most platforms for Java 8 toolchains, crucially including macOS arm64
        vendor.set(JvmVendorSpec.AZUL)
    }
}

tasks.compileJava {
    options.encoding = useCharset
}

minecraft {
    mcVersion.set(minecraftVersion)

    // Enable assertions in the mod's package when running the client or server
    extraRunJvmArguments.add("-ea:${project.group}")

    // Exclude some Maven dependency groups from being automatically included in the reobfuscated runs
    groupsToExcludeFromAutoReobfMapping.addAll(
            "com.diffplug",
            "com.diffplug.durian",
            "net.industrial-craft"
    )
}

repositories {
    mavenLocal()
    maven {
        name = "aliyun-public"
        url = uri("https://maven.aliyun.com/repository/public")
    }
    // RetroFuturaGradle
    maven {
        name = "GTNH Maven"
        url = uri("https://nexus.gtnewhorizons.com/repository/public/")
    }
    mavenCentral()
}

dependencies {
    compileOnly(fileTree("${projectDir}/libraries"))
}

tasks.processResources {
    filteringCharset = useCharset
    includeEmptyDirs = false
    val props = mapOf(
            "mod_id" to modId,
            "mod_name" to modName,
            "mod_version" to modVersion,
            "mod_authors" to modAuthors.replace(" ", "").replace(",", "\",\""),
            "minecraft_version" to minecraftVersion,
    )
    filesMatching(listOf("assets/**/*.lang", "**/mcmod.info", "**/pack.mcmeta")) {
        expand(props)
    }
    val assetsDir = "assets/${modId}"
    eachFile {
        if (path.startsWith("assets/")) {
            print("$path >> ")
            path = assetsDir + path.substring(6)
            println(path)
        }
    }
}

tasks.jar {
    dependsOn(tasks.shadowJar)
    enabled = false
    manifest {
        attributes(linkedMapOf(
                "Implementation-Title" to modName,
                "Implementation-Version" to modVersion,
                "Implementation-Vendor" to modAuthors,
                "Implementation-Timestamp" to DateFormatUtils.format(Date(), "yyyy-MM-dd HH:mm:ssZ"),
                "FMLCorePlugin" to fmlCorePluginClass,
                "FMLCorePluginContainsFMLMod" to true,
        ))
    }
}

tasks.shadowJar {
    archiveClassifier.set("")
    manifest.attributes.putAll(tasks.jar.get().manifest.attributes)
    dependencies {
        exclude {
            return@exclude true
        }
    }
    relocate(SimpleRelocator("\\\$\\{mod_id}", modId, mutableListOf(), mutableListOf(), true))
    relocate(SimpleRelocator("\\\$\\{mod_name}", modName, mutableListOf(), mutableListOf(), true))
    relocate(SimpleRelocator("\\\$\\{mod_version}", modVersion, mutableListOf(), mutableListOf(), true))
}

tasks.reobfJar {
    dependsOn(tasks.shadowJar)
    inputJar.set(tasks.shadowJar.get().archiveFile)
}

tasks.create<Copy>("copyToMods") {
    mustRunAfter(tasks.jar, tasks.reobfJar)
    from("${projectDir}/build/libs/${modName}-${modVersion}.jar")
    into("${projectDir}/run/mods")
}

tasks.publish {
    dependsOn(tasks.reobfJar)
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            artifact(tasks.reobfJar)
        }
    }
    repositories {
        maven {
            url = uri("file:///${localMavenRepository}")
        }
    }
}

tasks.runClient {
    dependsOn("copyToMods")
    username.set(minecraftUsername)
}

tasks.runObfClient {
    dependsOn("copyToMods")
    username.set(minecraftUsername)
}
