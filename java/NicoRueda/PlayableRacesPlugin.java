package NicoRueda;

import NicoRueda.commands.PlayableRacesPluginCommand;
import NicoRueda.components.RaceData;
import NicoRueda.listeners.PlayerListener;
import NicoRueda.listeners.UndeadEffectSystem;
import NicoRueda.races.AmphibianRace;
import NicoRueda.races.BeastRace;
import NicoRueda.races.EldritchRace;
import NicoRueda.races.HumanRace;
import NicoRueda.races.MonsterRace;
import NicoRueda.races.UndeadRace;
import NicoRueda.races.RaceRegistry;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.event.EventRegistry;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import javax.annotation.Nonnull;
import java.util.logging.Level;

public class PlayableRacesPlugin extends JavaPlugin {

    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
    private static PlayableRacesPlugin instance;

    public PlayableRacesPlugin(@Nonnull JavaPluginInit init) {
        super(init);
        instance = this;
    }

    public static PlayableRacesPlugin getInstance() {
        return instance;
    }

    @Override
    protected void setup() {
        LOGGER.at(Level.INFO).log("[PlayableRaces] Iniciando setup...");
        registerRaces();
        registerRaceDataComponent();
        registerCommands();
        registerListeners();
        LOGGER.at(Level.INFO).log("[PlayableRaces] Setup completado.");
    }

    @Override
    protected void start() {
        LOGGER.at(Level.INFO).log("[PlayableRaces] Iniciado. Usa /race help para ver los comandos.");
    }

    @Override
    protected void shutdown() {
        LOGGER.at(Level.INFO).log("[PlayableRaces] Cerrando...");
        RaceRegistry.clear();
        instance = null;
    }

    private void registerRaces() {
        RaceRegistry.register(new HumanRace());
        RaceRegistry.register(new EldritchRace());
        RaceRegistry.register(new AmphibianRace());
        RaceRegistry.register(new BeastRace());
        RaceRegistry.register(new MonsterRace());
        RaceRegistry.register(new UndeadRace());
        LOGGER.at(Level.INFO).log("[PlayableRaces] %d razas registradas.", RaceRegistry.all().size());
    }

    private void registerRaceDataComponent() {
        try {
            ComponentType<EntityStore, RaceData> raceDataType =
                    getEntityStoreRegistry().registerComponent(
                            RaceData.class, "RaceData", RaceData.CODEC);
            RaceManager.setRaceDataType(raceDataType);
            LOGGER.at(Level.INFO).log("[PlayableRaces] Componente RaceData registrado.");
        } catch (Exception e) {
            LOGGER.at(Level.SEVERE).withCause(e).log("[PlayableRaces] Error registrando RaceData.");
        }
    }

    private void registerCommands() {
        try {
            getCommandRegistry().registerCommand(new PlayableRacesPluginCommand());
            LOGGER.at(Level.INFO).log("[PlayableRaces] Comando /race registrado.");
        } catch (Exception e) {
            LOGGER.at(Level.WARNING).withCause(e).log("[PlayableRaces] Error registrando comandos.");
        }
    }

    private void registerListeners() {
        EventRegistry eventBus = getEventRegistry();
        try {
            new PlayerListener().register(eventBus);
            LOGGER.at(Level.INFO).log("[PlayableRaces] Listeners de jugador registrados.");
        } catch (Exception e) {
            LOGGER.at(Level.WARNING).withCause(e).log("[PlayableRaces] Error registrando PlayerListener.");
        }
        try {
            new UndeadEffectSystem().register(eventBus);
        } catch (Exception e) {
            LOGGER.at(Level.WARNING).withCause(e).log("[PlayableRaces] Error registrando UndeadEffectSystem.");
        }
        try {
            eventBus.registerGlobal(PlayerReadyEvent.class, this::onPlayerReady);
            LOGGER.at(Level.INFO).log("[PlayableRaces] Listener PlayerReadyEvent registrado.");
        } catch (Exception e) {
            LOGGER.at(Level.WARNING).withCause(e).log("[PlayableRaces] Error registrando PlayerReadyEvent.");
        }
    }

    private void onPlayerReady(PlayerReadyEvent event) {
        Player player = event.getPlayer();
        if (player == null) return;

        String raceId = RaceManager.getPlayerRace(player);
        if (raceId != null) {
            RaceManager.reapplyStats(player, raceId);
            player.sendMessage(Message.raw("[PlayableRaces] Tu raza actual: " + raceId
                    + ". Usa /race info para verla o /race ui para cambiarla."));
        } else {
            player.sendMessage(Message.raw("[PlayableRaces] Sin raza asignada. "
                    + "Usa /race ui para elegir o /race list para ver las opciones."));
        }
    }
}
