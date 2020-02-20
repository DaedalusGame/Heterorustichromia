package rustichromia.util;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.stream.Stream;

public class IngredientSized extends Ingredient implements IHasSize {
    Ingredient ingredient;
    int size;

    public IngredientSized(Ingredient ingredient, int size) {
        this.ingredient = ingredient;
        this.size = size;
    }

    @Override
    public ItemStack[] getMatchingStacks() {
        return Arrays.stream(ingredient.getMatchingStacks()).map(stack -> copyWithSize(stack,size)).toArray(ItemStack[]::new);
    }

    private ItemStack copyWithSize(ItemStack stack,int size) {
        stack = stack.copy();
        stack.setCount(size);
        return stack;
    }

    @Override
    public int getSize() {
        return size;
    }

    @Override
    public boolean apply(@Nullable ItemStack stack) {
        return ingredient.apply(stack) && stack.getCount() >= size;
    }
}
