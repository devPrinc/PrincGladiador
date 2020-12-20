package com.devprinc.gladiator.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import com.devprinc.gladiator.PrincGladiador;
import com.devprinc.gladiator.command.GladiatorCommand;

import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;

public class Gladiator {

    private static Map<Clan, List<ClanPlayer>> jogadores;
    private static int limite;
    private static boolean iniciado;
    private static boolean avisado;
    private static SimpleClans simpleClans;
    private static BukkitTask avisarTask;
    private static Location spawn;
    private static Location saida;
    private static int premio;
    private static Economy economy;

    static {
        jogadores = new HashMap<>();
        limite = 20;
        iniciado = false;
        avisado = false;
        simpleClans = PrincGladiador.getSimpleClans();
        premio = 30000;
        economy = PrincGladiador.getEconomy();
    }

    public static void entrar(Player p) {
        if (!avisado || iniciado) {
            p.sendMessage("§cVocê não pode entrar no evento gladiador agora!");
            return;
        }

        ClanPlayer cp = simpleClans.getClanManager().getClanPlayer(p);
        if (cp == null) {
            p.sendMessage("§cVocê não tem um clâ para participar do evento gladiador!");
            return;
        }

        List<ClanPlayer> jogadoresDoClan = jogadores.get(cp.getClan());
        if (jogadoresDoClan == null) jogadoresDoClan = new ArrayList<>();
        
        if (jogadoresDoClan.contains(cp)) {
        	p.sendMessage("§cVocê já está no evento gladiador");
        	return;
        }

        if (jogadoresDoClan.size() >= limite) {
            p.sendMessage("§cSeu clâ já esta lotado para ir ao evento gladiador!");
            return;
        }

        jogadoresDoClan.add(cp);

        jogadores.put(cp.getClan(), jogadoresDoClan);
        p.sendMessage("§aVocê entrou no evento gladiador!");
        p.teleport(spawn);
    }

    public static void sair(Player p) {
        if (!avisado) {
        	p.sendMessage("§cO Evento gladiador não foi iniciado");
            return;
        }
        
        if (iniciado) {
        	p.sendMessage("§cO evento gladiador já começou!");
        }

        ClanPlayer cp = simpleClans.getClanManager().getClanPlayer(p);
        if (cp == null) {
            p.sendMessage("§cVocê não tem um clâ para participar do evento gladiador!");
            return;
        }

        List<ClanPlayer> jogadoresDoClan = jogadores.get(cp.getClan());
        if (jogadoresDoClan == null) {
            p.sendMessage("§cSeu clâ não está participando do evento gladiador!");
            return;
        }

        if (!jogadoresDoClan.contains(cp)) {
        	p.sendMessage("§cVocê não está no evento gladiador");
        	return;
        }
        
        jogadoresDoClan.remove(cp);
        jogadores.put(cp.getClan(), jogadoresDoClan);

        sendMessageToAll(
                "",
                "§cO Jogador §7" + p.getName() + "§c(§f" + cp.getClan().getTag() + "§c) foi eliminado do evento gladiador!",
                ""
        );
        p.sendMessage(new String[] {
                "",
                "§cO Jogador §7" + p.getName() + "§c(§f" + cp.getClan().getTag() + "§c) foi eliminado do evento gladiador!",
                ""
        });

        if (jogadoresDoClan.size() == 0) {
        	if (iniciado) {
	            sendMessageToAll(
	                    "",
	                    "§cO clâ §7" + cp.getClan().getName() + "§c foi eliminado do evento gladiador!",
	                    ""
	            );
	            
	            p.sendMessage(new String[] {
	                    "",
	                    "§cO clâ §7" + cp.getClan().getName() + "§c foi eliminado do evento gladiador!",
	                    ""
	            });
        	}

            jogadores.remove(cp.getClan());

        	if (jogadores.size() <= 1) {
        	    terminar(jogadores.keySet().iterator().next());
            }
        }

        p.teleport(saida);
        p.sendMessage("§cVocê saiu do Evento Gladiador");
    }

    private static void terminar(Clan clan) {
    	if (clan == null) {
            Bukkit.broadcastMessage("");
            Bukkit.broadcastMessage("§eO gvento gladiador terminou!");
            Bukkit.broadcastMessage("§eNão houveram vencedores!");
            Bukkit.broadcastMessage("");
    		return;
    	}
        List<ClanPlayer> jogadores = Gladiator.jogadores.get(clan);
        float coins = premio / jogadores.size();

        Bukkit.broadcastMessage("");
        Bukkit.broadcastMessage("§eO gvento gladiador terminou!");
        Bukkit.broadcastMessage("§eO clâ vencedor foi: §7" + clan.getName());
        Bukkit.broadcastMessage("§eCada membro do clâ ganhou §7" + String.format("%.1f", coins) + "§e coins");
        Bukkit.broadcastMessage("");

        for (ClanPlayer clanPlayer : jogadores) {
            clanPlayer.toPlayer().teleport(saida);
            economy.depositPlayer(clanPlayer.toPlayer(), coins);
        }

        PrincGladiador main = PrincGladiador.getMain();
        
        main.getConfig().set("Ultimo-Ganhador", clan.getName());
        main.saveConfig();
    }

    public static boolean estaNoGladiador(Player p) {
        for (List<ClanPlayer> clanPlayers : jogadores.values()) {
            for (ClanPlayer clanPlayer : clanPlayers) {
                if (p.getName().equals(clanPlayer.toPlayer().getName())) return true;
            }
        }

        return false;
    }
    
//    public static void removerJogador(Player p) {
//    	for (List<ClanPlayer> clanPlayers : jogadores.values()) {
//    for (int i = 0; i < clanPlayers.size(); i++) {
//            	if (p.getName().equals(clanPlayers.get(i).toPlayer().getName())) {
//           		clanPlayers.remove(i);
//           		}
//           }
//       }
//    }

    private static void sendMessageToAll(String... msg) {
        for (List<ClanPlayer> clanPlayers : jogadores.values()) {
            for (ClanPlayer clanPlayer : clanPlayers) {
                Player p = clanPlayer.toPlayer();
                p.sendMessage(msg);
            }
        }

        Bukkit.getConsoleSender().sendMessage(msg);
    }

    public static void iniciar(Player p) {
        if (!p.hasPermission(GladiatorCommand.PERMISSION)) {
            p.sendMessage("§cVocê não possui permissão para executar este comando.");
            return;
        }

        if (avisado || iniciado) {
            p.sendMessage("§cO evento gladiador já foi iniciado/avisado!");
            return;
        }

        if (spawn == null || saida == null) {
        	p.sendMessage("§cO spawn ou a saída não foram setados!");
        	return;
        }
        
        avisado = true;
        avisarTask = new BukkitRunnable() {
            int avisos = /* TODO: 10 */ 2;
            @Override
            public void run() {
                if (avisos > 0) {
                    Bukkit.broadcastMessage("");
                    Bukkit.broadcastMessage("§eO evento gladiador foi iniciado!");
                    Bukkit.broadcastMessage("§eDigite §7/gladiador entrar§e para participar!");
                    Bukkit.broadcastMessage("§ePara participar você precisará ter um clâ!");
                    Bukkit.broadcastMessage("");
                    
                    avisos--;
                    return;
                }

                cancel();
                if (jogadores.size() < /* TODO: 2 */ 1) {
                    cancelar(
                            "",
                            "§cO evento gladiador foi cancelado por falta de clâs.",
                            ""
                    );
                    return;
                }

                sendMessageToAll(
                		"",
                		"§eO evento gladiador iniciou!",
                		"§eO Dano foi §e§lLIBERADO§e!",
                		""
                );
                iniciado = true;
            }
        }.runTaskTimer(PrincGladiador.getMain(), 0, 20L * /* TODO: 30 */ 5);
    }

    private static void cancelar(String... msg) {
        for (String str : msg) Bukkit.broadcastMessage(str);

        avisarTask.cancel();
        avisado = false;
        iniciado = false;
        for (List<ClanPlayer> clanPlayers : jogadores.values()) {
            for (ClanPlayer clanPlayer : clanPlayers) {
            	clanPlayer.toPlayer().teleport(saida);
            }
        }
    }

    public static void cancelar(Player p) {
        if (!p.hasPermission(GladiatorCommand.PERMISSION)) {
            p.sendMessage("§cVocê não possui permissão para executar este comando.");
            return;
        }

        if (!avisado && !iniciado) {
            p.sendMessage("§cVocê não pode cancelar o que não foi iniciado!");
            return;
        }

        Bukkit.broadcastMessage("");
        Bukkit.broadcastMessage("§cO evento gladiador foi cancelado por um staffer!");
        Bukkit.broadcastMessage("");

        avisarTask.cancel();
        avisado = false;
        iniciado = false;
        for (List<ClanPlayer> clanPlayers : jogadores.values()) {
            for (ClanPlayer clanPlayer : clanPlayers) {
            	clanPlayer.toPlayer().teleport(saida);
            }
        }
    }
    
    public static void setupLocation() {
        PrincGladiador main = PrincGladiador.getMain();
    	
    	Gladiator.spawn = (Location) main.getConfig().get("Locations.Spawn");
    	Gladiator.saida = (Location) main.getConfig().get("Locations.Saida");
    }
    
    public static void setSpawn(Player p) {
    	if (!p.hasPermission(GladiatorCommand.PERMISSION)) {
            p.sendMessage("§cVocê não possui permissão para executar este comando.");
            return;
        }
    	
    	Gladiator.spawn = p.getLocation();
    	p.sendMessage("§eVocê alterou a localização do spawn do evento gladiador.");
    }
    
    public static void setSaida(Player p) {
    	if (!p.hasPermission(GladiatorCommand.PERMISSION)) {
            p.sendMessage("§cVocê não possui permissão para executar este comando.");
            return;
        }
    	
    	Gladiator.saida = p.getLocation();
    	p.sendMessage("§eVocê alterou a localização da saída do evento gladiador.");
    }
    
    public static void saveLocations() {
        PrincGladiador main = PrincGladiador.getMain();
    	
    	main.getConfig().set("Locations.Spawn", Gladiator.spawn);
    	main.getConfig().set("Locations.Saida", Gladiator.saida);
    	
    	main.saveConfig();
    }

}
