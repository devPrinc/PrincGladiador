package com.devprinc.gladiator.listener;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.devprinc.gladiator.utils.Gladiator;

public class GladiatorListeners implements Listener {

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEntityDamage(EntityDamageEvent e) {
		if (e.getEntity() instanceof Player) {
			Player p = (Player) e.getEntity();
			
			if (Gladiator.estaNoGladiador(p)) e.setCancelled(true);
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPotionSplash(PotionSplashEvent e) {
		for (LivingEntity livingEntity : e.getAffectedEntities()) {
			if (livingEntity instanceof Player) {
				Player p = (Player) livingEntity;
				
				if (Gladiator.estaNoGladiador(p)) e.getAffectedEntities().remove(p);
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent e) {
		if (!e.getMessage().toLowerCase().startsWith("/gladiador sair")) {
			if (Gladiator.estaNoGladiador(e.getPlayer())) {
				e.setCancelled(true);
				e.getPlayer().sendMessage("§cVocê não pode executar comandos no evento gladiador.");
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerQuit(PlayerQuitEvent e) {
		Player p = e.getPlayer();
		if (Gladiator.estaNoGladiador(p)) {
			Gladiator.sair(p);
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerDeath(PlayerDeathEvent e) {
		Player p = e.getEntity();
		if (Gladiator.estaNoGladiador(p)) {
			Gladiator.sair(p);
		}
	}
	
}