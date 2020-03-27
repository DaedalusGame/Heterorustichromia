package rustichromia.compat.jei;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import mezz.jei.api.gui.ITooltipCallback;
import net.minecraft.item.ItemStack;
import rustichromia.util.Misc;
import rustichromia.util.Result;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class ResultHelper {
    BiMap<Result, ItemStack> mapper = HashBiMap.create();
    ITooltipCallback<ItemStack> tooltipCallback = new ITooltipCallback<ItemStack>() {
        @Override
        public void onTooltip(int slotIndex, boolean input, ItemStack ingredient, List<String> tooltip) {
            Result result = mapper.inverse().get(ingredient);
            if(result != null) {
                result.getJEITooltip(tooltip);
            }
        }
    };

    public ResultHelper() {
    }

    public ITooltipCallback<ItemStack> getTooltipCallback() {
        return tooltipCallback;
    }

    public List<ItemStack> getItems(Collection<Result> results) {
        return results.stream().map(this::getStack).collect(Collectors.toList());
    }

    public List<List<ItemStack>> splitIntoBoxes(Collection<Result> results, int boxes) {
        return Misc.splitIntoBoxes(getItems(results),boxes);
    }

    public ItemStack getStack(Result result) {
        return mapper.get(result);
    }

    public void setup(Collection<Result> results) {
        for (Result result : results) {
            ItemStack stack = result.getJEIStack();
            mapper.put(result,stack);
        }
    }
}
