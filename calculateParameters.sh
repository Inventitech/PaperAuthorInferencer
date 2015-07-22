#!/bin/bash

# make sure we get proper decimals
LANG=en_us_8859_1

FILES=src/test/resources/*
NUM_AUTHROS=$(seq 5)
OUTPUT=author-guess-results.csv

if [ -f $OUTPUT ] 
then
	rm $OUTPUT
fi

echo "file, percentage, result" >> $OUTPUT


for f in $FILES
do
	for perc in $(seq 0 0.05 1)
	do
	  echo "Processing file $f with $authors authors and $perc ratio ..."
	  RESULT=$(java -jar target/paper-author-inferencer-0.0.1-SNAPSHOT-jar-with-dependencies.jar -t $perc $f)
	  echo "$f, $perc, $RESULT" >> $OUTPUT
	done
done

