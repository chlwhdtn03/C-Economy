package economy.chlwhdtn;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import net.md_5.bungee.api.ChatColor;
import shop.chlwhdtn.Shop;

public class Economy extends JavaPlugin implements CommandExecutor, Listener {
	
	private static Economy economy;
	
	public static Economy getInstance() {
		return economy;
	}
	
	private String prefix = "[C-Economy]";
	
	@Override
	public void onEnable() {
		economy = this;
		check();
		Bukkit.getPluginManager().registerEvents(this, this);
		MoneyFileManager.reloadConfig();
	}
	
	@Override
	public void onDisable() {
		MoneyFileManager.saveConfig();
	}
	
	private void check() {
		System.out.println("-- C-Economy --");
		System.out.println("경제 플러그인, C-Economy가 활성화 되었습니다.");
		System.out.println("추가 요소를 확인합니다...");
		System.out.println("-- -- -- -- -- -- --");
	}
	
	public static void online(Plugin plugin) {
		switch(plugin.getName()) {
		case "C-Shop":
			System.out.println("C-Shop 확인");
			break;
			default:
				System.out.println("알 수 없는 호환 시도");
				Bukkit.getPluginManager().disablePlugin(plugin);
		}
	}
	
	private Plugin getplugin(String name) { 
		return Bukkit.getPluginManager().getPlugin(name);
	}
	
	@Override
	public boolean onCommand(CommandSender cs, Command command, String label, String[] args) {
		if(command.getName().equals("돈")) {
			cs.sendMessage(ChatColor.GOLD + "돈 : " + String.format("%,d￦", MoneyManager.getMoney(cs.getName())));
		}
		if(command.getName().equals("송금")) {
			
		}
		return true;
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		if(!MoneyManager.hasAccount(event.getPlayer().getName())) {
			MoneyManager.setMoney(event.getPlayer().getName(), 100000);
		}
	}
}
