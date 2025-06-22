import net.minecrell.pluginyml.bukkit.BukkitPluginDescription
import net.minecrell.pluginyml.paper.PaperPluginDescription

plugins {
  `java-library`
  idea
  id("com.github.johnrengelman.shadow") version "8.1.1"
  id("io.papermc.paperweight.userdev") version "1.7.1"
  id("xyz.jpenilla.run-paper") version "2.3.0"
  id("net.minecrell.plugin-yml.paper") version "0.6.0"
}

group = "me.arian"
version = "0.0.1"
description = "World Edit Addon"

java {
  toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

repositories {
  mavenCentral()
  maven("https://papermc.io/repo/repository/maven-public/")
  maven("https://maven.enginehub.org/repo/")
  maven("https://repo.codemc.io/repository/maven-releases/")
}

dependencies {
  paperweight.paperDevBundle("1.20.1-R0.1-SNAPSHOT")

//  implementation(platform("com.intellectualsites.bom:bom-newest:1.49")) // Ref: https://github.com/IntellectualSites/bom
//  compileOnly("com.fastasyncworldedit:FastAsyncWorldEdit-Core")
//  compileOnly("com.fastasyncworldedit:FastAsyncWorldEdit-Bukkit") // { isTransitive = false }
  implementation("com.github.retrooper:packetevents-spigot:2.4.0")
}

tasks {
  compileJava {
    options.encoding = Charsets.UTF_8.name()

    options.release.set(17)
  }
  javadoc {
    options.encoding = Charsets.UTF_8.name()
  }
  assemble {
    dependsOn(reobfJar)
  }
  build {
    dependsOn(shadowJar)
  }
  shadowJar {
    relocate("io.github.retrooper.packetevents", "me.arian.wea.pe")
    relocate("com.github.retrooper.packetevents", "me.arian.wea.pe")
    archiveFileName = "WEAddon.jar"
  }
}

paper {
  name = "WEAddon"
  version = project.version.toString()
  description = project.description
  author = "Arian"

  main = "me.arian.wea.WEAddon"
  apiVersion = "1.20"
  load = BukkitPluginDescription.PluginLoadOrder.POSTWORLD


  prefix = "WEAddon"
  defaultPermission = BukkitPluginDescription.Permission.Default.OP

  serverDependencies {
        register("WorldEdit") {
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
        }
    }

  permissions {
    register("wea.*") {
      children = listOf("wea.blockstateplacer", "wea.chunkreset", "wea.display",
        "wea.createexportworld", "wea.biome", "wea.rotate",
        "wea.setblock", "wea.world", "wea.head")
    }
    register("wea.blockstateplacer") {
      default = BukkitPluginDescription.Permission.Default.OP
    }
    register("wea.chunkreset") {
      default = BukkitPluginDescription.Permission.Default.OP
    }
    register("wea.display") {
      default = BukkitPluginDescription.Permission.Default.OP
    }
    register("wea.createexportworld") {
      default = BukkitPluginDescription.Permission.Default.OP
    }
    register("wea.biome") {
      default = BukkitPluginDescription.Permission.Default.OP
    }
    register("wea.rotate") {
      default = BukkitPluginDescription.Permission.Default.OP
    }
    register("wea.setblock") {
      default = BukkitPluginDescription.Permission.Default.OP
    }
    register("wea.world") {
      default = BukkitPluginDescription.Permission.Default.OP
    }
    register("wea.head") {
      default = BukkitPluginDescription.Permission.Default.OP
    }
  }
}

idea {
  module {
    isDownloadSources = true
  }
}
