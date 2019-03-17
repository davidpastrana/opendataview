#!/bin/bash
# Description: Download CKAN data from different Open Data portals
# Purpose: Used to retrieve data for the project www.opendataview.com - Master thesis in Business Informatics at TU Wien
# Usage: 1. Paste any CKAN url ending with "/action" into the url variable (set any '#' in front of it to comment)
#        2. Execute the script with: ./ckan_data_parser.sh
#        3. All data will appear inside the same directory under /data/{ckan portal url}
# Developer: David Rodriguez-Pastrana Parareda
# Date: 25 of January 2019

#paste here any CKAN url (ending with /action)
url="
#http://www.tenerifedata.com/api/3/action
http://opendata-ajuntament.barcelona.cat/data/api/3/action
https://www.data.gv.at/katalog/api/3/action
http://opendatastore.brussels/api/3/action
https://www.offenesdatenportal.de/api/3/action
"

for urlIt in $url; do
echo "web: $urlIt"

dir=$(echo $urlIt | cut -d'/' -f3)

[ ! -d /data/$dir ] && mkdir -p ./data/$dir
[ ! -d /data/$dir/xsl ] && mkdir -p ./data/$dir/xsl
[ ! -d /data/$dir/logs ] && mkdir -p ./data/$dir/logs
[ ! -d /data/$dir/xsl_tabs ] && mkdir -p ./data/$dir/xsl_tabs

#we get all the results from all packageids
result=($(curl -s $urlIt/package_list | jq -c '.result | .[]'))

#we loop through every packageid
for packageIt in "${result[@]}"; do
  
packageId=`echo ${packageIt//\"}`

echo ""
echo "packageid: $packageId"


ckanUrl=`echo $urlIt/package_show?id=$packageId`

#we select the formats which we want to download
results=($(curl -s $ckanUrl | jq -r '.result | .resources[] | select(.url | endswith("xls","xlsx","csv")) | [.name, .url]')) 2>> ./data/$dir/logs/errors.log

csvName=`echo ${results[@]} | sed 's/\[//g;s/\]//g;s/^ //g'  | tr "," "\n" | tr -d '"' | tr " " _ | head -1`
csvUrl=`echo ${results[@]} | sed 's/\[//g;s/\]//g;s/^ //g'  | tr "," "\n" | tr -d '"' | tail -1`

#we print our results we've found
echo "filename: $csvName"
echo "fileurl: $csvUrl"

if [[ $csvUrl =~ .*\.(csv) ]]; then
   echo "CSV File: $csvUrl"
   curl -s $csvUrl > ./data/$dir/$csvName.csv 2>> ./data/$dir/logs/errors.log

#in case we got XLS or XLSX formats, we convert its stylesheets into single files in CSV format
elif [[ $csvUrl =~ .*\.(xls|xlsx) ]]; then

    cd ./data/$dir/xsl/ && curl -s -L -O $csvUrl
    cd ../../..

    echo "Converting $csvName to CSV ..."
    ssconvert -S ./data/$dir/xsl/${csvUrl##*/} ./data/$dir/$csvName.csv 2>> ./data/$dir/logs/errors.log

    [[ -f ./data/$dir/$csvName.csv.0 ]] && mv ./data/$dir/${csvName}.csv.0 ./data/$dir/${csvName}.csv
    [[ -f ./data/$dir/$csvName.csv.1 ]] && mv ./data/$dir/${csvName}.csv.1 ./data/$dir/xsl_tabs/${csvName}_1.csv
    [[ -f ./data/$dir/$csvName.csv.2 ]] && mv ./data/$dir/${csvName}.csv.2 ./data/$dir/xsl_tabs/${csvName}_2.csv
    [[ -f ./data/$dir/$csvName.csv.3 ]] && mv ./data/$dir/${csvName}.csv.3 ./data/$dir/xsl_tabs/${csvName}_3.csv
    [[ -f ./data/$dir/$csvName.csv.4 ]] && mv ./data/$dir/${csvName}.csv.4 ./data/$dir/xsl_tabs/${csvName}_4.csv
    [[ -f ./data/$dir/$csvName.csv.5 ]] && mv ./data/$dir/${csvName}.csv.5 ./data/$dir/xsl_tabs/${csvName}_5.csv

else
    echo "Other File?: $csvUrl!"
    curl -s $csvUrl > ./data/$dir/$csvName.csv 2>> ./data/$dir/logs/errors.log
fi

done
done

