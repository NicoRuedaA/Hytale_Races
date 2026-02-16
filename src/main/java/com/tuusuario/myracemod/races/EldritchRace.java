package com.tuusuario.myracemod.races;

import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
import com.hypixel.hytale.server.core.modules.entitystats.asset.DefaultEntityStatTypes;
import com.hypixel.hytale.server.core.modules.entitystats.modifier.Modifier;
import com.hypixel.hytale.server.core.modules.entitystats.modifier.StaticModifier;

public class EldritchRace implements RaceDefinition {

    @Override
    public String id() {
        return "eldritch";
    }

    @Override
    public String displayName() {
        return "Eldritch";
    }

    @Override
    public String tagline() {
        return "Poder arcano prohibido a cambio de tu propia vitalidad.";
    }

    @Override
    public void applyStats(EntityStatMap stats, String modifierKey) {
        int healthId = DefaultEntityStatTypes.getHealth();
        int manaId = DefaultEntityStatTypes.getMana();

        // 1. PENALIZACIÓN DE VIDA (-20 HP máximo)
        StaticModifier healthPenalty = new StaticModifier(
                Modifier.ModifierTarget.MAX,
                StaticModifier.CalculationType.ADDITIVE,
                -20.0f
        );
        stats.putModifier(healthId, modifierKey, healthPenalty);
        // No llamar addStatValue aquí: stats.update() (en RaceManager) ajusta
        // automáticamente la vida actual si supera el nuevo máximo, sin doble descuento.

        // 2. BONIFICACIÓN DE MANÁ (+20 MP máximo)
        StaticModifier manaBonus = new StaticModifier(
                Modifier.ModifierTarget.MAX,
                StaticModifier.CalculationType.ADDITIVE,
                20.0f
        );
        stats.putModifier(manaId, modifierKey, manaBonus);
        // Mismo motivo: stats.update() ajusta el valor actual al nuevo máximo.
    }

    @Override
    public void removeStats(EntityStatMap stats, String modifierKey) {
        int healthId = DefaultEntityStatTypes.getHealth();
        int manaId = DefaultEntityStatTypes.getMana();

        // Eliminamos ambos modificadores (la penalización y el bono)
        stats.removeModifier(healthId, modifierKey);
        stats.removeModifier(manaId, modifierKey);
    }
}