package GobangTest;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class GobangFrame extends JFrame {
	LeftPanel leftPanel;

	// 1:文本框
	public JTextArea textArea1_1;
	// 2：模式
	public JRadioButton mode;// true为人人，false为人机
	public JRadioButton jRadioButton2_2;
	// 3：智能
	public JRadioButton intel;// true为估值函数，false为估值函数+搜索树
	public JRadioButton jRadioButton3_2;
	// 4:搜索树
	public JComboBox<Integer> depth;
	public JComboBox<Integer> node;
	// 5:其他
	public JCheckBox order;
	public JCheckBox tips;
	public JButton regret;
	public JButton new_Game;
	// 6:人机模式
	public JRadioButton humanFirst;
	public JRadioButton jRadioButton6_2;
	// 7:播放背景音乐
	public MusicTest music;
	public JButton music_start;
	public JButton music_stop;

	// 启动游戏的入口
	public void start() {
		// 设置窗口
		setSize(GobangConst.WINDOW_WIDTH, GobangConst.WINDOW_HEIGHT);
		setLocation(500, 200);
		setTitle("五子棋");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// 添加左侧棋盘
		leftPanel = new LeftPanel();
		add(leftPanel, BorderLayout.WEST);

		// 添加右侧棋盘
		JPanel rightPanel = new JPanel();
		rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));

		// 1：添加文本框
		JPanel panel1 = new JPanel();
		panel1.setLayout(new BoxLayout(panel1, BoxLayout.X_AXIS));
		panel1.setBorder(new TitledBorder("单击鼠标右键，查看各个估值"));
		// textArea1_1 = new JTextArea();
		textArea1_1 = new JTextArea(10, 10);
		textArea1_1.setEditable(false);
		textArea1_1.setLineWrap(true);

		JScrollPane jScrollPane = new JScrollPane(textArea1_1);
		Dimension size = jScrollPane.getPreferredSize(); // 获得文本域的首选大小
		jScrollPane.setBounds(110, 90, size.width, size.height);
		// textArea1_1.add(jScrollBar);
		panel1.add(textArea1_1);
		panel1.add(jScrollPane);
		rightPanel.add(panel1);

		// 2:模式
		JPanel panel2 = new JPanel();
		panel2.setBorder(new TitledBorder("模式"));
		mode = new JRadioButton("人人模式");
		jRadioButton2_2 = new JRadioButton("人机模式");
		mode.setSelected(true);
		ButtonGroup buttonGroup2_1 = new ButtonGroup();
		buttonGroup2_1.add(mode);
		buttonGroup2_1.add(jRadioButton2_2);
		panel2.add(mode);
		panel2.add(jRadioButton2_2);
		rightPanel.add(panel2);

		// 3：智能
		JPanel panel3 = new JPanel();
		panel3.setBorder(new TitledBorder("智能"));
		intel = new JRadioButton("估值函数");
		jRadioButton3_2 = new JRadioButton("估值函数+搜索树");
		intel.setSelected(true);
		ButtonGroup buttonGroup3_1 = new ButtonGroup();// 添加进组使之互异
		buttonGroup3_1.add(intel);
		buttonGroup3_1.add(jRadioButton3_2);
		panel3.add(intel);
		panel3.add(jRadioButton3_2);
		rightPanel.add(panel3);

		// 4:搜索树
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

		// 5:其他
		JPanel panel5 = new JPanel();
		panel5.setBorder(new TitledBorder("其他"));
		panel5.setLayout(new BoxLayout(panel5, BoxLayout.X_AXIS));
		order = new JCheckBox("显示顺序");
		tips = new JCheckBox("提示");
		regret = new JButton("悔棋");
		new_Game = new JButton("新游戏");
		// checkBox5_1.setSelected(true);
		panel5.add(order);
		panel5.add(tips);
		panel5.add(regret);
		panel5.add(new_Game);
		rightPanel.add(panel5);

		// 6:人机模式
		JPanel panel6 = new JPanel();
		panel6.setBorder(new TitledBorder("人机模式"));
		humanFirst = new JRadioButton("人类先手");
		jRadioButton6_2 = new JRadioButton("电脑先手");
		humanFirst.setSelected(true);
		ButtonGroup buttonGroup6_1 = new ButtonGroup();// 添加进组使之互异
		buttonGroup6_1.add(humanFirst);
		buttonGroup6_1.add(jRadioButton6_2);
		panel6.add(humanFirst);
		panel6.add(jRadioButton6_2);
		rightPanel.add(panel6);

		// 7:播放音乐
		JPanel panel7 = new JPanel();
		panel7.setBorder(new TitledBorder("BGM"));
		music_start = new JButton("播放");
		music_stop = new JButton("关闭");
		panel7.add(music_start);
		panel7.add(music_stop);
		rightPanel.add(panel7);

		// 添加监听器
		mode.addMouseListener(mouseListener);
		jRadioButton2_2.addMouseListener(mouseListener);
		intel.addMouseListener(mouseListener);
		jRadioButton3_2.addMouseListener(mouseListener);
		depth.addMouseListener(mouseListener);
		node.addMouseListener(mouseListener);
		order.addMouseListener(mouseListener);
		tips.addMouseListener(mouseListener);
		regret.addMouseListener(mouseListener);
		new_Game.addMouseListener(mouseListener);// 注意添加监听器
		humanFirst.addMouseListener(mouseListener);
		jRadioButton6_2.addMouseListener(mouseListener);
		music_start.addMouseListener(mouseListener);
		music_stop.addMouseListener(mouseListener);
		// 将右边框加入界面窗口,并设置窗口可见
		add(rightPanel);
		setVisible(true);
	}

	MouseListener mouseListener = new MouseListener() {

		@Override
		public void mouseClicked(MouseEvent e) {
			Object object = e.getSource();
			if (object == mode)
				System.out.println("人人模式");
			else if (object == jRadioButton2_2)
				System.out.println("人机模式");
			else if (object == intel)
				System.out.println("估值函数");
			else if (object == jRadioButton3_2)
				System.out.println("估值函数+搜索树");
			else if (object == regret)
				leftPanel.set_Regret();
			else if (object == order)
				// System.out.println("显示顺序"+order.isSelected());
				leftPanel.Set_ShowNumber(order.isSelected());
			else if (object == tips)
				leftPanel.Set_Showtips(tips.isSelected());
			else if (object == new_Game)
				leftPanel.New_Game(mode.isSelected(), intel.isSelected(),
						(int) depth.getSelectedItem(), (int) node.getSelectedItem(), order.isSelected(),
						humanFirst.isSelected(), textArea1_1, tips.isSelected());
			else if (object == humanFirst)
				System.out.println("人机先手");
			else if (object == jRadioButton6_2)
				System.out.println("电脑先手");
			else if (object == music_start)
				play_Music();
			else if (object == music_stop)
				stop_music();
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

	protected void play_Music() {
		music = new MusicTest();
		MusicTest.play_Music();
	}

	protected void stop_music() {
		MusicTest.Stop_music();
	}
}
