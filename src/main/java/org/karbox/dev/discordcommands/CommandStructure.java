package org.karbox.dev.discordcommands;

import org.javacord.api.DiscordApi;
import org.karbox.dev.DiscordBuilder;

// With an interface, we can set for ourselves the default structure for commands on discord,
// and how they should be passed.

// When creating a command, we're going to @Override the registerCommand, and do what we have to do,
// and every propriety here, will be passed and implemented by default on every command

// We need to call on every command, our DiscordApi object (duh) so, we implement it by default, here.

// We let registerCommand() function here, because it is EXPECTED to exist.
// We can pass empty variables, functions and more, and @Override it, because we don't actually want it empty,
// we just expect it to be EXIST, but to be different on every class that has this interface implemented.

public interface CommandStructure {
    DiscordApi api = DiscordBuilder.getInstance().api;
    default void registerCommand() {}
}
