/*
 *     <one line to give the program's name and a brief idea of what it does.>
 *     Copyright (C) 2024/01/19 ideal-state
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package team.idealstate.minecraftforge.xray.core;

import java.io.File;
import java.nio.file.Files;

/**
 * <p>Test</p>
 *
 * <p>Created on 2024/1/18 14:08</p>
 *
 * @author ketikai
 * @since 1.0.0
 */
public abstract class Test {

    public static void dumpClass(String name, byte[] classBytes) {
        try {
            Files.write(new File("G:/Downloads/test/" + name + ".class").toPath(), classBytes);
        } catch (java.io.IOException e) {
            throw new RuntimeException(e);
        }
    }
}
