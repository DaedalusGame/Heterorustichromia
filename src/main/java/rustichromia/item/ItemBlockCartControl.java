package rustichromia.item;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rustichromia.Rustichromia;
import rustichromia.cart.Control;
import rustichromia.cart.ControlSupplier;
import rustichromia.tile.TileEntityCartControl;
import rustichromia.util.Misc;

import javax.annotation.Nonnull;

public class ItemBlockCartControl extends ItemBlock {
    public ItemBlockCartControl(Block block) {
        super(block);
        setHasSubtypes(true);
    }

    public ItemStack fromControl(ControlSupplier supplier) {
        ItemStack stack = new ItemStack(this);
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setString("type", supplier.getResourceLocation().toString());
        stack.setTagCompound(nbt);
        return stack;
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (this.isInCreativeTab(tab)) {
            for (ControlSupplier supplier : Control.getSuppliers()) {
                items.add(fromControl(supplier));
            }
        }
    }

    public ControlSupplier getControlSupplier(ItemStack stack) {
        if(stack.hasTagCompound()) {
            NBTTagCompound compound = stack.getTagCompound();
            ResourceLocation resourceLocation = new ResourceLocation(compound.getString("type"));
            return Control.get(resourceLocation);
        }
        return null;
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        ControlSupplier supplier = getControlSupplier(stack);
        if(supplier != null)
            return "item.cart_control."+ supplier.getUnlocalizedName();
        else
            return "item.cart_control";
    }

    @Nonnull
    public ModelResourceLocation getModel(ItemStack stack) {
        ControlSupplier supplier = getControlSupplier(stack);
        if(supplier != null)
            return supplier.getModelVariant();
        else
            return new ModelResourceLocation(new ResourceLocation(Rustichromia.MODID,"cart_control"),"inventory");
    }

    public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, IBlockState newState) {
        ControlSupplier supplier = getControlSupplier(stack);

        if(supplier == null)
            return false;

        boolean success = super.placeBlockAt(stack, player, world, pos, side, hitX, hitY, hitZ, newState);

        if(success && !world.isRemote) {
            TileEntityCartControl tile = (TileEntityCartControl) world.getTileEntity(pos);
            if(tile != null) {
                EnumFacing controlFacing = Misc.getFaceOrientation(side, hitX, hitY, hitZ);
                Control control = supplier.get().setFacing(controlFacing);
                tile.setData(control);
            }
        }

        return success;
    }

    @SideOnly(Side.CLIENT)
    public boolean hasEffect(ItemStack stack)
    {
        return true;
    }
}
