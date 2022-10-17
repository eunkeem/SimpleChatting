package chat;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class ChatClient {
	
	public static final String IP_SERVER = "172.30.1.94";
	public static final int PORT_SERVER = 1126;
	public static final Scanner scanner = new Scanner(System.in);
	
	public static void main(String[] args) {
		
		Socket socket = null;
		
		OutputStream outputStream = null;
		OutputStreamWriter outputStreamWriter = null;
		PrintWriter printWriter = null;
		
		InputStream inputStream = null;
		InputStreamReader inputStreamReader = null;
		BufferedReader bufferedReader = null;
		
		String nickname = null;
			
		try {
			// 1. Ŭ���̾�Ʈ ���� ����
			socket = new Socket();

			// 2. ������ ���� ��û(connect), ���ӵ� ������ ��� �� ���� �ð� ������ ����ó�� ����
			socket.connect(new InetSocketAddress(IP_SERVER, PORT_SERVER));
			System.out.println("[ä�ù濡 �����߽��ϴ�.]"); // ���� ��û �㰡 ��
			
			// 3-1. join
			// 3-1-1. �г����� �����Ѵ�.
			while(true) {
				System.out.print("[�г����� �Է��ϼ���.]: ");
				nickname = scanner.nextLine();
				
				if(nickname.isEmpty() == false) {
					break;
				}
				
				System.out.println("[�г����� ���� ���� �� ���� �̻� �Է��ϼ���.]");
				
			}
			scanner.close();
			
			// 3-1-2. ������ ���� ��� ���� ��Ʈ���� �����´�.(������ PrintWriter�� ����, BufferedReader�� �����Ƿ� �Ȱ��� ����)
			outputStream = socket.getOutputStream();
			outputStreamWriter = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);
			printWriter = new PrintWriter(outputStreamWriter, true); // ,true�� �ָ� '�ڵ� flush'��
			
			inputStream = socket.getInputStream();
			inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
			bufferedReader = new BufferedReader(inputStreamReader);
			
			// 3-1-3. �г����� ������ ������.
			String nicknameConvert = "join:" + nickname + "\r\n"; // join ���� ������ ä��
			printWriter.println(nicknameConvert);
			
			/* 4. UI(AWT) â���� BufferedReader, PrintWriter �������� ��û�ϰ� ������ �ް� ���ѷ��� �ӿ��� ����
		      UIâ���� ���� �ʰ� ��� ���� 4���� ������ �����Ѵ�. */ 
			// ���� ������ �۵��� ���� ��ü ����: �����κ��� �� �����͸� ��� �а�, TextArea�� ����ϴ� ������
			ChatClientWindow chatClientWindow = new ChatClientWindow(nickname, socket);
			// UIâ�� �����ش�.
			chatClientWindow.show();
			
		} catch (IOException e) {
			System.out.println("[���� ���� ������ �߻��߽��ϴ�.]"); // ���� ��û �Ұ� ��
		
		}
			
	}

}
