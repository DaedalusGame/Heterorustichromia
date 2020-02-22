package rustichromia.recipe;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class QuernRecipe extends BasicMachineRecipe {
    public List<Ingredient> inputs = new ArrayList<>();
    public List<ItemStack> outputs = new ArrayList<>();

    public QuernRecipe(ResourceLocation id, double minPower, double maxPower, double time) {
        super(id,minPower,maxPower,time);
    }

    public QuernRecipe(ResourceLocation id, Collection<Ingredient> inputs, Collection<ItemStack> outputs, double minPower, double maxPower, double time) {
        super(id,minPower,maxPower,time);
        this.inputs.addAll(inputs);
        this.outputs.addAll(outputs);
    }

    public boolean matches(TileEntity tile, double power, List<ItemStack> inputs) {
        if(power < minPower || power > maxPower)
            return false;
        ArrayList<Ingredient> toCheck = new ArrayList<>(this.inputs);
        Iterator<Ingredient> toCheckIterator = toCheck.iterator();
        while(toCheckIterator.hasNext()) {
            Ingredient check = toCheckIterator.next();
            for (ItemStack input : inputs) {
                if(check.apply(input))
                    toCheckIterator.remove();
            }
        }
        return toCheck.isEmpty();
    }

    public List<ItemStack> getResults(TileEntity tile, double power, List<ItemStack> inputs) {
        return outputs.stream().map(ItemStack::copy).collect(Collectors.toList());
    }
}
