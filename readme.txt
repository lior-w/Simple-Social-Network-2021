to run the server, oprn the trminal from the SPL-NET file and run the commands:

1) mvn clean
2)mvn package
3) 
*for REACTOR, and port = 7777, and number Of Threads = 4 run:
mvn exec:java -Dexec.mainClass=bgu.spl.net.impl.BGS.ReactorMain -Dexec.args="7777 4"
*for the TCP, and port = 7777 run:
mvn exec:java -Dexec.mainClass=bgu.spl.net.impl.BGS.TPCMain -Dexec.args="7777"

then, open the terminal from the Client file and run the commands:

1)make clean
2)make

for each client run the command:

bin/BGSclient

examples messages:

REGISTER morty 1234 12-12-2012
LOGIN morty 1234 1
LOGOUT
FOLLOW 0 Rick
POST I hate Rick
PM Rick hello Rick
LOGSTAT
STAT Rick|Bird-person
BLOCK Rick


the filtered words stored in impl.BGS.DataBase