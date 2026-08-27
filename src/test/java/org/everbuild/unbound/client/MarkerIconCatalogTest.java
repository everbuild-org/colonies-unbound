package org.everbuild.unbound.client;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.InputStream;
import org.everbuild.unbound.marker.MarkerType;
import org.everbuild.unbound.marker.WorksitePoiType;
import org.junit.jupiter.api.Test;

class MarkerIconCatalogTest {
    @Test
    void everyFacilityAndPoiResolvesToAPackagedIcon() throws Exception {
        for (final MarkerType type : MarkerType.values()) {
            assertPackaged(type.id());
        }
        for (final WorksitePoiType type : WorksitePoiType.values()) {
            assertPackaged(type.id());
        }
    }

    private static void assertPackaged(final String iconId) throws Exception {
        final String textureId = MarkerIconCatalog.textureId(iconId);
        final String path = "/assets/coloniesunbound/textures/marker/icons/" + textureId + ".png";
        try (InputStream stream = MarkerIconCatalogTest.class.getResourceAsStream(path)) {
            assertNotNull(stream, () -> iconId + " should resolve to " + path);
        }
    }
}
