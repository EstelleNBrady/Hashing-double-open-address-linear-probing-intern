import java.util.function.Consumer;
import java.util.function.Supplier;

import edu.uwm.cs351.StringCache;
import junit.framework.TestCase;

public class TestInternals extends TestCase {

	private StringCache c;
	private StringCache.Spy spy;
	
	protected void setUp() {
		spy = new StringCache.Spy();
	}
	
	private int reports = 0;
	
	protected void assertReporting(boolean expected, Supplier<Boolean> test) {
		reports = 0;
		Consumer<String> savedReporter = spy.getReporter();
		try {
			spy.setReporter((String message) -> {
				++reports;
				if (message == null || message.trim().isEmpty()) {
					assertFalse("Uninformative report is not acceptable", true);
				}
				if (expected) {
					assertFalse("Reported error incorrectly: " + message, true);
				}
			});
			assertEquals(expected, test.get().booleanValue());
			if (!expected) {
				assertEquals("Expected exactly one invariant error to be reported", 1, reports);
			}
			spy.setReporter(null);
		} finally {
			spy.setReporter(savedReporter);
		}

	}

	
	public void testA() {
		c = spy.create(null, 0);
		assertReporting(false, () -> spy.wellFormed(c));
	}
	
	public void testB() {
		c = spy.create(new String[1], 0);
		assertReporting(false, () -> spy.wellFormed(c));
	}
	
	public void testC() {
		String[] a = new String[2];
		c = spy.create(a, 0);
		assertReporting(true, () -> spy.wellFormed(c));
	}

	public void testD() {
		String[] a = new String[2];
		a[1] = "a";
		assertReporting(false, () -> spy.wellFormed(spy.create(a, 0)));
		assertReporting(true,  () -> spy.wellFormed(spy.create(a, 1)));
		assertReporting(false, () -> spy.wellFormed(spy.create(a, 2)));
	}
	
	public void testE() {
		String[] a = new String[3];		
		a[2] = "b";
		assertReporting(true, () -> spy.wellFormed(spy.create(a, 1)));
	}
	
	public void testF() {
		String a[] = new String[4];
		assertReporting(false, () -> spy.wellFormed(spy.create(a, 0)));
	}
	
	public void testG() {
		String a[] = new String[5];
		assertReporting(true,  () -> spy.wellFormed(spy.create(a, 0)));
		assertReporting(false, () -> spy.wellFormed(spy.create(a, 1)));
		a[2] = "a";
		assertReporting(true,  () -> spy.wellFormed(spy.create(a, 1)));
		a[3] = "b";
		assertReporting(false, () -> spy.wellFormed(spy.create(a, 1)));
		assertReporting(true,  () -> spy.wellFormed(spy.create(a, 2)));		
	}
	
	public void testH() {
		String a[] = new String[3];
		a[1] = "a";
		a[2] = "b";
		assertReporting(false, () -> spy.wellFormed(spy.create(a, 2)));		
	}
	
	public void testI() {
		String a[] = new String[2];
		a[0] = "c";
		assertReporting(false, () -> spy.wellFormed(spy.create(a, 1)));	
		a[1] = a[0];
		a[0] = null;
		assertReporting(true,  () -> spy.wellFormed(spy.create(a, 1)));		
	}

	public void testJ() {
		String a[] = new String[5];
		a[4] = "hello, world";
		assertReporting(true,  () -> spy.wellFormed(spy.create(a, 1)));
		a[1] = a[4];
		a[4] = null;
		assertReporting(false, () -> spy.wellFormed(spy.create(a, 1)));		
	}
	
	public void testK() {
		String[] a = new String[5];
		a[2] = "a";
		a[4] = "b";
		assertReporting(false, () -> spy.wellFormed(spy.create(a, 2)));	
		a[4] = "c";
		assertReporting(true,  () -> spy.wellFormed(spy.create(a, 2)));	
		a[3] = "b";
		assertReporting(false, () -> spy.wellFormed(spy.create(a, 3)));				
	}
	
	public void testL() {
		String[] a = new String[11];
		a[4] = "world";
		a[6] = "hello";
		assertReporting(false, () -> spy.wellFormed(spy.create(a, 2)));	
		a[3] = a[4];
		a[4] = null;
		assertReporting(true,  () -> spy.wellFormed(spy.create(a, 2)));				
	}
	
	public void testM() {
		String[] a = new String[5];
		a[1] = "f";
		assertReporting(false, () -> spy.wellFormed(spy.create(a, 1)));	
		a[2] = "a";
		assertReporting(true,  () -> spy.wellFormed(spy.create(a, 2)));	
		a[1] = "a";
		assertReporting(false, () -> spy.wellFormed(spy.create(a, 1)));	
		assertReporting(false, () -> spy.wellFormed(spy.create(a, 2)));
	}
	
	public void testN() {
		String[] a = new String[11];
		a[2] = "f";
		assertReporting(false, () -> spy.wellFormed(spy.create(a, 1)));	
		a[3] = "world";
		assertReporting(true,  () -> spy.wellFormed(spy.create(a, 2)));	
		a[3] = "hello";
		assertReporting(false, () -> spy.wellFormed(spy.create(a, 2)));		
	}
	
	public void testO() {
		String[] a = new String[29];
		a[12] = "c";
		assertReporting(true,  () -> spy.wellFormed(spy.create(a, 1)));	
		a[11] = "hello";
		assertReporting(true,  () -> spy.wellFormed(spy.create(a, 2)));	
		a[10] = "a";
		assertReporting(true,  () -> spy.wellFormed(spy.create(a, 3)));	
		a[9] = "b";
		assertReporting(true,  () -> spy.wellFormed(spy.create(a, 4)));	
		a[8] = "c";
		assertReporting(false, () -> spy.wellFormed(spy.create(a, 4)));	
		assertReporting(false, () -> spy.wellFormed(spy.create(a, 5)));	
		a[8] = "d";
		assertReporting(false, () -> spy.wellFormed(spy.create(a, 5)));	
	}
	
	public void testP() {
		String a[] = new String[29];
		a[12] = "hello";
		a[15] = "hwllo, world";
		assertReporting(true,  () -> spy.wellFormed(spy.create(a, 2)));	
		a[11] = "b";
		a[14] = "f";
		assertReporting(true,  () -> spy.wellFormed(spy.create(a, 4)));	
		a[10] = "c";
		a[9] = "e";
		assertReporting(false, () -> spy.wellFormed(spy.create(a, 6)));	
		a[13] = "d";
		assertReporting(true,  () -> spy.wellFormed(spy.create(a, 7)));	
	}
	
	public void testQ() {
		String a[] = new String[7];
		a[6] = "b";
		assertReporting(false, () -> spy.wellFormed(spy.create(a, 1)));	
		a[0] = "hello";
		assertReporting(true,  () -> spy.wellFormed(spy.create(a, 2)));	
	}
}
