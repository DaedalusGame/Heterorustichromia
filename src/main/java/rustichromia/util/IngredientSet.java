package rustichromia.util;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.oredict.OreIngredient;

import java.util.ArrayList;

public class IngredientSet extends ArrayList<Ingredient> {
    public IngredientSet stack(ItemStack stack) {
        if(!stack.isEmpty()) {
            Ingredient ingredient = Ingredient.fromStacks(stack);
            if (stack.getCount() > 1)
                ingredient = new IngredientSized(ingredient, stack.getCount());
            add(ingredient);
        }
        return this;
    }

    public IngredientSet ore(String ore, int count) {
        if(Misc.oreExists(ore) && count > 0) {
            Ingredient ingredient = new OreIngredient(ore);
            if(count > 1)
                ingredient = new IngredientSized(ingredient,count);
            add(ingredient);
        }
        return this;
    }
}
