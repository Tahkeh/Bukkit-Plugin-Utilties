package de.xzise.wrappers.economy;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;

import de.xzise.MinecraftUtil;
import de.xzise.XLogger;
import de.xzise.wrappers.economy.AccountWrapper;
import de.xzise.wrappers.economy.BOSEcon0;
import de.xzise.wrappers.economy.EconomyWrapper;
import de.xzise.wrappers.economy.EconomyWrapperFactory;
import de.xzise.wrappers.economy.Essentials;
import de.xzise.wrappers.economy.iConomyFactory;

public class EconomyHandler {
    
    public enum PayResult {
        /** The price was paid. */
        PAID,
        /** The price couldn't paid, but not because the player hasn't enough. */
        UNABLE,
        /** The price couldn't paid, because the player hasn't enough. */
        NOT_ENOUGH;
    }
    
    public static final Map<String, EconomyWrapperFactory> FACTORIES = new HashMap<String, EconomyWrapperFactory>();
    
    static {
        FACTORIES.put("BOSEconomy", new BOSEcon0.Factory());
        FACTORIES.put("iConomy", new iConomyFactory());
        FACTORIES.put("Essentials", new Essentials.Factory());
    }
    
    public static final AccountWrapper NULLARY_ACCOUNT = new AccountWrapper() {
        
        @Override
        public boolean hasEnough(int price) {
            return false;
        }
        
        @Override
        public void add(int price) {}
    };
    
    private EconomyWrapper economy;
    private AccountWrapper tax = NULLARY_ACCOUNT;
    private String economyPluginName;
    private String economyBaseName;
    private final PluginManager pluginManager;
    private final XLogger logger;

    public EconomyHandler(PluginManager pluginManager, String economyPluginName, String economyBaseName, XLogger logger) {
        this.setBaseAccount();
        this.economyPluginName = economyPluginName;
        this.economyBaseName = economyBaseName;
        this.pluginManager = pluginManager;
        this.logger = logger;
    }

    /**
     * Pays for an action if the sender has enough money. If the sender is not a player no money will be transfered.
     * @param sender The paying sender.
     * @param reciever Optional reciever of the price. If null only the basic price is to pay.
     * @param price The amount of money which the reciever get.
     * @param basic The basic price like an tax.
     * @return If the price could be paid or if there was nothing to pay.
     */
    public PayResult pay(CommandSender sender, String reciever, int price, int basic) {
        if (this.economy != null) {
           Player player = MinecraftUtil.getPlayer(sender);
           if (player != null) {
               AccountWrapper executor = this.getAccount(player.getName());
               if (price + basic == 0) {
                   return PayResult.PAID;
               } else
               // Not negative
               //TODO: Add option if allow
//               if (executor.getBalance() >= price + basic) {
               if (executor.hasEnough(price + basic)) {    
                   executor.add(-price -basic);
                   this.tax.add(basic);
                   if (MinecraftUtil.isSet(reciever)) {
                       AccountWrapper owner = this.getAccount(reciever);
                       owner.add(price);
                   }
                   return PayResult.PAID;
               } else {
                   return PayResult.NOT_ENOUGH;
               }
           } else {
               this.logger.info("Couldn't pay action, because the executor is not a player.");
           }
        } else if (price > 0) {
            sender.sendMessage(ChatColor.RED + "You should pay for this warp. But no iConomy found.");
        }
        return PayResult.UNABLE;
    }
    
    private final AccountWrapper getAccount(String name) {
        return this.economy.getAccount(name);
    }
    
    public PayResult pay(CommandSender sender, int basic) {
        return this.pay(sender, null, 0, basic);
    }
    
    public boolean isActive() {
        return this.economy != null;
    }
    
    public String format(int price) {
        if (this.economy != null) {
            return this.economy.format(price);
        } else {
            return "";
        }
    }
    
    public void reloadConfig(String economyPluginName, String economyBaseName) {
        this.economyPluginName = economyPluginName;
        this.economyBaseName = economyBaseName;
        this.init();
        this.setBaseAccount();
    }
    
    private void setBaseAccount() {
        if (MinecraftUtil.isSet(this.economyBaseName) && this.isActive()) {
            this.tax = this.economy.getAccount(this.economyBaseName);
        } else {
            this.tax = NULLARY_ACCOUNT;
        }
    }
    
    public void init() {
        this.economy = null;
        for (String string : FACTORIES.keySet()) {
            this.init(this.pluginManager.getPlugin(string));
            if (this.economy != null) {
                return;
            }
        }
        if (this.economy == null) {
            this.logger.info("No economy system found until here. Economy plugin will be maybe activated later.");
        }
    }

    public void init(Plugin plugin) {
        if (plugin != null && this.economy == null) {
            PluginDescriptionFile pdf = plugin.getDescription();
            if (!MinecraftUtil.isSet(this.economyPluginName) || (pdf.getName().equalsIgnoreCase(this.economyPluginName))) {
                EconomyWrapperFactory factory = FACTORIES.get(pdf.getName());
                if (factory != null) {
                    if (plugin.isEnabled()) {
                        try {
                            this.economy = factory.create(plugin, this.logger);
                        } catch (Exception e) {
                            //TODO: Better exception handling
                            this.economy = null;
                        }
                        if (this.economy == null) {
                            this.logger.warning("Invalid economy system found: " + pdf.getFullName());
                        } else {
                            this.setBaseAccount();
                            this.logger.info("Linked with economy system: " + pdf.getFullName());
                        }
                    } else {
                        this.logger.warning("Doesn't link to disabled economy system: " + pdf.getFullName());
                    }
                }
            }
        }
    }
    
    public boolean unload(Plugin plugin) {
        if (this.economy != null && plugin == this.economy.getPlugin()) {
            this.economy = null;
            this.logger.info("Deactivated economy system.");
            return true;
        } else {
            return false;
        }
    }
}
