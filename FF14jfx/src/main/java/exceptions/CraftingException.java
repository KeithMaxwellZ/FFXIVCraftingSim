package exceptions;

public class CraftingException extends Exception
{
	
	public final ExceptionStatus es;
	
	public CraftingException(ExceptionStatus es) {
		this.es = es;
	}
}
