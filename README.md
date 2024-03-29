### 网关NTM
开箱即用的网关集成，支持单机和分布式部署。

### 项目模块
* gk5-ntm-common: 基础包
* gk5-ntm-rpc: 网关RPC版本
* gk5-ntm-standalone: 网关单机版本
* gk5-ntm-iam: 权限控制
* gk5-ntm-sample: 案例
* gk5-ntm-doc: 文档

### 感谢【排名不分先后】
* spring
* mybatis
* jackson
* mybatis-plus
* dubbo
* fastjson

感谢其他优秀的开源社区和优秀开发者

### 业务统一接口
#### 请求路径
```
${domain}:${port}/ntm
```
#### 请求方式
GET、POST
> POST采用form表单提交

#### 请求结构
参数|类型|允许为空|载体|说明|案例
:---:|:---:|:---:|:---:|:---:|:---:
ntmApi|String|N|Body|名称|chech.loginIn
v|Number|N|Body|版本|1
ts|Number|N|Header|时间戳(毫秒)|1565769717936
lng|String|N|Header|经度|"23.34"
lat|String|N|Header|纬度|"23.34"
ttid|String|N|Header|软硬件信息|HUAWEI P20@Android@Android 7.0@1.0.2
appid|String|N|Header|系统标识|chech
ia|String|Y|Header|32位认证标识Token|0c7b79497a8b3d6a8cd06b21883e6a28
oia|String|Y|Header|32位认证标识(旧)|0c7b79497a8b3d6a8cd06b21883e6a28
channel|String|N|Header|渠道|hz
sign|String|N|Body|签名|备注
data|String|Y|Body|业务参数JSON格式，必须经过URL编码|{"mobile":"13819468874"}

##### ttid=brand@platform@system@version
字段|类型|允许为空|说明|案例
:---:|:---:|:---:|:---:|:---:
brand|String|N|厂牌机型|"HUAWEI P20" 或 "Apple SE" 或 "Chrome/76.0.3809.100"
platform|String|N|平台|"Android" 或 "IOS" 或 "H5"
system|String|N|系统版本|"Android 7.0" 或 "ios11.0.0" 或 "Macintosh; Intel Mac OS X 10_14_6"
version|String|N|客户端版本(大版本号.小版本号.bugfix版本)|1.0.0


##### sign
对下面拼接的字符串加密，加密方式RSA2048
```
api=%s&appid=%s&channel=%s&data=%s&ia=%s&ts=%s&ttid=%s&v=%s
```
> 如果某个参数为空，带空字符串进行加密

公钥：
```
MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAsLp2NLUOqRdWItvH3zWJsdWvveUvWj/B+XRzRuzDTvsaMgHAfE7zpjEqFj4/BVw4LThOseLXwsxt4RHxEpykL/OMpMUz8MJmN3gKVID43tN75H0b6IL46ymMNwOcPt9EfEGdnUaTJEwxG4VpKIGWxDOUnEybS7QuUztJlNMByxEYR69dLUSSOCM+TbQNIVeVVXixPhI43pb5umR/3mQ+XHTuAZ2zoMA4OsrEO/052Wdv66dmNtaK8eTwwrGcmHOCi5fQSWT2gi3Vq592461w3H8cbdfHU+3cgS0HkG4u3D/cU8eK8lUSUKxIY9wcv2nDdNWpYOwi5oY06sNF9UpSwQIDAQAB
```

#### 返回结构
参数|类型|允许为空|说明|案例
:---:|:---:|:---:|:---:|:---:
code|Number|N|状态|0-成功 其他-异常
msg|String|N|状态陈述|成功
data|Object|N|业务数据|{"mobile":"18205710401"}
api|String|N|接口|"chech.test.demo"
v|Number|N|版本|1
ia|String|Y|32位认证标识|0c7b79497a8b3d6a8cd06b21883e6a28


基础接口：
* [接口文档](/gk5-ntm-doc/DOC-API.MD)
* [接口参数文档](/gk5-ntm-doc/DOC-APIPARAM.MD)
* [接口统计文档](/gk5-ntm-doc/DOC-APICOLLECT.MD)


### NGINX 配置
```nginx
#user  nobody;
worker_processes  3;

#error_log  logs/error.log;
#error_log  logs/error.log  notice;
#error_log  logs/error.log  info;

#pid        logs/nginx.pid;


events {
    worker_connections  1024;
}


http {
    include       mime.types;
    default_type  application/octet-stream;

    sendfile        on;
    keepalive_timeout  65;

    server {
        # HTTPS传输
        listen       443 ssl;
        # 反向代理的域名
        server_name  ntmx-internal.zdautoservice.com;


        ssl_certificate ../cert/zd_new.pem;
        ssl_certificate_key ../cert/zd_new.key;

        ssl_session_cache shared:SSL:1m;
        ssl_session_timeout 5m;

        ssl_ciphers HIGH:!aNULL:!MD5;
        ssl_prefer_server_ciphers on;


        #error_page  404              /404.html;
        location / {
            # CORS 配置
            add_header 'Access-Control-Allow-Origin' '*';
            add_header 'Access-Control-Allow-Methods' 'GET,POST,OPTIONS';
            # 是否允许cookie传输
            add_header 'Access-Control-Allow-Credentials' 'true';
            add_header 'Access-Control-Allow-Headers' 'Authorization,Content-Type,Accept,Origin,User-Agent,DNT,Cache-Control,X-Mx-ReqToken,X-Data-Type,X-Requested-With,X-Data-Type,X-Auth-Token,channel,ia,oia,v,ts,lng,lat,ttid,appid';
            
            if ($request_method = 'OPTIONS') {
                add_header Access-Control-Allow-Origin *;
                add_header Access-Control-Max-Age 1728000;
                add_header Access-Control-Allow-Methods GET,POST,OPTIONS;
                add_header Access-Control-Allow-Headers  'Authorization,Content-Type,Accept,Origin,User-Agent,DNT,Cache-Control,X-Mx-ReqToken,X-Data-Type,X-Requested-With,X-Data-Type,X-Auth-Token,channel,ia,oia,v,ts,lng,lat,ttid,appid';
                add_header Content-Type' 'application/x-www-form-urlencoded;charset=utf-8';
                add_header Content-Length 0 ;
                return 204;
            }
            # 根据自己的容器端口修改
            proxy_pass http://127.0.0.1:8085/;
            proxy_set_header Host $host;
            proxy_set_header X-Real-IP $remote_addr;
            proxy_set_header REMOTE-HOST $remote_addr;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
       }


        # redirect server error pages to the static page /50x.html
        #
        error_page   500 502 503 504  /50x.html;
        location = /50x.html {
            root   html;
        }
    }
}
```