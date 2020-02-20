package rustichromia.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import rustichromia.tile.TileEntityAssembler;

public class GuiHandler implements IGuiHandler {
    public static final int ASSEMBLER_RECIPE = 0;

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        switch (ID) {
            case ASSEMBLER_RECIPE:
                TileEntityAssembler assembly = (TileEntityAssembler) world.getTileEntity(new BlockPos(x, y, z));
                return new ContainerAssembler(player, assembly);
            default:
                return null;
        }
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        switch (ID) {
            case ASSEMBLER_RECIPE:
                TileEntityAssembler assembly = (TileEntityAssembler) world.getTileEntity(new BlockPos(x, y, z));
                return new GuiAssembler(player, assembly);
            default:
                return null;
        }
    }
}

