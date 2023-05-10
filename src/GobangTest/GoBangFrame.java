package GobangTest;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class GoBangFrame extends JFrame {
	LeftPanel leftPanel;

	//文本框
	public JTextArea textArea;
	//模式
	public JRadioButton MODE_NORMAL;//人人模式
	public JRadioButton MODE_AUTO;//人机模式
	//智能
	public JRadioButton INTEL;//估值函数
	public JRadioButton INTEL_TREE;//估值函数+搜索树
	//搜索树
	public JComboBox<Integer> depth;//搜索树深度
	public JComboBox<Integer> node;//搜索树每层的节点
	//其他
	public JCheckBox order;//显示顺序
	public JCheckBox tips;//显示提示
	public JButton regret;//悔棋
	public JButton newGame;
	//人机模式
	public JRadioButton humanFirst;
	public JRadioButton machineFirst;

	// 启动游戏的入口
	public void start() {
		// 设置窗口
		setSize(GoBangConst.WINDOW_WIDTH, GoBangConst.WINDOW_HEIGHT);
		setLocation(500, 200);
		setTitle("五子棋");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// 添加左侧棋盘
		leftPanel = new LeftPanel();
		add(leftPanel, BorderLayout.WEST);

		// 添加右侧棋盘
		JPanel rightPanel = new JPanel();
		rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));

		// 1.添加文本框
		JPanel panel1 = new JPanel();
		panel1.setLayout(new BoxLayout(panel1, BoxLayout.X_AXIS));
		panel1.setBorder(new TitledBorder("单击鼠标右键，查看各个估值"));
		// textArea1_1 = new JTextArea();
		textArea = new JTextArea(10, 10);
		textArea.setEditable(false);
		textArea.setLineWrap(true);

		JScrollPane jScrollPane = new JScrollPane(textArea);
		Dimension size = jScrollPane.getPreferredSize(); // 获得文本域的首选大小
		jScrollPane.setBounds(110, 90, size.width, size.height);
		// textArea1_1.add(jScrollBar);
		panel1.add(textArea);
		panel1.add(jScrollPane);
		rightPanel.add(panel1);

		// 2.模式
		JPanel panel2 = new JPanel();
		panel2.setBorder(new TitledBorder("模式"));
		MODE_NORMAL = new JRadioButton("人人模式");
		MODE_AUTO = new JRadioButton("人机模式");
		MODE_NORMAL.setSelected(true);
		ButtonGroup buttonGroup2_1 = new ButtonGroup();
		buttonGroup2_1.add(MODE_NORMAL);
		buttonGroup2_1.add(MODE_AUTO);
		panel2.add(MODE_NORMAL);
		panel2.add(MODE_AUTO);
		rightPanel.add(panel2);

		// 3.智能
		JPanel panel3 = new JPanel();
		panel3.setBorder(new TitledBorder("智能"));
		INTEL = new JRadioButton("估值函数");
		INTEL_TREE = new JRadioButton("估值函数+搜索树");
		INTEL.setSelected(true);
		ButtonGroup buttonGroup3_1 = new ButtonGroup();// 添加进组使之互异
		buttonGroup3_1.add(INTEL);
		buttonGroup3_1.add(INTEL_TREE);
		panel3.add(INTEL);
		panel3.add(INTEL_TREE);
		rightPanel.add(panel3);

		// 4.搜索树
		JPanel panel4 = new JPanel();
		panel4.setBorder(new TitledBorder("搜索树"));
		JLabel label4_1 = new JLabel("搜索深度:");
		JLabel label4_2 = new JLabel("每层节点:");
		depth = new JComboBox<>(new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10});
		node = new JComboBox<>(new Integer[]{5, 6, 7, 8, 9, 10, 11, 12, 13});
		panel4.add(label4_1);
		panel4.add(depth);
		panel4.add(label4_2);
		panel4.add(node);
		rightPanel.add(panel4);

		// 5.其他
		JPanel panel5 = new JPanel();
		panel5.setBorder(new TitledBorder("其他"));
		panel5.setLayout(new BoxLayout(panel5, BoxLayout.X_AXIS));
		order = new JCheckBox("显示顺序");
		tips = new JCheckBox("提示");
		regret = new JButton("悔棋");
		newGame = new JButton("新游戏");
		// checkBox5_1.setSelected(true);
		panel5.add(order);
		panel5.add(tips);
		panel5.add(regret);
		panel5.add(newGame);
		rightPanel.add(panel5);

		// 6.人机模式
		JPanel panel6 = new JPanel();
		panel6.setBorder(new TitledBorder("人机模式"));
		humanFirst = new JRadioButton("人类先手");
		machineFirst = new JRadioButton("电脑先手");
		humanFirst.setSelected(true);
		ButtonGroup buttonGroup6_1 = new ButtonGroup();// 添加进组使之互异
		buttonGroup6_1.add(humanFirst);
		buttonGroup6_1.add(machineFirst);
		panel6.add(humanFirst);
		panel6.add(machineFirst);
		rightPanel.add(panel6);

		// 添加监听器
		MODE_NORMAL.addMouseListener(mouseListener);
		MODE_AUTO.addMouseListener(mouseListener);
		INTEL.addMouseListener(mouseListener);
		INTEL_TREE.addMouseListener(mouseListener);
		depth.addMouseListener(mouseListener);
		node.addMouseListener(mouseListener);
		order.addMouseListener(mouseListener);
		tips.addMouseListener(mouseListener);
		regret.addMouseListener(mouseListener);
		newGame.addMouseListener(mouseListener);// 注意添加监听器
		humanFirst.addMouseListener(mouseListener);
		machineFirst.addMouseListener(mouseListener);
		// 将右边框加入界面窗口,并设置窗口可见
		add(rightPanel);
		setVisible(true);
	}

	MouseListener mouseListener = new MouseListener() {

		@Override
		public void mouseClicked(MouseEvent e) {
			Object object = e.getSource();
			if (object == MODE_NORMAL)
				System.out.println("人人模式");
			else if (object == MODE_AUTO)
				System.out.println("人机模式");
			else if (object == INTEL)
				System.out.println("估值函数");
			else if (object == INTEL_TREE)
				System.out.println("估值函数+搜索树");
			else if (object == regret)
				leftPanel.set_Regret();
			else if (object == order)
				// System.out.println("显示顺序"+order.isSelected());
				leftPanel.Set_ShowNumber(order.isSelected());
			else if (object == tips)
				leftPanel.setShowTips(tips.isSelected());
			else if (object == newGame)
				leftPanel.New_Game(MODE_NORMAL.isSelected(), INTEL.isSelected(),
						(int) depth.getSelectedItem(), (int) node.getSelectedItem(), order.isSelected(),
						humanFirst.isSelected(), textArea, tips.isSelected());
			else if (object == humanFirst)
				System.out.println("人机先手");
			else if (object == machineFirst)
				System.out.println("电脑先手");
			leftPanel.requestFocus(true);
		}

		@Override
		public void mouseReleased(MouseEvent e) {

		}

		@Override
		public void mousePressed(MouseEvent e) {

		}

		@Override
		public void mouseExited(MouseEvent e) {

		}

		@Override
		public void mouseEntered(MouseEvent e) {

		}

	};
}
