/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.panryba.mc.stonedrop;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
//import pl.panryba.mc.exp.ExpHelper;
//import pl.panryba.mc.exp.FishExpEvent;

/**
 *
 * @author PanRyba.pl
 */
public class PluginApi {

    private final Random r;
    private final RandomDrop randomDrop;
    private final Map<Material, Integer> stats;
    private final Set<Player> blockedCobble;
    private final Plugin plugin;
    private final boolean updateFishExp;

    public PluginApi(Plugin plugin, DropConfig defaultConfig) {
        this.plugin = plugin;
        this.updateFishExp = this.plugin.getConfig().getBoolean("update_fish_exp", false);
        
        this.r = new Random();
        this.randomDrop = new RandomDrop(defaultConfig);
        this.stats = new EnumMap<>(Material.class);
        this.blockedCobble = new HashSet<>();
    }

    void updateDrop(Player player, Block block, int maxItems) {
        ItemStack itemInHand = player.getItemInHand();
        DropInfo newDrop = randomDrop.generateForBlock(block, itemInHand);
        if (newDrop == null) {
            return;
        }

        Location blockLoc = block.getLocation();
        World world = blockLoc.getWorld();

        ItemStack item = newDrop.produceStack();

        int itemsInStack = 1;
        if (r.nextInt(2) == 0) {
            itemsInStack += r.nextInt(maxItems);
        }

        item.setAmount(itemsInStack);

        HashMap<Integer, ItemStack> failed = player.getInventory().addItem(item);
        for (ItemStack stack : failed.values()) {
            world.dropItemNaturally(blockLoc, stack);
        }

        String name = newDrop.getDeclination().getBiernik();
        if (item.getAmount() > 1) {
            player.sendMessage(ChatColor.BLUE + "Znalazles " + name + " x " + item.getAmount() + "!");
        } else {
            player.sendMessage(ChatColor.BLUE + "Znalazles " + name + "!");
        }

        Material type = item.getType();
        Integer current = this.stats.get(type);

        if (current == null) {
            current = item.getAmount();
        } else {
            current += item.getAmount();
        }

        this.stats.put(type, current);
    }

    boolean shouldDisableDrop(Block block) {
        return this.randomDrop.shouldDisableDrop(block);
    }

    Material getDisabledReplacement() {
        return this.randomDrop.getDisabledReplacement();
    }

    void giveExp(final Player player, Block block) {
        //int beforeExp = ExpHelper.getTotalExperience(player);
        
        float newExp = (float) (player.getExp() + 1.0 / 18.0);

        if (newExp >= 1.0) {
            player.setLevel(player.getLevel() + 1);
            newExp -= 1.0f;
        }
        
        player.setExp(newExp);
        /*
        final int expDiff = ExpHelper.getTotalExperience(player) - beforeExp;
        if(expDiff > 0 && this.updateFishExp) {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {

                @Override
                public void run() {
                    FishExpEvent event = new FishExpEvent(player, expDiff);
                    Bukkit.getPluginManager().callEvent(event);
                }
            });
        }
        */
    }

    public void damageItemInHands(Player player, ItemStack inHand) {
        if (inHand != null) {
            short maxDurability = inHand.getType().getMaxDurability();

            if (maxDurability > 0) {
                int unbreakingLevel = inHand.getEnchantmentLevel(Enchantment.DURABILITY);

                if (unbreakingLevel > 0) {
                    int roll = r.nextInt(unbreakingLevel);
                    if (roll > 0) {
                        return;
                    }
                }

                int newDurability = inHand.getDurability() + 1;

                if (newDurability >= maxDurability) {
                    player.setItemInHand(new ItemStack(Material.AIR));
                } else {
                    inHand.setDurability((short) newDurability);
                }
            }
        }
    }

    public boolean hasBlockedCobble(Player player) {
        return this.blockedCobble.contains(player);
    }

    public boolean switchCobble(Player player) {
        if (this.hasBlockedCobble(player)) {
            this.blockedCobble.remove(player);
            return true;
        } else {
            this.blockedCobble.add(player);
            return false;
        }
    }
}
