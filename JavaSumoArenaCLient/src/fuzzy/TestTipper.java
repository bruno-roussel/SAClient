package fuzzy;

import net.sourceforge.jFuzzyLogic.FIS;

/**
 * Test parsing an FCL file
 * 
 * @author pcingola@users.sourceforge.net
 */
public class TestTipper {
	public static void main(String[] args) throws Exception {
		// Load from 'FCL' file
		String fileName = "fcl/tipper.fcl";
		FIS fis = FIS.load(fileName, true);
		// Error while loading?
		if (fis == null) {
			System.err.println("Can't load file: '" + fileName + "'");
			return;
		}

		// Show
		//fis.chart();

		// Set inputs
		fis.setVariable("service", 6);
		fis.setVariable("food", 3);

		// Evaluate
		fis.evaluate();

		// Show output variable's chart
		//fis.getVariable("tip").chartDefuzzifier(true);

		System.out.println(fis.getVariable("tip").getLatestDefuzzifiedValue());
		// Print ruleSet
		//System.out.println(fis);
	}
}