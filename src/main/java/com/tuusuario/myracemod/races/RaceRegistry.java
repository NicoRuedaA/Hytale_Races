package com.tuusuario.myracemod.races;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Registro central de todas las razas disponibles.
 *
 * Para añadir una raza nueva en el futuro:
 *   1. Crea tu clase (p.ej. OrcRace implements RaceDefinition)
 *   2. Llama a RaceRegistry.register(new OrcRace()) en MyRaceMod.start()
 */
public final class RaceRegistry {

    // LinkedHashMap para mantener el orden de registro (importante para la UI)
    private static final Map<String, RaceDefinition> RACES = new LinkedHashMap<>();

    private RaceRegistry() {}

    /** Registra una raza. Se llama desde MyRaceMod al iniciar. */
    public static void register(RaceDefinition race) {
        if (race == null || race.id() == null) return;
        RACES.put(race.id().toLowerCase(), race);
        System.out.println("[MyRaceMod] Raza registrada: " + race.displayName()
                + " (id: " + race.id() + ")");
    }

    /**
     * Obtiene una raza por su id.
     * Devuelve null si no existe — el caller debe comprobar esto.
     */
    public static RaceDefinition get(String id) {
        if (id == null) return null;
        return RACES.get(id.toLowerCase());
    }

    /** Comprueba si existe una raza con ese id. */
    public static boolean exists(String id) {
        return id != null && RACES.containsKey(id.toLowerCase());
    }

    /** Devuelve todas las razas registradas (de solo lectura). */
    public static Collection<RaceDefinition> all() {
        return Collections.unmodifiableCollection(RACES.values());
    }

    /** Limpia el registro (útil para futuros hot-reloads). */
    public static void clear() {
        RACES.clear();
    }
}
