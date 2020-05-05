package cn.br.common.function;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.br.common.function.Promises.Promise;

/**
 *  事件流
 * @author ZJL
 *
 */
public class Events {

	private static Logger logger = LoggerFactory.getLogger(Events.class);

	public static class EventStream<T> {

		final int bufferSize;
		final ConcurrentLinkedQueue<T> events = new ConcurrentLinkedQueue<T>();
		final List<Promise<T>> waiting = Collections.synchronizedList(new ArrayList<Promise<T>>());

		public EventStream() {
			this.bufferSize = 100;
		}

		public EventStream(int maxBufferSize) {
			this.bufferSize = maxBufferSize;
		}

		public synchronized Promise<T> nextEvent() {
			if (events.isEmpty()) {
				LazyTask task = new LazyTask();
				waiting.add(task);
				return task;
			}
			return new LazyTask(events.peek());
		}

		public synchronized void publish(T event) {
			if (events.size() > bufferSize) {
				logger.warn("Dropping message.  If this is catastrophic to your app, use a BlockingEvenStream instead");
				events.poll();
			}
			events.offer(event);
			notifyNewEvent();
		}

		void notifyNewEvent() {
			T value = events.peek();
			for (Promise<T> task : waiting) {
				task.invoke(value);
			}
			waiting.clear();
		}

		class LazyTask extends Promise<T> {

			public LazyTask() {
			}

			public LazyTask(T value) {
				invoke(value);
			}

			@Override
			public T get() throws InterruptedException, ExecutionException {
				T value = super.get();
				markAsRead(value);
				return value;
			}

			@Override
			public T getOrNull() {
				T value = super.getOrNull();
				markAsRead(value);
				return value;
			}

			private void markAsRead(T value) {
				if (value != null) {
					events.remove(value);
				}
			}
		}
	}

	public static class IndexedEvent<M> {

		private static final AtomicLong idGenerator = new AtomicLong(1);
		final public M data;
		final public Long id;

		public IndexedEvent(M data) {
			this.data = data;
			this.id = idGenerator.getAndIncrement();
		}

		@Override
		public String toString() {
			return "Event(id: " + id + ", " + data + ")";
		}

		public static void resetIdGenerator() {
			idGenerator.set(1);
		}
	}

	public static class ArchivedEventStream<T> {

		final int archiveSize;
		final ConcurrentLinkedQueue<IndexedEvent<T>> events = new ConcurrentLinkedQueue<IndexedEvent<T>>();
		final List<FilterTask<T>> waiting = Collections.synchronizedList(new ArrayList<FilterTask<T>>());
		final List<EventStream<T>> pipedStreams = new ArrayList<EventStream<T>>();

		public ArchivedEventStream(int archiveSize) {
			this.archiveSize = archiveSize;
		}

		public synchronized EventStream<T> eventStream() {
			final EventStream<T> stream = new EventStream<T>(archiveSize);
			for (IndexedEvent<T> event : events) {
				stream.publish(event.data);
			}
			pipedStreams.add(stream);
			return stream;
		}

		public synchronized Promise<List<IndexedEvent<T>>> nextEvents(long lastEventSeen) {
			FilterTask<T> filter = new FilterTask<T>(lastEventSeen);
			waiting.add(filter);
			notifyNewEvent();
			return filter;
		}

		public synchronized List<IndexedEvent> availableEvents(long lastEventSeen) {
			List<IndexedEvent> result = new ArrayList<IndexedEvent>();
			for (IndexedEvent event : events) {
				if (event.id > lastEventSeen) {
					result.add(event);
				}
			}
			return result;
		}

		public List<T> archive() {
			List<T> result = new ArrayList<T>();
			for (IndexedEvent<T> event : events) {
				result.add(event.data);
			}
			return result;
		}

		public synchronized void publish(T event) {
			if (events.size() >= archiveSize) {
				logger.warn("Dropping message.  If this is catastrophic to your app, use a BlockingEvenStream instead");
				events.poll();
			}
			events.offer(new IndexedEvent(event));
			notifyNewEvent();
			for (EventStream<T> eventStream : pipedStreams) {
				eventStream.publish(event);
			}
		}

		void notifyNewEvent() {
			for (ListIterator<FilterTask<T>> it = waiting.listIterator(); it.hasNext();) {
				FilterTask<T> filter = it.next();
				for (IndexedEvent<T> event : events) {
					filter.propose(event);
				}
				if (filter.trigger()) {
					it.remove();
				}
			}
		}

		static class FilterTask<K> extends Promise<List<IndexedEvent<K>>> {

			final Long lastEventSeen;
			final List<IndexedEvent<K>> newEvents = new ArrayList<IndexedEvent<K>>();

			public FilterTask(Long lastEventSeen) {
				this.lastEventSeen = lastEventSeen;
			}

			public void propose(IndexedEvent<K> event) {
				if (event.id > lastEventSeen) {
					newEvents.add(event);
				}
			}

			public boolean trigger() {
				if (newEvents.isEmpty()) {
					return false;
				}
				invoke(newEvents);
				return true;
			}
		}
	}
}
