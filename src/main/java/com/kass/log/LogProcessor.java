package com.kass.log;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.kass.log.LogEntry.ENTRY_SPLITTER;
import static com.kass.log.LogEntry.IP_INDEX;

//
//See readme.txt for the problem statement
//

/**
 * Handles the processing of a com.kass.log.log file as specified in the readme.txt file.
 * Note that this implementation assumes that there is one server and the size of the com.kass.log.log
 * file is not the gigabytes, otherwise a different strategy is needed where the load
 * could be handled on a cluster of servers/machines...
 *
 */
public class LogProcessor {

    /**
     * returns a list of com.kass.log.log entries that meet certain specs
     *
     * @param logL the com.kass.log.log entry list
     * @param predicate specifies what entries to get
     * @return list of com.kass.log.log entries
     *
     * Time Complexity: O(n)
     * Space Complexity: O(n)
     */
    public List<LogEntry> getLogEntries(List<String> logL, final Predicate<String> predicate) {
        List<LogEntry> retL = new ArrayList<>();

        if (logL == null || logL.isEmpty())
            return retL;

        // transform the entries to LogEntry, only interest with the ones that meet the passed condition
        for (Object anEntry : logL) {
            LogEntry entry = LogEntry.parse((String) anEntry, predicate);
            if (entry != null) {
                retL.add(entry);
            }
        }

        // sort the list of entries
        Collections.sort(retL, (LogEntry a, LogEntry b) -> LogEntry.isBefore(a, b));
        return retL;
    }

    /**
     * checks it the com.kass.log.log entries are evenly distributed among the machines (IP addresses).
     * The assumption here is that the same service is running on multiple machines. This
     * method determines if the load is evenly distributed among those machines.
     *
     * If the distribution standard deviation is 0 then they are evenly distributed otherwise they
     * are not
     *
     * @param logL list of com.kass.log.log entries
     * @return true or false to indicate if the entries are evenly distributed among the IPs
     *
     * Time Complexity: O(n)
     * Space Complexity: O(n)
     *
     */
    public boolean isEvenlyDistributed(List<String> logL){
        // collect how many entries per IP
        Map<String, Double> counts = logL.parallelStream().
                collect(Collectors.toConcurrentMap(
                        w -> w.split(ENTRY_SPLITTER)[IP_INDEX], w -> 1d, Double::sum));

        // calculate the standard deviation of the number of entries
        return (0 == getStgDev(counts.values().toArray(new Double[0])));
    }

    /**
     * calculates the standard deviation of a list of doubles
     * @param list list of doubles
     * @return standard deviation of the input list
     */
    public double getStgDev(Double[] list){
        return new BigDecimal(new StandardDeviation().evaluate(ArrayUtils.toPrimitive(list)))
                .setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

}
