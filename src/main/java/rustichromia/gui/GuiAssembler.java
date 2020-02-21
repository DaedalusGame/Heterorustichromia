package rustichromia.gui;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.input.Mouse;
import rustichromia.Rustichromia;
import rustichromia.tile.TileEntityAssembler;

import java.io.IOException;
import java.util.List;

public class GuiAssembler extends GuiContainer {
    /** Amount scrolled in Creative mode inventory (0 = top, 1 = bottom) */
    private float currentScroll;
    /** True if the scrollbar is being dragged */
    private boolean isScrolling;
    /** True if the left mouse button was held down last time drawScreen was called. */
    private boolean wasClicking;

    private static final ResourceLocation guiLocation = new ResourceLocation(Rustichromia.MODID,"textures/gui/assembler_recipe.png");

    public GuiAssembler(EntityPlayer player, TileEntityAssembler assembler) {
        super(new ContainerAssembler(player,assembler));
        this.ySize = 165;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(guiLocation);
        int xPos = (this.width - this.xSize) / 2;
        int yPos = (this.height - this.ySize) / 2;
        drawTexturedModalRect(xPos, yPos, 0, 0, this.xSize, this.ySize);
        int i = xPos + 156;
        int j = yPos + 8;
        int k = yPos + 77;
        this.drawTexturedModalRect(i, j + (int)((float)(k - j - 14) * this.currentScroll), 176 + (this.needsScrollBars() ? 0 : 12), 0, 12, 15);

        for (Slot slot : inventorySlots.inventorySlots) {
            if(slot instanceof ContainerAssembler.SlotAssemblerRecipe && ((ContainerAssembler.SlotAssemblerRecipe) slot).isSelected())
                this.drawTexturedModalRect(xPos + slot.xPos, yPos + slot.yPos, 176, 15, 16, 16);
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        boolean leftClick = Mouse.isButtonDown(0);
        int xPos = (this.width - this.xSize) / 2;
        int yPos = (this.height - this.ySize) / 2;
        int k = xPos + 156;
        int l = yPos + 8;
        int i1 = xPos + 167;
        int j1 = yPos + 77;

        if (!this.wasClicking && leftClick && mouseX >= k && mouseY >= l && mouseX < i1 && mouseY < j1)
        {
            this.isScrolling = this.needsScrollBars();
        }

        if (!leftClick)
        {
            this.isScrolling = false;
        }

        this.wasClicking = leftClick;

        if (this.isScrolling)
        {
            this.currentScroll = ((float)(mouseY - l) - 7.5F) / ((float)(j1 - l) - 15.0F);
            this.currentScroll = MathHelper.clamp(this.currentScroll, 0.0F, 1.0F);
            ((ContainerAssembler)this.inventorySlots).scrollTo(this.currentScroll);
        }
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }

    @Override
    protected void renderHoveredToolTip(int mouseX, int mouseY) {
        Slot slot = getSlotUnderMouse();
        if(slot instanceof ContainerAssembler.SlotAssemblerRecipe) {
            List<String> tooltip = ((ContainerAssembler.SlotAssemblerRecipe) slot).getTooltip();
            FontRenderer font = fontRenderer;
            this.drawHoveringText(tooltip, mouseX, mouseY, font);
        } else {
            super.renderHoveredToolTip(mouseX, mouseY);
        }
    }

    @Override
    public void handleMouseInput() throws IOException
    {
        super.handleMouseInput();
        int mouseWheel = Mouse.getEventDWheel();

        if (mouseWheel != 0 && this.needsScrollBars())
        {
            int j = (((ContainerAssembler)this.inventorySlots).searchRecipes.size()) / ContainerAssembler.COLUMNS - ContainerAssembler.ROWS;

            if (mouseWheel > 0)
            {
                mouseWheel = 1;
            }

            if (mouseWheel < 0)
            {
                mouseWheel = -1;
            }

            this.currentScroll = (float)((double)this.currentScroll - (double)mouseWheel / (double)j);
            this.currentScroll = MathHelper.clamp(this.currentScroll, 0.0F, 1.0F);
            ((ContainerAssembler)this.inventorySlots).scrollTo(this.currentScroll);
        }
    }

    private boolean needsScrollBars() {
        return (((ContainerAssembler)this.inventorySlots).searchRecipes.size()) / (float)ContainerAssembler.COLUMNS > ContainerAssembler.ROWS;
    }

}
