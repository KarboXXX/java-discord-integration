package org.karbox.dev.discordcommands;

import com.github.alexdlaird.ngrok.protocol.Tunnel;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.interaction.SlashCommand;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.SlashCommandInteractionOption;
import org.javacord.api.interaction.SlashCommandOption;
import org.karbox.dev.DiscordBuilder;
import org.karbox.dev.DiscordIntegration;
import org.karbox.dev.connection.AutoNgrok;

import java.awt.*;
import java.util.Objects;
import java.util.Optional;

public class ManageNgrokCommands implements CommandStructure {
    public void registerCommand() {
        SlashCommand disconnectNgrok = SlashCommand.with(
                "ngrok-disconnect",
                "Disconecta o servidor do cliente (proxy end-point) ngrok."
        ).createGlobal(api).join();

        SlashCommand reconnectNgrok = SlashCommand.with(
                "ngrok-reconnect",
                "Reconecta o servidor do cliente (proxy end-point) ngrok."
        ).addOption(
                SlashCommandOption
                        .createBooleanOption(
                                "renew",
                                "Reconectar a proxy end-point com outra porta?",
                                true))
                .createGlobal(api)
                .join();

        String[] allowedUserIds = {
                "1038949799401095188", // Mello (Felipe)
                "708857740965183559", // KarboX
                "900126187924373557", // Trilha (Lucas)
        };

        api.addSlashCommandCreateListener(event -> {
            SlashCommandInteraction slashCommandInteraction = event.getSlashCommandInteraction();
            if (slashCommandInteraction.getCommandName().equals(disconnectNgrok.getName())) {
                boolean allow = false;
                for (String allowedUserId : allowedUserIds) {
                    if (!allow)
                        allow = Objects.equals(slashCommandInteraction.getUser().getIdAsString(), allowedUserId);
                }

                if (allow) {
                    try {
                        AutoNgrok.getInstance().disconnect();

                        if (DiscordBuilder.getInstance().serverStatus.asIncomingWebhook().isPresent()) {
                            EmbedBuilder embed = new EmbedBuilder()
                                    .setTitle("Proxy end-point desconectado.")
                                    .setDescription("Proxy end-point ngrok foi desconectado manualmente por " + slashCommandInteraction.getUser().getName())
                                    .setAuthor("KarboXXX",
                                            "http://github.com/KarboXXX",
                                            "https://avatars.githubusercontent.com/u/48266134?v=4"
                                    )
                                    .setColor(Color.YELLOW);

                            DiscordBuilder.getInstance().serverStatus
                                    .asIncomingWebhook().get().sendMessage(embed);
                        }

                        slashCommandInteraction.createImmediateResponder()
                                .setContent("proxy end-point **desconectado.**").respond();
                    } catch (Exception e) {
                        slashCommandInteraction.createImmediateResponder()
                                .setContent("Não há conexões no momento, vai desconectar oq? mlk malucoKKKKKKKKKK")
                                .respond();
                    }
                } else {
                    slashCommandInteraction.createImmediateResponder()
                            .setContent("você não tem permissão suficiente para esta operação.").respond();
                }
            }

            if (slashCommandInteraction.getCommandName().equals(reconnectNgrok.getName())) {
                boolean allow = false;
                for (String allowedUserId : allowedUserIds) {
                    if (!allow)
                        allow = Objects.equals(slashCommandInteraction.getUser().getIdAsString(), allowedUserId);
                }

                if (allow) {
                    Optional<SlashCommandInteractionOption> renew = slashCommandInteraction.getOptionByName("renew");
                    if (!renew.isPresent()) return;

                    java.util.Optional<Boolean> renewValue = renew.get().getBooleanValue();
                    if (!renewValue.isPresent()) return;

                    if (renewValue.get()) {
                        try {
                            Tunnel newTunnel = AutoNgrok.getInstance().reconnectWithAnotherTunnel();

                            if (DiscordBuilder.getInstance().serverStatus.asIncomingWebhook().isPresent()) {
                                EmbedBuilder embed = new EmbedBuilder()
                                        .setTitle("Proxy end-point reconectado.")
                                        .setDescription("Proxy end-point ngrok foi reconectado com uma nova porta" +
                                                " manualmente por " + slashCommandInteraction.getUser().getName())
                                        .addInlineField("Novo end-point",
                                                newTunnel.getPublicUrl().replace("tcp://", ""))
                                        .setAuthor("KarboXXX",
                                                "http://github.com/KarboXXX",
                                                "https://avatars.githubusercontent.com/u/48266134?v=4"
                                        )
                                        .setColor(Color.GREEN);

                                DiscordBuilder.getInstance().serverStatus
                                        .asIncomingWebhook().get().sendMessage(embed);
                            }

                            slashCommandInteraction.createImmediateResponder()
                                    .setContent("proxy end-point **reconectado, com uma nova porta**.")
                                    .respond();
                        } catch (Exception e) {
                            slashCommandInteraction.createImmediateResponder()
                                    .setContent("Não é possível reconectar. Cliente gateway proxy" +
                                            " possui uma lista **não nula** de conexões no momento.")
                                    .respond();
                        }
                    } else {
                        try {
                            Tunnel reconnectedTunnel = AutoNgrok.getInstance().reconnect();

                            if (DiscordBuilder.getInstance().serverStatus.asIncomingWebhook().isPresent()) {
                                EmbedBuilder embed = new EmbedBuilder()
                                        .setTitle("Proxy end-point reconectado.")
                                        .setDescription("Proxy end-point ngrok foi reconectado manualmente por "
                                                + slashCommandInteraction.getUser().getName())
                                        .addInlineField("Mesmo end-point",
                                                reconnectedTunnel.getPublicUrl().replace("tcp://", ""))
                                        .setAuthor("KarboXXX",
                                                "http://github.com/KarboXXX",
                                                "https://avatars.githubusercontent.com/u/48266134?v=4"
                                        )
                                        .setColor(Color.GREEN);

                                DiscordBuilder.getInstance().serverStatus
                                        .asIncomingWebhook().get().sendMessage(embed);
                            }

                            slashCommandInteraction.createImmediateResponder()
                                    .setContent("proxy end-point **reconectado, com a mesma porta**.")
                                    .respond();

                        } catch (Exception e) {
                            slashCommandInteraction.createImmediateResponder()
                                    .setContent("Não é possível reconectar. Cliente gateway proxy" +
                                            " possui uma lista **não nula** de conexões no momento.")
                                    .respond();
                        }
                    }
                }
            }
        });

        DiscordIntegration.getInstance().getLogger().info(
                "ManageNgrokCommands commands loaded. (ngrok disconnect & reconnect)"
        );
    }
}
