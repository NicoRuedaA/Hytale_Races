package NicoRueda.races;

import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
import com.hypixel.hytale.server.core.modules.entitystats.asset.DefaultEntityStatTypes;
import com.hypixel.hytale.server.core.modules.entitystats.asset.EntityStatType;
import com.hypixel.hytale.server.core.modules.entitystats.modifier.Modifier;
import com.hypixel.hytale.server.core.modules.entitystats.modifier.StaticModifier;

public class ElfRace implements RaceDefinition {

    @Override
    public String id() { return "elf"; }

    @Override
    public String displayName() { return "Elfo"; }

    @Override
    public String tagline() { return "Ágil e incansable, se mueve como el viento."; }

    @Override
    public void applyStats(EntityStatMap stats, String modifierKey) {
        int manaId  = DefaultEntityStatTypes.getMana();
        int speedId = getSpeedStatId();

        // 1. Maná (+50)
        StaticModifier manaBonus = new StaticModifier(
                Modifier.ModifierTarget.MAX,
                StaticModifier.CalculationType.ADDITIVE,
                50.0f
        );
        stats.putModifier(manaId, modifierKey, manaBonus);
        stats.addStatValue(manaId, 50.0f);

        // 2. Velocidad (+20% — MULTIPLICATIVE)
        if (speedId != -1) {
            StaticModifier speedBonus = new StaticModifier(
                    Modifier.ModifierTarget.MAX,
                    StaticModifier.CalculationType.MULTIPLICATIVE,
                    0.2f
            );
            stats.putModifier(speedId, modifierKey, speedBonus);
        }
    }

    @Override
    public void removeStats(EntityStatMap stats, String modifierKey) {
        int manaId  = DefaultEntityStatTypes.getMana();
        int speedId = getSpeedStatId();

        stats.removeModifier(manaId, modifierKey);
        if (speedId > 0) {
            stats.removeModifier(speedId, modifierKey);
        }
    }

    private int getSpeedStatId() {
        try {
            if (EntityStatType.getAssetMap() != null) {
                int idx = EntityStatType.getAssetMap().getIndex("MovementSpeed");
                return (idx > 0) ? idx : -1;
            }
        } catch (Exception ignored) {}
        return -1;
    }
}
