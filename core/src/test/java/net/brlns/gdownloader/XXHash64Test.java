package net.brlns.gdownloader;

import java.nio.charset.StandardCharsets;
import net.brlns.gdownloader.util.XXHash64;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class XXHash64Test {

    @Test
    public void testEmptyInput() {
        XXHash64 xxHash = new XXHash64(0);
        long expected = 0xef46db3751d8e999L;

        Assertions.assertEquals(expected, xxHash.digest());

        xxHash.reset(0);
        Assertions.assertEquals(expected, xxHash.digest());
    }

    @Test
    public void testChunkingEquivalence() {
        String input = "Lorem ipsum dolor sit amet, consectetur adipiscing elit."
            + " Sed do eiusmod tempor incididunt ut labore et dolore magna aliqua."
            + " Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat."
            + " Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur."
            + " Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.";

        byte[] data = input.getBytes(StandardCharsets.UTF_8);

        XXHash64 hashComplete = new XXHash64(12345L);
        hashComplete.update(data, 0, data.length);
        long expected = hashComplete.digest();

        XXHash64 hashChunked = new XXHash64(12345L);
        for (byte b : data) {
            hashChunked.update(new byte[] {b}, 0, 1);
        }
        long actualByteByByte = hashChunked.digest();

        XXHash64 hashBlocks = new XXHash64(12345L);
        hashBlocks.update(data, 0, 15);
        hashBlocks.update(data, 15, data.length - 15);
        long actualBlocks = hashBlocks.digest();

        Assertions.assertEquals(expected, actualByteByByte);
        Assertions.assertEquals(expected, actualBlocks);
    }

    @Test
    public void testSeedVariance() {
        byte[] data = "https://www.youtube.com/watch?v=cDfPt0bX90E".getBytes(StandardCharsets.UTF_8);

        XXHash64 hash1 = new XXHash64(0);
        hash1.update(data, 0, data.length);

        XXHash64 hash2 = new XXHash64(42);
        hash2.update(data, 0, data.length);

        Assertions.assertNotEquals(hash1.digest(), hash2.digest());
    }

    @Test
    public void testOffsetAndLength() {
        byte[] fullData = "pad--TARGET--pad".getBytes(StandardCharsets.UTF_8);
        byte[] targetData = "TARGET".getBytes(StandardCharsets.UTF_8);

        XXHash64 hash1 = new XXHash64(0);
        hash1.update(fullData, 5, 6);

        XXHash64 hash2 = new XXHash64(0);
        hash2.update(targetData, 0, targetData.length);

        Assertions.assertEquals(hash2.digest(), hash1.digest());
    }
}
