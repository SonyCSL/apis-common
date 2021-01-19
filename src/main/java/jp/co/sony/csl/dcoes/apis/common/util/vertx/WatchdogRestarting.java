package jp.co.sony.csl.dcoes.apis.common.util.vertx;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

/**
 * This is the default implementation for restarting WatchDog.
 * Simply executes HTTP GET for specified URL.
 * @author OES Project
 * WatchDog 再起動のデフォルト実装.
 * 指定された URL に対し定期的に HTTP GET するだけ.
 * @author OES Project
 */
public class WatchdogRestarting extends AbstractVerticle {
	private static final Logger log = LoggerFactory.getLogger(WatchdogRestarting.class);

	/**
	 * This is the default cycle for restarting WatchDog.
	 * The value is {@value}.
	 * WatchDog を再起動する周期のデフォルト.
	 * 値は {@value}.
	 */
	private static final Long DEFAULT_PERIOD_MSEC = 5000L;
	/**
	 * This is the default HTTP request timeout value.
	 * The value is {@value}.
	 * HTTP リクエストのタイムアウト時間のデフォルト.
	 * 値は {@value}.
	 */
	private static final Long DEFAULT_REQUEST_TIMEOUT_MSEC = 5000L;

	private long watchdogRestartingTimerId_ = 0L;
	private boolean stopped_ = false;
	private HttpClient client_;
	private String uri_;

	/**
	 * Called during startup.
	 * Launches timer.
	 * @param startFuture {@inheritDoc}
	 * @throws Exception {@inheritDoc}
	 * 起動時に呼び出される.
	 * タイマを起動する.
	 * @param startFuture {@inheritDoc}
	 * @throws Exception {@inheritDoc}
	 */
	@Override public void start(Future<Void> startFuture) throws Exception {
		init(resInit -> {
			if (resInit.succeeded()) {
				if (client_ != null) {
					watchdogRestartingTimerHandler_(0L);
				}
				if (log.isTraceEnabled()) log.trace("started : " + deploymentID());
				startFuture.complete();
			} else {
				startFuture.fail(resInit.cause());
			}
		});
	}

	/**
	 * Called when stopped.
	 * Sets flag to stop timer.
	 * @throws Exception {@inheritDoc}
	 * 停止時に呼び出される.
	 * タイマを止めるためのフラグを立てる.
	 * @throws Exception {@inheritDoc}
	 */
	@Override public void stop() throws Exception {
		stopped_ = true;
		if (log.isTraceEnabled()) log.trace("stopped : " + deploymentID());
	}

	////

	/**
	 * Reads settings from CONFIG and initializes.
	 * - {@code CONFIG.watchdog.enabled}
	 * - {@code CONFIG.watchdog.host}
	 * - {@code CONFIG.watchdog.port}
	 * - {@code CONFIG.watchdog.uri}
	 * @param completionHandler the completion handler
	 * CONFIG から設定を読み込み初期化する.
	 * - {@code CONFIG.watchdog.enabled}
	 * - {@code CONFIG.watchdog.host}
	 * - {@code CONFIG.watchdog.port}
	 * - {@code CONFIG.watchdog.uri}
	 * @param completionHandler the completion handler
	 */
	protected void init(Handler<AsyncResult<Void>> completionHandler) {
		Boolean enabled = VertxConfig.config.getBoolean(Boolean.FALSE, "watchdog", "enabled");
		if (enabled) {
			if (log.isInfoEnabled()) log.info("watchdog enabled");
			String host = VertxConfig.config.getString("watchdog", "host");
			Integer port = VertxConfig.config.getInteger("watchdog", "port");
			uri_ = VertxConfig.config.getString("watchdog", "uri");
			if (host != null && port != null && uri_ != null && !host.isEmpty() && 0 < port && !uri_.isEmpty()) {
				client_ = vertx.createHttpClient(new HttpClientOptions().setDefaultHost(host).setDefaultPort(port));
				completionHandler.handle(Future.succeededFuture());
			} else {
				completionHandler.handle(Future.failedFuture("invalid watchdog.host and/or watchdog.port and/or watchdog.uri value in config : " + VertxConfig.config.jsonObject()));
			}
		} else {
			if (log.isInfoEnabled()) log.info("watchdog disabled");
			completionHandler.handle(Future.succeededFuture());
		}
	}

	/**
	 * Sets timer for periodically executing WatchDog restart process.
	 * The wait time is {@code CONFIG.watchdog.periodMsec} ( default value {@link #DEFAULT_PERIOD_MSEC} ).
	 * WatchDog 再起動処理を定期的に実行するためのタイマを設定する.
	 * 待ち時間は {@code CONFIG.watchdog.periodMsec} ( デフォルト値 {@link #DEFAULT_PERIOD_MSEC} ).
	 */
	private void setWatchdogRestartingTimer_() {
		Long delay = VertxConfig.config.getLong(DEFAULT_PERIOD_MSEC, "watchdog", "periodMsec");
		setWatchdogRestartingTimer_(delay);
	}
	/**
	 * Sets timer for periodically executing WatchDog restart process.
	 * @param delay cycle [ms]
	 * WatchDog 再起動処理を定期的に実行するためのタイマを設定する.
	 * @param delay 周期 [ms]
	 */
	private void setWatchdogRestartingTimer_(long delay) {
		watchdogRestartingTimerId_ = vertx.setTimer(delay, this::watchdogRestartingTimerHandler_);
	}
	/**
	 * Restarts WatchDog process.
	 * @param timerId timer ID
	 * WatchDog 再起動処理.
	 * @param timerId タイマ ID
	 */
	private void watchdogRestartingTimerHandler_(Long timerId) {
		if (stopped_) return;
		if (null == timerId || timerId.longValue() != watchdogRestartingTimerId_) {
			if (log.isWarnEnabled()) log.warn("illegal timerId : " + timerId + ", watchdogRestartingTimerId_ : " + watchdogRestartingTimerId_);
			return;
		}
		new Sender_().execute_(r -> {
			if (r.succeeded()) {
				// nop
			} else {
				log.error(r.cause().getMessage());
			}
			setWatchdogRestartingTimer_();
		});
	}

	////

	/**
	 * Periodically executes HTTP GET for specified URL.
	 * @author OES Project
	 * 指定された URL に対し定期的に HTTP GET するだけ.
	 * @author OES Project
	 */
	private class Sender_ {
		private boolean completed_ = false;
		/**
		 * Executes HTTP GET process.
		 * (Maybe because of poor implementation) The result may be returned twice, so this is blocked here.
		 * @param completionHandler the completion handler
		 * HTTP GET 処理実行.
		 * ( 実装がまずいのか ) 二度結果が返ってくることがあるためここでブロックする.
		 * @param completionHandler the completion handler
		 */
		private void execute_(Handler<AsyncResult<Void>> completionHandler) {
			send_(r -> {
				if (!completed_) {
					completed_ = true;
					completionHandler.handle(r);
				} else {
					log.error("send_() result returned more than once : " + r);
				}
			});
		}
		/**
		 * Executes HTTP GET process.
		 * HTTP timeout is {@code CONFIG.watchdog.enabled} ( default value {@link #DEFAULT_REQUEST_TIMEOUT_MSEC} ).
		 * @param completionHandler the completion handler
		 * HTTP GET 処理実行.
		 * HTTP タイムアウト は {@code CONFIG.watchdog.enabled} ( デフォルト値 {@link #DEFAULT_REQUEST_TIMEOUT_MSEC} ).
		 * @param completionHandler the completion handler
		 */
		private void send_(Handler<AsyncResult<Void>> completionHandler) {
			Long requestTimeoutMsec = VertxConfig.config.getLong(DEFAULT_REQUEST_TIMEOUT_MSEC, "watchdog", "requestTimeoutMsec");
			client_.get(uri_, resGet -> {
				if (log.isDebugEnabled()) log.debug("status : " + resGet.statusCode());
				if (resGet.statusCode() == 200) {
					completionHandler.handle(Future.succeededFuture());
				} else {
					resGet.bodyHandler(error -> {
						completionHandler.handle(Future.failedFuture("http get failed : " + resGet.statusCode() + " : " + resGet.statusMessage() + " : " + error));
					}).exceptionHandler(t -> {
						completionHandler.handle(Future.failedFuture("http get failed : " + resGet.statusCode() + " : " + resGet.statusMessage() + " : " + t));
					});
				}
			}).setTimeout(requestTimeoutMsec).exceptionHandler(t -> {
				completionHandler.handle(Future.failedFuture(t));
			}).end();
		}
	}

}
