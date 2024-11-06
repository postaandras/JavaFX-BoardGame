package model;

import java.util.Formattable;
import java.util.FormattableFlags;
import java.util.Formatter;

public record Position(int row, int col) implements Formattable {

    @Override
    public String toString() {
        return String.format("(%d,%d)", row, col);
    }


    @Override
    public void formatTo(Formatter formatter, int flags, int width, int precision) {
        StringBuilder sb = new StringBuilder();
        sb.append(" (").append(row).append(",").append(col).append(") ");
        if (precision != -1 && precision < sb.length()) {
            sb.setLength(precision);
        }

        if (width > sb.length()) {
            for (int i = sb.length(); i < width; i++) {
                if ((flags & FormattableFlags.LEFT_JUSTIFY) == FormattableFlags.LEFT_JUSTIFY) {
                    sb.append(' ');
                } else {
                    sb.insert(0, ' ');
                }
            }
        }
        formatter.format(sb.toString());
    }
}