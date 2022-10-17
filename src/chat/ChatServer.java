package chat;

import java.io.IOException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.util.Vector;

public class ChatServer {
	
	// 0-2. ���� ��Ʈ ��ȣ ����(1024~65535)
	public static final int PORT = 1126;
	
	public static void main(String[] args) {
		
		String ip = null;
		
		// 0-1. ���� IP(��ſ�) Ȯ��(InetAddress)
		try {
			ip = InetAddress.getLocalHost().getHostAddress();
		
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		ServerSocket serverSocket = null;
		
		/* ���� Ŭ���̾�Ʈ���� ���� ��Ʈ��(�帧)�� ����Ʈȭ�Ͽ� ����
		   �� ��Ʈ��(�帧)�� Ŭ���̾�Ʈ�� �������� ����ϴ� ���� �ڿ�(�÷��� ������ ��ũ) */
		/* PrintWriter: Writer�� ����Ͽ� Writer ��� �پ��� ��� ��� ����
		   (print/println/printf ��, Ư�� println()(���� ����)���� ����� ������ readLine()(���� �ձ��� ����)�� ������ ��)
		   Ŭ���̾�Ʈ�� 2byte ������ ���(���� ��� ��� ��Ʈ���� �����)  */
		Vector<PrintWriter> printWriterList = new Vector<>();
		
		try {
			// 1. ���� ���� ����
			serverSocket = new ServerSocket();
			
			// 2. ���� ���� ���ε�
			serverSocket.bind(new InetSocketAddress(ip, PORT));
			System.out.println("[������ ��ٸ��ϴ�.] " + Thread.currentThread().getId() + "�� IP: " + ip + ", " + 
			                                        Thread.currentThread().getId() + "�� ��Ʈ��ȣ: " + PORT);
			/* Thread.currentThread().getName(): 'main' ����
			   Thread.currentThread().getId(): '1' ���� */
			
			// 3. Ŭ���̾�Ʈ�κ��� ���� ��û �ޱ�(���� �ݺ�)
			while(true) {
				// 3-1. Ŭ���̾�Ʈ ���� ��û ��� �� ���� �� ��ſ� ���� ����(��� ����)
				Socket socket = serverSocket.accept(); 	
				
				// 3-2. (���ÿ� ���� ����� ��ȭ�ϱ� ����) Ŭ���̾�Ʈ���� ����� ������� ����(����, ��Ʈ��(�帧) Ȱ��)
				ChatServerThread chatServerThread = new ChatServerThread(socket, printWriterList);
				chatServerThread.start();
				
				// 3-3. Ŭ���̾�Ʈ�� ���� �ּ�(IP �ּҿ� ��Ʈ ��ȣ) Ȯ��
				SocketAddress socketAddress = socket.getRemoteSocketAddress();
				InetSocketAddress inetSocketAddress = (InetSocketAddress) socketAddress;
				System.out.println("[������ �����մϴ�.] " + "Ŭ���̾�Ʈ�� IP: " + inetSocketAddress.getHostString() + 
						                                ", Ŭ���̾�Ʈ�� ��Ʈ��ȣ: " + inetSocketAddress.getPort());
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		
		} finally {
			// 4. (�۵� ���̸�) ���� ���� �ݱ�
			try {
				if(serverSocket != null && !serverSocket.isClosed()) {
					serverSocket.close();
				} 
			
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			
			}
			
		}

	}

}