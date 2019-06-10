package rustichromia.block;

import mysticalmechanics.block.BlockAxle;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public abstract class BlockAxleBase extends BlockAxle {
    public BlockAxleBase(Material material) {
        super(material);
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.INVISIBLE;
    }

    @Nullable
    @Override
    public abstract TileEntity createTileEntity(World world, IBlockState state);
}
