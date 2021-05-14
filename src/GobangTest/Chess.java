package GobangTest;

public class Chess implements Comparable<Chess>{
	//棋子位置
	private int X;
	private int Y;
	//黑白玩家
	private int Player;
	//落子顺序
	private int Order;
	
	private int offence;
	private int defence;
	private int sum;
	
	private StringBuffer buffer;
	
	public Chess(int X,int Y,int Player,int Order)
	{
		this.X=X;
		this.Y=Y;
		this.Player=Player;
		this.Order=Order;
		this.buffer=new StringBuffer();
	}
	
	public int getX() {
		return X;
	}
	public void setX(int x) {
		X = x;
	}
	public int getY() {
		return Y;
	}
	public void setY(int y) {
		Y = y;
	}

	public int getPlayer() {
		return Player;
	}

	public void setPlayer(int player) {
		Player = player;
	}

	public int getOrder() {
		return Order;
	}

	public void setOrder(int order) {
		Order = order;
	}

	public int getOffence() {
		return offence;
	}

	public void setOffence(int offence) {
		this.offence = offence;
	}

	public int getDefence() {
		return defence;
	}

	public void setDefence(int defence) {
		this.defence = defence;
	}

	public int getSum() {
		return sum;
	}

	public void setSum(int sum) {
		this.sum = sum;
	}
	

	public StringBuffer getBuffer() {
		return buffer;
	}

	public void setBuffer(StringBuffer buffer) {
		this.buffer = buffer;
	}

	@Override
	public int compareTo(Chess o) {
		if (sum > o.getSum())
			return -1;
		else if (sum < o.getSum())
			return 1;
		else
			return 0;
	}
	
}
