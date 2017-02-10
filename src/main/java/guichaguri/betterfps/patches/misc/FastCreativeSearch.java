package guichaguri.betterfps.patches.misc;

import guichaguri.betterfps.transformers.Conditions;
import guichaguri.betterfps.transformers.annotations.Condition;
import guichaguri.betterfps.transformers.annotations.Copy;
import guichaguri.betterfps.transformers.annotations.Copy.Mode;
import java.util.Iterator;
import java.util.Locale;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextFormatting;

/**
 * @author Guilherme Chaguri
 */
@Condition(Conditions.FAST_SEARCH)
public abstract class FastCreativeSearch extends GuiContainerCreative {
    @Copy
    private String oldSearchText;
    @Copy
    private NonNullList<ItemStack> itemBuffer;

    public FastCreativeSearch(EntityPlayer player) {
        super(player);
    }

    @Copy(Mode.PREPEND)
    @Override
    public void initGui() {
        // When the Gui is initialized, let's create our buffer list

        if(itemBuffer == null) itemBuffer = NonNullList.create();
    }

    @Copy(Mode.PREPEND)
    @Override
    public void setCurrentCreativeTab(CreativeTabs tab) {
        // When the tab changes, clear the search history so when the search field is shown again, it will rebuild the results

        oldSearchText = null;
    }

    @Copy(Mode.REPLACE)
    @Override
    public void updateCreativeSearch() {
        // The search algorithm is still the same
        // But the amount of work it has to do has been reduced
        // Before this improvement, the search would have to rebuild its results everytime the text changed
        // Now, the search only rebuilds completely when necessary
        // It will also significantly reduce the amount of rebuilding work when adding/removing characters

        String search = this.searchField.getText().toLowerCase(Locale.ROOT);
        boolean rebuildCache = false;
        GuiContainerCreative.ContainerCreative container = (GuiContainerCreative.ContainerCreative)this.inventorySlots;

        if(oldSearchText == null) {
            // The cache is null, rebuild it
            rebuildCache = true;
        } else if(search.equals(oldSearchText)) {
            // Text is the same - do nothing
            return;
        } else if(search.startsWith(oldSearchText)) {
            // Text added - Use current results and just refine them
        } else if(oldSearchText.startsWith(search)) {
            // Text removed - Ignore current results and look for new ones
            NonNullList<ItemStack> items = container.itemList;
            container.itemList = itemBuffer;
            itemBuffer = items;
            updateAdditionalItems(container);
            updateFilteredItems(container);
        } else {
            // Unknown? - Rebuild cache
            rebuildCache = true;
        }

        if(rebuildCache) {
            // Rebuild results again
            container.itemList.clear();
            updateAdditionalItems(container);
            updateFilteredItems(container);
        }

        Iterator<ItemStack> iterator = container.itemList.iterator();
        boolean lookupBuffer = !itemBuffer.isEmpty();
        EntityPlayer player = mc.player;
        boolean advancedTooltips = mc.gameSettings.advancedItemTooltips;

        while(iterator.hasNext()) {
            ItemStack itemstack = iterator.next();

            if(lookupBuffer && itemBuffer.contains(itemstack)) continue;

            for(String s : itemstack.getTooltip(player, advancedTooltips)) {
                if(!TextFormatting.getTextWithoutFormattingCodes(s).toLowerCase(Locale.ROOT).contains(search)) {
                    iterator.remove();
                    break;
                }
            }
        }

        if(lookupBuffer) itemBuffer.clear();
        oldSearchText = search;

        this.currentScroll = 0.0F;
        container.scrollTo(0.0F);
    }

    @Copy
    private void updateAdditionalItems(GuiContainerCreative.ContainerCreative container) {
        CreativeTabs.CREATIVE_TAB_ARRAY[selectedTabIndex].displayAllRelevantItems(container.itemList);
    }

    /**
     * Forge has a method with the exact same name and descriptor as this one which works with custom search tabs.
     * To prevent conflicts, this is set to copy instead of replacing, so Forge should overwrite this method
     */
    @Copy(Mode.COPY)
    private void updateFilteredItems(GuiContainerCreative.ContainerCreative container) {
        NonNullList<ItemStack> list = container.itemList;

        for(Item item : Item.REGISTRY) {
            if(item != null && item.getCreativeTab() != null) {
                item.getSubItems(item, null, list);
            }
        }

        for(Enchantment enchantment : Enchantment.REGISTRY) {
            if(enchantment != null && enchantment.type != null) {
                Items.ENCHANTED_BOOK.getAll(enchantment, list);
            }
        }
    }
}
