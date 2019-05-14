
### 前言

本篇是为了记录在适配Android6.0和7.0的时候出现的思考,以下内容大多数参考了大佬们的文章,仅做记录.

看完之后希望能解决以下问题.

### 疑问:

1.  Android内部存储、外部存储、SD卡区别在哪里？
2.  getDataDirectory，getFilesDir，getCacheDir，getDir，getExternalStorageDirectory，getExternalStoragePublicDirectory，getExternalFilesDir，getExternalCacheDir，getExternalCacheDir，getRootDirectory等方法的区别和关联？
3.  哪些目录需要读写权限？读写SD卡一定要权限?

### 问题一

>内部存储,外部存储,SD卡区别;


![image.png](https://upload-images.jianshu.io/upload_images/4916021-3ccd175c8cc7eb24.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

![图片出自底部参考一](http://upload-images.jianshu.io/upload_images/4916021-dcfa6963fd475f87?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

在Android4.4以下的时候,系统仅有内部存储和外部存储(SD卡),然后再4.4之后,系统支持了手机内置外部存储和外置外部存储(SD卡);到这里可能有个疑问getExternalStorageDirectory(),到底是SD卡还是手机内置外部存储?这个问题引入第二个问题.

### 问题二

>不同安卓版本下getDataDirectory，getFilesDir，getCacheDir，getDir，getExternalStorageDirectory，getExternalStoragePublicDirectory，getExternalFilesDir，getExternalCacheDir，getExternalCacheDir，getRootDirectory

- getExternalStorageDirectory(),到底是SD卡还是手机内置外部存储?

    Android4.4以前,getExternalStorageDirectory可以获取外置SD卡根路径;4.4之后外部存储就包含两部分了（内置外部存储和外置SD卡），其中通过getExternalStorageDirectory获取的内置外部存储，而外置SD卡的根路径系统并没有明确给出方法,网上有许多方案,但是可以使用getExternalFilesDirs来获取,虽然不是SD卡的根路径,但是可以选择使用sd卡上的私有路径.而且上述方法是获取文件夹的绝对路径,那么想想办法根路径还是能拿得到的,但是感觉不优雅.

- Environment.getDataDirectory()

内部存储的根路径，/data

- Context.getFilesDir

这个方法是获取某个应用在内部存储中的files路径，/data/user/0/pakageName/files

- Context.getCacheDir

这个方法是获取某个应用在内部存储中的cache路径,/data/user/0/pakageName/cache

- Context.getDir("storage", Context.MODE_PRIVATE)

这个方法是获取某个应用在内部存储中的自定义路径,/data/user/0/pakageName/app_storage

- Environment.getExternalStorageDirectory

获取外部存储的根路径,4.4以下指的是sd卡，4.4开始值得是内部外置存储,/storage/emulated/0

- Environment.getExternalStoragePublicDirectory

外部共有目录，例如图像，音乐，用户可以看到，图库可以检索到；/storage/emulated/0/type

- Context.getExternalFilesDir

某个应用在外部存储中的files路径, /storage/emulated/0/Android/data/pakageName/files/

- Context.getExternalCacheDir

这个方法是获取某个应用在外部存储中的cache路径

- Environment.getDownloadCacheDirectory()

/data/cache,内部存储的cache目录

- Environment.getRootDirectory() = /system

存放系统文件，物理上在内部存储

### 问题三
>哪些目录需要读写权限？

从上面可以观察到：用Context都与App自己的存储路径有关，Environment都是公共的，从4.4开始，读取或写入应用私有目录中的文件不再需要 READ_EXTERNAL_STORAGE 或 WRITE_EXTERNAL_STORAGE 权限！当然包括在外部存储里的；但是要注意，既然app相关的路径，app卸载的时候会对应的路径下的文件会被删除，而且不会被系统图库所扫描到，如果想让系统扫描到，那就放到Evironment相关的路径下。值得注意的是：**不是私有路径下的存储是需要动态获取读写权限的**.

### 额外补充

我们开发的时候应该怎么合理的使用存储:

- 隐私
- 功能

考虑因素:隐私;如果你不想让其它app扫描到或者被用户删除,那么你可以放到内部存储里（当内部存储不足的时候系统会帮你删了，而且不打招呼，所以存储的文件要小）,如果并不是那么重要,而且也不需要暴露给外部展示,放在外部存储的私有目录下是极好的,优点:不用权限,存储比较大，安全性相对较高。放在外部公共目录下的，那就没有啥子安全性了，谁都能看。

考虑因素:功能;SD卡和外部存储的选用,外部存储比SD卡可靠,当外部存储不足的时候考虑外部SD卡存储(自己的想法),还有用户想保存图片(此时需要权限),要么放到共有的图片路径下,要么放到自己外部文件夹下,而且用的文件一定要记得用完删除.这里我们在适配6.0读写权限的时候就能省下很多力气,因为在此之前,很多文件都放到了,getExternalStorageDirectory,导致要添加权限检查的地方很多,这是个契机,修改存储路径到外部的私有路径下,一是不要权限,二是避免了App删除的时候留下很多垃圾,对用户好,你也轻松.

此外:

Android为了兼容类SD卡,外部存储,ContextCompat.getExternalFilesDirs()，建议使用这个方法获取外部存储的位置。

因为从 Android 4.4 开始，可通过调用 getExternalFilesDirs() 来同时访问两个位置(外部存储和sd卡)，该方法将会返回包含各个位置条目的 File 数组。 数组中的第一个条目被视为外部主存储；除非该位置已满或不可用，否则应该使用该位置。 如果您希望在支持 Android 4.3 和更低版本的同时访问两个可能的位置，请使用支持库中的静态方法 ContextCompat.getExternalFilesDirs()。 在 Android 4.3 和更低版本中，此方法也会返回一个 File 数组，但其中始终仅包含一个条目。这句话是官方文档上我Copy下来的。

### 最后

到这里基本就完了,如有不正确的地方,请指正。

[代码Github地址](https://github.com/BuleB/AndroidStorage)




### 参考

[彻底搞懂Android文件存储---内部存储，外部存储以及各种存储路径解惑](https://blog.csdn.net/u010937230/article/details/73303034)

[了解 Android 应用的文件存储目录，掌握持久化数据的正确姿势](http://yifeng.studio/2017/04/27/android-app-file-storage-directory/)

[存储选项](https://developer.android.com/guide/topics/data/data-storage.html#AccessingExtFiles)

![此处应有签名](https://upload-images.jianshu.io/upload_images/4916021-f4d74472f638692d.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)


