package jp.co.sony.csl.dcoes.apis.common.util.vertx;

import io.vertx.core.AsyncResult;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import jp.co.sony.csl.dcoes.apis.common.util.StringUtil;

/**
 * Implements exclusive control function between different processes.
 * Uses the file system.
 * @author OES Project
 * 異なるプロセス間の排他制御機能.
 * ファイルシステムを使う.
 * @author OES Project
 */
public class FileSystemExclusiveLockUtil {
	private static final Logger log = LoggerFactory.getLogger(FileSystemExclusiveLockUtil.class);

	private static final JsonObjectUtil.DefaultString DEFAULT_LOCK_FILE_FORMAT = new JsonObjectUtil.DefaultString(StringUtil.TMPDIR + "/.apis.%s.lock");

	/**
	 * Creates exclusive control object within process related to this class.
	 * Implements an exclusive lock because {@link FileLock} causes thread conflicts.
	 * 本クラスに関するプロセス内の排他制御オブジェクト.
	 * {@link FileLock} はスレッド競合するので排他ロックを咬ませる.
	 */
	private static LocalExclusiveLock exclusiveLock_ = new LocalExclusiveLock(FileSystemExclusiveLockUtil.class.getName());
	/**
	 * Gets exclusive control lock within process related to this class.
	 * Receives the lock object using completionHandler's {@link AsyncResult#result()}.
	 * @see LocalExclusiveLock#acquire(Vertx, Handler)
	 * @param vertx vertx instance
	 * @param completionHandler the completion handler
	 * 本クラスに関するプロセス内の排他制御のロックを獲得する.
	 * completionHandler の {@link AsyncResult#result()} でロックオブジェクトを受け取る.
	 * @see LocalExclusiveLock#acquire(Vertx, Handler)
	 * @param vertx vertx インスタンス
	 * @param completionHandler the completion handler
	 */
	public static void acquireExclusiveLock(Vertx vertx, Handler<AsyncResult<LocalExclusiveLock.Lock>> completionHandler) {
		exclusiveLock_.acquire(vertx, completionHandler);
	}
	/**
	 * Resets exclusive control lock within process related to this class.
	 * @see LocalExclusiveLock#reset(Vertx)
	 * @param vertx vertx instance
	 * 本クラスに関するプロセス内の排他制御のロックをリセットする.
	 * @see LocalExclusiveLock#reset(Vertx)
	 * @param vertx vertx インスタンス
	 */
	public static void resetExclusiveLock(Vertx vertx) {
		exclusiveLock_.reset(vertx);
	}

	private static Map<String, FileChannel> channels_ = new ConcurrentHashMap<>();
	private static Map<String, FileLock> locks_ = new ConcurrentHashMap<>();

	private FileSystemExclusiveLockUtil() { }

	/**
	 * Gets lock.
	 * Receives true/false using completionHandler's {@link AsyncResult#result()}.
	 * - Unlocked and lock acquisition is successful: {@code true}
	 * - Unlocked and lock acquisition fails: {@code false}
	 * - Locked and {@code allowAlreadyLocked} is {@code true} : {@code true}
	 * - Locked and {@code allowAlreadyLocked} is {@code false} : {@code false}
	 * @param vertx vertx instance
	 * @param name lock name
	 * @param allowAlreadyLocked flag indicating whether or not to assume success if lock with lock with specified name exists. Successful if {@code true}	 
	 * @param completionHandler the completion handler
	 * ロックを獲得する.
	 * completionHandler の {@link AsyncResult#result()} で可否を受け取る.
	 * - 未ロックでロック獲得成功 : {@code true}
	 * - 未ロックでロック獲得失敗 : {@code false}
	 * - ロック済で {@code allowAlreadyLocked} が {@code true} : {@code true}
	 * - ロック済で {@code allowAlreadyLocked} が {@code false} : {@code false}
	 * @param vertx vertx インスタンス
	 * @param name ロックの名前
	 * @param allowAlreadyLocked 指定された名前のロックが存在する場合に成功とするか否かを指定するフラグ. {@code true} なら成功
	 * @param completionHandler the completion handler
	 */
	public static void lock(Vertx vertx, String name, boolean allowAlreadyLocked, Handler<AsyncResult<Boolean>> completionHandler) {
		if (vertx != null && name != null) {
			acquireExclusiveLock(vertx, resExclusiveLock -> {
				if (resExclusiveLock.succeeded()) {
					LocalExclusiveLock.Lock lock = resExclusiveLock.result();
					acquireLock_(vertx, name, allowAlreadyLocked, resAcquireLock -> {
						lock.release();
						completionHandler.handle(resAcquireLock);
					});
				} else {
					log.error(resExclusiveLock);
					completionHandler.handle(Future.failedFuture(resExclusiveLock.cause()));
				}
			});
		} else {
			completionHandler.handle(Future.failedFuture("vertx and name should not be null"));
		}
	}
	/**
	 * Releases lock.
	 * Receives result using completionHandler's {@link AsyncResult#result()}.
	 * - Locked and lock release is successful : {@code true}
	 * - Unlocked and {@code allowNotLocked} is {@code true} : {@code true}
	 * - Unlocked and {@code allowNotLocked} is {@code false} : {@code false}
	 * @param vertx vertx instance
	 * @param name lock name
	 * @param allowNotLocked flag indicating whether or not to assume success if lock with specified name does not exists. Successful if {@code true}	
	 * @param completionHandler the completion handler
	 * ロックを開放する.
	 * completionHandler の {@link AsyncResult#result()} で結果を受け取る.
	 * - ロック済でロック開放成功 : {@code true}
	 * - 未ロックで {@code allowNotLocked} が {@code true} : {@code true}
	 * - 未ロックで {@code allowNotLocked} が {@code false} : {@code false}
	 * @param vertx vertx インスタンス
	 * @param name ロックの名前
	 * @param allowNotLocked 指定された名前のロックが存在しない場合に成功とするか否かを指定するフラグ. {@code true} なら成功
	 * @param completionHandler the completion handler
	 */
	public static void unlock(Vertx vertx, String name, boolean allowNotLocked, Handler<AsyncResult<Boolean>> completionHandler) {
		if (vertx != null && name != null) {
			acquireExclusiveLock(vertx, resExclusiveLock -> {
				if (resExclusiveLock.succeeded()) {
					LocalExclusiveLock.Lock lock = resExclusiveLock.result();
					releaseLock_(vertx, name, allowNotLocked, resReleaseLock -> {
						lock.release();
						completionHandler.handle(resReleaseLock);
					});
				} else {
					log.error(resExclusiveLock);
					completionHandler.handle(Future.failedFuture(resExclusiveLock.cause()));
				}
			});
		} else {
			completionHandler.handle(Future.failedFuture("vertx and name should not be null"));
		}
	}
	/**
	 * Confirms lock.
	 * Receives confirmation (exists/does not exist) using completionHandler's {@link AsyncResult#result()}.
	 * @param vertx vertx instance
	 * @param name lock name
	 * @param completionHandler the completion handler
	 * ロックを確認する.
	 * completionHandler の {@link AsyncResult#result()} で有無を受け取る.
	 * @param vertx vertx インスタンス
	 * @param name ロックの名前
	 * @param completionHandler the completion handler
	 */
	public static void check(Vertx vertx, String name, Handler<AsyncResult<Boolean>> completionHandler) {
		if (vertx != null && name != null) {
			acquireExclusiveLock(vertx, resExclusiveLock -> {
				if (resExclusiveLock.succeeded()) {
					LocalExclusiveLock.Lock lock = resExclusiveLock.result();
					checkLock_(vertx, name, resCheckLock -> {
						lock.release();
						completionHandler.handle(resCheckLock);
					});
				} else {
					log.error(resExclusiveLock);
					completionHandler.handle(Future.failedFuture(resExclusiveLock.cause()));
				}
			});
		} else {
			completionHandler.handle(Future.failedFuture("vertx and name should not be null"));
		}
	}
	/**
	 * Resets the lock function.
	 * Fails all lock waiting processes and deletes the queue.
	 * @param vertx vertx instance
	 * @param completionHandler the completion handler
	 * ロック機能をリセットする.
	 * ロック待ち処理を全て失敗させ待ち行列を削除する.
	 * @param vertx vertx インスタンス
	 * @param completionHandler the completion handler
	 */
	public static void reset(Vertx vertx, Handler<AsyncResult<Void>> completionHandler) {
		acquireExclusiveLock(vertx, resExclusiveLock -> {
			if (resExclusiveLock.succeeded()) {
				LocalExclusiveLock.Lock lock = resExclusiveLock.result();
				resetLocks_(vertx, resReetLock -> {
					lock.release();
					completionHandler.handle(resReetLock);
				});
			} else {
				log.error(resExclusiveLock);
				completionHandler.handle(Future.failedFuture(resExclusiveLock.cause()));
			}
		});
	}

	////

	/**
	 * This is the actual lock acquisition process.
	 * Receives true/false using completionHandler's {@link AsyncResult#result()}.
	 * - Unlocked and lock acquisition is successful : {@code true}
	 * - Unlocked and lock acquisition fails : {@code false}
	 * - Locked and {@code allowAlreadyLocked} is {@code true} : {@code true}
	 * - Locked and {@code allowAlreadyLocked} is {@code false} : {@code false}
	 * @param vertx vertx instance
	 * @param name lock name
	 * @param allowAlreadyLocked flag indicating whether or not to assume success if lock with specified name already exists. Successful if {@code true}
	 * @param completionHandler the completion handler
	 * ロック獲得の実処理.
	 * completionHandler の {@link AsyncResult#result()} で可否を受け取る.
 	 * - 未ロックでロック獲得成功 : {@code true}
	 * - 未ロックでロック獲得失敗 : {@code false}
	 * - ロック済で {@code allowAlreadyLocked} が {@code true} : {@code true}
	 * - ロック済で {@code allowAlreadyLocked} が {@code false} : {@code false}
	 * @param vertx vertx インスタンス
	 * @param name ロックの名前
	 * @param allowAlreadyLocked 指定された名前のロックが存在する場合に成功とするか否かを指定するフラグ. {@code true} なら成功
	 * @param completionHandler the completion handler
	 */
	private static void acquireLock_(Vertx vertx, String name, boolean allowAlreadyLocked, Handler<AsyncResult<Boolean>> completionHandler) {
		if (locks_.get(name) != null) {
			String msg = "already locked for name : " + name;
			if (allowAlreadyLocked) {
				if (log.isDebugEnabled()) log.debug(msg);
				completionHandler.handle(Future.succeededFuture(Boolean.TRUE));
			} else {
				if (log.isWarnEnabled()) log.warn(msg);
				completionHandler.handle(Future.succeededFuture(Boolean.FALSE));
			}
		} else {
			getChannel_(vertx, name, resGetChannel -> {
				if (resGetChannel.succeeded()) {
					FileChannel channel = resGetChannel.result();
					FileLock lock;
					try {
						lock = channel.tryLock();
					} catch (Exception e) {
						log.error(e);
						completionHandler.handle(Future.failedFuture(e));
						return;
					}
					if (lock != null) {
						if (log.isDebugEnabled()) log.debug("lock acquired for name : " + name);
						locks_.put(name, lock);
						completionHandler.handle(Future.succeededFuture(Boolean.TRUE));
					} else {
						if (log.isDebugEnabled()) log.debug("lock failed for name : " + name);
						completionHandler.handle(Future.succeededFuture(Boolean.FALSE));
					}
				} else {
					completionHandler.handle(Future.failedFuture(resGetChannel.cause()));
				}
			});
		}
	}
	/**
	 * Gets {@link FileChannel} which is used for locking.
	 * Receives {@link FileChannel} object using completionHandler's {@link AsyncResult#result()} .
	 * @param vertx vertx instance
	 * @param name lock name
	 * @param completionHandler the completion handler
	 * ロックに使用する {@link FileChannel} を取得する.
	 * completionHandler の {@link AsyncResult#result()} で {@link FileChannel} オブジェクトを受け取る.
	 * @param vertx vertx インスタンス
	 * @param name ロックの名前
	 * @param completionHandler the completion handler
	 */
	private static void getChannel_(Vertx vertx, String name, Handler<AsyncResult<FileChannel>> completionHandler) {
		FileChannel channel = channels_.get(name);
		if (channel != null && channel.isOpen()) {
			completionHandler.handle(Future.succeededFuture(channel));
		} else {
			ensureFile_(vertx, name, resEnsureFile -> {
				if (resEnsureFile.succeeded()) {
					Path path = resEnsureFile.result();
					FileChannel newChannel;
					try {
						newChannel = FileChannel.open(path, StandardOpenOption.WRITE, StandardOpenOption.SPARSE);
					} catch (Exception e) {
						log.error(e);
						completionHandler.handle(Future.failedFuture(e));
						return;
					}
					channels_.put(name, newChannel);
					completionHandler.handle(Future.succeededFuture(newChannel));
				} else {
					completionHandler.handle(Future.failedFuture(resEnsureFile.cause()));
				}
			});
		}
	}
	private static final String PATH_FORMAT_;
	static {
		String s = VertxConfig.config.getString(DEFAULT_LOCK_FILE_FORMAT, "fileSystemExclusiveLockFileFormat");
		PATH_FORMAT_ = StringUtil.fixFilePath(s);
	}
	/**
	 * If the file corresponding to specified {@code name} does not exist, creates the file and gets its {@link Path}. 
	 * Receives {@link Path} object using completionHandler's {@link AsyncResult#result()}.
	 * @param vertx vertx instance
	 * @param name lock name
	 * @param completionHandler the completion handler
	 * 指定した {@code name} に対応するファイルが無ければ作りその {@link Path} を取得する.
	 * completionHandler の {@link AsyncResult#result()} で {@link Path} オブジェクトを受け取る.
	 * @param vertx vertx インスタンス
	 * @param name ロックの名前
	 * @param completionHandler the completion handler
	 */
	private static void ensureFile_(Vertx vertx, String name, Handler<AsyncResult<Path>> completionHandler) {
		String path = String.format(PATH_FORMAT_, name);
		vertx.fileSystem().exists(path, resExists -> {
			if (resExists.succeeded()) {
				if (resExists.result()) {
					// Already exists → Successful
					// もうある → 成功
					completionHandler.handle(Future.succeededFuture(Paths.get(path)));
				} else {
					// Does not exist
					// ない
					if (log.isInfoEnabled()) log.info("creating lock file : " + path);
					vertx.fileSystem().createFile(path, resCreate -> {
						if (resCreate.succeeded()) {
							// → Created
							// → 作れた
							if (log.isInfoEnabled()) log.info("chmoding lock file : " + path);
							vertx.fileSystem().chmod(path, "rw-rw-rw-", resChmod -> {
								// → Changes permissions because other processes open the file.
								// → 他のプロセスも開くのでパーミッションを変えておく
								if (resChmod.succeeded()) {
									// → Successful
									// → 成功
									completionHandler.handle(Future.succeededFuture(Paths.get(path)));
								} else {
									log.error(resChmod.cause());
									completionHandler.handle(Future.failedFuture(resChmod.cause()));
								}
							});
						} else {
							// → Could not create → Reconfirms because the file may have been created in between confirming and creating
							// → 作れなかった → 確認してから作るまでにできた可能性があるので再確認
							vertx.fileSystem().exists(path, resExistsAgain -> {
								if (resExistsAgain.succeeded()) {
									if (resExistsAgain.result()) {
										// → File exists → Success
										// → あった → 成功
										completionHandler.handle(Future.succeededFuture(Paths.get(path)));
									} else {
										// → File does not exist after all → Error
										// → やっぱりなかった → エラー
										log.error(resCreate.cause());
										completionHandler.handle(Future.failedFuture(resCreate.cause()));
									}
								} else {
									log.error(resExistsAgain.cause());
									completionHandler.handle(Future.failedFuture(resExistsAgain.cause()));
								}
							});
						}
					});
				}
			} else {
				log.error(resExists.cause());
				completionHandler.handle(Future.failedFuture(resExists.cause()));
			}
		});
	}

	/**
	 * This is the actual lock release process.
	 * Receives result using completionHandler's {@link AsyncResult#result()}.
	 * - Locked and lock release is successful : {@code true}
	 * - Unlocked and  {@code allowNotLocked} is {@code true} : {@code true}
	 * - Unlocked and  {@code allowNotLocked} is {@code false} : {@code false}
	 * @param vertx vertx instance
	 * @param name lock name
	 * @param allowNotLocked flag indicating whether or not to assume success if lock with specified name does not exist. Successful if {@code true}
	 * @param completionHandler the completion handler
	 * ロック開放の実処理.
	 * completionHandler の {@link AsyncResult#result()} で結果を受け取る.
	 * - Locked and lock release is successful : {@code true}
	 * - 未ロックで {@code allowNotLocked} が {@code true} : {@code true}
	 * - 未ロックで {@code allowNotLocked} が {@code false} : {@code false}
	 * @param vertx vertx インスタンス
	 * @param name ロックの名前
	 * @param allowNotLocked 指定された名前のロックが存在しない場合に成功とするか否かを指定するフラグ. {@code true} なら成功
	 * @param completionHandler the completion handler
	 */
	private static void releaseLock_(Vertx vertx, String name, boolean allowNotLocked, Handler<AsyncResult<Boolean>> completionHandler) {
		FileLock lock = locks_.remove(name);
		if (lock != null) {
			try {
				lock.release();
			} catch (Exception e) {
				locks_.put(name, lock);
				log.error(e);
				completionHandler.handle(Future.failedFuture(e));
				return;
			}
			if (log.isDebugEnabled()) log.debug("lock released for name : " + name);
			FileChannel channel = channels_.remove(name);
			if (channel != null) {
				try {
					channel.close();
				} catch (Exception e) {
					channels_.put(name, channel);
					log.error(e);
					completionHandler.handle(Future.failedFuture(e));
					return;
				}
				completionHandler.handle(Future.succeededFuture(Boolean.TRUE));
			} else {
				String msg = "no channel found for name : " + name;
				log.error(msg);
				completionHandler.handle(Future.failedFuture(msg));
			}
		} else {
			String msg = "no lock found for name : " + name;
			if (allowNotLocked) {
				if (log.isDebugEnabled()) log.debug(msg);
				completionHandler.handle(Future.succeededFuture(Boolean.TRUE));
			} else {
				if (log.isWarnEnabled()) log.warn(msg);
				completionHandler.handle(Future.succeededFuture(Boolean.FALSE));
			}
		}
	}

	/**
	 * This the actual lock confirmation process.
	 * Receives confirmation (exist/does not exist) using completionHandler's {@link AsyncResult#result()}.
	 * @param vertx vertx instance
	 * @param name lock name
	 * @param completionHandler the completion handler
	 * ロック確認の実処理.
	 * completionHandler の {@link AsyncResult#result()} で有無を受け取る.
	 * @param vertx vertx インスタンス
	 * @param name ロックの名前
	 * @param completionHandler the completion handler
	 */
	private static void checkLock_(Vertx vertx, String name, Handler<AsyncResult<Boolean>> completionHandler) {
		completionHandler.handle(Future.succeededFuture(Boolean.valueOf(locks_.get(name) != null)));
	}

	/**
	 * This is the actual process to reset lock function.
	 * Fails all lock waiting processes and deletes the queue.
	 * @param vertx vertx instance
	 * @param completionHandler the completion handler
	 * ロック機能リセットの実処理.
	 * ロック待ち処理を全て失敗させ待ち行列を削除する.
	 * @param vertx vertx インスタンス
	 * @param completionHandler the completion handler
	 */
	private static void resetLocks_(Vertx vertx, Handler<AsyncResult<Void>> completionHandler) {
		if (log.isDebugEnabled()) log.debug("resetting locks...");
		@SuppressWarnings("rawtypes") List<Future> releaseFutures = new ArrayList<>();
		for (String aName : locks_.keySet()) {
			Future<Boolean> aFuture = Future.future();
			releaseLock_(vertx, aName, true, aFuture);
			releaseFutures.add(aFuture);
		}
		CompositeFuture.all(releaseFutures).setHandler(ar -> {
			if (ar.succeeded()) {
				completionHandler.handle(Future.succeededFuture());
			} else {
				completionHandler.handle(Future.failedFuture(ar.cause()));
			}
		});
	}

}
