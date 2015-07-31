#!/bin/bash

# make sure we get proper decimals
LANG=en_us_8859_1

FILES=../icse-proceedings/combined/*
OUTPUT=author-guess-results.csv

if [ -f $OUTPUT ] 
then
	rm $OUTPUT
fi

start=`date +%s`

echo "file, author, a.occurenceRatio a.referenceEntriesRatio, a.eldestRefDelta, a.newestRefDelta, a.firstOccurrenceRatio, isRealAuthor" >> $OUTPUT
for f in $FILES
do
	  echo "Processing file $f ..."
	  RESULT=$(java -jar target/paper-author-inferencer-0.0.1-SNAPSHOT-jar-with-dependencies.jar $f)
	  echo "$RESULT" >> $OUTPUT
done

end=`date +%s`
runtime=$(((end-start)/60))
echo "Computation took $runtime minutes."
