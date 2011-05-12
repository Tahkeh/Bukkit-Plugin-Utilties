package de.xzise.wrappers.economy;

import org.bukkit.plugin.Plugin;

import cosine.boseconomy.BOSEconomy;
import de.xzise.XLogger;

public class BOSEcon0 implements EconomyWrapper {

    private BOSEconomy economy;
    
    public BOSEcon0(BOSEconomy plugin) {
        //TODO: Test
        this.economy = plugin;
    }
    
    public final class BOSEAccount implements AccountWrapper {

        private final BOSEconomy economy;
        private final String name;
        
        public BOSEAccount(BOSEconomy economy, String name) {
            this.economy = economy;
            this.name = name;
        }
        
        @Override
        public boolean hasEnough(int price) {
            return this.economy.getPlayerMoney(this.name) >= price;
        }

        @Override
        public void add(int price) {
            this.economy.addPlayerMoney(this.name, price, false);
        }
        
    }
    
    @Override
    public AccountWrapper getAccount(String name) {
        return new BOSEAccount(this.economy, name);
    }

    @Override
    public String format(int price) {
        //TODO: Negatives? Zeros?
        if (price == 1) {
            return price + " " + this.economy.getMoneyName();
        } else {
            return price + " " + this.economy.getMoneyNamePlural();
        }
    }
    
    public static class Factory implements EconomyWrapperFactory {

        @Override
        public EconomyWrapper create(Plugin plugin, XLogger logger) {
            if (plugin instanceof BOSEconomy) {
                return new BOSEcon0((BOSEconomy) plugin);
            } else {
                return null;
            }
        }
        
    }

    @Override
    public Plugin getPlugin() {
        return this.economy;
    }

}
