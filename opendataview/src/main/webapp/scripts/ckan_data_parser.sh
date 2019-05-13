#!/bin/bash
# Description: Download CKAN data from different Open Data portals
# Purpose: Used to retrieve data for the project www.opendataview.com - Master thesis in Business Informatics at TU Wien
# Usage: 1. Paste any CKAN url ending with "/action" into the url variable (set any '#' in front of it to comment)
#        2. Execute the script with: ./ckan_data_parser.sh
#        3. All data will appear inside the same directory under /data/{ckan portal url}
# Developer: David Rodriguez-Pastrana Parareda
# Date: 25 of January 2019

#paste here any CKAN url (ending with /action)
#e.g.http://www.tenerifedata.com/api/3/action/package_show?id=medicina-y-salud
#e.g.http://www.tenerifedata.com/api/3/action/package_show?id=comercio
#e.g. https://www.data.gv.at/katalog/api/3/action/package_show?id=almnutzungskartierung-nationalpark-hohe-tauern-erweiterung-fleisstaler
#e.g. https://www.data.gv.at/katalog/api/3/action/package_show?id=stadt-wien_citybikestandortewien
#interesting "metadata" fields: name, url, metadata_created, metadata_modified, maintainer_email, maintainer, metadata_linkage, update_frequency, license_id, attribute_description,schema_language
#"format": "CSV", "format": "XLS", "mimetype": "text/csv"
url="
https://opendata-ajuntament.barcelona.cat/data/api/3/action
http://www.tenerifedata.com/api/3/action
#https://www.data.gv.at/katalog/api/3/action
http://opendatastore.brussels/api/3/action
https://www.offenesdatenportal.de/api/3/action
http://data.europa.eu/euodp/data/api/3/action
https://www.govdata.de/ckan/api/3/action
https://africaopendata.org/api/3/action
http://opendata.aragon.es/catalogo/api/3/action
https://data.qld.gov.au/api/3/action
http://open.canada.ca/data/api/3/action
https://data.vulekamali.gov.za/api/3/action
"


for urlIt in $url; do
echo "web: $urlIt"

dir=$(echo $urlIt | cut -d'/' -f3)

[ ! -d /data/$dir ] && mkdir -p ./data/$dir
[ ! -d /data/$dir/xls ] && mkdir -p ./data/$dir/xls
[ ! -d /data/$dir/logs ] && mkdir -p ./data/$dir/logs
[ ! -d /data/$dir/xls_tabs ] && mkdir -p ./data/$dir/xls_tabs

#we get all the results from all packageids
result=($(curl -s $urlIt/package_list | jq -c '.result | .[]'))

#counter=1

#we loop through every packageid
for packageIt in "${result[@]}"; do


packageId=`echo ${packageIt//\"}`

echo ""
echo "packageid: $packageId"


ckanUrl=`echo $urlIt/package_show?id=$packageId`

mydir=$(pwd)
touch $mydir/data/$dir/logs/errors.log
#we select the formats which we want to download
#results=($(curl -s $ckanUrl | jq -r '.result | .resources[] | select(.url | endswith("csv","xls","xlsx")) | [.name, .url]')) 2>> $mydir/data/$dir/logs/errors.log
results=($(curl -s $ckanUrl | jq -r '.result | .resources[] | select(.format=="CSV") | [.name, .url]')) 2>> $mydir/data/$dir/logs/errors.log

csvName=`echo ${results[@]} | sed 's/\[//g;s/\]//g;s/^ //g'  | tr "," "\n" | tr -d '"' | tr " " _ | head -1`
csvUrl=`echo ${results[@]} | sed 's/\[//g;s/\]//g;s/^ //g'  | tr "," "\n" | tr -d '"' | tail -1`
csvUrl=`echo $csvUrl | xargs`

#we print our results we've found
echo "filename: $csvName"
echo "fileurl: $csvUrl"



if [[ $csvUrl =~ csv$ ]]; then

#to slower down api calls
#sleep .2

separator=";"
separator2=","

#old
cd $mydir/data/$dir/
curl -s -O -J -L "$csvUrl"
cd $mydir
#check if csv extension exists
csvName2=$(ls -t $mydir/data/$dir/ | head -n1)
echo "New file is: $mydir/data/$dir/$csvName2"

csvUrl=$(echo $csvUrl | sed 's/&/\\\\\&/g')

echo "new url2 is $csvUrl"

#csvName2=$(echo "$csvName" | sed 's/\.csv$//g')

if `head -1 $mydir/data/$dir/${csvName2} | grep -q ','`; then
echo "we have comma separator"
#we add a first column to the csv file with headername "source" and value "csvUrl" of the file
awk 'NR==1{sub(/^/,"source'"$separator2"'")} 1' $mydir/data/$dir/${csvName2} > $mydir/data/$dir/tmp.csv
awk 'NR>1{sub(/^/,"'"${csvUrl}${separator2}"'")} 1' $mydir/data/$dir/tmp.csv > $mydir/data/$dir/tmp2.csv
elif `head -1 $mydir/data/$dir/${csvName2} | grep -q ';'`; then
echo "we have semicolon separator"
#we add a first column to the csv file with headername "source" and value "csvUrl" of the file
awk 'NR==1{sub(/^/,"source'"$separator"'")} 1' $mydir/data/$dir/${csvName2} > $mydir/data/$dir/tmp.csv
awk 'NR>1{sub(/^/,"'"${csvUrl}${separator}"'")} 1' $mydir/data/$dir/tmp.csv > $mydir/data/$dir/tmp2.csv
fi
mv $mydir/data/$dir/tmp2.csv $mydir/data/$dir/${csvName2}
#((counter+=1))
#in case we got XLS or XLSX formats, we convert its stylesheets into single files in CSV format
elif [[ $csvUrl =~ .*(xls|xlsx) ]]; then

cd $mydir/data/$dir/xls/ && curl -s -J -L -O $csvUrl
cd $mydir

echo "Converting $csvName to CSV ..."
ssconvert -S $mydir/data/$dir/xls/${csvUrl##*/} $mydir/data/$dir/xls/$csvName.csv 2>> $mydir/data/$dir/logs/errors.log

#we delay 2ms because of the XLS to CSV conversion
#sleep 1

#first xls spreadsheet will be converted to csv inside "data" folder together with the other csv files
[[ -f $mydir/data/$dir/xls/$csvName.csv.0 ]] && mv $mydir/data/$dir/xls/${csvName}.csv.0 $mydir/data/$dir/${csvName}.csv
#((counter+=1))

#if `head -1 $mydir/data/$dir/$csvName.csv | grep -q ','`; then
#echo "we have comma separator" => after conversion is always comma
#we add a first column to the csv file with headername "source" and value "csvUrl" of the file
awk 'NR==1{sub(/^/,"source,")} 1' $mydir/data/$dir/$csvName.csv > $mydir/data/$dir/tmp.csv
awk 'NR>1{sub(/^/,"'"$csvUrl"',")} 1' $mydir/data/$dir/tmp.csv > $mydir/data/$dir/tmp2.csv
#fi
mv $mydir/data/$dir/tmp2.csv $mydir/data/$dir/$csvName.csv

# we transform next existing tabs (spreadsheets limited to 5) into csv inside xls_tabs" folder
[[ -f ./data/$dir/xls/$csvName.csv.1 ]] && mv ./data/$dir/xls/${csvName}.csv.1 ./data/$dir/xls_tabs/${csvName}_1.csv
[[ -f ./data/$dir/xls/$csvName.csv.2 ]] && mv ./data/$dir/xls/${csvName}.csv.2 ./data/$dir/xls_tabs/${csvName}_2.csv
[[ -f ./data/$dir/xls/$csvName.csv.3 ]] && mv ./data/$dir/xls/${csvName}.csv.3 ./data/$dir/xls_tabs/${csvName}_3.csv
[[ -f ./data/$dir/xls/$csvName.csv.4 ]] && mv ./data/$dir/xls/${csvName}.csv.4 ./data/$dir/xls_tabs/${csvName}_4.csv
[[ -f ./data/$dir/xls/$csvName.csv.5 ]] && mv ./data/$dir/xls/${csvName}.csv.5 ./data/$dir/xls_tabs/${csvName}_5.csv

: '
else
echo "Other File?: $csvUrl!"
curl -s $csvUrl > $mydir/data/$dir/$csvName.csv 2>> $mydir/data/$dir/logs/errors.log
'
fi


rm -f $mydir/data/$dir/tmp.csv $mydir/data/$dir/tmp2.csv

done
done

