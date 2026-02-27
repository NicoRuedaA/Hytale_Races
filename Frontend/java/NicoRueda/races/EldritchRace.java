package NicoRueda.races;

import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
import com.hypixel.hytale.server.core.modules.entitystats.asset.DefaultEntityStatTypes;
import com.hypixel.hytale.server.core.modules.entitystats.modifier.Modifier;
import com.hypixel.hytale.server.core.modules.entitystats.modifier.StaticModifier;

public class EldritchRace implements RaceDefinition {

    @Override
    public String id() { return "eldritch"; }

    @Override
    public String displayName() { return "Eldritch"; }

    @Override
    public String tagline() { return "Poder arcano prohibido a cambio de tu propia vitalidad."; }

    @Override
    public void applyStats(EntityStatMap stats, String modifierKey) {
        int healthId = DefaultEntityStatTypes.getHealth();
        int manaId   = DefaultEntityStatTypes.getMana();

        // 1. Penalización de vida (-20 HP máximo)
        StaticModifier healthPenalty = new StaticModifier(
                Modifier.ModifierTarget.MAX,
                StaticModifier.CalculationType.ADDITIVE,
                -20.0f
        );
        stats.putModifier(healthId, modifierKey, healthPenalty);

        // 2. Bonificación de maná (+20 MP máximo)
        StaticModifier manaBonus = new StaticModifier(
                Modifier.ModifierTarget.MAX,
                StaticModifier.CalculationType.ADDITIVE,
                20.0f
        );
        stats.putModifier(manaId, modifierKey, manaBonus);
        // stats.update() en RaceManager ajusta los valores actuales al nuevo máximo.
    }

    @Override
    public void removeStats(EntityStatMap stats, String modifierKey) {
        int healthId = DefaultEntityStatTypes.getHealth();
        int manaId   = DefaultEntityStatTypes.getMana();

        stats.removeModifier(healthId, modifierKey);
        stats.removeModifier(manaId, modifierKey);
    }
}
