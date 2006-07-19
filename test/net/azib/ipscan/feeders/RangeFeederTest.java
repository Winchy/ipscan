package net.azib.ipscan.feeders;

import junit.framework.TestCase;

/**
 * Test of RangeFeeder 
 * 
 * @author anton
 */
public class RangeFeederTest extends TestCase {

	public void testHappyPath() throws FeederException {
		RangeFeeder rangeFeeder = new RangeFeeder();
		assertEquals(2, rangeFeeder.initialize(new String[] {"10.11.12.13", "10.11.12.15"}));
		assertTrue(rangeFeeder.hasNext());
		assertEquals("10.11.12.13", rangeFeeder.next().getHostAddress());
		assertTrue(rangeFeeder.hasNext());
		assertEquals("10.11.12.14", rangeFeeder.next().getHostAddress());
		assertTrue(rangeFeeder.hasNext());
		assertEquals("10.11.12.15", rangeFeeder.next().getHostAddress());
		assertFalse(rangeFeeder.hasNext());
	}
	
	public void testInvalidRange() {
		try {
			new RangeFeeder().initialize("10.11.12.13", "10.11.12.10");
			fail();
		}
		catch (FeederException e) {
			FeederTestUtils.assertFeederException("range.greaterThan", e);
		}
	}

	public void testMalformedIP() {
		try {
			new RangeFeeder().initialize("10.11.12.abc", "10.11.12.10");
			fail();
		}
		catch (FeederException e) {
			FeederTestUtils.assertFeederException("malformedIP", e);
		}
		try {
			new RangeFeeder().initialize("10.11.12.1", "ziga");
			fail();
		}
		catch (FeederException e) {
			FeederTestUtils.assertFeederException("malformedIP", e);
		}
	}
	
	public void testExtremeValues() {
		RangeFeeder rangeFeeder = null; 
		
		rangeFeeder = new RangeFeeder();
		rangeFeeder.initialize("0.0.0.0", "0.0.0.0");
		assertTrue(rangeFeeder.hasNext());
		assertEquals("0.0.0.0", rangeFeeder.next().getHostAddress());
		assertFalse(rangeFeeder.hasNext());
		
		rangeFeeder.initialize("255.255.255.255", "255.255.255.255");
		assertTrue(rangeFeeder.hasNext());
		assertEquals("255.255.255.255", rangeFeeder.next().getHostAddress());
		assertFalse(rangeFeeder.hasNext());
	}
		
	public void testGetPercentageComplete() throws Exception {
		RangeFeeder rangeFeeder = new RangeFeeder();
		rangeFeeder.initialize("100.11.12.13", "100.11.12.15");
		assertEquals(0, rangeFeeder.getPercentageComplete());
		rangeFeeder.next();
		assertEquals(33, rangeFeeder.getPercentageComplete());
		rangeFeeder.next();
		assertEquals(67, rangeFeeder.getPercentageComplete());
		rangeFeeder.next();
		assertEquals(100, rangeFeeder.getPercentageComplete());
		
		rangeFeeder.initialize("255.255.255.255", "255.255.255.255");
		assertEquals(0, rangeFeeder.getPercentageComplete());
		rangeFeeder.next();
		assertEquals(100, rangeFeeder.getPercentageComplete());
	}
	
	public void testGetInfo() {
		RangeFeeder rangeFeeder = new RangeFeeder();
		rangeFeeder.initialize("100.11.12.13", "100.11.12.13");
		assertEquals("100.11.12.13 - 100.11.12.13", rangeFeeder.getInfo());
		rangeFeeder.initialize("0.0.0.0", "255.255.255.255");
		assertEquals("0.0.0.0 - 255.255.255.255", rangeFeeder.getInfo());
	}
}
