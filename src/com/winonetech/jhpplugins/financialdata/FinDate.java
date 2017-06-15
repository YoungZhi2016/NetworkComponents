package com.winonetech.jhpplugins.financialdata;

import java.io.File;
import java.io.IOException;

import com.winonetech.jhpplugins.utils.FileUtils;
import com.winonetech.jhpplugins.utils.ICEUtil;
import com.winonetech.jhpplugins.utils.Logs;

/**
 * Created by Administrator on 2017/6/14. 金融数据
 */
public class FinDate {
	private final static String SAVEFINA_PATH = "D:\\financialData.xml";

	// 保存金融数据
	public static void saveFinData() {
		try {
			File file = new File(SAVEFINA_PATH);
			FileUtils.writeFile(SAVEFINA_PATH, obtFinData());
			if (file.exists() && file.isFile())
				noticeTerminal();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// 获取金融数据
	public static String obtFinData() {
		return ICEUtil.geTerminalServicePrx().getFinData();
	}

	// 通知终端获取
	private static void noticeTerminal() {
		Logs.info("------>>> ͨ通知终端 ");
	}

}
