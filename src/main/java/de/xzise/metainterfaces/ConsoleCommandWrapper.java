/*
 * This file is part of Bukkit Plugin Utilities.
 * 
 * Bukkit Plugin Utilities is free software: you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * 
 * Foobar is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.xzise.metainterfaces;

import org.bukkit.command.ConsoleCommandSender;

import de.xzise.MinecraftUtil;

public class ConsoleCommandWrapper extends CommandSenderWrapper<ConsoleCommandSender> implements ConsoleCommandSender, LinesCountable, Nameable {

    public final static String NAME = "[CONSOLE]";

    public ConsoleCommandWrapper(ConsoleCommandSender sender) {
        super(sender);
    }

    @Override
    public int getMaxLinesVisible() {
        return MinecraftUtil.CONSOLE_LINES_COUNT;
    }

}
