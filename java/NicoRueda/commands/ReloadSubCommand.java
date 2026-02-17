package NicoRueda.commands;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;

import NicoRueda.PlayableRacesPlugin;

import javax.annotation.Nonnull;

/**
 * /race reload - Reload plugin configuration
 */
public class ReloadSubCommand extends CommandBase {

    public ReloadSubCommand() {
        super("reload", "Reload plugin configuration");
        this.setPermissionGroup(null);
    }

    @Override
    protected boolean canGeneratePermission() {
        return false;
    }

    @Override
    protected void executeSync(@Nonnull CommandContext context) {
        PlayableRacesPlugin plugin = PlayableRacesPlugin.getInstance();

        if (plugin == null) {
            context.sendMessage(Message.raw("Error: Plugin not loaded"));
            return;
        }

        context.sendMessage(Message.raw("Reloading PlayableRaces..."));

        // TODO: Add your reload logic here
        // Example: Reload config files, refresh caches, etc.

        context.sendMessage(Message.raw("PlayableRaces reloaded successfully!"));
    }
}