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
package net.brlns.gdownloader.util;

/**
 * @author Gabriel / hstr0100 / vertx010
 */
public final class XXHash64 {

    private static final long PRIME1 = 0x9E3779B185EBCA87L;
    private static final long PRIME2 = 0xC2B2AE3D27D4EB4FL;
    private static final long PRIME3 = 0x165667B19E3779F9L;
    private static final long PRIME4 = 0x85EBCA77C2B2AE63L;
    private static final long PRIME5 = 0x27D4EB2F165667C5L;

    private long v1, v2, v3, v4, totalLen, seed;

    private final byte[] buf = new byte[32];
    private int bufUsed;

    public XXHash64(long seed) {
        reset(seed);
    }

    public void reset(long seedIn) {
        seed = seedIn;
        v1 = seed + PRIME1 + PRIME2;
        v2 = seed + PRIME2;
        v3 = seed;
        v4 = seed - PRIME1;
        totalLen = 0;
        bufUsed = 0;
    }

    public void update(byte[] input, int offset, int len) {
        totalLen += len;

        if (bufUsed + len < 32) {
            System.arraycopy(input, offset, buf, bufUsed, len);
            bufUsed += len;

            return;
        }

        if (bufUsed > 0) {
            int fill = 32 - bufUsed;
            System.arraycopy(input, offset, buf, bufUsed, fill);
            v1 = Long.rotateLeft(v1 + read64(buf, 0) * PRIME2, 31) * PRIME1;
            v2 = Long.rotateLeft(v2 + read64(buf, 8) * PRIME2, 31) * PRIME1;
            v3 = Long.rotateLeft(v3 + read64(buf, 16) * PRIME2, 31) * PRIME1;
            v4 = Long.rotateLeft(v4 + read64(buf, 24) * PRIME2, 31) * PRIME1;
            offset += fill;
            len -= fill;
            bufUsed = 0;
        }

        while (len >= 32) {
            v1 = Long.rotateLeft(v1 + read64(input, offset) * PRIME2, 31) * PRIME1;
            v2 = Long.rotateLeft(v2 + read64(input, offset + 8) * PRIME2, 31) * PRIME1;
            v3 = Long.rotateLeft(v3 + read64(input, offset + 16) * PRIME2, 31) * PRIME1;
            v4 = Long.rotateLeft(v4 + read64(input, offset + 24) * PRIME2, 31) * PRIME1;
            offset += 32;
            len -= 32;
        }

        if (len > 0) {
            System.arraycopy(input, offset, buf, 0, len);
            bufUsed = len;
        }
    }

    public long digest() {
        long h64;
        if (totalLen >= 32) {
            h64 = Long.rotateLeft(v1, 1) + Long.rotateLeft(v2, 7) + Long.rotateLeft(v3, 12) + Long.rotateLeft(v4, 18);
            h64 = (h64 ^ (Long.rotateLeft(v1 * PRIME2, 31) * PRIME1)) * PRIME1 + PRIME4;
            h64 = (h64 ^ (Long.rotateLeft(v2 * PRIME2, 31) * PRIME1)) * PRIME1 + PRIME4;
            h64 = (h64 ^ (Long.rotateLeft(v3 * PRIME2, 31) * PRIME1)) * PRIME1 + PRIME4;
            h64 = (h64 ^ (Long.rotateLeft(v4 * PRIME2, 31) * PRIME1)) * PRIME1 + PRIME4;
        } else {
            h64 = seed + PRIME5;
        }

        h64 += totalLen;

        int p = 0;
        int end = bufUsed;

        while (p + 8 <= end) {
            h64 = Long.rotateLeft(h64 ^ (Long.rotateLeft(read64(buf, p) * PRIME2, 31) * PRIME1), 27) * PRIME1 + PRIME4;
            p += 8;
        }

        if (p + 4 <= end) {
            h64 = Long.rotateLeft(h64 ^ (read32(buf, p) * PRIME1), 23) * PRIME2 + PRIME3;
            p += 4;
        }

        while (p < end) {
            h64 = Long.rotateLeft(h64 ^ ((buf[p] & 0xFFL) * PRIME5), 11) * PRIME1;
            p++;
        }

        h64 ^= h64 >>> 33;
        h64 *= PRIME2;
        h64 ^= h64 >>> 29;
        h64 *= PRIME3;
        h64 ^= h64 >>> 32;

        return h64;
    }

    private long read64(byte[] p, int offset) {
        return (p[offset] & 0xFFL)
            | ((p[offset + 1] & 0xFFL) << 8)
            | ((p[offset + 2] & 0xFFL) << 16)
            | ((p[offset + 3] & 0xFFL) << 24)
            | ((p[offset + 4] & 0xFFL) << 32)
            | ((p[offset + 5] & 0xFFL) << 40)
            | ((p[offset + 6] & 0xFFL) << 48)
            | ((p[offset + 7] & 0xFFL) << 56);
    }

    private long read32(byte[] p, int offset) {
        return ((p[offset] & 0xFFL)
            | ((p[offset + 1] & 0xFFL) << 8)
            | ((p[offset + 2] & 0xFFL) << 16)
            | ((p[offset + 3] & 0xFFL) << 24)) & 0xFFFFFFFFL;
    }
}
