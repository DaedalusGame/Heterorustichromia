package rustichromia.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import rustichromia.tile.TileEntityMechTorchToggle;

import javax.annotation.Nullable;

public class BlockMechTorchToggle extends BlockMechTorch {
    public BlockMechTorchToggle(Material blockMaterialIn) {
        super(blockMaterialIn);
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileEntityMechTorchToggle();
    }
}
