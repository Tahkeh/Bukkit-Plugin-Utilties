package de.xzise.wrappers.permissions;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.nijiko.permissions.Entry.EntryVisitor;
import com.nijiko.permissions.Group;
import com.nijiko.permissions.PermissionHandler;
import com.nijiko.permissions.User;
import com.nijikokun.bukkit.Permissions.Permissions;

import de.xzise.wrappers.permissions.Permissions3Legacy;
import de.xzise.XLogger;

public class Permissions3Wrapper implements PermissionsWrapper {

    private final PermissionHandler handler;
    private final Plugin plugin;
    private final XLogger logger;

    public Permissions3Wrapper(Permissions permissions, XLogger logger) {
        this.handler = permissions.getHandler();
        this.plugin = permissions;
        this.logger = logger;
    }

    @Override
    public Plugin getPlugin() {
        return this.plugin;
    }

    @Override
    public Boolean has(CommandSender sender, Permission<Boolean> permission) {
        if (sender instanceof Player) {
            return this.handler.permission((Player) sender, permission.getName());
        } else {
            return null;
        }
    }

    private User getUser(CommandSender sender) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            return this.handler.getUserObject(player.getName(), player.getWorld().getName());
        } else {
            return null;
        }
    }

    private <T> T getPlayerValue(CommandSender sender, EntryVisitor<T> visitor) {
        User user = this.getUser(sender);
        return user == null ? null : user.recursiveCheck(visitor);
    }

    private <T> T getGroupValue(String group, String world, EntryVisitor<T> visitor) {
        Group groupObj = this.handler.getGroupObject(world, group);
        return groupObj == null ? null : groupObj.recursiveCheck(visitor);
    }

    @Override
    public Integer getInteger(CommandSender sender, Permission<Integer> permission) {
        return this.getPlayerValue(sender, Permissions3Legacy.getIntVisitor(permission.getName(), this.logger));
    }

    @Override
    public Double getDouble(CommandSender sender, Permission<Double> permission) {
        return this.getPlayerValue(sender, Permissions3Legacy.getDoubleVisitor(permission.getName(), this.logger));
    }

    @Override
    public String[] getGroup(String world, String player) {
        return this.handler.getGroups(world, player);
    }

    @Override
    public String getString(CommandSender sender, Permission<String> permission, boolean recursive) {
        if (recursive) {
            return this.getPlayerValue(sender, Permissions3Legacy.getStringVisitor(permission.getName(), this.logger));
        } else {
            User u = this.getUser(sender);
            return u == null ? null : u.getRawString(permission.getName());
        }
    }

    @Override
    public String getString(String groupname, String world, Permission<String> permission) {
        return this.getGroupValue(groupname, world, Permissions3Legacy.getStringVisitor(permission.getName(), this.logger));
    }

}