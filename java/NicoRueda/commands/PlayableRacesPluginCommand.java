package NicoRueda.commands;

import com.hypixel.hytale.server.core.command.system.basecommands.AbstractCommandCollection;

/**
 * Comando raíz /race.
 *
 * Subcomandos disponibles:
 *   /race help           — Muestra esta ayuda
 *   /race change <raza>  — Elige tu raza
 *   /race info           — Muestra tu raza actual
 *   /race reset          — Borra tu raza actual
 *   /race list           — Lista las razas disponibles
 *   /race ui             — Abre el dashboard de selección de raza
 *   /race reload         — Recarga la configuración del plugin
 */
public class PlayableRacesPluginCommand extends AbstractCommandCollection {

    public PlayableRacesPluginCommand() {
        super("race", "Comandos de PlayableRaces");

        // Subcomandos de información y administración
        addSubCommand(new HelpSubCommand());
        addSubCommand(new ReloadSubCommand());

        // Subcomandos de raza (lógica de gameplay)
        addSubCommand(new RaceCommand.ChangeSubCommand());
        addSubCommand(new RaceCommand.InfoRaceSubCommand());
        addSubCommand(new RaceCommand.ResetSubCommand());
        addSubCommand(new RaceCommand.ListSubCommand());

        // Subcomando de UI
        addSubCommand(new UISubCommand());
    }

    @Override
    protected boolean canGeneratePermission() {
        return false;
    }
}
