package rustichromia.recipe;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import rustichromia.util.Result;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class GinRecipe extends BasicMachineRecipe {
    public List<Ingredient> inputs = new ArrayList<>();
    public List<Result> outputsInterior = new ArrayList<>();
    public List<Result> outputsExterior = new ArrayList<>();

    public GinRecipe(ResourceLocation id, double minPower, double maxPower, double time) {
        super(id,minPower,maxPower,time);
    }

    public GinRecipe(ResourceLocation id, Collection<Ingredient> inputs, Collection<Result> outputsInterior, Collection<Result> outputsExterior, double minPower, double maxPower, double time) {
        super(id,minPower,maxPower,time);
        this.inputs.addAll(inputs);
        this.outputsInterior.addAll(outputsInterior);
        this.outputsExterior.addAll(outputsExterior);
    }

    public boolean matches(TileEntity tile, double power, List<ItemStack> inputs) {
        if(power < minPower || power > maxPower)
            return false;
        ArrayList<Ingredient> toCheck = new ArrayList<>(this.inputs);
        Iterator<Ingredient> toCheckIterator = toCheck.iterator();
        while(toCheckIterator.hasNext()) {
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

    public List<Result> getResultsInterior(TileEntity tile, double power, List<ItemStack> inputs) {
        return transformResults(outputsInterior);
    }

    public List<Result> getResultsExterior(TileEntity tile, double power, List<ItemStack> inputs) {
        return transformResults(outputsExterior);
    }
}
