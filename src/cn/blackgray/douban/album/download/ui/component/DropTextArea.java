package cn.blackgray.douban.album.download.ui.component;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import javax.swing.JOptionPane;

import cn.blackgray.douban.album.download.common.Common;
import cn.blackgray.douban.album.download.ui.MainFrame;

/**
 * 拖放文本域控件
 * @author BlackGray
 */
public class DropTextArea extends JPopupTextArea implements DropTargetListener{

	private static final long serialVersionUID = 1L;

	public DropTextArea() {
		new DropTarget(this, DnDConstants.ACTION_COPY_OR_MOVE, this);
	}

	@Override
	public void dragEnter(DropTargetDragEvent dtde) {

	}

	@Override
	public void dragOver(DropTargetDragEvent dtde) {

	}

	@Override
	public void dropActionChanged(DropTargetDragEvent dtde) {

	}

	@Override
	public void dragExit(DropTargetEvent dte) {

	}

	@Override
	public void drop(DropTargetDropEvent dtde) {
		try {
			// Transferable tr = dtde.getTransferable();
			if (dtde.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
				this.setText("");
				dtde.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
				@SuppressWarnings("unchecked")
				List<File> list = (List<File>) dtde.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
				for (File file : list) {
					if (file.isDirectory()) {
						//获取目录地址
						File dir = new File(file.getAbsolutePath());
						if (dir.isDirectory()) {
							File doc = new File(dir.getAbsolutePath() + "/描述.txt");
							if (doc.exists()) {
								try {
									BufferedReader br = new BufferedReader(new FileReader(doc));
									String str = br.readLine();
									br.close();
									if (str != null && str.length() != 0) {
										if (str.split(" ").length >= 2) {
											//暂时只支持单个相册更新
											this.setText(str.split(" ")[1]);
											Common.PATH_DOWNLOAD = dir.getParent();
											Common.IS_UPDATE = true;
										}
									}
								} catch (FileNotFoundException e) {
									e.printStackTrace();
								} catch (IOException e) {
									e.printStackTrace();
								}
							} else {
								JOptionPane.showMessageDialog(MainFrame.getInstance(),"所需要的描述文档不存在，无法更新，T^T");
							}
						}
					}
				}
				dtde.dropComplete(true);
				this.updateUI();
			} else {
//				dtde.rejectDrop();
				dtde.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
				String str = (String) dtde.getTransferable().getTransferData(DataFlavor.stringFlavor);
				this.append(str + "\n");
				dtde.dropComplete(true);
				this.updateUI();
			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} catch (UnsupportedFlavorException ufe) {
			ufe.printStackTrace();
		}
	}

}
