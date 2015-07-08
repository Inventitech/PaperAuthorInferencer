#!/bin/bash

FILES=src/test/resources/*
NUM_AUTHROS=$(seq 5)
OUTPUT=author-guess-results.csv

if [ -f $OUTPUT ] 
then
	rm $OUTPUT
fi

echo "file, maxAuthors, percentage, result" >> $OUTPUT
for authors in $(seq 5)
do
	for perc in $(seq 0 0.05 1)
	do
		for f in $FILES
		do
		  echo "Processing file $f with $authors authors and $perc ratio ..."
		  RESULT=$(java -jar target/paper-author-inferencer-0.0.1-SNAPSHOT-jar-with-dependencies.jar $f -n $authors -t $perc)
		  echo "$f, $authors, $perc, $RESULT" >> $OUTPUT
		done
	done
done

