package com.devprinc.gladiator.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.devprinc.gladiator.PrincGladiador;
import com.devprinc.gladiator.utils.Gladiator;

public class GladiatorCommand implements CommandExecutor {

    public static final String PERMISSION = "gladiador.admin";

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {

        if (!(sender instanceof Player)) return true;
        Player p = (Player) sender;

        if (args.length < 1) {
            PrincGladiador main = PrincGladiador.getMain();
        	
          	p.sendMessage(" ");
        	p.sendMessage("§eUltimo clâ ganhador: §7" + main.getConfig().getString("Ultimo-Ganhador"));
        	p.sendMessage(" ");
            if (p.hasPermission(PERMISSION)) {
            	p.sendMessage(" ");
            	p.sendMessage("§2Comandos disponíveis:");
            	p.sendMessage(" ");
                p.sendMessage("§a/gladiador iniciar §8- §7Inicie o evento.");
                p.sendMessage("§a/gladiador cancelar §8- §7Cancele o evento.");
                p.sendMessage("§a/gladiador setspawn §8- §7Setar spawn do evento.");
                p.sendMessage("§a/gladiador setsaida §8- 47Setar saída do evento.");
            }
            return true;
        }

        if (args[0].equalsIgnoreCase("entrar")) {
            Gladiator.entrar(p);
        } else if (args[0].equalsIgnoreCase("sair")) {
            Gladiator.sair(p);
        } else if (args[0].equalsIgnoreCase("iniciar")) {
            Gladiator.iniciar(p);
        } else if (args[0].equalsIgnoreCase("cancelar")) {
            Gladiator.cancelar(p);
        } else if (args[0].equalsIgnoreCase("setspawn")) {
            Gladiator.setSpawn(p);
        } else if (args[0].equalsIgnoreCase("setsaida")) {
        	Gladiator.setSaida(p);
        }

        return false;
    }
}