package rustichromia.util;

import com.google.common.collect.Lists;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.oredict.OreDictionary;
import rustichromia.tile.TileEntityAssembler;

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

    public static void dropInventory(World world, BlockPos pos, IItemHandler itemHandler) {
        for (int i = 0; i < itemHandler.getSlots(); i++)
            InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), itemHandler.getStackInSlot(i));
    }

    public static void dropInventory(World world, BlockPos pos, ItemBuffer itemBuffer) {
        itemBuffer.dropAll(world,pos);
    }

    public static void syncTE(TileEntity tile, boolean broken) {
        mysticalmechanics.util.Misc.syncTE(tile,broken);
    }

    public static AxisAlignedBB rotateAABB(AxisAlignedBB aabb, EnumFacing facing) {
        switch (facing) {
            case DOWN:
                return new AxisAlignedBB(aabb.minX, 1 - aabb.maxY, aabb.minZ, aabb.maxX, 1 - aabb.minY, aabb.maxZ);
            case UP:
                return new AxisAlignedBB(aabb.minX, aabb.minY, aabb.minZ, aabb.maxX, aabb.maxY, aabb.maxZ);
            case NORTH:
                return new AxisAlignedBB(aabb.minX, aabb.minZ, aabb.maxY, aabb.maxX, aabb.maxZ, aabb.minY);
            case SOUTH:
                return new AxisAlignedBB(aabb.minX, aabb.maxZ, aabb.minY, aabb.maxX, aabb.minZ, aabb.maxY);
            case WEST:
                return new AxisAlignedBB(aabb.maxY, aabb.minX, aabb.minZ, aabb.minY, aabb.maxX, aabb.maxZ);
            case EAST:
                return new AxisAlignedBB(aabb.minY, aabb.maxX, aabb.minZ, aabb.maxY, aabb.minX, aabb.maxZ);
            default:
                return null;
        }
    }

    public static RayTraceResult raytraceMultiAABB(List<AxisAlignedBB> aabbs, BlockPos pos, Vec3d start, Vec3d end) {
        List<RayTraceResult> list = Lists.newArrayList();

        list.addAll(aabbs.stream().map(axisalignedbb -> rayTrace2(pos, start, end, axisalignedbb)).collect(Collectors.toList()));

        RayTraceResult raytraceresult1 = null;
        double d1 = 0.0D;

        for(RayTraceResult raytraceresult : list) {
            if(raytraceresult != null) {
                double d0 = raytraceresult.hitVec.squareDistanceTo(end);

                if(d0 > d1) {
                    raytraceresult1 = raytraceresult;
                    d1 = d0;
                }
            }
        }

        return raytraceresult1;
    }

    private static RayTraceResult rayTrace2(BlockPos pos, Vec3d start, Vec3d end, AxisAlignedBB boundingBox) {
        Vec3d vec3d = start.subtract((double) pos.getX(), (double) pos.getY(), (double) pos.getZ());
        Vec3d vec3d1 = end.subtract((double) pos.getX(), (double) pos.getY(), (double) pos.getZ());
        RayTraceResult raytraceresult = boundingBox.calculateIntercept(vec3d, vec3d1);
        return raytraceresult == null ? null : new RayTraceResult(raytraceresult.hitVec.addVector((double) pos.getX(), (double) pos.getY(), (double) pos.getZ()), raytraceresult.sideHit, pos);
    }
}
