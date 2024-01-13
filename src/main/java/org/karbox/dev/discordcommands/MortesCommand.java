package org.karbox.dev.discordcommands;

import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.interaction.SlashCommand;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.karbox.dev.DiscordIntegration;

import java.awt.*;
import java.util.*;

import static org.bukkit.Bukkit.getOfflinePlayers;
import static org.bukkit.Bukkit.getOnlinePlayers;

public class MortesCommand implements CommandStructure {
    public void registerCommand() {
        SlashCommand thisCommand = SlashCommand.with(
                "mortes", "Lista o número de mortes de cada jogador"
        ).createGlobal(api).join();

        api.addSlashCommandCreateListener(event -> {
            SlashCommandInteraction slashCommandInteraction = event.getSlashCommandInteraction();
            if (slashCommandInteraction.getCommandName().equals(thisCommand.getName())) {
                slashCommandInteraction.respondLater().thenAccept(originalResponseUpdater -> {
                    // originalResponseUpdater.setContent("Buscando jogadores...").update();

                    Collection<? extends Player> onlinePlayersCollection = getOnlinePlayers();
                    OfflinePlayer[] offlinePlayers = getOfflinePlayers();
                    HashMap<String, int[]> allPlayersStats = new HashMap<String, int[]>();

                    StringBuilder statsMessage = new StringBuilder();

                    if ((offlinePlayers.length - 1) > 0) {
                        for (OfflinePlayer offlinePlayer : offlinePlayers) {
                            allPlayersStats.put(
                                    offlinePlayer.getName(),
                                    new int[]{
                                            offlinePlayer.getStatistic(Statistic.DEATHS),
                                            offlinePlayer.getStatistic(Statistic.DAMAGE_DEALT)
                                    }
                            );
                        }
                    }

                    if (!onlinePlayersCollection.isEmpty()) {
                        for (Player onlinePlayer : onlinePlayersCollection) {
                            allPlayersStats.put(
                                    onlinePlayer.getName(),
                                    new int[]{
                                            onlinePlayer.getStatistic(Statistic.DEATHS),
                                            onlinePlayer.getStatistic(Statistic.DAMAGE_DEALT)
                                    }
                            );
                        }
                    }

                    if (!allPlayersStats.isEmpty()) {
                        for (Map.Entry<String, int[]> entry : allPlayersStats.entrySet()) {
                            String nickname = entry.getKey();
                            int[] stats = entry.getValue();
                            statsMessage.append("- ")
                                    .append(nickname)
                                    .append(" = **")
                                    .append(stats[0])
                                    .append("** mortes, **")
                                    .append(stats[1])
                                    .append("** dano causado.")
                                    .append("\n");
                        }
                    } else {
                        statsMessage.append(
                                "Não foi possível obter informação sobre a estatística de mortes"
                                        + "dos jogadores offline/online"
                        );
                    }

                    EmbedBuilder embed = new EmbedBuilder()
                            .setTitle("Total de mortes e dano causado do servidor")
                            .setDescription(statsMessage.toString())
                            .setAuthor("KarboXXX",
                                    "http://github.com/KarboXXX",
                                    "https://avatars.githubusercontent.com/u/48266134?v=4"
                            )
                            .setColor(Color.RED);

                    originalResponseUpdater.addEmbed(embed).update();
                });
            }
        });

        DiscordIntegration.getInstance().getLogger().info(
                "Mortes command loaded."
        );
    }

}
