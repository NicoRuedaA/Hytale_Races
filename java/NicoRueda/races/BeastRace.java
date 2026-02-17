package NicoRueda.races;

import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
import com.hypixel.hytale.server.core.modules.entitystats.asset.DefaultEntityStatTypes;
import com.hypixel.hytale.server.core.modules.entitystats.asset.EntityStatType;
import com.hypixel.hytale.server.core.modules.entitystats.modifier.Modifier;
import com.hypixel.hytale.server.core.modules.entitystats.modifier.StaticModifier;

public class BeastRace implements RaceDefinition {

    @Override
    public String id() { return "beast"; }

    @Override
    public String displayName() { return "Bestia"; }

    @Override
    public String tagline() { return "Fuerza salvaje y velocidad depredadora. La magia no es su fuerte."; }

    @Override
    public void applyStats(EntityStatMap stats, String modifierKey) {
        int manaId         = DefaultEntityStatTypes.getMana();
        int speedId        = getSpeedStatId();
        int attackDamageId = getAttackDamageStatId();

        // 1. Penalización de maná (-30 MP)
        StaticModifier manaPenalty = new StaticModifier(
                Modifier.ModifierTarget.MAX,
                StaticModifier.CalculationType.ADDITIVE,
                -30.0f
        );
        stats.putModifier(manaId, modifierKey, manaPenalty);

        // 2. Velocidad (+25% — MULTIPLICATIVE)
        if (speedId != -1) {
            StaticModifier speedBonus = new StaticModifier(
                    Modifier.ModifierTarget.MAX,
                    StaticModifier.CalculationType.MULTIPLICATIVE,
                    0.25f
            );
            stats.putModifier(speedId, modifierKey, speedBonus);
        }

        // 3. Daño de ataque (+15% — MULTIPLICATIVE)
        if (attackDamageId != -1) {
            StaticModifier damageBonus = new StaticModifier(
                    Modifier.ModifierTarget.MAX,
                    StaticModifier.CalculationType.MULTIPLICATIVE,
                    0.15f
            );
            stats.putModifier(attackDamageId, modifierKey, damageBonus);
        }
    }

    @Override
    public void removeStats(EntityStatMap stats, String modifierKey) {
        int manaId         = DefaultEntityStatTypes.getMana();
        int speedId        = getSpeedStatId();
        int attackDamageId = getAttackDamageStatId();

        stats.removeModifier(manaId, modifierKey);
        if (speedId > 0)        stats.removeModifier(speedId, modifierKey);
        if (attackDamageId > 0) stats.removeModifier(attackDamageId, modifierKey);
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

    private int getAttackDamageStatId() {
        try {
            if (EntityStatType.getAssetMap() != null) {
                int idx = EntityStatType.getAssetMap().getIndex("AttackDamage");
                if (idx > 0) return idx;
                idx = EntityStatType.getAssetMap().getIndex("Damage");
                if (idx > 0) return idx;
                idx = EntityStatType.getAssetMap().getIndex("MeleeDamage");
                return (idx > 0) ? idx : -1;
            }
        } catch (Exception ignored) {}
        return -1;
    }
}
