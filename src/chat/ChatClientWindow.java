package chat;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.Frame;
import java.awt.Panel;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class ChatClientWindow {

	// �������
    private String name;
    
    // UI�� ���õ� Ŭ����
    private Frame frame; // ��ü Ʋ
    private Panel pannel; // �ϴ� �κ�
    private Button buttonSend; // �޼��� ������ ��ư
    private TextField textField; // ä�� �Է��ϴ� â
    private TextArea textArea; // ä�� ���̴� â
    private Socket socket;

    // ������
    public ChatClientWindow(String name, Socket socket) {
        this.name = name;
        frame = new Frame(name);
        pannel = new Panel();
        buttonSend = new Button("Send");
        textField = new TextField();
        textArea = new TextArea(30, 80);
        this.socket = socket;

        /* ������ �۵�
        1. ���� Ŭ����, 'UI���� ���� ���̴±���'��� ����)
        2. �������� ������ �����͸� �о TextArea�� ������ִ� ������ */
        new ChatClientReceiveThread(socket).start();
    }

    public void show() {
    	
        // Button(��ư ��ü�� ����Ŵ)
        buttonSend.setBackground(Color.GRAY); // ���
        buttonSend.setForeground(Color.BLACK); // �۾�, ��Ʈ ������� Default ����
        buttonSend.addActionListener( new ActionListener() { // �̺�Ʈ ó��
            @Override
            public void actionPerformed( ActionEvent actionEvent ) {
                sendMessage();
            }
        });
        
        /* ���ٽ����� �ϸ�,
        buttonSend.addActionListener( (ActionEvent actionEvent) -> {
                sendMessage();
            }
        ); */

        // Textfield(ä�� �Է��ϴ� â)
        textField.setColumns(80); // �Է�â ���̰� '80����'��� ��
        textField.addKeyListener( new KeyAdapter() { // �̺�Ʈ ó��, KeyAdapter�� Ű���带 �� ������ �̺�Ʈ �߻�
            public void keyReleased(KeyEvent e) {
                char keyCode = e.getKeyChar();
                if (keyCode == KeyEvent.VK_ENTER) { // ���� ġ�� �������� ����, ���� ġ�� sendMessage()�� �޼��� ������
                    sendMessage();
                }
            }
        });

        // Pannel(�ϴ� �κп� ���η� textField, buttonSend �߰�(default�� ����))
        pannel.setBackground(Color.LIGHT_GRAY); // �ǳ� ��� �� ���� ȸ��
        pannel.add(textField); // �ǳ� �ӿ� textField �߰�
        pannel.add(buttonSend); // �ǳ� �ӿ� buttonSend �߰�
        frame.add(BorderLayout.SOUTH, pannel); // �ǳ��� ������ ���� ��ġ�� �߰�

        // TextArea(ä�� ���̴� â)
        textArea.setEditable(false); // �۾� �� ���� ��(editable - false = ���� �Ұ���)
        frame.add(BorderLayout.CENTER, textArea); // textArea�� ������ �߾ӿ� �߰�

        // Frame(�߿�)
        frame.addWindowListener(new WindowAdapter() { // �̺�Ʈ ó��
            public void windowClosing(WindowEvent e) { // '�����찡 ������~'�̶�� �̺�Ʈ(������ �߻�)
                PrintWriter pw;
                try {
                    pw = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);
                    String request = "quit\r\n";
                    pw.println(request);
                    System.exit(0); // Ŭ���̾�Ʈ ��ü�� ����
                }
                catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });
        frame.setVisible(true); // �������� ������
        frame.pack(); // ������ ����� �°� ���� ��ҵ�
    }

    // ������ �����ϴ� �޼ҵ�
    private void sendMessage() {
        PrintWriter pw;
        try {
        	
        	/* �Ʒ� ������ �ѹ��� �� ��
        	OutputStream outputStream = socket.getOutputStream();
    		OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);
    		PrintWriter printWriter = new PrintWriter(outputStreamWriter, true); // ,true�� �ָ� '�ڵ� flush'��
    		*/        	
            pw = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);
            String message = textField.getText() + " "; // ���鵵 ���������� '+ " "' ���ش�.
            String request = "message:" + message + "\r\n"; // �������� 'message'
            pw.println(request);

            textField.setText(""); // ������ Send�ϸ� ������
            textField.requestFocus(); // ������ Ŀ�� ���� �տ��� ���� �Ÿ�
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    // �ν��Ͻ� ���� Ŭ����(������(���� �۵� ��), 4�� ���)
    private class ChatClientReceiveThread extends Thread{
        Socket socket = null;

        ChatClientReceiveThread(Socket socket){
            this.socket = socket;
        }

        // ���� �ָ� BufferedReader�� �о TextArea�� �Ѹ���.
        public void run() {
            try {
            	
            	/* �Ʒ� ������ �ѹ��� �� ��
            	InputStream inputStream = socket.getInputStream();
    			InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
    			bufferedReader bufferedReader = new BufferedReader(inputStreamReader);
    			*/
                BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
                
                while(true) {
                    String msg = br.readLine();
                    textArea.append(msg);
                    textArea.append("\n");
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
