package com.tuusuario.myracemod.commands;

import com.hypixel.hytale.server.core.Message;
import com.tuusuario.myracemod.RaceManager;
import com.tuusuario.myracemod.races.RaceDefinition;
import com.tuusuario.myracemod.races.RaceRegistry;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.command.system.AbstractCommand;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgumentType;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractCommandCollection;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import java.util.stream.Collectors;
import javax.annotation.Nonnull;

/**
 * Comandos disponibles:
 *
 *   /race change <raza>   — Elige una raza y recibe mensaje de confirmación
 *   /race info            — Muestra qué raza tienes actualmente
 *   /race reset           — Borra tu raza actual
 *   /race list            — Lista todas las razas disponibles
 */
public class RaceCommand extends AbstractCommandCollection {

    public RaceCommand() {
        super("race", "Gestión de razas");
        addSubCommand(new ChangeCommand());
        addSubCommand(new InfoCommand());
        addSubCommand(new ResetCommand());
        addSubCommand(new ListCommand());
    }

    @Override
    protected boolean canGeneratePermission() { return false; }

    // ════════════════════════════════════════════════════════════════════════
    //  /race change <raza>
    // ════════════════════════════════════════════════════════════════════════

    private static class ChangeCommand extends AbstractPlayerCommand {

        private final RequiredArg<String> raceArg;

        ChangeCommand() {
            super("change", "Elige tu raza", false);
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

            // Validar que la raza existe
            if (!RaceRegistry.exists(raceId)) {
                ctx.sendMessage(Message.raw("§cRaza inválida: §f" + input));
                ctx.sendMessage(Message.raw("§6Disponibles: §f" + listRaceIds()));
                return;
            }

            // Obtener el jugador para leer su raza actual
            Player player = (Player) store.getComponent(ref, Player.getComponentType());
            if (player == null) {
                ctx.sendMessage(Message.raw("§cError: no se pudo obtener el jugador."));
                return;
            }

            // Comprobar si ya tiene esa raza
            String currentRace = RaceManager.getPlayerRace(player);
            if (raceId.equals(currentRace)) {
                ctx.sendMessage(Message.raw("§eYa eres §f"
                    + RaceRegistry.get(raceId).displayName() + "§e."));
                return;
            }

            // Guardar la nueva raza
            RaceManager.selectRace(player, raceId);

            // ── Mensaje de confirmación ────────────────────────────────────
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

    // ════════════════════════════════════════════════════════════════════════
    //  /race info
    // ════════════════════════════════════════════════════════════════════════

    private static class InfoCommand extends AbstractPlayerCommand {

        InfoCommand() {
            super("info", "Muestra tu raza actual", false);
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
            Player player = (Player) store.getComponent(ref, Player.getComponentType());
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

    // ════════════════════════════════════════════════════════════════════════
    //  /race reset
    // ════════════════════════════════════════════════════════════════════════

    private static class ResetCommand extends AbstractPlayerCommand {

        ResetCommand() {
            super("reset", "Borra tu raza actual", false);
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
            Player player = (Player) store.getComponent(ref, Player.getComponentType());
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

    // ════════════════════════════════════════════════════════════════════════
    //  /race list
    // ════════════════════════════════════════════════════════════════════════

    private static class ListCommand extends AbstractPlayerCommand {

        ListCommand() {
            super("list", "Lista las razas disponibles", false);
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
