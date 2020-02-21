package rustichromia.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import rustichromia.tile.TileEntityAssembler;
import rustichromia.tile.TileEntityRatiobox;

public class GuiHandler implements IGuiHandler {
    public static final int ASSEMBLER_RECIPE = 0;
    public static final int RATIOBOX = 1;

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        switch (ID) {
            case ASSEMBLER_RECIPE:
                TileEntityAssembler assembler = (TileEntityAssembler) world.getTileEntity(new BlockPos(x, y, z));
                return new ContainerAssembler(player, assembler);
            case RATIOBOX:
                TileEntityRatiobox ratioBox = (TileEntityRatiobox) world.getTileEntity(new BlockPos(x, y, z));
                return new ContainerRatioBox(player, ratioBox);
            default:
                return null;
        }
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        switch (ID) {
            case ASSEMBLER_RECIPE:
                TileEntityAssembler assembler = (TileEntityAssembler) world.getTileEntity(new BlockPos(x, y, z));
                return new GuiAssembler(player, assembler);
            case RATIOBOX:
                TileEntityRatiobox ratioBox = (TileEntityRatiobox) world.getTileEntity(new BlockPos(x, y, z));
                return new GuiRatioBox(player, ratioBox);
            default:
                return null;
        }
    }
}

