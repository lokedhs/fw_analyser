#!/bin/sh

port=7701

iptables -A OUTPUT -p tcp --dport $port -j DROP
port=`expr $port + 1`

for method in icmp-net-unreachable icmp-host-unreachable icmp-port-unreachable icmp-proto-unreachable icmp-net-prohibited icmp-host-prohibited icmp-admin-prohibited tcp-reset ; do
    iptables -A OUTPUT -p tcp --dport $port -j REJECT --reject-with $method
    port=`expr $port + 1`
done
