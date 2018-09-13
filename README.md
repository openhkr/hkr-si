#氢氪出行-支付网关 hkr-si

##hkr-si是什么
hkr-si是一个开源的，基于spring boot开发的支付网关。

##hkr-si有哪些功能
###可以接入支付宝支付
###可以接入微信支付
>启动服务。调用sign接口，可以得到一串app使用的参数，app使用这串参数，可以调用支付宝app，发起支付。

>resources路径下有PayDemo.ipa文件，可以将这个文件安装在运行了IOS系统的手机中。将接口返回的sign贴到发起支付的输入框中，即可跳转到三方支付APP进行支付。
###用户余额管理

##如何部署
###1.下载代码

###2.修改配置
#### application.yml

1. 修改这个配置文件的 mysql  redis信息。
2. 替换notify url中host和port信息。这个地址是第三方支付机构支付成功后，回调我们系统的地址。

#### com.reachauto.hkr.si.config.WechatConfigure

将KEY,APP_ID,MCH_ID,SUB_MCH_ID几个常量替换为你自己在微信支付平台申请得到的信息。
讲你在微信平台申请到的HTTPS证书文件 apiclient_cert.p12放到resources路径下。

#### com.reachauto.hkr.si.config.AliPayConfig
在支付宝商户平台签约并设置好应用的公钥私钥
[公钥私钥设置请参考]（https://docs.open.alipay.com/291/105972）
将APP_ID,PRIVATE_KEY,ALI_PUB_KEY几个常量替换为你申请得到的信息。
     
###3.编译打包
>工程使用maven进行依赖管理。在有maven环境的情况下，执行mvn install命令，会在target目录下生成hkr-si.jar。
这是个可执行的spring boot文件。

###4.启动部署
>使用java -jar hkr-si.jar命令启动，初始堆内存建议大于等于512M。

##变动履历


##FAQ
