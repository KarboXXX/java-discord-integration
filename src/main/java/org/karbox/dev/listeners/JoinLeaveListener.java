package org.karbox.dev.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.webhook.Webhook;
import org.karbox.dev.DiscordBuilder;

import java.awt.*;

public class JoinLeaveListener implements Listener {

    // Things here are really simple, just plain Java Paper/Spigot, and stuff I've explained before.

    @EventHandler
    public void onLeave(PlayerQuitEvent player) {
        Webhook webhook = DiscordBuilder.getInstance().joinLeave;

        if (!webhook.asIncomingWebhook().isPresent()) return;

        EmbedBuilder leaveEmbed = new EmbedBuilder()
                .setTitle("Um jogador saiu do servidor")
                .setDescription(player.getPlayer().getName() + " saiu do servidor")
                .setAuthor("KarboXXX",
                        "http://github.com/KarboXXX",
                        "https://avatars.githubusercontent.com/u/48266134?v=4"
                )
                .setColor(Color.YELLOW);
        webhook.asIncomingWebhook().get().sendMessage(leaveEmbed);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent player) {
        Webhook webhook = DiscordBuilder.getInstance().joinLeave;

        if (webhook == null) return;
        if (!webhook.asIncomingWebhook().isPresent()) return;

        EmbedBuilder joinEmbed = new EmbedBuilder()
                .setTitle("Um jogador entrou no servidor")
                .setDescription(player.getPlayer().getName() + " entrou no servidor")
                .setAuthor("KarboXXX",
                        "http://github.com/KarboXXX",
                        "https://avatars.githubusercontent.com/u/48266134?v=4"
                )
                .setColor(Color.BLUE);
        webhook.asIncomingWebhook().get().sendMessage(joinEmbed);
    }
}
