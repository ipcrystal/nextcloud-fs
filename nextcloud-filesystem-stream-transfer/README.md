# Nextcloud File System Stream Transfer 
### Release
[release docker-compose.zip](https://github.com/mimotronik/nextcloud-filesystem-stream-transfer/releases)
### Deploy
```shell script
unzip docker-compose.zip

cd docker-compose

docker-compose up -d
```

### Notice
- Need changing accepted domain in config.php
```shell script
docker volume ls

cd .....

cd config

vi config.php
```

