package org.karbox.dev.discordcommands;

import org.bukkit.entity.Player;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.interaction.SlashCommand;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.karbox.dev.DiscordIntegration;

import java.awt.*;
import java.util.Collection;

import static org.bukkit.Bukkit.getOnlinePlayers;

public class ListCommand implements CommandStructure { // Read CommandStructure.
    @Override
    public void registerCommand() {
        SlashCommand thisCommand = SlashCommand.with( // we don't need to actually use this variable, but we just store it just in case
                "list", "Lista os jogadores presentes no servidor" // name, description
        )
                .createGlobal(api) // register it globally for our bot (you can use .createForServer(DiscordApi, Server) if you want)
                .join(); // the blah blah we've seen before

        // the fun part begins, your chance to give up is offered.

        // here, we are registering a SlashCommandEvent, on every event, this stuff here happens.
        api.addSlashCommandCreateListener(event -> {
            // "event" lambda propriety is a SlashCommandCreateEvent object, just so you know

            // we get the SlashCommandInteraction out of our "general" SlashCommandEvent,
            // because we have other events inside it, we just want the actual SlashCommandInteraction,
            // between the user and the bot.
            SlashCommandInteraction slashCommandInteraction = event.getSlashCommandInteraction();

            // if the ran command is this class' command
            // note: if you're using IntellijIDEA, thisCommand is purple because it was
            // declared outside this lambda function, you can see we're inside a lambda by looking
            // at the footer of your IDEA (ListCommand > registerCommand > Lambda)
            if (slashCommandInteraction.getCommandName().equals(thisCommand.getName())) {
                Collection<? extends Player> onlinePlayers = getOnlinePlayers();

                StringBuilder description = new StringBuilder();
                Color color = Color.GREEN;
                String title = "Jogadores online";

                if (onlinePlayers.isEmpty()) {
                    color = Color.YELLOW;
                    title = "Nenhum jogador online";
                    description.append("Seja o primeiro!");
                } else {
                    for (Player player : onlinePlayers) {
                        description.append("- ").append(player.getName());
                        description.append(" (").append(player.getPing()).append("ms) ");

                        description.append("\n");
                    }
                }

                EmbedBuilder embed = new EmbedBuilder()
                        .setTitle(title)
                        .setDescription(description.toString())
                        .setAuthor("KarboXXX",
                                "http://github.com/KarboXXX",
                                "https://avatars.githubusercontent.com/u/48266134?v=4"
                        )
                        .setColor(color);

                slashCommandInteraction // as the interaction
                        .createImmediateResponder() // we have other ways to respond the user,
                        // we can use .respondLater() and stuff, but now we are just responding quick.
                        .addEmbed(embed).respond(); // adding our embed and responding.
                // Be not afraid. Our respond() method returns a CompletableFuture, we just don't care,
                // because we don't actually want the message at the end of this CompletableFuture.
            }
        });

        DiscordIntegration.getInstance().getLogger().info(
                "List command loaded."
        ); // tell the client we successfully loaded the command.
    }

}
