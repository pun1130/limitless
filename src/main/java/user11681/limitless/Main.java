package user11681.limitless;

import java.util.Collection;
import java.util.Collections;
import user11681.anvil.entrypoint.CommonListenerInitializer;
import user11681.anvil.event.Listener;
import user11681.anvilevents.event.i18n.TranslationEvent;
import user11681.usersmanual.math.RomanNumerals;

public class Main implements CommonListenerInitializer {
    @Listener
    public static void onTranslation(final TranslationEvent event) {
        final String key = event.getKey();

        if (key.matches("enchantment\\.level\\.\\d+")) {
            event.setValue(RomanNumerals.fromDecimal(Integer.parseInt(key.replaceAll("\\D", ""))));
        }
    }

    @Override
    public Collection<Class<?>> get() {
        return Collections.singleton(Main.class);
    }
}
