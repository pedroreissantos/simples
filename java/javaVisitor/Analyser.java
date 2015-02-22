public interface Analyser {
	public boolean parse();
	public int errors();
	public void accept(Compiler c);
	public String filename();
}
