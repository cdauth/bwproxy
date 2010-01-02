package de.cdauth.bwproxy;

import java.util.Date;

import java.text.SimpleDateFormat;

/**
 * This static class prints any log file information to the right place.
 * @author Candid Dauth
 * @license GPL-3
*/

public class Logger
{
	/**
	 * A fatal error occured that causes the program to stop.
	*/

	public static void fatal(String a_message)
	{
		java.lang.System.err.println(timePrefix() + a_message);
		java.lang.System.exit(1);
	}

	public static void fatal(String a_message, Throwable a_exception)
	{
		java.lang.System.err.println(timePrefix() + a_message + ": " + a_exception.getMessage());
		a_exception.printStackTrace(java.lang.System.err);
		java.lang.System.exit(1);
	}

	/**
	 * An error occured that has to be printed in any case.
	*/

	public static void error(String a_message)
	{
		java.lang.System.err.println(timePrefix() + a_message);
	}

	public static void error(String a_message, Throwable a_exception)
	{
		error(a_message + ": " + a_exception.getMessage());
		a_exception.printStackTrace(java.lang.System.err);
	}

	public static void debug(String a_message)
	{
		error(a_message);
	}

	public static void debug(String a_message, Throwable a_exception)
	{
		error(a_message, a_exception);
	}

	/**
	 * Creates a prefix string for log output that contains
	 * the time.
	 * @return "YYYY-MM-DDTHH:MM:SS -- "
	*/

	protected static String timePrefix()
	{
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		String ret = format.format(new Date()) + " -- ";
		return ret;
	}
}
