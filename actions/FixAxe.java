package scripts.actions;

import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api.types.generic.Condition;
import org.tribot.api2007.Equipment;
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
		return Inventory.find(Filters.Items.nameEquals(PICK_AXE_HEAD,PICK_AXE_HANDLE)).length > 0;

	}

	@Override
	public void execute() {
		print("Fixing axe");

		if (MFUtil.getCountOfFreeInvSpace() < 2) {
			if (Inventory.find(Filters.Items.nameContains("ore", "Clay")).length > 0)
				if (Inventory.drop(Inventory.find(Filters.Items.nameContains(
						"ore", "Clay"))) != 2)
					return;
			if (Inventory.isFull())
				return;
		}
		if (Equipment.isEquipped(PICK_AXE_HANDLE)) {
			if (MFUtil.switchTab(GameTab.TABS.EQUIPMENT)
					&& Equipment.remove(Equipment.SLOTS.WEAPON) == 1)
				MFUtil.switchTab(GameTab.TABS.INVENTORY);
		}
		
		RSGroundItem[] pickAxeHead = GroundItems.find(PICK_AXE_HEAD);
		if (GameTab.getOpen() == GameTab.TABS.INVENTORY) {
			if (pickAxeHead.length > 0 && pickAxeHead[0] != null) {
				if (pickAxeHead[0].click("Take " + PICK_AXE_HEAD))
					Timing.waitCondition(new Condition() {

						@Override
						public boolean active() {
							General.sleep(100, 200);
							return Inventory.find(PICK_AXE_HEAD).length > 0;
						}
					}, General.random(3000, 4000));
			}
			if (hasBothAxeComponents()) {
				if (Inventory.find(PICK_AXE_HEAD)[0].click("Use"))
					General.sleep(this.abc.itemInteractionDelay());
				else
					return;
				if (Inventory.find(PICK_AXE_HANDLE)[0].click("Use"))
					General.sleep(this.abc.itemInteractionDelay());
				else
					return;
			}
			if (Globals.HAS_PICK_INVENTORY.getStatus())
				if (Inventory.find(Filters.Items.nameContains("pickaxe"))[0]
						.click("W"))
					Timing.waitCondition(new Condition() {

						@Override
						public boolean active() {
							General.sleep(100, 200);
							return Globals.HAS_PICK_EQUIPMENT.getStatus();
						}

					}, General.random(1500, 2000));
		}
	}

	@Override
	public boolean isValid() {
		return Globals.HAS_PICKAXE_HANDLE.getStatus();
	}
}
