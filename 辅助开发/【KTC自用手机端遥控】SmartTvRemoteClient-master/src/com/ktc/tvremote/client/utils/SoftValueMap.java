package com.ktc.tvremote.client.utils;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.HashMap;

public class SoftValueMap<K, V> extends HashMap<K, V> {
	private HashMap<K, SoftValue<K, V>> temp;
	private ReferenceQueue<V> queue;

	public SoftValueMap() {
		temp = new HashMap<K, SoftValue<K, V>>();
		queue = new ReferenceQueue<V>();
	}

	@Override
	public V put(K key, V value) {
		SoftValue<K, V> sr = new SoftValue<K, V>(value, key, queue);// 将sr与queue绑定
		temp.put(key, sr);
		return null;
	}

	@Override
	public V get(Object key) {
		clearSR();
		SoftValue<K, V> sr = temp.get(key);
		if (sr != null) {
			return sr.get();
		} else {
			return null;
		}
	}

	@Override
	public boolean containsKey(Object key) {
		clearSR();
		SoftValue<K, V> sr = temp.get(key);
		if (sr != null) {
			return sr.get() != null;
		}
		return false;

	}

	private void clearSR() {
		SoftValue<K, V> poll = (SoftValue<K, V>) queue.poll();
		while (poll != null) {
			temp.remove(poll.key);
			poll = (SoftValue<K, V>) queue.poll();
		}
	}

	@Override
	public void clear() {
		temp.clear();
	}
	private class SoftValue<K, V> extends SoftReference<V> {
		private Object key;

		public SoftValue(V r, Object key, ReferenceQueue<? super V> q) {
			super(r, q);
			this.key = key;
		}
	}

}
