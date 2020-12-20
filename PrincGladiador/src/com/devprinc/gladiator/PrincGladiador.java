package com.devprinc.gladiator;

import net.milkbowl.vault.economy.Economy;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.devprinc.gladiator.command.GladiatorCommand;
import com.devprinc.gladiator.listener.GladiatorListeners;
import com.devprinc.gladiator.utils.Gladiator;

public class PrincGladiador extends JavaPlugin {

    private static PrincGladiador main;
    private static SimpleClans simpleClans;
    private static Economy economy;

    @Override
    public void onEnable() {
        Bukkit.getConsoleSender().sendMessage("§6§lGLADIADOR V1.0.0");
        Bukkit.getConsoleSender().sendMessage("§fplugin inicializado!");
        Bukkit.getConsoleSender().sendMessage("§fVersão refeita/sem bugs do §aRGladiador");
        main = this;
        if ((simpleClans = hookSimpleClans()) == null || (economy = hookEconomy()) == null) {
            getServer().getConsoleSender().sendMessage("§cSimpleCans ou Vault não foi encontrado...");
            return;
        }
        getServer().getConsoleSender().sendMessage("§aSimpleClans e Vault conectado com sucesso!");

        getServer().getPluginManager().registerEvents(new GladiatorListeners(), this);
        getCommand("gladiador").setExecutor(new GladiatorCommand());
        
        saveDefaultConfig();
        Gladiator.setupLocation();
    }

    @Override
    public void onDisable() {
    	Gladiator.saveLocations();
    	
        HandlerList.unregisterAll(this);
    }

    private SimpleClans hookSimpleClans() {
        Plugin p = getServer().getPluginManager().getPlugin("SimpleClans");
        if (p == null || !(p instanceof SimpleClans)) {
            return null;
        }

        return (SimpleClans) p;
    }

    private Economy hookEconomy() {
        if (!getServer().getPluginManager().isPluginEnabled("Vault")) return null;
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) return null;

        return rsp.getProvider();
    }

    public static SimpleClans getSimpleClans() {
        return simpleClans;
    }

    public static PrincGladiador getMain() {
        return main;
    }

    public static Economy getEconomy() {
        return economy;
    }

}