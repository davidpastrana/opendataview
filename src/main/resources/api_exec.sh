#!/bin/bash

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

result=($(curl -s $urlIt/package_list | jq -c '.result | .[]'))

for packageIt in "${result[@]}"; do
  #echo "name isss ${i}"
  
packageId=`echo ${packageIt//\"}`

echo ""
echo "packageid: $packageId"
#ckanUrl=`echo http://www.tenerifedata.com/api/3/action/package_show?id=asociaciones-ciudadania`
ckanUrl=`echo $urlIt/package_show?id=$packageId`

#results=($(curl -s $ckanUrl | jq -r '.result | .resources[] | select(.mimetype=="text/csv" | contains("csv","xls","xlsx")) | .name, .url'))

results=($(curl -s $ckanUrl | jq -r '.result | .resources[] | select(.url | endswith("xls","xlsx","csv")) | [.name, .url]')) 2>> ./data/$dir/logs/errors.log

csvName=`echo ${results[@]} | sed 's/\[//g;s/\]//g;s/^ //g'  | tr "," "\n" | tr -d '"' | tr " " _ | head -1`
csvUrl=`echo ${results[@]} | sed 's/\[//g;s/\]//g;s/^ //g'  | tr "," "\n" | tr -d '"' | tail -1`

#csvName=${results[0]}
#csvUrl=${results[1]}

echo "filename: $csvName"
echo "fileurl: $csvUrl"

if [[ $csvUrl =~ .*\.(csv) ]]; then
   echo "CSV File: $csvUrl"
   curl -s $csvUrl > ./data/$dir/$csvName.csv 2>> ./data/$dir/logs/errors.log
elif [[ $csvUrl =~ .*\.(xls|xlsx) ]]; then


    #curl -s -L -O $csvUrl > ./data/$dir/xsl/${csvUrl##*/}
    cd ./data/$dir/xsl/ && curl -s -L -O $csvUrl
    cd ../../..
    #file2=`echo $file`
    echo "Converting $csvName to XSL!"
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

