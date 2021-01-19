package jp.co.sony.csl.dcoes.apis.common.util.vertx;

import io.vertx.core.AsyncResult;
import io.vertx.core.Context;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.VertxException;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import java.util.PriorityQueue;
import java.util.Queue;

import jp.co.sony.csl.dcoes.apis.common.util.StackTraceUtil;

/**
 * Implements exclusive control function between different processes.
 * This function uses the file system.
 * プロセス内の排他制御機能.
 * @author OES Project
 */
public class LocalExclusiveLock {
	private static final Logger log = LoggerFactory.getLogger(LocalExclusiveLock.class);

	/**
	 * Sets time that lock is maintained for more than a certain period of time as log output function's wait time.	 
	 * The value is {@value}.
	 * ロックが一定時間以上保持された時にログを出力する機能の待ち時間.
	 * 値は {@value}.
	 */
	private static final Long DEFAULT_LOCK_LIMIT_MSEC = 5000L;

	private String name_;
	private boolean locked_ = false;
	private Queue<Entry_> queue_ = new PriorityQueue<>();

	/**
	 * Specifies name and creates instance.
	 * The name does not have significance for the lock function. It is used only in the log output.
	 * @param name log name
	 * 名前を指定してインスタンスを作成する.
	 * 名前は機能上の意味は持たない. ログ出力に用いるだけ.
	 * @param name ロックの名前
	 */
	public LocalExclusiveLock(String name) {
		name_ = name;
	}

	/**
	 * This interface represents the lock of this function.
	 * @author OES Project
	 * 本機能のロックを表現するインタフェイス
	 * @author OES Project
	 */
	public static interface Lock {
		void release();
	}

	/**
	 * Acquires lock without priority.
	 * Gets {@link Lock} via {@code completionHandler}.
	 * Releases using {@link Lock#release()} to make sure acquired lock is released when process is over.
	 * If lock cannot be acquired immediately, it is added to the queue.
	 * @param vertx vertx instance
	 * @param completionHandler the completion handler
	 * 優先権なしでロックを獲得する.
	 * {@code completionHandler} 経由で {@link Lock} を取得する.
	 * 獲得したロックは処理が終わったら必ず {@link Lock#release()} で開放する.
	 * 直ちに獲得できない場合は待ち行列に追加される.
	 * @param vertx vertx インスタンス
	 * @param completionHandler the completion handler
	 */
	public void acquire(Vertx vertx, Handler<AsyncResult<Lock>> completionHandler) {
		acquire(vertx, false, completionHandler);
	}
	/**
	 * Specifies priority and then acquires lock.
	 * If lock cannot be acquired immediately, it is added to the queue.
	 * If priority is false, acquires lock in order of calls.
	 * If priority is true, ignores order in queue and acquires prioritized lock.
	 * If there multiple priority flags that are true, acquires lock in order of calls.
	 * @param vertx vertx instance
	 * @param privileged priority flag
	 * @param completionHandler the completion handler
	 * 優先権を指定してロックを獲得する.
	 * 直ちに獲得できない場合は待ち行列に追加される.
	 * 優先権が false なら呼び出した順番でロックを獲得する.
	 * 優先権が true なら待ち行列の順序を無視して優先的にロックを獲得する.
	 * 優先権 true が複数ある場合は呼び出した順番でロックを獲得する.
	 * @param vertx vertx インスタンス
	 * @param privileged 優先権フラグ
	 * @param completionHandler the completion handler
	 */
	public void acquire(Vertx vertx, boolean privileged, Handler<AsyncResult<Lock>> completionHandler) {
		Entry_ entry = new Entry_(privileged, vertx.getOrCreateContext(), completionHandler);
		vertx.<Entry_>executeBlocking(future -> {
			synchronized (queue_) { // Synchronizes by separating and processing threads
									// スレッドを分けて処理し同期する
				queue_.add(entry);
				if (!locked_) {
					// If not in locked state, puts in locked state and retrieves and returns the first entry in queue
					// ロック状態でなければロック状態にしキューの最初のエントリを取り出して返す
					locked_ = true;
					future.complete(queue_.poll());
				} else {
					// Does not return anything if already in locked state
					// すでにロック状態なら何も返さない
					if (log.isInfoEnabled()) log.info("local exclusive lock for " + name_ + " ; queue size : " + queue_.size());
					future.complete();
				}
			}
		}, res -> {
			Entry_ next = res.result();
			if (next != null) {
				// When the next entry is returned, creates new lock object and returns it -> Lock acquisition is successful
				// 次のエントリが返ってきたら新しいロックオブジェクトを作って返す → ロック獲得成功
				next.context_.runOnContext(v -> {
					next.completionHandler_.handle(Future.succeededFuture(new Lock_(vertx, next.stackTrace_)));
				});
			}
		});
	}
	/**
	 * Resets the lock function.
	 * Makes all lock waiting processes to fail and deletes the queue.
	 * @param vertx vertx instance
	 * ロック機能をリセットする.
	 * ロック待ち処理を全て失敗させ待ち行列を削除する.
	 * @param vertx vertx インスタンス
	 */
	public void reset(Vertx vertx) {
		if (log.isInfoEnabled()) log.info("reset local exclusive lock for " + name_);
		vertx.<Queue<Entry_>>executeBlocking(future -> {
			synchronized (queue_) { // Synchronizes by separating and processing threads
									// スレッドを分けて処理し同期する
				// Copies the queue, empties original queue, releases lock state, and returns copied queue
				// キューをコピーし, 元のキューを空にし, ロック状態を解除し, コピーしたキューを返す	
				Queue<Entry_> queue = new PriorityQueue<>(queue_);
				queue_.clear();
				locked_ = false;
				future.complete(queue);
			}
		}, res -> {
			Queue<Entry_> queue = res.result();
			while (!queue.isEmpty()) {
				// Fails when retrieving entries one by one from queue → Lock acquisition failed
				// キューからエントリを一つずつ取り出し fail → ロック獲得失敗
				Entry_ entry = queue.poll();
				if (entry != null) {
					entry.context_.runOnContext(v -> {
						entry.completionHandler_.handle(Future.failedFuture("local exclusive lock for " + name_ + " ; reset"));
					});
				}
			}
		});
	}

	/**
	 * This class represents queue entries.
	 * @author OES Project
	 * 待ち行列のエントリを表すクラス.
	 * @author OES Project
	 */
	private class Entry_ implements Comparable<Entry_> {
		private final boolean privileged_;
		private final Context context_;
		private final Handler<AsyncResult<Lock>> completionHandler_;
		private final StackTraceElement[] stackTrace_;
		/**
		 * Creates instance.
		 * @param privileged priority
		 * @param context context object to be processed during lock acquisition
		 * @param completionHandler the completion handler
		 * インスタンス作成.
		 * @param privileged 優先権
		 * @param context ロック獲得時に処理が実行される context オブジェクト
		 * @param completionHandler the completion handler
		 */
		private Entry_(boolean privileged, Context context, Handler<AsyncResult<Lock>> completionHandler) {
			privileged_ = privileged;
			context_ = context;
			completionHandler_ = completionHandler;
			stackTrace_ = StackTraceUtil.stackTrace(new Class[] {LocalExclusiveLock.class, LocalExclusiveLock.Entry_.class});
		}
		/**
		 * This is trick for prioritized items to get ahead in the queue.
		 * 優先権を持つものが待ち行列中で先に並ぶための小細工.
		 */
		@Override public int compareTo(Entry_ other) {
			int thisVal = (this.privileged_) ? 1 : 0;
			int otherVal = (other.privileged_) ? 1 : 0;
			if (thisVal == otherVal) {
				thisVal = this.hashCode();
				otherVal = other.hashCode();
			}
			if(otherVal < thisVal) {
				return -1;
			} else if (thisVal < otherVal) {
				return 1;
			} else {
				return 0;
			}
		}
	}

	/**
	 * This is the {@link Lock} implementation.
	 * @author OES Project
	 * {@link Lock} の実装.
	 * @author OES Project
	 */
	private class Lock_ implements Lock {
		private Vertx vertx_;
		private StackTraceElement[] stackTrace_;
		private boolean released_ = false;
		private long acquiredTime_ = 0L;
		private long timerId_ = 0L;
		/**
		 * Creates instance.
		 * @param vertx vertx instance
		 * @param stackTrace stack trace.
		 * Acquired at the point in time it is added to the queue.
		 * Has no meaning in the function. Used only for log output.
		 * インスタンス作成.
		 * @param vertx vertx インスタンス
		 * @param stackTrace スタックトレース.
		 * 待ち行列に追加された時点で取得される.
		 * 機能上の意味は持たない. ログ出力に用いるだけ.
		 */
		private Lock_(Vertx vertx, StackTraceElement[] stackTrace) {
			vertx_ = vertx;
			stackTrace_ = stackTrace;
			acquiredTime_ = System.currentTimeMillis();
			setLockCheckTimer_();
		}
		/**
		 * Sets timer for outputting to log when lock has been maintained for more than a certain amount of time.
		 * The default value of the wait time is {@link #DEFAULT_LOCK_LIMIT_MSEC}.
		 * ロックが一定時間以上保持された時にログを出力するためのタイマを設定する.
		 * 待ち時間はデフォルト値 {@link #DEFAULT_LOCK_LIMIT_MSEC}.
		 */
		private void setLockCheckTimer_() {
			Long delay = DEFAULT_LOCK_LIMIT_MSEC;
			setLockCheckTimer_(delay);
		}
		/**
		 * Sets timer for outputting to log when lock has been maintained for more than a certain amount of time.
		 * @param delay wait time [ms]
		 * ロックが一定時間以上保持された時にログを出力するためのタイマを設定する.
		 * @param delay 待ち時間 [ms]
		 */
		private void setLockCheckTimer_(long delay) {
			timerId_ = vertx_.setTimer(delay, this::lockCheckTimerHandler_);
		}
		/**
		 * Executes process to output log when lock has been maintained for more than a certain amount of time.
		 * @param timerId : timer ID
		 * ロックが一定時間以上保持された時にログを出力するための処理
		 * @param timerId : タイマ ID
		 */
		private void lockCheckTimerHandler_(Long timerId) {
			if (released_) return;
			if (null == timerId || timerId.longValue() != timerId_) {
				if (log.isWarnEnabled()) log.warn("illegal timerId : " + timerId + ", timerId_ : " + timerId_);
				return;
			}
			long lockingTime = System.currentTimeMillis() - acquiredTime_;
			String message = "Lock " + this + " has been locked for " + lockingTime + " ms ; limit : " + DEFAULT_LOCK_LIMIT_MSEC;
			VertxException stackTrace = new VertxException("Lock limit exceeded");
			stackTrace.setStackTrace(stackTrace_);
			if (log.isWarnEnabled()) log.warn(message, stackTrace);
			setLockCheckTimer_();
		}
		/**
		 * Releases acquired lock.
		 * Readies next one if there is a queue.
		 * 獲得したロックを開放する.
		 * 待ち行列がある場合は次を点火する.
		 */
		@Override public void release() {
			vertx_.cancelTimer(timerId_);
			vertx_.<Entry_>executeBlocking(future -> {
				synchronized (queue_) { // Synchronizes by separating threads and processing
										// スレッドを分けて処理し同期する
					if (released_) {
						// Release completed ( Measure if called multiple times due to bug on user's side) → Warns and ignores
						// リリース済み ( 利用側のバグで複数回呼ばれた場合の対策 ) → 警告してスルー
						if (log.isWarnEnabled()) log.warn("local exclusive lock for " + name_ + " ; already released");
						future.complete();
					} else {
						released_ = true;
						if (!queue_.isEmpty()) {
							// If there is an entry in the queue, puts it in lock state, retrieves the first entry in queues, and returns it.
							// キューにエントリがあったらロック状態にしキューの最初のエントリを取り出して返す
							locked_ = true;
							future.complete(queue_.poll());
						} else {
							// If queue is empty, removes lock state and does not return anything
							// キューが空ならロック状態を解除し何も返さない
							locked_ = false;
							future.complete();
						}
					}
				}
			}, res -> {
				Entry_ next = res.result();
				if (next != null) {
					// When the next entry is returned, creates new lock object and returns it → Lock acquisition is successful
					// 次のエントリが返ってきたら新しいロックオブジェクトを作って返す → ロック獲得成功
					if (log.isInfoEnabled()) log.info("local exclusive lock for " + name_ + " ; queue size : " + queue_.size());
					next.context_.runOnContext(v -> {
						next.completionHandler_.handle(Future.succeededFuture(new Lock_(vertx_, next.stackTrace_)));
					});
				}
			});
		}
	}

}
