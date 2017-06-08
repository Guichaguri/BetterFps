package guichaguri.betterfps.patches.misc;

import guichaguri.betterfps.BetterFpsHelper;
import guichaguri.betterfps.special.Multithreading;
import guichaguri.betterfps.special.Multithreading.IMultithreaded;
import guichaguri.betterfps.transformers.Conditions;
import guichaguri.betterfps.transformers.annotations.Condition;
import guichaguri.betterfps.transformers.annotations.Copy;
import guichaguri.betterfps.transformers.annotations.Copy.Mode;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.client.util.ITooltipFlag.TooltipFlags;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextFormatting;

/**
 * The search algorithm is still the same
 * But the amount of work it has to do has been reduced
 * Before this improvement, the search would have to rebuild its results every time the text changed
 * Now, the search only rebuilds completely when necessary
 * It will also significantly reduce the amount of rebuilding work when adding/removing characters
 *
 * @author Guilherme Chaguri
 */
@Condition(Conditions.FAST_SEARCH)
public abstract class FastCreativeSearch extends GuiContainerCreative implements IMultithreaded {

    @Copy
    private Thread searchThread;

    @Copy
    private String oldSearchText;

    @Copy
    private NonNullList<ItemStack> itemBuffer;

    @Copy
    private boolean asyncSearch;

    public FastCreativeSearch(EntityPlayer player) {
        super(player);
    }

    @Copy(Mode.PREPEND)
    @Override
    public void initGui() {
        // When the Gui is initialized, let's initialize our variables

        asyncSearch = BetterFpsHelper.getConfig().asyncSearch;
        if(!asyncSearch) {
            if(itemBuffer == null) itemBuffer = NonNullList.create();
        }
    }

    @Copy(Mode.PREPEND)
    @Override
    public void setCurrentCreativeTab(CreativeTabs tab) {
        // When the tab changes, clear the search history so when the search field is shown again, it will rebuild the results

        if(asyncSearch) {
            Multithreading.stop(searchThread);
            searchThread = null;
        }
        oldSearchText = null;
    }

    @Copy(Mode.REPLACE)
    @Override
    public void updateCreativeSearch() {
        // TODO REDO
        if(asyncSearch) {
            Multithreading.stop(searchThread);
            searchThread = Multithreading.start(this, "search");
        } else {
            updateCreativeSearchSync();
        }
    }

    /**
     * Asynchronously updates the search
     */
    @Copy
    @Override
    public void run(String task) {
        // TODO REDO
        String search = searchField.getText().toLowerCase(Locale.ROOT);
        boolean rebuildCache = false;
        GuiContainerCreative.ContainerCreative container = (GuiContainerCreative.ContainerCreative)inventorySlots;
        GuiContainerCreative.ContainerCreative containerBuffer = new ContainerCreative(mc.player);
        List<ItemStack> itemBuffer = null;

        if(oldSearchText == null) {
            // The cache is null, rebuild it
            rebuildCache = true;
        } else if(search.equals(oldSearchText)) {
            // Text is the same - Update the screen
            currentScroll = 0.0F;
            container.scrollTo(0.0F);
            return;
        } else if(search.startsWith(oldSearchText)) {
            // Text added - Use current results and just refine them
            containerBuffer.itemList.addAll(container.itemList);
        } else if(oldSearchText.startsWith(search)) {
            // Text removed - Leave current results and look for new ones
            itemBuffer = new ArrayList<ItemStack>(container.itemList);
            updateBaseItems(containerBuffer);
            updateFilteredItems(containerBuffer);
        } else {
            // Unknown? - Rebuild cache
            rebuildCache = true;
        }

        if(rebuildCache) {
            // Rebuild results again
            containerBuffer.itemList.clear();
            updateBaseItems(containerBuffer);
            updateFilteredItems(containerBuffer);
        }

        // Update the search
        updateSearch(containerBuffer.itemList.iterator(), itemBuffer, search);

        // Invert Containers
        inventorySlots = containerBuffer;
        // Update the cache text
        oldSearchText = search;
        // Update the screen
        currentScroll = 0.0F;
        containerBuffer.scrollTo(0.0F);
    }

    /**
     * Synchronously update the search
     */
    @Copy
    public void updateCreativeSearchSync() {
        // TODO REDO
        String search = searchField.getText().toLowerCase(Locale.ROOT);
        boolean rebuildCache = false;
        GuiContainerCreative.ContainerCreative container = (GuiContainerCreative.ContainerCreative)inventorySlots;

        if(oldSearchText == null) {
            // The cache is null, rebuild it
            rebuildCache = true;
        } else if(search.equals(oldSearchText)) {
            // Text is the same - Do nothing
            return;
        } else if(search.startsWith(oldSearchText)) {
            // Text added - Use current results and just refine them
        } else if(oldSearchText.startsWith(search)) {
            // Text removed - Leave current results and look for new ones
            NonNullList<ItemStack> items = container.itemList;
            container.itemList = itemBuffer;
            itemBuffer = items;
            updateBaseItems(container);
            updateFilteredItems(container);
        } else {
            // Unknown? - Rebuild cache
            rebuildCache = true;
        }

        if(rebuildCache) {
            // Rebuild results again
            container.itemList.clear();
            updateBaseItems(container);
            updateFilteredItems(container);
        }

        // Update the search
        updateSearch(container.itemList.iterator(), itemBuffer, search);

        // Clean up the item buffer
        itemBuffer.clear();
        // Update the cache text
        oldSearchText = search;
        // Update the screen
        currentScroll = 0.0F;
        container.scrollTo(0.0F);
    }

    @Copy
    private void updateSearch(Iterator<ItemStack> iterator, List<ItemStack> itemBuffer, String search) {
        // TODO REDO
        boolean lookupBuffer = itemBuffer != null && !itemBuffer.isEmpty();
        EntityPlayer player = mc.player;
        TooltipFlags advancedTooltips = mc.gameSettings.advancedItemTooltips ? TooltipFlags.ADVANCED : TooltipFlags.NORMAL;

        while(iterator.hasNext()) {
            ItemStack itemstack = iterator.next();

            if(lookupBuffer && itemBuffer.contains(itemstack)) continue;

            boolean shouldRemove = true;
            for(String s : itemstack.getTooltip(player, advancedTooltips)) {
                if(TextFormatting.getTextWithoutFormattingCodes(s).toLowerCase(Locale.ROOT).contains(search)) {
                    shouldRemove = false;
                    break;
                }
            }
            if(shouldRemove) iterator.remove();
        }
    }

    @Copy
    private void updateBaseItems(GuiContainerCreative.ContainerCreative container) {
        NonNullList<ItemStack> list = container.itemList;
        CreativeTabs tab = CreativeTabs.CREATIVE_TAB_ARRAY[selectedTabIndex];

        tab.displayAllRelevantItems(list);

        // Let's not add all items when we're not in the search tab
        // Custom search tabs (with a feature added by Forge) should work as expected
        // Vanilla should always use the search tab for searching
        if(tab != CreativeTabs.SEARCH) return;

        for(Item item : Item.REGISTRY) {
            if(item != null && item.getCreativeTab() != null) {
                item.getSubItems(tab, list);
            }
        }
    }

    /**
     * Forge has a method with the exact same name and descriptor as this one which works with custom search tabs.
     * To prevent conflicts, this is set to copy instead of replacing, so Forge should overwrite this method
     */
    @Copy(Mode.COPY)
    private void updateFilteredItems(GuiContainerCreative.ContainerCreative container) {
        NonNullList<ItemStack> list = container.itemList;

        for(Enchantment enchantment : Enchantment.REGISTRY) {
            if(enchantment != null && enchantment.type != null) {
                //TODO
                //Items.ENCHANTED_BOOK.getAll(enchantment, list);
            }
        }
    }
}
