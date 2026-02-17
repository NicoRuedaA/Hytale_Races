package NicoRueda.listeners;

import NicoRueda.RaceManager;
import com.hypixel.hytale.assetstore.map.IndexedLookupTableAssetMap;
import com.hypixel.hytale.event.EventRegistry;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.asset.type.entityeffect.config.EntityEffect;
import com.hypixel.hytale.server.core.asset.type.entityeffect.config.OverlapBehavior;
import com.hypixel.hytale.server.core.entity.effect.ActiveEntityEffect;
import com.hypixel.hytale.server.core.entity.effect.EffectControllerComponent;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.event.events.entity.EntityEffectAppliedEvent;
import com.hypixel.hytale.server.core.Message;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;

/**
 * Escucha EntityEffectAppliedEvent para detectar cuando se aplica veneno a un Undead
 * e inyecta inmediatamente una curación con duración x2.
 *
 * Resultado: el veneno hace Y daño, la cura devuelve 2Y → neto +Y HP.
 *
 * Usa reflection para leer ActiveEntityEffect.remainingDuration,
 * igual que hace el mod BuffStacks de referencia.
 */
public class UndeadEffectSystem {

    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();

    // IDs de debuffs que deben curar al Undead en vez de dañarle
    private static final Set<String> POISON_IDS = new HashSet<>(Arrays.asList(
            "Poison_T1", "Poison_T2", "Poison_T3"
    ));

    // IDs de buffs que deben dañar al Undead en vez de curarle
    private static final Set<String> REGEN_IDS = new HashSet<>(Arrays.asList(
            "HealthRegen_Buff_T1", "HealthRegen_Buff_T2", "HealthRegen_Buff_T3",
            "Potion_Health_Lesser_Regen", "Potion_Health_Greater_Regen",
            "Potion_Signature_Lesser_Regen", "Potion_Signature_Greater_Regen",
            "Food_Health_Regen_Tiny", "Food_Health_Regen_Small",
            "Food_Health_Regen_Medium", "Food_Health_Regen_Large"
    ));

    // Efectos que inyectamos nosotros → ignorar para no crear bucle infinito
    private static final Set<String> INJECTED_IDS = new HashSet<>(Arrays.asList(
            "HealthRegen_Buff_T1", "Poison_T1"
    ));

    // Reflection sobre ActiveEntityEffect.remainingDuration (técnica de BuffStacks)
    private Field remainingDurationField;

    // Cache de índices de efectos en el AssetMap
    private int          regenEffectIndex  = -1;
    private int          poisonEffectIndex = -1;
    private EntityEffect regenEffect       = null;
    private EntityEffect poisonEffect      = null;
    private boolean      effectsCached     = false;

    public UndeadEffectSystem() {
        initReflection();
    }

    // ── Registro ──────────────────────────────────────────────────────────

    public void register(EventRegistry eventBus) {
        try {
            eventBus.register(EntityEffectAppliedEvent.class, this::onEffectApplied);
            LOGGER.at(Level.INFO).log("[PlayableRaces] UndeadEffectSystem registrado.");
        } catch (Exception e) {
            LOGGER.at(Level.WARNING).withCause(e)
                    .log("[PlayableRaces] Error registrando UndeadEffectSystem.");
        }
    }

    // ── Evento ────────────────────────────────────────────────────────────

    private void onEffectApplied(EntityEffectAppliedEvent event) {
        if (!effectsCached) cacheEffects();

        try {
            // 1. Obtener el efecto aplicado
            String effectId = event.getEffectId();
            if (effectId == null || INJECTED_IDS.contains(effectId)) return;

            // 2. Solo actuar si es veneno/regen conocidos o debuffs genéricos
            boolean isPoison = POISON_IDS.contains(effectId);
            boolean isRegen  = REGEN_IDS.contains(effectId);
            if (!isPoison && !isRegen) return;

            // 3. Obtener el jugador afectado
            Player player = event.getPlayer();
            if (player == null) return;

            // 4. Comprobar que es raza Undead
            String race = RaceManager.getPlayerRace(player);
            if (!"undead".equalsIgnoreCase(race)) return;

            // 5. Obtener EffectControllerComponent
            var ref   = player.getReference();
            if (ref == null || !ref.isValid()) return;
            var store = ref.getStore();

            EffectControllerComponent effectController =
                    store.getComponent(ref, EffectControllerComponent.getComponentType());
            if (effectController == null) return;

            // 6. Leer duración restante del efecto recién aplicado
            @SuppressWarnings("unchecked")
            Int2ObjectMap<ActiveEntityEffect> activeEffects =
                    (Int2ObjectMap<ActiveEntityEffect>) effectController.getActiveEffects();
            if (activeEffects == null) return;

            IndexedLookupTableAssetMap<String, EntityEffect> assetMap = EntityEffect.getAssetMap();
            float remaining = 0f;

            for (Int2ObjectMap.Entry<ActiveEntityEffect> entry : activeEffects.int2ObjectEntrySet()) {
                EntityEffect asset = assetMap.getAsset(entry.getIntKey());
                if (asset != null && effectId.equals(asset.getId())) {
                    remaining = getRemainingDuration(entry.getValue());
                    break;
                }
            }

            if (remaining <= 0f) return;

            // 7. Inyectar el efecto inverso
            if (isPoison) {
                injectRegen(ref, store, effectController, remaining, effectId, player);
            } else {
                injectPoison(ref, store, effectController, remaining);
            }

        } catch (Exception e) {
            LOGGER.at(Level.WARNING).withCause(e)
                    .log("[PlayableRaces] Error en UndeadEffectSystem.onEffectApplied");
        }
    }

    // ── Inyección ─────────────────────────────────────────────────────────

    private void injectRegen(
            com.hypixel.hytale.component.Ref<com.hypixel.hytale.server.core.universe.world.storage.EntityStore> ref,
            com.hypixel.hytale.component.Store<com.hypixel.hytale.server.core.universe.world.storage.EntityStore> store,
            EffectControllerComponent controller,
            float duration, String sourceEffectId, Player player) {

        if (regenEffectIndex == -1 || regenEffect == null) return;
        try {
            // Duración x2: veneno hace Y daño, cura devuelve 2Y → neto +Y HP
            float healDuration = duration * 2f;
            controller.addEffect(ref, regenEffectIndex, regenEffect,
                    healDuration, OverlapBehavior.EXTEND, store.getAccessor());

            // Debug en chat
            player.sendMessage(Message.raw(
                    "§a[Undead DEBUG] §fCurando por §c" + sourceEffectId
                    + " §f| Restante: §e" + String.format("%.1f", duration) + "s"
                    + " §f| Curación inyectada: §a" + String.format("%.1f", healDuration) + "s"
            ));

        } catch (Exception e) {
            LOGGER.at(Level.WARNING).withCause(e)
                    .log("[PlayableRaces] Error inyectando curación Undead.");
        }
    }

    private void injectPoison(
            com.hypixel.hytale.component.Ref<com.hypixel.hytale.server.core.universe.world.storage.EntityStore> ref,
            com.hypixel.hytale.component.Store<com.hypixel.hytale.server.core.universe.world.storage.EntityStore> store,
            EffectControllerComponent controller,
            float duration) {

        if (poisonEffectIndex == -1 || poisonEffect == null) return;
        try {
            controller.addEffect(ref, poisonEffectIndex, poisonEffect,
                    duration, OverlapBehavior.EXTEND, store.getAccessor());
        } catch (Exception e) {
            LOGGER.at(Level.WARNING).withCause(e)
                    .log("[PlayableRaces] Error inyectando veneno Undead.");
        }
    }

    // ── Inicialización ────────────────────────────────────────────────────

    private void initReflection() {
        try {
            remainingDurationField = ActiveEntityEffect.class
                    .getDeclaredField("remainingDuration");
            remainingDurationField.setAccessible(true);
            LOGGER.at(Level.INFO).log("[PlayableRaces] Reflection OK: ActiveEntityEffect.remainingDuration");
        } catch (Exception e) {
            LOGGER.at(Level.WARNING).withCause(e)
                    .log("[PlayableRaces] Reflection fallida en ActiveEntityEffect.");
        }
    }

    private void cacheEffects() {
        try {
            IndexedLookupTableAssetMap<String, EntityEffect> assetMap = EntityEffect.getAssetMap();
            regenEffectIndex  = assetMap.getIndexOrDefault("HealthRegen_Buff_T1", -1);
            poisonEffectIndex = assetMap.getIndexOrDefault("Poison_T1", -1);
            if (regenEffectIndex  != -1) regenEffect  = assetMap.getAsset(regenEffectIndex);
            if (poisonEffectIndex != -1) poisonEffect = assetMap.getAsset(poisonEffectIndex);
            LOGGER.at(Level.INFO).log("[PlayableRaces] Efectos cacheados: HealthRegen_Buff_T1@%d Poison_T1@%d",
                    regenEffectIndex, poisonEffectIndex);
        } catch (Exception e) {
            LOGGER.at(Level.WARNING).withCause(e)
                    .log("[PlayableRaces] Error cacheando efectos Undead.");
        }
        effectsCached = true;
    }

    // ── Utilidades ────────────────────────────────────────────────────────

    private float getRemainingDuration(ActiveEntityEffect effect) {
        if (remainingDurationField == null || effect == null) return 0f;
        try {
            return remainingDurationField.getFloat(effect);
        } catch (Exception e) {
            return 0f;
        }
    }
}
