package com.noob.test;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SyncTest {
	public static void main2(String[] args) {
		Object obj = new Object();
		Thread thread1 = new Thread(() -> {
			synchronized (obj) {
				try {
					log.info("1-before wait");
					obj.wait();
					log.info("1-after wait");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		});

		Thread thread2 = new Thread(() -> {
			synchronized (obj) {
				try {
					log.info("2-before wait");
					obj.wait();
					log.info("2-after wait");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		});
		Thread thread3 = new Thread(() -> {
			synchronized (obj) {
				try {
					log.info("3-before notifyAll");
					obj.notifyAll();
					try {
						Thread.sleep(2000);
					} catch (Exception e) {
						e.printStackTrace();
					}
					log.info("3-after notifyAll");
					/*
					 * obj.wait(); log.info("3-after wait");
					 */

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			try {
				Thread.sleep(2000);
			} catch (Exception e) {
				e.printStackTrace();
			}
			log.info("3-out synchronized");

		});

		thread1.start();
		thread2.start();

		try {
			Thread.sleep(2000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		thread3.start();

	}

	public static void main(String[] args) {
		Object obj = new Object();
		Thread thread1 = new Thread(() -> {
			synchronized (obj) {
				try {
					Thread.sleep(200000);
					log.info("1-before wait");
					obj.wait();
					log.info("1-after wait");
				} catch (Exception e) {
					log.info("1-catch {}. interrupt状态: {}", e.toString(), Thread.currentThread().isInterrupted());
				}
			}
		});

		Thread thread2 = new Thread(() -> {
			try {
				log.info("2-before wait");
				obj.wait();
				log.info("2-after wait");
			} catch (Exception e) {
				log.info("2-catch {}.", e.toString());
			}
		});

		thread1.start();

		try {
			Thread.sleep(2000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		thread2.start();

		log.info("线程1初始interrupt状态:{}", thread1.isInterrupted());
		thread1.interrupt();

	}
}
