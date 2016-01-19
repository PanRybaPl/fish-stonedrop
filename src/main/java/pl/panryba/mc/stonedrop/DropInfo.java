/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.panryba.mc.stonedrop;

import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import pl.panryba.mc.pl.Declination;
import pl.panryba.mc.pl.DeclinationManager;

import java.util.Set;

/**
 *
 * @author PanRyba.pl
 */
public class DropInfo implements Comparable<Integer> {

    private final int fromInclusive;
    private final int toExclusive;
    private final Material material;
    private final int minY;
    private final int displayChance;
    
    private boolean hasDamageSet;
    private int damage;
    
    private boolean hasFixedDeclination;
    private String finxedDeclinationName;
    
    private boolean hasSpecificBiome;
    private Biome specificBiome;
    private Set<Material> allowedMaterials;
    
    public float getDisplayChance() {
        return this.displayChance / 100.0f;
    }
    
    public float getChance() {
        return (this.toExclusive - this.fromInclusive) / 100.0f;
    }

    public DropInfo(int from, int count, int displayCount, int minY, Material material) {
        this.fromInclusive = from;
        this.toExclusive = from + count;
        this.material = material;
        this.minY = minY;
        this.displayChance = displayCount;
    }
    
    public int getFromInclusive() {
        return this.fromInclusive;
    }
    
    public int getToExclusive() {
        return this.toExclusive;
    }

    @Override
    public int compareTo(Integer o) {
        if (this.toExclusive <= o) {
            return -1;
        }

        if (this.fromInclusive > o) {
            return 1;
        }

        return 0;
    }

    public Material getMaterial() {
        return this.material;
    }

    @Override
    public String toString() {
        return fromInclusive + " - " + toExclusive + ": " + material;
    }
    
    public int getMaxY() {
        return this.minY;
    }

    void setDamage(int damage) {
        this.damage = damage;
        this.hasDamageSet = true;
    }
    
    void setAllowedMaterials(Set<Material> materials) {
        if(materials != null && materials.isEmpty()) {
            materials = null;
        }
        
        this.allowedMaterials = materials;        
    }
    
    public Set<Material> getAllowedMaterials() {
        return this.allowedMaterials;
    }

    public ItemStack produceStack() {
        ItemStack stack;
        if(this.hasDamageSet)
            stack = new ItemStack(this.material, 1, (short) this.damage);
        else
            stack = new ItemStack(this.material);
        
        return stack;
    }

    public void setFixedDeclination(String declName) {
        this.finxedDeclinationName = declName;
        this.hasFixedDeclination = true;
    }

    public Declination getDeclination() {
        if(this.hasFixedDeclination)
            return new Declination(this.finxedDeclinationName, this.finxedDeclinationName, this.finxedDeclinationName, this.finxedDeclinationName, this.finxedDeclinationName, this.finxedDeclinationName, this.finxedDeclinationName);
        
        return DeclinationManager.getInstance().getDeclination(this.material);
    }
    
    public void setSpecificBiome(Biome biome) {
        this.specificBiome = biome;
        this.hasSpecificBiome = true;
    }
    
    public Biome getSpecificBiome() {
        if(!hasSpecificBiome) {
            return null;
        }
        
        return this.specificBiome;
    }

    boolean isValidFor(Block block) {
       if(this.getMaxY() < block.getLocation().getBlockY()) {
           return false;
       }
       
       if(this.hasSpecificBiome && block.getBiome() != this.specificBiome) {
           return false;
       }
       
       return true;
    }
    
    boolean isValidFor(ItemStack itemInHand) {
        if(this.allowedMaterials == null) {
            return true;
        }
        
        if(itemInHand == null) {
            return false;
        }
        
        return this.allowedMaterials.contains(itemInHand.getType());
    }
}
