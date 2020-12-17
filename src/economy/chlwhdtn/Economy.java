package economy.chlwhdtn;

import java.util.Arrays;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import net.md_5.bungee.api.ChatColor;

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
		getCommand("수표").setExecutor(this);
		getCommand("송금").setExecutor(this);
		MoneyFileManager.reloadConfig();
	}
	
	@Override
	public void onDisable() {
		Bukkit.getScheduler().cancelTasks(getInstance());
		MoneyFileManager.saveConfig();
	}
	
	private void check() {
		System.out.println("-- "+ prefix +" --");
		System.out.println("경제 플러그인, C-Economy가 활성화 되었습니다.");
	}
	
	public static void online(Plugin plugin) {
		switch(plugin.getName()) {
		case "C-Gamble":
			System.out.println("도박 플러그인, "+ plugin.getName() + "가 활성화 되었습니다.");
			break;
		case "C-Shop":
			System.out.println("상업 플러그인, "+ plugin.getName() + "가 활성화 되었습니다.");
			break;
		case "C-Land":
			System.out.println("토지 플러그인, "+ plugin.getName() + "가 활성화 되었습니다.");
			break;
		case "C-Mine":
			System.out.println("광업 플러그인, "+ plugin.getName() + "가 활성화 되었습니다.");
			break;
		case "C-Util":
			System.out.println("유틸 플러그인, "+ plugin.getName() + "가 활성화 되었습니다.");
			break;
			default:
				System.out.println("알 수 없는 호환 시도. 해당 플러그인을 차단합니다.");
				Bukkit.getPluginManager().disablePlugin(plugin);
		}
	}
	
	@Override
	public boolean onCommand(CommandSender cs, Command command, String label, String[] args) {
		MoneyFileManager.reloadConfig();
		if(command.getName().equals("돈")) {
			if(args.length == 1) {
				if(args[0].equals("설정")) {
					if(!cs.isOp()) {
						cs.sendMessage(ChatColor.RED + "사용할 수 없습니다.");
						return false;
					}
					cs.sendMessage(ChatColor.RED + "/돈 설정 <플레이어> <금액>");
					return true;
				}
				String playername = args[0];
				if(!MoneyManager.hasAccount(playername)) {
					cs.sendMessage(ChatColor.RED + "존재하지 않는 플레이어 입니다.");
					return false;
				}
				cs.sendMessage(ChatColor.GOLD + playername + "님의 돈 : " + String.format("%,d￦", MoneyManager.getMoney(playername)));
				return true;
			} else if(args.length == 3) {
				if(args[0].equals("설정")) {
					if(!cs.isOp()) {
						cs.sendMessage(ChatColor.RED + "사용할 수 없습니다.");
						return false;
					}

					if(!MoneyManager.hasAccount(args[1])) {
						cs.sendMessage(ChatColor.RED + "존재하지 않는 플레이어 입니다.");
						return false;
					}
					
					long amount;
					try {
						amount = Long.parseLong(args[2]);
					} catch(NumberFormatException e) {
						cs.sendMessage(ChatColor.RED + "금액을 입력하세요.");
						return false;
					}
	
					MoneyManager.setMoney(args[1], amount);
					MoneyFileManager.saveConfig();
					cs.sendMessage(ChatColor.GREEN + args[1] + "님의 돈을 " + String.format("%,d￦", amount) + "으로 설정했습니다.");
					return true;
				}
			}
			
			if(cs instanceof Player)
				cs.sendMessage(ChatColor.GOLD + "돈 : " + String.format("%,d￦", MoneyManager.getMoney(cs.getName())));
			else
				cs.sendMessage(ChatColor.RED + "플레이어만 사용할 수 있는 명령어입니다.");
			return true;
		}
		if(command.getName().equals("수표")) {
			if(args.length == 1) {
				long amount;
				
				try {
					amount = Long.parseLong(args[0]);
					if(amount < 0 && !cs.isOp())
						amount *= -1;
					if(amount == 0) {
						cs.sendMessage(ChatColor.RED + "발행할 수 없습니다.");
						return false;
					}
				} catch(NumberFormatException e) {
					cs.sendMessage(ChatColor.RED + "금액을 입력하세요.");
					return false;
				}
				Player p = (Player) cs;
				boolean inv = false;
				for (ItemStack is : p.getInventory().getStorageContents()) {
					if (is == null) {
						inv = true;
						break;
					}

					if (is.getType() == getCheck(amount).getType()) {
						if (1 < getCheck(amount).getMaxStackSize()) {
							inv = true;
							break;
						} else {
							inv = false;
						}
					}

					if (is != null) {
						inv = false;
					}
					inv = false;
				}

				if (!inv) {
					p.sendMessage("§c인벤토리에 공간이 부족합니다.");
					return false;
				}
				
				if(cs.isOp()) {
					p.getInventory().addItem(getCheck(amount));
					MoneyFileManager.saveConfig();
					cs.sendMessage(ChatColor.GREEN + String.format("%,d￦", amount) + "원 수표를 발행했습니다.");
					return true;
				}
				
				if(!MoneyManager.hasEnoghMoney(cs.getName(), amount)) {
					cs.sendMessage(ChatColor.RED + "가지고 있는 돈이 부족합니다.");
					return false;
				}
				MoneyManager.addMoney(cs.getName(), -amount);
				MoneyFileManager.saveConfig();
				p.getInventory().addItem(getCheck(amount));
				cs.sendMessage(ChatColor.GREEN + String.format("%,d￦", amount) + "원 수표를 발행했습니다.");
				return true;

				
			} else {
				cs.sendMessage(ChatColor.RED + "/수표 <금액>");
			}
		}
		
		if(command.getName().equals("송금")) {
			if(cs.isOp() == false)
				return false;
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
					MoneyFileManager.saveConfig();
					cs.sendMessage(ChatColor.GREEN + String.format("%,d￦", amount) + "을 " + playername + "님에게 송금했습니다.");
					return true;
				}
				
				if(!MoneyManager.hasEnoghMoney(cs.getName(), amount)) {
					cs.sendMessage(ChatColor.RED + "가지고 있는 돈이 부족합니다.");
				}
				
				MoneyManager.addMoney(cs.getName(), -amount);
				MoneyManager.addMoney(playername, amount);
				MoneyFileManager.saveConfig();
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
			MoneyManager.setMoney(event.getPlayer().getName(), 125000);
			MoneyFileManager.saveConfig();
			
		}
		event.getPlayer().teleport(Bukkit.getWorld("land").getSpawnLocation().add(0, 1, 0)); // C-LAND 한정
	}
	
	public ItemStack getCheck(long price) {
		ItemStack result = new ItemStack(Material.ENCHANTED_BOOK);
		ItemMeta im = result.getItemMeta();
		im.setDisplayName("§6수표");
		im.setLore(Arrays.asList(ChatColor.GOLD + String.format("%,d￦", price)));
		result.setItemMeta(im);
		return result;
	}
}
