package ca.fxco.experimentalperformance.utils;

import ca.fxco.experimentalperformance.ExperimentalPerformance;
import ca.fxco.experimentalperformance.memoryDensity.InfoHolderData;
import ca.fxco.experimentalperformance.memoryDensity.VersionedInfoHolderData;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.Version;
import net.fabricmc.loader.api.metadata.version.VersionPredicate;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class HolderUtils {

    private static final ConcurrentHashMap<VersionedInfoHolderData, VersionedInfoHolderData.InfoHolderPart> bestInfoHolderPartCache = new ConcurrentHashMap<>();

    public static boolean shouldRunHolder(InfoHolderData holderData) {
        String modId = holderData.getModId();
        Optional<ModContainer> modContainer = FabricLoader.getInstance().getModContainer(modId);
        if (modContainer.isEmpty()) return false;
        Version version = modContainer.get().getMetadata().getVersion();
        return VersionPredicate.parse(holderData.getVersionPredicate()).test(version);
    }

    @Nullable
    public static VersionedInfoHolderData.InfoHolderPart getBestInfoHolderPart(VersionedInfoHolderData holderData) {
        VersionedInfoHolderData.InfoHolderPart cachedPart = bestInfoHolderPartCache.get(holderData);
        if (cachedPart != null) return cachedPart;

        String modId = holderData.getModId();
        Optional<ModContainer> modContainer = FabricLoader.getInstance().getModContainer(modId);
        if (modContainer.isEmpty()) return null;
        Version version = modContainer.get().getMetadata().getVersion();

        for (VersionedInfoHolderData.InfoHolderPart holderPart : holderData.getVersionedInfoHolderParts()) {
            if (VersionPredicate.parse(holderPart.versionPredicate()).test(version)) {
                bestInfoHolderPartCache.put(holderData, holderPart);
                return holderPart;
            }
        }

        return null;
    }

    public static InfoHolderData createInfoHolderFromPart(VersionedInfoHolderData holderData,
                                                          VersionedInfoHolderData.InfoHolderPart holderPart) {
        return new InfoHolderData(
                holderData.getTargetClassName(),
                Stream.concat(
                        holderData.getRedirectFields().stream(),
                        holderPart.extraRedirectFields().stream()
                ).toList(),
                holderPart.versionPredicate(),
                holderData.getModId()
        );
    }
}
