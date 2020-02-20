package rustichromia.util;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.NonNullList;
import net.minecraftforge.oredict.OreDictionary;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Misc {
    public static final HashSet<String> ORE_BLACKLIST = new HashSet<>();

    static {
        ORE_BLACKLIST.add("Coal");
        ORE_BLACKLIST.add("Lapis");
        ORE_BLACKLIST.add("Redstone");
        ORE_BLACKLIST.add("Quartz");
        ORE_BLACKLIST.add("NetherQuartz");
        ORE_BLACKLIST.add("Prismarine");
        ORE_BLACKLIST.add("Glowstone");
        ORE_BLACKLIST.add("Salt");
    }

    public static <T> List<List<T>> splitIntoBoxes(List<T> stacks, int boxes) {
        ArrayList<List<T>> splitStacks = new ArrayList<>();
        for (int i = 0; i < boxes; i++) {
            final int finalI = i;
            splitStacks.add(IntStream.range(0, stacks.size()).filter(index -> index % boxes == finalI).mapToObj(stacks::get).collect(Collectors.toList()));
        }
        return splitStacks;
    }

    public static boolean isOre(ItemStack stack, String ore) {
        if(stack.isEmpty())
            return false;
        for (int id : OreDictionary.getOreIDs(stack)) {
            if(OreDictionary.getOreName(id).equals(ore))
                return true;
        }
        return false;
    }

    public static boolean oreStartsWith(ItemStack stack, String ore) {
        if(stack.isEmpty())
            return false;
        for (int id : OreDictionary.getOreIDs(stack)) {
            if(OreDictionary.getOreName(id).startsWith(ore))
                return true;
        }
        return false;
    }

    public static boolean oreEndsWith(ItemStack stack, String ore) {
        if(stack.isEmpty())
            return false;
        for (int id : OreDictionary.getOreIDs(stack)) {
            if(OreDictionary.getOreName(id).endsWith(ore))
                return true;
        }
        return false;
    }

    public static boolean oreExists(String name) {
        return OreDictionary.doesOreNameExist(name) && OreDictionary.getOres(name, false).stream().anyMatch(stack -> stack != null && !stack.isEmpty());
    }

    public static boolean isOreBlacklisted(String ore) {
        return ORE_BLACKLIST.contains(ore);
    }

    public static ItemStack getOreStack(String ore) {
        Collection<ItemStack> candidates = OreDictionary.getOres(ore);
        for (ItemStack stack : candidates) {
            if(stack == null || stack.isEmpty())
                continue;
            return stack.copy();
        }
        return ItemStack.EMPTY;
    }
}
