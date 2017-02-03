package guichaguri.betterfps.patches.misc;

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
        if(itemBuffer == null) itemBuffer = NonNullList.create();
    }

    @Copy(Mode.PREPEND)
    @Override
    public void setCurrentCreativeTab(CreativeTabs tab) {
        oldSearchText = null;
    }

    @Copy(Mode.REPLACE)
    @Override
    public void updateCreativeSearch() {
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
            addAllItemsToSearch(container.itemList);
        } else {
            // Unknown? - Rebuild cache
            rebuildCache = true;
        }

        if(rebuildCache) {
            // Rebuild results again
            container.itemList.clear();
            addAllItemsToSearch(container.itemList);
        }

        Iterator<ItemStack> iterator = container.itemList.iterator();
        boolean lookupBuffer = !itemBuffer.isEmpty();

        while(iterator.hasNext()) {
            ItemStack itemstack = iterator.next();

            if(lookupBuffer && itemBuffer.contains(itemstack)) continue;

            for(String s : itemstack.getTooltip(this.mc.player, this.mc.gameSettings.advancedItemTooltips)) {
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
    private void addAllItemsToSearch(NonNullList<ItemStack> items) {
        for(Item item : Item.REGISTRY) {
            if(item != null && item.getCreativeTab() != null) {
                item.getSubItems(item, null, items);
            }
        }

        for(Enchantment enchantment : Enchantment.REGISTRY) {
            if(enchantment != null && enchantment.type != null) {
                Items.ENCHANTED_BOOK.getAll(enchantment, items);
            }
        }
    }
}
