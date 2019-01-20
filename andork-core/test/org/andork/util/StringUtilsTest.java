package org.andork.util;

import org.junit.Assert;
import org.junit.Test;

public class StringUtilsTest {
	@Test
	public void testWrapEmptyString() {
		String text = new StringBuilder()
			.append("")
			.toString();
		
		String expected = new StringBuilder()
			.append("")
			.toString();
		
		Assert.assertEquals(expected, StringUtils.wrap(text, 80));
	}
	
	@Test
	public void testWrapParagraphs() {
		String text = new StringBuilder()
			.append("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.")
			.append("\n\n")
			.append("Sed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium doloremque laudantium, totam rem aperiam, eaque ipsa quae ab illo inventore veritatis et quasi architecto beatae vitae dicta sunt explicabo. Nemo enim ipsam voluptatem quia voluptas sit aspernatur aut odit aut fugit, sed quia consequuntur magni dolores eos qui ratione voluptatem sequi nesciunt. Neque porro quisquam est, qui dolorem ipsum quia dolor sit amet, consectetur, adipisci velit, sed quia non numquam eius modi tempora incidunt ut labore et dolore magnam aliquam quaerat voluptatem. Ut enim ad minima veniam, quis nostrum exercitationem ullam corporis suscipit laboriosam, nisi ut aliquid ex ea commodi consequatur? Quis autem vel eum iure reprehenderit qui in ea voluptate velit esse quam nihil molestiae consequatur, vel illum qui dolorem eum fugiat quo voluptas nulla pariatur?")
			.toString();
		
		String expected = StringUtils.join("\n",
			//123456789012345678901234567890123456789"
			"Lorem ipsum dolor sit amet, consectetur",
			"adipiscing elit, sed do eiusmod tempor",
			"incididunt ut labore et dolore magna",
			"aliqua. Ut enim ad minim veniam, quis",
			"nostrud exercitation ullamco laboris",
			"nisi ut aliquip ex ea commodo consequat.",
			"Duis aute irure dolor in reprehenderit",
			"in voluptate velit esse cillum dolore eu",
			"fugiat nulla pariatur. Excepteur sint",
			"occaecat cupidatat non proident, sunt in",
			"culpa qui officia deserunt mollit anim",
			"id est laborum.",
			"",
			"Sed ut perspiciatis unde omnis iste",
			"natus error sit voluptatem accusantium",
			"doloremque laudantium, totam rem",
			"aperiam, eaque ipsa quae ab illo",
			"inventore veritatis et quasi architecto",
			"beatae vitae dicta sunt explicabo. Nemo",
			"enim ipsam voluptatem quia voluptas sit",
			"aspernatur aut odit aut fugit, sed quia",
			"consequuntur magni dolores eos qui",
			"ratione voluptatem sequi nesciunt. Neque",
			"porro quisquam est, qui dolorem ipsum",
			"quia dolor sit amet, consectetur,",
			"adipisci velit, sed quia non numquam",
			"eius modi tempora incidunt ut labore et",
			"dolore magnam aliquam quaerat",
			"voluptatem. Ut enim ad minima veniam,",
			"quis nostrum exercitationem ullam",
			"corporis suscipit laboriosam, nisi ut",
			"aliquid ex ea commodi consequatur? Quis",
			"autem vel eum iure reprehenderit qui in",
			"ea voluptate velit esse quam nihil",
			"molestiae consequatur, vel illum qui",
			"dolorem eum fugiat quo voluptas nulla",
			"pariatur?"
		);
		
		Assert.assertEquals(expected, StringUtils.wrap(text, 40));
	}

	@Test
	public void testWrapLongWord() {
		String text = new StringBuilder()
			.append("foo barrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrr")
			.toString();
		
		String expected = StringUtils.join("\n",
			text.substring(4, 24),
			text.substring(24, 44),
			text.substring(44)
		);
		
		Assert.assertEquals(expected, StringUtils.wrap(text, 20));
	}
	
	@Test
	public void testBug001() {
		String input = "continues 150'+ of same then a junction with a 4hx3w twisty and hard to travel passage";
				
		String expected = StringUtils.join("\n",
			"continues 150'+ of same then a junction with a 4hx3w",
			"twisty and hard to travel passage"
		);

		Assert.assertEquals(expected, StringUtils.wrap(input, 55));
	}
}
