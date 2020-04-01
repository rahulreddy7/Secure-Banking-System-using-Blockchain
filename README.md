# ss-banking-api

spring boot application

Amazon EC2 Instance: 
Instance ID: i-0f70444a54c1fee3b <br />
Public DNS (IPv4): ec2-18-222-64-16.us-east-2.compute.amazonaws.com <br />
Instance state: running <br />
IPv4 Public IP: 18.222.64.16 <br />
Instance type: t2.micro <br />
Private IPs: 172.31.42.7 <br />
Private DNS: ip-172-31-42-7.us-east-2.compute.internal <br />

To SSH INTO AWS:<br />
chmod 400 soft-sec.pem<br />
ssh -i soft-sec.pem ubuntu@18.222.64.16 <br />
//soft-sec.pem file is in the codebe home directory<br />

To directly log into the MONGODB instance running on AWS:<br />
mongo -u admin -p myadminpassword 18.222.64.16/admin<br />

To run java code:
1) Clone the repo
2) Import project into eclipse as "Existing Maven Projects"
3) Right click to "Run As" -> Spring Boot App/Java Application

Test sample APIs:
1) GET API - URL: localhost:8081/acc/alldata
2) GET API - URL: localhost:8081/users/getdata
Response should be status 200.

MongoDB Structure:
1) $ show dbs
admin   0.000GB
config  0.000GB
local   0.000GB
mydb    0.000GB
2) $ use mydb
switched to db mydb
3) $ db.getCollectionNames()
[ "Accounts", "Transactions", "Users", "employee", "workflow" ]




Work distibution:<br />
Branch- nchen19-patch-1 <br />
Authors : Nanqiao Chen, Anay Paul<br />

scp -i soft-sec.pem /Users/devansh/Desktop/sbs-microservice-0.0.1-SNAPSHOT.jar ubuntu@18.222.64.16:~/
 ps -ef | grep java
 sudo kill -9 5762
nohup java -jar sbs-microservice-0.0.1-SNAPSHOT.jar > new_deploy.log 2>&1 &
tail -f new_deploy.log



tier1emp@protonmail.com<br />
tier1pass<br />
"username":"tier1",<br />
"employee_password": "tier1pass"<br />
<br />
tier2emp@protonmail.com<br />
tier2pass<br />
"username":"tier2",<br />
"employee_password": "tier2pass",<br />
<br />
adminemp@protonmail.com<br />
adminpass<br />
"username":"admin",<br />
"employee_password": "adminpass"<br />