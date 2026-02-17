package NicoRueda.races;

import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;

public interface RaceDefinition {

    String id();
    String displayName();
    String tagline();

    void applyStats(EntityStatMap stats, String modifierKey);
    void removeStats(EntityStatMap stats, String modifierKey);
}
