package de.cdauth.cmdargs;

import java.io.PrintStream;

import java.util.Collection;
import java.util.Hashtable;
import java.util.Vector;

/**
 * This class provides an interface to parse command line arguments.
 * @author Candid Dauth
*/

public class ArgumentParser
{
	private Hashtable<Character,Argument> m_arguments_short;
	private Hashtable<String,Argument> m_arguments_long;

	private String m_progname;

	/**
	 * @param a_progname The name/path of the program using the
	 * ArgumentParser. Used in the usage message.
	 * @author Candid Dauth
	*/
	public ArgumentParser(String a_progname)
	{
		m_arguments_short = new Hashtable<Character,Argument>();
		m_arguments_long = new Hashtable<String,Argument>();
		m_progname = a_progname;
	}

	/**
	 * This method adds a new possible command line argument.
	 * @param a_argument An Argument instance that contains all
	 * settings about this argument.
	 * @author Candid Dauth
	*/

	public synchronized void addArgument(Argument a_argument)
		throws ArgumentParserException
	{
		if(m_arguments_short.containsKey(a_argument.shortName()) || a_argument.shortName() == 'h')
			throw new ArgumentParserException("This short name is already defined.");
		if(m_arguments_long.containsKey(a_argument.longName()) || a_argument.longName().equals("help"))
			throw new ArgumentParserException("This long name is already defined.");

		m_arguments_short.put(a_argument.shortName(), a_argument);
		m_arguments_long.put(a_argument.longName(), a_argument);
	}

	/**
	 * This method parses the command line arguments, stores any
	 * passed parameters into the Argument objects, and quits
	 * the program in case of wrong arguments.
	 * @param a_args The argument array passed by the JVM to the main
	 * function.
	 * @author Candid Dauth
	*/

	public synchronized String[] parseArguments(String[] a_args)
	{
		boolean argument_expected = true;
		Vector<String> remaining_arguments = new Vector<String>();
		Argument argument = null;
		boolean error = false;
		boolean help = false;

		// Walk through each command line argument and
		// evaluate it.
		g_arguments: for(int i=0; i<a_args.length; i++)
		{
			if(argument_expected && a_args[i].length() > 1 && a_args[i].charAt(0) == '-')
			{ // This is an option (beginning with -) and not a normal parameter.
				if(a_args[i].equals("--")) // -- sets that no further options follow
					argument_expected = false;
				else
				{
					if(a_args[i].startsWith("--"))
					{ // The argument is passed in long form
						String arg_name;
						int ioe = a_args[i].indexOf('=');
						if(ioe != -1)
							arg_name = a_args[i].substring(2, ioe);
						else
							arg_name = a_args[i].substring(2);
						argument = m_arguments_long.get(arg_name);
						if(argument == null)
						{
							if(arg_name.equals("help"))
								help = true;
							else
								System.err.println("Unknown option --"+arg_name+".");
							error = true;
							break g_arguments;
						}

						argument.m_set++;
						switch(argument.m_parameter)
						{
							case Argument.PARAMETER_REQUIRED:
								if(ioe != -1) // Parameter form: --opt=param
									argument.m_passed_parameter = a_args[i].substring(ioe+1);
								else if(i+1 < a_args.length) // Parameter form: --opt param
									argument.m_passed_parameter = a_args[++i];
								else
								{
									System.err.println("Parameter required for option --"+arg_name+".");
									error = true;
									break g_arguments;
								}
								break;
							case Argument.PARAMETER_OPTIONAL:
								if(ioe != -1) // Parameter form: --opt=param
									argument.m_passed_parameter = a_args[i].substring(ioe+1);
								else if(i+1 < a_args.length && (a_args[i+1].length() <= 1 || a_args[i+1].charAt(0) != '-')) // Parameter form: --opt param
									argument.m_passed_parameter = a_args[++i];
								break;
						}
					}
					else
					{ // The argument is passed in short form
						char arg_name = a_args[i].charAt(1);
						argument = m_arguments_short.get(arg_name);
						if(argument == null)
						{
							if(arg_name == 'h')
								help = true;
							else
								System.err.println("Unknown option -"+arg_name+".");
							error = true;
							break g_arguments;
						}

						argument.m_set++;
						switch(argument.m_parameter)
						{
							case Argument.PARAMETER_REQUIRED:
								if(a_args[i].length() > 2) // parameter form: -o param
									argument.m_passed_parameter = a_args[i].substring(2);
								else if(i+1 < a_args.length) // parameter form: -oparam
									argument.m_passed_parameter = a_args[++i];
								else
								{
									System.err.println("Parameter required for option -"+arg_name+".");
									error = true;
									break g_arguments;
								}
								break;
							case Argument.PARAMETER_OPTIONAL:
								if(a_args[i].length() > 2) // parameter form: -o param
									argument.m_passed_parameter = a_args[i].substring(2);
								else if(i+1 < a_args.length && (a_args[i+1].length() <= 1 || a_args[i+1].charAt(0) != '-')) // parameter form: -oparam
									argument.m_passed_parameter = a_args[++i];
								break;
							default:
								// Multiple options can be passed at once, for example -h -v as -hv
								for(int j=2; j<a_args[i].length(); j++)
								{
									arg_name = a_args[i].charAt(j);
									argument = m_arguments_short.get(arg_name);
									if(argument == null)
									{
										if(arg_name == 'h')
											help = true;
										else
											System.err.println("Unknown option -"+arg_name+".");
										error = true;
										break g_arguments;
									}
									argument.m_set++;
									if(argument.m_parameter == Argument.PARAMETER_REQUIRED)
									{
										System.err.println("Parameter required for option -"+arg_name+".");
										error = true;
										break g_arguments;
									}
								}
								break;
						}
					}
				}
			}
			else // Normal command line parameter
				remaining_arguments.add(a_args[i]);
		}

		if(!error)
		{ // Check if all required arguments have been passed
			Collection<Argument> all_arguments = m_arguments_short.values();
			for(Argument arg : all_arguments)
			{
				if(arg.m_required && arg.m_set <= 0)
				{
					System.err.println("Option --"+arg.longName()+" is required.");
					error = true;
					break;
				}
			}
		}

		if(error)
		{
			if(help)
			{
				printHelp(System.out);
				System.exit(0);
			}
			else
			{
				System.err.println();
				System.err.println("Use --help for a list of options.");
				System.exit(1);
			}
		}

		return remaining_arguments.toArray(new String[remaining_arguments.size()]);
	}

	/**
	 * Prints out a usage message containing all options added
	 * using AddArgument().
	 * @param a_out The stream where the help should be printed,
	 * for example System.err.
	 * @author Candid Dauth
	*/

	public void printHelp(PrintStream a_out)
	{
		int col_len = 0;
		Collection<Argument> all_arguments = m_arguments_short.values();
		for(Argument arg : all_arguments)
		{
			int this_len = arg.longName().length();
			switch(arg.m_parameter)
			{
				case Argument.PARAMETER_OPTIONAL:
					this_len += 2;
				case Argument.PARAMETER_REQUIRED:
					this_len += 4;
			}
			if(this_len > col_len)
				col_len = this_len;
		}
		col_len += 11;

		StringBuffer line = new StringBuffer();
		line.append("Usage: "+m_progname);
		boolean other_options = false;
		for(Argument arg : all_arguments)
		{
			if(arg.m_required)
			{
				line.append(" -"+arg.shortName());
				switch(arg.m_parameter)
				{
					case Argument.PARAMETER_REQUIRED:
						line.append(" <PARAM>");
						break;
					case Argument.PARAMETER_OPTIONAL:
						line.append(" [PARAM]");
						break;
				}
			}
			else
				other_options = true;
		}
		if(other_options)
			line.append(" [OPTIONS] ...");
		a_out.println(line.toString());

		if(all_arguments.size() > 0)
		{
			a_out.println();
			a_out.println("List of options:");

			for(Argument arg : all_arguments)
			{
				line = new StringBuffer();
				line.append(" ");
				line.append("-"+arg.shortName()+", --"+arg.longName());
				switch(arg.m_parameter)
				{
					case Argument.PARAMETER_REQUIRED:
						line.append("=PARAM");
						break;
					case Argument.PARAMETER_OPTIONAL:
						line.append("[=PARAM]");
						break;
				}
				for(int i=line.length(); i<col_len; i++)
					line.append(" ");
				if(arg.m_description != null)
					line.append(arg.m_description);
				a_out.println(line.toString());
			}
		}
	}

	public void printHelp()
	{
		printHelp(System.err);
	}
}
