package rustichromia.util;

import c4.conarm.lib.armor.ArmorCore;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import slimeknights.mantle.util.ItemStackList;
import slimeknights.tconstruct.library.tools.ToolCore;

import java.util.ArrayList;

public class TinkersUtil {
    public static ItemStack buildTool(ArrayList<ItemStack> parts, ToolCore core) {
        NonNullList<ItemStack> input = ItemStackList.of(parts);
        if(core != null) {
            return core.buildItemFromStacks(input);
        }

        return ItemStack.EMPTY;
    }

    public static ItemStack buildArmor(ArrayList<ItemStack> parts, ArmorCore core) {
        NonNullList<ItemStack> input = ItemStackList.of(parts);
        if(core != null) {
            return core.buildItemFromStacks(input);
        }

        return ItemStack.EMPTY;
    }
}
