package scripts;

import org.tribot.api2007.Banking;
import org.tribot.api2007.Equipment;
import org.tribot.api2007.Inventory;
import org.tribot.api2007.Player;
import org.tribot.api2007.ext.Filters;

import scripts.sbf.graphics.UserSelections;
import scripts.sbf.skill.SkillGlobals;
import scripts.sbf.skill.SkillManager;

public enum Globals {
	IS_IN_MINE_AREA {

		@Override
		public boolean getStatus() {
			return SkillManager.getInstance()
					.getSkillArea().contains(Player.getPosition());
		}

	},
	WALK_BANK {

		@Override
		public boolean getStatus() {
			return !Banking.isBankScreenOpen()
					&& (DEPOSIT_NEEDS.getStatus() || WITHDRAW_NEEDS.getStatus());
		}

	},
	DEPOSIT {

		@Override
		public boolean getStatus() {
			return Banking.isBankScreenOpen() && DEPOSIT_NEEDS.getStatus();
		}

	},
	WITHDRAW {

		@Override
		public boolean getStatus() {
			return Banking.isBankScreenOpen() && WITHDRAW_NEEDS.getStatus();
		}

	},
	DEPOSIT_NEEDS {

		@Override
		public boolean getStatus() {
			return Inventory.isFull()
					&& !UserSelections.getInstance().get("Mining method")
							.equals("Powermine");
		}

	},
	WITHDRAW_NEEDS {

		@Override
		public boolean getStatus() {
			return !HAS_PICK_EQUIPMENT.getStatus()
					|| HAS_PICKAXE_HANDLE.getStatus();
		}

	},
	WALK_TO_MINE {

		@Override
		public boolean getStatus() {
			return !IS_IN_MINE_AREA.getStatus() && !WITHDRAW_NEEDS.getStatus()
					&& !DEPOSIT_NEEDS.getStatus();
		}

	},
	MINE {

		@Override
		public boolean getStatus() {
			return !SkillGlobals.HOPPING.getUpdatedStatus()
					&& IS_IN_MINE_AREA.getStatus()
					&& !WITHDRAW_NEEDS.getStatus()
					&& !DEPOSIT_NEEDS.getStatus()
					&& !HAS_PICKAXE_HANDLE.getStatus();
		}

	},
	OPEN_BANK {

		@Override
		public boolean getStatus() {
			return WALK_BANK.getStatus()
					&& SkillGlobals.ARRIVED_AT_DEPOSITORY.getUpdatedStatus();
		}

	},
	EQUIP {

		@Override
		public boolean getStatus() {
			return !HAS_PICK_EQUIPMENT.getStatus();
		}

	},
	HAS_PICK_INVENTORY {

		@Override
		public boolean getStatus() {
			return Inventory.find(Filters.Items.nameNotEquals("Broken pickaxe")
					.combine(Filters.Items.nameContains("pickaxe"), true)).length > 0;
		}

	},
	HAS_PICK_EQUIPMENT {

		@Override
		public boolean getStatus() {
			return Equipment.find(Filters.Items.nameNotEquals("Broken pickaxe")
					.combine(Filters.Items.nameContains("pickaxe"), true)).length > 0;
		}

	},
	HAS_PICKAXE_HANDLE {

		@Override
		public boolean getStatus() {

			return Equipment.find("Pickaxe handle").length > 0;
		}

	};

	public abstract boolean getStatus();
}
