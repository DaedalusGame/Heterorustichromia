package rustichromia.item;

import mysticalmechanics.api.IMechUnit;
import mysticalmechanics.api.MysticalMechanicsAPI;
import net.minecraft.client.resources.I18n;

public class ItemDisk extends ItemAdditive {
    public ItemDisk(int defaultAmount) {
        super(defaultAmount);
    }

    @Override
    public String formatAmount(int amount) {
        IMechUnit unit = MysticalMechanicsAPI.IMPL.getDefaultUnit();
        if(unit != null)
            return I18n.format("rustichromia.tooltip."+getUnlocalizedName(),unit.format(amount));
        else
            return "";
    }
}
