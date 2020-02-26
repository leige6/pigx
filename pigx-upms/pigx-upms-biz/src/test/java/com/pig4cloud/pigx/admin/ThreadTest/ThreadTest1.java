package com.pig4cloud.pigx.admin.ThreadTest;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ThreadTest1 {

	int date=0;
	ReentrantLock reentrantLock=new ReentrantLock();
	Condition c1=reentrantLock.newCondition();
	Condition c2=reentrantLock.newCondition();


	public static void main(String[] args) {
		int count=20;
		ThreadTest1 t1=new ThreadTest1();
		new Thread(
				new Runnable() {
					@Override
					public void run() {
						for(int i=0;i<=count;i++){
							t1.oddNumber();
						}
					}
				}
		).start();
		new Thread(
				new Runnable() {
					@Override
					public void run() {
						for(int i=0;i<=count;i++){
							t1.evenNumber();;
						}
					}
				}
		).start();
		System.out.println("任务执行结束！");
	}

	//打印奇数
	public void oddNumber(){
		reentrantLock.lock();
		try {
			if((date%2)==0){
			   c1.await();
			}
			System.out.println("线程："+Thread.currentThread().getName()+" 打印："+date);
			date++;
			c2.signal();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	//打印偶数
	public void evenNumber(){
		reentrantLock.lock();
		try {
			if((date%2)!=0){
				c2.await();
			}
			System.out.println("线程："+Thread.currentThread().getName()+" 打印："+date);
			date++;
			c1.signal();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
