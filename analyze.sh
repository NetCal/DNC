#!/bin/bash

uname="$(uname -a)"
java="$(java -version 2>&1 >/dev/null | grep version)"
lastupdate="$(sed -n '/.*upgrade.*/{x;p;d;q}; x' /var/log/apt/history.log)" #<- this may contain multiple lines
#lastupdate="$(${lastupdate##*\n})"
timestamp="$(date +"%H:%M:%S %d-%m-%Y")"
sum=$(awk '{sum+=$1}END{ print sum/NR}' runtimes)

#echo $sum
#echo $uname
#echo $java
#echo $timestamp
#echo $lastupdate

output="$sum,$timestamp,$uname,$java,$lastupdate"
echo $output
echo "$output" >> "statistics"
