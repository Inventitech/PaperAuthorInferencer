#!/bin/bash

# make sure we get proper decimals
LANG=en_us_8859_1

FILES=src/test/resources/2013_*
NUM_AUTHROS=$(seq 5)
OUTPUT=author-guess-results.csv

if [ -f $OUTPUT ] 
then
	rm $OUTPUT
fi

echo "file, author, a.occurenceRatio a.referenceEntriesRatio, a.eldestRefDelta, a.newestRefDelta, isRealAuthor(a)" >> $OUTPUT


for f in $FILES
do
	  echo "Processing file $f ..."
	  RESULT=$(java -jar target/paper-author-inferencer-0.0.1-SNAPSHOT-jar-with-dependencies.jar $f)
	  echo "------------------------------------------------" >> $OUTPUT
	  echo "$f" >> $OUTPUT
	  echo "$RESULT" >> $OUTPUT
done

