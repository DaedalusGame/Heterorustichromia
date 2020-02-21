package rustichromia.gui;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;
import rustichromia.Rustichromia;
import rustichromia.network.MessageUpdateRatiobox;
import rustichromia.network.PacketHandler;
import rustichromia.tile.TileEntityRatiobox;

import java.io.IOException;

public class GuiRatioBox extends GuiContainer {
    private static final ResourceLocation guiLocation = new ResourceLocation(Rustichromia.MODID,"textures/gui/ratiobox.png");

    private static final Rectangle BUTTON_OFF_UP = new Rectangle(38,46, 7, 12);
    private static final Rectangle BUTTON_OFF_DOWN = new Rectangle(128,46, 7, 12);
    private static final Rectangle BUTTON_ON_UP = new Rectangle(38,64, 7, 12);
    private static final Rectangle BUTTON_ON_DOWN = new Rectangle(128,64, 7, 12);

    public GuiRatioBox(EntityPlayer player, TileEntityRatiobox ratioBox) {
        super(new ContainerRatioBox(player,ratioBox));
        this.ySize = 164;
    }

    private double getRatioOn() {
        return ((ContainerRatioBox)inventorySlots).getRatioOn();
    }

    private double getRatioOff() {
        return ((ContainerRatioBox)inventorySlots).getRatioOff();
    }

    private float getAngleA(float partialTicks){
        return ((ContainerRatioBox)inventorySlots).getAngleA(partialTicks);
    }

    private float getAngleB(float partialTicks){
        return ((ContainerRatioBox)inventorySlots).getAngleB(partialTicks);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(guiLocation);
        int xPos = (this.width - this.xSize) / 2;
        int yPos = (this.height - this.ySize) / 2;
        drawTexturedModalRect(xPos, yPos, 0, 0, this.xSize, this.ySize);
        drawTexturedModalRect(xPos+BUTTON_OFF_UP.x, yPos+BUTTON_OFF_UP.y, 192 + ((getRatioOff() <= 0) ? BUTTON_OFF_UP.width : 0), 12, BUTTON_OFF_UP.width, BUTTON_OFF_UP.height);
        drawTexturedModalRect(xPos+BUTTON_OFF_DOWN.x, yPos+BUTTON_OFF_DOWN.y, 192 + ((getRatioOff() >= 1) ? BUTTON_OFF_DOWN.width : 0), 0, BUTTON_OFF_DOWN.width, BUTTON_OFF_DOWN.height);
        drawTexturedModalRect(xPos+BUTTON_ON_UP.x, yPos+BUTTON_ON_UP.y, 192 + ((getRatioOn() <= 0) ? BUTTON_ON_UP.width : 0), 12, BUTTON_ON_UP.width, BUTTON_ON_UP.height);
        drawTexturedModalRect(xPos+BUTTON_ON_DOWN.x, yPos+BUTTON_ON_DOWN.y, 192 + ((getRatioOn() >= 1) ? BUTTON_ON_DOWN.width : 0), 0, BUTTON_ON_DOWN.width, BUTTON_ON_DOWN.height);

        GlStateManager.pushMatrix();
        GlStateManager.translate(xPos,yPos,0);
        GlStateManager.pushMatrix();
        GlStateManager.translate(75,16,0);
        GlStateManager.translate(8,8,0);
        GlStateManager.rotate(getAngleB(partialTicks), 0,0, 1);
        GlStateManager.translate(-8,-8,0);
        drawTexturedModalRect(0,0,176, 0, 16, 16);
        GlStateManager.popMatrix();
        GlStateManager.pushMatrix();
        GlStateManager.translate(82,23,0);
        GlStateManager.translate(8,8,0);
        GlStateManager.rotate(-getAngleA(partialTicks), 0,0, 1);
        GlStateManager.translate(-8,-8,0);
        drawTexturedModalRect(0,0,176, 0, 16, 16);
        GlStateManager.popMatrix();
        GlStateManager.popMatrix();
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        Integer ratioOffA = (int)Math.round(getRatioOff() * 100);
        Integer ratioOffB = (int)Math.round((1-getRatioOff()) * 100);
        Integer ratioOnA = (int)Math.round(getRatioOn() * 100);
        Integer ratioOnB = (int)Math.round((1-getRatioOn()) * 100);

        fontRenderer.drawString(ratioOffB.toString(), 86 - 16 - fontRenderer.getStringWidth(ratioOffB.toString()) / 2, 49, 4210752);
        fontRenderer.drawString(":", 86 - fontRenderer.getStringWidth(":") / 2, 49, 4210752);
        fontRenderer.drawString(ratioOffA.toString(), 86 + 16 - fontRenderer.getStringWidth(ratioOffA.toString()) / 2, 49, 4210752);
        fontRenderer.drawString(ratioOnB.toString(), 86 - 16 - fontRenderer.getStringWidth(ratioOnB.toString()) / 2, 67, 4210752);
        fontRenderer.drawString(":", 86 - fontRenderer.getStringWidth(":") / 2, 67, 4210752);
        fontRenderer.drawString(ratioOnA.toString(), 86 + 16 - fontRenderer.getStringWidth(ratioOnA.toString()) / 2, 67, 4210752);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        double amount = 0.01;
        if(Keyboard.isKeyDown(42) || Keyboard.isKeyDown(54))
            amount = 0.1;

        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;

        if(BUTTON_OFF_UP.contains(mouseX-i,mouseY-j) && mouseButton == 0)
            setRatio(getRatioOn(),getRatioOff()-amount);
        if(BUTTON_OFF_DOWN.contains(mouseX-i,mouseY-j) && mouseButton == 0)
            setRatio(getRatioOn(),getRatioOff()+amount);
        if(BUTTON_ON_UP.contains(mouseX-i,mouseY-j) && mouseButton == 0)
            setRatio(getRatioOn()-amount, getRatioOff());
        if(BUTTON_ON_DOWN.contains(mouseX-i,mouseY-j) && mouseButton == 0)
            setRatio(getRatioOn()+amount, getRatioOff());

        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    private void setRatio(double ratioOn, double ratioOff) {
        ((ContainerRatioBox)inventorySlots).setRatio(ratioOn,ratioOff);
        PacketHandler.INSTANCE.sendToServer(new MessageUpdateRatiobox(ratioOn,ratioOff));
    }
}
