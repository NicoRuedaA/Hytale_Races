package com.tuusuario.myracemod.races;

import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;

public class HumanRace implements RaceDefinition {

    @Override
    public String id() {
        return "human";
    }

    @Override
    public String displayName() {
        return "Humano";
    }

    @Override
    public String tagline() {
        return "El habitante estándar. Sin bonificaciones ni penalizaciones.";
    }

    // ── Lógica de Stats (VACÍA) ───────────────────────────────────────────

    @Override
    public void applyStats(EntityStatMap stats, String modifierKey) {
        // No hacemos nada. El humano es la raza base.
    }

    @Override
    public void removeStats(EntityStatMap stats, String modifierKey) {
        // No hay nada que limpiar porque no aplicamos nada.
    }
}