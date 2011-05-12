package de.xzise.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.taylorkelly.help.Help;

import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import de.xzise.wrappers.help.HelpAPI;
import de.xzise.wrappers.help.TKellyHelpPlugin;

public class CommonCommandMap implements CommandMap {

    private Map<String, SubCommand> subCommandsMap;
    private List<SubCommand> subCommandsList;
    private HelpCommand helper;
    private SubCommand defaultCommand;

    public CommonCommandMap() {
        this.subCommandsMap = new HashMap<String, SubCommand>();
        this.subCommandsList = new ArrayList<SubCommand>();
    }

    public CommonCommandMap(List<SubCommand> subCommands) {
        this();
    }
    
    public void clear() {
        this.subCommandsMap.clear();
        this.helper = null;
    }

    public void populate(List<SubCommand> subCommands) {
        this.subCommandsList.addAll(subCommands);
        for (SubCommand subCommand : subCommands) {
            for (String text : subCommand.getCommands()) {
                if (this.subCommandsMap.put(text, subCommand) != null) {
                    throw new IllegalArgumentException("Command was already registered!");
                }
            }
        }
    }
    
    public void setHelper(HelpCommand helpCommand) {
        this.helper = helpCommand;
        this.helper.setCommandMap(this);
    }
    
    public void setDefault(SubCommand defaultCommand) {
        this.defaultCommand = defaultCommand;
    }
    
    public boolean executeCommand(CommandSender sender, String[] parameters) {
        if (parameters.length == 0) {
            if (!this.defaultCommand.execute(sender, parameters)) {
                return this.helper.execute(sender, parameters);
            } else {
                return true;
            }
        } else {
            SubCommand command = this.subCommandsMap.get(parameters[0]);
            if (command != null) {
                if (command.execute(sender, parameters)) {
                    return true;
                } else if (command instanceof FullHelpable) {
                    this.helper.showCommandHelp(sender, (FullHelpable) command);
                    return true;
                } else {
                    return false;
                }
            } else {
                return this.defaultCommand.execute(sender, parameters);
            }
        }
    }

    @Override
    public List<SubCommand> getCommandList() {
        return this.subCommandsList;
    }

    @Override
    public SubCommand getCommand(String name) {
        return this.subCommandsMap.get(name);
    }

    @Override
    public void setHelperPlugin(Plugin helperPlugin, Plugin owner) {
        HelpAPI api = null;
        if (helperPlugin instanceof Help) {
            api = new TKellyHelpPlugin((Help) helperPlugin);
        }
        
        if (api != null) {
            for (SubCommand command : this.subCommandsList) {
                if (command instanceof SmallHelpable) {
                    ((SmallHelpable) command).register(api, owner);
                }
            }
        }
    }

}