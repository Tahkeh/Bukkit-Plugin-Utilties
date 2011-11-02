package de.xzise.wrappers.permissions;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import de.xzise.XLogger;
import de.xzise.wrappers.Factory;
import de.xzise.wrappers.Handler;

public class PermissionsHandler extends Handler<PermissionsWrapper> {

    private static final String[] EMPTY_STRING_ARRAY = new String[0];

    public static final Map<String, Factory<PermissionsWrapper>> FACTORIES = new HashMap<String, Factory<PermissionsWrapper>>();
    private static final PermissionsWrapper NULLARY_PERMISSIONS = new PermissionsWrapper() {
        
        @Override
        public Plugin getPlugin() {
            return null;
        }
        
        @Override
        public Boolean has(CommandSender sender, Permission<Boolean> permission) {
            return null;
        }
        
        @Override
        public Integer getInteger(CommandSender sender, Permission<Integer> permission) {
            return null;
        }

        @Override
        public String[] getGroup(String world, String player) {
            return null;
        }

        @Override
        public Double getDouble(CommandSender sender, Permission<Double> permission) {
            return null;
        }

        @Override
        public String getString(CommandSender sender, Permission<String> permission, boolean recursive) {
            return null;
        }

        @Override
        public String getString(String groupname, String world, Permission<String> permission) {
            return null;
        }
    };
    
    static {
        FACTORIES.put("Permissions", new PermissionPluginWrapperFactory());
        FACTORIES.put("PermissionsBukkit", new PermissionsBukkitWrapper.FactoryImpl());
    }

    public PermissionsHandler(PluginManager pluginManager, String plugin, XLogger logger) {
        super(FACTORIES, NULLARY_PERMISSIONS, pluginManager, "permissions", plugin, logger);
    }

    public boolean permission(CommandSender sender, Permission<Boolean> permission) {
        Boolean result;
        try {
            result = this.getWrapper().has(sender, permission);
        } catch (UnsupportedOperationException e) {
            result = null;
            this.logger.info("PermissionsManager permission check wasn't supported by this plugin.");
        }
        if (result != null) {
            this.logger.info("Checked permission '" + permission.getName() + "' (Def: " + permission.getDefault() + ") and wrapper returned " + result);
            return result;
        } else {
            try {
                boolean result2 = sender.hasPermission(permission.getName());
                this.logger.info("Checked permission '" + permission.getName() + "' (Def: " + permission.getDefault() + ") and SuperPerms returned " + result2);
                return result2;
            } catch (NoSuchMethodError e) {
                boolean result2 = hasByDefault(sender, permission.getDefault());
                this.logger.info("Checked permission '" + permission.getName() + "' (Def: " + permission.getDefault() + ") and returned default " + result2);
                return result2;
            }
        }
    }

    private static boolean hasByDefault(CommandSender sender, Boolean def) {
        if (def != null && def == true) {
            return true;
        } else {
            return sender.isOp();
        }
    }

    // To prevent the unchecked warning
    public boolean permissionOr(CommandSender sender, Permission<Boolean> p1, Permission<Boolean> p2) {
        return this.permission(sender, p1) || this.permission(sender, p2);
    }

    public boolean permissionOr(CommandSender sender, Collection<? extends Permission<Boolean>> permissions) {
        for (Permission<Boolean> permission : permissions) {
            if (this.permission(sender, permission)) {
                return true;
            }
        }
        return false;
    }

    public int getInteger(CommandSender sender, Permission<Integer> permission) {
        Integer result;
        try {
            result = this.getWrapper().getInteger(sender, permission);
        } catch (UnsupportedOperationException e) {
            result = null;
            this.logger.info("PermissionsManager integer getter wasn't supported by this plugin.");
        }
        if (result != null) {
            return result;
        } else {
            return permission.getDefault();
        }
    }

    public double getDouble(CommandSender sender, Permission<Double> permission) {
        Double result;
        try {
            result = this.getWrapper().getDouble(sender, permission);
        } catch (UnsupportedOperationException e) {
            result = null;
            this.logger.info("PermissionsManager double getter wasn't supported by this plugin.");
        }
        if (result != null) {
            return result;
        } else {
            return permission.getDefault();
        }
    }
    
    public String getString(CommandSender sender, Permission<String> permission) {
        return this.getString(sender, permission, true);
    }
    
    public String getUserString(CommandSender sender, Permission<String> permission) {
        return this.getString(sender, permission, false);
    }
    
    private String getString(CommandSender sender, Permission<String> permission, boolean recursive) {
        String result;
        try {
            result = this.getWrapper().getString(sender, permission, recursive);
        } catch (UnsupportedOperationException e) {
            result = null;
            this.logger.info("PermissionsManager string getter wasn't supported by this plugin.");
        }
        if (result != null) {
            return result;
        } else {
            return permission.getDefault();
        }
    }
    
    public String getString(String world, String groupname, Permission<String> permission) {
        String result;
        try {
            result = this.getWrapper().getString(groupname, null, permission);
        } catch (UnsupportedOperationException e) {
            result = null;
            this.logger.info("PermissionsManager string getter wasn't supported by this plugin.");
        }
        if (result != null) {
            return result;
        } else {
            return permission.getDefault();
        }
    }

    public String[] getGroup(String world, String player) {
        String[] groups;
        try {
            groups = this.getWrapper().getGroup(world, player);
        } catch (UnsupportedOperationException e) {
            groups = null;
            this.logger.info("PermissionsManager group getter wasn't supported by this plugin.");
        }
        return groups == null ? EMPTY_STRING_ARRAY : groups;
    }
    
}
