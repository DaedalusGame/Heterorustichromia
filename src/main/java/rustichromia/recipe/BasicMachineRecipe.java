package rustichromia.recipe;

import com.google.common.collect.Lists;
import mysticalmechanics.api.IMechUnit;
import mysticalmechanics.api.MysticalMechanicsAPI;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import rustichromia.util.Result;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class BasicMachineRecipe {
    public ResourceLocation id;
    public double minPower, maxPower;
    public double time;

    public BasicMachineRecipe(ResourceLocation id, double minPower, double maxPower, double time) {
        this.id = id;
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

    public List<String> getBasePowerData() {
        List<String> tooltip = new ArrayList<>();
        IMechUnit unit = MysticalMechanicsAPI.IMPL.getDefaultUnit();
        if (minPower > 0)
            tooltip.add(String.format("Lower: %s", unit.format(minPower)));
        if (!Double.isInfinite(maxPower))
            tooltip.add(String.format("Upper: %s", unit.format(maxPower)));
        return tooltip.isEmpty() ? null : tooltip;
    }

    public List<String> getPowerData() {
        return null;
    }

    public List<String> getExtraData() {
        return null;
    }

    protected List<Result> transformResults(Collection<Result> results) {
        return results.stream().map(Result::transform).filter(x -> !x.isEmpty()).collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return id.toString();
    }
}
