package user11681.limitless;

import it.unimi.dsi.fastutil.chars.CharArrayList;
import it.unimi.dsi.fastutil.chars.CharList;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;

public class Limitless {
    protected static final CharList ROMAN = new CharArrayList() {{
        add('I');
        add('V');
        add('X');
        add('L');
        add('C');
        add('D');
        add('M');
    }};
    protected static final IntList DECIMAL = new IntArrayList() {{
        add(1);
        add(5);
        add(10);
        add(50);
        add(100);
        add(500);
        add(1000);
    }};

    public static String fromDecimal(long decimal) {
        if (decimal == 0) {
            return "nulla";
        }

        final StringBuilder roman = new StringBuilder();
        final int index = DECIMAL.size() - 1;
        final int largest = DECIMAL.getInt(index);

        while (decimal >= largest) {
            roman.append(ROMAN.getChar(index));
            decimal -= largest;
        }

        int div = 1;

        while (decimal >= div) {
            div *= 10;
        }

        div /= 10;

        while (decimal > 0) {
            int lastNum = (int) (decimal / div);

            if (lastNum <= 3) {
                for (int i = 0; i < lastNum; i++) {
                    roman.append(toRoman(div));
                }
            } else if (lastNum == 4) {
                roman.append(toRoman(div)).append(toRoman(div * 5));
            } else if (lastNum <= 8) {
                roman.append(toRoman(div * 5));

                for (int i = 0, end = lastNum - 5; i < end; i++) {
                    roman.append(toRoman(div));
                }
            } else if (lastNum == 9) {
                roman.append(toRoman(div)).append(toRoman(div * 10));
            }

            decimal %= div;
            div /= 10;
        }

        return roman.toString();
    }

    public static char toRoman(final int decimal) {
        return ROMAN.getChar(DECIMAL.indexOf(decimal));
    }
}
