package com.froobert.curvyrails.sound;

import com.froobert.curvyrails.CurvyRails;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModSoundEvents {

    private static final DeferredRegister<SoundEvent> deferredRegister =
            DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, CurvyRails.MODID);

    public static final DeferredHolder<SoundEvent, SoundEvent> CURVE_FORM_TINK =
            register("tink");



    public static void register(IEventBus modEventBus) {
        deferredRegister.register(modEventBus);
    }

    private static DeferredHolder<SoundEvent, SoundEvent> register(String name) {
        return deferredRegister.register(name,
                () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(CurvyRails.MODID, name)));
    }
}
