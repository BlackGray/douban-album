package cn.blackgray.douban.album.download.ui.component;

/**
 * @author songyinghao
 * @email haoyingsong@foxmail.com
 * @desc   All Rights Reserved
 * @license LGPL
 */
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;

/**
 * 右键菜单文本域
 * @author BlackGray
 */
public class JPopupTextArea extends JTextArea implements MouseListener,
ActionListener {

	private static final long serialVersionUID = -406608462064697359L;
	private JPopupMenu popupMenu = null;
	private JMenuItem cutMenu = null, copyMenu = null, pasteMenu = null,
	selectAllMenu = null,formatMenu = null;

	public JPopupTextArea() {
		super();
		popupMenu = new JPopupMenu();
		cutMenu = new JMenuItem("剪切");
		copyMenu = new JMenuItem("复制");
		pasteMenu = new JMenuItem("粘贴");
		selectAllMenu = new JMenuItem("全选");
		formatMenu = new JMenuItem("格式化");

		cutMenu.setAccelerator(KeyStroke.getKeyStroke('X', InputEvent.CTRL_MASK));
		copyMenu.setAccelerator(KeyStroke.getKeyStroke('C',InputEvent.CTRL_MASK));
		pasteMenu.setAccelerator(KeyStroke.getKeyStroke('V',InputEvent.CTRL_MASK));
		selectAllMenu.setAccelerator(KeyStroke.getKeyStroke('A',InputEvent.CTRL_MASK));
		formatMenu.setAccelerator(KeyStroke.getKeyStroke('F',InputEvent.CTRL_MASK));
		

		cutMenu.addActionListener(this);
		copyMenu.addActionListener(this);
		pasteMenu.addActionListener(this);
		selectAllMenu.addActionListener(this);
		formatMenu.addActionListener(this);
		

		popupMenu.add(cutMenu);
		popupMenu.add(copyMenu);
		popupMenu.add(pasteMenu);
		popupMenu.add(new JSeparator());
		popupMenu.add(selectAllMenu);
		popupMenu.add(new JSeparator());
		popupMenu.add(formatMenu);

		this.add(popupMenu);
		this.addMouseListener(this);

	}

	public void actionPerformed(ActionEvent e) {

		if (e.getSource() == copyMenu) {
			this.copy();
		}
		if (e.getSource() == pasteMenu) {
			this.paste();
		}
		if (e.getSource() == cutMenu) {
			this.cut();
		}
		if (e.getSource() == selectAllMenu) {
			this.selectAll();
		}
		if (e.getSource() == formatMenu) {
			this.setText(this.getText().replaceAll("[^\r\n]http://", "/\r\nhttp://").trim());
			this.setText(this.getText().replaceAll("[^\r\n]https://", "/\r\nhttps://").trim());
		}

	}

	public void mousePressed(MouseEvent e) {
		popupMenuTrigger(e);
	}

	public void mouseReleased(MouseEvent e) {
		popupMenuTrigger(e);
	}

	public void mouseClicked(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	private void popupMenuTrigger(MouseEvent e) {
		if (e.isPopupTrigger()) {
			this.requestFocusInWindow();
			cutMenu.setEnabled(isAbleToCopyAndCut());
			copyMenu.setEnabled(isAbleToCopyAndCut());
			pasteMenu.setEnabled(isAbleToPaste());
			selectAllMenu.setEnabled(isAbleToSelectAll());
			popupMenu.show(this, e.getX() + 3, e.getY() + 3);
		}
	}

	private boolean isAbleToSelectAll() {
		return !("".equalsIgnoreCase(this.getText()) || (null == this.getText()));
	}

	private boolean isAbleToCopyAndCut() {
		return (this.getSelectionStart() != this.getSelectionEnd());
	}

	private boolean isAbleToPaste() {
		Transferable content = this.getToolkit().getSystemClipboard().getContents(this);
		try {
			return (content.getTransferData(DataFlavor.stringFlavor) instanceof String);
		} catch (UnsupportedFlavorException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

}