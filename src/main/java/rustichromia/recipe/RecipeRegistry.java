package rustichromia.recipe;

import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import rustichromia.Rustichromia;

public class RecipeRegistry {
    @SubscribeEvent
    public void registerRecipes(RegistryEvent.Register<IRecipe> event) {
        event.getRegistry().register(new RecipeCombine().setRegistryName(new ResourceLocation(Rustichromia.MODID,"combine")));
    }
}
