#!/bin/bash

uname="$(uname -a)"
java="$(java -version 2>&1 | grep version)"
lastupdate="$(sed -n '/.*upgrade.*/{x;p;d;q}; x' /var/log/apt/history.log)" #<- this may contain multiple lines
lastupdate=${lastupdate##*Start-Date: }
timestamp="$(date +"%H:%M:%S %d-%m-%Y")"
sum=$(awk '{sum+=$1}END{ print sum/NR}' runtimes)

#echo $sum
#echo $uname
#echo $java
#echo $timestamp
#echo $lastupdate

output="$timestamp,$sum,$uname,$java,$lastupdate"
echo $output
echo "$output" >> "statistics"