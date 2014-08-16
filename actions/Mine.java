package scripts.actions;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api.types.generic.Condition;
import org.tribot.api.types.generic.Filter;
import org.tribot.api2007.GameTab;
import org.tribot.api2007.Inventory;
import org.tribot.api2007.Objects;
import org.tribot.api2007.PathFinding;
import org.tribot.api2007.Player;
import org.tribot.api2007.Skills.SKILLS;
import org.tribot.api2007.types.RSModel;
import org.tribot.api2007.types.RSObject;
import org.tribot.api2007.types.RSObjectDefinition;

import scripts.Globals;
import scripts.sbf.action.Action;
import scripts.sbf.skill.SkillGlobals;
import scripts.sbf.util.ABC;
import scripts.sbf.util.MFUtil;

public class Mine extends Action {
	private final List<String> availableOres = selections.getContents("ores");
	private final Map<String, short[]> oresMap = skillManager
			.getSkillResourcesByModifiedColourMap();
	private final ABC abc = manager.getABC();
	private LinkedList<short[]> rocks = new LinkedList<>();
	private short[] colourOfRockToMine;
	private final String dropMethod = selections.get("Dropping Method");
	private final String miningMethod = selections.get("Mining method");
	private final Filter<RSObject> colourFilter = new Filter<RSObject>() {

		@Override
		public boolean accept(RSObject rock) {
			if (rock != null) {
				RSObjectDefinition rockDefinition = rock.getDefinition();
				if (rockDefinition != null)
					for (short modColor : rockDefinition.getModifiedColors())
						for (short colorToMine : colourOfRockToMine)
							if (modColor == colorToMine)
								return true;

			}
			return false;
		}

	};
	private final Filter<RSObject> nonSmokeFilter = new Filter<RSObject>() {

		@Override
		public boolean accept(RSObject rock) {
			RSModel model = rock.getModel();
			return model != null && model.getVertexCount() < 150;
		}

	};

	private boolean walkToSpecifiedRock(final RSObject rock) {
		return PathFinding.aStarWalk(rock.getPosition()) ? Timing
				.waitCondition(new Condition() {

					@Override
					public boolean active() {
						General.sleep(100, 200);
						return rock.isOnScreen();
					}

				}, General.random(5000, 6000)) : false;
	}

	private boolean clickRock(final RSObject rock) {
		RSModel rockModel = rock.getModel();
		return rockModel != null && MFUtil.clickModel(rockModel, "Mine") ? Timing
				.waitCondition(new Condition() {

					@Override
					public boolean active() {
						General.sleep(100, 200);
						return Player.getAnimation() != -1;
					}

				}, General.random(7000, 8500)) : false;
	}

	private boolean checkLevelUp() {
		return SKILLS.MINING.getActualLevel() > skillManager.getCurrentLevel();
	}

	private boolean detectSmokingRock(RSObject rock) {
		return !nonSmokeFilter.accept(rock);
	}

	private void handleSmokingRock() {
		if (PathFinding.aStarWalk(skillManager.getSkillTile()))
			Timing.waitCondition(new Condition() {

				@Override
				public boolean active() {
					General.sleep(100, 200);
					return MFUtil.isOnTile(skillManager.getSkillTile());
				}

			}, General.random(5000, 6000));
	}

	@Override
	public void execute() {
		print("Mining");
		if (rocks.isEmpty())
			for (String rockName : availableOres)
				rocks.add(oresMap.get(rockName));

		if (!rocks.isEmpty())
			colourOfRockToMine = rocks.get(0);

		if (colourOfRockToMine == null)
			return;

		final RSObject[] rocksToMine = Objects.findNearest(30,
				nonSmokeFilter.combine(colourFilter, true));

		if (rocksToMine.length > 0) {
			if (rocksToMine[0] != null && !Inventory.isFull()) {
				if (!rocksToMine[0].isOnScreen())
					if (!walkToSpecifiedRock(rocksToMine[0]))
						return;
				if (!clickRock(rocksToMine[0]))
					return;
				while (Player.getAnimation() != -1) {
					General.sleep(100, 200);
					this.abc.doAllIdleActions(SKILLS.MINING,
							GameTab.TABS.INVENTORY);
					if (checkLevelUp()) {
						skillManager.updateCurrentLevel();
						break;
					}
					if (detectSmokingRock(rocksToMine[0])) {
						handleSmokingRock();
						break;
					}
				}
			}
		} else {
			if (selections.get("Is mining rare").equals("yes"))
				for (String rare : skillManager.getRareResourceList())
					if (SkillGlobals.HOPPING.setStatus(skillManager
							.getSkillResourceNameByModifiedColour(
									colourOfRockToMine).equalsIgnoreCase(rare)))
						return;
		}

		if (miningMethod.equals("Powermine")) {
			switch (dropMethod) {
			case "M1D1":
				Inventory.drop(Inventory.getAll());
				break;
			case "Regular":
				if (Inventory.isFull())
					Inventory.drop(Inventory.getAll());

				break;
			}
		}
		if (Inventory.isFull())
			rocks.poll();

	}

	@Override
	public boolean isValid() {
		return Globals.MINE.getStatus();
	}
}