package GobangTest;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LeftPanel extends JPanel {
    // 当前下棋的用户
    int currentPlayer = 1;//(后面用3-currentPlayer表示另外一个用户)
    private Chess chessByTree;//记录极大极小算法的值

    private boolean mode;//ture为人人对战，false为人机对战
    private boolean intel;//true为估值函数，false为估值函数+搜索树
    private int depth;//搜索深度
    private int node;//每层节点数
    private boolean isShowNumber;//是否显示数字
    // 是否人类先手
    private boolean isOver;//判断游戏是否结束
    private boolean isDogFall;//判断是否平局
    private boolean isEmpty;
    private int count = 0;//记录下棋次数
    private JTextArea textArea;
    private boolean isDrawTip;//是否展示提示
    private Chess bestChess;//推荐最好的棋子

    // 用于存放坐标,范围为[0,0]到[0,14]与[14,0]到[14,14]
    private int x, y;
    //创建棋盘中棋子的数组
    private final Chess[][] chess = new Chess[GoBangConst.LINE_NUMBER][GoBangConst.LINE_NUMBER];

    // 函数构造(构造器)
    public LeftPanel() {
        setPreferredSize(new Dimension(GoBangConst.GAME_WIDTH, GoBangConst.GAME_HEIGHT));
        setBackground(Color.ORANGE);
        setFocusable(true);//键盘监听要焦点
        isShowNumber = false;
        isDrawTip = false;
        isOver = true;
        textArea = new JTextArea();
        textArea.setEditable(true);
        //添加监听器监听鼠标移动
        addMouseMotionListener(mouseMotionListener);
        //添加监听器监听鼠标事件
        addMouseListener(mouseListener);
        //添加监听器监听键盘
        addKeyListener(keyListener);
        //初始化二维数组
        for (int i = 0; i < chess.length; i++) {
            for (int j = 0; j < chess[i].length; j++) {
                chess[i][j] = new Chess(i, j, currentPlayer, i * chess.length + j);
                currentPlayer = 3 - currentPlayer;
            }
        }
    }

    // 鼠标监听器监听鼠标移动
    MouseMotionListener mouseMotionListener = new MouseMotionListener() {
        @Override
        public void mouseMoved(MouseEvent e) {
            // 获取当前鼠标坐标值
            int xi = e.getX();
            int yi = e.getY();
            // 判断边界
            if (xi > 0 && xi < GoBangConst.LINE_NUMBER * GoBangConst.LINE_DISTANCE + GoBangConst.OFFSET / 2 && yi > 0
                    && yi < GoBangConst.LINE_NUMBER * GoBangConst.LINE_DISTANCE + GoBangConst.OFFSET / 2) {
                x = (xi - GoBangConst.OFFSET / 2) / GoBangConst.LINE_DISTANCE;
                y = (yi - GoBangConst.OFFSET / 2) / GoBangConst.LINE_DISTANCE;
                // 鼠标移动时刷新
                repaint();
            }
        }

        @Override
        public void mouseDragged(MouseEvent e) {

        }
    };

    // 鼠标监听器监听鼠标点击
    MouseListener mouseListener = new MouseListener() {
        @Override
        public void mouseClicked(MouseEvent e) {
            // 游戏结束注意退出
            if (isOver) {
                JOptionPane.showMessageDialog(LeftPanel.this, "游戏已结束，请重新开始新游戏！");
                return;
            }
            // 游戏正常进行
            int xi = e.getX();
            int yi = e.getY();

            // 判断边界
            if (xi > 0 && xi < GoBangConst.LINE_NUMBER * GoBangConst.LINE_DISTANCE + GoBangConst.OFFSET / 2 && yi > 0
                    && yi < GoBangConst.LINE_NUMBER * GoBangConst.LINE_DISTANCE + GoBangConst.OFFSET / 2) {
                x = (xi - GoBangConst.OFFSET / 2) / GoBangConst.LINE_DISTANCE;
                y = (yi - GoBangConst.OFFSET / 2) / GoBangConst.LINE_DISTANCE;
                if (e.getButton() == MouseEvent.BUTTON1)//左键按下
                {
                    if (chess[x][y].getPlayer() == GoBangConst.EMPTY) {
                        // 人类下战
                        chess[x][y] = new Chess(x, y, currentPlayer, count);
                        currentPlayer = 3 - currentPlayer;
                        count++;
                        isEmpty = false;//已经下棋，棋盘非空
                        boolean is_win;
                        //把最后一步棋下完形成5个连珠再判断结束
                        repaint();
                        //判断是否分出胜负
                        is_win = CheckWin(chess[x][y]);
                        isDogFall = Check_DogFall(chess);

                        //电脑下棋
                        if (!mode && !is_win && !isDogFall) {
                            if (intel) {
                                //估值函数
                                //机器下棋
                                List<Chess> list = get_SortedList(currentPlayer);
                                Chess chessBean = list.get(0);
                                chessBean.setOrder(count);
                                count++;
                                chessBean.setPlayer(currentPlayer);
                                currentPlayer = 3 - currentPlayer;
                                chess[chessBean.getX()][chessBean.getY()] = chessBean;
                                CheckWin(chessBean);
                                repaint();
                            } else {
                                //估值函数+搜索树
                                getByTree2(0, currentPlayer, chess, -Integer.MAX_VALUE, Integer.MAX_VALUE);
                                Chess chessBean = chessByTree;
                                chessBean.setOrder(count);
                                count++;
                                chessBean.setPlayer(currentPlayer);
                                currentPlayer = 3 - currentPlayer;
                                chess[chessBean.getX()][chessBean.getY()] = chessBean;
                                CheckWin(chessBean);
                            }
                        }
                    }
                } else if (e.getButton() == MouseEvent.BUTTON3)//鼠标右键按下获取估值
                {
                    Chess chessman = chess[x][y];
                    int offense = get_Value(chessman, currentPlayer);
                    int defense = get_Value(chessman, 3 - currentPlayer);
                    if (chessman.getPlayer() == GoBangConst.EMPTY)//位置为空可以估值
                    {
                        chessman.getBuffer().setLength(0);
                        chessman.getBuffer().append("(").append(chessman.getX()).append(",").append(chessman.getY()).append(")  ").append("  进攻:").append(offense).append(" 防御:").append(defense).append(" 总分:").append(offense + defense).append("\n");
                        textArea.append(chessman.getBuffer().toString());
                    } else// 位置不空不可估值
                    {
                        chessman.getBuffer().setLength(0);
                        chessman.getBuffer().append("位置为空不可估值\n");
                        textArea.append(chessman.getBuffer().toString());
                    }
                }
            }
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

    // 键盘监听器
    KeyListener keyListener = new KeyListener() {
        @Override
        public void keyPressed(KeyEvent e) {
            // 游戏结束注意退出
            if (isOver) {
                JOptionPane.showMessageDialog(LeftPanel.this, "游戏已结束，请重新开始新游戏！");
                return;
            }
            if (x >= 0 && x < GoBangConst.LINE_NUMBER && y >= 0 && y < GoBangConst.LINE_NUMBER) {
                // 特别注意在边界上要退出！！
                if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_W) {
                    if (y == 0)
                        return;
                    --y;
                    repaint();
                } else if (e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_S) {
                    if (y == GoBangConst.LINE_NUMBER - 1)
                        return;
                    ++y;
                    repaint();
                } else if (e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_A) {
                    if (x == 0)
                        return;
                    --x;
                    repaint();
                } else if (e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_D) {
                    if (x == GoBangConst.LINE_NUMBER - 1)
                        return;
                    ++x;
                    repaint();
                } else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    if (chess[x][y].getPlayer() == GoBangConst.EMPTY) {
                        // 人类下战
                        chess[x][y] = new Chess(x, y, currentPlayer, count);
                        currentPlayer = 3 - currentPlayer;
                        count++;
                        isEmpty = false;// 已经下棋，棋盘非空
                        boolean is_win;
                        // 把最后一步棋下完形成5个连珠再判断结束
                        repaint();
                        // 判断是否分出胜负
                        is_win = CheckWin(chess[x][y]);
                        isDogFall = Check_DogFall(chess);

                        // 电脑下棋
                        if (!mode && !is_win && !isDogFall) {
                            if (intel) {
                                // 估值函数
                                // 机器下棋
                                List<Chess> list = get_SortedList(currentPlayer);
                                Chess chessBean = list.get(0);
                                chessBean.setOrder(count);
                                count++;
                                chessBean.setPlayer(currentPlayer);
                                currentPlayer = 3 - currentPlayer;
                                chess[chessBean.getX()][chessBean.getY()] = chessBean;
                                CheckWin(chessBean);
                                repaint();
                            } else {
                                // 估值函数+搜索树
                                long startTime = System.currentTimeMillis(); // 获取开始时间
                                getByTree2(0, currentPlayer, chess, -Integer.MAX_VALUE, Integer.MAX_VALUE);
                                //Chess chessBean = getByTree(0, currentPlayer, chess);
                                Chess chessBean = chessByTree;
                                long endTime = System.currentTimeMillis(); // 获取结束时间
                                System.out.println("(估值函数+搜索树)程序运行时间：" + (endTime - startTime) + "ms"); // 输出程序运行时间
                                chessBean.setOrder(count);
                                count++;
                                chessBean.setPlayer(currentPlayer);
                                currentPlayer = 3 - currentPlayer;
                                chess[chessBean.getX()][chessBean.getY()] = chessBean;
                                CheckWin(chessBean);
                            }
                        }
                    }
                }
            }
            if (e.getKeyCode() == KeyEvent.VK_SPACE && isDrawTip) {
                x = bestChess.getX();
                y = bestChess.getY();

                repaint();// 刷新
            }
        }

        @Override
        public void keyTyped(KeyEvent e) {

        }

        @Override
        public void keyReleased(KeyEvent e) {

        }
    };

    // 极大极小搜索算法
    public int getByTree2(int Depth, int currentPlayer, Chess[][] chess, int alpha, int beta) {
        // 克隆当前棋盘
        Chess[][] chessCopy = clone(chess);

        // 获取克隆棋盘中空位置的排序后链表
        List<Chess> list = get_SortedList(currentPlayer, chessCopy);

        // 开始搜索
        if (LeftPanel.this.depth == Depth)// 到达最大搜索深度，停止搜索
            return list.get(0).getSum();
        else {
            for (int i = 0; i < node; i++) {
                Chess chess2 = list.get(i);
                int score;
                // 冲四必杀先选择
                if (chess2.getSum() > Level.ALIVE_4.score)
                    score = chess2.getSum();
                    //非必杀继续搜索
                else {
                    // 先下一部最优棋，再让对手下棋进行模拟
                    chessCopy[chess2.getX()][chess2.getY()].setPlayer(currentPlayer);
                    score = getByTree2(Depth + 1, 3 - currentPlayer, chessCopy, alpha, beta);
                }

                // 根据层数返回极大极小值
                if (Depth % 2 == 0) {// MAX层
                    if (score > alpha) {
                        alpha = score;
                        // 递归结束条件
                        if (Depth == 0)
                            chessByTree = chess2;
                    }

                    if (alpha >= beta) {
                        // 剪枝
                        score = alpha;
                        return score;
                    }
                } else {// MIN层
                    // 由于起始层是MAX层，所以再MIN层不存在递归结束
                    if (score < beta)
                        beta = score;
                    if (alpha >= beta) {
                        // 剪枝
                        score = beta;
                        return score;
                    }
                }
            }
            return (Depth % 2 == 0 ? alpha : beta);
        }
    }

    // 获取克隆棋盘(真正棋盘在全局变量中)的排序后链表
    public List<Chess> get_SortedList(int currentPlayer, Chess[][] chess) {
        List<Chess> list = new ArrayList<>();
        for (Chess[] value : chess) {
            for (Chess chessBean : value) {
                // 下面注释为搜索15*15个棋子，使用的是判断棋子是否需要估值再进行估值减少计算量
                if (chessBean.getPlayer() == GoBangConst.EMPTY && is_NeedValued(chessBean)) {
                    // 获取该位置的各个方向总得分
                    int offense = get_Value(chessBean, currentPlayer);// 进攻时获取自己得分
                    int defence = get_Value(chessBean, 3 - currentPlayer);// 防守时获取对方得分
                    chessBean.setOffence(offense);
                    chessBean.setDefence(defence);
                    chessBean.setSum(offense + defence);
                    list.add(chessBean);
                }
            }
        }
        // 对链表进行排序
        Collections.sort(list);
        return list;
    }

    // 克隆当前棋盘用于搜索
    private Chess[][] clone(Chess[][] chess) {
        Chess[][] chessCopy = new Chess[GoBangConst.LINE_NUMBER][GoBangConst.LINE_NUMBER];
        for (int i = 0; i < chessCopy.length; i++)
            for (int j = 0; j < chessCopy[i].length; j++) {
                Chess chessBean = chess[i][j];
                chessCopy[i][j] = new Chess(chessBean.getX(), chessBean.getY(), chessBean.getPlayer(),
                        chessBean.getOrder());
            }
        return chessCopy;
    }

    // 判断是否需要进行估值，距离太远的位置不算(分值肯定很低)
    private boolean is_NeedValued(Chess chessBean) {
        // 注意边界问题
        int xi = chessBean.getX() - 3;
        //int yi = chessBean.getY() - 3;
        int yi;
        if (chessBean.getPlayer() == GoBangConst.EMPTY) {
            for (int i = 0; i < 5; i++) {
                ++xi;
                yi = chessBean.getY() - 3;// 这个很重要！！！
                if (xi < 0 || xi > GoBangConst.LINE_NUMBER - 1)
                    continue;
                for (int j = 0; j < 5; j++) {
                    ++yi;
                    if (yi < 0 || yi > GoBangConst.LINE_NUMBER - 1)
                        continue;
                    if (chess[xi][yi].getPlayer() != GoBangConst.EMPTY) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    // 获取计算过得分后并且排序后的空位置列表
    public List<Chess> get_SortedList(int currentPlayer) {
        List<Chess> list = new ArrayList<>();
        if (isEmpty) {
            bestChess = new Chess(GoBangConst.LINE_NUMBER / 2, GoBangConst.LINE_NUMBER / 2, 0, 0);
            list.add(bestChess);
            return list;
        }
        for (Chess[] item : chess) {
            for (Chess chessBean : item) {
                // 下面注释为搜索15*15个棋子，使用的是判断棋子是否需要估值再进行估值减少计算量
                if (chessBean.getPlayer() == GoBangConst.EMPTY && is_NeedValued(chessBean)) {
                    // 获取该位置的各个方向总得分
                    int offense = get_Value(chessBean, currentPlayer);// 进攻时获取自己得分
                    int defence = get_Value(chessBean, 3 - currentPlayer);// 防守时获取对方得分
                    chessBean.setOffence(offense);
                    chessBean.setDefence(defence);
                    chessBean.setSum(offense + defence);
                    list.add(chessBean);
                }
            }
        }
        // 对链表进行排序
        Collections.sort(list);
        int maxScore = list.get(0).getSum();
        List<Chess> bestList = new ArrayList<>();
        for (Chess value : list) {
            if (value.getSum() == maxScore)
                bestList.add(value);
            else
                break;
        }
        Collections.shuffle(bestList);
        // return list;
        return bestList;
    }

    // 获取该位置的各个方向总得分
    private int get_Value(Chess chessBean, int currentPlayer) {
        Level level1 = get_Level(chessBean, currentPlayer, Layout.Direction.LEFT_TO_RIGHT);
        Level level2 = get_Level(chessBean, currentPlayer, Layout.Direction.UP_TO_BUTTON);
        Level level3 = get_Level(chessBean, currentPlayer, Layout.Direction.NORTHWEST_TO_SOUTHEAST);
        Level level4 = get_Level(chessBean, currentPlayer, Layout.Direction.SOUTHWEST_TO_NORTHEAST);
        return get_ValueByLevel(level1, level2, level3, level4) + Layout.position[chessBean.getX()][chessBean.getY()];
    }

    // 通过四个方向的棋型判断得分
    private int get_ValueByLevel(Level level1, Level level2, Level level3, Level level4) {
        int[] levelCount = new int[Level.values().length];
        // 数组初始化
        for (int i = 0; i < Level.values().length; i++)
            levelCount[i] = 0;

        levelCount[level1.index]++;
        levelCount[level2.index]++;
        levelCount[level3.index]++;
        levelCount[level4.index]++;

        int score = 0;
        if (levelCount[Level.GO_4.index] >= 2
                || levelCount[Level.GO_4.index] >= 1 && levelCount[Level.ALIVE_3.index] >= 1)// 双活4，冲4活三
            score = 10000;
        else if (levelCount[Level.ALIVE_3.index] >= 2)// 双活3
            score = 5000;
        else if (levelCount[Level.SLEEP_3.index] >= 1 && levelCount[Level.ALIVE_3.index] >= 1)// 活3眠3
            score = 1000;
        else if (levelCount[Level.ALIVE_2.index] >= 2)// 双活2
            score = 100;
        else if (levelCount[Level.SLEEP_2.index] >= 1 && levelCount[Level.ALIVE_2.index] >= 1)// 活2眠2
            score = 10;
        score = Math.max(score, Math.max(Math.max(level1.score, level2.score), Math.max(level3.score, level4.score)));
        return score;
    }

    // 根据方向获取该棋子的Level
    private Level get_Level(Chess chessBean, int currentPlayer, Layout.Direction direction) {
        String left, right;
        if (direction == Layout.Direction.LEFT_TO_RIGHT) {
            left = get_StringByDirection(chessBean, -1, 0);
            right = get_StringByDirection(chessBean, 1, 0);
        } else if (direction == Layout.Direction.UP_TO_BUTTON) {
            left = get_StringByDirection(chessBean, 0, 1);
            right = get_StringByDirection(chessBean, 0, -1);
        } else if (direction == Layout.Direction.NORTHWEST_TO_SOUTHEAST) {
            left = get_StringByDirection(chessBean, 1, 1);
            right = get_StringByDirection(chessBean, -1, -1);
        } else {
            left = get_StringByDirection(chessBean, -1, 1);
            right = get_StringByDirection(chessBean, 1, -1);
        }
        String str = left + currentPlayer + right;// 正向字符串
        String str_res = new StringBuffer(str).reverse().toString();// 反向字符串

        for (Level level : Level.values()) {
            Pattern pattern = Pattern.compile(level.regex[currentPlayer - 1]);
            Matcher matcher1 = pattern.matcher(str);
            boolean b1 = matcher1.find();
            Matcher matcher2 = pattern.matcher(str_res);
            boolean b2 = matcher2.find();
            if (b1 || b2)
                return level;
        }
        return null;
    }

    // 通过方向获取字符串
    private String get_StringByDirection(Chess chessman, int x, int y) {
        boolean is_reverse = false;
        StringBuilder str = new StringBuilder();
        if (y > 0 || (x < 0 && y == 0))
            is_reverse = true;

        int xi = chessman.getX();
        int yi = chessman.getY();
        for (int i = 0; i < 5; i++) {
            xi += x;
            yi += y;
            if (xi >= 0 && xi < GoBangConst.LINE_NUMBER && yi >= 0 && yi < GoBangConst.LINE_NUMBER) {
                if (is_reverse) {
                    str.insert(0, chess[xi][yi].getPlayer());
                } else {
                    str.append(chess[xi][yi].getPlayer());
                }
            }
        }
        return str.toString();
    }

    // 检查是否出现胜负(只需判断当前的棋子就行，下一个棋子检查一个棋子无需循环)
    private boolean CheckWin(Chess chessBean) {
        boolean result = false;
        // 横线检查
        if (Get_count(chessBean, 1, 0) + Get_count(chessBean, -1, 0) >= 4)
            result = true;
            // 纵向检查
        else if (Get_count(chessBean, 0, 1) + Get_count(chessBean, 0, -1) >= 4)
            result = true;
            // 左上到右下检查
        else if (Get_count(chessBean, -1, 1) + Get_count(chessBean, 1, -1) >= 4)
            result = true;
            // 左下到右上检查
        else if (Get_count(chessBean, -1, -1) + Get_count(chessBean, 1, 1) >= 4)
            result = true;
        if (result) {
            isOver = true;
            JOptionPane.showMessageDialog(LeftPanel.this, "已决出胜负");
        }
        return result;
    }

    // 获取各个方向的同色棋子个数
    private int Get_count(Chess chessBean, int xi, int yi) {
        int sum = 0;
        int x_temp = chessBean.getX();
        int y_temp = chessBean.getY();
        for (int i = 0; i < 4; i++) {
            x_temp += xi;
            y_temp += yi;
            // 判断边界(不能取等也就是只能取到14)
            if (x_temp >= 0 && x_temp < GoBangConst.LINE_NUMBER && y_temp >= 0 && y_temp < GoBangConst.LINE_NUMBER) {
                if (chessBean.getPlayer() == chess[x_temp][y_temp].getPlayer())
                    sum++;
                else // 不是同色及时返回
                    break;
            }
        }
        return sum;
    }

    // 检查是否平局
    private boolean Check_DogFall(Chess[][] chess) {
        boolean result = true;
        for (int i = 0; i < GoBangConst.LINE_NUMBER; i++) {
            for (int j = 0; j < GoBangConst.LINE_NUMBER; j++) {
                if (chess[i][j].getPlayer() == GoBangConst.EMPTY) {
                    // 横线检查
                    if (Get_count(chess[i][j], 1, 0) + Get_count(chess[i][j], -1, 0) >= 4)
                        result = false;
                        // 纵向检查
                    else if (Get_count(chess[i][j], 0, 1) + Get_count(chess[i][j], 0, -1) >= 4)
                        result = false;
                        // 左上到右下检查
                    else if (Get_count(chess[i][j], -1, 1) + Get_count(chess[i][j], 1, -1) >= 4)
                        result = false;
                        // 左下到右上检查
                    else if (Get_count(chess[i][j], -1, -1) + Get_count(chess[i][j], 1, 1) >= 4)
                        result = false;
                }
                if (!result)
                    break;
            }
            if (!result)
                break;
        }
        if (result) {
            isOver = true;
            JOptionPane.showMessageDialog(LeftPanel.this, "游戏平局");
        }
        return result;
    }

    // 画部件
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D graphics2d = (Graphics2D) g;
        graphics2d.setStroke(new BasicStroke(2));
        Draw_Line(graphics2d);// 画棋盘
        Draw_Star(graphics2d);// 画天元和中心
        Draw_Chess(graphics2d);// 绘制棋子
        if (chess[x][y].getPlayer() == GoBangConst.EMPTY)
            Draw_Tip(graphics2d);// 画提示框
        else
            Draw_BanTip(graphics2d);// 画禁止框
        Draw_Mark(graphics2d);// 画两边的数字和字母位置
        Draw_Order(graphics2d);// 显示棋子顺序
        if (!isOver)// 在非开始的情况下不能找到提示棋子！！！
            Draw_TipChess(graphics2d);
    }

    private void Draw_TipChess(Graphics2D graphics2d) {
        if (isDrawTip) {
            graphics2d.setColor(Color.BLUE);
            graphics2d.setStroke(new BasicStroke(3));
            List<Chess> tipChess_list = get_SortedList(currentPlayer);
            int maxScore = tipChess_list.get(0).getSum();
            bestChess = tipChess_list.get(0);
            for (Chess value : tipChess_list) {
                if (value.getSum() == maxScore) {
                    int xi = GoBangConst.OFFSET + value.getX() * GoBangConst.LINE_DISTANCE;
                    int yi = GoBangConst.OFFSET + value.getY() * GoBangConst.LINE_DISTANCE;
                    int width = GoBangConst.CHESS_WIDTH;
                    graphics2d.drawOval(xi - width / 2, yi - width / 2, width, width);
                } else
                    break;
            }
        }
    }

    // 显示棋子顺序
    private void Draw_Order(Graphics2D graphics2d) {
        if (isShowNumber) {
            for (Chess[] value : chess) {
                for (Chess chessman : value) {
                    if (chessman.getPlayer() != GoBangConst.EMPTY) {
                        graphics2d.setColor(Color.RED);
                        FontMetrics fontMetrics = graphics2d.getFontMetrics();
                        int number = chessman.getOrder();
                        int width = fontMetrics.stringWidth(String.valueOf(number));
                        int height = fontMetrics.getAscent();
                        // 计算坐标并刻画数字
                        int xi = GoBangConst.OFFSET + chessman.getX() * GoBangConst.LINE_DISTANCE;
                        int yi = GoBangConst.OFFSET + chessman.getY() * GoBangConst.LINE_DISTANCE;
                        graphics2d.drawString(String.valueOf(number), xi - width / 2, yi + height / 2);
                    }
                }
            }
        }
    }

    // 绘制棋子
    private void Draw_Chess(Graphics2D graphics2d) {
        for (Chess[] value : chess) {
            for (Chess item : value) {
                if (item.getPlayer() != GoBangConst.EMPTY) {
                    // 在棋子非空的情况下选择不同画笔
                    if (item.getPlayer() == GoBangConst.BLACK)
                        graphics2d.setColor(Color.BLACK);
                    else if (item.getPlayer() == GoBangConst.WHITE)
                        graphics2d.setColor(Color.WHITE);
                    // 计算棋子的窗口坐标并进行绘制
                    int xi = GoBangConst.OFFSET + item.getX() * GoBangConst.LINE_DISTANCE;
                    int yi = GoBangConst.OFFSET + item.getY() * GoBangConst.LINE_DISTANCE;
                    int width = GoBangConst.CHESS_WIDTH;
                    graphics2d.fillOval(xi - width / 2, yi - width / 2, width, width);
                }
            }
        }
        if (!isShowNumber) {
            // 给最后一个棋子加上红点标志
            Chess last_Chess = get_LastChess();
            // 如果开局双方没有下的话不存在最后一个棋子
            if (last_Chess != null) {
                graphics2d.setColor(Color.RED);
                int xi = GoBangConst.OFFSET + last_Chess.getX() * GoBangConst.LINE_DISTANCE;
                int yi = GoBangConst.OFFSET + last_Chess.getY() * GoBangConst.LINE_DISTANCE;
                int width = GoBangConst.CHESS_WIDTH / 5;
                graphics2d.fillOval(xi - width / 2, yi - width / 2, width, width);
            }
        }
    }

    // 搜索最后一个棋子
    private Chess get_LastChess() {
        Chess chessBean = null;
        for (Chess[] value : chess) {
            for (Chess item : value) {
                if (item.getPlayer() != GoBangConst.EMPTY) {
                    if (chessBean == null)
                        chessBean = item;
                    else if (item.getOrder() > chessBean.getOrder())
                        chessBean = item;
                }
            }
        }
        return chessBean;
    }

    // 画两边的数字和字母位置
    private void Draw_Mark(Graphics2D graphics2d) {
        graphics2d.setColor(Color.BLACK);
        graphics2d.setFont(new Font("宋体", Font.BOLD, 15));
        FontMetrics fontMetrics = graphics2d.getFontMetrics();
        for (int i = 0; i < 15; i++) {
            // 纵向画数字
            String str_Number = String.valueOf(i + 1);
            int width = fontMetrics.getHeight();
            graphics2d.drawString(str_Number, GoBangConst.OFFSET / 6, (i + 1) * GoBangConst.LINE_DISTANCE + width / 3);
            // 横向画字母
            String str_Alphabet = String.valueOf((char) (65 + i));
            graphics2d.drawString(str_Alphabet, GoBangConst.OFFSET + i * GoBangConst.LINE_DISTANCE - width / 3,
                    GoBangConst.OFFSET / 5 * 4 + GoBangConst.LINE_NUMBER * GoBangConst.LINE_DISTANCE);
        }
    }

    // 绘制提示框
    private void Draw_Tip(Graphics2D graphics2d) {
        // 定画色
        graphics2d.setColor(Color.RED);
        // 根据棋盘内的坐标算出窗口中的坐标
        int xi = GoBangConst.OFFSET + x * GoBangConst.LINE_DISTANCE;
        int yi = GoBangConst.OFFSET + y * GoBangConst.LINE_DISTANCE;
        int half = GoBangConst.OFFSET / 2;
        int quarter = GoBangConst.OFFSET / 4;
        // 画左上角
        // 画横线
        graphics2d.drawLine(xi - half, yi - half, xi - half + quarter, yi - half);
        // 画竖线
        graphics2d.drawLine(xi - half, yi - half, xi - half, yi - half + quarter);

        // 画右上角
        // 画横线
        graphics2d.drawLine(xi + half, yi - half, xi + half - quarter, yi - half);
        // 画竖线
        graphics2d.drawLine(xi + half, yi - half, xi + half, yi - half + quarter);

        // 画左下角
        // 画横线
        graphics2d.drawLine(xi - half, yi + half, xi - half + quarter, yi + half);
        // 画竖线
        graphics2d.drawLine(xi - half, yi + half, xi - half, yi + half - quarter);

        // 画右下角
        // 画横线
        graphics2d.drawLine(xi + half, yi + half, xi + half - quarter, yi + half);
        // 画竖线
        graphics2d.drawLine(xi + half, yi + half, xi + half, yi + half - quarter);
    }

    // 绘制禁止框
    private void Draw_BanTip(Graphics2D graphics2d) {
        int xi = GoBangConst.OFFSET + x * GoBangConst.LINE_DISTANCE;
        int yi = GoBangConst.OFFSET + y * GoBangConst.LINE_DISTANCE;
        graphics2d.setColor(Color.RED);
        graphics2d.setStroke(new BasicStroke(5));
        int width = GoBangConst.CHESS_WIDTH;
        graphics2d.drawOval(xi - width / 2, yi - width / 2, width, width);
        int x1 = GoBangConst.OFFSET + x * GoBangConst.LINE_DISTANCE
                - (int) (Math.sin(Math.PI / 4) * GoBangConst.CHESS_WIDTH / 2);
        int y1 = GoBangConst.OFFSET + y * GoBangConst.LINE_DISTANCE
                + (int) (Math.sin(Math.PI / 4) * GoBangConst.CHESS_WIDTH / 2);
        int x2 = GoBangConst.OFFSET + x * GoBangConst.LINE_DISTANCE
                + (int) (Math.sin(Math.PI / 4) * GoBangConst.CHESS_WIDTH / 2);
        int y2 = GoBangConst.OFFSET + y * GoBangConst.LINE_DISTANCE
                - (int) (Math.sin(Math.PI / 4) * GoBangConst.CHESS_WIDTH / 2);
        graphics2d.drawLine(x1, y1, x2, y2);
    }

    // 画制天元和中心
    private void Draw_Star(Graphics2D graphics2d) {
        int quarter = GoBangConst.LINE_NUMBER / 4;// 整个棋盘四分之一的宽度
        // 画中心
        int center = GoBangConst.OFFSET + GoBangConst.LINE_DISTANCE * (GoBangConst.LINE_NUMBER / 2);
        graphics2d.fillOval(center - GoBangConst.STAR_WIDTH / 2, center - GoBangConst.STAR_WIDTH / 2,
                GoBangConst.STAR_WIDTH, GoBangConst.STAR_WIDTH);

        // 左上角点
        int center1 = GoBangConst.OFFSET + GoBangConst.LINE_DISTANCE * quarter;
        graphics2d.fillOval(center1 - GoBangConst.STAR_WIDTH / 2, center1 - GoBangConst.STAR_WIDTH / 2,
                GoBangConst.STAR_WIDTH, GoBangConst.STAR_WIDTH);

        // 右下角点
        int center2 = GoBangConst.OFFSET + GoBangConst.LINE_DISTANCE * (GoBangConst.LINE_NUMBER - quarter);
        graphics2d.fillOval(center2 - GoBangConst.STAR_WIDTH / 2, center2 - GoBangConst.STAR_WIDTH / 2,
                GoBangConst.STAR_WIDTH, GoBangConst.STAR_WIDTH);

        // 左下角点
        int center3_X = GoBangConst.OFFSET + GoBangConst.LINE_DISTANCE * quarter;
        int center3_Y = GoBangConst.OFFSET + GoBangConst.LINE_DISTANCE * (GoBangConst.LINE_NUMBER - quarter);
        graphics2d.fillOval(center3_X - GoBangConst.STAR_WIDTH / 2, center3_Y - GoBangConst.STAR_WIDTH / 2,
                GoBangConst.STAR_WIDTH, GoBangConst.STAR_WIDTH);

        // 右上角点
        int center4_X = GoBangConst.OFFSET + GoBangConst.LINE_DISTANCE * (GoBangConst.LINE_NUMBER - quarter);
        int center4_Y = GoBangConst.OFFSET + GoBangConst.LINE_DISTANCE * quarter;
        graphics2d.fillOval(center4_X - GoBangConst.STAR_WIDTH / 2, center4_Y - GoBangConst.STAR_WIDTH / 2,
                GoBangConst.STAR_WIDTH, GoBangConst.STAR_WIDTH);
    }

    // 绘制棋盘
    private void Draw_Line(Graphics2D graphics2d) {
        // 绘制横线
        for (int i = 0; i < GoBangConst.LINE_NUMBER; i++)
            graphics2d.drawLine(GoBangConst.OFFSET, // x1
                    GoBangConst.OFFSET + i * GoBangConst.LINE_DISTANCE, // y1
                    GoBangConst.OFFSET + GoBangConst.LINE_DISTANCE * (GoBangConst.LINE_NUMBER - 1), // x2
                    GoBangConst.OFFSET + i * GoBangConst.LINE_DISTANCE);// y2

        // 绘制竖线
        for (int i = 0; i < GoBangConst.LINE_NUMBER; i++)
            graphics2d.drawLine(GoBangConst.OFFSET + i * GoBangConst.LINE_DISTANCE, // x1
                    GoBangConst.OFFSET, // y1
                    GoBangConst.OFFSET + i * GoBangConst.LINE_DISTANCE, // x2
                    GoBangConst.OFFSET + GoBangConst.LINE_DISTANCE * (GoBangConst.LINE_NUMBER - 1));// y2
    }

    // 设置是否显示数字
    public void Set_ShowNumber(boolean is_ShowNumber) {
        this.isShowNumber = is_ShowNumber;
        repaint();
    }

    // 新游戏的初始化
    public void New_Game(boolean mode, boolean intel, int depth, int node, boolean isShowNumber,
                         boolean human_FirstHand, JTextArea textArea, boolean is_tips) {
        // 初始化设置
        this.mode = mode;
        this.intel = intel;
        this.depth = depth;
        this.node = node;
        this.isShowNumber = isShowNumber;
        this.textArea = textArea;
        this.textArea.setText("");// 设置为空
        this.isOver = false;
        this.isDogFall = false;
        this.count = 0;
        this.currentPlayer = GoBangConst.BLACK;
        this.isDrawTip = is_tips;
        this.isEmpty = true;

        // 初始化棋盘
        for (int i = 0; i < chess.length; i++) {
            for (int j = 0; j < chess[i].length; j++) {
                chess[i][j] = new Chess(i, j, 0, 0);
            }
        }

        JOptionPane.showMessageDialog(this, "游戏开始！");

        // 人机对战+机器先手
        if (!mode && !human_FirstHand) {
            int center = GoBangConst.LINE_NUMBER / 2;
            chess[center][center].setOrder(0);
            count++;
            chess[center][center].setPlayer(currentPlayer);
            currentPlayer = 3 - currentPlayer;
            isEmpty = false;// 防止人类先手+提示报错(棋盘没有棋子无法获取棋子分数)
        }
        repaint();
    }

    // 悔棋
    public void set_Regret() {
        // 判断游戏是否结束
        if (isOver) {
            JOptionPane.showMessageDialog(LeftPanel.this, "游戏已结束，无法悔棋");
        } else {
            int times;// 设置循环次数，人机模式悔棋二次，人人模式悔棋一次
            if (mode)// 人人模式
                times = 1;
            else// 人机模式
                times = 2;
            // 判断是否有棋子
            if (count > 0) {
                for (int i = 0; i < times; i++) {
                    if (!mode && count == 1)// 人机模式下只有一个棋子是电脑下的此时不能
                    {
                        JOptionPane.showMessageDialog(LeftPanel.this, "开局电脑先手无法悔棋");
                        return;
                    }
                    Chess chessBean = get_LastChess();
                    chess[chessBean.getX()][chessBean.getY()].setOrder(0);
                    chess[chessBean.getX()][chessBean.getY()].setPlayer(GoBangConst.EMPTY);
                    count--;
                    currentPlayer = 3 - currentPlayer;
                    repaint();
                }
            } else {
                JOptionPane.showMessageDialog(LeftPanel.this, "没有棋子无法悔棋");
            }
        }
    }

    // 提示框设置
    public void setShowTips(boolean isDrawTip) {
        this.isDrawTip = isDrawTip;
        if (!isOver)
            repaint();
    }

    // 棋型信息
    public enum Level {
        CON_5("长连", 0, new String[]{"11111", "22222"}, 100000),
        ALIVE_4("活四", 1, new String[]{"011110", "022220"}, 10000),
        GO_4("冲四", 2, new String[]{"011112|0101110|0110110", "022221|0202220|0220220"}, 500),
        DEAD_4("死四", 3, new String[]{"211112", "122221"}, -5),
        ALIVE_3("活三", 4, new String[]{"01110|010110", "02220|020220"}, 200),
        SLEEP_3("眠三", 5,
                new String[]{"001112|010112|011012|10011|10101|2011102", "002221|020221|022021|20022|20202|1022201"},
                50),
        DEAD_3("死三", 6, new String[]{"21112", "12221"}, -5),
        ALIVE_2("活二", 7, new String[]{"00110|01010|010010", "00220|02020|020020"}, 5),
        SLEEP_2("眠二", 8,
                new String[]{"000112|001012|010012|10001|2010102|2011002",
                        "000221|002021|020021|20002|1020201|1022001"},
                3),
        DEAD_2("死二", 9, new String[]{"2112", "1221"}, -5), NULL("null", 10, new String[]{"", ""}, 0);

        private final String name;
        private final int index;
        private final String[] regex;// 正则表达式
        private final int score;// 分值

        // 构造方法
        Level(String name, int index, String[] regex, int score) {
            this.name = name;
            this.index = index;
            this.regex = regex;
            this.score = score;
        }

        // 覆盖方法
        @Override
        public String toString() {
            return this.name;
        }
    }
}
