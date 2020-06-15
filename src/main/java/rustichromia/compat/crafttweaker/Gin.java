package rustichromia.compat.crafttweaker;

import crafttweaker.IAction;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.IItemStack;
import crafttweaker.mc1120.CraftTweaker;
import net.minecraft.util.ResourceLocation;
import rustichromia.recipe.GinRecipe;
import rustichromia.recipe.RecipeRegistry;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenRegister
@ZenClass(Gin.CLASS)
public class Gin {
    public static final String NAME = "Gin";
    public static final String CLASS = "mods.rustichromia.Gin";

    @ZenMethod
    public static void add(String id, IIngredient[] inputs, Object[] internal, Object[] external, double minPower, double maxPower, double time) {
        GinRecipe recipe = new GinRecipe(new ResourceLocation(CraftTweaker.MODID,id), CTUtil.toIngredients(inputs),  CTUtil.toResults(internal), CTUtil.toResults(external), minPower, maxPower, time);
        CraftTweaker.LATE_ACTIONS.add(new Add(recipe));
    }

    @ZenMethod
    public static void remove(String id) {
        CraftTweaker.LATE_ACTIONS.add(new Remove(new ResourceLocation(id)));
    }

    @ZenMethod
    public static void removeAll() {
        CraftTweaker.LATE_ACTIONS.add(new RemoveAll());
    }

    public static class Add implements IAction {
        GinRecipe recipe;

        public Add(GinRecipe recipe) {
            this.recipe = recipe;
        }

        @Override
        public void apply() {
            RecipeRegistry.ginRecipes.add(recipe);
        }

        @Override
        public String describe() {
            return String.format("Adding %s recipe: %s",NAME,recipe.toString());
        }
    }

    public static class Remove implements IAction {
        ResourceLocation id;

        public Remove(ResourceLocation id) {
            this.id = id;
        }

        @Override
        public void apply() {
            RecipeRegistry.ginRecipes.removeIf(recipe -> recipe.id.equals(id));
        }

        @Override
        public String describe() {
            return String.format("Removing %s recipe: %s",NAME,id.toString());
        }
    }

    public static class RemoveAll implements IAction {
        public RemoveAll() {
        }

        @Override
        public void apply() {
            RecipeRegistry.ginRecipes.clear();
        }

        @Override
        public String describe() {
            return String.format("Removing all %s recipes",NAME);
        }
    }
}
