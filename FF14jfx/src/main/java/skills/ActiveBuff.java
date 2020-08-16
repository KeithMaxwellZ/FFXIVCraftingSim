package skills;

public class ActiveBuff
{
	public final Buff buff;
	private int remaining;
	
	public ActiveBuff(Buff buff, int last) {
		this.buff = buff;
		this.remaining = last;
	}
	
	public void decrease() {
		if(buff!=Buff.inner_quiet) {remaining--;}
	}
	
	
	public int getRemaining() {
		return remaining;
	}
	
	public void setRemaining(int remaining) {
		this.remaining = remaining;
	}
}
