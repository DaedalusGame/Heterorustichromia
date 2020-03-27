package rustichromia.block;

import net.minecraft.block.BlockCrops;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import rustichromia.Registry;

import java.util.Random;

public class BlockCotton extends BlockCrops implements IPlantable {
    public static final PropertyBool HARVESTED = PropertyBool.create("harvested");
    public static final PropertyInteger AGE = PropertyInteger.create("age", 0, 7);

    public BlockCotton() {
        setDefaultState(getDefaultState().withProperty(HARVESTED, false));
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    protected Item getSeed() {
        return Item.getItemFromBlock(this);
    }

    @Override
    protected Item getCrop() {
        return Registry.COTTON;
    }

    @Override
    protected PropertyInteger getAgeProperty() {
        return AGE;
    }

    @Override
    public int getMaxAge() {
        return 7;
    }

    @Override
    public boolean isMaxAge(IBlockState state) {
        return super.isMaxAge(state) && !state.getValue(HARVESTED);
    }

    @Override
    public boolean canBlockStay(World world, BlockPos pos, IBlockState state) {
        return (world.getLight(pos) >= 9 || world.canSeeSky(pos)) && canBePlantedHere(world, pos);
    }

    public boolean canBePlantedHere(World world, BlockPos pos) {
        IBlockState soil = world.getBlockState(pos.down());
        return soil.getBlock().canSustainPlant(soil, world, pos.down(), EnumFacing.UP, this);
    }

    @Override
    public EnumPlantType getPlantType(IBlockAccess world, BlockPos pos) {
        return EnumPlantType.Crop;
    }

    @Override
    public IBlockState getPlant(IBlockAccess world, BlockPos pos) {
        return getDefaultState();
    }

    private boolean isMilitaristic(IBlockAccess world, BlockPos pos, IBlockState state) {
        for (EnumFacing facing : EnumFacing.HORIZONTALS) {
            if(!isMilitaristic(world,pos,state,facing))
                return false;
        }
        return true;
    }

    private boolean isMilitaristic(IBlockAccess world, BlockPos pos, IBlockState state, EnumFacing facing){
        BlockPos.MutableBlockPos testPos = new BlockPos.MutableBlockPos(pos);
        int myAge = state.getValue(AGE);
        int maxEmpty = 3;
        for(int i = 0; i < 6 && maxEmpty > 0; i++) {
            testPos.move(facing);
            IBlockState testState = world.getBlockState(testPos);
            if(testState.getBlock() == this) {
                if(testState.getValue(AGE) < myAge)
                    return false;
            } else {
                maxEmpty--;
            }
        }
        return true;
    }

    @Override
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
        this.checkAndDropBlock(worldIn, pos, state);

        if (!worldIn.isAreaLoaded(pos, 1)) return;
        if (worldIn.getLightFromNeighbors(pos.up()) < 9) return;
        int age = this.getAge(state);

        if (age < this.getMaxAge()) {
            float chance = getGrowthChance(this, worldIn, pos);

            if(rand.nextInt((int) (25.0F / chance) + 1) > 0)
                return;
            if(!isMilitaristic(worldIn,pos,state))
                return;

            if (net.minecraftforge.common.ForgeHooks.onCropsGrowPre(worldIn, pos, state, true)) {
                worldIn.setBlockState(pos, state.withProperty(AGE,age + 1), 2);
                net.minecraftforge.common.ForgeHooks.onCropsGrowPost(worldIn, pos, state, worldIn.getBlockState(pos));
            }
        }
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if(this.getAge(state) >= this.getMaxAge())
        {
            ItemStack heldItem = playerIn.getHeldItem(hand);
            int fortune = EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, heldItem);
            NonNullList<ItemStack> drops = NonNullList.create();
            getDrops(drops,worldIn,pos,state,fortune);
            for (ItemStack stack : drops) {
                spawnAsEntity(worldIn, pos, stack);
            }
            worldIn.setBlockState(pos, state.withProperty(HARVESTED, true), 2);
            return true;
        }
        return super.onBlockActivated(worldIn, pos, state, playerIn, hand, facing, hitX, hitY, hitZ);
    }

    @Override
    public void grow(World worldIn, BlockPos pos, IBlockState state) {
        float chance = getGrowthChance(this, worldIn, pos) * 3;

        if(worldIn.rand.nextInt((int) (25.0F / chance) + 1) > 0)
            return;

        int newAge = this.getAge(state) + 1;
        int maxAge = this.getMaxAge();

        if (newAge > maxAge)
            newAge = maxAge;

        worldIn.setBlockState(pos, state.withProperty(AGE, newAge), 2);
    }

    @Override
    protected int getBonemealAgeIncrease(World worldIn) {
        return 1;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, AGE, HARVESTED);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        boolean harvested = state.getValue(HARVESTED);
        int age = state.getValue(AGE);

        if (harvested)
            age = getMaxAge()+1;

        return age;
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        IBlockState state = getDefaultState();
        if (meta > getMaxAge())
            return state.withProperty(AGE, getMaxAge()).withProperty(HARVESTED, true);
        return state.withProperty(AGE, meta);
    }

    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        int age = state.getValue(AGE);
        boolean harvested = state.getValue(HARVESTED);
        Random rand = world instanceof World ? ((World)world).rand : new Random();

        if(age < getMaxAge())
            return;
        if(harvested)
            return;
        for (int i = 0; i < 3 + fortune; ++i)
        {
            if (rand.nextInt(2) < 1)
            {
                drops.add(new ItemStack(this.getCrop(), 1, 0));
            }
        }
    }

    @SubscribeEvent
    public void trampleCrops(BlockEvent.FarmlandTrampleEvent event) {
        World world = event.getWorld();
        BlockPos pos = event.getPos();

        IBlockState crop = world.getBlockState(pos.up());

        if(crop.getBlock() == this)
            event.setCanceled(true);
    }
}
