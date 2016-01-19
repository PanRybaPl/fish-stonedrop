/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.panryba.mc.stonedrop.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.panryba.mc.pl.Declination;
import pl.panryba.mc.pl.DeclinationManager;
import pl.panryba.mc.pl.LanguageHelper;
import pl.panryba.mc.stonedrop.DropConfig;
import pl.panryba.mc.stonedrop.DropInfo;

/**
 *
 * @author PanRyba.pl
 */
public class StoneCommand implements CommandExecutor {
    
    private DropConfig config;
    
    public StoneCommand(DropConfig config) {
        this.config = config;
    }
    
    @Override
    public boolean onCommand(CommandSender cs, Command cmnd, String string, String[] strings) {
        List<String> msgs = new ArrayList<>();
        
        msgs.add("");
        msgs.add(ChatColor.GREEN + "-- Lista przedmiotow, jakie mozna znalezc kopiac " + ChatColor.GRAY + "kamien:");
        for(DropInfo range : this.config.getRanges()) {
            Declination decl = range.getDeclination();
            Biome specificBiome = range.getSpecificBiome();
            
            String chanceString = ChatColor.GRAY + "szansa: " + ChatColor.YELLOW + String.format("%.2f%%", range.getDisplayChance());
            String firstLine = ChatColor.RED + "- " + decl.getMianownik() + " " + chanceString;
            if(range.getMaxY() < 255) {
                firstLine = firstLine + " " + ChatColor.GRAY + "od poziomu " + ChatColor.YELLOW + "Y=" + Integer.toString(range.getMaxY()) + " w dol";
            }            
            
            msgs.add(firstLine);
            
            List<String> details = new ArrayList<>();
            
            if(specificBiome != null) {
                details.add(ChatColor.GRAY + "wylacznie w biomie " + ChatColor.YELLOW + getBiomeName(specificBiome));
            }            
            
            Set<Material> materials = range.getAllowedMaterials();
            if(materials != null) {
                DeclinationManager mgr = DeclinationManager.getInstance();
                String matStr = ChatColor.GRAY + "mozna wykopac: " + ChatColor.YELLOW;
                
                boolean first = true;
                for(Material mat : materials) {
                    if(!first) {
                        matStr += ", ";
                    } else {
                        first = false;
                    }
                    
                    matStr += mgr.getDeclination(mat).getNarzednik();
                    
                }
                details.add(matStr);
            }
            
            for(String detail : details) {
                msgs.add(ChatColor.DARK_GRAY + "   " + detail);
            }
        }
        
        msgs.add(ChatColor.RED + "- Dodatkowe bonusy otrzymasz kopiac kilofem fortuny");
        
        String[] msgsArray = new String[msgs.size()];
        msgs.toArray(msgsArray);
        
        cs.sendMessage(msgsArray);
        return true;
    }

    private String getBiomeName(Biome specificBiome) {
        switch(specificBiome) {
            case EXTREME_HILLS:
                return "Extreme Hills";
            default:
                return specificBiome.toString();
        }
    }
    
}
