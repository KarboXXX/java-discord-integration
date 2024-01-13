package org.karbox.dev.minecraftcommands;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.karbox.dev.DiscordBuilder;
import org.karbox.dev.DiscordIntegration;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.*;
import java.util.List;

import static java.lang.Integer.parseInt;

// Saving waypoints on discord.
public class WaypointCommand implements CommandExecutor, TabExecutor {

    // with TabExecutor, we can tell Minecraft how to handle the TAB completer, on our command.
    // basically, Minecraft's Tab Handler just needs you to pass a List<String>, and if
    // you used Tab completion before you just understand why...
    @Override
    public @Nullable List<String> onTabComplete(CommandSender sender,
                                                Command command,
                                                String label,
                                                String[] args) {

        // this is plain Java Paper/Spigot, not explaining it.
        if (!(sender instanceof Player)) return null;

        if (args.length == 1) {
            return Collections.singletonList(
                    String.format("%.0f", ((Player) sender).getLocation().getX())
            );
        }
        if (args.length == 2) {
            return Collections.singletonList(
                    String.format("%.0f", ((Player) sender).getLocation().getY())
            );
        }
        if (args.length == 3) {
            return Collections.singletonList(
                    String.format("%.0f", ((Player) sender).getLocation().getZ())
            );
        }
        if (args.length == 4) {
            List<World> worlds = DiscordIntegration.getInstance().getServer().getWorlds();
            ArrayList<String> worldNames = new ArrayList<String>();

            worlds.forEach(w -> {
                worldNames.add(w.getName());
            });

            return worldNames;
        }
        if (args.length > 4) {
            return Collections.singletonList("");
        }

        return null;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // a lot of stuff we covered before. And more Paper/Spigot
        if (sender instanceof Player) {
            if (!DiscordBuilder.getInstance().waypoints.asIncomingWebhook().isPresent()) {
                sender.sendMessage(ChatColor.RED + "Desculpe, ocorreu um erro com o webhook de waypoints.");
                return true;
            }

            Player p = (Player) sender;

            if (args.length < 3) {
                Location playerLocation = p.getLocation();
                String x, y, z, world;

                x = String.format("%.0f", playerLocation.getX());
                y = String.format("%.0f", playerLocation.getY());
                z = String.format("%.0f", playerLocation.getZ());
                world = playerLocation.getWorld().getName();

                EmbedBuilder embed = new EmbedBuilder()
                        .setTitle("Novo waypoint de " + p.getName())
                        .addInlineField("\"Waypoint rápido\"",
                                String.format("**%s %s %s**, (%s)", x, y, z, world)
                        )
                        .setAuthor("KarboXXX",
                                "http://github.com/KarboXXX",
                                "https://avatars.githubusercontent.com/u/48266134?v=4"
                        )
                        .setColor(Color.GRAY);

                DiscordBuilder.getInstance().waypoints.asIncomingWebhook().get().sendMessage(embed);

                p.sendMessage(ChatColor.GREEN + "Waypoint \"Waypoint rápido\" " +
                        "("+x+" "+y+" "+z+" em "+world+") foi salvo!");

                return true;
            }

            StringBuilder nome = new StringBuilder();
            String x, y, z, world;

            try {
                x = String.valueOf(parseInt(args[0]));
                y = String.valueOf(parseInt(args[1]));
                z = String.valueOf(parseInt(args[2]));
                world = args[3];

                World saidWorld = DiscordIntegration.getInstance().getServer().getWorld(world);
                if (saidWorld == null) {
                    world = p.getLocation().getWorld().getName();
                    saidWorld = p.getLocation().getWorld();
                }

                Location saidLocation = new Location(saidWorld, Double.parseDouble(x),
                        Double.parseDouble(y), Double.parseDouble(z));

                if (saidWorld.getWorldBorder().isInside(saidLocation)) {
                    List<String> argsAsList = Arrays.asList(args);
                    /*argsAsList.remove(x);
                    argsAsList.remove(y);
                    argsAsList.remove(z);
                    argsAsList.remove(world);*/

                    final String finalWorld = world;
                    argsAsList.forEach(arg -> {
                        if (Objects.equals(arg, x) || Objects.equals(arg, y) ||
                            Objects.equals(arg, z) || Objects.equals(arg, finalWorld))
                            return;

                        if (nome.length() <= 60)
                            nome.append(arg).append(" ");
                    });

                    nome.deleteCharAt(nome.lastIndexOf(" "));

                    EmbedBuilder embed = new EmbedBuilder()
                            .setTitle("Novo waypoint de " + p.getName())
                            .addInlineField("\"" + nome.toString() + "\"",
                                    String.format("**%s %s %s**, (%s)", x, y, z, world)
                            )
                            .setAuthor("KarboXXX",
                                    "http://github.com/KarboXXX",
                                    "https://avatars.githubusercontent.com/u/48266134?v=4"
                            )
                            .setColor(Color.GRAY);

                    DiscordBuilder.getInstance().waypoints.asIncomingWebhook().get().sendMessage(embed);

                    p.sendMessage(ChatColor.GREEN + "Waypoint \"" + nome.toString() + "\" " +
                            "("+x+" "+y+" "+z+" em "+world+") foi salvo!");

                } else {
                    sender.sendMessage(
                            ChatColor.RED + "Você não pode criar um waypoint fora da borda do mundo."
                    );
                }
            } catch (NumberFormatException e) {
                sender.sendMessage(
                        ChatColor.RED + command.getUsage()
                );
            }
        } else {
            sender.sendMessage(
                    "Apenas jogadores podem salvar coordenadas no chat do servidor do Discord."
            );
        }

        return true;
    }
}
