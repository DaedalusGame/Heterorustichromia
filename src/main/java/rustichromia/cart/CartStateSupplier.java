package rustichromia.cart;

import net.minecraft.util.ResourceLocation;

import java.util.function.Supplier;

public abstract class CartStateSupplier implements Supplier<CartState> {
    final ResourceLocation resourceLocation;

    public CartStateSupplier(ResourceLocation resourceLocation) {
        this.resourceLocation = resourceLocation;
    }

    public ResourceLocation getResourceLocation() {
        return resourceLocation;
    }
}
