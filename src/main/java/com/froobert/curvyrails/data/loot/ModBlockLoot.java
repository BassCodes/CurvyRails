package com.froobert.curvyrails.data.loot;

import com.froobert.curvyrails.blocks.ModBlocks;
import com.froobert.curvyrails.blocks.SmallCurveRail;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.predicates.AnyOfCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.Set;

public class ModBlockLoot extends BlockLootSubProvider {
    public ModBlockLoot(HolderLookup.Provider provider) {
        super(Set.of(), FeatureFlags.DEFAULT_FLAGS, provider);
    }

    @Override
    protected void generate() {
        add(ModBlocks.MEDIUM_CURVE_RAIL.get(), block -> createSingleItemTable(Items.RAIL));
        {
            // Small curve has three blocks despite being made of 3 rails. Should only drop 3 rails.
            var a = LootItemBlockStatePropertyCondition.hasBlockStateProperties(ModBlocks.SMALL_CURVE_RAIL.get()).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(SmallCurveRail.SECTION, SmallCurveRail.SmallCurveSection.Entry));
            var b = LootItemBlockStatePropertyCondition.hasBlockStateProperties(ModBlocks.SMALL_CURVE_RAIL.get()).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(SmallCurveRail.SECTION, SmallCurveRail.SmallCurveSection.Middle));
            var c = LootItemBlockStatePropertyCondition.hasBlockStateProperties(ModBlocks.SMALL_CURVE_RAIL.get()).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(SmallCurveRail.SECTION, SmallCurveRail.SmallCurveSection.Exit));

            add(ModBlocks.SMALL_CURVE_RAIL.get(), LootTable.lootTable()
                    .withPool(
                            LootPool.lootPool()
                                    .setRolls(ConstantValue.exactly(1))
                                    .add(LootItem.lootTableItem(Items.RAIL).when(AnyOfCondition.anyOf(a, b, c)))
                    ));
        }
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return ModBlocks.BLOCKS.getEntries().stream().map(DeferredHolder::get).map(Block.class::cast).toList();
    }
}
