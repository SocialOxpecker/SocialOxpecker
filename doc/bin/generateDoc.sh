#!/bin/bash
base=$(dirname $0)
cd $base
cd ..
if [ ! -d  "doc"  ]; then
    mkdir doc
fi
for file in $(ls *.asciidoc )
do
    fileName=$(basename $file | sed -e 's/.asciidoc$//')
    asciidoc -a icons -a data-uri -a toc -o doc/$fileName.html $file 
done
