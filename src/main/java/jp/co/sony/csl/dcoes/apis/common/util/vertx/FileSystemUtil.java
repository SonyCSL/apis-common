package jp.co.sony.csl.dcoes.apis.common.util.vertx;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

/**
 * Implements file system utility.
 * @author OES Project
 * ファイルシステム便利機能.
 * @author OES Project
 */
public class FileSystemUtil {
	private static final Logger log = LoggerFactory.getLogger(FileSystemUtil.class);

	private FileSystemUtil() { }

	/**
	 * Creates directory hierarchy using the specified path.
	 * Returns success even if directory hierarchy already exists.
	 * @param vertx vertx instance
	 * @param path path
	 * @param completionHandler the completion handler
	 * 指定されたパスでディレクトリ階層を作る.
	 * すでに存在していても成功を返す.
	 * @param vertx vertx インスタンス
	 * @param path パス
	 * @param completionHandler the completion handler
	 */
	public static void ensureDirectory(Vertx vertx, String path, Handler<AsyncResult<Void>> completionHandler) {
		vertx.fileSystem().exists(path, resExists -> {
			if (resExists.succeeded()) {
				if (resExists.result()) {
					// Already exists → Success
					// もうある → 成功
					completionHandler.handle(Future.succeededFuture());
				} else {
					// Does not exist
					// ない
					vertx.fileSystem().mkdirs(path, resMkdirs -> {
						if (resMkdirs.succeeded()) {
							// → Created → Success
							// → 作れた → 成功
							completionHandler.handle(Future.succeededFuture());
						} else {
							// → Could not create → Reconfirms because the file may have been created in between confirming and creating
							// → 作れなかった → 確認してから作るまでにできた可能性があるので再確認
							vertx.fileSystem().exists(path, resExistsAgain -> {
								if (resExistsAgain.succeeded()) {
									if (resExistsAgain.result()) {
										// → Exists → Success
										// → あった → 成功
										completionHandler.handle(Future.succeededFuture());
									} else {
										// → File does not exist after all → Error
										// → やっぱりなかった → エラー
										log.error(resMkdirs.cause());
										completionHandler.handle(Future.failedFuture(resMkdirs.cause()));
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

}
