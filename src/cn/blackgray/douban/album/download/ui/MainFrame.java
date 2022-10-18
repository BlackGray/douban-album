/*
 * MainFrame.java
 *
 * Created on __DATE__, __TIME__
 */

package cn.blackgray.douban.album.download.ui;

import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import cn.blackgray.douban.album.download.common.Common;
import cn.blackgray.douban.album.download.common.Console;
import cn.blackgray.douban.album.download.common.utils.CommonUtils;
import cn.blackgray.douban.album.download.service.DownloadService;
import cn.blackgray.douban.album.download.service.creator.HtmlCreator;
import cn.blackgray.douban.album.download.ui.component.DropTextArea;

/**
 * 下载器主界面
 */
public class MainFrame extends javax.swing.JFrame {

	private static final long serialVersionUID = 1L;

	/** Creates new form MainFrame */
	private MainFrame() {
		initComponents();
		this.setBounds(350, 100, this.getWidth(), this.getHeight());
		String ext = "";
//		String ext = " FOR 与阿米果.avi";
		this.setTitle("豆瓣相册下载" + Common.VERSION + ext);
		Console.setArea(infoTextArea);
		//进度条设置
		progressBar.setOrientation(JProgressBar.HORIZONTAL);
		progressBar.setMinimum(0);
		progressBar.setStringPainted(true);
		progressBar.setPreferredSize(new Dimension(300, 20));
		progressBar.setBorderPainted(true);
		//progressBar.setBackground(new Color(255, 255, 255));
		albumListProgressBar.setOrientation(JProgressBar.HORIZONTAL);
		albumListProgressBar.setMinimum(0);
		albumListProgressBar.setStringPainted(true);
		albumListProgressBar.setPreferredSize(new Dimension(300, 20));
		albumListProgressBar.setBorderPainted(true);
		//处理单元进度条
		processUnitProgressBar.setOrientation(JProgressBar.HORIZONTAL);
		processUnitProgressBar.setMinimum(0);
		processUnitProgressBar.setStringPainted(true);
		processUnitProgressBar.setPreferredSize(new Dimension(300, 20));
		processUnitProgressBar.setBorderPainted(true);
		//设置默认存储路径
		pathTextField.setText(Common.PATH_DOWNLOAD);
	}

	private static MainFrame instance = null;

	public static MainFrame getInstance() {
		if (instance == null) {
			instance = new MainFrame();
		}
		return instance;
	}

	//GEN-BEGIN:initComponents
	// <editor-fold defaultstate="collapsed" desc="Generated Code">
	private void initComponents() {

		javax.swing.JScrollPane infoScrollPane = new javax.swing.JScrollPane();
		infoTextArea = new javax.swing.JTextArea();
		downloadBtn = new javax.swing.JButton();
		javax.swing.JTabbedPane tabbedPane = new javax.swing.JTabbedPane();
		javax.swing.JScrollPane albumScrollPane = new javax.swing.JScrollPane();
		albumTextArea = new DropTextArea();
		javax.swing.JPanel pathPanel = new javax.swing.JPanel();
		pathTextField = new javax.swing.JTextField();
		changePathBtn = new javax.swing.JButton();
		javax.swing.JLabel pathLabel = new javax.swing.JLabel();
		javax.swing.JLabel isDownloadRawLabel = new javax.swing.JLabel();
		isDownloadRawCheckBox = new javax.swing.JCheckBox();
		progressBar = new javax.swing.JProgressBar();
		javax.swing.JButton jButton1 = new javax.swing.JButton();
		albumListProgressBar = new javax.swing.JProgressBar();
		albumListCountLabel = new javax.swing.JLabel();
		processUnitProgressBar = new javax.swing.JProgressBar();
		processUnitCountLabel = new javax.swing.JLabel();
		javax.swing.JMenuBar jMenuBar1 = new javax.swing.JMenuBar();
		javax.swing.JMenu jMenu1 = new javax.swing.JMenu();
		javax.swing.JMenuItem jMenuItem1 = new javax.swing.JMenuItem();
		javax.swing.JMenuItem jMenuItem4 = new javax.swing.JMenuItem();
		javax.swing.JSeparator jSeparator1 = new javax.swing.JSeparator();
		javax.swing.JMenuItem jMenuItem2 = new javax.swing.JMenuItem();
		javax.swing.JMenu jMenu4 = new javax.swing.JMenu();
		javax.swing.JMenuItem jMenuItem5 = new javax.swing.JMenuItem();

		setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

		infoTextArea.setColumns(20);
		infoTextArea.setRows(5);
		infoScrollPane.setViewportView(infoTextArea);

		downloadBtn.setText("\u4e0b\u8f7d");
		downloadBtn.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				downloadBtnActionPerformed(evt);
			}
		});

		albumTextArea.setColumns(20);
		albumTextArea.setRows(5);
		albumTextArea
				.setToolTipText("\u6309\u7167\u76f8\u518c\u5730\u5740\u4e0b\u8f7d\u6240\u6709\u56fe\u7247~");
		albumScrollPane.setViewportView(albumTextArea);

		tabbedPane.addTab("\u76f8\u518c\u5730\u5740", albumScrollPane);

		changePathBtn.setText("\u4fee\u6539");
		changePathBtn.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				changePathBtnActionPerformed(evt);
			}
		});

		pathLabel.setText("\u4fdd\u5b58\u8def\u5f84\uff1a");

		isDownloadRawLabel.setText("\u4fdd\u5b58\u5927\u56fe\uff1a");

		isDownloadRawCheckBox.setSelected(true);

		javax.swing.GroupLayout pathPanelLayout = new javax.swing.GroupLayout(
				pathPanel);
		pathPanel.setLayout(pathPanelLayout);
		pathPanelLayout
				.setHorizontalGroup(pathPanelLayout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								pathPanelLayout
										.createSequentialGroup()
										.addContainerGap()
										.addGroup(
												pathPanelLayout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.LEADING)
														.addGroup(
																javax.swing.GroupLayout.Alignment.TRAILING,
																pathPanelLayout
																		.createSequentialGroup()
																		.addComponent(
																				pathLabel)
																		.addPreferredGap(
																				javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																		.addComponent(
																				pathTextField,
																				javax.swing.GroupLayout.DEFAULT_SIZE,
																				188,
																				Short.MAX_VALUE)
																		.addPreferredGap(
																				javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																		.addComponent(
																				changePathBtn))
														.addGroup(
																pathPanelLayout
																		.createSequentialGroup()
																		.addComponent(
																				isDownloadRawLabel)
																		.addPreferredGap(
																				javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																		.addComponent(
																				isDownloadRawCheckBox)))
										.addContainerGap()));
		pathPanelLayout
				.setVerticalGroup(pathPanelLayout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								pathPanelLayout
										.createSequentialGroup()
										.addGap(22, 22, 22)
										.addGroup(
												pathPanelLayout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.BASELINE)
														.addComponent(
																changePathBtn)
														.addComponent(pathLabel)
														.addComponent(
																pathTextField,
																javax.swing.GroupLayout.PREFERRED_SIZE,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																javax.swing.GroupLayout.PREFERRED_SIZE))
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addGroup(
												pathPanelLayout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.BASELINE)
														.addComponent(
																isDownloadRawLabel)
														.addComponent(
																isDownloadRawCheckBox))
										.addContainerGap(17, Short.MAX_VALUE)));

		tabbedPane.addTab("\u8bbe\u7f6e", pathPanel);

		jButton1.setIcon(new javax.swing.ImageIcon(
				getClass()
						.getResource(
								"/cn/blackgray/douban/album/download/resources/images/ui/icon-dir.gif"))); // NOI18N
		jButton1.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jButton1ActionPerformed(evt);
			}
		});

		albumListCountLabel.setText("[0/0] ");

		processUnitCountLabel.setText("[0/0] ");

		jMenu1.setText("\u7a0b\u5e8f");

		jMenuItem1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
				java.awt.event.KeyEvent.VK_O,
				java.awt.event.InputEvent.CTRL_MASK));
		jMenuItem1.setText("\u6253\u5f00\u5b58\u50a8\u76ee\u5f55");
		jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jMenuItem1ActionPerformed(evt);
			}
		});
		jMenu1.add(jMenuItem1);

		jMenuItem4.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
				java.awt.event.KeyEvent.VK_H,
				java.awt.event.InputEvent.CTRL_MASK));
		jMenuItem4.setText("\u624b\u52a8\u751f\u6210\u9875\u9762");
		jMenuItem4.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jMenuItem4ActionPerformed(evt);
			}
		});
		jMenu1.add(jMenuItem4);
		jMenu1.add(jSeparator1);

		jMenuItem2.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
				java.awt.event.KeyEvent.VK_E,
				java.awt.event.InputEvent.CTRL_MASK));
		jMenuItem2.setText("\u5173\u95ed\u7a0b\u5e8f");
		jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jMenuItem2ActionPerformed(evt);
			}
		});
		jMenu1.add(jMenuItem2);

		jMenuBar1.add(jMenu1);

		jMenu4.setText("\u5e2e\u52a9\u5e16");

		jMenuItem5
				.setIcon(new javax.swing.ImageIcon(
						getClass()
								.getResource(
										"/cn/blackgray/douban/album/download/resources/images/ui/face.jpg"))); // NOI18N
		jMenuItem5.setText("\u731b\u51fb\u8fd9\u91cc~");
		jMenuItem5.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jMenuItem5ActionPerformed(evt);
			}
		});
		jMenu4.add(jMenuItem5);

		jMenuBar1.add(jMenu4);

		setJMenuBar(jMenuBar1);

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(
				getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(layout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(
						layout.createSequentialGroup()
								.addContainerGap()
								.addGroup(
										layout.createParallelGroup(
												javax.swing.GroupLayout.Alignment.TRAILING)
												.addComponent(
														infoScrollPane,
														javax.swing.GroupLayout.Alignment.LEADING,
														javax.swing.GroupLayout.DEFAULT_SIZE,
														498, Short.MAX_VALUE)
												.addGroup(
														layout.createSequentialGroup()
																.addPreferredGap(
																		javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																.addComponent(
																		tabbedPane,
																		javax.swing.GroupLayout.DEFAULT_SIZE,
																		346,
																		Short.MAX_VALUE)
																.addPreferredGap(
																		javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																.addComponent(
																		downloadBtn,
																		javax.swing.GroupLayout.PREFERRED_SIZE,
																		145,
																		javax.swing.GroupLayout.PREFERRED_SIZE))
												.addGroup(
														layout.createSequentialGroup()
																.addGroup(
																		layout.createParallelGroup(
																				javax.swing.GroupLayout.Alignment.TRAILING)
																				.addComponent(
																						processUnitProgressBar,
																						javax.swing.GroupLayout.DEFAULT_SIZE,
																						447,
																						Short.MAX_VALUE)
																				.addComponent(
																						albumListProgressBar,
																						javax.swing.GroupLayout.DEFAULT_SIZE,
																						447,
																						Short.MAX_VALUE)
																				.addComponent(
																						progressBar,
																						javax.swing.GroupLayout.DEFAULT_SIZE,
																						447,
																						Short.MAX_VALUE))
																.addPreferredGap(
																		javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																.addGroup(
																		layout.createParallelGroup(
																				javax.swing.GroupLayout.Alignment.TRAILING)
																				.addComponent(
																						jButton1,
																						javax.swing.GroupLayout.PREFERRED_SIZE,
																						44,
																						javax.swing.GroupLayout.PREFERRED_SIZE)
																				.addComponent(
																						albumListCountLabel)
																				.addComponent(
																						processUnitCountLabel))))
								.addContainerGap()));
		layout.setVerticalGroup(layout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(
						layout.createSequentialGroup()
								.addGroup(
										layout.createParallelGroup(
												javax.swing.GroupLayout.Alignment.LEADING,
												false)
												.addGroup(
														layout.createSequentialGroup()
																.addContainerGap()
																.addComponent(
																		tabbedPane,
																		javax.swing.GroupLayout.DEFAULT_SIZE,
																		124,
																		Short.MAX_VALUE))
												.addGroup(
														layout.createSequentialGroup()
																.addGap(35, 35,
																		35)
																.addComponent(
																		downloadBtn,
																		javax.swing.GroupLayout.DEFAULT_SIZE,
																		102,
																		Short.MAX_VALUE)))
								.addPreferredGap(
										javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(infoScrollPane,
										javax.swing.GroupLayout.DEFAULT_SIZE,
										184, Short.MAX_VALUE)
								.addPreferredGap(
										javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
								.addGroup(
										layout.createParallelGroup(
												javax.swing.GroupLayout.Alignment.LEADING,
												false)
												.addComponent(
														progressBar,
														javax.swing.GroupLayout.DEFAULT_SIZE,
														javax.swing.GroupLayout.DEFAULT_SIZE,
														Short.MAX_VALUE)
												.addComponent(
														jButton1,
														javax.swing.GroupLayout.DEFAULT_SIZE,
														javax.swing.GroupLayout.DEFAULT_SIZE,
														Short.MAX_VALUE))
								.addPreferredGap(
										javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addGroup(
										layout.createParallelGroup(
												javax.swing.GroupLayout.Alignment.TRAILING,
												false)
												.addGroup(
														layout.createSequentialGroup()
																.addComponent(
																		processUnitProgressBar,
																		javax.swing.GroupLayout.PREFERRED_SIZE,
																		javax.swing.GroupLayout.DEFAULT_SIZE,
																		javax.swing.GroupLayout.PREFERRED_SIZE)
																.addPreferredGap(
																		javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																.addComponent(
																		albumListProgressBar,
																		javax.swing.GroupLayout.PREFERRED_SIZE,
																		javax.swing.GroupLayout.DEFAULT_SIZE,
																		javax.swing.GroupLayout.PREFERRED_SIZE))
												.addGroup(
														layout.createSequentialGroup()
																.addComponent(
																		processUnitCountLabel)
																.addPreferredGap(
																		javax.swing.LayoutStyle.ComponentPlacement.RELATED,
																		javax.swing.GroupLayout.DEFAULT_SIZE,
																		Short.MAX_VALUE)
																.addComponent(
																		albumListCountLabel)))
								.addContainerGap()));

		pack();
	}// </editor-fold>
	//GEN-END:initComponents

	private void jMenuItem5ActionPerformed(java.awt.event.ActionEvent evt) {
		Common.openURLWithBrowse(Common.URL_HELP, this);
	}

	private void jMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				JFileChooser chooser = new JFileChooser(Common.PATH_APP);
				chooser.setDialogTitle("请选择要生成文件的目录");
				chooser.setMultiSelectionEnabled(true);
				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				chooser.showOpenDialog(MainFrame.getInstance());
				File[] dirs = chooser.getSelectedFiles();
				Console.print("开始生成，请骚等~");
				if (dirs.length != 0) {
					try {
						for (File dir : dirs) {
							HtmlCreator.createAlbumHTML(dir.getAbsolutePath());
							Console.print("[Finish]" + dir.getAbsolutePath());
						}
						Console.print("生成完毕");
					} catch (IOException e) {
						Console.print("生成错误");
						Console.print(e.getMessage());
						e.printStackTrace();
					}
				}
			}
		}).start();
	}

	private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {
		openDir();
	}

	private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {
		System.exit(0);
	}

	private void changePathBtnActionPerformed(java.awt.event.ActionEvent evt) {
		JFileChooser chooser = new JFileChooser(Common.PATH_APP);
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		chooser.showSaveDialog(this);
		File dir = chooser.getSelectedFile();
		if (dir != null) {
			Common.PATH_DOWNLOAD = dir.getAbsolutePath();
			pathTextField.setText(Common.PATH_DOWNLOAD);
		}
	}

	private void openDir() {
		Desktop desktop = Desktop.getDesktop();
		try {
			File dir = new File(Common.PATH_DOWNLOAD);
			if (dir.exists()) {
				desktop.open(dir);
			} else {
				JOptionPane.showMessageDialog(this, "暂无图片");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {
		openDir();
	}

	/**
	 * 执行下载动作
	 */
	private void downloadBtnActionPerformed(java.awt.event.ActionEvent evt) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				//下载相册
				download();
			}
		}).start();
	}

	private void download() {
		downloadBtn.setEnabled(false);
		//获取路径
		if (Common.IS_UPDATE) {
			Common.IS_UPDATE = false;
		} else {
			Common.PATH_DOWNLOAD = pathTextField.getText();
		}
		File file = new File(Common.PATH_DOWNLOAD);
		if (!file.exists()) {
			file.mkdirs();
		}
		//获取相册地址
		String[] urls = albumTextArea.getText().split("[\\t \\n]+");
		List<String> urlList = new ArrayList<String>();
		boolean flag = true;
		for (int i = 0; i < urls.length; i++) {
			String url = urls[i];
			if (!url.startsWith("http://") && !url.startsWith("https://")) {
				JOptionPane.showMessageDialog(MainFrame.getInstance(),
						"地址格式错误，请检查后重新输入");
				downloadBtn.setEnabled(true);
				flag = false;
				break;
			} else {
				//如果是豆瓣相册，去除尾部分页信息
				if (url.indexOf("douban.com") != -1) {
					if (url.indexOf("?start=") != -1) {
						url = url.substring(0, url.indexOf("?start="));
					}
					if (url.indexOf("?m_start=") != -1) {
						url = url.substring(0, url.indexOf("?m_start="));
					}
				}
				//尾部添加“/”
				if (url.indexOf("?") != -1 || url.substring(url.length() - 1).equals("/")) {
					urlList.add(url);
				} else {
					urlList.add(url + "/");
				}
				System.out.println(url);
			}
		}
		//地址无误，执行下载
		if (flag) {
			//是否下载原始大图
			Common.IS_DOWNLOAD_RAW = isDownloadRawCheckBox.isSelected();
			//启动下载
			DownloadService.download(urlList);
		}

	}

	/**
	 * @param args the command line arguments
	 */
	public static void main(String args[]) {
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					for (LookAndFeelInfo info : UIManager
							.getInstalledLookAndFeels()) {
						if ("Nimbus".equals(info.getName())) {
							UIManager.setLookAndFeel(info.getClassName());
							break;
						}
					}
				} catch (Exception e) {
					JOptionPane.showMessageDialog(null,
							"未找到新皮肤，请升级JDK到6.0 update 10");
				}
				
				MainFrame frame = MainFrame.getInstance();
				
				//设置macos下快捷键
				if (CommonUtils.isMacOS()) {
					JTextArea albumTextArea = frame.albumTextArea;
					int MASK = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
					albumTextArea.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_A, MASK), "select-all");
					albumTextArea.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_C, MASK), "copy");
					albumTextArea.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_X, MASK), "cut");
					albumTextArea.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_V, MASK), "paste");
				}
				
				frame.setVisible(true);
				
				//设置透明度
				//AWTUtilities.setWindowOpacity(MainFrame.getInstance(), 0.70f);
			}
		});
		
//		//检查是否有最新版本，如果有，提示下载
//		if (VersionChecker.haveNewVersion()) {
//			int result = JOptionPane.showConfirmDialog(MainFrame.getInstance(),
//					"有新版本，是否立即查看？", "版本提示", JOptionPane.YES_NO_OPTION,
//					JOptionPane.QUESTION_MESSAGE);
//			if (result == JOptionPane.YES_OPTION) {
//				Common.openURLWithBrowse(Common.URL_HELP,
//						MainFrame.getInstance());
//			}
//		}
		
	}

	//GEN-BEGIN:variables
	// Variables declaration - do not modify
	public javax.swing.JLabel albumListCountLabel;
	public javax.swing.JProgressBar albumListProgressBar;
	javax.swing.JTextArea albumTextArea;
	javax.swing.JButton changePathBtn;
	public javax.swing.JButton downloadBtn;
	javax.swing.JTextArea infoTextArea;
	javax.swing.JCheckBox isDownloadRawCheckBox;
	javax.swing.JTextField pathTextField;
	public javax.swing.JLabel processUnitCountLabel;
	public javax.swing.JProgressBar processUnitProgressBar;
	public javax.swing.JProgressBar progressBar;
	// End of variables declaration//GEN-END:variables

}