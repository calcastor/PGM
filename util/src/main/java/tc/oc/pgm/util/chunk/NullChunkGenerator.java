package tc.oc.pgm.util.chunk;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;
import tc.oc.pgm.util.Version;
import tc.oc.pgm.util.platform.Platform;

/** A chunk generator that creates empty chunks. */
public class NullChunkGenerator extends ChunkGenerator {
  public static final NullChunkGenerator INSTANCE = new NullChunkGenerator();
  private static final boolean SKIP_BIOMES =
      Platform.MINECRAFT_VERSION.isOlderThan(new Version(1, 13, 0));
  private static final byte[] CHUNK = new byte[0];

  private NullChunkGenerator() {}

  public byte[] generate(final World world, final Random random, final int x, final int z) {
    return CHUNK;
  }

  @Override
  public List<BlockPopulator> getDefaultPopulators(World world) {
    return Collections.emptyList();
  }

  @Override
  public ChunkData generateChunkData(
      World world, Random random, int chunkX, int chunkZ, BiomeGrid biome) {
    if (SKIP_BIOMES) {
      return super.generateChunkData(world, random, chunkX, chunkZ, biome);
    }

    ChunkData chunkData = super.createChunkData(world);

    // For everyblock in the chunk set the biome to plains
    for (int x = 0; x < 16; x++) {
      for (int z = 0; z < 16; z++) {
        biome.setBiome(x, z, Biome.PLAINS);
      }
    }
    return chunkData;
  }

  @Override
  public boolean canSpawn(World world, int x, int z) {
    return true;
  }

  @Override
  public Location getFixedSpawnLocation(World world, Random random) {
    return new Location(world, 0, 128, 0);
  }
}
