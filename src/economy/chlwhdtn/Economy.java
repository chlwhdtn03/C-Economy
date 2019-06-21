package economy.chlwhdtn;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
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
		
		getCommand("돈").setExecutor(this);
		getCommand("송금").setExecutor(this);
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
			if(args.length == 1) {
				String playername = args[0];
				if(!MoneyManager.hasAccount(playername)) {
					cs.sendMessage(ChatColor.RED + "존재하지 않는 플레이어 입니다.");
					return false;
				}
				cs.sendMessage(ChatColor.GOLD + playername + "님의 돈 : " + String.format("%,d￦", MoneyManager.getMoney(playername)));
				return true;
			}
			
			if(cs instanceof Player)
				cs.sendMessage(ChatColor.GOLD + "돈 : " + String.format("%,d￦", MoneyManager.getMoney(cs.getName())));
			else
				cs.sendMessage(ChatColor.RED + "플레이어만 사용할 수 있는 명령어입니다.");
			return true;
		}
		if(command.getName().equals("송금")) {
			if(args.length == 2) {
				String playername = args[0];
				long amount;
				
				if(!MoneyManager.hasAccount(playername)) {
					cs.sendMessage(ChatColor.RED + "존재하지 않는 플레이어 입니다.");
					return false;
				}
				
				try {
					amount = Long.parseLong(args[1]);
					if(amount < 0 && !cs.isOp())
						amount *= -1;
					if(amount == 0) {
						cs.sendMessage(ChatColor.RED + "송금할 수 없습니다.");
						return false;
					}
				} catch(NumberFormatException e) {
					cs.sendMessage(ChatColor.RED + "금액을 입력하세요.");
					return false;
				}
				
				if(cs.isOp()) {
					MoneyManager.addMoney(playername, amount);
					cs.sendMessage(ChatColor.GREEN + String.format("%,d￦", amount) + "을 " + playername + "님에게 송금했습니다.");
					return true;
				}
				
				if(!MoneyManager.hasEnoghMoney(playername, amount)) {
					cs.sendMessage(ChatColor.RED + "가지고 있는 돈이 부족합니다.");
				}
				
				MoneyManager.addMoney(cs.getName(), -amount);
				MoneyManager.addMoney(playername, amount);
				cs.sendMessage(ChatColor.GREEN + String.format("%,d￦", amount) + "을 " + playername + "님에게 송금했습니다.");
				return true;

				
			} else {
				cs.sendMessage(ChatColor.RED + "/송금 <플레이어> <금액>");
			}
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
