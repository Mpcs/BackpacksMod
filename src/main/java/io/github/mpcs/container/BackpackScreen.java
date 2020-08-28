package io.github.mpcs.container;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.network.PacketByteBuf;

public class BackpackScreen extends HandledScreen<BackpackContainer> {
    private Identifier TEXTURE;
    private Identifier SLOT_TEXTURE = new Identifier("mbackpacks", "textures/gui/slot.png");
    private int slots;
    private String name;
    public BackpackScreen(int syncId, PlayerEntity player, PacketByteBuf buf) {
        super(new BackpackContainer(syncId, player.inventory, buf), player.inventory, new TranslatableText("container.mbackpacks.backpack"));
        name = buf.readString();
        slots = buf.readInt();
        TEXTURE = new Identifier("mbackpacks", "textures/gui/backpack" + ((slots/9) + (slots % 9 == 0 ? 0 : 1)) + ".png");
        if((slots/9) == 4)
            this.backgroundWidth = 184;
        else if((slots/9) > 4) {
            this.backgroundHeight = 222;
        }
    }

    public BackpackScreen(BackpackContainer syncId, Text player, PacketByteBuf buf) {
        super(syncId, syncId.getInventory(), new TranslatableText("container.mbackpacks.backpack"));
        name = buf.readString();
        slots = buf.readInt();
        TEXTURE = new Identifier("mbackpacks", "textures/gui/backpack" + ((slots/9) + (slots % 9 == 0 ? 0 : 1)) + ".png");
        if((slots/9) == 4)
            this.backgroundWidth = 184;
        else if((slots/9) > 4) {
            this.backgroundHeight = 222;
        }
    }

    @Override
    protected void init() {
        super.init();
        this.x = (this.width - this.backgroundWidth) / 2;
    }

    @Override
    protected void drawBackground(MatrixStack m, float var1, int var2, int var3) {
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.client.getTextureManager().bindTexture(TEXTURE);
        int guiX = this.x;
        int guiY = (this.height - this.backgroundHeight) / 2;
        this.drawTexture(m, guiX, guiY, 0, 0, this.backgroundWidth, this.backgroundHeight);// Guessing "blit" was "drawTexture"

        drawSlots(m, guiX, guiY);
    }

    private void drawSlots(MatrixStack m, int guiX, int guiY) {
        this.client.getTextureManager().bindTexture(SLOT_TEXTURE);
        for(int y = 0; y < (slots/9); y++)
            for(int x = 0; x < 9; x++) {
                this.drawTexture(m, guiX + 7 + (x * 18), guiY + 17 + (y * 18), 0, 0, 18, 18);
            }

        if((slots % 9) != 0)
            for(int x = 0; x < (slots % 9); x++) {
                this.drawTexture(m, guiX + 7 + (x * 18), guiY + 17 + (slots/9 * 18), 0, 0, 18, 18);
            }
    }

    //@Override
    //protected void drawForeground(MatrixStack m,int int_1, int int_2) {
    //    this.font.draw(name, 8.0F, 6.0F, 4210752);
    //}

    @Override
    protected void drawForeground(MatrixStack matrixStack, int i, int j)
    {
        this.textRenderer.draw(matrixStack, this.title, 8.0F, 6.0F, 4210752);
        this.textRenderer.draw(matrixStack, this.playerInventory.getDisplayName(), 8.0F, (float)(this.backgroundHeight - 96 + 2), 4210752);
    }

    @Override
    public void render(MatrixStack m, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(m);
        super.render(m, mouseX, mouseY, partialTicks);
        this.drawMouseoverTooltip(m, mouseX, mouseY);
    }
}
