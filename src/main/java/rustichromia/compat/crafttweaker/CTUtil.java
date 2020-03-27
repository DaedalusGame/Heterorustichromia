package rustichromia.compat.crafttweaker;

import crafttweaker.api.block.IBlockState;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.item.WeightedItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import rustichromia.util.Result;
import rustichromia.util.ResultBlock;
import rustichromia.util.ResultItem;
import rustichromia.util.ResultItemChance;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class CTUtil {
    public static Ingredient toIngredient(IIngredient ingredient) {
        if(ingredient == null)
            return Ingredient.EMPTY;
        return new IngredientCraftTweaker(ingredient);
    }

    public static Collection<Ingredient> toIngredients(IIngredient[] ingredients) {
        return Arrays.stream(ingredients).map(CTUtil::toIngredient).collect(Collectors.toList());
    }

    public static Collection<ItemStack> toItemStacks(IItemStack[] stacks) {
        return Arrays.stream(stacks).map(CraftTweakerMC::getItemStack).collect(Collectors.toList());
    }

    public static Collection<Result> toResults(Object[] objects) {
        List<Result> results = new ArrayList<>();
        for (Object obj : objects) {
            if(obj instanceof IItemStack)
                results.add(toResultItem((IItemStack) obj));
            if(obj instanceof IBlockState)
                results.add(toResultBlock((IBlockState) obj));
            if(obj instanceof WeightedItemStack)
                results.add(toResultItemWeighted((WeightedItemStack) obj));
        }
        return results;
    }

    private static Result toResultItem(IItemStack obj) {
        return new ResultItem(CraftTweakerMC.getItemStack(obj));
    }

    private static Result toResultItemWeighted(WeightedItemStack obj) {
        return new ResultItemChance(CraftTweakerMC.getItemStack(obj.getStack()),obj.getChance());
    }

    private static Result toResultBlock(IBlockState obj) {
        return new ResultBlock(CraftTweakerMC.getBlockState(obj));
    }
}