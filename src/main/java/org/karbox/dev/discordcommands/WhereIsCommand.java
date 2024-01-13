package org.karbox.dev.discordcommands;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.javacord.api.entity.message.MessageFlag;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.interaction.*;
import org.karbox.dev.DiscordIntegration;

import java.awt.*;
import java.util.*;

import static org.bukkit.Bukkit.getOnlinePlayers;
import static org.bukkit.Bukkit.getPlayer;

public class WhereIsCommand implements CommandStructure {
    @Override
    public void registerCommand() {
        SlashCommand whereis = SlashCommand.with(
                "whereis", "Mostrar as coordenadas de um jogador (online) no servidor."
                )
                .addOption(SlashCommandOption.createStringOption(
                        "nickname", "Nickname do usuário online", true
                        )
                )
                .createGlobal(api).join();

        api.addAutocompleteCreateListener(event -> {
            if (event.getAutocompleteInteraction().getFocusedOption().getName().equals("nickname")) {
                Collection<? extends Player> onlinePlayers = getOnlinePlayers();
                ArrayList<SlashCommandOptionChoice> choices = new ArrayList<>();

                onlinePlayers.forEach(player -> {
                    choices.add(SlashCommandOptionChoice.create(player.getName(), player.getName()));
                });

                event.getAutocompleteInteraction().respondWithChoices(choices);
            }
        });

        api.addSlashCommandCreateListener(event -> {
            SlashCommandInteraction slashCommandInteraction = event.getSlashCommandInteraction();
            if (slashCommandInteraction.getCommandName().equals(whereis.getName())) {
                if (slashCommandInteraction.getArgumentByName("nickname").isPresent()) {
                    SlashCommandInteractionOption nickname = slashCommandInteraction
                            .getArgumentByName("nickname").get();

                    if (nickname.getStringValue().isPresent()) {
                        Player player = getPlayer(nickname.getStringValue().get());

                        if (player != null) {
                            Location playerLocation = player.getLocation();

                            EmbedBuilder embed = new EmbedBuilder()
                                    .setTitle("Posição de " + player.getName())
                                    .addInlineField("Coordenadas", String.format("%.0f %.0f %.0f",
                                            playerLocation.getX(), playerLocation.getY(), playerLocation.getZ()))
                                    .addInlineField("Mundo", playerLocation.getWorld().getName())
                                    .setAuthor("KarboXXX",
                                            "http://github.com/KarboXXX",
                                            "https://avatars.githubusercontent.com/u/48266134?v=4")
                                    .setColor(Color.GRAY);

                            slashCommandInteraction.createImmediateResponder().addEmbed(embed).respond();
                        } else {
                            slashCommandInteraction.createImmediateResponder().setFlags(MessageFlag.EPHEMERAL)
                                    .setContent("Player inválido, ou player está offline").respond();
                        }
                    } else {
                        slashCommandInteraction.createImmediateResponder().setFlags(MessageFlag.EPHEMERAL)
                                .setContent("Argumento 'nickname' vazio, ou mal-formatado.").respond();
                    }
                } else {
                    slashCommandInteraction.createImmediateResponder().setFlags(MessageFlag.EPHEMERAL)
                            .setContent("Argumento 'nickname' faltando.").respond();
                }
            }
        });

        DiscordIntegration.getInstance().getLogger().info("WhereIs command loaded.");
    }
}
