[TOC]

## 并发编程

##### 	线程之间如何通信（这里线程指的是并发执行的活动实体）

##### 	      通信是指线程之间以何种机制来交换信息

- 在命令式编程当中：两种线程通信机制
  - 共享内存
    线程之间通过写-读内存中的公共状态来隐式通信
  - 消息传递
    线程之间没有公共状态，线程之间必须通过明确的发送消息来显示进行通信

- 线程之间如何同步

  ##### 同步是指程序用于控制不同线程之间操作发生相对顺序的机制

  - 在共享内存并发模型里，同步是显示进行的
  - 在消息传递的并发模型里，由于消息的发送必须在消息的接收之前，因此同步是隐式进行的
  - 注：java的并发采用的是共享内存模型，java线程之间的通信总是隐式进行-->容易导致出现各种内存可见性问题
    	

```wiki
3.数据依赖性
如果两个操作访问同一个变量，且这两个操作中有一个为写操作，此时这两个操作之间就存在数据依赖性。
三种类型：
*写后写
*写后读
*读后写
注：这里所说的数据依赖性仅针对单个处理器中执行的指令序列和单个线程中执行的操作
4.重排序
	(1)编译器重排序
	a.编译器在不改变单线程程序语义的前提下，可以重新安排语句的执行顺序
	(2)处理器重排序
	b.指令级并行重排序
	c.内存系统的重排序
	java源代码-->a-->b-->c-->最终执行指令的序列
	注：JMM属于语言级内存模型，他确保在不同编译器和不同处理器平台之上，通过禁止特定类型的编译器重排序和处理器重排序，提供了一致内存可见性的保证
		处理器内存模型时硬件级的内存模型
		顺序一致性内存模型时理论参考模型
	4.1处理器重排序与内存屏障指令
```


​		
```java
	公平锁：
	 static final class FairSync extends Sync {
    private static final long serialVersionUID = -3000897897090466540L;
    /**
     * Fair version of tryAcquire.  Don't grant access unless
     * recursive call or no waiters or is first.
     */
	 //加锁
    @ReservedStackAccess
    protected final boolean tryAcquire(int acquires) {
        final Thread current = Thread.currentThread();
        int c = getState();//获取锁的开始，首先读volatile变量state-->AbstractQueuedSynchronizer -->private volatile int state;
        if (c == 0) {
            if (!hasQueuedPredecessors() &&
                compareAndSetState(0, acquires)) {
                setExclusiveOwnerThread(current);
                return true;
            }
        }
        else if (current == getExclusiveOwnerThread()) {
            int nextc = c + acquires;
            if (nextc < 0)
                throw new Error("Maximum lock count exceeded");
            setState(nextc);
            return true;
        }
        return false;
    }
	使用公平锁加锁lock()方法轨迹：
	1.ReenTrantLock:lock();
	2.FairSync:lock();
	3.AbstractQueuedSynchronizer:acquire(int arg)
	4.ReenTrantLock:tryAcquire(int acquires)
```


​		
```java
	//释放锁
	@ReservedStackAccess
    protected final boolean tryRelease(int releases) {
        int c = getState() - releases;
        if (Thread.currentThread() != getExclusiveOwnerThread())
            throw new IllegalMonitorStateException();
        boolean free = false;
        if (c == 0) {
            free = true;
            setExclusiveOwnerThread(null);
        }
        setState(c);	//释放锁的最后，写volatile变量state
        return free;
    }
	使用公平锁解锁lock()方法轨迹：
	1.ReenTrantLock:unlock();		
	2.AbstractQueuedSynchronizer:release(int arg)
	4.Sync:tryRelease(int releases)
}

	非公平锁：
	只讨论加锁
	1.ReenTrantLock:lock();
	2.NonfairSync:lock();
	3.AbstractQueuedSynchronizer:compareAndSetState(int expect,int update)
	
	protected final boolean compareAndSetState(int expect,int update){
		return unsafe.compareAndSwapInt(this,stateOffset,expect,update);
	}
	compareAndSetState简称CAS
	如果当前状态值等于预期值，则以原子方式将同步状态设置为给定的更新值。此操作具有volatile读与写的内存语义
	    @HotSpotIntrinsicCandidate
		public final native boolean compareAndSetInt(Object o, long offset,int expected,int x);
	
	@ForceInline
	public final boolean compareAndSwapInt(Object o, long offset,int expected,int x) {
		return theInternalUnsafe.compareAndSetInt(o, offset, expected, x);
	}
	
	总结：
	(1)公平锁和非公平锁释放时，最后都要写一个volatile变量state
	(2)公平锁获取时，首先会去读volatile这个变量
	(3)非公平锁获取时，首先会用CAS更新这个volatile变量，这个操作同时具有volatile读和写的内存语义
	锁释放-获取的内存语义的实现至少有下面两种方式：
		1.利用volatile变量写-读所具有的内存语义
		2.使用CAS所附带的volatile读和volatile写的内存语义
		
5.final
	对于final域，编译器和处理器遵循两个规则：
	(1)在构造函数内对一个final域的写入，与随后把这个被构造对象的引用赋值给一个引用变量，这两个操作之间不能重排序
	(2)初次读一个final域对象的引用，与随后初次读这个final域，这两个操作之间不能重排序
	
	重排序规则可以确保：
	(前提条件：在构造函数的内部，不能让这个被构造对象的引用为其他线程可见，也就是对象引用不能在构造函数中“逸出”)
		在引用变量为任意线程可见之前，该引用变量指向的对象的final域已经在构造函数中被正确初始化过了
	5.1写final域的重排序规则：
		a.JMM禁止编译器把final域的写重排序到构造函数之外
		b.编译器会在final域写之后，构造函数return之前，插一个StroeStore屏障。这个屏障禁止把final的写重排序到构造函数之外
		
	5.2读final域的重排序规则：
		a.在一个线程中，初次读对象引用与初次读该对象包含的final域，JMM禁止重排序这两个操作。编译器会在读final域的操作之前插入一个LoadLoad屏障
		fina引用为什么不能从构造函数内"逸出"？
总结：JMM遵循的基本规则
			只要不改变程序结果(指的是单线程程序和正确同步的多线程程序)，编译器和处理器怎么优化都行
			
https://www.processon.com/view/576cde74e4b03265640e7f42#map  --->j.u.c思维导图
http://www.codeceo.com/article/talk-about-concurrency.html   --->一个单例模式引发多线程的思考
```


​			
​			
​			
​			
​			
​			
​			
​			
​			
​			
​			
​			
​			
​			
​			
​			
​			
​			
​			
​			
​			
​			
​			
​			
​			
​			
​			
​			
​			
​			
​			
​			
​			
​			
​			
