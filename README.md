# EM integration 
Integration EM manuscript system with GigaDB database

## Requirements
1. JDK 1.7
2. Import all jars in the lib directory

## Setting
All parameters setting in the /configuration/setting.xml 

1. databaseUrl: GigaDB production database version
2. databaseUserName: e.g. gigadb
3. databasePassword:
4. savedir: EM integration excel spreedsheets and log files saving directory.
5. mailto: email address to receive the notification for the dataset status changing. e.g. test@gmail.com, test2@gmail.com
6. link: dataset url in the email content. e.g if you deploy in aws ec2 http://gigadb-staging-jesse.gigatools.net/
