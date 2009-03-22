package de.cdauth.cmdargs;

/**
 * This exception is thrown whenever an error in this package occurs,
 * such as the addition of an argument that already exists.
 * @author Candid Dauth
*/

public class ArgumentParserException extends Exception
{
	public ArgumentParserException()
	{
		super();
	}

	public ArgumentParserException(String a_message)
	{
		super(a_message);
	}
}
