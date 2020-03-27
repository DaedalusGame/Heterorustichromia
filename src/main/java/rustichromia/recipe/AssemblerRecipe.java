package rustichromia.recipe;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import rustichromia.util.Result;
import rustichromia.util.ResultItem;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class AssemblerRecipe extends BasicMachineRecipe {
    public int tier;
    public List<Ingredient> inputs = new ArrayList<>();
    public List<Result> outputs = new ArrayList<>();

    public AssemblerRecipe(@Nonnull ResourceLocation id, int tier, double minPower, double maxPower, double time) {
        super(id, minPower, maxPower, time);
        this.tier = tier;
    }

    public AssemblerRecipe(@Nonnull ResourceLocation id, int tier, @Nonnull Collection<Ingredient> inputs, @Nonnull Collection<Result> outputs, double minPower, double maxPower, double time) {
        super(id, minPower, maxPower, time);
        this.tier = tier;
        this.inputs.addAll(inputs);
        this.outputs.addAll(outputs);
    }

    public boolean matches(TileEntity tile, double power, List<ItemStack> inputs) {
        if (power < minPower || power > maxPower)
            return false;
        ArrayList<Ingredient> toCheck = new ArrayList<>(this.inputs);
        Iterator<Ingredient> toCheckIterator = toCheck.iterator();
        while (toCheckIterator.hasNext()) {
            Ingredient check = toCheckIterator.next();
            for (ItemStack input : inputs) {
                if (check.apply(input)) {
                    toCheckIterator.remove();
                    break;
                }
            }
        }
        return toCheck.isEmpty();
    }

    public List<Result> getResults(TileEntity tile, double power, List<ItemStack> inputs) {
        return transformResults(outputs);
    }

    public String getName() {
        for (Result output : outputs) {
            if(output instanceof ResultItem)
                return ((ResultItem) output).getStack().getDisplayName();
        }
        return "Recipe #" + (hashCode() & 0xFF); //Fallback!
    }

    public ItemStack getIcon() {
        for (Result output : outputs) {
            if(output instanceof ResultItem)
                return ((ResultItem) output).getStack();
        }
        return new ItemStack(Items.STICK);
    }
}
