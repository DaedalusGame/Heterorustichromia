package rustichromia.item;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBed;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class ItemBlockBed extends ItemBlock {
    public ItemBlockBed(Block block) {
        super(block);
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        EnumFacing placementFacing = getFacingForPlacement(player);

        BlockPos placePos = pos;
        if (!world.getBlockState(pos).getBlock().isReplaceable(world, pos))
            placePos = placePos.offset(facing);

        if(!isFree(world,placePos) || !isFree(world,placePos.offset(placementFacing)))
            return EnumActionResult.FAIL;

        return super.onItemUse(player, world, pos, hand, facing, hitX, hitY, hitZ);
    }

    private boolean isFree(World world, BlockPos pos) {
        IBlockState state = world.getBlockState(pos);
        return state.getBlock().isReplaceable(world,pos);
    }

    public EnumFacing getFacingForPlacement(EntityPlayer player) {
        int i = MathHelper.floor((double)(player.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
        return EnumFacing.getHorizontal(i);
    }

    @Override
    public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, IBlockState newState) {
        EnumFacing placementFacing = getFacingForPlacement(player);

        IBlockState placeState = block.getDefaultState().withProperty(BlockBed.FACING, placementFacing);

        IBlockState head = placeState.withProperty(BlockBed.PART, BlockBed.EnumPartType.HEAD);
        IBlockState foot = placeState.withProperty(BlockBed.PART, BlockBed.EnumPartType.FOOT);

        BlockPos footPos = pos;
        BlockPos headPos = pos.offset(placementFacing);

        if (!world.setBlockState(headPos, head, 11)) return false;
        world.setBlockState(footPos, foot, 11);

        head = world.getBlockState(headPos);
        foot = world.getBlockState(footPos);
        if (head.getBlock() == this.block && foot.getBlock() == this.block)
        {
            this.block.onBlockPlacedBy(world, headPos, head, player, stack);
            this.block.onBlockPlacedBy(world, footPos, foot, player, stack);

            if (player instanceof EntityPlayerMP)
                CriteriaTriggers.PLACED_BLOCK.trigger((EntityPlayerMP)player, pos, stack);
        }

        return true;
    }
}
