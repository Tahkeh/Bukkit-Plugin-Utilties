package de.xzise.metainterfaces;

import org.bukkit.Server;
import org.bukkit.command.CommandSender;

public class CommandSenderWrapper<T extends CommandSender> implements CommandSender {

    protected final T sender;
    
    protected CommandSenderWrapper(T sender) {
        this.sender = sender;
    }
    
    public T getSender() {
        return this.sender;
    }
    
    @Override
    public void sendMessage(String message) {
        this.sender.sendMessage(message);
    }

    @Override
    public boolean isOp() {
        return this.sender.isOp();
    }

    @Override
    public Server getServer() {
        return this.sender.getServer();
    }
}
