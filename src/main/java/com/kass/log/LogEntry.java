package com.kass.log;

import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.function.Predicate;

public class LogEntry {
    // -- sample --
    // Jul 13 13:19:43.890
    // ###10.1.21.108
    // ###analytics-svc
    // ###be456d81-2744-4cb3-b28b-bf2219dfcefe
    // ###Updating graph to include nodes related to payment 999

    public final static  String ENTRY_SPLITTER = "###";
    public final static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM dd HH:mm:ss.SSS");
    public final static int TS_INDEX = 0;
    public final static int IP_INDEX = 1;
    public final static int SERVICE_NAME_INDEX = 2;
    public final static int CORR_INDEX = 3;
    public final static int MSG_INDEX = 4;

    private Date timeStamp;
    private String ip;
    private String serviceName;
    private String corId;
    private String msg;

    public LogEntry(Date timeStamp, String ip, String serviceName, String corId, String msg) {
        this.timeStamp = timeStamp;
        this.ip = ip;
        this.serviceName = serviceName;
        this.corId = corId;
        this.msg = msg;
    }

    /**
     * parses and transforms a string entry into a LogEntry, returns the entry only if it meets the passed
     * condition, otherwise it returns null
     *
     * @param entry the string entry
     * @param predicate the condition
     * @return LogEntry
     */
    public static LogEntry parse(String entry, final Predicate<String> predicate){
        String[] entryArr = StringUtils.split(entry, ENTRY_SPLITTER, 5);
        try {
            if (predicate.test(entryArr[CORR_INDEX])) {
                return new LogEntry(simpleDateFormat.parse(
                        entryArr[TS_INDEX]),
                        entryArr[IP_INDEX],
                        entryArr[SERVICE_NAME_INDEX],
                        entryArr[CORR_INDEX],
                        entryArr[MSG_INDEX]);
            }
        } catch (ParseException e) {
            // send to the log
            e.printStackTrace();
        }
        return null;
    }

    /**
     * checks the order of the passed 2 entries according to the timestamp and the service name
     *
     * @param first an entry
     * @param second the other entry
     * @return -1 if the first is before the second, 1 if the second before the first or if they have
     * the same exact TS and service name
     *
     */
    public static int isBefore(LogEntry first, LogEntry second){
        if (! first.getTimeStamp().equals(second.getTimeStamp())){
            return (first.getTimeStamp().before(second.getTimeStamp())) ? -1 : 1;
        }
        return (first.getServiceName().compareTo(second.getServiceName()));
    }

    //---------- getters & setters ----------

    public Date getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getCorId() {
        return corId;
    }

    public void setCorId(String corId) {
        this.corId = corId;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    @Override
    public String toString() {
        return "com.kass.log.LogEntry{" +
                "timeStamp=" + timeStamp +
                ", serviceName='" + serviceName + '\'' +
                '}';
    }
}
