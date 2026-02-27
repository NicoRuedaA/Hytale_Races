package NicoRueda.races;

import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
import com.hypixel.hytale.server.core.modules.entitystats.asset.EntityStatType;
import com.hypixel.hytale.server.core.modules.entitystats.modifier.Modifier;
import com.hypixel.hytale.server.core.modules.entitystats.modifier.StaticModifier;

public class AmphibianRace implements RaceDefinition {

    @Override
    public String id() { return "amphibian"; }

    @Override
    public String displayName() { return "Anfibio"; }

    @Override
    public String tagline() { return "Los océanos son tu hogar. Respira bajo el agua sin límites."; }

    @Override
    public void applyStats(EntityStatMap stats, String modifierKey) {
        int oxygenId   = getOxygenStatId();
        int swimSpeedId = getSwimSpeedStatId();

        // 1. Respiración ilimitada bajo el agua (valor extremadamente alto)
        if (oxygenId != -1) {
            StaticModifier oxygenBonus = new StaticModifier(
                    Modifier.ModifierTarget.MAX,
                    StaticModifier.CalculationType.ADDITIVE,
                    999999.0f
            );
            stats.putModifier(oxygenId, modifierKey, oxygenBonus);
        }

        // 2. Velocidad de nado (+30% — MULTIPLICATIVE)
        if (swimSpeedId != -1) {
            StaticModifier swimSpeedBonus = new StaticModifier(
                    Modifier.ModifierTarget.MAX,
                    StaticModifier.CalculationType.MULTIPLICATIVE,
                    0.30f
            );
            stats.putModifier(swimSpeedId, modifierKey, swimSpeedBonus);
        }
    }

    @Override
    public void removeStats(EntityStatMap stats, String modifierKey) {
        int oxygenId    = getOxygenStatId();
        int swimSpeedId = getSwimSpeedStatId();

        if (oxygenId > 0)    stats.removeModifier(oxygenId, modifierKey);
        if (swimSpeedId > 0) stats.removeModifier(swimSpeedId, modifierKey);
    }

    private int getOxygenStatId() {
        try {
            if (EntityStatType.getAssetMap() != null) {
                int idx = EntityStatType.getAssetMap().getIndex("Oxygen");
                if (idx > 0) return idx;
                idx = EntityStatType.getAssetMap().getIndex("Air");
                if (idx > 0) return idx;
                idx = EntityStatType.getAssetMap().getIndex("Breath");
                if (idx > 0) return idx;
                idx = EntityStatType.getAssetMap().getIndex("WaterBreathing");
                return (idx > 0) ? idx : -1;
            }
        } catch (Exception ignored) {}
        return -1;
    }

    private int getSwimSpeedStatId() {
        try {
            if (EntityStatType.getAssetMap() != null) {
                int idx = EntityStatType.getAssetMap().getIndex("SwimSpeed");
                if (idx > 0) return idx;
                idx = EntityStatType.getAssetMap().getIndex("SwimmingSpeed");
                if (idx > 0) return idx;
                idx = EntityStatType.getAssetMap().getIndex("WaterSpeed");
                return (idx > 0) ? idx : -1;
            }
        } catch (Exception ignored) {}
        return -1;
    }
}
