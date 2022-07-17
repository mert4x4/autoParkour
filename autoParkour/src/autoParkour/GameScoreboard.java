package autoParkour;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import net.md_5.bungee.api.ChatColor;

public class GameScoreboard {
	
	private ScoreboardManager m; 
	private Scoreboard b;
	private Objective o;
	
	private Player p;
	
	public void init(Player p, int score) {
		this.p = p;
		m = Bukkit.getScoreboardManager();
		b = m.getNewScoreboard();
		o = b.registerNewObjective("Score", "");
		o.setDisplaySlot(DisplaySlot.SIDEBAR);
		o.setDisplayName(ChatColor.DARK_AQUA + "Auto Parkour");
		
		Score score_ = o.getScore(ChatColor.GOLD + "Score:");
		score_.setScore(score);
		p.setScoreboard(b);
	}
	
	public void update(int score) {
		Score score_ = o.getScore(ChatColor.GOLD + "Score:");
		score_.setScore(score);
		p.setScoreboard(b);
	}
	
	public void clear() {
		p.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard()); //clears scoreboard
	}
	
	
}
