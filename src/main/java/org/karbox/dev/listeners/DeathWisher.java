package org.karbox.dev.listeners;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.javacord.api.entity.webhook.Webhook;
import org.karbox.dev.DiscordBuilder;

public class DeathWisher implements Listener {
    @EventHandler
    public void whenDead(PlayerDeathEvent deathEvent) {
        Webhook webhook = DiscordBuilder.getInstance().deathWish;

        Location location = deathEvent.getPlayer().getLocation();
        Player killer = deathEvent.getPlayer().getKiller();
        String coords = String.format("%.0f %.0f %.0f", location.getX(), location.getY(), location.getZ());

        String message = deathEvent.getPlayer().getName() + " morreu em *"
                + coords + "* (" + deathEvent.getPlayer().getWorld().getName()
                + ", " + deathEvent.getPlayer().getLevel() + " XP)";

        if (killer != null) {
            if (killer != deathEvent.getPlayer())
                message += ", por **" + killer.getName() + "**";
            else
                message += ", preferindo a saída mais fácil";
        } else {
            Entity entity = deathEvent.getEntity();
            if (entity.getLastDamageCause() == null) return;

            EntityDamageEvent.DamageCause cause = entity.getLastDamageCause().getCause();

            if (cause.equals(EntityDamageEvent.DamageCause.FALL))
                message += ", de altura";

            if (cause.equals(EntityDamageEvent.DamageCause.ENTITY_ATTACK)) {
                EntityDamageEvent guiltyDamageEvent = deathEvent.getPlayer().getLastDamageCause();
                if (guiltyDamageEvent != null) {
                    Entity guiltyEntity = guiltyDamageEvent.getEntity();
                    message += ", por " + guiltyEntity.getName();
                }
            }

            if (cause.equals(EntityDamageEvent.DamageCause.SUICIDE))
                message += ", preferindo a saída mais fácil.";

            if (cause.equals(EntityDamageEvent.DamageCause.FLY_INTO_WALL))
                message += ", de burrice (energia cinética)";

            if (cause.equals(EntityDamageEvent.DamageCause.DROWNING))
                message += ", por afogamento";

            if (cause.equals(EntityDamageEvent.DamageCause.STARVATION))
                message += ", de fome";

            if (cause.equals(EntityDamageEvent.DamageCause.LIGHTNING))
                message += ", por um fodendo TROVÃO...";

            if (cause.equals(EntityDamageEvent.DamageCause.SUFFOCATION))
                message += ", por sufocamento";

            if (cause.equals(EntityDamageEvent.DamageCause.LAVA))
                message += ", na lava";

            if (cause.equals(EntityDamageEvent.DamageCause.FIRE)
                    || cause.equals(EntityDamageEvent.DamageCause.FIRE_TICK)
                    || cause.equals(EntityDamageEvent.DamageCause.HOT_FLOOR))
                message += ", no fogo";

            if (cause.equals(EntityDamageEvent.DamageCause.MAGIC))
                message += ", por ??? magia? wtf";

            if (cause.equals(EntityDamageEvent.DamageCause.DRAGON_BREATH))
                message += ", pelo BAFO DE PICA do dragão.";
        }

        if (webhook.asIncomingWebhook().isPresent())
            webhook.asIncomingWebhook().get().sendMessage(message);
    }
}
