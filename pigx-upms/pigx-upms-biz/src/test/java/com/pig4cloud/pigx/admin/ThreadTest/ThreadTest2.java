package com.pig4cloud.pigx.admin.ThreadTest;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ThreadTest2 {

	int date=0;


	public static void main(String[] args) {
		int count=20;
		ThreadTest2 t1=new ThreadTest2();
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
	public synchronized void oddNumber(){

		try {
			if((date%2)==0){
				this.wait();
			}
			System.out.println("线程："+Thread.currentThread().getName()+" 打印："+date);
			date++;
			this.notify();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	//打印偶数
	public synchronized void evenNumber(){
		try {
			if((date%2)!=0){
				this.wait();
			}
			System.out.println("线程："+Thread.currentThread().getName()+" 打印："+date);
			date++;
			this.notify();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
