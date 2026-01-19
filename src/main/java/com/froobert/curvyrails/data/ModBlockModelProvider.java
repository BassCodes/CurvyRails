package com.froobert.curvyrails.data;

import com.froobert.curvyrails.blocks.SmallCurveRail;
import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;

import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;

import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredBlock;

import static com.froobert.curvyrails.blocks.ModBlocks.*;
import static com.froobert.curvyrails.CurvyRails.MODID;

public class ModBlockModelProvider extends BlockStateProvider {

    public ModBlockModelProvider(PackOutput packOutput, ExistingFileHelper fileHelper) {
        super(packOutput, MODID, fileHelper);
    }

    private <T extends Block> void createRailModel(String name, DeferredBlock<T> block) {
        getVariantBuilder(block.get())
                .forAllStatesExcept(state -> {
                    Direction facing = state.getValue(SmallCurveRail.FACING);

                    int yRot = switch (facing) {
                        case NORTH -> 0;
                        case EAST  -> 90;
                        case SOUTH -> 180;
                        case WEST  -> 270;
                        default -> 0;
                    };

                    SmallCurveRail.CurveSection section = state.getValue(SmallCurveRail.SECTION);
                    var modelName = name + "_" + section.getSerializedName();
                    var model = this.models()
                            .withExistingParent(modelName, mcLoc("block/rail_flat"))
                            .texture("rail", modLoc("block/"+ section.getSerializedName() + "_rail"))
                            .renderType("cutout");
                    return ConfiguredModel.builder()
                            .modelFile(model)
                            .rotationY(yRot)
                            .build();
                }, SmallCurveRail.SHAPE);


    }

    @Override
    protected void registerStatesAndModels() {
        createRailModel( "curve_rail", SMALL_CURVE_RAIL);
    }
}
