package scripts;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.table.DefaultTableModel;

import org.tribot.api2007.types.RSTile;

import scripts.actions.Deposit;
import scripts.actions.Equip;
import scripts.actions.FixAxe;
import scripts.actions.LogOut;
import scripts.actions.Mine;
import scripts.actions.OpenBank;
import scripts.actions.WalkToBank;
import scripts.actions.WalkToMine;
import scripts.actions.Withdraw;
import scripts.sbf.graphics.AbstractGUI;
import scripts.sbf.state.State;
import scripts.sbf.util.worldhop.GoToWorldScreen;
import scripts.sbf.util.worldhop.Hop;
import scripts.sbf.util.worldhop.LogIn;
import scripts.states.Banking;
import scripts.states.DeathWalking;
import scripts.states.Hopping;
import scripts.states.Mining;

public class mfAIOMinerGUI extends AbstractGUI {

	/**
	 * Creates new form mfAIOMinerGUI
	 */
	public mfAIOMinerGUI() {
		initComponents();
	}

	
	@Override
	public List<State> createStateAssociations() {
		List<State> associations = new ArrayList<State>();

		State mining = new Mining();
		State banking = new Banking();
		State worldHopping = new Hopping();

		mining.addActions(new Equip(), new WalkToMine(), new Mine(),
				new FixAxe());
		banking.addActions(new WalkToBank(), new OpenBank(), new Deposit(),
				new Withdraw());
		worldHopping.addActions(new LogOut(), new GoToWorldScreen(), new Hop(),
				new LogIn());
		Collections.addAll(associations, worldHopping, mining, banking);
		return associations;
	}

	
	@Override
	public void processUserSelections() {
		processMiningLocations();
		processOresTable();
		processDroppingMethod();
		processMining();

		selections.put("Player hop amount", hopWhenPlayerNumCheckBox.getText());
	}

	private void processDroppingMethod() {
		if (powerMineRadioButton.isSelected())
			selections.put("Dropping Method",
					(String) dropMethodCheckBox.getSelectedItem());

	}

	private void processMining() {
		if (powerMineRadioButton.isSelected())
			selections.put("Mining method", "Powermine");
		else
			selections.put("Mining method", "Banking");

	}

	public void processMiningLocations() {
		selections.put("Location", (String) locationComboBox.getSelectedItem());
		switch (selections.get("Location")) {
		case "Varrock East":
			skillManager.loadSkillTile(new RSTile(3285, 3365));

			break;
		case "Varrock West":
			skillManager.loadSkillTile(new RSTile(3178, 3368));

			break;
		case "Lumbridge Swamp East":
			skillManager.loadSkillTile(new RSTile(3226, 3147));

			break;
		case "Lumbridge Swamp West":
			skillManager.loadSkillTile(new RSTile(3145, 3147));
			break;
		case "Al-Kharid":
			skillManager.loadSkillTile(new RSTile(3298, 3298));

			break;
		case "Wilderness - Hobgoblins' Mine":
			skillManager.loadSkillTile(new RSTile(3082, 3760));

			break;
		case "Wilderness - Runite Mine":
			skillManager.loadSkillTile(new RSTile(3059, 3884));

			break;
		case "Wilderness - Pirates' Mine":
			skillManager.loadSkillTile(new RSTile(3058, 3945));

			break;
		case "Wilderness - Skeletons' Mine":
			skillManager.loadSkillTile(new RSTile(3017, 3590));

			break;
		case "Wilderness - Steel Mine":
			skillManager.loadSkillTile(new RSTile(3104, 3566));

			break;
		case "East Ardougne":
			skillManager.loadSkillTile(new RSTile(2710, 3300));

			break;
		case "South Ardougne":
			skillManager.loadSkillTile(new RSTile(2604, 3235));

			break;
		case "TzHaar":
			skillManager.loadSkillTile(new RSTile(2454, 5168));

			break;
		case "Yanille":
			skillManager.loadSkillTile(new RSTile(2628, 3145));

			break;
		case "Barbarian village":
			skillManager.loadSkillTile(new RSTile(3081, 3421));

			break;
		case "Rimmington":
			skillManager.loadSkillTile(new RSTile(2979, 3238));

			break;
		}
		skillManager.loadSkillArea(30);
	}

	private List<String> loadOres() {

		List<String> customList = new ArrayList<>();
		for (int j = 0; j < oresTable.getRowCount(); j++) {
			String value = (String) oresTable.getValueAt(j, 0);
			if (value != null && value.length() > 0)
				customList.add(value);
		}

		return customList;

	}

	public void processOresTable() {
		List<String> oreValues = new ArrayList<>();
		for (String a : loadOres()) {
			if (a.equals("Gold") || a.equals("Mithril") || a.equals("Adamant")
					|| a.equals("Runite"))
				selections.put("Is mining rare", "yes");
			oreValues.add(a);
		}
		selections.put("ores", oreValues);
	}

}
