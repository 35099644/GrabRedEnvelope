package com.zzy.grabredenvelope;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import org.json.JSONObject;

import android.content.Context;
import android.os.Build;
import android.widget.EditText;
import android.widget.Toast;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class QQChat implements IXposedHookLoadPackage {
	static String msgUid = "";
	static String frienduin = "";
	static int istroop = 0;
	static int grouptype;
	static String selfuin = "";
	static String selfname = "";
	static String senderuin = "";
	static long time = 0L;
	static Context globalcontext = null;
	static Context context = null;
	static Object FriendsManager = null;
	static Object HotChatManager = null;
	static Object globalqqinterface = null;
	static Object tick = null;
	static ClassLoader loader;
	static boolean toggle = true;
	static boolean passwd = false;
	static boolean sleep = false;
	static long sleeptime = 500;
	XC_MethodHook.Unhook unhook = null;

	static {
		XposedBridge.log("---------------");
		XposedBridge.log("--------ELF_QQhongbao----------");
		XposedBridge.log("---------------");
	}

	public void log(String tag, Object log) {
		XposedBridge.log("[" + tag + "]" + log.toString());
		;
	}

	private void sendpasswd(XC_LoadPackage.LoadPackageParam loadPackageParam,
			String passwd) throws Exception {
		Object session = XposedHelpers.newInstance(XposedHelpers.findClass(
				"com.tencent.mobileqq.activity.aio.SessionInfo",
				loadPackageParam.classLoader));
		Field[] fields = session.getClass().getDeclaredFields();
		for (Field field : fields) {
			field.setAccessible(true);
		}
		fields[0].setInt(session, istroop);
		fields[1].setLong(session, new Date().getTime() / 1000);
		fields[2].set(session, null);
		fields[3].set(session, frienduin);
		fields[5].set(session, frienduin);
		fields[6].setInt(session, 1);
		fields[8].setInt(session, 10004);
		ArrayList arrayList = new ArrayList();
		Object ChatActivityFacade$SendMsgParams = XposedHelpers
				.newInstance(XposedHelpers
						.findClass(
								"com.tencent.mobileqq.activity.ChatActivityFacade$SendMsgParams",
								loadPackageParam.classLoader));
		// log("passwd", "sending");
		XposedHelpers.callStaticMethod(XposedHelpers.findClass(
				"com.tencent.mobileqq.activity.ChatActivityFacade",
				loadPackageParam.classLoader), "a", globalqqinterface, context,
				session, passwd, arrayList, ChatActivityFacade$SendMsgParams);
	}

	private void toshow(String message) {
		Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
	}

	private void dohook(final XC_LoadPackage.LoadPackageParam loadPackageParam) {

		XposedHelpers.findAndHookConstructor("mqq.app.TicketManagerImpl",
				loadPackageParam.classLoader, "mqq.app.AppRuntime",
				new XC_MethodHook() {
					@Override
					protected void afterHookedMethod(MethodHookParam param)
							throws Throwable {
						tick = param.thisObject;
					}
				});
		XposedHelpers.findAndHookMethod(
				"com.tencent.mobileqq.app.MessageHandlerUtils",
				loadPackageParam.classLoader, "a",
				"com.tencent.mobileqq.app.QQAppInterface",
				"com.tencent.mobileqq.data.MessageRecord", Boolean.TYPE,
				new XC_MethodHook() {
					@Override
					protected void afterHookedMethod(MethodHookParam param)
							throws Throwable {
						if (!toggle) {
							return;
						}
						senderuin = XposedHelpers.getObjectField(param.args[1],
								"senderuin").toString();
						log("hongbao debug messagerecord param z senderuin",
								senderuin);
						selfuin = XposedHelpers.getObjectField(param.args[1],
								"selfuin").toString();
						log("hongbao debug messagerecord param z selfuin",
								selfuin);
						String msgtype = XposedHelpers.getObjectField(
								param.args[1], "msgtype").toString();
						log("hongbao debug messagerecord param z msgtype",
								msgtype);
						String msgid = XposedHelpers.getObjectField(
								param.args[1], "msgUid").toString();
						frienduin = XposedHelpers.getObjectField(param.args[1],
								"frienduin").toString();
						log("hongbao debug messagerecord param z frienduin",
								frienduin);
						istroop = (int) XposedHelpers.getObjectField(
								param.args[1], "istroop");
						log("hongbao debug messagerecord param z istroop",
								istroop);
						log("hongbao debug messagerecord param z msg",
								XposedHelpers.getObjectField(param.args[1],
										"msg"));
						log("hongbao debug", param.args[1].toString());
						if (msgtype.equals("-2025")
								&& !senderuin.equals(selfuin)) {
							msgUid = msgid;
						}
					}
				});
		XposedHelpers.findAndHookMethod(
				"com.tencent.mobileqq.activity.BaseChatPie",
				loadPackageParam.classLoader, "onClick", "android.view.View",
				new XC_MethodReplacement() {
					@Override
					protected Object replaceHookedMethod(
							MethodHookParam methodHookParam) throws Throwable {
						String viewname = methodHookParam.args[0].getClass()
								.getName();
						log("hongbao debug click view name", viewname);
						if (viewname.equals("com.tencent.widget.PatchedButton")) {
							Field field1 = XposedHelpers.findFirstFieldByExactType(
									XposedHelpers
											.findClass(
													"com.tencent.mobileqq.activity.BaseChatPie",
													loadPackageParam.classLoader),
									XposedHelpers
											.findClass(
													"com.tencent.mobileqq.activity.aio.SessionInfo",
													loadPackageParam.classLoader));
							Object session = field1
									.get(methodHookParam.thisObject);
							Field[] fields = session.getClass()
									.getDeclaredFields();
							for (Field field : fields) {
								field.setAccessible(true);
								Object o = field.get(session);
								if (o == null) {
									log("hongbao filed is", " null");
								} else {
									log("hongbao field is", o.toString());
								}
							}

							EditText editText = (EditText) XposedHelpers
									.findFirstFieldByExactType(
											XposedHelpers
													.findClass(
															"com.tencent.mobileqq.activity.BaseChatPie",
															loadPackageParam.classLoader),
											XposedHelpers
													.findClass(
															"com.tencent.widget.XEditTextEx",
															loadPackageParam.classLoader))
									.get(methodHookParam.thisObject);
							String command = editText.getText().toString();
							if (command.equals("��")) {
								toggle = true;
								editText.setText("");
								toshow("����������Ѿ���");
							} else if (command.equals("��")) {
								toggle = false;
								editText.setText("");
								toshow("����������Ѿ��ر�");
							} else if (command.equals("����")) {
								sleep = true;
								editText.setText("");
								toshow("��ʱ�Ѿ���");
							} else if (command.equals("����")) {
								sleep = false;
								editText.setText("");
								toshow("��ʱ�Ѿ��ر�");
							} else if (command.equals("����")) {
								passwd = true;
								editText.setText("");
								toshow("�����Ѿ���");
							} else if (command.equals("����")) {
								passwd = false;
								editText.setText("");
								toshow("�����Ѿ��ر�");
							} else if (command.matches(",time\\d+")) {
								String tmp = command.replace(",time", "");
								sleeptime = Long.valueOf(tmp);
								toshow("��ʱ���óɹ�");
							} else {
								XposedBridge.invokeOriginalMethod(
										methodHookParam.method,
										methodHookParam.thisObject,
										methodHookParam.args);
							}
						} else {
							XposedBridge.invokeOriginalMethod(
									methodHookParam.method,
									methodHookParam.thisObject,
									methodHookParam.args);
						}
						return null;
					}
				});

		XposedHelpers
				.findAndHookMethod(
						"com.tencent.mobileqq.activity.aio.item.QQWalletMsgItemBuilder",
						loadPackageParam.classLoader,
						"a",
						"jjv",
						"com.tencent.mobileqq.data.MessageForQQWalletMsg",
						"com.tencent.mobileqq.activity.aio.OnLongClickAndTouchListener",
						new XC_MethodHook() {
							int issend;

							@Override
							protected void beforeHookedMethod(
									MethodHookParam param) throws Throwable {
								issend = (int) XposedHelpers.getObjectField(
										param.args[1], "issend");
								if (issend != 1) {
									XposedHelpers.setObjectField(param.args[1],
											"issend", 1);
								}
							}

							@Override
							protected void afterHookedMethod(
									MethodHookParam param) throws Throwable {
								XposedHelpers.setObjectField(param.args[1],
										"issend", issend);
							}
						});

		XposedHelpers.findAndHookMethod(
				"com.tencent.mobileqq.data.MessageForQQWalletMsg",
				loadPackageParam.classLoader, "doParse", new XC_MethodHook() {
					@Override
					protected void afterHookedMethod(MethodHookParam param)
							throws Throwable {
						if (true) {
							return;
						}
						if (!toggle || msgUid.equals("")) {
							return;
						}
						// log("hongbao debug", "hongbao message");
						msgUid = "";
						JSONObject jsonObject = new JSONObject();
						selfname = "Tencent";
						grouptype = 0;
						if (String.valueOf(istroop).equals("3000")) {
							// bb8
							grouptype = 2;
						} else if (String.valueOf(istroop).equals("1")) {

							Map map = (Map) XposedHelpers
									.findFirstFieldByExactType(
											HotChatManager.getClass(),
											Map.class).get(HotChatManager);
							if (map != null & map.containsKey(frienduin)) {
								grouptype = 5;
							} else {
								grouptype = 1;
							}
						} else if (String.valueOf(istroop).equals("0")) {
							grouptype = 0;

						} else if (String.valueOf(istroop).equals("1004")) {
							// 3ec
							grouptype = 4;

						} else if (String.valueOf(istroop).equals("1000")) {
							// 3e8
							grouptype = 3;

						} else if (String.valueOf(istroop).equals("1001")) {
							// 3e9
							grouptype = 6;

						}
						Object mQQWalletRedPacketMsg = XposedHelpers
								.getObjectField(param.thisObject,
										"mQQWalletRedPacketMsg");
						String redPacketId = XposedHelpers.getObjectField(
								mQQWalletRedPacketMsg, "redPacketId")
								.toString();
						int messageType = (int) XposedHelpers.getObjectField(
								param.thisObject, "messageType");
						String authkey = XposedHelpers.getObjectField(
								mQQWalletRedPacketMsg, "authkey").toString();
						Object QQWalletTransferMsgElem = XposedHelpers
								.getObjectField(mQQWalletRedPacketMsg, "elem");
						String nativeAndroid = XposedHelpers.getObjectField(
								QQWalletTransferMsgElem, "nativeAndroid")
								.toString();
						String redpacketpasswd = XposedHelpers.getObjectField(
								QQWalletTransferMsgElem, "title").toString();
						// log("hongbao debug: nativeAndroid", nativeAndroid);
						// log("hongbao debug: messageType:", messageType);
						// log("hongbao debug: authkey;", authkey);
						// log("hongbao debug: redPacketId;", redPacketId);
						// log("hongbao debug: redpacketpasswd",
						// redpacketpasswd);
						{
							jsonObject.put("listid", redPacketId);
							jsonObject.put("name", "TencentRobot");
							jsonObject.put("authkey", authkey);
							jsonObject.put("trans_seq", 1);
							jsonObject.put("uin", selfuin);
							if (String.valueOf(istroop).equals("0")) {
								jsonObject.put("groupid", selfuin);
							} else {
								jsonObject.put("groupid", frienduin);
							}
							jsonObject.put("groupuin", frienduin);
							jsonObject.put("grouptype", grouptype);
							String skey = "";
							skey = XposedHelpers.callMethod(tick, "getSkey",
									selfuin).toString();
							jsonObject.put("skey", skey);
							jsonObject.put("skey_type", "2");
							jsonObject.put("channel", 2);
							XposedBridge.log(jsonObject.toString());
							Object object = XposedHelpers.callStaticMethod(
									loader.loadClass("com.tenpay.android.qqplugin.a.c"),
									"a", "com.tenpay.android.qqplugin.a.ac");
							final String url = XposedHelpers.callMethod(object,
									"a", context, jsonObject, true).toString();
							final Object object1 = loader
									.loadClass(
											"com.tenpay.android.qqplugin.b.d")
									.getDeclaredConstructor(boolean.class)
									.newInstance(true);
							if (sleep) {
								Thread.sleep(sleeptime);
							}
							XposedHelpers.callMethod(object1, "a", url);
							log("------", "�����");
							// log("hongbao debug url:====", url);
							// log("hongbao debug jsobject", jsonObject);
							if (messageType == 6) {
								sendpasswd(loadPackageParam, redpacketpasswd);
							}
						}
						return;

					}
				});

		XposedHelpers.findAndHookConstructor(
				"com.tencent.mobileqq.app.HotChatManager",
				loadPackageParam.classLoader,
				"com.tencent.mobileqq.app.QQAppInterface", new

				XC_MethodHook() {
					@Override
					protected void afterHookedMethod(MethodHookParam param)
							throws Throwable {
						HotChatManager = param.thisObject;
						globalqqinterface = param.args[0];
						context = (Context) XposedHelpers.callStaticMethod(
								XposedHelpers
										.findClass(
												"com.tencent.common.app.BaseApplicationImpl",
												loadPackageParam.classLoader),
								"getContext");
						loader = (ClassLoader) XposedHelpers.callStaticMethod(
								XposedHelpers
										.findClass(
												"com.tencent.mobileqq.pluginsdk.PluginStatic",
												loadPackageParam.classLoader),
								"getOrCreateClassLoader", context,
								"qwallet_plugin.apk");
					}
				});
	}

	@Override
	public void handleLoadPackage(
			final XC_LoadPackage.LoadPackageParam loadPackageParam)
			throws Throwable {

		if (loadPackageParam.packageName.equals("com.tencent.mobileqq")) {
			log("tencent", "found the qq runing");
			int ver = Build.VERSION.SDK_INT;
			if (ver < 21) {
				XposedHelpers.findAndHookMethod(
						"com.tencent.common.app.BaseApplicationImpl",
						loadPackageParam.classLoader, "onCreate",
						new XC_MethodHook() {
							@Override
							protected void afterHookedMethod(
									MethodHookParam param) throws Throwable {
								log("hongbao debug",
										"has hooked all function system ver:<5.0");
								dohook(loadPackageParam);
							}
						});
			} else {
				log("hongbao debug", "has hooked all function system ver:>5.0");
				dohook(loadPackageParam);
			}
		}
	}
}
