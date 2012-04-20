/*
 * This file is part of Bukkit Plugin Utilities.
 * 
 * Bukkit Plugin Utilities is free software: you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * 
 * Bukkit Plugin Utilities is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with Bukkit Plugin Utilities.
 * If not, see <http://www.gnu.org/licenses/>.
 */

package de.xzise.bukkit.util.cast;

/**
 * Casts the object to a string, if the object isn't null. It will call the
 * {@link Object#toString()} method.
 */
public final class StringCaster implements Caster<String> {

    public static final StringCaster INSTANCE = new StringCaster();

    private StringCaster() {
    };

    /**
     * Casts the object to a string, if the object isn't null. It will call the
     * {@link Object#toString()} method.
     */
    public String cast(Object o) {
        return o == null ? null : o.toString();
    }
}