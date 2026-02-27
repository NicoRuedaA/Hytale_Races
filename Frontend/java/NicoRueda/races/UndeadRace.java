package NicoRueda.races;

import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
import com.hypixel.hytale.server.core.modules.entitystats.asset.DefaultEntityStatTypes;
import com.hypixel.hytale.server.core.modules.entitystats.modifier.Modifier;
import com.hypixel.hytale.server.core.modules.entitystats.modifier.StaticModifier;

public class UndeadRace implements RaceDefinition {

    @Override
    public String id() {
        return "undead";
    }

    @Override
    public String displayName() {
        return "No-muerto";
    }

    @Override
    public String tagline() {
        return "Entre la vida y la muerte. Resistente a la necrosis, débil a la magia.";
    }

    @Override
    public void applyStats(EntityStatMap stats, String modifierKey) {
        int healthId = DefaultEntityStatTypes.getHealth();
        int manaId   = DefaultEntityStatTypes.getMana();

        // +10 HP máximo: los no-muertos son físicamente resistentes
        stats.putModifier(healthId, modifierKey, new StaticModifier(
                Modifier.ModifierTarget.MAX,
                StaticModifier.CalculationType.ADDITIVE,
                10.0f
        ));

        // -10 MP máximo: la energía arcana no fluye bien en cuerpos no-muertos
        stats.putModifier(manaId, modifierKey, new StaticModifier(
                Modifier.ModifierTarget.MAX,
                StaticModifier.CalculationType.ADDITIVE,
                -10.0f
        ));
    }

    @Override
    public void removeStats(EntityStatMap stats, String modifierKey) {
        int healthId = DefaultEntityStatTypes.getHealth();
        int manaId   = DefaultEntityStatTypes.getMana();

        stats.removeModifier(healthId, modifierKey);
        stats.removeModifier(manaId, modifierKey);
    }
}
