/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.panryba.mc.stonedrop;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

/**
 *
 * @author PanRyba.pl
 */
public class RandomDrop {

    private DropConfig defaultConfig;

    public RandomDrop(DropConfig defaultConfig) {
        this.defaultConfig = defaultConfig;
    }

    public DropInfo generateForBlock(Block block, ItemStack itemInHand) {
        if (this.defaultConfig == null) {
            return null;
        }

        Material material = block.getType();
        if (material == null) {
            return null;
        }

        if (!shouldDropFrom(material)) {
            return null;
        }

        Random r = new Random();
        int dice = r.nextInt(10000);

        DropInfo range = this.defaultConfig.getDrop(block, dice, itemInHand);
        
        return range;
    }

    private boolean shouldDropFrom(Material material) {
        return material == Material.STONE;
    }

    Material getDisabledReplacement() {
        return this.defaultConfig.getReplacement();
    }

    boolean shouldDisableDrop(Block block) {
        if(block == null) {
            return false;
        }
        
        return this.defaultConfig.isDisabled(block.getType());
    }
}
