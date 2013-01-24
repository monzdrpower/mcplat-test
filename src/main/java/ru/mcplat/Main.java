package ru.mcplat;

import static ru.mcplat.Utils.print;
import static ru.mcplat.Utils.printLine;
import static ru.mcplat.Utils.readLine;
import static ru.mcplat.Utils.init;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;

/**
 * @author denisov
 * 
 *         <p>
 *         Необходимо реализовать на Java консольное приложение, которое
 *         выполняет следующие действия после запуска: Спрашивает имя. Выводит
 *         строку приветствия. Спрашивает данные для подключения к некой БД.
 *         Выводит на экран список всех таблиц указанной БД и рядом в скобках
 *         количество построенных для каждой таблицы индексов. Завершает работу.
 *         <p>
 *         Приложение должно быть "user friendly", стабильным, должно работать с
 *         любой пользовательской БД.
 *         <p>
 *         В качестве СУБД можно использовать по желанию либо MySQL, либо MS SQL
 *         любых свежих версий.
 * 
 *         кодировка консоли - в первом параметре. пример запуска
 *         "java -jar mcplat.jar Cp1251"
 */

public class Main {

	private Connection connection = null;

	private static final String JDBC_DRIVER = "com.microsoft.sqlserver.jdbc.SQLServerDriver";

	private String greetingName = null, url = null, dbname = null, username = null, password = null;

	public static void main(String[] args) {
		
		init(args);
		
		Main main = new Main();
		main.sayHello();
		boolean exit = main.setupConnection();
		if(exit){
			main.sayBye();
			return;
		}
		Map<String, Integer> tableList = main.getTableList();
		main.printTableList(tableList);
		main.sayBye();
	}

	/**
	 * @return true - соединение установлено, false - пользователь прервал работу
	 */
	private boolean setupConnection() {

		while (connection == null) {
			printLine("Введите данные для подключения к MSSQL через JDBC");
			
			print("JDBC URL (пример: jdbc:sqlserver://server_name:1433):");
			url = readLine();
			
			print("название БД:");
			dbname = readLine();
			
			print("логин:");
			username = readLine();
			
			print("пароль:");
			password = readLine();

			try {
				Class.forName(JDBC_DRIVER);
				connection = DriverManager.getConnection(url, username, password);
			} catch (Exception e) {
				print("Ошибка соединения с БД:" + e.getMessage() + "\nхотите попробовать ещё раз? (Нет:n):");
				String decision = readLine();
				if (decision.equalsIgnoreCase("n")) {
					return true;
				}
			}
		}
		return false;

	}

	private void sayHello() {
		print("Представьтесь, пожалуйста:");
		greetingName = readLine();
		printLine("Привет, " + greetingName + ", сейчас мы посмотрим таблицы и количество индексов в БД.");
	}

	private Map<String, Integer> getTableList() {
		if (connection == null)
			return null;

		Map<String, Integer> results = null;

		try {
			ResultSet rs = null;
			try {

				final String query = "select o.name, count(i.object_id) from [" + dbname + "].sys.indexes i, [" + dbname
						+ "].sys.objects o where i.object_id = o.object_id and o.type_desc = 'USER_TABLE' "
						+ "group by o.name order by 1";

				Statement stmt = connection.createStatement();
				rs = stmt.executeQuery(query);

			} catch (SQLException e) {
				printLine("Ошибка доступа к БД:" + e.getMessage() + "\nтребуется переподключение");
			}

			results = new TreeMap<String, Integer>();
			while (rs.next()) {
				results.put(rs.getString(1), rs.getInt(2));
			}
		} catch (SQLException e) {
			printLine("Ошибка доступа к БД:" + e.getMessage() + "\nтребуется переподключение");
		} finally {
			// моё любимое место
			try {
				connection.close();
			} catch (SQLException e) {
				throw new IllegalStateException("Ошибка закрытия соединения с БД");
			}
		}
		return results;
	}

	private void printTableList(Map<String, Integer> results) {

		if (results == null || results.isEmpty()) {
			printLine("Таблицы не найдены в БД " + dbname);
			return;
		}

		printLine("");
		// добавим форматирование
		int max = 0;
		for (String key : results.keySet()) {
			max = key.length() > max ? key.length() : max;
		}

		for (String key : results.keySet()) {
			String pattern = StringUtils.rightPad(key, max + 3, ".") + " (" + results.get(key) + ")";
			printLine(pattern);
		}
	}

	private void sayBye() {
		printLine("Пока, " + greetingName + "!");
	}

}
