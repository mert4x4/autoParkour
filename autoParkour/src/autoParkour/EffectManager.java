package autoParkour;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import java.util.Random;

public class EffectManager {

	
	public PotionEffect effect;
	public PotionEffectType[] possibleEffectTypes = {PotionEffectType.JUMP,
											  PotionEffectType.SPEED};
	
	private Random rand;
	private Player p;
	
	public boolean effectApplied = false;
	
	EffectManager(Player p){
		rand = new Random();
		this.p = p;
		
		
	}
	
	public void applyRandomEffect(){
		if(!effectApplied) {
			int effectChance = rand.nextInt(100);
			
			if(effectChance >= 0 && effectChance <= 50) {
				clearEffects();
				p.sendMessage(ChatColor.LIGHT_PURPLE + "no effects!");
			}
			else if (effectChance > 50 && effectChance <= 80) {
				clearEffects();
				int level = rand.nextInt(1) + 1;
				effect = new PotionEffect(PotionEffectType.SPEED,Integer.MAX_VALUE,level);
				p.addPotionEffect(effect);
				p.sendMessage(ChatColor.BLUE + "speed ");
			}
			else if (effectChance > 80 && effectChance <= 100) {
				clearEffects();
				int level = rand.nextInt(1) + 1;
				effect = new PotionEffect(PotionEffectType.JUMP,Integer.MAX_VALUE,level);
				p.addPotionEffect(effect);
				p.sendMessage(ChatColor.AQUA + "jumping ");
			}		
			
			effectApplied = true;
		}
	}
	
	public boolean clearEffects(){
		for (PotionEffect effect : p.getActivePotionEffects())
            p.removePotionEffect(effect.getType());
		
		return true;
	}
		
}
