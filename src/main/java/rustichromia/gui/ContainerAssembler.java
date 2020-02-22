package rustichromia.gui;

import com.google.common.collect.Lists;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.*;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import rustichromia.network.MessageSelectAssemblerRecipe;
import rustichromia.network.PacketHandler;
import rustichromia.recipe.AssemblerRecipe;
import rustichromia.recipe.RecipeRegistry;
import rustichromia.tile.TileEntityAssembler;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ContainerAssembler extends Container {
    final RecipeBlob NO_RECIPE = new RecipeBlob(null) {
        @Override
        public String getName() {
            return "No Filter";
        }

        @Override
        public List<String> getTooltip() {
            return Lists.newArrayList(getName());
        }

        @Override
        public ItemStack getIcon() {
            return new ItemStack(Blocks.BARRIER);
        }

        @Override
        public ResourceLocation getID() {
            return null;
        }

        @Override
        public boolean isSelected() {
            return assembler.getFilter() == null;
        }
    };

    class RecipeBlob {
        AssemblerRecipe recipe;

        public RecipeBlob(AssemblerRecipe recipe) {
            this.recipe = recipe;
        }

        public ItemStack getIcon() {
            return recipe.getIcon();
        }

        public String getName() {
            return recipe.getName();
        }

        public List<String> getTooltip() {
            List<String> tooltip = Lists.newArrayList(getName());
            List<String> basePowerData = recipe.getBasePowerData();
            List<String> powerData = recipe.getPowerData();
            List<String> extraData = recipe.getExtraData();
            if(basePowerData != null)
                tooltip.addAll(formatTooltip(basePowerData,TextFormatting.GRAY));
            if(powerData != null)
                tooltip.addAll(formatTooltip(powerData,TextFormatting.GRAY));
            if(extraData != null)
                tooltip.addAll(formatTooltip(extraData,TextFormatting.GRAY));
            return tooltip;
        }

        private List<String> formatTooltip(List<String> tooltip, TextFormatting formatting) {
            return tooltip.stream().map(line -> formatting + line).collect(Collectors.toList());
        }

        public boolean isSelected() {
            return recipe.id.equals(assembler.getFilter());
        }

        public ResourceLocation getID() {
            return recipe.id;
        }
    }

    class SlotAssemblerRecipe extends UnboundSlot {
        public SlotAssemblerRecipe(int index, int xPosition, int yPosition) {
            super(index, xPosition, yPosition);
        }

        public int getScrollOffset() {
            return COLUMNS * scrollRows;
        }

        public RecipeBlob getRecipe() {
            if(getSlotIndex() + getScrollOffset() < searchRecipes.size())
                return searchRecipes.get(getSlotIndex() + getScrollOffset());
            return null;
        }

        @Override
        public ItemStack getStack() {
            RecipeBlob recipe = getRecipe();
            if(recipe != null)
                return recipe.getIcon();
            return ItemStack.EMPTY;
        }

        @Override
        public boolean isEnabled() {
            return getSlotIndex() + getScrollOffset() < searchRecipes.size();
        }

        public List<String> getTooltip() {
            return getRecipe().getTooltip();
        }

        public boolean isSelected() {
            RecipeBlob recipe = getRecipe();
            return recipe != null && recipe.isSelected();
        }
    }

    public static final int COLUMNS = 6;
    public static final int ROWS = 4;

    TileEntityAssembler assembler;
    Slot searchSlot;
    IInventory searchInventory = new InventoryBasic("AssemblerSearch", true,1);
    List<RecipeBlob> recipes = new ArrayList<>();
    List<RecipeBlob> searchRecipes = new ArrayList<>();
    float currentScroll;
    int scrollRows;
    boolean forceClose;

    public ContainerAssembler(EntityPlayer player, TileEntityAssembler assembler) {
        this.assembler = assembler;
        recipes.add(NO_RECIPE);
        for (AssemblerRecipe recipe : RecipeRegistry.assemblerRecipes) {
            if(recipe.tier <= assembler.getTier())
                recipes.add(new RecipeBlob(recipe));
        }

        this.addSlotToContainer(searchSlot = new Slot(searchInventory,0, 8, 8) {
            @Override
            public void onSlotChanged() {
                super.onSlotChanged();
                updateSearch();
            }
        });
        int index = 0;
        for(int y = 0; y < ROWS; y++) {
            for(int x = 0; x < COLUMNS; x++) {
                this.addSlotToContainer(new SlotAssemblerRecipe(index, 44 + x * 18, 8 + y * 18));
                index++;
            }
        }

        bindPlayerInventory(player.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY,null));

        updateSearch();
    }

    private void updateSearch() {
        searchRecipes.clear();
        ItemStack searchStack = searchSlot.getStack();
        if(searchStack.isEmpty())
            searchRecipes.addAll(recipes);
        else {
            for (RecipeBlob recipe : recipes) {
                if(recipe.recipe == null || matchesSearch(recipe.recipe,searchStack))
                    searchRecipes.add(recipe);
            }
        }
        scrollTo(currentScroll);
    }

    private boolean matchesSearch(@Nonnull AssemblerRecipe recipe, ItemStack searchStack) {
        for (Ingredient ingredient : recipe.inputs) {
            if (ingredient.apply(searchStack)) {
                return true;
            }
        }
        for (ItemStack result : recipe.outputs) {
            if (result.isItemEqual(searchStack)) {
                return true;
            }
        }
        return false;
    }

    public void scrollTo(float currentScroll) {
        this.currentScroll = currentScroll;
        int maxRows = MathHelper.ceil(searchRecipes.size() / (float)COLUMNS);
        scrollRows = MathHelper.clamp((int) ((maxRows - ROWS + 1) * currentScroll),0, Math.max(0,maxRows - ROWS));
    }

    protected void bindPlayerInventory(IItemHandler inventoryPlayer) {
        for(int i = 0; i < 3; i++)
        {
            for(int j = 0; j < 9; j++)
            {
                addSlotToContainer(new SlotItemHandler(inventoryPlayer, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for(int i = 0; i < 9; i++)
        {
            addSlotToContainer(new SlotItemHandler(inventoryPlayer, i, 8 + i * 18, 84+58));
        }
    }

    @Override
    public void onContainerClosed(EntityPlayer playerIn) {
        super.onContainerClosed(playerIn);
        if (!assembler.getWorld().isRemote) {
            returnSlot(playerIn, playerIn.getEntityWorld(), searchSlot);
        }
    }

    private void returnSlot(EntityPlayer playerIn, World worldIn, Slot slot) {
        if (!playerIn.isEntityAlive() || playerIn instanceof EntityPlayerMP && ((EntityPlayerMP) playerIn).hasDisconnected()) {
            playerIn.dropItem(slot.getStack(), false);
        } else {
            playerIn.inventory.placeItemBackInInventory(worldIn, slot.getStack());
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return !forceClose;
    }

    @Override
    public ItemStack slotClick(int slotId, int dragType, ClickType clickTypeIn, EntityPlayer player) {
        if(slotId > 0 && slotId <= ROWS * COLUMNS) {
            Slot slot = getSlot(slotId);
            if(slot instanceof SlotAssemblerRecipe && player instanceof EntityPlayerSP) {
                RecipeBlob recipe = ((SlotAssemblerRecipe) slot).getRecipe();
                PacketHandler.INSTANCE.sendToServer(new MessageSelectAssemblerRecipe(recipe.getID(),true));
            }
            return ItemStack.EMPTY;
        }
        return super.slotClick(slotId, dragType, clickTypeIn, player);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index)
    {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);

        if (slot != null && slot.getHasStack())
        {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            if (index < 1)
            {
                if (!this.mergeItemStack(itemstack1, 1+ROWS*COLUMNS, 1+ROWS*COLUMNS + 27 + 9, true))
                {
                    return ItemStack.EMPTY;
                }
            }
            else if (!this.mergeItemStack(itemstack1, 0, 1, false))
            {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty())
            {
                slot.putStack(ItemStack.EMPTY);
            }
            else
            {
                slot.onSlotChanged();
            }

            if (itemstack1.getCount() == itemstack.getCount())
            {
                return ItemStack.EMPTY;
            }

            slot.onTake(playerIn, itemstack1);
        }

        return itemstack;
    }

    public void setRecipe(ResourceLocation id, boolean shouldClose) {
        assembler.setFilter(id);
        if(shouldClose)
            forceClose = true;
    }
}
