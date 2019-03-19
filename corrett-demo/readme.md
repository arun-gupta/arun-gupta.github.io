# Corretto Demo

## Amazon Linux 2

```
ssh -i ~/.ssh/arun-us-east1.pem ec2-user@ec2-3-89-4-75.compute-1.amazonaws.com
sudo yum update -y
java -version
sudo amazon-linux-extras enable corretto8
sudo yum install java-1.8.0-amazon-corretto-devel -y
java -version
```

## Corretto on Ubuntu 18.04

```
ssh -i ~/.ssh/arun-us-east1.pem ubuntu@ec2-3-80-95-35.compute-1.amazonaws.com
java -version
sudo apt-get update && sudo apt-get install java-common
curl -O https://d2jnoze5tfhthg.cloudfront.net/java-11-amazon-corretto-jdk_11.0.2.9-2_amd64.deb
sudo dpkg --install java-11-amazon-corretto-jdk_11.0.2.9-2_amd64.deb
java -version
```

## Docker

```
docker container run amazoncorretto java -version
```

## JavaFX sample using Corretto on Windows Server 2019

. Use Remote Desktop to open up connection to Windows Server
. Explain `Tools`, `Java Platform`, configure Corretto
. Select Java file, click on `Run file`


