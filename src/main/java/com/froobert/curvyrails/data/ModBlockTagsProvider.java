package com.froobert.curvyrails.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

import static com.froobert.curvyrails.CurvyRails.MODID;
import static com.froobert.curvyrails.blocks.ModBlocks.MEDIUM_CURVE_RAIL;
import static com.froobert.curvyrails.blocks.ModBlocks.SMALL_CURVE_RAIL;

public class ModBlockTagsProvider extends BlockTagsProvider {

    public ModBlockTagsProvider(PackOutput packOutput,
                                CompletableFuture<HolderLookup.Provider> lookupProvider,
                                ExistingFileHelper fileHelper) {
        super(packOutput, lookupProvider, MODID, fileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {

        this.tag(BlockTags.RAILS).add(SMALL_CURVE_RAIL.get()).add(MEDIUM_CURVE_RAIL.get());
    }


}
