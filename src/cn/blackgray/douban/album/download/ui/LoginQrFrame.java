package cn.blackgray.douban.album.download.ui;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import cn.blackgray.douban.album.download.common.utils.LoginUtils;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class LoginQrFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;

	/**
	 * Create the frame.
	 */
	public LoginQrFrame() {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				closeWindow();
			}
		});
		setTitle("请使用豆瓣APP扫描二维码登录");
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
	}

	
	/**
	 * 关闭窗口
	 */
	private void closeWindow(){
		//取消登录中标识
		LoginUtils.IS_LOGGING_IN = false;
		this.removeAll();
		this.revalidate();
		//销毁本组件
		this.dispose();
	}
	
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					LoginQrFrame frame = new LoginQrFrame();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
}
