# keykeeper
用以保存密码的APP，目前还在持续开发中



适用于Android8.1以上版本的手机。

整体使用kotlin进行开发。

所用到的框架大致有coroutines（协程）、Room（数据库）、dagger2（依赖注入）、retrofit（虽说现在的部分还没用到，以后可能会用到吧）、TinyPinyin（获取首字母的时候有用到）

下一个更新就是数据加密部分了。在做了在做了（咕咕咕）



---

以下是架构图（又现UML图...）

![https://github.com/Ricinix/keykeeper/blob/master/docs/uml%E7%B1%BB%E5%9B%BE.jpg](https://github.com/Ricinix/keykeeper/blob/master/docs/uml类图.jpg)

以下是数据库部分设计图：

![https://github.com/Ricinix/keykeeper/blob/master/docs/%E6%95%B0%E6%8D%AE%E5%BA%93.jpg](https://github.com/Ricinix/keykeeper/blob/master/docs/数据库.jpg)

---

以下是部分截图：

欢迎界面：

![https://github.com/Ricinix/keykeeper/blob/master/docs/%E6%AC%A2%E8%BF%8E%E7%95%8C%E9%9D%A2.png](https://github.com/Ricinix/keykeeper/blob/master/docs/欢迎界面.png)

主界面：

![https://github.com/Ricinix/keykeeper/blob/master/docs/%E4%B8%BB%E7%95%8C%E9%9D%A2.png](https://github.com/Ricinix/keykeeper/blob/master/docs/主界面.png)

