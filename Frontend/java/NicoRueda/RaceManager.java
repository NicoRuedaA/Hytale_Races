package NicoRueda;

import NicoRueda.components.RaceData;
import NicoRueda.races.RaceDefinition;
import NicoRueda.races.RaceRegistry;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

public final class RaceManager {

    private static ComponentType<EntityStore, RaceData> raceDataType;

    private static final String RACE_MODIFIER_KEY = "playableraces_race_bonus";

    private RaceManager() {}

    public static void setRaceDataType(ComponentType<EntityStore, RaceData> type) {
        raceDataType = type;
    }

    // ── Seleccionar raza ───────────────────────────────────────────────────

    public static void selectRace(Player player, String newRaceId) {
        if (player == null || newRaceId == null || raceDataType == null) return;

        RaceDefinition newRace = RaceRegistry.get(newRaceId);
        if (newRace == null) {
            player.sendMessage(Message.raw("Error: La raza '" + newRaceId + "' no existe."));
            return;
        }

        var ref = player.getReference();
        if (ref == null || !ref.isValid()) return;

        try {
            var store = ref.getStore();

            EntityStatMap stats = (EntityStatMap) store.getComponent(ref, EntityStatMap.getComponentType());
            RaceData data = (RaceData) store.getComponent(ref, raceDataType);

            if (data == null) data = new RaceData();

            if (data.hasRace() && data.getSelectedRace().equalsIgnoreCase(newRaceId)) {
                player.sendMessage(Message.raw("¡Ya eres de la raza " + newRace.displayName() + "!"));
                return;
            }

            // 1. Limpiar stats de la raza anterior
            if (data.hasRace() && stats != null) {
                RaceDefinition oldRace = RaceRegistry.get(data.getSelectedRace());
                if (oldRace != null) {
                    oldRace.removeStats(stats, RACE_MODIFIER_KEY);
                }
            }

            // 2. Persistir la nueva raza
            data.setSelectedRace(newRaceId);
            store.putComponent(ref, raceDataType, data);

            // 3. Aplicar stats de la nueva raza
            if (stats != null) {
                newRace.applyStats(stats, RACE_MODIFIER_KEY);
                stats.update();
            }

            System.out.println("[PlayableRaces] Raza cambiada a: "
                    + newRace.displayName() + " (" + player.getDisplayName() + ")");

        } catch (Exception e) {
            System.err.println("[PlayableRaces] Error cambiando raza: " + e.getMessage());
            e.printStackTrace();
            player.sendMessage(Message.raw("Ocurrió un error interno al cambiar de raza."));
        }
    }

    // ── Consultas ──────────────────────────────────────────────────────────

    public static String getPlayerRace(Player player) {
        if (player == null || raceDataType == null) return null;

        var ref = player.getReference();
        if (ref == null || !ref.isValid()) return null;

        try {
            RaceData data = (RaceData) ref.getStore().getComponent(ref, raceDataType);
            return (data != null && data.hasRace()) ? data.getSelectedRace() : null;
        } catch (Exception e) {
            return null;
        }
    }

    public static boolean hasRace(Player player) {
        return getPlayerRace(player) != null;
    }

    /**
     * Re-aplica los stats de la raza guardada al iniciar sesión.
     * Necesario porque EntityStatMap es volátil (no persiste entre sesiones),
     * pero RaceData sí persiste.
     */
    public static void reapplyStats(Player player, String raceId) {
        if (player == null || raceDataType == null) return;

        RaceDefinition race = RaceRegistry.get(raceId);
        if (race == null) return;

        var ref = player.getReference();
        if (ref == null || !ref.isValid()) return;

        try {
            var store = ref.getStore();
            EntityStatMap stats = (EntityStatMap) store.getComponent(ref, EntityStatMap.getComponentType());
            if (stats != null) {
                race.applyStats(stats, RACE_MODIFIER_KEY);
                stats.update();
            }
        } catch (Exception e) {
            System.err.println("[PlayableRaces] Error re-aplicando stats al login: " + e.getMessage());
        }
    }

    public static void resetRace(Player player) {
        if (player == null || raceDataType == null) return;

        var ref = player.getReference();
        if (ref == null || !ref.isValid()) return;

        try {
            var store = ref.getStore();
            EntityStatMap stats = (EntityStatMap) store.getComponent(ref, EntityStatMap.getComponentType());
            RaceData data = (RaceData) store.getComponent(ref, raceDataType);

            if (data != null && data.hasRace() && stats != null) {
                RaceDefinition currentRace = RaceRegistry.get(data.getSelectedRace());
                if (currentRace != null) {
                    currentRace.removeStats(stats, RACE_MODIFIER_KEY);
                    stats.update();
                }
            }

            store.putComponent(ref, raceDataType, new RaceData());
            System.out.println("[PlayableRaces] Raza reseteada para " + player.getDisplayName());

        } catch (Exception e) {
            System.err.println("[PlayableRaces] Error reseteando raza: " + e.getMessage());
        }
    }
}
