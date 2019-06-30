# dex-finder

## 简介

`dex-finder` 是一个基于 dexlib2 实现的简单脚本，用于确认一个类所在的 dex 的位置，设计时是为了以下两种情况使用的：

1. 现在的 apk 大部分都已经是 multi-dex 格式了，使用 JEB 打开整个 apk 很慢，使用 `dex-finder` 可以快速定位到目标

2. 应用程序使用的类，可能来自动态加载的其他 dex，我们只需要把整个目录都拖出来，逐个文件运行一遍就可以找到它 

站在巨人的肩膀上，作为一个小工具，还是比较好用的。

## 下载地址

TODO: release

## 用法
```
usage: java -jar [-d] [-r] [-us] [-f file/directory] [-c classname]
 -c,--class <arg>   Class you want to find.
 -d,--debug         Enable debug log.
 -f,--file <arg>    File or directory to be scanned.
 -h,--help          Show help.
 -r,--recursive     Recursive scan files.
 -us,--use-sig      Use class signature. If enable, use Ljava/lang/String; .
java -jar dex-finder.jar -f demo.apk -c com.example.Activity
java -jar dex-finder.jar -f classed.dex -c com.example.Activity
java -jar dex-finder.jar -f /path/unzip_result/ -c com.example.Activity
```

`-c` 指定想要查的类（可以同时指定多个）
`-f` 指定搜索的文件或文件夹（可以同时指定多个）
    可以是 dex
    可以是 apk
    可以是 apk 直接解压后的文件夹
`-r` 如果是文件夹，是否递归搜索
`-us` 使用类的签名。例如：默认情况输入 java.lang.String，开启 use-sig 后，需要输入 Ljava/lang/String;
`-d` 开启详细日志（其实也没多详细） 

## 示例

以微博国际版为例，它有两个 dex 文件，无需解压，直接调用，我们随便挑 3 个类

```
➜  dex-finder java -jar dex-finder-1.0.0.jar -c com.weico.international.activity.MainFragmentActivity -c de.greenrobot.event.EventBus -c com.weico.international.utility.LogUtil -f /tmp/weico-no-ads/weico.apk -r 
[main] INFO com.leadroyal.dex.CommandParser - parse command success
[main] INFO com.leadroyal.dex.ClassFinder - Check /tmp/weico-no-ads/weico.apk->classes.dex finished
[main] INFO com.leadroyal.dex.ClassFinder - Check /tmp/weico-no-ads/weico.apk->classes2.dex finished
This is report!
====Known classes====
Lde/greenrobot/event/EventBus; found @ /tmp/weico-no-ads/weico.apk->classes2.dex
Lcom/weico/international/activity/MainFragmentActivity; found @ /tmp/weico-no-ads/weico.apk->classes2.dex
Lcom/weico/international/utility/LogUtil; found @ /tmp/weico-no-ads/weico.apk->classes.dex
====Unknown classes====
```

再找一个大型的 APP，例如淘宝的 apk，它会动态加载 `libsgmain.so` 这个 dex 文件

使用指定单独 apk 的方式，有一个找不到的类

```
➜  dex-finder java -jar dex-finder-1.0.0.jar -c c8.STqg -c com.alibaba.wireless.security.framework.IRouterComponent -c com.alibaba.wireless.security.mainplugin.SecurityGuardMainPlugin -f /tmp/tb.apk -r
[main] INFO com.leadroyal.dex.CommandParser - parse command success
[main] INFO com.leadroyal.dex.ClassFinder - Check /tmp/tb.apk->classes.dex finished
[main] INFO com.leadroyal.dex.ClassFinder - Check /tmp/tb.apk->classes2.dex finished
[main] INFO com.leadroyal.dex.ClassFinder - Check /tmp/tb.apk->classes3.dex finished
[main] INFO com.leadroyal.dex.ClassFinder - Check /tmp/tb.apk->classes4.dex finished
[main] INFO com.leadroyal.dex.ClassFinder - Check /tmp/tb.apk->classes5.dex finished
[main] INFO com.leadroyal.dex.ClassFinder - Check /tmp/tb.apk->classes6.dex finished
[main] INFO com.leadroyal.dex.ClassFinder - Check /tmp/tb.apk->classes7.dex finished
[main] INFO com.leadroyal.dex.ClassFinder - Check /tmp/tb.apk->classes8.dex finished
[main] INFO com.leadroyal.dex.ClassFinder - Check /tmp/tb.apk->classes9.dex finished
[main] INFO com.leadroyal.dex.ClassFinder - Check /tmp/tb.apk->classes10.dex finished
[main] INFO com.leadroyal.dex.ClassFinder - Check /tmp/tb.apk->classes11.dex finished
[main] INFO com.leadroyal.dex.ClassFinder - Check /tmp/tb.apk->classes12.dex finished
[main] INFO com.leadroyal.dex.ClassFinder - Check /tmp/tb.apk->classes13.dex finished
[main] INFO com.leadroyal.dex.ClassFinder - Check /tmp/tb.apk->classes14.dex finished
This is report!
====Known classes====
Lcom/alibaba/wireless/security/framework/IRouterComponent; found @ /tmp/tb.apk->classes.dex
Lc8/STqg; found @ /tmp/tb.apk->classes.dex
====Unknown classes====
Lcom/alibaba/wireless/security/mainplugin/SecurityGuardMainPlugin;
```

解压后，指定目录，全部都找到了

```
➜  dex-finder java -jar dex-finder-1.0.0.jar -c c8.STqg -c com.alibaba.wireless.security.framework.IRouterComponent -c com.alibaba.wireless.security.mainplugin.SecurityGuardMainPlugin -f /tmp/tt -r
[main] INFO com.leadroyal.dex.CommandParser - parse command success
[main] INFO com.leadroyal.dex.ClassFinder - Check /tmp/tt/classes9.dex finished
[main] INFO com.leadroyal.dex.ClassFinder - Check /tmp/tt/classes8.dex finished
[main] INFO com.leadroyal.dex.ClassFinder - Check /tmp/tt/classes11.dex finished
[main] INFO com.leadroyal.dex.ClassFinder - Check /tmp/tt/classes10.dex finished
[main] INFO com.leadroyal.dex.ClassFinder - Check /tmp/tt/classes12.dex finished
[main] INFO com.leadroyal.dex.ClassFinder - Check /tmp/tt/classes3.dex finished
[main] INFO com.leadroyal.dex.ClassFinder - Check /tmp/tt/classes2.dex finished
[main] INFO com.leadroyal.dex.ClassFinder - Check /tmp/tt/classes13.dex finished
[main] INFO com.leadroyal.dex.ClassFinder - Check /tmp/tt/classes.dex finished
[main] INFO com.leadroyal.dex.ClassFinder - Check /tmp/tt/classes6.dex finished
[main] INFO com.leadroyal.dex.ClassFinder - Check /tmp/tt/classes7.dex finished
[main] INFO com.leadroyal.dex.ClassFinder - Check /tmp/tt/classes14.dex finished
[main] INFO com.leadroyal.dex.ClassFinder - Check /tmp/tt/lib/armeabi/libsgmisc.so->classes.dex finished
[main] INFO com.leadroyal.dex.ClassFinder - Check /tmp/tt/lib/armeabi/libsgavmp.so->classes.dex finished
[main] INFO com.leadroyal.dex.ClassFinder - Check /tmp/tt/lib/armeabi/libservicefakedex.so->classes.dex finished
[main] INFO com.leadroyal.dex.ClassFinder - Check /tmp/tt/lib/armeabi/libsgnocaptcha.so->classes.dex finished
[main] INFO com.leadroyal.dex.ClassFinder - Check /tmp/tt/lib/armeabi/libpreverify.so finished
[main] INFO com.leadroyal.dex.ClassFinder - Check /tmp/tt/lib/armeabi/libsgsecuritybody.so->classes.dex finished
[main] INFO com.leadroyal.dex.ClassFinder - Check /tmp/tt/lib/armeabi/libsgmain.so->classes.dex finished
This is report!
====Known classes====
Lcom/alibaba/wireless/security/mainplugin/SecurityGuardMainPlugin; found @ /tmp/tt/lib/armeabi/libsgmain.so->classes.dex
Lc8/STqg; found @ /tmp/tt/classes.dex->./
Lcom/alibaba/wireless/security/framework/IRouterComponent; found @ /tmp/tt/classes.dex->./
====Unknown classes====
```


## 编译

```
gradle build
```

会在 `build/libs/` 下获得由 springboot 打包过的可独立运行的文件。

