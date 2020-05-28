package user11681.overpowered.event;

import user11681.anvil.event.Anvil;
import user11681.anvil.event.Listener;
import user11681.anvilevents.event.i18n.TranslationEvent;
import user11681.farmerlib.math.RomanNumerals;

@Anvil
public class CommonEvents {
    @Listener
    public static void onTranslation(final TranslationEvent event) {
        final String key = event.getKey();

        if (key.matches("enchantment\\.level\\.\\d+")) {
            event.setValue(RomanNumerals.fromDecimal(Integer.parseInt(key.replaceAll("\\D", ""))));
        }
    }
}
