package rustichromia.compat.crafttweaker;

import crafttweaker.IAction;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.IItemStack;
import crafttweaker.mc1120.CraftTweaker;
import net.minecraft.util.ResourceLocation;
import rustichromia.recipe.AssemblerRecipe;
import rustichromia.recipe.QuernRecipe;
import rustichromia.recipe.RecipeRegistry;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenRegister
@ZenClass(Assembler.CLASS)
public class Assembler {
    public static final String NAME = "Assembler";
    public static final String CLASS = "mods.rustichromia.Assembler";

    @ZenMethod
    public static void add(String id, int tier, IIngredient[] inputs, IItemStack[] outputs, double minPower, double maxPower, double time) {
        AssemblerRecipe recipe = new AssemblerRecipe(new ResourceLocation(CraftTweaker.MODID,id), tier, CTUtil.toIngredients(inputs), CTUtil.toItemStacks(outputs), minPower, maxPower, time);
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
        AssemblerRecipe recipe;

        public Add(AssemblerRecipe recipe) {
            this.recipe = recipe;
        }

        @Override
        public void apply() {
            RecipeRegistry.assemblerRecipes.add(recipe);
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
            RecipeRegistry.assemblerRecipes.removeIf(recipe -> recipe.id == id);
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
            RecipeRegistry.assemblerRecipes.clear();
        }

        @Override
        public String describe() {
            return String.format("Removing all %s recipes",NAME);
        }
    }
}
