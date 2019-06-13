package economy.chlwhdtn;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.file.YamlConfigurationOptions;

public class MoneyFileManager {

	private static FileConfiguration MoneyConfig = null;
	private static File MoneyConfigFile = null;

	public static void reloadConfig() {
		if (MoneyConfigFile == null) {
			MoneyConfigFile = new File(Economy.getInstance().getDataFolder(), "Money.yml");
		}
		MoneyConfig = YamlConfiguration.loadConfiguration(MoneyConfigFile);
		if(!MoneyConfig.contains("money")) return;
		for(String name : MoneyConfig.getConfigurationSection("money").getKeys(false)) {
			MoneyManager.setMoney(name, MoneyConfig.getLong("money."+name));
		}
	}

	public static FileConfiguration getConfig() {
		if (MoneyConfig == null) {
			reloadConfig();
		}
		return MoneyConfig;
	}

	public static void saveConfig() {
		if (MoneyConfig == null || MoneyConfigFile == null) {
			return;
		}
		try {
			for(String name : MoneyManager.getMoneyMap().keySet()) {
				MoneyConfig.set("money."+name , MoneyManager.getMoneyMap().get(name));
			}
			getConfig().save(MoneyConfigFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
