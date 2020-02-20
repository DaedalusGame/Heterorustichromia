package rustichromia.util;

import net.minecraft.item.ItemStack;

import java.util.ArrayList;

public class ResultSet extends ArrayList<ItemStack> {
    public ResultSet stack(ItemStack stack) {
        if(!stack.isEmpty())
            add(stack);
        return this;
    }

    public ResultSet ore(String ore, int count) {
        if(Misc.oreExists(ore)) {
            ItemStack stack = Misc.getOreStack(ore);
            stack.setCount(count);
            add(stack);
        }
        return this;
    }
}
