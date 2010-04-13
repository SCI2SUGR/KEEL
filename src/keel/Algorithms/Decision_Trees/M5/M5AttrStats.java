/**
* <p>
* @author Written by Cristobal Romero (Universidad de Córdoba) 10/10/2007
* @version 0.1
* @since JDK 1.5
*</p>
*/

package keel.Algorithms.Decision_Trees.M5;

/**
 * A Utility class that contains summary information on an
 * the values that appear in a dataset for a particular attribute.
 */
public class M5AttrStats {

    /** The number of int-like values */
    public int intCount = 0;

    /** The number of real-like values (i.e. have a fractional part) */
    public int realCount = 0;

    /** The number of missing values */
    public int missingCount = 0;

    /** The number of distinct values */
    public int distinctCount = 0;

    /** The number of values that only appear once */
    public int uniqueCount = 0;

    /** The total number of values (i.e. number of instances) */
    public int totalCount = 0;

    /** Stats on numeric value distributions */
    public SimpleStatistics numericStats;

    /** Counts of each nominal value */
    public int[] nominalCounts;

    /**
     * Updates the counters for one more observed distinct value.
     *
     * @param value the value that has just been seen
     * @param count the number of times the value appeared
     */
    protected void addDistinct(double value, int count) {

        if (count > 0) {
            if (count == 1) {
                uniqueCount++;
            }
            if (M5StaticUtils.eq(value, (double) ((int) value))) {
                intCount += count;
            } else {
                realCount += count;
            }
            if (nominalCounts != null) {
                nominalCounts[(int) value] = count;
            }
            if (numericStats != null) {
                numericStats.add(value, count);
                numericStats.calculateDerived();
            }
        }
        distinctCount++;
    }

    /**
     * Returns a human readable representation of this AttributeStats instance.
     *
     * @return a String represtinging these AttributeStats.
     */
    public String toString() {

        StringBuffer sb = new StringBuffer();
        sb.append(M5StaticUtils.padLeft("Type", 4)).append(M5StaticUtils.
                padLeft("Nom", 5));
        sb.append(M5StaticUtils.padLeft("Int",
                5)).append(M5StaticUtils.padLeft("Real", 5));
        sb.append(M5StaticUtils.padLeft("Missing", 12));
        sb.append(M5StaticUtils.padLeft("Unique", 12));
        sb.append(M5StaticUtils.padLeft("Dist", 6));
        if (nominalCounts != null) {
            sb.append(' ');
            for (int i = 0; i < nominalCounts.length; i++) {
                sb.append(M5StaticUtils.padLeft("C[" + i + "]", 5));
            }
        }
        sb.append('\n');

        long percent;
        percent = Math.round(100.0 * intCount / totalCount);
        if (nominalCounts != null) {
            sb.append(M5StaticUtils.padLeft("Nom", 4)).append(' ');
            sb.append(M5StaticUtils.padLeft("" + percent, 3)).append("% ");
            sb.append(M5StaticUtils.padLeft("" + 0, 3)).append("% ");
        } else {
            sb.append(M5StaticUtils.padLeft("Num", 4)).append(' ');
            sb.append(M5StaticUtils.padLeft("" + 0, 3)).append("% ");
            sb.append(M5StaticUtils.padLeft("" + percent, 3)).append("% ");
        }
        percent = Math.round(100.0 * realCount / totalCount);
        sb.append(M5StaticUtils.padLeft("" + percent, 3)).append("% ");
        sb.append(M5StaticUtils.padLeft("" + missingCount, 5)).append(" /");
        percent = Math.round(100.0 * missingCount / totalCount);
        sb.append(M5StaticUtils.padLeft("" + percent, 3)).append("% ");
        sb.append(M5StaticUtils.padLeft("" + uniqueCount, 5)).append(" /");
        percent = Math.round(100.0 * uniqueCount / totalCount);
        sb.append(M5StaticUtils.padLeft("" + percent, 3)).append("% ");
        sb.append(M5StaticUtils.padLeft("" + distinctCount, 5)).append(' ');
        if (nominalCounts != null) {
            for (int i = 0; i < nominalCounts.length; i++) {
                sb.append(M5StaticUtils.padLeft("" + nominalCounts[i], 5));
            }
        }
        sb.append('\n');
        return sb.toString();
    }
}
