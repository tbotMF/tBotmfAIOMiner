package scripts.actions;

import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api.types.generic.Condition;
import org.tribot.api2007.GameTab;
import org.tribot.api2007.GroundItems;
import org.tribot.api2007.Inventory;
import org.tribot.api2007.ext.Filters;
import org.tribot.api2007.types.RSGroundItem;

import scripts.Globals;
import scripts.sbf.action.Action;
import scripts.sbf.util.ABC;
import scripts.sbf.util.MFUtil;

public class FixAxe extends Action {
	private final String PICK_AXE_HEAD = "Pickaxe head";
	private final String PICK_AXE_HANDLE = "Pickaxe handle";
	private final ABC abc = manager.getABC();

	private boolean hasBothAxeComponents() {
		return Inventory.find(Filters.Items.nameEquals(PICK_AXE_HEAD,
				PICK_AXE_HANDLE)).length > 0;

	}

	private boolean freeInventorySpace() {
		return MFUtil.getCountOfFreeInvSpace() < 2 ? (Inventory
				.find(Filters.Items.nameContains("ore", "Clay")).length > 0 ? Inventory
				.drop(Inventory.find(Filters.Items.nameContains("ore", "Clay"))) == 2
				: false)
				: true;
	}

	private boolean findPickAxeHead() {
		RSGroundItem[] pickAxeHead = GroundItems.find(PICK_AXE_HEAD);
		if (pickAxeHead.length > 0 && pickAxeHead[0] != null) {
			if (pickAxeHead[0].click("Take " + PICK_AXE_HEAD))
				return Timing.waitCondition(new Condition() {

					@Override
					public boolean active() {
						General.sleep(100, 200);
						return Inventory.find(PICK_AXE_HEAD).length > 0;
					}
				}, General.random(3000, 4000));
		}
		return true;

	}

	private void fixAxe() {
		if (Inventory.find(PICK_AXE_HEAD)[0].click("Use"))
			General.sleep(this.abc.itemInteractionDelay());
		else
			return;
		if (Inventory.find(PICK_AXE_HANDLE)[0].click("Use"))
			General.sleep(this.abc.itemInteractionDelay());
		else
			return;
	}


	@Override
	public void execute() {
		print("Fixing axe");

		if (!freeInventorySpace())
			return;

		if (!MFUtil.switchTab(GameTab.TABS.INVENTORY))
			return;

		if (findPickAxeHead() && hasBothAxeComponents())
			fixAxe();

	}

	@Override
	public boolean isValid() {
		return Globals.HAS_PICKAXE_HANDLE.getStatus();
	}
}
