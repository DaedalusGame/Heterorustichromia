package rustichromia.cart;

import net.minecraft.util.ResourceLocation;

import java.util.function.Supplier;

public abstract class CartContentSupplier implements Supplier<CartContent> {
    final ResourceLocation resourceLocation;

    public CartContentSupplier(ResourceLocation resourceLocation) {
        this.resourceLocation = resourceLocation;
    }

    public ResourceLocation getResourceLocation() {
        return resourceLocation;
    }
}
