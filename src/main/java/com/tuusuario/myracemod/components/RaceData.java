package com.tuusuario.myracemod.components;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

public class RaceData implements Component<EntityStore> {

    private String selectedRace = null;

    public String getSelectedRace() {
        return selectedRace;
    }

    public void setSelectedRace(String raceId) {
        this.selectedRace = raceId;
    }

    public boolean hasRace() {
        return selectedRace != null && !selectedRace.isEmpty();
    }

    @Override
    public Component<EntityStore> clone() {
        RaceData copy = new RaceData();
        copy.selectedRace = this.selectedRace;
        return copy;
    }

    // Volvemos a la estructura original que tenías
    public static final BuilderCodec<RaceData> CODEC =
            BuilderCodec.builder(RaceData.class, RaceData::new)
                    .append(
                            new KeyedCodec<>("SelectedRace", (Codec<String>) Codec.STRING),
                            RaceData::setSelectedRace,
                            RaceData::getSelectedRace
                    )
                    .add()
                    .build();

    @Override
    public String toString() {
        return "RaceData{selectedRace='" + selectedRace + "'}";
    }
}