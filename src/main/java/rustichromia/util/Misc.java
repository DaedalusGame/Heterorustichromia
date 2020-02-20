package rustichromia.util;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Misc {
    public static <T> List<List<T>> splitIntoBoxes(List<T> stacks, int boxes) {
        ArrayList<List<T>> splitStacks = new ArrayList<>();
        for (int i = 0; i < boxes; i++) {
            final int finalI = i;
            splitStacks.add(IntStream.range(0, stacks.size()).filter(index -> index % boxes == finalI).mapToObj(stacks::get).collect(Collectors.toList()));
        }
        return splitStacks;
    }
}
