package scripts.sbf.action;

import org.tribot.api.General;

import scripts.sbf.graphics.UserSelections;
import scripts.sbf.manager.Manager;
import scripts.sbf.skill.SkillManager;

/**
 * The associated action of a parent state.
 * @author modulusfrank12
 */
public abstract class Action {

	
	protected UserSelections selections = UserSelections.getInstance();
	
	protected Manager manager = Manager.getInstance();
	
	protected SkillManager skillManager = SkillManager.getInstance();

	/**
	 * Executes the action.
	 */
	public abstract void execute();

	/**
	 * Determines if the action is valid.
	 */
	public abstract boolean isValid();

	/**
	 * Outputs the current status to paint.
	 */
	public void print(String stat) {
		manager.loadCurrentStatus(stat);
		General.println(stat);
	}

}
