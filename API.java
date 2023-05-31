import java.io.BufferedReader;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

class API {
	private static ServerSocket serverSocket;

	public static void start(int portNum) throws IOException, InterruptedException {
		serverSocket = new ServerSocket(portNum);
		Scanner scan = new Scanner(System.in);
		System.out.println("starting");
		while (true) {
			System.out.println("scanning more");
			new ConnectionHandler(serverSocket.accept()).start();
			System.out.println("thread is done?");
			// prevent CPU overload
			Thread.sleep(1);
			// if something is typed, finish
			// if(scan.hasNext()) {
			// break;
			// }
		}
		// System.out.println("stopping");

	}

	private static class ConnectionHandler extends Thread {
		private Socket clientSocket;
		private BufferedReader in;
		private PrintWriter out;

		public ConnectionHandler(Socket socket) {
			this.clientSocket = socket;
		}

		public void run() {
			try {
				out = new PrintWriter(clientSocket.getOutputStream(), true);
				in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
				String request;
				while ((request = in.readLine()) != null) {
					System.out.println("request is: " + request);
					Scanner scan = new Scanner(request);
					if (scan.hasNext()) {

						String token = scan.next();
						if (token.equals("login")) {
							System.out.println("Doing login");
							String username = scan.next(), password = scan.next();
							System.out.println("username = " + username + " password = " + password);
							boolean loggedIn = Database.verifyCredentials(username, password);
							out.println(loggedIn);
						} else if (token.equals("createUser")) {
							System.out.println("creating user");
							String username = scan.next(), password = scan.next();
							out.println(Database.createUser(username, password));
						} else if (token.equals("preferences")) {
							System.out.println("Doing preferences");
							out.println(Database.getPreferences(scan.next()));
						}else if (token.equals("setPreferences")) {
							System.out.println("setting preferences ");
							out.println(Database.setPreferences(scan.next(),scan.next()));
						}
						
					} else {
						break;
					}
					scan.close();
				}
				System.out.println("closing");
				in.close();
				out.close();
				clientSocket.close();

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

	public static void main(String[] args) {
		// test the database
		// System.out.println((Database.verifyCredentials("Steiner3","123")));

		try {
			start(6666);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}