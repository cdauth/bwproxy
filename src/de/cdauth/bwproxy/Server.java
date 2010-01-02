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
	private ServerSocket m_listenSocket;
	private ThreadGroup m_clientGroup;

	public Server() throws IOException
	{
		super("server");
		setPriority(5);
		
		Logger.debug("Starting server socket on port "+Options.getPort());
		m_listenSocket = new ServerSocket(Options.getPort());
		Logger.debug("Server is listening on port "+m_listenSocket.getLocalPort());

		m_clientGroup = new ThreadGroup("clients");
		m_clientGroup.setMaxPriority(3);
	}

	public void run()
	{
		while(true) {
			try
			{
				Logger.debug("Accepting new connection.");
				Socket client_socket = m_listenSocket.accept();
				new Connection(client_socket, m_clientGroup);
			}
			catch(Exception e)
			{
				Logger.error("Accepting connection failed.", e);
			}
		}
	}
}
