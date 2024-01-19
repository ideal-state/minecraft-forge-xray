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

package team.idealstate.minecraftforge.xray.mod.config;

import net.minecraft.client.Minecraft;
import team.idealstate.minecraftforge.xray.common.Tags;

import java.io.*;
import java.nio.file.Files;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

/**
 * <p>XRayOptions</p>
 *
 * <p>Created on 2024/1/18 22:14</p>
 *
 * @author ketikai
 * @since 1.0.0
 */
public final class XRayOptions {

    private static volatile XRayOptions instance;

    public static XRayOptions getInstance() {
        if (instance == null) {
            synchronized (XRayOptions.class) {
                if (instance == null) {
                    instance = new XRayOptions();
                }
            }
        }
        return instance;
    }

    private final Lock lock = new ReentrantLock();
    private volatile Set<String> excludeBlockIdentifiers;
    private final File configFile;

    private XRayOptions() {
        File configDir = new File(Minecraft.getMinecraft().mcDataDir, "config/" + Tags.MOD_ID);
        configDir.mkdirs();
        this.configFile = new File(configDir, "EXCLUDE_BLOCK_IDENTIFIERS");
        if (configFile.exists()) {
            if (configFile.isFile()) {
                try (BufferedReader bufferedReader = new BufferedReader(new FileReader(configFile))) {
                    this.excludeBlockIdentifiers = bufferedReader.lines().collect(Collectors.toSet());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        } else {
            final InputStream is = getClass().getResourceAsStream(
                    "/assets/" + Tags.MOD_ID + "/config/EXCLUDE_BLOCK_IDENTIFIERS");
            if (is == null) {
                return;
            }
            try (InputStream inputStream = is) {
                Files.copy(inputStream, configFile.toPath());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public boolean isExcludeBlock(String blockName) {
        lock.lock();
        try {
            if (excludeBlockIdentifiers == null || excludeBlockIdentifiers.isEmpty()) {
                return false;
            }
            return excludeBlockIdentifiers.contains(blockName);
        } finally {
            lock.unlock();
        }
    }

    public Set<String> getExcludeBlockIdentifiers() {
        lock.lock();
        try {
            if (excludeBlockIdentifiers == null || excludeBlockIdentifiers.isEmpty()) {
                return Collections.emptySet();
            }
            return excludeBlockIdentifiers;
        } finally {
            lock.unlock();
        }
    }

    public void setExcludeBlockIdentifiers(Collection<String> excludeBlockIdentifiers) {
        lock.lock();
        try {
            this.excludeBlockIdentifiers = new HashSet<>(excludeBlockIdentifiers);
            saveExcludeBlockIdentifiers();
        } finally {
            lock.unlock();
        }
    }

    private void saveExcludeBlockIdentifiers() {
        if (excludeBlockIdentifiers == null) {
            return;
        }
        lock.lock();
        try {
            if (excludeBlockIdentifiers == null) {
                return;
            }
            Files.write(configFile.toPath(), excludeBlockIdentifiers);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
    }
}
