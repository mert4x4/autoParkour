package autoParkour;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements Listener{
		
	private ArrayList<Player_> playerArray = new ArrayList<Player_>();
	
	@Override
	public void onEnable() {
		getServer().getPluginManager().registerEvents(this, this);
		
		System.out.println("auto parkour is plugin enabled!");
	}
	
	@Override
	public void onDisable() {
		
		if(playerArray.size() != 0) {
			for(Player_ p_: playerArray) {
				p_.clearBlocks();
				p_.backToInitLocation();
			}
		}
		
		System.out.println("auto parkour is disabled!");
		
	}
	
    public Player_ getPlayer(Player p__) {
    	if(playerArray.size() != 0) {
    		Player p = p__;
    		for(Player_ p_: playerArray) {
    			Player tempPlayer = p_.getPlayer();
    			if(tempPlayer.equals(p)) {
    				return p_;
    			}
    		}
    	}
    	return null;
    }
	
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(command.getName().equalsIgnoreCase("parkourInit")) {
			if(sender instanceof Player) {
				Player p = (Player) sender;
				if(playerArray.size() == 0) {
					playerArray.add(new Player_(p));
					for(Player_ p_: playerArray) {
						if(p_.getPlayer().equals(p) && !p_.ingame) {
							p_.initParkour(p);
							p_.getPlayer().sendMessage(ChatColor.GREEN + "parkour initialized!");
						}
					}
				}
				else {			
					if(getPlayer((Player) sender) != null) {
			    		Player_ p_ = getPlayer((Player) sender);
			    		if(!p_.ingame) {
			    			playerArray.add(new Player_(p));
							p_.initParkour(p);
							p_.getPlayer().sendMessage(ChatColor.GREEN + "parkour initialized!");
			    		}
			    	}
				}
			}	
		}		
		
		if(command.getName().equalsIgnoreCase("parkourQuit")) {
			if(sender instanceof Player) {
		    	if(getPlayer((Player) sender) != null) {
		    		Player_ p_ = getPlayer((Player) sender);
		    		p_.quit();
		    		playerArray.remove(p_);
		    		p_.getPlayer().sendMessage(ChatColor.BLUE + "quited from auto parkour!");
		    	}		
			}	
		}		
		return true;
	}
	
    @EventHandler
    public void onDamage(EntityDamageEvent e) {
    	if(getPlayer((Player) e.getEntity()) != null) {
    		Player_ p_ = getPlayer((Player) e.getEntity());
    		p_.getDemage();
    		
    		if(((Player) e.getEntity()).equals(p_.getPlayer()))
    			e.setCancelled(true);
    	}
    }
	    
    @EventHandler
    public void actOnQuitEvent(PlayerQuitEvent e) {
    	if(getPlayer(e.getPlayer()) != null) {
    		Player_ p_ = getPlayer(e.getPlayer());
    		p_.quit();
    		playerArray.remove(p_);
    	}
    }
    
    @EventHandler (priority = EventPriority.HIGH)
	public void onMove(PlayerMoveEvent e) {
    	if(getPlayer(e.getPlayer()) != null) {
    		getPlayer(e.getPlayer()).gameLoop();
    	}
    	
	}
	
}