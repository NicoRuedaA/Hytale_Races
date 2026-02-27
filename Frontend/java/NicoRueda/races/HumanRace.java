package NicoRueda.races;

import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;

public class HumanRace implements RaceDefinition {

    @Override
    public String id() { return "human"; }

    @Override
    public String displayName() { return "Humano"; }

    @Override
    public String tagline() { return "El habitante estándar. Sin bonificaciones ni penalizaciones."; }

    @Override
    public void applyStats(EntityStatMap stats, String modifierKey) {
        // El humano es la raza base — sin modificadores.
    }

    @Override
    public void removeStats(EntityStatMap stats, String modifierKey) {
        // Nada que limpiar.
    }
}
