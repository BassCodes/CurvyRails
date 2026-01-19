package com.froobert.curvyrails.sound;

import com.froobert.curvyrails.CurvyRails;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.common.data.SoundDefinitionsProvider;

public class ModSoundsProvider extends SoundDefinitionsProvider {
    public ModSoundsProvider(PackOutput output, ExistingFileHelper helper) {
        super(output, CurvyRails.MODID, helper);
    }

    @Override
    public void registerSounds() {
        this.add(ModSoundEvents.CURVE_FORM_TINK.get(),
                definition()
                        .with(sound(ResourceLocation.fromNamespaceAndPath(CurvyRails.MODID, "tink"))));
    }
}
