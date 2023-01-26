import edu.uwm.cs351.StringCache;
import junit.framework.TestCase;

public class TestStringCache extends TestCase {
	private StringCache c;
	private StringCache.Spy spy;
	
	@Override
	protected void setUp() {
		try {
			assert c.intern("a") == "a" : "OK";
			System.err.println("Assertions must be enabled to use this test suite.");
			System.err.println("In Eclipse: add -ea in the VM Arguments box under Run>Run Configurations>Arguments");
			assertFalse("Assertions must be -ea enabled in the Run Configuration>Arguments>VM Arguments",true);
		} catch (NullPointerException ex) {
			// muffle exception
		}
		c = new StringCache();
		spy = new StringCache.Spy();
	}

	public void test00() {
		assertEquals(2, spy.getCapacity(c));
		assertEquals(0, spy.getSize(c));
	}
	
	public void test01() {
		assertSame("a", c.intern("a"));
	}
	
	public void test02() {
		assertSame("a", c.intern("a"));
		assertSame("b", c.intern("b"));
	}
	
	public void test03() {
		c.intern("a");
		assertSame("a", c.intern(new String("a")));
	}
	
	public void test04() {
		c.intern("bread");
		c.intern("eggs");
		assertSame("eggs", c.intern(new String("eggs")));
		assertSame("bread", c.intern(new String(new char[] {'b','r','e','a','d'})));
	}
	
	public void test05() {
		assertNull(c.intern(null));
	}
	
	private String passThrough(String arg) {
		if (c.hashCode() != -43) return arg;
		return "highly unlikely";
	}
	
	public void test06() {
		c.intern("hello");
		c.intern(new String("hello"));
		assertSame("hello",c.intern("he" + passThrough("llo")));
	}
	
	public void test07() {
		c.intern("grapes");
		c.intern("lemon");
		assertEquals(11, spy.getCapacity(c));
	}
	
	public void test08() {
		c.intern("");
		assertSame("", c.intern("hello".substring(5)));
		assertSame("", c.intern("" + passThrough("")));
		assertEquals(2, spy.getCapacity(c));
	}
	
	public void test09() {
		c.intern("");
		assertNull(c.intern(null));
		assertEquals(2, spy.getCapacity(c));
	}
	
	public void test10() {
		String created = new String(new char[] {'f','i','s','h'});
		assertFalse(created == "fish");
		assertSame(created, c.intern(created));
	}
	
	public void test11() {
		c.intern("apples");
		c.intern("bread");
		c.intern("cabbage");
		c.intern("donuts");
		c.intern("eggs");
		assertSame("apples", c.intern("apples"));
		assertSame("bread", c.intern(new String("bread")));
		assertSame("cabbage", c.intern("cabb" + passThrough("age")));
		assertSame("donuts", c.intern("no donuts, thanks".substring(3,9)));
		assertSame("eggs", c.intern(new String(new char[] {'e', 'g', 'g', 's'})));
	}
	
	public void test12() {
		c.intern("apples");
		c.intern("bread");
		c.intern("cabbage");
		c.intern("donuts");
		c.intern("eggs");
		c.intern("apples");
		c.intern("bread");
		c.intern("cabbage");
		c.intern("donuts");
		c.intern("eggs");
		assertEquals(5, spy.getSize(c));
		assertEquals(11, spy.getCapacity(c));
	}
	
	public void test13() {
		c.intern("apples");
		c.intern("bread");
		c.intern("cabbage");
		c.intern("donuts");
		c.intern("eggs");
		c.intern("flowers");
		assertEquals(29, spy.getCapacity(c));
	}
	
	public void test14() {
		String source = "0123456789";
		for (int i=0; i <= 10; ++i) {
			for (int j=i; j <= 10; ++j) {
				c.intern(source.substring(i,j));
			}
		}
		String check = c.intern("456");
		assertFalse(check == "456");
		assertSame(check, c.intern("45"+passThrough("6")));
	}
	
	public void test15() {
		String source = "0123456789";
		for (int i=0; i <= 10; ++i) {
			for (int j=i; j <= 10; ++j) {
				c.intern(source.substring(i,j));
			}
		}
		assertEquals(56, spy.getSize(c));
		c.intern("apples");
		c.intern("bread");
		c.intern("cabbage");
		c.intern("donuts");
		c.intern("eggs");
		c.intern("flowers");
		c.intern("grapes");
		assertEquals(127, spy.getCapacity(c));
		c.intern("hello");
		assertEquals(257, spy.getCapacity(c));
	}
	
	public void test16() {
		c.intern("apples");
		c.intern("oranges");
		assertSame("HZcxf_", c.intern("HZcxf_"));
	}
}
