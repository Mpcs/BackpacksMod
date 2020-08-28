package io.github.mpcs;

import io.github.mpcs.container.BackpackContainer;
import io.github.mpcs.container.BackpackScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.render.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.screen.ScreenProviderRegistry;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.DyeableItem;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

public class BackpacksModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ColorProviderRegistry.ITEM.register((itemStack, i) -> i > 0 ? -1 : ((DyeableItem)itemStack.getItem()).getColor(itemStack),
                BackpacksMod.SMALL_BACKPACK,
                BackpacksMod.MEDIUM_BACKPACK,
                BackpacksMod.BIG_BACKPACK);
        //ScreenRegistry.register(BackpacksMod.BACKPACK_CONTAINTER,(syncId, identifier, player, buf) -> new BackpackScreen(syncId, player, buf));
        //ScreenProviderRegistry.INSTANCE.<BackpackContainer>registerFactory(BackpacksMod.BACKPACK_CONTAINTER, (container -> new BackpackScreen(container, MinecraftClient.getInstance().player.inventory, new TranslatableText("container.mbackpacks.backpack"))));
        ScreenProviderRegistry.INSTANCE.<BackpackContainer>registerFactory(BackpacksMod.BACKPACK_CONTAINTER,(syncId, identifier, player, buf) -> new BackpackScreen(syncId, player, buf));
    }
}
