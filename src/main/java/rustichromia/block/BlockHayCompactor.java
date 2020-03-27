package rustichromia.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import rustichromia.tile.IMultiSlave;
import rustichromia.tile.TileEntityHayCompactor;
import rustichromia.tile.TileEntityHayCompactorInlet;
import rustichromia.tile.TileEntityMultiSlave;

import javax.annotation.Nullable;
import java.util.List;

public class BlockHayCompactor extends Block implements IMultiBlock<TileEntityHayCompactor> {
    public enum EnumType implements IStringSerializable {
        Controller("controller"),
        Inlet("inlet"),
        None("none");

        private final String name;

        EnumType(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }

        public static EnumType getFromMeta(int meta) {
            EnumType[] values = values();
            return values[meta];
        }

        public int getMeta() {
            return this.ordinal();
        }
    }

    public static final PropertyEnum<EnumType> TYPE = PropertyEnum.create("type", EnumType.class);
    public static final PropertyInteger X = PropertyInteger.create("x", 0, 2);
    public static final PropertyInteger Y = PropertyInteger.create("y", 0, 2);
    public static final PropertyInteger Z = PropertyInteger.create("z", 0, 2);

    public BlockHayCompactor(Material material) {
        super(material);
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    @Nullable
    @Override
    public MultiBlockPart getPart(IBlockAccess world, BlockPos pos) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof IMultiSlave)
            return ((IMultiSlave) tile).getPart();
        return null;
    }

    @Override
    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
        switch (state.getValue(TYPE)) {
            case Controller:
                TileEntityHayCompactor tile = (TileEntityHayCompactor) worldIn.getTileEntity(pos);
                tile.build();
                break;
        }
    }

    @Override
    public boolean canPlaceBlockAt(World world, BlockPos pos) {
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                for (int y = 0; y <= 2; y++) {
                    if (y == 0 && (x != 0 || z != 0))
                        continue;
                    BlockPos buildPos = pos.add(x, y, z);
                    IBlockState state = world.getBlockState(buildPos);
                    if (!state.getBlock().isReplaceable(world, buildPos)) {
                        return false;
                    }
                }
            }
        }

        return super.canPlaceBlockAt(world, pos);
    }

    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
        checkValidMultiblock(worldIn, pos);
    }

    @Override
    public void breakPart(World world, BlockPos pos) {
        world.setBlockToAir(pos);
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
        MultiBlockPart part = getPart(world, pos);
        Vec3i offset = part != null ? part.getSlaveOffset() : Vec3i.NULL_VECTOR;
        return super.getActualState(state, world, pos)
                .withProperty(X, offset.getX() + 1)
                .withProperty(Y, offset.getY())
                .withProperty(Z, offset.getZ() + 1);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, TYPE, X, Y, Z);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(TYPE).getMeta();
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(TYPE, EnumType.getFromMeta(meta));
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        switch (state.getValue(TYPE)) {
            case Controller:
                return new TileEntityHayCompactor();
            case Inlet:
                return new TileEntityHayCompactorInlet();
            case None:
            default:
                return new TileEntityMultiSlave();
        }
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World player, List<String> tooltip, ITooltipFlag advanced) {
        super.addInformation(stack, player, tooltip, advanced);
        tooltip.add(1, I18n.format("rustichromia.tooltip.item.multiblock"));
    }
}
