package NicoRueda.races;

import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
import com.hypixel.hytale.server.core.modules.entitystats.asset.DefaultEntityStatTypes;
import com.hypixel.hytale.server.core.modules.entitystats.asset.EntityStatType;
import com.hypixel.hytale.server.core.modules.entitystats.modifier.Modifier;
import com.hypixel.hytale.server.core.modules.entitystats.modifier.StaticModifier;

public class MonsterRace implements RaceDefinition {

    @Override
    public String id() { return "monster"; }

    @Override
    public String displayName() { return "Monstruo"; }

    @Override
    public String tagline() { return "Resistente como una roca, pero lento como una tortuga."; }

    @Override
    public void applyStats(EntityStatMap stats, String modifierKey) {
        int healthId = DefaultEntityStatTypes.getHealth();
        int speedId  = getSpeedStatId();
        int armorId  = getArmorStatId();

        // 1. Vida (+40 HP máximo)
        StaticModifier healthBonus = new StaticModifier(
                Modifier.ModifierTarget.MAX,
                StaticModifier.CalculationType.ADDITIVE,
                40.0f
        );
        stats.putModifier(healthId, modifierKey, healthBonus);

        // 2. Armadura (+5 puntos base)
        if (armorId != -1) {
            StaticModifier armorBonus = new StaticModifier(
                    Modifier.ModifierTarget.MAX,
                    StaticModifier.CalculationType.ADDITIVE,
                    5.0f
            );
            stats.putModifier(armorId, modifierKey, armorBonus);
        }

        // 3. Velocidad (-20% — MULTIPLICATIVE)
        if (speedId != -1) {
            StaticModifier speedPenalty = new StaticModifier(
                    Modifier.ModifierTarget.MAX,
                    StaticModifier.CalculationType.MULTIPLICATIVE,
                    -0.20f
            );
            stats.putModifier(speedId, modifierKey, speedPenalty);
        }
    }

    @Override
    public void removeStats(EntityStatMap stats, String modifierKey) {
        int healthId = DefaultEntityStatTypes.getHealth();
        int speedId  = getSpeedStatId();
        int armorId  = getArmorStatId();

        stats.removeModifier(healthId, modifierKey);
        if (speedId > 0) stats.removeModifier(speedId, modifierKey);
        if (armorId > 0) stats.removeModifier(armorId, modifierKey);
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

    private int getArmorStatId() {
        try {
            if (EntityStatType.getAssetMap() != null) {
                int idx = EntityStatType.getAssetMap().getIndex("Armor");
                if (idx > 0) return idx;
                idx = EntityStatType.getAssetMap().getIndex("Defence");
                if (idx > 0) return idx;
                idx = EntityStatType.getAssetMap().getIndex("Defense");
                if (idx > 0) return idx;
                idx = EntityStatType.getAssetMap().getIndex("ArmorValue");
                return (idx > 0) ? idx : -1;
            }
        } catch (Exception ignored) {}
        return -1;
    }
}
