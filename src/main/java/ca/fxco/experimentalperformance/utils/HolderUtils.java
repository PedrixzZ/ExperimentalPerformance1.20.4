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
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class HolderUtils {

    private static final ConcurrentHashMap<VersionedInfoHolderData, VersionedInfoHolderData.InfoHolderPart> bestInfoHolderPartCache = new ConcurrentHashMap<>();

    public static boolean shouldRunHolder(InfoHolderData holderData) throws VersionParsingException {
        String modId = holderData.getModId();
        Optional<ModContainer> modContainer = FabricLoader.getInstance().getModContainer(modId);
        if (modContainer.isEmpty()) return false;
        Version version = modContainer.get().getMetadata().getVersion();
        return VersionPredicate.parse(holderData.getVersionPredicate()).test(version);
    }

    @Nullable
    public static VersionedInfoHolderData.InfoHolderPart getBestInfoHolderPart(VersionedInfoHolderData holderData) throws VersionParsingException {
        VersionedInfoHolderData.InfoHolderPart cachedPart = bestInfoHolderPartCache.get(holderData);
        if (cachedPart != null) return cachedPart;

        String modId = holderData.getModId();
        Optional<ModContainer> modContainer = FabricLoader.getInstance().getModContainer(modId);
        if (modContainer.isEmpty()) return null;
        Version version = modContainer.get().getMetadata().getVersion();

        List<VersionedInfoHolderData.InfoHolderPart> holderParts = holderData.getVersionedInfoHolderParts();
        VersionedInfoHolderData.InfoHolderPart bestPart = null;
        for (VersionedInfoHolderData.InfoHolderPart holderPart : holderParts) {
            if (VersionPredicate.parse(holderPart.versionPredicate()).test(version)) {
                if (bestPart == null || holderPart.weight() > bestPart.weight()) {
                    bestPart = holderPart;
                }
            }
        }

        if (bestPart != null) {
            bestInfoHolderPartCache.put(holderData, bestPart);
            return bestPart;
        }

        return null;
    }

    public static InfoHolderData createInfoHolderFromPart(VersionedInfoHolderData holderData,
                                                          VersionedInfoHolderData.InfoHolderPart holderPart) {
        return new InfoHolderData(
                holderData.getTargetClassName(),
                holderPart.extraRedirectFields().stream().collect(Collectors.toList()),
                holderPart.versionPredicate(),
                holderData.getModId()
        );
    }
                    }
