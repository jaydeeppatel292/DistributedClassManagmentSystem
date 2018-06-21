# DistributedClassManagmentSystem
Steps to run project:
1) Center.idl file presentin the src folder should be compiled to generate Corba compoments. It is compiled using the below command:
idlj -fall Center.idl

2) Next, we need to start the orbd server using below command:
after navigating to the project directory:
start orbd -ORBInitialPort 1050

3) Now, we just need to run below classes:
StartServer.java
This will start the multiple servers on the port and host as mentioned in the location.json file. 
StartClient.java
This will start the client.

(Note: the file is already compiled and the Corba components are present, so step 1 is not required to be done.)  

