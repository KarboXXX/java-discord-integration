# Discord Integration
An extremely simple project, to help beginners integrate their Minecraft Paper/Spigot plugins with their Discord Applications through Java. This project aims on improving the community, and helping new people on software engineering.

This project does not aim to teach Java, and it is expected that you at least know the syntax beforehand. It is also expected that you know at least what is a Minecraft Plugin. If you do not know how to make Minecraft Plugins, check [this YouTube playlist for more information.](https://www.youtube.com/watch?v=HqxcyK_YgOE)

## 📚 Dependencies
[Paper 1.20.1](https://papermc.io/downloads), [Java-Ngrok](https://github.com/alexdlaird/java-ngrok), [Javacord](https://javacord.org/wiki/)

- In this project, I used [Maven](https://maven.apache.org/), so you just need to copy and paste this section for declaring our dependencies:

```xml
<dependency>
    <groupId>org.javacord</groupId>
    <artifactId>javacord</artifactId>
    <version>3.8.0</version>
    <type>pom</type>
    <scope>compile</scope>
</dependency>
<dependency>
    <groupId>com.github.alexdlaird</groupId>
    <artifactId>java-ngrok</artifactId>
    <version>2.2.7</version>
</dependency> 
```
- And this section for declaring the repositories, which Maven needs to install the package dependencies from:
```xml
<repository>
    <id>sonatype</id>
    <url>https://oss.sonatype.org/content/groups/public/</url>
</repository>
<repository>
    <id>jitpack.io</id>
    <url>https://jitpack.io</url>
</repository>
```

- All this part done, now you can clone or copy parts of this repository and test things out for yourself. 80% of classes in this project are commented and documented, each part, variable, and method explained bit by bit. Create new things, and enjoy!

## 🏗️ Structure
From every interface, to every method, is thought before typed. This project structure keeps everything in it's place, where it's logical to be.

We start at the main class, [DiscordIntegration](src/main/java/org/karbox/dev/DiscordIntegration.java). When our plugin is enabled, we register our [Minecraft Commands](src/main/java/org/karbox/dev/minecraftcommands), and [Listeners](src/main/java/org/karbox/dev/listeners), then, we create our DiscordApi Object, [right here](src/main/java/org/karbox/dev/DiscordBuilder.java), along with our Webhooks, declared by the [user's configuration file](src/main/resources/config.yml), and when it loads, [Discord commands](src/main/java/org/karbox/dev/discordcommands) are loaded and registered, built by the same [CommandStructure Interface](src/main/java/org/karbox/dev/discordcommands/CommandStructure.java), where its expected methods and varibles are put in place. And if we enable it, we can even create our own [Ngrok Tunnel](src/main/java/org/karbox/dev/connection/AutoNgrok.java). Minecraft Commands on 1.12+, have something called TabExecuter. We use it to help the player typing the command, and to understand what's expected from the next argument, like we [did here, on the WaypointCommand](src/main/java/org/karbox/dev/minecraftcommands/WaypointCommand.java).