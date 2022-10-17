package chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Vector;

public class ChatServerThread extends Thread {

	// �������
	private String nickname;
	private Socket socket;
	
	// Ŭ���̾�Ʈ�� ����� PrintWriter(��Ʈ��, �������� �帧)�� ����(���� �ڿ�)
	private Vector<PrintWriter> printWriterList;
	
	// ������
	// ������ ��ü ���� �� ���ϰ� ��Ʈ��(�帧) ������ ������ �ʿ��ϹǷ� Default ������ ����� �ȵ�
	public ChatServerThread(Socket socket, Vector<PrintWriter> printWriterList) {
		this.nickname = null;
		this.socket = socket;
		this.printWriterList = printWriterList;
	}
	
	// ����Լ�(Getter/Setter, �������̵� ��� X)
	@Override
	public void run() {		
		// 1. ����(���Ͽ��� InputStream, OutputStream�� ����)�� ���� ���ϴ� ������ �������� ��/���
		// 1-1. ������ ���� �б� ����� �ο�(InputStream - InputStreamReader - BufferedReader)
		InputStream inputStream = null;
		InputStreamReader inputStreamReader = null;
		BufferedReader bufferedReader = null;
		
		// 1-2. ������ ���� ��� ����� �ο�(OutputStream - OutputStreamWriter - BufferedWriter)
		OutputStream outputStream = null;
		OutputStreamWriter outputStreamWriter = null;
		PrintWriter printWriter = null; // �ӵ����ٴ� ���� ���ϰ� BufferedWriter�� �ƴ� 'PrintWriter' ���
		
		try {
			inputStream = this.socket.getInputStream();
			/* BufferedReader�� �ӵ� ���� ��Ʈ���̹Ƿ� InputStreamReader���� ���ڼ� ������� ��
			   UTF-8�� 1byte �Ǵ� 3byte�� �а�, UTF-16�� ��� 2byte�� ����(���� �߻��ϹǷ� UTF-8 ���) */
			inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
			bufferedReader = new BufferedReader(inputStreamReader);
			
			outputStream = this.socket.getOutputStream();
			outputStreamWriter = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);
			printWriter = new PrintWriter(outputStreamWriter);
			
			// 2. Ŭ���̾�Ʈ�� �����(��ȭ��) �ְ� ����(���� �ݺ�)
			while(true) {
				
				/* 2-0. ������ ����ġ�� �ͱ��� �� �ν�(= scanner.nextLine()), �Է� ���� ������ ���� ���.
					    ���⼭ ������ ������ ����ó���� �Ѿ */
				String messageReceive = bufferedReader.readLine();
				
				/* 2-1. Ŭ���̾�Ʈ�� ���ڱ� ����(Ŭ���̾�Ʈ ����)�� �������� readLine()�� 'null'�� �о�´�.
				        Ŭ���̾�Ʈ�� ���� �� �� Enter ĥ ������ ���� ��� */
				if (messageReceive == null) {
					System.out.println("Ŭ���̾�Ʈ�� ������ �����߽��ϴ�. " + 
									   "Ŭ���̾�Ʈ IP: " + this.socket.getInetAddress().getHostAddress() + 
									   ", Ŭ���̾�Ʈ ��Ʈ��ȣ: " + this.socket.getPort());
					
					// 1) ������� �����Ƿ� �����ڿ� Vector<PrintWriter> list���� �����Ѵ�.
					synchronized (printWriterList) { // Vector�� 'list.remove(printWriter);'�� �ص� �ǳ� �� �����ϵ��� �ѹ� �� ����ȭ
						printWriterList.remove(printWriter);
					}
					
					// 2) Ŭ���̾�Ʈ�� ������ �������� �ٸ� Ŭ���̾�Ʈ���� �˸� ����
					String messageDisconnect = this.nickname + "���� �����߽��ϴ�.";
					sendMessage(messageDisconnect);
					
					break;
				}
				
				// 2-2. Ŭ���̾�Ʈ�� ��û�Ͽ� ���� �۵��ϴ� ���(�������� - 1. join, 2. message, 3. quit)
				// tokens[0]�� join, message, quit �� �ϳ�
				String[] tokens = messageReceive.split(":");
				
				/* 2-2-1. join(Ŭ���̾�Ʈ�� �޼��� �濡 �����ϱ�) - ����ȭ �Ǿ�����
		          tokens[0]: join, tokens[1]: nickname */
				if("join".equals(tokens[0])) {
					
					// 1) �г��� ����
					this.nickname = tokens[1];
					
					// 2) ���� �����Ϳ� PrintWriter(��Ʈ��) �߰�
					synchronized(printWriterList) {
						printWriterList.add(printWriter); // Ŭ���̾�Ʈ�� �߰��Ǹ� ��Ʈ�� �߰�							
					}
					
					// 3) �޼����� ����
					String messageJoin = this.nickname + "���� �����߽��ϴ�.";
					System.out.println(messageJoin); // ���� ȭ�鿡 ���
					
					// 4) Ŭ���̾�Ʈ�� ���������� �ٸ� Ŭ���̾�Ʈ���� ���� �˸� ����
					sendMessage(messageJoin);
				
				// 2-2-2. meessage(�޼��� �ְ� �ޱ�, ���� ���� ��)	
				} else if ("message".equals(tokens[0])) {
					
					// 1) �ٸ� Ŭ���̾�Ʈ�� �޼��� ����
					String messageSend = this.nickname + ": " + tokens[1];
					sendMessage(messageSend);
				
				// 2-2-3. quit(Ŭ���̾�Ʈ�� ���� ������) - ����ȭ �Ǿ�����
				} else if ("quit".equals(tokens[0])) {
										
					// 1) ������� �����Ƿ� �����ڿ� Vector<PrintWriter> list���� �����Ѵ�.
					synchronized (printWriterList) {
						printWriterList.remove(printWriter);
					}
					
					// 2) Ŭ���̾�Ʈ�� ���������� �ٸ� Ŭ���̾�Ʈ���� ���� �˸� ����
					String messageQuit = this.nickname + "���� �����߽��ϴ�.";
					sendMessage(messageQuit);
					
					break; // while�� ������ ���� �� finally�� ���� socket ����
				} 
			
			} 
			
		} catch (IOException e) {
			System.out.println(this.nickname + "�� ������ �̻��� �־ ����Ǿ����ϴ�.");
			
		} finally {
			if(this.socket != null && !this.socket.isClosed()) {
				try {
					this.socket.close();
				
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			synchronized(printWriterList) {
				printWriterList.remove(printWriter);
			}
			
		}
	
	} // end of run

	private void sendMessage(String message) {
		synchronized(printWriterList) {
			for(PrintWriter pw : printWriterList) {
				pw.println(message);
				pw.flush();
			}						
		}
	}
		
} // end of class
