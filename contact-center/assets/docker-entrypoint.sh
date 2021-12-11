#! /bin/bash 
###########################################
# Contact Center Start
###########################################

# constants
baseDir=$(cd `dirname "$0"`;pwd)

# functions

# main 
[ -z "${BASH_SOURCE[0]}" -o "${BASH_SOURCE[0]}" = "$0" ] || return
cd $baseDir
./mysql.setup.db.sh
./mysql.upgrade.db.sh

if [ $? -eq 0 ]; then
    if [ -f /tmp/ROOT ]; then
        rm -rf /tmp/ROOT
    fi
    java -jar contact-center.jar
else
    echo "Fail to resolve mysql database instance."
    exit 1
fi
