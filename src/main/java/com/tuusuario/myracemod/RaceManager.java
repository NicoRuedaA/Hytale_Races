package com.tuusuario.myracemod;

import com.tuusuario.myracemod.components.RaceData;
import com.tuusuario.myracemod.races.RaceDefinition;
import com.tuusuario.myracemod.races.RaceRegistry;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

public final class RaceManager {

    private static ComponentType<EntityStore, RaceData> raceDataType;

    // Esta clave se pasa a las razas para que sepan qué modificadores borrar/poner
    private static final String RACE_MODIFIER_KEY = "myracemod_race_bonus";

    private RaceManager() {}

    public static void setRaceDataType(ComponentType<EntityStore, RaceData> type) {
        raceDataType = type;
    }

    // ── Seleccionar raza (Lógica Central) ──────────────────────────────────

    public static void selectRace(Player player, String newRaceId) {
        // Validaciones básicas
        if (player == null || newRaceId == null || raceDataType == null) return;

        // Verificamos que la raza solicitada exista en el registro
        RaceDefinition newRace = RaceRegistry.get(newRaceId);
        if (newRace == null) {
            player.sendMessage(Message.raw("Error: La raza '" + newRaceId + "' no existe."));
            return;
        }

        var ref = player.getReference();
        if (ref == null || !ref.isValid()) return;

        try {
            var store = ref.getStore();

            // Obtenemos componentes necesarios (Stats y Datos de Raza)
            EntityStatMap stats = (EntityStatMap) store.getComponent(ref, EntityStatMap.getComponentType());
            RaceData data = (RaceData) store.getComponent(ref, raceDataType);

            // Si no tiene datos de raza, creamos uno nuevo
            if (data == null) data = new RaceData();

            // Si el jugador ya es de esta raza, no hacemos nada
            if (data.hasRace() && data.getSelectedRace().equalsIgnoreCase(newRaceId)) {
                player.sendMessage(Message.raw("¡Ya eres de la raza " + newRace.displayName() + "!"));
                return;
            }

            // 1. LIMPIEZA: Quitar stats de la raza ANTERIOR
            if (data.hasRace() && stats != null) {
                String oldRaceId = data.getSelectedRace();
                RaceDefinition oldRace = RaceRegistry.get(oldRaceId);

                if (oldRace != null) {
                    // La raza antigua limpia sus propios modificadores
                    oldRace.removeStats(stats, RACE_MODIFIER_KEY);
                }
            }

            // 2. PERSISTENCIA: Guardar la NUEVA raza en la base de datos
            data.setSelectedRace(newRaceId);
            store.putComponent(ref, raceDataType, data);

            // 3. APLICACIÓN: Poner stats de la NUEVA raza
            if (stats != null) {
                newRace.applyStats(stats, RACE_MODIFIER_KEY);
                stats.update(); // Hace efectivos los modificadores en el cliente
            }

            // Mensajes de éxito
            System.out.println("[MyRaceMod] Raza cambiada a: " + newRace.displayName() + " (" + player.getDisplayName() + ")");
            player.sendMessage(Message.raw("¡Has cambiado tu raza a: " + newRace.displayName() + "!"));
            player.sendMessage(Message.raw("" + newRace.tagline()));

        } catch (Exception e) {
            System.err.println("[MyRaceMod] Error cambiando raza: " + e.getMessage());
            e.printStackTrace();
            player.sendMessage(Message.raw("Ocurrió un error interno al cambiar de raza."));
        }
    }

    // ── Getters y Utilidades ───────────────────────────────────────────────

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
     * pero RaceData sí persiste. Sin esto, removeStats() falla al cambiar de raza
     * porque los modificadores no existen en el EntityStatMap actual.
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
            System.err.println("[MyRaceMod] Error re-aplicando stats al login: " + e.getMessage());
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

            // Limpiamos stats de la raza actual si existe
            if (data != null && data.hasRace() && stats != null) {
                RaceDefinition currentRace = RaceRegistry.get(data.getSelectedRace());
                if (currentRace != null) {
                    currentRace.removeStats(stats, RACE_MODIFIER_KEY);
                    stats.update(); // Hace efectiva la eliminación de modificadores
                }
            }

            // Guardamos datos vacíos (sin raza seleccionada)
            store.putComponent(ref, raceDataType, new RaceData());

            System.out.println("[MyRaceMod] Raza reseteada para " + player.getDisplayName());
            player.sendMessage(Message.raw("Tu raza ha sido reiniciada."));

        } catch (Exception e) {
            System.err.println("[MyRaceMod] Error reseteando: " + e.getMessage());
        }
    }
}