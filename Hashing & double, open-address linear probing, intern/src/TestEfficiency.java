import edu.uwm.cs351.StringCache;
import junit.framework.TestCase;


public class TestEfficiency extends TestCase {
    private static final int POWER = 20; // 1 million entries
    private static final int TESTS = 100000;
    
    private StringCache c;
    private StringCache.Spy spy;
    
	private String s(int i) {
		return "#" + i*(long)i;
	}

	protected void setUp() {
		try {
			assert c.intern(null) == "null" : "cannot run test with assertions enabled";
		} catch (NullPointerException ex) {
			throw new IllegalStateException("Cannot run test with assertions enabled");
		}
		c = new StringCache();
		spy = new StringCache.Spy();
	}
	
	protected void addMany() {
		int max = (1 << POWER);
		for (int i=0; i < max; ++i) {
			c.intern(s(i));
		}
	}
	
	public void test1() {
		addMany();
		assertTrue(spy.getCapacity(c) > (2<<POWER));
	}
	
	public void test2() {
		addMany();
		int s = spy.getCapacity(c) ;
		addMany();
		assertEquals(s, spy.getCapacity(c));
	}
	
	public void test3() {
		String key = c.intern(s(TESTS));
		addMany();
		for (int i=0; i < TESTS; ++i) {
			assertSame(key, c.intern(s(TESTS)));
		}
	}
}
