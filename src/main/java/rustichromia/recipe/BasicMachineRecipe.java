package rustichromia.recipe;

import com.google.common.collect.Lists;
import net.minecraft.util.math.MathHelper;

import java.util.List;

public class BasicMachineRecipe {
    public double minPower, maxPower;
    public double time;

    public BasicMachineRecipe(double minPower, double maxPower, double time) {
        this.minPower = minPower;
        this.maxPower = maxPower;
        this.time = time;
    }

    public double getVisiblePower() {
        return MathHelper.clamp(getStandardPower(),minPower,maxPower);
    }

    protected double getStandardPower() {
        return 1;
    }

    public double getTime() {
        return time;
    }

    public double getSpeed(double power) {
        return power;
    }

    public List<String> getPowerData() {
        return null;
    }

    public List<String> getExtraData() {
        return null;
    }
}
