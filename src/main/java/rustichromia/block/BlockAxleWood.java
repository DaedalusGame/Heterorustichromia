package rustichromia.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import rustichromia.tile.TileEntityAxleWood;

import javax.annotation.Nullable;

public class BlockAxleWood extends BlockAxleBase {
    public BlockAxleWood(Material material) {
        super(material);
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileEntityAxleWood();
    }
}
