package com.tuusuario.myracemod.races;

import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;

public interface RaceDefinition {

    String id();
    String displayName();
    String tagline();

    // Estos son los métodos nuevos obligatorios:
    void applyStats(EntityStatMap stats, String modifierKey);
    void removeStats(EntityStatMap stats, String modifierKey);
}