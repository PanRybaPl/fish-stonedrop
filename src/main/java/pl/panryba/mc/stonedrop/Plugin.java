/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.panryba.mc.stonedrop;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import pl.panryba.mc.stonedrop.commands.CobbleCommand;
import pl.panryba.mc.stonedrop.commands.StoneCommand;

/**
 *
 * @author PanRyba.pl
 */
public class Plugin extends JavaPlugin {

    @Override
    public void onEnable() {
        FileConfiguration config = getConfig();
        
        ConfigurationSection defaultDropConfig = config.getConfigurationSection("default.drop");
        Map<String, Object> dropsMap = (Map<String, Object>)defaultDropConfig.getValues(false);
        DropConfig defaultConfig = new DropConfig();
        
        for(Entry<String, Object> entry : dropsMap.entrySet()) {
            Material mat = Material.getMaterial(entry.getKey());
            Object value = entry.getValue();
            
            if(value instanceof Integer) {
                defaultConfig.addDrop((int) value, (int) value, 256, mat);
            } else if (value instanceof ConfigurationSection) {
                ConfigurationSection section = (ConfigurationSection)value;
                
                int chance = section.getInt("chance");
                int displayChance = section.getInt("display", chance);
                
                DropInfo range = defaultConfig.addDrop(section.getInt("chance"), displayChance, section.getInt("y"), mat);
                
                if(section.contains("damage"))
                    range.setDamage(section.getInt("damage"));
                
                if(section.contains("biome")) {
                    Biome biome = Biome.valueOf(section.getString("biome"));
                    range.setSpecificBiome(biome);
                }
                
                if(section.contains("declination")) {
                    ConfigurationSection declSection = section.getConfigurationSection("declination");
                    String declType = declSection.getString("type");
                    if(declType.equals("fixed")) {
                        String declName = declSection.getString("name");
                        range.setFixedDeclination(declName);
                    }
                }
                
                if(section.contains("materials")) {
                    Set<Material> materials = new HashSet<>();
                    
                    List<?> materialObjects = section.getList("materials");
                    for(Object materialObj : materialObjects) {
                        String materialName = (String)materialObj;
                        Material material = Material.getMaterial(materialName);
                        
                        materials.add(material);
                    }
                    
                    range.setAllowedMaterials(materials);
                }
            }
        }
        
        String replacement = config.getString("default.replacement");
        defaultConfig.setReplacement(Material.getMaterial(replacement));
        
        Set<Material> disabled = new HashSet<>();
        List<?> disabledList = config.getList("default.disabled");
        
        for(Object disabledObj : disabledList) {
            String disabledName = (String)disabledObj;
            Material disabledMat = Material.getMaterial(disabledName);
            disabled.add(disabledMat);
        }
        
        defaultConfig.setDisabled(disabled);
        
        PluginApi api = new PluginApi(this, defaultConfig);
        getServer().getPluginManager().registerEvents(new StoneDropListener(api), this);
        
        getCommand("stone").setExecutor(new StoneCommand(defaultConfig));
        getCommand("cobble").setExecutor(new CobbleCommand(api));        
    }
}
