package org.karbox.dev;

import com.github.alexdlaird.ngrok.protocol.Tunnel;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.intent.Intent;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.webhook.Webhook;
import org.karbox.dev.discordcommands.ManageNgrokCommands;
import org.karbox.dev.discordcommands.ListCommand;
import org.karbox.dev.discordcommands.MortesCommand;
import org.karbox.dev.discordcommands.WhereIsCommand;
import org.karbox.dev.connection.AutoNgrok;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.List;

public class DiscordBuilder {
    public DiscordApi api;

    public Webhook joinLeave;
    public Webhook serverStatus;
    public Webhook deathWish;
    public Webhook waypoints;
    public Webhook advancements;

    static DiscordBuilder instance;

    private final Intent[] intents = {
            Intent.GUILDS,
            Intent.GUILD_INTEGRATIONS,
    };

    public void buildDiscordApi(@Nullable String token, Runnable onError) {
        if (token == null || token.isEmpty()) { // Checks if String token is a valid string
            onError.run(); // Calls the function we pass as argument if the check fails
            return; // We do not want to continue building the DiscordAPI object, because we don't have a token to work with.
        }

        new DiscordApiBuilder()
                .setToken(token) // Set the token of the bot here
                .setIntents(this.intents) // Intents are basically states what you're planning to work with your bot
                // it improves performance, because can make the bot ignore message content events, for example.
                .login() // Log the bot in
                .thenAccept(this::onConnectToDiscord) // Call onConnectToDiscord(...) after a successful login
                .exceptionally(error -> { // If something goes wrong when logging in, returns null for this.
                    onError.run();
                    return null;
                });
    }

    public void disconnectDiscord() {
        if (!this.serverStatus.asIncomingWebhook().isPresent()) return;
        // This, checks if the channel of the webhook exists before we build our message.

        EmbedBuilder shutdownEmbed = new EmbedBuilder()
                .setTitle("Servidor desligado.")
                .setDescription("Servidor desligado")
                .setAuthor("KarboXXX",
                        "http://github.com/KarboXXX",
                        "https://avatars.githubusercontent.com/u/48266134?v=4"
                )
                .setColor(Color.RED);

        /*this.serverStatus.asIncomingWebhook().get().sendMessage(shutdownEmbed).thenAccept(con -> {
            this.api.disconnect().thenAccept(ignored -> {
                DiscordIntegration.getInstance().getLogger().info("DiscordAPI desconectado.");
            });
        });*/ //This is my original piece of code, but I decided to chop it down to explain it.

        this.serverStatus // we get the server status webhook object, from the propriety of our class instance.
                .asIncomingWebhook() // but we want it as an IncomingWebhook object, because we want to send messages THROUGH it, not with it. However, the return value of this function is what we call a CompletableFuture. If you worked with JavaScript before, it's something very close to a Promise<>()
                .get() // so calling this .get() method, we wait for the end of the CompletableFuture, and it returns the IncomingWebhook object we want.
                .sendMessage(shutdownEmbed) // we send our EmbedBuilder through it. Ok. This is also a CompletableFuture, so
                    .thenAccept(con -> { // When our message is sent and confirmed, we call .thenAccept() method, to call another function passing our Lambda argument "con", that is a Message object.
                        this.api
                            .disconnect() // we disconnect our API. This also returns a CompletableFuture, so
                                .thenAccept(ignored -> { // when we actually disconnect, we run
                                    DiscordIntegration
                                        .getInstance()
                                            .getLogger().info("DiscordAPI desconectado."); // this, calling our logger.
                                });
                    });
    }

    private void onConnectToDiscord(DiscordApi discordApi) {
        this.api = discordApi; // when we connect, we pass or discordApi to the api propriety of the class, just so we can access through our instance.

        FileConfiguration config = DiscordIntegration.getInstance().config; // pass the Main class config propriety instance, as our own variable

        String serverStatus, joinLeave, deathwish, waypoints;
        ConfigurationSection webhooksURL = config.getConfigurationSection("webhooksURL"); // we get the "webhooksURL" object from the config.yml, so we can get each webhook config key name

        assert webhooksURL != null; // if our webhooksURL equals null, means someone f*cked with the configuration file, so, we can just assert it's not null, because if it is, we throw a NullPointerException.
        serverStatus = webhooksURL.getString("serverStatus");
        joinLeave = webhooksURL.getString("joinLeave");
        deathwish = webhooksURL.getString("deaths");
        waypoints = webhooksURL.getString("waypoints");

        // even though our variable names are the same as the proprieties of the class, it does not
        // mess with it, because our class has its own proprieties, and these, are variables.
        // Having class proprieties with the same name as variables IS NOT a good code practice,
        // but I'm doing it to better improve reading only for the learning.

        if (serverStatus == null || joinLeave == null || deathwish == null || waypoints == null) {
            DiscordIntegration.getInstance()
                    .getLogger().info("You got empty webhooks, not continuing.");

            DiscordIntegration.getInstance()
                    .getServer()
                        .getPluginManager()
                            .disablePlugin(
                                    DiscordIntegration.getInstance()
                            );

            return;
        }

        /* Let's first unwrap this stuff:

        this.api -> our api, got from this class instance
                .getIncomingWebhookByUrl( -> we want it as a IncomingWebhook object, because we want to send message as it
                        joinLeave -> our String URL
                )
                .join(); -> we do not want a CompletableFuture, we want the result of the end of it.

                */

        /*
        * You are asking yourself; "Well, i've seen .join() and .get(), what's the matter?"
        *
        * .get() is for "I want the object that is RETURNED the end of this CompletableFuture"
        * .join() is for "I want the object that IS THE END of this CompletableFuture"
        * */

        this.joinLeave = this.api.getIncomingWebhookByUrl(joinLeave).join();
        this.serverStatus = this.api.getIncomingWebhookByUrl(serverStatus).join();
        this.deathWish = this.api.getIncomingWebhookByUrl(deathwish).join();
        this.waypoints = this.api.getIncomingWebhookByUrl(waypoints).join();

        // You see a lot of repeated code, usually this is more of a "what is going on" type of concern,
        // but in this case, it is not recommeded that you use .join() for often called CompletableFuture's
        // because it waits for the end of them, almost sleeping the thread you're on.
        // In this case, it's "ok", because we're getting it only at the start of the server, just 4 times.

        instance = this; // Saves the state of our class

        // Registering our discord commands

        new ListCommand().registerCommand(); // I'll explain stuff on this command, but first,
        // read the CommandStructure Interface for beforehand information.

        new MortesCommand().registerCommand();
        new ManageNgrokCommands().registerCommand();
        new WhereIsCommand().registerCommand();

        EmbedBuilder turningOnMessage = new EmbedBuilder()
                .setTitle("Servidor ligado")
                .setAuthor("KarboXXX",
                        "http://github.com/KarboXXX",
                        "https://avatars.githubusercontent.com/u/48266134?v=4"
                )
                .setColor(Color.GREEN);

        if (config.getBoolean("runNgrok")) {
            DiscordIntegration.getInstance().getLogger().info("Iniciando tunnel ngrok...");
            final Tunnel serverTunnel = new AutoNgrok().registerTunnel();

            turningOnMessage
                    .setDescription("Servidor conectado ao IP: `" +
                        serverTunnel.getPublicUrl().replace("tcp://", "") +
                    "`");
        } else {
            turningOnMessage
                    .setDescription("Servidor ligado.");
        }

        if (!this.serverStatus.asIncomingWebhook().isPresent()) return;

        this.serverStatus
                .asIncomingWebhook() // now, we're turning the Webhook into an IncomingWebhook Object. So:
                .get() // we want what is RETURNED at the end of our CompletableFuture.
                .sendMessage(turningOnMessage); // and send a message with our embed on it.
    }

    public static DiscordBuilder getInstance() {
        return instance;
    }
}
