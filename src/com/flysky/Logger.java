package com.flysky;

import java.io.FileWriter;
import java.io.PrintWriter;

public class Logger {
	public static void log(Throwable ex) {
		log(ex.getMessage());
	}
	public static void log(String info) {
		// Open matches log file.
		PrintWriter logFileWriter=null;
		try {
		   logFileWriter = new PrintWriter(new FileWriter("crawler.log"));
		   logFileWriter.append(info);
		} catch (Exception e) {
		}finally {
			if(logFileWriter!=null) {
				logFileWriter.close();
			}
		}
	}
}
