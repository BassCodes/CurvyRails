package com.froobert.curvyrails;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.VersionChecker;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.loading.moddiscovery.ModFileInfo;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

// This class will not load on dedicated servers. Accessing client side code from here is safe.
@Mod(value = CurvyRails.MODID, dist = Dist.CLIENT)
// You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
@EventBusSubscriber(modid = CurvyRails.MODID, value = Dist.CLIENT)
public class CurvyRailsClient {
    public CurvyRailsClient(ModContainer container) {
        // Allows NeoForge to create a config screen for this mod's configs.
        // The config screen is accessed by going to the Mods screen > clicking on your mod > clicking on config.
        // Do not forget to add translations for your config options to the en_us.json file.
//        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }

    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
        // Some client setup code
    }

    @SubscribeEvent
    static void handleClientLoggedIn(ClientPlayerNetworkEvent.LoggingIn event) {
        var modInfo = ModList.get().getModFileById(CurvyRails.MODID).getMods().getFirst();
        var result = VersionChecker.getResult(modInfo);

        var discordUrl = "https://discord.gg/EubEGfnFgU";
        var issueUrl = ((ModFileInfo) (modInfo.getOwningFile())).getIssueURL().toString();

        var message = CommonComponents.joinLines(
                Component.literal("Curvy Rails")
                        .withStyle(ChatFormatting.GREEN)
                        .append(Component.literal(" is currently in early Alpha.")
                        .withStyle(ChatFormatting.GRAY)),
                Component.literal("Expect bugs and breaking changes between versions")
                        .withStyle(ChatFormatting.GRAY),
                Component.literal("Report any bugs on ")
                        .withStyle(ChatFormatting.GRAY)
                        .append(
                        Component.literal("GitHub")
                        .withStyle(style -> style
                                .withColor(ChatFormatting.WHITE)
                                .withUnderlined(true)
                                .withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, issueUrl)))
                ).append(Component.literal(" or ")).append(
                        Component.literal("Discord").withStyle(
                            style -> style
                                    .withColor(ChatFormatting.BLUE)
                                    .withUnderlined(true)
                                    .withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, discordUrl))
                        )
                ),
                Component.literal(""));
            event.getPlayer().displayClientMessage(message, false);
    }
}
