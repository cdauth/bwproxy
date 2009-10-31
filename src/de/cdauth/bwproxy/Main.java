package de.cdauth.bwproxy;

/**
 * This application redirects all incoming TCP connnections to another TCP server and tries to limit the bandwidth for big downloads.
 * It starts two threads for each connection, one receiver and one sender. If the receiver thread has received a specified amount of
 * bytes, it is stopped and the connection is added to the “low-priority” receiver thread. That limits the total bandwidth used by all
 * its connections to a specified amount of bytes per second.
 * @author Candid Dauth
 * @license GPL-3
*/

public class Main
{
	/**
	 * The “low-priority” receiver thread.
	*/
	static LowPriorityReceiver sm_lpreceiver;

	/**
	 * The main method of the program. Parses the command-line options and starts the TCP server and the LowPriorityReceiver thread.
	*/

	public static void main(String[] a_args)
		throws Exception
	{
		System.out.println("This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.");
		System.out.println("This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.");
		System.out.println("You should have received a copy of the GNU General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.");
		System.out.println("");

		// Load command-line arguments
		Options.load(a_args);

		// Start server thread
		Server server = new Server();
		server.start();

		// Start low-priority receiver
		sm_lpreceiver = new LowPriorityReceiver();
		sm_lpreceiver.start();
	}

	/**
	 * Returns the started instance of the LowPriorityReceiver.
	*/

	public static LowPriorityReceiver getLPReceiverThread()
	{
		return sm_lpreceiver;
	}
}
