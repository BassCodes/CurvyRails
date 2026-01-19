package com.froobert.curvyrails.blocks;

import com.froobert.curvyrails.CurvyRails;
import com.froobert.curvyrails.ModItems;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModBlocks {

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(CurvyRails.MODID);

    private static final BlockBehaviour.Properties RAIL_BEHAVIOR = BlockBehaviour.Properties.ofFullCopy(Blocks.RAIL);

    public static final DeferredBlock<SmallCurveRail> SMALL_CURVE_RAIL = BLOCKS.register("small_curve_rail", () -> new SmallCurveRail(RAIL_BEHAVIOR));

    private static <T extends Block> DeferredBlock<T>  registerBlock(String name, Supplier<T> block) {
        DeferredBlock<T> result = BLOCKS.register(name, block);
        registerBlockItem(name,result);
        return result;
    }

    private static <T extends Block> void registerBlockItem(String name, DeferredBlock<T> block) {
        ModItems.ITEMS.register(name, ( ) -> new BlockItem(block.get(), new Item.Properties()));
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }

}
