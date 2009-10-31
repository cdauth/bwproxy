package de.cdauth.bwproxy;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * The server thread that listens on the specified port and starts new threads for each new connection. It opens a new TCP connection
 * to the proxy for each connection.
 * @author Candid Dauth
 * @license GPL-3
*/

public class Server extends Thread
{
	protected ServerSocket m_listen_socket;
	protected ThreadGroup m_threadgroup;

	public Server()
	{
		super("server");
		setPriority(5);
	}

	public void run()
	{
		try
		{
			Logger.debug("Starting server socket on port "+Options.getPort());
			m_listen_socket = new ServerSocket(Options.getPort());
			Logger.debug("Server is listening on port "+m_listen_socket.getLocalPort());

			m_threadgroup = new ThreadGroup("clients");
			m_threadgroup.setMaxPriority(3);

			while(true) {
				try
				{
					Logger.debug("Accepting new connection.");
					Socket client_socket = m_listen_socket.accept();
					Connection c = new Connection(client_socket, m_threadgroup);
				}
				catch(Exception e)
				{
					Logger.error("Accepting connection failed.", e);
				}
			}
		}
		catch(Exception e)
		{
			Logger.fatal("Server failed.", e);
		}
	}
}
