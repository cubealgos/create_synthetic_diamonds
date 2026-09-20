package synthetic_diamonds;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** The mod's server-and-common entrypoint. */
public final class SyntheticDiamonds implements ModInitializer {
    public static final String MOD_ID = "synthetic_diamonds";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("Synthetic Diamonds ready beside Create Fly");
    }
}
