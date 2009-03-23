package de.cdauth.bwproxy;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;
import java.io.EOFException;

/**
 * The “low-priority” receiver is a thread that limits the bandwidth of all its connections. A connection can be added using the add() method.
 * The total bandwidth used by all added connections is limited to the specified bandwidth (--bwlimit command-line option).
 * @author Candid Dauth
 * @license GPL-3
*/

public class LowPriorityReceiver extends Thread
{
	/**
	 * The added connections.
	*/
	LinkedList<Connection> m_connections = new LinkedList();

	public LowPriorityReceiver()
	{
		super("low priority receiver");
		setPriority(2);
	}

	public void run()
	{
		Logger.debug("Low priority receiver running.");

		int bwlimit = Options.getBWLimit()/100;
		if(bwlimit < 1) bwlimit = 1;

		byte[] buffer = new byte[bwlimit];
		int bwlimit_1,read;
		int last_additional = 0; // If one connection is slower than its allowed maximum speed, the additional bandwidth can be used by other connections during the next run of the while loop below
		InputStream input_stream;
		OutputStream output_stream;

		while(true)
		{
			synchronized(this)
			{
				if(m_connections.size() > 0)
				{
					bwlimit_1 = (last_additional + bwlimit) / m_connections.size();
					if(bwlimit_1 < 1) bwlimit_1 = 1;
					if(bwlimit_1 > bwlimit) bwlimit_1 = bwlimit;
					last_additional = bwlimit / m_connections.size();

					for(Connection c : m_connections)
					{
						try
						{
							if(c.canceled())
								throw new Exception("Connection.canceled() is true.");

							input_stream = c.getProxySocket().getInputStream();
							output_stream = c.getClientSocket().getOutputStream();
							read = input_stream.read(buffer, 0, bwlimit_1);

							if(read == -1)
								throw new EOFException();

							if(read > 0)
							{
								last_additional -= read;
								output_stream.write(buffer, 0, read);
							}
						}
						catch(Exception e)
						{
							Logger.error("One low priority connection aborted. Number is now "+m_connections.size(), e);
							c.cancel();
							m_connections.remove(c);
						}
					}

					if(last_additional < 0)
						last_additional = 0;
				}
				else
					last_additional = 0;
			}
			try { sleep(10); } catch(Exception e) { }
		}
	}

	/**
	 * Adds a connection to the thread.
	*/

	public synchronized void add(Connection a_connection)
	{
		m_connections.add(a_connection);
		Logger.debug("Adding low priority connection. Number ist now "+m_connections.size());
	}
}
