package rustichromia.util;

import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import rustichromia.tile.TileEntityCartControl;

import java.util.ArrayList;
import java.util.List;

public class CartUtil {
    public static boolean hasMultipleControls(IBlockAccess world, BlockPos pos) {
        return getControls(world,pos).size() > 1;
    }

    public static boolean hasControl(IBlockAccess world, BlockPos pos) {
        return getControls(world,pos).size() > 0;
    }

    public static List<TileEntityCartControl> getControls(IBlockAccess world, BlockPos pos) {
        List<TileEntityCartControl> controls = new ArrayList<>();
        for (EnumFacing facing : EnumFacing.VALUES) {
            BlockPos checkPos = pos.offset(facing);
            TileEntity tile = world.getTileEntity(checkPos);
            if(tile instanceof TileEntityCartControl) {
                TileEntityCartControl tileControl = (TileEntityCartControl) tile;
                if(tileControl.getControl() != null && tileControl.getFacing() == facing)
                    controls.add((TileEntityCartControl) tile);
            }
        }
        return controls;
    }

    public static void removeControls(World world, BlockPos pos) {
        for (EnumFacing facing : EnumFacing.VALUES) {
            BlockPos checkPos = pos.offset(facing);
            TileEntity tile = world.getTileEntity(checkPos);
            if(tile instanceof TileEntityCartControl && ((TileEntityCartControl) tile).getControl() != null) {
                world.setBlockToAir(checkPos);
            }
        }
    }
}
