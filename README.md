# myChat_server
###### 基于spring boot和netty开发的简易im服务器端
###### 数据库使用mysql，持久层框架为spring-data-jpa
###### 集成文件服务器FastDFS客户端
###### 使用前需先部署mysql和FastDFS

###### 如果觉得FastDFS搭建麻烦，可以通过docker一键部署
> Dockerfile

```
# 使用超小的Linux镜像alpine
FROM alpine:3.6

MAINTAINER YoungCoding <bingdianfeishui@gmail.com>

ENV HOME /root

# 安装准备
RUN    apk update \
        && apk add --no-cache --virtual .build-deps bash gcc libc-dev make openssl-dev pcre-dev zlib-dev linux-headers curl gnupg libxslt-dev gd-dev geoip-dev

# 下载fastdfs、libfastcommon、nginx插件的源码
RUN     cd /root \
        && curl -fSL https://github.com/happyfish100/libfastcommon/archive/V1.0.36.tar.gz -o fastcommon.tar.gz \
        && curl -fSL  https://codeload.github.com/happyfish100/fastdfs/tar.gz/V5.11 -o fastfs.tar.gz \
        && curl -fSL  https://github.com/happyfish100/fastdfs-nginx-module/archive/master.tar.gz -o nginx-module.tar.gz \
        && tar zxf fastcommon.tar.gz \
        && tar zxf fastfs.tar.gz \
        && tar zxf nginx-module.tar.gz

# 安装libfastcommon
RUN     cd ${HOME}/libfastcommon-1.0.36/ \
        && ./make.sh \
        && ./make.sh install

# 安装fastdfs v5.11
RUN     cd ${HOME}/fastdfs-5.11/ \
        && ./make.sh \
        && ./make.sh install

# 配置fastdfs: base_dir
RUN     cd /etc/fdfs/ \
        && cp storage.conf.sample storage.conf \
        && cp tracker.conf.sample tracker.conf \
        && cp client.conf.sample client.conf \
        && sed -i "s|/home/yuqing/fastdfs|/var/local/fdfs/tracker|g" /etc/fdfs/tracker.conf \
        && sed -i "s|/home/yuqing/fastdfs|/var/local/fdfs/storage|g" /etc/fdfs/storage.conf \
        && sed -i "s|/home/yuqing/fastdfs|/var/local/fdfs/storage|g" /etc/fdfs/client.conf

# 获取nginx源码，与fastdfs插件一起编译
RUN     cd ${HOME} \
        && curl -fSL http://nginx.org/download/nginx-1.12.2.tar.gz -o nginx-1.12.2.tar.gz \
        && tar zxf nginx-1.12.2.tar.gz \
        && chmod u+x ${HOME}/fastdfs-nginx-module-master/src/config \
        && echo 'ngx_module_incs="/usr/include/fastdfs /usr/include/fastcommon/"'>> ${HOME}/fastdfs-nginx-module-master/src/config \
        && echo 'CORE_INCS="$CORE_INCS /usr/include/fastdfs /usr/include/fastcommon/"'>> ${HOME}/fastdfs-nginx-module-master/src/config \
        && cd nginx-1.12.2 \
        && ./configure --add-module=${HOME}/fastdfs-nginx-module-master/src \
        && make && make install

# 设置nginx和fastdfs联合环境，并配置nginx
RUN     cp ${HOME}/fastdfs-nginx-module-master/src/mod_fastdfs.conf /etc/fdfs/ \
        && sed -i "s|^store_path0.*$|store_path0=/var/local/fdfs/storage|g" /etc/fdfs/mod_fastdfs.conf \
        && sed -i "s|^url_have_group_name =.*$|url_have_group_name = true|g" /etc/fdfs/mod_fastdfs.conf \
        && cd ${HOME}/fastdfs-5.11/conf/ \
        && cp http.conf mime.types anti-steal.jpg /etc/fdfs/ \
        && echo -e "\
events {\n\
    worker_connections  1024;\n\
}\n\
http {\n\
    include       mime.types;\n\
    default_type  application/octet-stream;\n\
    server {\n\
        listen 8888;\n\
        server_name localhost;\n\

        location ~ /group[0-9]/M00 {\n\
            ngx_fastdfs_module;\n\
        }\n\
    }\n\
}">/usr/local/nginx/conf/nginx.conf

# 清理文件
RUN rm -rf ${HOME}/*
RUN apk del .build-deps gcc libc-dev make openssl-dev linux-headers curl gnupg libxslt-dev gd-dev geoip-dev
RUN apk add bash pcre-dev zlib-dev


# 配置启动脚本，在启动时中根据环境变量替换nginx端口、fastdfs端口
# 默认nginx端口
ENV WEB_PORT 8888
# 默认fastdfs端口
ENV FDFS_PORT 22122
# 创建启动脚本
RUN     echo -e "\
mkdir -p /var/local/fdfs/storage/data /var/local/fdfs/tracker; \n\
ln -s /var/local/fdfs/storage/data/ /var/local/fdfs/storage/data/M00; \n\n\
sed -i \"s/listen\ .*$/listen\ \$WEB_PORT;/g\" /usr/local/nginx/conf/nginx.conf; \n\
sed -i \"s/http.server_port=.*$/http.server_port=\$WEB_PORT/g\" /etc/fdfs/storage.conf; \n\n\
if [ \"\$IP\" = \"\" ]; then \n\
    IP=`ifconfig eth0 | grep inet | awk '{print \$2}'| awk -F: '{print \$2}'`; \n\
fi \n\
sed -i \"s/^tracker_server=.*$/tracker_server=\$IP:\$FDFS_PORT/g\" /etc/fdfs/client.conf; \n\
sed -i \"s/^tracker_server=.*$/tracker_server=\$IP:\$FDFS_PORT/g\" /etc/fdfs/storage.conf; \n\
sed -i \"s/^tracker_server=.*$/tracker_server=\$IP:\$FDFS_PORT/g\" /etc/fdfs/mod_fastdfs.conf; \n\n\
/etc/init.d/fdfs_trackerd start; \n\
/etc/init.d/fdfs_storaged start; \n\
/usr/local/nginx/sbin/nginx; \n\
tail -f /usr/local/nginx/logs/access.log \
">/start.sh \
&& chmod u+x /start.sh

# 暴露端口。改为采用host网络，不需要单独暴露端口
# EXPOSE 80 22122

ENTRYPOINT ["/bin/bash","/start.sh"]
```
> docker-compose.yml

```
version: '3.0'

services:
    fastdfs:
        build: .
        image: youngcoding/fastdfs:5.11
        container_name: fastdfs
        environment:
            - WEB_PORT=8899
            - IP=104.198.89.63
        volumes:
            - /root/docker/fdfs/data:/var/local/fdfs
        privileged: true
        network_mode: "host"
```
启动命令:   docker-compose up -d
