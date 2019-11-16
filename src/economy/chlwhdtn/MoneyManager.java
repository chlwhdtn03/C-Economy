package economy.chlwhdtn;

import java.util.HashMap;

public class MoneyManager {
	private static HashMap<String, Long> hashmap = new HashMap<String, Long>();

	public static void setMoney(String player, long amount) {
		hashmap.put(player, amount);
	}
	public static void addMoney(String player, long amount) {
		hashmap.put(player, hashmap.get(player) + amount);
	}
	public static boolean hasAccount(String player)
    {
        return hashmap.containsKey(player);
    }
	public static long getMoney(String player)
    {
        return hashmap.get(player);
    }
	public static HashMap<String, Long> getMoneyMap() {
		return hashmap;
	}
	public static boolean hasEnoghMoney(String player, long money)
    {
    	if(getMoney(player) >= money)
    	{
    		return true;
    		
    	} else
    		return false;
    }



}
