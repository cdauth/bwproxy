package de.cdauth.bwproxy;

import de.cdauth.cmdargs.Argument;
import de.cdauth.cmdargs.ArgumentParser;
import de.cdauth.cmdargs.ArgumentParserException;
import java.net.InetSocketAddress;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * This static class manages all passed command line options.
 * @author Candid Dauth
 * @license GPL-3
*/

public class Options
{
	static protected ArgumentParser sm_arguments = null;
	static protected Argument sm_listen_token = null;
	static protected Argument sm_proxy_token = null;
	static protected Argument sm_max_traffic_token = null;
	static protected Argument sm_bwlimit_token = null;

	static
	{
		try
		{
			sm_arguments = new ArgumentParser("bwproxy");

			sm_listen_token = new Argument('l', "listen");
			sm_listen_token.setDescription("The port on which this program should listen.");
			sm_listen_token.setParameter(Argument.PARAMETER_REQUIRED);
			sm_listen_token.setRequired(true);
			sm_arguments.addArgument(sm_listen_token);

			sm_proxy_token = new Argument('p', "proxy");
			sm_proxy_token.setDescription("The proxy server to be used (hostname:port).");
			sm_proxy_token.setParameter(Argument.PARAMETER_REQUIRED);
			if(System.getenv("http_proxy") == null || System.getenv("http_proxy").equals(""))
				sm_proxy_token.setRequired(true);
			sm_arguments.addArgument(sm_proxy_token);

			sm_max_traffic_token = new Argument('t', "traffic-limit");
			sm_max_traffic_token.setDescription("If one connection has exceeded this number of bytes, the bandwidth limit is applied.");
			sm_max_traffic_token.setParameter(Argument.PARAMETER_REQUIRED);
			sm_arguments.addArgument(sm_max_traffic_token);

			sm_bwlimit_token = new Argument('b', "bwlimit");
			sm_bwlimit_token.setDescription("The bandwidth limit in bytes per second (min 100).");
			sm_bwlimit_token.setParameter(Argument.PARAMETER_REQUIRED);
			sm_bwlimit_token.setRequired(true);
			sm_arguments.addArgument(sm_bwlimit_token);
		}
		catch(ArgumentParserException e){}
	}

	static protected int sm_listen = 0;
	static protected InetSocketAddress sm_proxy = null;
	static protected int sm_max_traffic = 0;
	static protected int sm_bwlimit = 0;

	/**
	 * Reads the passed command line options and saves them into variables.
	 * @author Candid Dauth
	*/

	public static void load(String[] a_args)
		throws Exception
	{
		sm_arguments.parseArguments(a_args);

		sm_listen = Integer.parseInt(sm_listen_token.parameter());

		String proxy = sm_proxy_token.parameter();
		if(proxy == null)
			proxy = System.getenv("http_proxy");
		Matcher proxy_matcher1 = Pattern.compile("^\\[(.+)]:(\\d+)$").matcher(proxy);
		if(proxy_matcher1.matches())
			sm_proxy = new InetSocketAddress(proxy_matcher1.group(1), Integer.parseInt(proxy_matcher1.group(2)));
		else
		{
			Matcher proxy_matcher2 = Pattern.compile("^(.+):(\\d+)$").matcher(proxy);
			if(proxy_matcher2.matches())
				sm_proxy = new InetSocketAddress(proxy_matcher2.group(1), Integer.parseInt(proxy_matcher2.group(2)));
			else
				throw new IllegalArgumentException("Invalid proxy setting.");
		}

		if(sm_max_traffic_token.parameter() != null)
			sm_max_traffic = Integer.parseInt(sm_max_traffic_token.parameter());
		sm_bwlimit = Integer.parseInt(sm_bwlimit_token.parameter());
	}

	public static int getPort()
	{
		return sm_listen;
	}

	public static InetSocketAddress getProxy()
	{
		return sm_proxy;
	}

	public static int getMaxTraffic()
	{
		return sm_max_traffic;
	}

	public static int getBWLimit()
	{
		return sm_bwlimit;
	}
}
