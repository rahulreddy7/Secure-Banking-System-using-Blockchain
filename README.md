# ss-banking-api

spring boot application

Amazon EWS Instance: 
Instance ID: i-0f70444a54c1fee3b
Public DNS (IPv4): ec2-18-222-64-16.us-east-2.compute.amazonaws.com
Instance state: running
IPv4 Public IP: 18.222.64.16
Instance type: t2.micro
Private IPs: 172.31.42.7
Private DNS: ip-172-31-42-7.us-east-2.compute.internal

To SSH INTO AWS:
ssh -i soft-sec.pem ubuntu@18.222.64.16 
//soft-sec.pem file is in the codebe home directory

To directly log into the MONGODB instance running on AWS:
mongo -u admin -p myadminpassword 18.222.64.16/admin

To run java code:
1) Clone the repo
2) Import project into eclipse as "Existing Maven Projects"
3) Right click to "Run As" -> Spring Boot App/Java Application

Test sample APIs:
1) GET API - URL: localhost:8081/acc/alldata
2) GET API - URL: localhost:8081/users/getdata
Response should be status 200.

MongoDB Structure:
> show dbs
admin   0.000GB
config  0.000GB
local   0.000GB
mydb    0.000GB
> use mydb
switched to db mydb
> db.getCollectionNames()
[ "Accounts", "Transactions", "Users", "employee", "workflow" ]
