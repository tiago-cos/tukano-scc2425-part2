package utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Convenience class for parsing main class arguments...
 *
 * @author smduarte
 */
public class Args {

	static Map<String, String[]> _args = new HashMap<>();

	static String[] _current = new String[0];

	public static void use(String[] args) {
		_current = args;
	}

	public static void useArgs(String key) {
		_current = _args.get(key);
		if (_current == null)
			throw new RuntimeException("Unknown key...");
	}

	public static void setArgs(String key, String[] args) {
		_current = args;
		_args.put(key, args);
	}

	public static boolean contains(String flag) {
		return contains(_current, flag);
	}

	public static String valueOf(String flag, String defaultValue) {
		return valueOf(_current, flag, defaultValue);
	}

	public static int valueOf(String flag, int defaultValue) {
		return valueOf(_current, flag, defaultValue);
	}

	public static double valueOf(String flag, double defaultValue) {
		return valueOf(_current, flag, defaultValue);
	}

	public static boolean valueOf(String flag, boolean defaultValue) {
		return valueOf(_current, flag, defaultValue);
	}

	public static String[] valueOf(String flag, String[] defaultValue) {
		return valueOf(_current, flag, defaultValue);
	}

	public static List<String> subList(String flag) {
		return subList(_current, flag);
	}

	public static boolean contains(String[] args, String flag) {
		return Arrays.asList(args).contains(flag);
	}

	public static String valueOf(String[] args, int pos, String defaultValue) {
		return args.length > pos ? args[pos] : defaultValue;
	}

	public static int valueOf(String[] args, int pos, int defaultValue) {
		return args.length > pos ? Integer.valueOf(args[pos]) : defaultValue;
	}

	public static boolean valueOf(int pos, boolean defaultValue) {
		return _current.length > pos ? Boolean.valueOf(_current[pos]) : defaultValue;
	}

	public static String valueOf(String[] args, String flag, String defaultValue) {
		for (int i = 0; i < args.length - 1; i++)
			if (flag.equals(args[i]))
				return args[i + 1];
		return defaultValue;
	}

	public static int valueOf(String[] args, String flag, int defaultValue) {
		for (int i = 0; i < args.length - 1; i++)
			if (flag.equals(args[i]))
				return Integer.parseInt(args[i + 1]);
		return defaultValue;
	}

	public static double valueOf(String[] args, String flag, double defaultValue) {
		for (int i = 0; i < args.length - 1; i++)
			if (flag.equals(args[i]))
				return Double.parseDouble(args[i + 1]);
		return defaultValue;
	}

	public static boolean valueOf(String[] args, String flag, boolean defaultValue) {
		for (int i = 0; i < args.length - 1; i++)
			if (flag.equals(args[i]))
				return Boolean.parseBoolean(args[i + 1]);
		return defaultValue;
	}

	public static String[] valueOf(String[] args, String flag, String[] defaultValue) {
		var outArgs = new LinkedList<>();
		for (int i = 0; i < args.length - 1; i++)
			if (flag.equals(args[i])) {
				i++;
				while (!args[i].startsWith("-") && i < args.length) {
					outArgs.add(args[i]);
					i++;
				}
				return outArgs.toArray(new String[]{});
			}
		return defaultValue;
	}

	public static List<String> subList(String[] args, String flag) {
		var res = new ArrayList<String>();
		for (int i = 0; i < args.length - 1; i++)
			if (flag.equals(args[i])) {
				for (int j = i + 1; j < args.length; j++)
					if (args[j].startsWith("-"))
						return res;
					else
						res.add(args[j]);
			}
		return res;
	}

	public static String[] getCurrent() {
		var lines = new LinkedList<String>();
		var line = new StringBuilder();
		for (var arg : _current) {
			if (arg.startsWith("-")) {
				if (line.length() > 0)
					lines.add(line.toString());
				line = new StringBuilder();
				line.append(arg);
			} else
				line.append(" " + arg);
		}
		return lines.toArray(new String[]{});
	}

	public static String dumpArgs() {
		var result = new StringBuilder();
		for (var s : getCurrent()) {
			result.append(s);
			result.append("\n");
		}
		return result.toString();
	}
}
