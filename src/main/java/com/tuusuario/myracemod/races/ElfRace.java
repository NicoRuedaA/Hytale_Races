package com.tuusuario.myracemod.races;

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
        int manaId = DefaultEntityStatTypes.getMana();
        int speedId = getSpeedStatId();

        // 1. Maná (+50)
        StaticModifier manaBonus = new StaticModifier(
                Modifier.ModifierTarget.MAX,
                StaticModifier.CalculationType.ADDITIVE,
                50.0f
        );
        stats.putModifier(manaId, modifierKey, manaBonus);
        stats.addStatValue(manaId, 50.0f);

        // 2. Velocidad (+20% — MULTIPLICATIVE, no ADDITIVE)
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
        int manaId = DefaultEntityStatTypes.getMana();
        int speedId = getSpeedStatId();

        stats.removeModifier(manaId, modifierKey);
        // Protección extra: speedId puede ser MIN_VALUE si getIndex() no encuentra el stat
        if (speedId > 0) {
            stats.removeModifier(speedId, modifierKey);
        }
    }

    // Método auxiliar privado para buscar el ID de velocidad de forma segura
    private int getSpeedStatId() {
        try {
            if (EntityStatType.getAssetMap() != null) {
                int idx = EntityStatType.getAssetMap().getIndex("MovementSpeed");
                // getIndex puede devolver Integer.MIN_VALUE si no existe — lo rechazamos
                return (idx > 0) ? idx : -1;
            }
        } catch (Exception ignored) {}
        return -1;
    }
}