# Multi-threading Project

## Description
This app collect data from a HTTP Server using concurrency principles and expose all data to a TCP Server.
Main Points:
* Program should browse all uri with an access-token that is available only 20 sec
* TCP Server expose obtained data and accept some query (This will be a multi-client server)

````        
        MENU
1. Enter ALL to see all collected data
2.Enter which field's values you want to see
3.Search which object contain key-value.Please follow the format: KEY IS VALUE
4.Enter STOP to close connection
 ATTENTION: Don't forget the magic word 
````
## Requirements
* **Java Version** : 8
* **Docker Container**: alexburlacu/pr-server
## Configuration:
Project configurations such as http port, host etc. can be configured from **application.properties** file
## Run
1. Pull docker image: ```` docker pull alexburlacu/pr-server ````
2. Run Image: ```` docker run -p 5000:5000 <imageId>````
3. Run project
4. Connect to TCP Server : ```telnet <ip> <port>```