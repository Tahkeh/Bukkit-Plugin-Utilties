package de.xzise.wrappers.economy;

import org.bukkit.plugin.Plugin;

import com.earth2me.essentials.api.Economy;
import com.earth2me.essentials.api.NoLoanPermittedException;
import com.earth2me.essentials.api.UserDoesNotExistException;

import de.xzise.XLogger;

public class Essentials implements EconomyWrapper {

    private final Plugin economy;
    private final XLogger logger;

    public final class EssentialsAccount implements AccountWrapper {
        private final String name;
        private final XLogger logger;

        public EssentialsAccount(String name, XLogger logger) {
            this.name = name;
            this.logger = logger;
        }

        @Override
        public boolean hasEnough(double price) {
            try {
                return Economy.hasEnough(this.name, price);
            } catch (UserDoesNotExistException e) {
                this.logger.warning("Unable to check if the user " + this.name + " has enough, because the user doesn't exists.");
                return false;
            }
        }

        @Override
        public void add(double price) {
            try {
                Economy.add(this.name, price);
            } catch (UserDoesNotExistException e) {
                this.logger.warning("Unable to change the price of " + this.name + ", because the user doesn't exists.");
            } catch (NoLoanPermittedException e) {
                this.logger.warning("Unable to change the price of " + this.name + ", because the loan was permitted.");
            }
        }

        @Override
        public double getBalance() {
            try {
                return Economy.getMoney(name);
            } catch (UserDoesNotExistException e) {
                this.logger.warning("Unable to get the balance from user " + this.name + ", because the user doesn't exists.");
                return 0;
            }
        }
    }

    public Essentials(Plugin plugin, XLogger logger) {
        this.economy = plugin;
        this.logger = logger;
    }

    @Override
    public AccountWrapper getAccount(String name) {
        return new EssentialsAccount(name, this.logger);
    }

    @Override
    public String format(double price) {
        return Economy.format(price);
    }

    @Override
    public Plugin getPlugin() {
        return this.economy;
    }

    public static class Factory implements de.xzise.wrappers.Factory<EconomyWrapper> {

        @Override
        public EconomyWrapper create(Plugin plugin, XLogger logger) {
            if (plugin instanceof com.earth2me.essentials.Essentials) {
                Essentials buf = new Essentials(plugin, logger);
                try {
                    buf.format(0);
                    return buf;
                } catch (NoClassDefFoundError e) {
                    logger.info("Essentials plugin found, but without Economy API. Should be there since Essentials 2.2.13");
                    return null;
                }
            } else {
                return null;
            }
        }
    }
}
