package org.karbox.dev;

import com.github.alexdlaird.ngrok.protocol.Tunnel;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.karbox.dev.connection.AutoNgrok;
import org.karbox.dev.listeners.DeathWisher;
import org.karbox.dev.listeners.JoinLeaveListener;
import org.karbox.dev.minecraftcommands.WaypointCommand;

public final class DiscordIntegration extends JavaPlugin {

    public static DiscordIntegration instance;
    public FileConfiguration config = getConfig();

    public String token;

    public void onError() {
        getLogger().info("DiscordBuilder não pôde concluir login.");
        getServer().getPluginManager().disablePlugin(this);

    }

    @Override
    public void onEnable() {
        saveDefaultConfig();

        getLogger().info(
                "Plugin iniciado (KarboXXX -> https://github.com/KarboXXX)"
        );

        this.token = this.config.getString("token");

        new DiscordBuilder().buildDiscordApi(this.token, this::onError);

        getServer().getPluginManager().registerEvents(new JoinLeaveListener(), this);
        getServer().getPluginManager().registerEvents(new DeathWisher(), this);

        // waypoint command
        getCommand("waypoint").setExecutor(new WaypointCommand());

        instance = this;
    }

    @Override
    public void onDisable() {
        getLogger().info(
                "Disconectando Discord Bot... (KarboXXX)"
        );

        try {
            AutoNgrok.getInstance().disconnect();
        } catch (Exception ignored) {}

        DiscordBuilder.getInstance().disconnectDiscord();
    }

    public static DiscordIntegration getInstance() {
        return instance;
    }
}
