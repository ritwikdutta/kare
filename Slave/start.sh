#!/bin/bash

CP="$HOME/lib"
for entry in lib/*.jar
do
    CP=$CP:$entry
done
java -cp "$CP" com.hackathon.Main 