package rustichromia.cart;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

public abstract class ControlSupplier implements Supplier<Control> {
    final ResourceLocation resourceLocation;
    final String unlocalizedName;
    final ModelResourceLocation modelLocation;

    protected ControlSupplier(ResourceLocation resourceLocation, String unlocalizedName, ModelResourceLocation modelLocation) {
        this.resourceLocation = resourceLocation;
        this.unlocalizedName = unlocalizedName;
        this.modelLocation = modelLocation;
    }

    public ResourceLocation getResourceLocation() {
        return resourceLocation;
    }

    public String getUnlocalizedName() {
        return unlocalizedName;
    }

    @Nonnull
    public ModelResourceLocation getModelVariant() {
        return modelLocation;
    }

    public ResourceLocation getModel() {
        return new ResourceLocation(modelLocation.getResourceDomain(),modelLocation.getResourcePath());
    }
}
