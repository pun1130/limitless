package user11681.limitless;

import com.google.common.math.IntMath;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.longs.Long2ReferenceOpenHashMap;
import it.unimi.dsi.fastutil.objects.ReferenceArrayList;
import user11681.commonformatting.CommonFormatting;

public class Limitless {
    private static final String[] BASE_NUMERALS = {"I", "V", "X", "L", "C", "D", "M"};

    private static transient final Long2ReferenceOpenHashMap<String> CACHE = new Long2ReferenceOpenHashMap<>(new long[]{0}, new String[]{"nulla"});

    protected static final ReferenceArrayList<String> ROMAN = new ReferenceArrayList<String>(BASE_NUMERALS, false) {{
        this.size = this.a.length;

        final int baseCount = BASE_NUMERALS.length;
        int j;
        int i;
        StringBuilder builder;

        for (int level = 0; level < 2; level++) {
            for (i = 1; i < baseCount; i++) {
                builder = new StringBuilder();

                for (j = 0; j <= level; j++) {
                    builder.append("§").append(CommonFormatting.OVERLINE_CODES[j]);
                }

                this.add(builder + BASE_NUMERALS[i] + "§r");
            }
        }
    }};
    protected static final IntList DECIMAL = new IntArrayList(new int[]{1, 5, 10, 50, 100, 500, 1000}, false) {{
        this.size = this.a.length;

        final int baseCount = BASE_NUMERALS.length;
        int j;

        for (int level = 1; level < 3; level++) {
            for (j = 1; j < baseCount; j++) {
                this.add(this.getInt(j) * IntMath.pow(1000, level));
            }
        }
    }};

    public static String fromDecimal(final long decimal) {
        final String cachedValue = CACHE.get(decimal);

        if (cachedValue != null) {
            return cachedValue;
        }

        long mutableDecimal = decimal;
        final StringBuilder roman = new StringBuilder();
        final int index = DECIMAL.size() - 1;
        final int largest = DECIMAL.getInt(index);

        while (mutableDecimal >= largest) {
            roman.append(ROMAN.get(index));
            mutableDecimal -= largest;
        }

        int div = 1;

        while (mutableDecimal >= div) {
            div *= 10;
        }

        div /= 10;

        while (mutableDecimal > 0) {
            int lastNum = (int) (mutableDecimal / div);

            if (lastNum <= 3) {
                for (int i = 0; i < lastNum; i++) {
                    roman.append(getRoman(div));
                }
            } else if (lastNum == 4) {
                roman.append(getRoman(div)).append(getRoman(div * 5));
            } else if (lastNum <= 8) {
                roman.append(getRoman(div * 5));

                for (int i = 0, end = lastNum - 5; i < end; i++) {
                    roman.append(getRoman(div));
                }
            } else if (lastNum == 9) {
                roman.append(getRoman(div)).append(getRoman(div * 10));
            }

            mutableDecimal %= div;
            div /= 10;
        }

        CACHE.put(decimal, roman.toString());

        return roman.toString();
    }

    private static String getRoman(final int decimal) {
        return ROMAN.get(DECIMAL.indexOf(decimal));
    }
}
