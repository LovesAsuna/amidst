package amidst.mojangapi.mocking;

import java.util.EnumSet;
import java.util.Set;
import java.util.function.Function;

import amidst.documentation.ThreadSafe;
import amidst.mojangapi.minecraftinterface.MinecraftInterface;
import amidst.mojangapi.minecraftinterface.MinecraftInterfaceException;
import amidst.mojangapi.minecraftinterface.RecognisedVersion;
import amidst.mojangapi.world.Dimension;
import amidst.mojangapi.world.WorldOptions;
import amidst.mojangapi.world.testworld.storage.json.BiomeDataJson;
import amidst.mojangapi.world.testworld.storage.json.WorldMetadataJson;

@ThreadSafe
public class FakeMinecraftInterface implements MinecraftInterface {
	private final WorldMetadataJson worldMetadataJson;
	private final BiomeDataJson quarterBiomeData;
	private final BiomeDataJson fullBiomeData;

	public FakeMinecraftInterface(
			WorldMetadataJson worldMetadataJson,
			BiomeDataJson quarterBiomeData,
			BiomeDataJson fullBiomeData) {
		this.worldMetadataJson = worldMetadataJson;
		this.quarterBiomeData = quarterBiomeData;
		this.fullBiomeData = fullBiomeData;
	}

	@Override
	public MinecraftInterface.WorldAccessor createWorldAccessor(WorldOptions worldOptions) throws MinecraftInterfaceException {
		if (worldMetadataJson.getSeed() == worldOptions.getWorldSeed().getLong() && worldMetadataJson.getWorldType().equals(worldOptions.getWorldType())
				&& worldOptions.getGeneratorOptions().isEmpty()) {
			return new WorldAccessor();
		} else {
			throw new MinecraftInterfaceException("the world has to match");
		}
	}

	@Override
	public RecognisedVersion getRecognisedVersion() {
		return worldMetadataJson.getRecognisedVersion();
	}

	private class WorldAccessor implements MinecraftInterface.WorldAccessor {
		private WorldAccessor() {
		}

		@Override
		public<T> T getBiomeData(Dimension dimension, int x, int y, int width, int height,
				boolean useQuarterResolution, Function<int[], T> biomeDataMapper)
				throws MinecraftInterfaceException {
			BiomeDataJson biomes = useQuarterResolution ? quarterBiomeData : fullBiomeData;
			return biomeDataMapper.apply(biomes.get(dimension, x, y, width, height));
		}

		@Override
		public Set<Dimension> supportedDimensions() {
			return EnumSet.allOf(Dimension.class);
		}
	}
}
