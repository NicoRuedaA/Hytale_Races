package NicoRueda.commands;

import NicoRueda.RaceManager;
import NicoRueda.races.RaceDefinition;
import NicoRueda.races.RaceRegistry;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgumentType;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import javax.annotation.Nonnull;
import java.util.stream.Collectors;

/**
 * Subcomandos de raza disponibles vía chat:
 *
 *   /race change <raza>  — Elige una raza
 *   /race info           — Muestra tu raza actual
 *   /race reset          — Borra tu raza actual
 *   /race list           — Lista todas las razas disponibles
 *
 * Estos subcomandos se registran en PlayableRacesPluginCommand junto a
 * HelpSubCommand, InfoSubCommand, ReloadSubCommand y UISubCommand.
 */
public final class RaceCommand {

    private RaceCommand() {}

    // ── /race change <raza> ────────────────────────────────────────────────

    public static class ChangeSubCommand extends AbstractPlayerCommand {

        private final RequiredArg<String> raceArg;

        public ChangeSubCommand() {
            super("change", "Elige tu raza");
            this.raceArg = withRequiredArg(
                "raza",
                "Raza a elegir: " + listRaceIds(),
                (ArgumentType<String>) ArgTypes.STRING
            );
        }

        @Override
        protected boolean canGeneratePermission() { return false; }

        @Override
        protected void execute(
            @Nonnull CommandContext ctx,
            @Nonnull Store<EntityStore> store,
            @Nonnull Ref<EntityStore> ref,
            @Nonnull PlayerRef playerRef,
            @Nonnull World world
        ) {
            String input  = raceArg.get(ctx);
            String raceId = (input != null) ? input.toLowerCase() : null;

            if (!RaceRegistry.exists(raceId)) {
                ctx.sendMessage(Message.raw("§cRaza inválida: §f" + input));
                ctx.sendMessage(Message.raw("§6Disponibles: §f" + listRaceIds()));
                return;
            }

            Player player = store.getComponent(ref, Player.getComponentType());
            if (player == null) {
                ctx.sendMessage(Message.raw("§cError: no se pudo obtener el jugador."));
                return;
            }

            String currentRace = RaceManager.getPlayerRace(player);
            if (raceId.equals(currentRace)) {
                ctx.sendMessage(Message.raw("§eYa eres §f"
                    + RaceRegistry.get(raceId).displayName() + "§e."));
                return;
            }

            RaceManager.selectRace(player, raceId);

            RaceDefinition race = RaceRegistry.get(raceId);
            ctx.sendMessage(Message.raw("§6╔═══════════════════════════╗"));
            ctx.sendMessage(Message.raw("§6║  §aRaza seleccionada§6       ║"));
            ctx.sendMessage(Message.raw("§6╠═══════════════════════════╣"));
            ctx.sendMessage(Message.raw("§6║  §f" + race.displayName()));
            ctx.sendMessage(Message.raw("§6║  §7" + race.tagline()));
            ctx.sendMessage(Message.raw("§6╚═══════════════════════════╝"));

            if (currentRace != null) {
                ctx.sendMessage(Message.raw("§8(antes eras §7"
                    + RaceRegistry.get(currentRace).displayName() + "§8)"));
            }
        }
    }

    // ── /race info ─────────────────────────────────────────────────────────

    public static class InfoRaceSubCommand extends AbstractPlayerCommand {

        public InfoRaceSubCommand() {
            super("info", "Muestra tu raza actual");
        }

        @Override
        protected boolean canGeneratePermission() { return false; }

        @Override
        protected void execute(
            @Nonnull CommandContext ctx,
            @Nonnull Store<EntityStore> store,
            @Nonnull Ref<EntityStore> ref,
            @Nonnull PlayerRef playerRef,
            @Nonnull World world
        ) {
            Player player = store.getComponent(ref, Player.getComponentType());
            if (player == null) {
                ctx.sendMessage(Message.raw("§cError: no se pudo obtener el jugador."));
                return;
            }

            String raceId = RaceManager.getPlayerRace(player);
            if (raceId == null) {
                ctx.sendMessage(Message.raw("§cNo tienes ninguna raza asignada todavía."));
                ctx.sendMessage(Message.raw("§7Usa §f/race list §7para ver las opciones."));
                return;
            }

            RaceDefinition race = RaceRegistry.get(raceId);
            ctx.sendMessage(Message.raw("§6Tu raza: §f" + race.displayName()));
            ctx.sendMessage(Message.raw("§7" + race.tagline()));
        }
    }

    // ── /race reset ────────────────────────────────────────────────────────

    public static class ResetSubCommand extends AbstractPlayerCommand {

        public ResetSubCommand() {
            super("reset", "Borra tu raza actual");
        }

        @Override
        protected boolean canGeneratePermission() { return false; }

        @Override
        protected void execute(
            @Nonnull CommandContext ctx,
            @Nonnull Store<EntityStore> store,
            @Nonnull Ref<EntityStore> ref,
            @Nonnull PlayerRef playerRef,
            @Nonnull World world
        ) {
            Player player = store.getComponent(ref, Player.getComponentType());
            if (player == null) {
                ctx.sendMessage(Message.raw("§cError: no se pudo obtener el jugador."));
                return;
            }

            if (!RaceManager.hasRace(player)) {
                ctx.sendMessage(Message.raw("§eNo tenías ninguna raza asignada."));
                return;
            }

            RaceManager.resetRace(player);
            ctx.sendMessage(Message.raw("§aRaza borrada. Usa §f/race change <raza> §apara elegir de nuevo."));
        }
    }

    // ── /race list ─────────────────────────────────────────────────────────

    public static class ListSubCommand extends AbstractPlayerCommand {

        public ListSubCommand() {
            super("list", "Lista las razas disponibles");
        }

        @Override
        protected boolean canGeneratePermission() { return false; }

        @Override
        protected void execute(
            @Nonnull CommandContext ctx,
            @Nonnull Store<EntityStore> store,
            @Nonnull Ref<EntityStore> ref,
            @Nonnull PlayerRef playerRef,
            @Nonnull World world
        ) {
            ctx.sendMessage(Message.raw("§6══ Razas disponibles ══"));
            for (RaceDefinition race : RaceRegistry.all()) {
                ctx.sendMessage(Message.raw("§f" + race.displayName()
                    + " §8— §7/race change " + race.id()));
                ctx.sendMessage(Message.raw("  §8" + race.tagline()));
            }
        }
    }

    // ── Helper ─────────────────────────────────────────────────────────────

    private static String listRaceIds() {
        return RaceRegistry.all().stream()
            .map(RaceDefinition::id)
            .collect(Collectors.joining(", "));
    }
}
