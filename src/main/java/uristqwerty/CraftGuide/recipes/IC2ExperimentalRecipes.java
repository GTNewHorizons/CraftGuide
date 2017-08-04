package uristqwerty.CraftGuide.recipes;

import ic2.api.item.IC2Items;
import ic2.api.recipe.IMachineRecipeManager;
import ic2.api.recipe.IMachineRecipeManager.RecipeIoContainer;
import ic2.api.recipe.IRecipeInput;
import ic2.api.recipe.IScrapboxManager;
import ic2.api.recipe.Recipes;
import ic2.core.recipe.AdvRecipe;
import ic2.core.recipe.AdvShapelessRecipe;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidContainerRegistry.FluidContainerData;
import net.minecraftforge.oredict.OreDictionary;
import uristqwerty.CraftGuide.api.ChanceSlot;
import uristqwerty.CraftGuide.api.ConstructedRecipeTemplate;
import uristqwerty.CraftGuide.api.CraftGuideAPIObject;
import uristqwerty.CraftGuide.api.EUSlot;
import uristqwerty.CraftGuide.api.ExtraSlot;
import uristqwerty.CraftGuide.api.ItemSlot;
import uristqwerty.CraftGuide.api.RecipeGenerator;
import uristqwerty.CraftGuide.api.RecipeProvider;
import uristqwerty.CraftGuide.api.RecipeTemplate;
import uristqwerty.CraftGuide.api.Slot;
import uristqwerty.CraftGuide.api.SlotType;
import uristqwerty.CraftGuide.api.StackInfo;

public class IC2ExperimentalRecipes extends CraftGuideAPIObject implements RecipeProvider
{
	public static interface AdditionalMachines
	{
		public Object[] extraMacerators();
		public Object[] extraExtractors();
		public Object[] extraCompressors();
	}

	public static List<AdditionalMachines> additionalMachines = new ArrayList<>();

	public IC2ExperimentalRecipes()
	{
		StackInfo.addSource(new IC2GeneratorFuel());
		StackInfo.addSource(new IC2Power());
		StackInfo.addSource(new IC2ExperimentalAmplifiers());
	}

	@Override
	public void generateRecipes(RecipeGenerator generator)
	{
		addCraftingRecipes(generator);

		addMachineRecipes(generator, IC2Items.getItem("macerator"), getMacerator(), Recipes.macerator);
		addMachineRecipes(generator, IC2Items.getItem("extractor"), getExtractor(), Recipes.extractor);
		addMachineRecipes(generator, IC2Items.getItem("compressor"), getCompressor(), Recipes.compressor);
		addMachineRecipes(generator, IC2Items.getItem("centrifuge"), Recipes.centrifuge);
		addMachineRecipes(generator, IC2Items.getItem("blockcutter"), Recipes.blockcutter);
		addMachineRecipes(generator, IC2Items.getItem("blastfurance"), Recipes.blastfurnace);
		addMachineRecipes(generator, IC2Items.getItem("recycler"), Recipes.recycler);
		addMachineRecipes(generator, IC2Items.getItem("metalformer"), Recipes.metalformerExtruding);
		addMachineRecipes(generator, IC2Items.getItem("metalformer"), Recipes.metalformerCutting);
		addMachineRecipes(generator, IC2Items.getItem("metalformer"), Recipes.metalformerRolling);
		addMachineRecipes(generator, IC2Items.getItem("orewashingplant"), Recipes.oreWashing);

		addScrapboxOutput(generator, IC2Items.getItem("scrapBox"), Recipes.scrapboxDrops);
	}

	private Object getMacerator()
	{
		ArrayList<Object> macerator = new ArrayList<>();
		macerator.add(IC2Items.getItem("macerator"));

		for(AdditionalMachines additional: additionalMachines)
		{
			Object machines[] = additional.extraMacerators();

			if(machines != null)
			{
				for(Object machine: machines)
				{
					macerator.add(machine);
				}
			}
		}

		return macerator;
	}

	private Object getExtractor()
	{
		ArrayList<Object> extractor = new ArrayList<>();
		extractor.add(IC2Items.getItem("extractor"));

		for(AdditionalMachines additional: additionalMachines)
		{
			Object machines[] = additional.extraExtractors();

			if(machines != null)
			{
				for(Object machine: machines)
				{
					extractor.add(machine);
				}
			}
		}

		return extractor;
	}

	private Object getCompressor()
	{
		ArrayList<Object> compressor = new ArrayList<>();
		compressor.add(IC2Items.getItem("compressor"));

		for(AdditionalMachines additional: additionalMachines)
		{
			Object machines[] = additional.extraCompressors();

			if(machines != null)
			{
				for(Object machine: machines)
				{
					compressor.add(machine);
				}
			}
		}

		return compressor;
	}

	private void addMachineRecipes(RecipeGenerator generator, ItemStack type, IMachineRecipeManager recipeManager)
	{
		addMachineRecipes(generator, type, type, recipeManager);
	}

	private void addMachineRecipes(RecipeGenerator generator, ItemStack type, Object machine, IMachineRecipeManager recipeManager)
	{
		addMachineRecipes(generator, type, machine, recipeManager, 2, 800);
	}

	private void addMachineRecipes(RecipeGenerator generator, ItemStack type, Object machine, IMachineRecipeManager recipeManager, int eut, int totalEU)
	{
		if(recipeManager == null || recipeManager.getRecipes() == null)
			return;

		int maxOutput = 1;

		for(RecipeIoContainer recipe: recipeManager.getRecipes())
		{
			maxOutput = Math.max(maxOutput, recipe.output.items.size());
		}

		int columns = (maxOutput+1) / 2;

		Slot[] recipeSlots = new Slot[maxOutput + 3];

		recipeSlots[0] = new ItemSlot(columns > 1? 3 : 12, 21, 16, 16, true).drawOwnBackground();
		recipeSlots[1] = new ExtraSlot(columns > 1? 23 : 31, 30, 16, 16, machine).clickable().showName().setSlotType(SlotType.MACHINE_SLOT);
		recipeSlots[2] = new EUSlot(columns > 1? 23 : 31, 12).setConstantPacketSize(eut).setConstantEUValue(-totalEU);

		for(int i = 0; i < maxOutput/2; i++)
		{
			recipeSlots[i * 2 + 3] = new ItemSlot((columns > 1? 41 : 50) + i * 18, 12, 16, 16, true).setSlotType(SlotType.OUTPUT_SLOT).drawOwnBackground();
			recipeSlots[i * 2 + 4] = new ItemSlot((columns > 1? 41 : 50) + i * 18, 30, 16, 16, true).setSlotType(SlotType.OUTPUT_SLOT).drawOwnBackground();
		}

		if((maxOutput & 1) == 1)
		{
			recipeSlots[columns * 2 + 1] = new ItemSlot((columns > 1? 23 : 32) + columns * 18, 21, 16, 16, true).setSlotType(SlotType.OUTPUT_SLOT).drawOwnBackground();
		}

		RecipeTemplate template = generator.createRecipeTemplate(recipeSlots, type);

		for(RecipeIoContainer recipe: recipeManager.getRecipes())
		{
			ArrayList<ItemStack> inputs = new ArrayList<>();

			for(ItemStack s: recipe.input.getInputs())
			{
				ItemStack stack = s.copy();
				stack.stackSize = recipe.input.getAmount();
				inputs.add(stack);
			}

			Object[] recipeContents = new Object[maxOutput + 3];
			recipeContents[0] = inputs;
			recipeContents[1] = machine;
			recipeContents[2] = null;
			List<ItemStack> output = recipe.output.items;

			for(int i = 0; i < Math.min(maxOutput, output.size()); i++)
			{
				recipeContents[i + 3] = output.get(i);
			}

			generator.addRecipe(template, recipeContents);
		}
	}

	private void addScrapboxOutput(RecipeGenerator generator, ItemStack scrapbox, IScrapboxManager scrapboxDrops)
	{
		Slot[] recipeSlots = new Slot[]{
				new ExtraSlot(18, 21, 16, 16, scrapbox).clickable().showName().setSlotType(SlotType.INPUT_SLOT),
				new ChanceSlot(44, 21, 16, 16, true).setFormatString(" (%1$.3f%% chance)").setRatio(100000).setSlotType(SlotType.OUTPUT_SLOT).drawOwnBackground(),
		};

		RecipeTemplate template = generator.createRecipeTemplate(recipeSlots, scrapbox);

		for(Entry<ItemStack, Float> entry: scrapboxDrops.getDrops().entrySet())
		{
			Object[] recipeContents = new Object[]{
					scrapbox,
					new Object[]{
							entry.getKey(),
							(int)(entry.getValue() * 100000),
					},
			};

			generator.addRecipe(template, recipeContents);
		}
	}

	private void addCraftingRecipes(RecipeGenerator generator)
	{
		ItemStack workbench = new ItemStack(Blocks.crafting_table);
		ConstructedRecipeTemplate smallShaped = generator.buildTemplate(workbench)
				.shapedItemGrid(2, 2).nextColumn(1)
				.outputItem()
				.finishTemplate();

		ConstructedRecipeTemplate shaped = generator.buildTemplate(workbench)
				.shapedItemGrid(3, 3).nextColumn(1)
				.outputItem()
				.finishTemplate();

		ConstructedRecipeTemplate shapeless = generator.buildTemplate(workbench)
				.shapelessItemGrid(3, 3).nextColumn(1)
				.outputItem()
				.finishTemplate();

		for(IRecipe r: CraftingManager.getInstance().getRecipeList())
		{
			if(r instanceof AdvShapelessRecipe)
			{
				AdvShapelessRecipe recipe = (AdvShapelessRecipe)r;
				if(recipe.canShow())
				{
					shapeless.buildRecipe()
						.shapelessItemGrid(resolveAll(recipe.input))
						.item(recipe.getRecipeOutput())
						.addRecipe(generator);
				}
			}
			else if(r instanceof AdvRecipe)
			{
				AdvRecipe recipe = (AdvRecipe)r;
				if(recipe.canShow())
				{
					int width = recipe.inputWidth;
					int height = recipe.inputHeight;
					Object[] input = expandInput(recipe.input, width, recipe.masks[0]);
					ConstructedRecipeTemplate template = width < 3 && height < 3? smallShaped : shaped;

					template.buildRecipe()
						.shapedItemGrid(width, height, resolveAll(input))
						.item(recipe.getRecipeOutput())
						.addRecipe(generator);
				}
			}
		}
	}

	private Object[] expandInput(Object[] input, int width, int mask)
	{
		int height = ((mask & 0x007) != 0? 1 : 0) + ((mask & 0x038) != 0? 1 : 0) + ((mask & 0x1c0) != 0? 1 : 0);

		Object[] expanded = new Object[width * height];
		int i = 0;
		for(int y = 0; y < height; y++)
		{
			for(int x = 0; x < width; x++)
			{
				if((mask & (1 << (8 - (y * 3 + x)))) != 0)
				{
					expanded[y * width + x] = input[i++];
				}
			}
		}

		return expanded;
	}

	private Object[] resolveAll(Object[] input)
	{
		Object ret[] = new Object[input.length];
		for(int i = 0; i< input.length; i++)
		{
			ret[i] = resolve(input[i]);
		}
		return ret;
	}

	private Object resolve(Object item)
	{
		if(item instanceof String)
		{
			String itemString = (String)item;

			if(itemString.startsWith("liquid$"))
			{
				String fluidName = itemString.substring(7);

				ArrayList<Object> containers = new ArrayList<>();
				for(FluidContainerData container: FluidContainerRegistry.getRegisteredFluidContainerData())
				{
					if(container.fluid.getFluid().getName().equals(fluidName))
					{
						containers.add(container.filledContainer);
					}
				}

				return containers;
			}
			else
			{
				return OreDictionary.getOres(itemString);
			}
		}
		else if(item instanceof ItemStack)
		{
			return item;
		}
		else if(item instanceof IRecipeInput)
		{
			return ((IRecipeInput)item).getInputs();
		}
		else if(item instanceof List)
		{
			boolean containsItemStacks = true;

			for(Object o: (List<?>)item)
			{
				if(!(o instanceof ItemStack))
				{
					containsItemStacks = false;
					break;
				}
			}

			if(containsItemStacks)
				return item;

			ArrayList<Object> newlist = new ArrayList<>(((List<?>)item).size());

			for(Object o: (List<?>)item)
			{
				Object r = resolve(o);

				if(r instanceof Collection)
				{
					newlist.addAll((Collection<?>)r);
				}
				else
				{
					newlist.add(r);
				}
			}

			return newlist;
		}
		else if(item instanceof Iterable)
		{
			ArrayList<Object> newlist = new ArrayList<>();

			for(Object o: (Iterable<?>)item)
			{
				Object r = resolve(o);

				if(r instanceof Collection)
				{
					newlist.addAll((Collection<?>)r);
				}
				else
				{
					newlist.add(r);
				}
			}

			return newlist;
		}
		else if(item != null && item.getClass().isArray())
		{
			ArrayList<Object> newlist = new ArrayList<>();

			for(Object o: (Object[])item)
			{
				Object r = resolve(o);

				if(r instanceof Collection)
				{
					newlist.addAll((Collection<?>)r);
				}
				else
				{
					newlist.add(r);
				}
			}

			return newlist;
		}
		else
		{
			return null;
		}
	}
}
