package ru.mcplat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

public class Utils {

	private static PrintStream out = null;
	private static BufferedReader in = null;

	public static void init(String[] args) {

		String encoding = System.getProperty("file.encoding");

		if (args.length > 0) {
			encoding = args[0];
		}

		try {
			out = new PrintStream(System.out, true, encoding);
			in = new BufferedReader(new InputStreamReader(System.in, encoding));
		} catch (UnsupportedEncodingException e) {
			throw new IllegalStateException("Кодировка " + encoding + " не поддерживается");
		}

	}
	
	public static String readLine() {
		if (in == null)
			throw new IllegalStateException("Не инициализирован поток ввода");
		try {
			return in.readLine();
		} catch (IOException e) {
			throw new IllegalStateException("Ошибка ввода/вывода " + e.getMessage());
		}
	}

	public static void print(String string) {
		if (out == null)
			throw new IllegalStateException("Не инициализирован поток вывода");
		out.print(string);
	}

	public static void printLine(String string) {
		print(string + "\n");
	}

	
}
