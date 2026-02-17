package com.tuusuario.myracemod;

import com.hypixel.hytale.server.core.Message;
import com.tuusuario.myracemod.commands.RaceCommand;
import com.tuusuario.myracemod.components.RaceData;
import com.tuusuario.myracemod.races.ElfRace;
import com.tuusuario.myracemod.races.HumanRace;
import com.tuusuario.myracemod.races.EldritchRace;
import com.tuusuario.myracemod.races.RaceRegistry;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.command.system.AbstractCommand;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

public class MyRaceMod extends JavaPlugin {

    public MyRaceMod(JavaPluginInit init) {
        super(init);
    }

    @Override
    protected void start() {
        System.out.println("[MyRaceMod] Iniciando MyRaceMod");

        // 1. Registrar razas
        RaceRegistry.register(new HumanRace());
        RaceRegistry.register(new ElfRace());
        RaceRegistry.register(new EldritchRace());

        System.out.println("[MyRaceMod] " + RaceRegistry.all().size() + " razas registradas.");

        // 2. Registrar componente persistente RaceData
        ComponentType<EntityStore, RaceData> raceDataType =
                getEntityStoreRegistry().registerComponent(
                        RaceData.class,
                        "RaceData",
                        RaceData.CODEC
                );
        RaceManager.setRaceDataType(raceDataType);

        // 3. Registrar comandos
        getCommandRegistry().registerCommand((AbstractCommand) new RaceCommand());

        // 4. Evento: jugador entra al servidor
        getEventRegistry().registerGlobal(PlayerReadyEvent.class, this::onPlayerReady);

        System.out.println("[MyRaceMod] Listo. Usa /race list para empezar.");
    }

    private void onPlayerReady(PlayerReadyEvent event) {
        Player player = event.getPlayer();
        if (player == null) return;

        String raceId = RaceManager.getPlayerRace(player);

        if (raceId != null) {
            // Re-aplicar los stats de la raza guardada al entrar al servidor.
            // Sin esto, al intentar cambiar de raza el removeStats falla porque
            // los modificadores no existen en el EntityStatMap de esta sesión.
            RaceManager.reapplyStats(player, raceId);
            player.sendMessage(Message.raw("[MyRaceMod] Tu raza actual: " + raceId
                    + ". Usa /race info para verla."));
        } else {
            player.sendMessage(Message.raw("[MyRaceMod] Sin raza asignada. "
                    + "Usa /race list para elegir."));
        }
    }
}