You have a file with logs aggregated from multiple services and machines (one service can run on multiple VMs).
Logs are unordered.Each log entry consists of the following elements:

timestamp
ip
serviceName
correlationId
msg

Elements are separated with '###' sequence.

Log entry example:
Jul 13 13:19:43.890###10.1.21.108###analytics-svc###be456d81-2744-4cb3-b28b-bf2219dfcefe###Updating graph to include nodes related to payment 999
Log entries are separated with a "new line" character: '\n'

Implement the following functions:

- Order logs by timestamp in ascending order. Use serviceName ( order lexicographically) in case of timestamp ties.
- Given a correlation Id, return a list of log entries in the order of processing (use timestamp to order log entries).
- Determine if traffic is distributed evenly.
