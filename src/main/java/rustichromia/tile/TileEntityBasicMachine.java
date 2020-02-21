package rustichromia.tile;

import mysticalmechanics.api.IMechCapability;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import rustichromia.Rustichromia;
import rustichromia.gui.GuiHandler;
import rustichromia.recipe.BasicMachineRecipe;

public abstract class TileEntityBasicMachine<TRecipe extends BasicMachineRecipe> extends TileEntity implements ITickable {
    public final double RECIPE_CHECK_TIME = 30;

    public double lastAngle;
    public double angle;
    int recipeCheck;
    public double recipeTime;
    public TRecipe recipe;
    public IMechCapability mechPower;

    public boolean activate(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand,
                            EnumFacing side, float hitX, float hitY, float hitZ) {
        return false;
    }

    public void breakBlock(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
        clearInventory();
    }

    public abstract TRecipe findRecipe(double speed);

    public abstract boolean matchesRecipe(TRecipe recipe, double speed);

    public abstract void consumeInputs(TRecipe recipe);

    public abstract void produceOutputs(TRecipe recipe, double speed);

    public abstract void clearInventory();

    public boolean hasInventory(EnumFacing facing) {
        TileEntity tile = getWorld().getTileEntity(getPos().offset(facing));
        return tile != null && tile.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY,facing.getOpposite()); //Don't care what kind of inventory
    }

    public ItemStack pushToInventory(ItemStack stack, EnumFacing facing, boolean simulate) {
        TileEntity tile = getWorld().getTileEntity(getPos().offset(facing));
        if(tile != null && tile.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY,facing.getOpposite())) {
            IItemHandler handler = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, facing.getOpposite());
            ItemStack currentPush = stack;
            for(int i = 0; i < handler.getSlots(); i++){
                if(currentPush.isEmpty())
                    break;
                currentPush = handler.insertItem(i,currentPush,simulate);
            }
            return currentPush;
        }
        return stack;
    }

    public void resetRecipe() {
        recipe = null;
        recipeTime = 0;
    }

    @Override
    public void update() {
        double speed = mechPower.getPower(null);
        if(!world.isRemote) {
            if(speed > 0)
                recipeCheck++;
            if(recipe == null && recipeCheck > RECIPE_CHECK_TIME) {
                recipe = findRecipe(speed);
                recipeCheck -= RECIPE_CHECK_TIME;
            }
            if(recipe != null) {
                recipeTime += recipe.getSpeed(speed);
                boolean matches = matchesRecipe(recipe, speed);
                while(recipeTime >= recipe.getTime() && matches) {
                    produceOutputs(recipe, speed);
                    consumeInputs(recipe);
                    recipeTime -= recipe.getTime();
                    matches = matchesRecipe(recipe, speed);
                }
                if(!matches)
                    resetRecipe();
            } else
                resetRecipe();
        } else {
            lastAngle = angle;
            angle += speed;
        }
    }
}
