/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.panryba.mc.stonedrop;

import java.util.Collection;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author PanRyba.pl
 */
public class StoneDropListener implements Listener {

    private final PluginApi api;
    private final Material replacement;

    public StoneDropListener(PluginApi api) {
        this.api = api;
        this.replacement = api.getDisabledReplacement();
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onExplosion(EntityExplodeEvent event) {
        for (Block block : event.blockList()) {
            if (api.shouldDisableDrop(block)) {
                block.setType(replacement);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBreak(BlockBreakEvent event) {
        if (event == null) {
            return;
        }

        if (event.isCancelled()) {
            return;
        }

        Player player = event.getPlayer();
        if (player == null) {
            return;
        }

        if (player.getGameMode() == GameMode.CREATIVE) {
            return;
        }

        Block block = event.getBlock();
        if (block == null) {
            return;
        }

        if (api.shouldDisableDrop(block)) {
            event.setCancelled(true);
            block.setType(this.replacement);
            player.sendMessage(ChatColor.GRAY + "Ten blok wypada tylko z kamienia - zobacz /kamien");
            return;
        }

        boolean isStone = block.getType() == Material.STONE;
        ItemStack inHand = player.getItemInHand();

        if (isStone) {
            int maxItems = 1;

            if (inHand != null) {
                maxItems += inHand.getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS);
            }

            api.giveExp(player, block);
            api.updateDrop(player, block, maxItems);
        }

        switch (block.getType()) {
            case STONE:
            case COBBLESTONE:
                if (isStone || block.getType() == Material.COBBLESTONE) {
                    event.setCancelled(true);
                    block.setType(Material.AIR);

                    api.damageItemInHands(player, inHand);

                    if (!api.hasBlockedCobble(player)) {
                        ItemStack drop;
                        if (isStone && inHand != null && inHand.getEnchantmentLevel(Enchantment.SILK_TOUCH) > 0) {
                            drop = new ItemStack(Material.STONE, 1);
                        } else {
                            drop = new ItemStack(Material.COBBLESTONE, 1);
                        }

                        Map<Integer, ItemStack> failed = player.getInventory().addItem(drop);
                        for (ItemStack fail : failed.values()) {
                            player.getWorld().dropItemNaturally(block.getLocation(), fail);
                        }
                    }
                }
                break;
            case OBSIDIAN:
            case REDSTONE_BLOCK:
            case WOOD:
            case LOG:
            case REDSTONE_WIRE:
            case PISTON_BASE:
            case PISTON_STICKY_BASE:
            case TORCH:
            case REDSTONE_TORCH_OFF:
            case REDSTONE_TORCH_ON:
                event.setCancelled(true);

                Collection<ItemStack> drops = block.getDrops(inHand);
                block.setType(Material.AIR);

                api.damageItemInHands(player, inHand);
                
                for (ItemStack drop : drops) {
                    Map<Integer, ItemStack> failed = player.getInventory().addItem(drop);
                    for (ItemStack fail : failed.values()) {
                        player.getWorld().dropItemNaturally(block.getLocation(), fail);
                    }
                }
                break;
            default:
                break;
        }
    }
}
