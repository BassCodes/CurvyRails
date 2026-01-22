package com.froobert.curvyrails.data;

import com.froobert.curvyrails.blocks.MediumCurveRail;
import com.froobert.curvyrails.blocks.SmallCurveRail;
import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredBlock;

import java.util.function.Predicate;

import static com.froobert.curvyrails.CurvyRails.MODID;
import static com.froobert.curvyrails.blocks.CurveRailProperties.FACING;
import static com.froobert.curvyrails.blocks.ModBlocks.MEDIUM_CURVE_RAIL;
import static com.froobert.curvyrails.blocks.ModBlocks.SMALL_CURVE_RAIL;

public class ModBlockModelProvider extends BlockStateProvider {

    public ModBlockModelProvider(PackOutput packOutput, ExistingFileHelper fileHelper) {
        super(packOutput, MODID, fileHelper);
    }

    private <T extends Block> void createRailModelMedium(DeferredBlock<T> block, String modelName, String textureName, Predicate<BlockState> validState, Vec3 from, Vec3 to) {
        var air = models().withExistingParent("empty_rail", mcLoc("block/air")).texture("particle", mcLoc("block/rail"));

        // There truly must be a better way than this
        getVariantBuilder(block.get())
                .forAllStates(state -> {
                    if (validState.test(state)) {
                        var model = this.models()
                                .getBuilder("block/curves/" + modelName)
                                .texture("rail", modLoc("block/curves/" + textureName))
                                .texture("particle", modLoc("block/curves/" + textureName))
                                .renderType("cutout")
                                .element()
                                .from((float) from.x, (float) from.y, (float) from.z)
                                .to((float) to.x, (float) to.y, (float) to.z)
                                .face(Direction.UP)
                                .uvs(0, 0, 16, 16)
                                .texture("#rail")
                                .end()
                                .end();

                        Direction facing = state.getValue(FACING);
                        int yRot = switch (facing) {
                            case NORTH -> 0;
                            case EAST -> 90;
                            case SOUTH -> 180;
                            case WEST -> 270;
                            default -> 0;
                        };

                        return ConfiguredModel.builder().modelFile(model).rotationY(yRot).build();
                    } else {
                        return ConfiguredModel.builder().modelFile(air).build();
                    }
                });
    }

    @Override
    protected void registerStatesAndModels() {
        createRailModelMedium(MEDIUM_CURVE_RAIL, "medium_curve_rail", "medium_curve",
                state -> state.getValue(MediumCurveRail.SECTION) == MediumCurveRail.MediumCurveSection.Middle,
                new Vec3(-16.0, 0.0, -16.0),
                new Vec3(32.0, 1.0, 32.0)
        );
        createRailModelMedium(SMALL_CURVE_RAIL, "small_curve_rail", "small_curve",
                state -> state.getValue(SmallCurveRail.SECTION) == SmallCurveRail.SmallCurveSection.Middle,
                new Vec3(0.0, 0.0, 0.0),
                new Vec3(32.0, 1.0, 32.0)
        );
    }
}
