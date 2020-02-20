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
        ItemStack heldItem = player.getHeldItem(hand);
        if (!heldItem.isEmpty()){
            boolean didFill = FluidUtil.interactWithFluidHandler(player, hand, world, pos, side);
            this.markDirty();
            return didFill;
        }
        return false;
    }

    public abstract TRecipe findRecipe(double speed);

    public abstract boolean matchesRecipe(TRecipe recipe, double speed);

    public abstract void consumeInputs(TRecipe recipe);

    public abstract void produceOutputs(TRecipe recipe, double speed);

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
