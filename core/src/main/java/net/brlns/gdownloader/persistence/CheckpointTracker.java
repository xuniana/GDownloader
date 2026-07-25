/*
 * Copyright (C) 2026 hstr0100
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.brlns.gdownloader.persistence;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Gabriel / hstr0100 / vertx010
 */
@Slf4j
public class CheckpointTracker<K> {

    private final Map<K, Long> lastFingerprints = new ConcurrentHashMap<>();

    public boolean isDirty(ICheckpointable<K> candidate) {
        Long lastFingerprint = lastFingerprints.get(candidate.getCheckpointKey());

        return lastFingerprint == null || lastFingerprint != candidate.checkpointFingerprint();
    }

    public void markClean(ICheckpointable<K> candidate) {
        lastFingerprints.put(candidate.getCheckpointKey(), candidate.checkpointFingerprint());
    }

    public void markAllClean(List<? extends ICheckpointable<K>> candidates) {
        for (ICheckpointable<K> candidate : candidates) {
            markClean(candidate);
        }
    }

    public <T extends ICheckpointable<K>> List<T> filterDirty(List<T> candidates) {
        List<T> dirty = new ArrayList<>(candidates.size());

        for (T candidate : candidates) {
            if (isDirty(candidate)) {
                dirty.add(candidate);
            }
        }

        if (log.isDebugEnabled()) {
            log.debug("Checkpoint filter: {} dirty out of {} candidates", dirty.size(), candidates.size());
        }

        return dirty;
    }

    public void forget(K key) {
        lastFingerprints.remove(key);
    }

    public void clear() {
        lastFingerprints.clear();
    }

    public int size() {
        return lastFingerprints.size();
    }
}
