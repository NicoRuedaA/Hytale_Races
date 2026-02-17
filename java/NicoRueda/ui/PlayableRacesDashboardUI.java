package NicoRueda.ui;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType;
import com.hypixel.hytale.protocol.packets.interface_.NotificationStyle;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.pages.InteractiveCustomUIPage;
import com.hypixel.hytale.server.core.ui.builder.EventData;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.util.NotificationUtil;

import NicoRueda.RaceManager;
import NicoRueda.races.RaceDefinition;
import NicoRueda.races.RaceRegistry;

import javax.annotation.Nonnull;

public class PlayableRacesDashboardUI extends InteractiveCustomUIPage<PlayableRacesDashboardUI.UIEventData> {

    public static final String LAYOUT = "playableraces/Dashboard.ui";

    // Una constante de acción por raza
    private static final String ACTION_HUMAN     = "select_human";
    private static final String ACTION_ELDRITCH  = "select_eldritch";
    private static final String ACTION_AMPHIBIAN = "select_amphibian";
    private static final String ACTION_BEAST     = "select_beast";
    private static final String ACTION_MONSTER   = "select_monster";
    private static final String ACTION_CLOSE     = "close";

    private final PlayerRef playerRef;

    public PlayableRacesDashboardUI(@Nonnull PlayerRef playerRef) {
        super(playerRef, CustomPageLifetime.CanDismiss, UIEventData.CODEC);
        this.playerRef = playerRef;
    }

    @Override
    public void build(
            @Nonnull Ref<EntityStore> ref,
            @Nonnull UICommandBuilder cmd,
            @Nonnull UIEventBuilder evt,
            @Nonnull Store<EntityStore> store
    ) {
        cmd.append(LAYOUT);

        Player player = store.getComponent(ref, Player.getComponentType());
        cmd.set("#StatusText.Text", buildStatusText(player));

        evt.addEventBinding(CustomUIEventBindingType.Activating, "#HumanButton",
                new EventData().append("Action", ACTION_HUMAN), false);
        evt.addEventBinding(CustomUIEventBindingType.Activating, "#EldritchButton",
                new EventData().append("Action", ACTION_ELDRITCH), false);
        evt.addEventBinding(CustomUIEventBindingType.Activating, "#AmphibianButton",
                new EventData().append("Action", ACTION_AMPHIBIAN), false);
        evt.addEventBinding(CustomUIEventBindingType.Activating, "#BeastButton",
                new EventData().append("Action", ACTION_BEAST), false);
        evt.addEventBinding(CustomUIEventBindingType.Activating, "#MonsterButton",
                new EventData().append("Action", ACTION_MONSTER), false);
        evt.addEventBinding(CustomUIEventBindingType.Activating, "#UndeadButton",
                new EventData().append("Action", ACTION_MONSTER), false);
        evt.addEventBinding(CustomUIEventBindingType.Activating, "#CloseButton",
                new EventData().append("Action", ACTION_CLOSE), false);
    }

    @Override
    public void handleDataEvent(
            @Nonnull Ref<EntityStore> ref,
            @Nonnull Store<EntityStore> store,
            @Nonnull UIEventData data
    ) {
        if (data.action == null) return;

        switch (data.action) {
            case ACTION_HUMAN:     handleRaceSelection(ref, store, "human");     break;
            case ACTION_ELDRITCH:  handleRaceSelection(ref, store, "eldritch");  break;
            case ACTION_AMPHIBIAN: handleRaceSelection(ref, store, "amphibian"); break;
            case ACTION_BEAST:     handleRaceSelection(ref, store, "beast");     break;
            case ACTION_MONSTER:   handleRaceSelection(ref, store, "monster");   break;
            case ACTION_CLOSE:     this.close();                                 break;
        }
    }

    private void handleRaceSelection(
            @Nonnull Ref<EntityStore> ref,
            @Nonnull Store<EntityStore> store,
            @Nonnull String raceId
    ) {
        if (!RaceRegistry.exists(raceId)) {
            sendStatusUpdate("Error: raza desconocida '" + raceId + "'.");
            return;
        }

        Player player = store.getComponent(ref, Player.getComponentType());
        if (player == null) {
            sendStatusUpdate("Error: no se pudo obtener el jugador.");
            return;
        }

        String currentRace = RaceManager.getPlayerRace(player);
        if (raceId.equals(currentRace)) {
            sendStatusUpdate("Ya eres " + RaceRegistry.get(raceId).displayName() + ".");
            return;
        }

        RaceManager.selectRace(player, raceId);

        RaceDefinition race = RaceRegistry.get(raceId);
        sendStatusUpdate("Raza cambiada a: " + race.displayName());

        NotificationUtil.sendNotification(
            playerRef.getPacketHandler(),
            Message.raw("PlayableRaces"),
            Message.raw("¡Ahora eres " + race.displayName() + "!"),
            NotificationStyle.Success
        );
    }

    private void sendStatusUpdate(String text) {
        UICommandBuilder cmd = new UICommandBuilder();
        cmd.set("#StatusText.Text", text);
        this.sendUpdate(cmd, false);
    }

    private String buildStatusText(Player player) {
        if (player == null) return "Sin información de jugador.";
        String raceId = RaceManager.getPlayerRace(player);
        if (raceId == null) return "Sin raza asignada. ¡Elige una!";
        RaceDefinition race = RaceRegistry.get(raceId);
        if (race == null) return "Raza actual: " + raceId;
        return "Raza actual: " + race.displayName() + " — " + race.tagline();
    }

    public static class UIEventData {
        public static final BuilderCodec<UIEventData> CODEC = BuilderCodec.builder(
                UIEventData.class, UIEventData::new)
            .append(new KeyedCodec<>("Action", (Codec<String>) Codec.STRING),
                    (e, v) -> e.action = v, e -> e.action)
            .add()
            .build();

        private String action;
        public UIEventData() {}
    }
}
