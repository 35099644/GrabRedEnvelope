package com.zzy.grabredenvelope;

import java.io.StringReader;
import java.util.Date;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class MicroMsgChat implements IXposedHookLoadPackage {

	static boolean t = true;
	static boolean sleep = true;
	static long sleeptime = 300;
	static {
		XposedBridge.log("================================");
		XposedBridge.log("----     hook micro msg luckymoney 1.1   ----");
		XposedBridge.log("================================");

	}

	@Override
	public void handleLoadPackage(
			final XC_LoadPackage.LoadPackageParam loadPackageParam)
			throws Throwable {
		if (!loadPackageParam.packageName.contains("tencent.mm")) {
			return;
		}
		XposedBridge.log("mm is runing");
		try {
			XposedHelpers.findAndHookMethod(
					"com.tencent.mm.booter.notification.b",
					loadPackageParam.classLoader, "a",
					"com.tencent.mm.booter.notification.b", String.class,
					String.class, int.class, int.class, boolean.class,
					new XC_MethodHook() {
						@Override
						protected void beforeHookedMethod(MethodHookParam param)
								throws Throwable {
							if (!t) {
								return;
							}
							String msgtype = "436207665";
							if (param.args[3].toString().equals(msgtype)) {
								String xmlmsg = param.args[2].toString();
								// XposedBridge.log(xmlmsg);
								String s = "<msg>";
								int i = xmlmsg.indexOf(s);
								String xl = xmlmsg.substring(i);
								String saveurl = "";
								// nativeurl
								String p = "nativeurl";
								try {
									XmlPullParserFactory factory = XmlPullParserFactory
											.newInstance();
									factory.setNamespaceAware(true);
									XmlPullParser pz = factory.newPullParser();
									pz.setInput(new StringReader(xl));
									int v = pz.getEventType();
									while (v != XmlPullParser.END_DOCUMENT) {
										if (v == XmlPullParser.START_TAG) {
											if (pz.getName().equals(p)) {
												pz.nextToken();
												saveurl = pz.getText();
												break;
											}
										}
										v = pz.next();
									}
								} catch (Exception e) {
									;
								}
								String nativeurl = saveurl;
								Uri nativeUrl = Uri.parse(nativeurl);
								String ver = "v1.0";
								String msgType = nativeUrl
										.getQueryParameter("msgtype");
								String channelId = nativeUrl
										.getQueryParameter("channelid");
								String sendId = nativeUrl
										.getQueryParameter("sendid");
								String headImg = "";
								String nickName = "";
								String sessionUserName = param.args[1]
										.toString();
								// XposedBridge.log("elf luckymoney="+param.args[1].toString());
								// if
								// (param.args[1].toString().endsWith("@chatroom"))
								// {
								// nickName = "������";
								// headImg =
								// "http://wx.qlogo.cn/mmhead/ver_1/icQcmZn5iak8MYgvv3yibWlncxVBaetsTAPmrFXs4vIxhlDRUMKnictOUq0lboHlOTEetkHdocTQ3CrOaxIsJ4PmrdFCCYCCLNYVdjHECeWu6dc/132";
								// sessionUserName =
								// nativeUrl.getQueryParameter("sendusername");
								// }

								final Object x = XposedHelpers.newInstance(
										XposedHelpers
												.findClass(
														"com.tencent.mm.plugin.luckymoney.c.ab",
														loadPackageParam.classLoader),
										Integer.valueOf(msgType), Integer
												.valueOf(channelId), sendId,
										nativeurl, headImg, nickName,
										sessionUserName, ver);
								final Object o = XposedHelpers.callStaticMethod(
										XposedHelpers.findClass(
												"com.tencent.mm.model.ah",
												loadPackageParam.classLoader),
										"tE");
								if (sleep) {
									Thread.sleep(sleeptime);
								}
								XposedBridge.log("elf ,getting luckymoney "
										+ new Date());
								XposedHelpers.callMethod(o, "d", x);
							}
						}
					});

			XposedHelpers.findAndHookMethod(
					"com.tencent.mm.pluginsdk.ui.chat.ChatFooter$2",
					loadPackageParam.classLoader, "onClick", View.class,
					new XC_MethodReplacement() {
						@Override
						protected Object replaceHookedMethod(
								MethodHookParam methodHookParam)
								throws Throwable {
							Object object = methodHookParam.thisObject;
							Object object1 = XposedHelpers.getObjectField(
									object, "iLO");
							Object object2 = XposedHelpers.callStaticMethod(
									object1.getClass(), "a", object1);
							EditText editText = (EditText) object2;
							String command = editText.getEditableText()
									.toString().trim();
							Context context = (Context) XposedHelpers.callStaticMethod(
									XposedHelpers
											.findClass(
													"com.tencent.mm.sdk.platformtools.y",
													loadPackageParam.classLoader),
									"getContext");
							// Context context = (Context)
							// XposedHelpers.callMethod(object1.getClass(),"getContext");
							if (command.equals("��")) {
								t = true;
								Toast.makeText(context, "��������˴�",
										Toast.LENGTH_SHORT).show();
							} else if (command.equals("��")) {
								t = false;
								Toast.makeText(context, "��������˹ر�",
										Toast.LENGTH_SHORT).show();
							} else if (command.equals("����")) {
								sleep = true;
								Toast.makeText(context, "��ʱ�Ѿ�����",
										Toast.LENGTH_SHORT).show();
							} else if (command.equals("����")) {
								sleep = false;
								Toast.makeText(context, "��ʱ�Ѿ��ر�",
										Toast.LENGTH_SHORT).show();
							} else if (command.matches("time\\d{2,}")) {
								String tmp = command.replace("time", "");
								sleeptime = Long.valueOf(tmp);
								Toast.makeText(context, "��ʱ���óɹ�: " + tmp,
										Toast.LENGTH_SHORT).show();
							} else if (command.equals("xfun")) {
								Toast.makeText(
										context,
										"����˵����\n���򿪺��������\n���رպ��������\n����������ʱ\n�����ر���ʱ\ntime���������������ʱ\nxfun ��ʾ������",
										Toast.LENGTH_LONG).show();
							} else {
								return XposedBridge.invokeOriginalMethod(
										methodHookParam.method,
										methodHookParam.thisObject,
										methodHookParam.args);
							}
							editText.setText("");
							// XposedBridge.log("hongbao toggle is " + t);
							// XposedBridge.log("hongbao sleep is " + sleep);
							return null;
						}
					});
		} catch (Exception e) {
		}
	}
}
