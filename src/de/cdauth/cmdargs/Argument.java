package de.cdauth.cmdargs;

/**
 * Instances of this class represent a command line argument.
 * @author Candid Dauth
*/

public class Argument
{
	/** The short name of this argument (p.e. h for -h) */
	private char m_short;
	/** The long name of this argument (p.e. help for --help) */
	private String m_long;

	/** The argument may not be passed with a parameter. */
	public final static int PARAMETER_NONE = 1;
	/** The argument may have a parameter passed. */
	public final static int PARAMETER_OPTIONAL = 2;
	/** The argument must have a parameter passed. */
	public final static int PARAMETER_REQUIRED = 3;

	/** Default: the argument must not have any parameter */
	protected int m_parameter = PARAMETER_NONE;
	/** A description for the usage output. */
	protected String m_description = null;
	/** Does the argument have to be passed? (Useful only with
	PARAMETER_REQUIRED) */
	protected boolean m_required = false;

	/** How often was the argument set? (Useful for multiple verbosity
	levels (-vv) */
	protected int m_set = 0;
	/** Contains a parameter that was passed with the argument. */
	protected String m_passed_parameter = null;

	/**
	 * @param a_short The short form of the argument (for example
	 * 'h' for -h option)
	 * @param a_long The long version of the argument (for example
	 * "help" for --help option)
	 * @author Candid Dauth
	*/

	public Argument(char a_short, String a_long)
	{
		m_short = a_short;
		m_long = a_long;
	}

	/**
	 * Returns the short form of the argument. (For example
	 * 'h' for -h option)
	 * @author Candid Dauth
	*/
	public char shortName()
	{
		return m_short;
	}

	/**
	 * Returns the long form of the argument. (For example
	 * "help" for --help option)
	 * @author Candid Dauth
	*/
	public String longName()
	{
		return m_long;
	}

	/**
	 * Defines the description, which is displayed in the
	 * usage description.
	 * @param a_description The description of the argument's effects.
	 * @author Candid Dauth
	*/
	public void setDescription(String a_description)
	{
		m_description = a_description;
	}

	/**
	 * Defines if the option is required to be passed. (Only
	 * useful with PARAMETER_REQUIRED)
	 * @param a_required Is the option required?
	 * @author Candid Dauth
	*/
	public void setRequired(boolean a_required)
	{
		m_required = a_required;
	}

	/**
	 * Defines if a parameter can/must/must not be passed to the
	 * option. A parameter to the option -h/--help could be
	 * passed as -hparam, -h param, --help param, or
	 * --help=param.
	 * @param a_parameter A constant out of PARAMETER_NONE,
	 * PARAMETER_OPTIONAL, and PARAMETER_REQUIRED.
	 * @author Candid Dauth
	*/
	public void setParameter(int a_parameter)
		throws IllegalArgumentException
	{
		if(a_parameter != PARAMETER_NONE && a_parameter != PARAMETER_OPTIONAL && a_parameter != PARAMETER_REQUIRED)
			throw new IllegalArgumentException("Has to be either PARAMTER_NONE or PARAMETER_OPTIONAL or PARAMETER_REQUIRED.");

		m_parameter = a_parameter;
	}

	/**
		Returns how often the argument was passed. Useful for
		multiple verbosity levels (-vv).
		@author Candid Dauth
	*/
	public int set()
	{
		return m_set;
	}

	/**
	 * Returns the parameter that might have been passed to the
	 * argument.
	 * @return The passed parameter or null if no parameter was
	 * passed.
	*/
	public String parameter()
	{
		return m_passed_parameter;
	}
}
