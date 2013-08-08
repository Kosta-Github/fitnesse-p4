package fitnesse.wiki.cmSystems;

import java.io.*;
import java.util.*;

public class PerforceCmSystem {
  
	// is called just before file is about to be written
	public static void cmUpdate(String file, String payload) throws Exception {
		// System.err.println("cmUpdate: " + file);

		if (isIgnored(file)) {
			return;
		}

		Map<String, String> fstats = getFileStats(file);

		if (isUnknown(fstats)) {
			executeP4("cmUpdate", "add " + file);
		}
	}

	// is called just after file has been written
	public static void cmEdit(String file, String payload) throws Exception {
		// System.err.println("cmEdit: " + file);

		if (isIgnored(file)) {
			return;
		}

		Map<String, String> fstats = getFileStats(file);

		if (isUnknown(fstats)) {
			return;
		}

		if (isIntegratedOrBranched(fstats)) {
			executeP4("cmEdit", "reopen " + file);
		}

		if (isOpened(fstats) && !isOpenForDelete(fstats)) {
			return;
		}

		if (isOpenForDelete(fstats)) {
			executeP4("cmEdit", "revert " + file);
		}

		executeP4("cmEdit", "edit " + file);
	}

	// is called just before the directory defining a page will be deleted
	public static void cmPreDelete(String folder, String payload) throws Exception {
		// System.err.println("cmPreDelete: " + folder);
		
		// nothing to do for P4
	}

	// is called just after the directory defining a page has been deleted
	public static void cmDelete(String folder, String payload) throws Exception {
		// System.err.println("cmDelete: " + folder);
		
		String directoryPath = folder + "/...";
		String contentFilename = "content.txt";
		Map<String, String> fstats = getFileStats(folder + "/" + contentFilename);

		if (isUnknown(fstats)) {
			return;
		}

		if (isOpenForDelete(fstats)) {
			return;
		}

		if (isOpened(fstats)) {
			executeP4("cmDelete", "revert " + directoryPath);
		}

		if (!isOpenForAdd(fstats)) {
			executeP4("cmDelete", "delete " + directoryPath);
		}
	}


	private static String readFromStream(InputStream input) throws Exception {
		BufferedReader buf = new BufferedReader(new InputStreamReader(input));

		String output = "";
		String line;
		while((line = buf.readLine()) != null) {
			output += line + "\n";
		}
		return output;
	}

	private static String executeP4(String method, String command) throws Exception {
		try {
			System.out.println("p4 " + command);
		
			Process proc = Runtime.getRuntime().exec("p4.exe " + command);
			proc.waitFor();
			
			String output = readFromStream(proc.getInputStream());
			
			int exitValue = proc.exitValue();
			if (exitValue != 0) {
				System.err.println(method + " command: " + command);
				System.err.println(method + " exit code: " + exitValue);
				System.err.println(method + " out:" + output);
				System.err.println(method + " err:" + readFromStream(proc.getErrorStream()));
			}
			
			return output;
		} catch(Exception ex) {
			System.err.println("exception: " + ex.toString());
			ex.printStackTrace();
			return "";
		}
	}

	private static Map<String, String> getFileStats(String filePath) throws Exception {
		String fstatOutput = executeP4("getFileStats", "fstat " + filePath);

		Map<String, String> fstatMap = new HashMap<String, String>();
		for (String line : fstatOutput.split("\n")) {
			String[] tokenizedLine = line.split(" ");
			if (tokenizedLine.length > 2) {
				fstatMap.put(tokenizedLine[1], tokenizedLine[2]);
			}
		}
		return fstatMap;
	}

	private static List<String> ignoredPaths;
	static {
		ignoredPaths = new ArrayList<String>();
		ignoredPaths.add("/RecentChanges/");
		ignoredPaths.add("/ErrorLogs/");
	}

	private static boolean isIgnored(String filePath) {
		File currentFile = new File(filePath);
		String absolutePath = currentFile.getAbsolutePath().replace('\\', '/');

		for (String ignoredItem : ignoredPaths) {
			if (absolutePath.contains(ignoredItem)) {
				return true;
			}
		}

		if (!currentFile.exists()) {
			return true;
		}

		if (currentFile.isDirectory()) {
			return true;
		}

		return false;
	}

	private static boolean isIntegratedOrBranched(Map<String, String> fstats) {
		return (
			"integrate".equals(fstats.get("action")) ||
			"branch".equals(fstats.get("action"))
		);
	}

	private static boolean isOpened(Map<String, String> fstats) {
		return (fstats.get("action") != null);
	}

	private static boolean isOpenForAdd(Map<String, String> fstats) {
		return "add".equals(fstats.get("action"));
	}

	private static boolean isOpenForDelete(Map<String, String> fstats) {
		return "delete".equals(fstats.get("action"));
	}

	private static boolean isUnknown(Map<String, String> fstats) {
		return fstats.get("clientFile") == null;
	}
}
