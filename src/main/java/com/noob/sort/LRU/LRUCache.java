package com.noob.sort.LRU;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

/**
 * Least recently used，最近最少使用算法
 * <p>
 * 根据数据的历史访问记录来进行淘汰数据，其核心思想是“如果数据最近被访问过，那么将来被访问的几率也更高”。
 * <p>
 * 1. 新数据插入到链表头部；
 * <p>
 * 2. 每当缓存命中（即缓存数据被访问），则将数据移到链表头部；
 * <p>
 * 3. 当链表满的时候，将链表尾部的数据丢弃。
 */
public class LRUCache<K, V> extends LinkedHashMap<K, V> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 873242808323499391L;
	private final int maxCapacity;
	private final Lock lock = new ReentrantLock();

	public LRUCache(int maxCapacity) {
		/**
		 * LinkedHashMap重写了hashMap的get操作： accessOrder为true,
		 * 在get()操作后执行afterNodeAccess()将该Node移动到最后
		 */
		super(maxCapacity, 0.75f, true);
		this.maxCapacity = maxCapacity;

	}

	/**
	 * hashMap在put操作预留了后置处理方法afterNodeInsertion。LinkedHashMap重写增加判定是否需要删除最前面（最近最少访问的）节点
	 */
	@Override
	protected boolean removeEldestEntry(java.util.Map.Entry<K, V> eldest) {
		return size() > maxCapacity;
	}

	private <T> T lock(Supplier<T> op) {
		try {
			lock.lock();
			return op.get();
		} finally {
			lock.unlock();
		}
	}

	@Override
	public boolean containsKey(Object key) {
		return lock(() -> super.containsKey(key));
	}

	@Override
	public V get(Object key) {

		return lock(() -> super.get(key));

	}

	@Override
	public V put(K key, V value) {
		return lock(() -> super.put(key, value));

	}

	public int size() {
		return lock(() -> super.size());

	}

	public void clear() {
		try {
			lock.lock();
			super.clear();
		} finally {
			lock.unlock();
		}
	}

	public Set<Map.Entry<K, V>> getAll() {
		return lock(() -> super.entrySet());

	}
}
