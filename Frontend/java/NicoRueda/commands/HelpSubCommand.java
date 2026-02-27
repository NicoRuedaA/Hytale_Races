package NicoRueda.commands;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;

import javax.annotation.Nonnull;

/**
 * /race help — Muestra todos los comandos disponibles.
 */
public class HelpSubCommand extends CommandBase {

    public HelpSubCommand() {
        super("help", "Muestra los comandos disponibles");
        this.setPermissionGroup(null);
    }

    @Override
    protected boolean canGeneratePermission() { return false; }

    @Override
    protected void executeSync(@Nonnull CommandContext context) {
        context.sendMessage(Message.raw("§6══ PlayableRaces — Comandos ══"));
        context.sendMessage(Message.raw("§f/race help          §7— Muestra esta ayuda"));
        context.sendMessage(Message.raw("§f/race list          §7— Lista las razas disponibles"));
        context.sendMessage(Message.raw("§f/race change <raza> §7— Elige tu raza"));
        context.sendMessage(Message.raw("§f/race info          §7— Muestra tu raza actual"));
        context.sendMessage(Message.raw("§f/race reset         §7— Borra tu raza actual"));
        context.sendMessage(Message.raw("§f/race ui            §7— Abre el selector de raza (UI)"));
        context.sendMessage(Message.raw("§f/race reload        §7— Recarga la configuración"));
        context.sendMessage(Message.raw("§6══════════════════════════════"));
    }
}
