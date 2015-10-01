logfile=$1

echo " [+] BUILDING NCC"
mvn clean package -Dmaven.test.skip=true >> $logfile

