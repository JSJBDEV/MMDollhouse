package ace.actually;

import ace.actually.blocks.ClueBlock;
import ace.actually.blocks.DecisionBlock;
import ace.actually.blocks.DollhouseBlock;
import ace.actually.blocks.SafeBlock;
import ace.actually.items.PolymerBlockItem;
import ace.actually.items.TestItem;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import net.fabricmc.api.ModInitializer;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Function;

public class MMDollhouse implements ModInitializer {
	public static final String MOD_ID = "mmdollhouse";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static final Identifier DATA = Identifier.of("mmdollhouse","data");

	public static final Block DOLLHOUSE = register("dollhouse", DollhouseBlock::new,AbstractBlock.Settings.create());
	public static final Block CLUE_BLOCK = register("clue",ClueBlock::new,AbstractBlock.Settings.create());
	public static final Block DECISION_BLOCK = register("decision", DecisionBlock::new,AbstractBlock.Settings.create());
	public static final Block SAFE_BLOCK = register("safe", SafeBlock::new,AbstractBlock.Settings.create());

	public static final Item TEST_ITEM = register("test", TestItem::new,new Item.Settings());

	public static final RegistryKey<World> HOUSES = RegistryKey.of(RegistryKeys.WORLD,Identifier.of("mmdollhouse","dollhouses"));

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		PolymerResourcePackUtils.addModAssets("mmdollhouse");
		LOGGER.info("Hello Fabric world!");
	}

	private static Block register(String path, Function<AbstractBlock.Settings, Block> factory, AbstractBlock.Settings settings) {
		final Identifier identifier = Identifier.of("mmdollhouse", path);
		final RegistryKey<Block> registryKey = RegistryKey.of(RegistryKeys.BLOCK, identifier);

		final Block block = Blocks.register(registryKey, factory, settings);
		Items.register(block, PolymerBlockItem::new);
		return block;
	}

	public static Item register(String path, Function<Item.Settings, Item> factory, Item.Settings settings) {
		final RegistryKey<Item> registryKey = RegistryKey.of(RegistryKeys.ITEM, Identifier.of("mmdollhouse", path));
		return Items.register(registryKey, factory, settings);
	}
}