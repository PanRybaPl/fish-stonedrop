/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.panryba.mc.stonedrop;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author PanRyba.pl
 */
public class DropConfig {
    private final List<DropInfo> ranges;
    private final DropInfo[] rangesMap;
    
    private Set<Material> disabled;
    private Material disabledReplacement;
    private int reserved;
    
    public DropConfig() {
        this.ranges = new ArrayList<>();
        this.rangesMap = new DropInfo[10000];
    }
    
    public void setDisabled(Set<Material> disabled) {
        if(disabled == null) {
            disabled = new HashSet<>();
        }
        
        this.disabled = disabled;
    }
    
    public boolean isDisabled(Material material) {
        return this.disabled.contains(material);
    }
    
    public Material getReplacement() {
        return this.disabledReplacement;
    }
    
    void setReplacement(Material replacement) {
        this.disabledReplacement = replacement;
    }    
    
    public DropInfo addDrop(int promil, int displayPromil, int maxY, Material material) {
        DropInfo range = new DropInfo(reserved, promil, displayPromil, maxY, material);
        for(int i = range.getFromInclusive(); i < range.getToExclusive(); ++i) {
            this.rangesMap[i] = range;
        }
        
        Bukkit.getLogger().info("Added drop - " + range.toString());
        this.ranges.add(range);
        
        this.reserved += promil;
        return range;
    }
    
    public DropInfo getDrop(Block block, int dice, ItemStack itemInHand) {
        DropInfo range = this.rangesMap[dice];
        if(range == null) {
            return null;
        }
        
        if(!range.isValidFor(block)) {
            return null;
        }
            
        if(!range.isValidFor(itemInHand)) {
            return null;
        }
                        
        return range;
    }
    
    public Iterable<DropInfo> getRanges() {
        return this.ranges;
    }
}
