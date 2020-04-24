# Printer


基于某蓝牙打印机sdk进行二次封装的，简便其调用流程：

1. 调用蓝牙连接目标打印机 T3 or T5
2. 根据目标蓝牙连接名区分是T3 还是T5类型的打印机
3. 实例化对应的Printer对象
4. 通过Document对象构建打印文档
5. PrintManager 对象准备就绪后可拿到PrintService实例，PrintService是此蓝牙打印机的打印模块，内部通过单线程池进行打印工作

