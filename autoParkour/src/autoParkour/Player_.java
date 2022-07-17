package autoParkour;

import java.util.Random;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import net.md_5.bungee.api.ChatColor;

public class Player_{
	
	private Block[] blockArray;
	
	private int goalBlockIndex;
	private int prevBlockIndex;
	private int curBlockIndex;
	
	private int arraySize = 4;
	
	private Player p;
	
	private Random rand;
	
	private Material[] materialArray = {Material.BLUE_WOOL, Material.LIME_WOOL,
										Material.YELLOW_WOOL, Material.RED_WOOL,
										Material.ORANGE_WOOL, Material.PINK_WOOL,
										Material.CYAN_WOOL};
	
	private Location firstLoc;
	
	private GameScoreboard scoreboard;
	
	private boolean soundEffectPlayed = false;
	
	private int counter = 0;
	
	public int score = 0; 
	public boolean ingame = false;
	
	private EffectManager effectManager;
	
	Player_(Player p){
		rand = new Random();
		scoreboard = new GameScoreboard();
		this.p = p;
		firstLoc = p.getLocation();
		effectManager = new EffectManager(p);
	}
	
	public void initParkour(Player p){	
		ingame = true;
		
		score = 0;
		scoreboard.init(p,score);
		
		goalBlockIndex = 1;
		prevBlockIndex = 0;
		curBlockIndex = 0;
		
		clearBlocks();
		
		Location loc = firstLoc;
		
		blockArray = new Block[arraySize];
		
		//create first block of block array
		p.teleport(new Location(p.getWorld(),loc.getBlockX(),loc.getBlockY()+200,loc.getBlockZ()));
		blockArray[0] = new Location(p.getWorld(),loc.getBlockX(),loc.getBlockY()+199,loc.getBlockZ()).getBlock();
		
		//create rest of block array
		for(int i=1;i<blockArray.length;i++) {
			blockArray[i] = createRandomLocation(p,blockArray[i-1]).getBlock();
		}
		
		//set block types of block array
		for(int i=0;i<blockArray.length;i++) {
			blockArray[i].setType(materialArray[i % materialArray.length]);
			counter++;
		}
		counter--;
		
		p.playSound(p.getLocation(), Sound.ENTITY_COW_MILK, 0.2F, 1.0F);
	}
	
	public Location createRandomLocation(Player p,Block b) {
		
		int b_x = b.getX(), b_y = b.getY(), b_z = b.getZ();
		int x_ = rand.nextInt(5) -2;
		int y_ = rand.nextInt(3) -1;
		int z_ = rand.nextInt(2) + 3;
		
		while(z_ == 4 && y_ == 1) {
			x_ = rand.nextInt(5) -2;
			y_ = rand.nextInt(3) -1;
			z_ = rand.nextInt(2) + 3;
		}

		return (new Location(p.getWorld(),b_x + x_, b_y + y_, b_z + z_));
	}
	
	public void setPlayerBlockIndex(Player p){
		int p_x = p.getLocation().getBlockX(), p_y = p.getLocation().getBlockY(), p_z = p.getLocation().getBlockZ();			
		
		prevBlockIndex = curBlockIndex;

		for(int i=0;i<blockArray.length;i++) {
			int b_x = blockArray[i].getX(), b_y = blockArray[i].getY(), b_z = blockArray[i].getZ();
			if((p_x == b_x || p_x == b_x -1 || p_x == b_x + 1) && 
				(p_z == b_z || p_z == b_z -1 || p_z == b_z + 1) &&
				(p_y == b_y + 1)) {
				curBlockIndex = i;	
			}
		}
	}
	
	
	public int getPrevBlockIndex(int currentIndex) {
		if(currentIndex != 0) {
			return currentIndex - 1;
		}
		else if(currentIndex == 0) {
			return blockArray.length - 1;
		}
		else
			return -1;
	}
	
	public int getLastBlockIndex(int currentIndex) {
		return (currentIndex + arraySize-2) % arraySize;
	}
	
	public void reCreateBlock(int index, Player p) {
		int prev = getPrevBlockIndex(index);
		int last = getLastBlockIndex(index);
		//delete prev block
		blockArray[prev].setType(Material.AIR);
		
		//create new block
		blockArray[prev] = createRandomLocation(p,blockArray[last]).getBlock();
		blockArray[prev].setType(materialArray[counter % materialArray.length]);
		
		if(((score % 10) - 7 == 0) && score > 10) {
			blockArray[prev].setType(Material.QUARTZ_BLOCK);
		}
		else if(score == 10-arraySize+1) {
			blockArray[prev].setType(Material.QUARTZ_BLOCK);
		}
	}
	
	public void gameLoop() {
		
		if(blockArray != null && ingame) {
			setPlayerBlockIndex(p);
			
			if(curBlockIndex == goalBlockIndex && curBlockIndex != prevBlockIndex) {
				counter++;
				goalBlockIndex++;
				score++;
				scoreboard.update(score);
				soundEffectPlayed = false;
				effectManager.effectApplied = false;
						
				if(goalBlockIndex == arraySize)
					goalBlockIndex = 0;
				
				reCreateBlock(curBlockIndex,p);
			}
			
			if(blockArray[curBlockIndex].getLocation().getBlockY() - p.getLocation().getBlockY() > 5) {
		        Location loc = blockArray[prevBlockIndex].getLocation();
		        loc.setY(loc.getY() + 1);
		        p.teleport(loc);
		        
		        score = 0;
		        counter++;
		        scoreboard.update(score);
		        effectManager.clearEffects();
		        p.playSound(p.getLocation(), Sound.ENTITY_SLIME_ATTACK, 0.2F, 1.0F);
			}
			
			if(score %10 == 0 && score != 0 && !soundEffectPlayed) {
				p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.2F, 1.0F);
				soundEffectPlayed = true;
			}
			
			if(score %10 == 0 && score != 0) {
				effectManager.applyRandomEffect();
			}
			
		}
	}
	
	public void clearBlocks() {
		if(blockArray != null)
			for(Block i: blockArray) {
				i.setType(Material.AIR);
		}
	}
	
	public void backToInitLocation() {
		p.teleport(firstLoc);
	}
	
	public void quit() {
		ingame = false;
		
		if(blockArray != null)
			for(Block i: blockArray) {
				i.setType(Material.AIR);
		}
		
		effectManager.clearEffects();
		
		backToInitLocation();
		scoreboard.clear();
		
	}
	
	public void getDemage() {
        Location loc = blockArray[prevBlockIndex].getLocation();
        loc.setY(loc.getY() + 1);
        p.teleport(loc);
	}
	
	public Player getPlayer() {
		return this.p;
	}

	
}
