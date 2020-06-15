package rustichromia.util;

import com.google.common.collect.Lists;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.oredict.OreDictionary;
import org.lwjgl.util.vector.Quaternion;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static net.minecraft.util.EnumFacing.Axis.X;

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

    public static float lerpAngle(float a, float b, float slide) {
        return a + angleDistance(a,b)*slide;
    }

    public static float angleDistance(float a, float b) {
        float max = 360;
        float da = (b - a) % max;
        return 2*da % max - da;
    }

    public static Quaternion slerp(Quaternion a, Quaternion b, float slide) {
        a = a.normalise(null);
        b = b.normalise(null);

        if(slide <= 0)
            return a;
        if(slide >= 1)
            return b;

        float dot = Quaternion.dot(a,b);

        if(dot < 0)
        {
            a = new Quaternion(-a.x,-a.y,-a.z,-a.w);
            dot = -dot;
        }

        final float dotThreshold = 0.9995f;
        if(dot > dotThreshold) {
            Quaternion result = new Quaternion(
                    (float)MathHelper.clampedLerp(a.x,b.x,slide),
                    (float)MathHelper.clampedLerp(a.y,b.y,slide),
                    (float)MathHelper.clampedLerp(a.z,b.z,slide),
                    (float)MathHelper.clampedLerp(a.w,b.w,slide)
            );
            return result.normalise(null);
        }

        double theta0 = Math.acos(dot);
        double theta = theta0 * slide;
        double sin_theta = Math.sin(theta);
        double sin_theta0 = Math.sin(theta0);

        float s0 = (float) (Math.cos(theta) - dot * sin_theta / sin_theta0);  // == sin(theta_0 - theta) / sin(theta_0)
        float s1 = (float) (sin_theta / sin_theta0);

        return new Quaternion(
                a.x * s0 + b.x * s1,
                a.y * s0 + b.y * s1,
                a.z * s0 + b.z * s1,
                a.w * s0 + b.w * s1
        );
    }

    public static EnumFacing getFaceOrientation(double u, double v) {
        u -= 0.5;
        v -= 0.5;

        if(Math.abs(u) > Math.abs(v)) {
            if(u > 0)
                return EnumFacing.EAST;
            else
                return EnumFacing.WEST;
        } else {
            if(v > 0)
                return EnumFacing.SOUTH;
            else
                return EnumFacing.NORTH;
        }
    }

    public static EnumFacing mapOrientationToFacing(EnumFacing.Axis u, EnumFacing.Axis v, EnumFacing orientation) {
        if(orientation.getAxis() == EnumFacing.Axis.X) {
            return EnumFacing.getFacingFromAxis(orientation.getAxisDirection(), u);
        } else {
            return EnumFacing.getFacingFromAxis(orientation.getAxisDirection(), v);
        }
    }

    public static EnumFacing getFaceOrientation(EnumFacing side, float hitX, float hitY, float hitZ) {
        EnumFacing controlFacing = null;
        switch (side.getAxis()) {
            case X:
                controlFacing = getFaceOrientation(hitY, hitZ);
                break;
            case Y:
                controlFacing = getFaceOrientation(hitX, hitZ);
                break;
            case Z:
                controlFacing = getFaceOrientation(hitX, hitY);
                break;
        }
        return controlFacing;
    }

    public static EnumFacing getTrueFacing(EnumFacing orientation, EnumFacing base) {
        switch (base.getAxis()) {
            case X:
                return mapOrientationToFacing(EnumFacing.Axis.Y, EnumFacing.Axis.Z, orientation);
            case Y:
                return mapOrientationToFacing(X, EnumFacing.Axis.Z, orientation);
            case Z:
                return mapOrientationToFacing(X, EnumFacing.Axis.Y, orientation);
            default:
                return null;
        }
    }

    public static EnumFacing turn(EnumFacing facing, EnumFacing.Axis axis, int amount) {
        amount = (amount % 4 + 4) % 4;
        for(int i = 0; i < amount; i++)
            facing = facing.rotateAround(axis);
        return facing;
    }
}
