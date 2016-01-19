/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.panryba.mc.stonedrop.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.panryba.mc.stonedrop.PluginApi;

/**
 *
 * @author PanRyba.pl
 */
public class CobbleCommand implements CommandExecutor {

    private final PluginApi api;
    public CobbleCommand(PluginApi api) {
        this.api = api;
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmnd, String string, String[] strings) {
        if(!(cs instanceof Player)) {
            return false;
        }
        
        Player player = (Player)cs;
        boolean current = api.switchCobble(player);
        
        if(current) {
            player.sendMessage(ChatColor.GRAY + "Wlaczyles wypadanie bruku");
        } else {
            player.sendMessage(ChatColor.GRAY + "Wylaczyles wypadanie bruku");
        }
        
        return true;
    }
    
}
