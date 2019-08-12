#!/usr/bin/env bash 
cd ..
docker-compose stop db; 
docker-compose rm -f; 
for i in $(docker volume ls  | awk '{print $2}'  | grep -v "VOLUME"); 
do 
   docker volume rm $i;
done;

docker-compose up -d db;
