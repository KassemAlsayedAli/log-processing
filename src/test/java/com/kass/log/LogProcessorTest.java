package com.kass.log;

import com.kass.log.LogEntry;
import com.kass.log.LogProcessor;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class LogProcessorTest {

    @org.junit.Test
    public void sortByTimestampServiceNameTest() {
        List<String> logL = new ArrayList<>();
        logL.add("Jul 13 13:19:43.890###10.1.21.108###analytics-svc1###11-be456d81-2744-4cb3-b28b-bf2219dfcefe###Updating graph to include nodes related to payment 999");
        logL.add("Jul 11 13:19:43.890###10.1.21.108###analytics-svc123###22-be456d81-2744-4cb3-b28b-bf2219dfcefe###Updating graph to include nodes related to payment 999");
        logL.add("Jul 11 13:19:43.890###10.1.21.108###analytics-svc1###be456d81-2744-4cb3-b28b-bf2219dfcefe###Updating graph to include nodes related to payment 999");
        logL.add("Jul 15 13:19:43.890###10.1.21.108###analytics-svc1###be456d81-2744-4cb3-b28b-bf2219dfcefe###Updating graph to include nodes related to payment 999");

        //System.out.println(logL);
        List<LogEntry> retL = new LogProcessor().getLogEntries(logL, (s) -> true);
        //System.out.println(retL);

        try {
            assertEquals(LogEntry.simpleDateFormat.parse("Jul 11 13:19:43.890"), retL.get(0).getTimeStamp());
        } catch (Exception e){
            e.printStackTrace();
        }
        assertEquals("analytics-svc1", retL.get(0).getServiceName());
        assertEquals("analytics-svc123", retL.get(1).getServiceName());

    }

    @org.junit.Test
    public void getLogByCorrIdTest() {
        List<String> logL = new ArrayList<>();
        logL.add("Jul 13 13:19:43.890###10.1.21.108###analytics-svc1###11-be456d81-2744-4cb3-b28b-bf2219dfcefe###Updating graph to include nodes related to payment 999");
        logL.add("Jul 11 13:19:43.890###10.1.21.108###analytics-svc123###22-be456d81-2744-4cb3-b28b-bf2219dfcefe###Updating graph to include nodes related to payment 999");
        logL.add("Jul 11 13:19:43.890###10.1.21.108###analytics-svc1###be456d81-2744-4cb3-b28b-bf2219dfcefe###Updating graph to include nodes related to payment 999");
        logL.add("Jul 15 13:19:43.890###10.1.21.108###analytics-svc1###be456d81-2744-4cb3-b28b-bf2219dfcefe###Updating graph to include nodes related to payment 999");

        //System.out.println(logL);
        List<LogEntry> retL = new LogProcessor().getLogEntries(logL, (s) -> "be456d81-2744-4cb3-b28b-bf2219dfcefe".equals(s));
        //System.out.println(retL);

        assertEquals(2, retL.size());
        assertEquals("be456d81-2744-4cb3-b28b-bf2219dfcefe", retL.get(0).getCorId());
        assertEquals("be456d81-2744-4cb3-b28b-bf2219dfcefe", retL.get(1).getCorId());
    }

    @org.junit.Test
    public void evenlyDistributedTest() {
        List<String> logL = new ArrayList<>();

        // 2 10.1.21.108
        logL.add("Jul 13 13:19:43.890###10.1.21.108###analytics-svc1###11-be456d81-2744-4cb3-b28b-bf2219dfcefe###Updating graph to include nodes related to payment 999");
        logL.add("Jul 13 13:19:43.890###10.1.21.108###analytics-svc1###11-be456d81-2744-4cb3-b28b-bf2219dfcefe###Updating graph to include nodes related to payment 999");
        // 2 10.1.21.109
        logL.add("Jul 13 13:19:43.890###10.1.21.109###analytics-svc2###11-be456d81-2744-4cb3-b28b-bf2219dfcefe###Updating graph to include nodes related to payment 999");
        logL.add("Jul 13 13:19:43.890###10.1.21.109###analytics-svc2###11-be456d81-2744-4cb3-b28b-bf2219dfcefe###Updating graph to include nodes related to payment 999");

        assertEquals(true, new LogProcessor().isEvenlyDistributed(logL));
    }

    @org.junit.Test
    public void notEvenlyDistributedTest() {
        List<String> logL = new ArrayList<>();

        // 1 10.1.21.108
        logL.add("Jul 13 13:19:43.890###10.1.21.108###analytics-svc1###11-be456d81-2744-4cb3-b28b-bf2219dfcefe###Updating graph to include nodes related to payment 999");
        // 3 10.1.21.1089
        logL.add("Jul 13 13:19:43.890###10.1.21.109###analytics-svc1###11-be456d81-2744-4cb3-b28b-bf2219dfcefe###Updating graph to include nodes related to payment 999");
        logL.add("Jul 13 13:19:43.890###10.1.21.109###analytics-svc1###11-be456d81-2744-4cb3-b28b-bf2219dfcefe###Updating graph to include nodes related to payment 999");
        logL.add("Jul 13 13:19:43.890###10.1.21.109###analytics-svc1###11-be456d81-2744-4cb3-b28b-bf2219dfcefe###Updating graph to include nodes related to payment 999");
        // 1 10.1.21.107
        logL.add("Jul 13 13:19:43.890###10.1.21.107###analytics-svc1###11-be456d81-2744-4cb3-b28b-bf2219dfcefe###Updating graph to include nodes related to payment 999");

        assertEquals(false, new LogProcessor().isEvenlyDistributed(logL));
    }

    @org.junit.Test
    public void parseTest() {

        String entry = "Jul 13 13:19:43.890###10.1.21.108###analytics-svc1###11-be456d81-2744-4cb3-b28b-bf2219dfcefe###Updating graph to include nodes related to payment 999";
        assertEquals(false, LogEntry.parse(entry, (s) -> true).getMsg().contains("###"));

        entry = "Jul 13 13:19:43.890###10.1.21.108###analytics-svc1###11-be456d81-2744-4cb3-b28b-bf2219dfcefe###Updating graph to include nodes ### related to payment ### 999";
        assertEquals(true, LogEntry.parse(entry, (s) -> true).getMsg().contains("###"));
    }

    @org.junit.Test
    public void getStgDevTest() {
        Double[] v = {1d, 2d, 3d, 4d, 5d};
        Double[] v1 = {1d, 2d, 3d, 10d, 100d};

        LogProcessor logProcessor = new LogProcessor();
        //System.out.println("std dev: " + logProcessor.getStgDev(v));
        assertEquals(1.58, logProcessor.getStgDev(v), 0.0);

        //System.out.println("std dev: " + logProcessor.getStgDev(v1));
        assertEquals(43.08, logProcessor.getStgDev(v1), 0.0);
    }
}
