package rustichromia.util;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.Collection;

public class ResultSet extends ArrayList<Result> {
    public ResultSet stack(ItemStack stack) {
        if(!stack.isEmpty())
            add(new ResultItem(stack));
        return this;
    }

    public ResultSet stack(ItemStack stack, float chance) {
        if(!stack.isEmpty())
            add(new ResultItemChance(stack, chance));
        return this;
    }

    public ResultSet ore(String ore, int count) {
        if(Misc.oreExists(ore)) {
            ItemStack stack = Misc.getOreStack(ore);
            stack.setCount(count);
            add(new ResultItem(stack));
        }
        return this;
    }

    public ResultSet block(IBlockState state, ItemStack stack) {
        add(new ResultBlock(state,stack));
        return this;
    }
}
