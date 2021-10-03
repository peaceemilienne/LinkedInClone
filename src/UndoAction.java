package edu.institution.actions.asn10;

import java.util.Scanner;
import java.util.Stack;

import edu.institution.ApplicationHelper;
import edu.institution.UserRepository;
import edu.institution.actions.MenuAction;
import edu.institution.actions.asn3.AddUserAction;
import edu.institution.actions.asn3.DeleteUserAction;
import edu.institution.actions.asn3.SignoffAction;
import edu.institution.actions.asn4.AddConnectionAction;
import edu.institution.actions.asn4.RemoveConnectionAction;
import edu.institution.actions.asn7.AddSkillsetAction;
import edu.institution.actions.asn7.RemoveSkillsetAction;
import edu.institution.asn2.LinkedInException;
import edu.institution.asn2.LinkedInUser;

public class UndoAction implements MenuAction {

	public static Stack<MenuAction> history = new Stack<>();

	@Override
	public boolean process(Scanner scanner, UserRepository userRepository, LinkedInUser loggedInUser) {

		if (history.isEmpty()) {

			System.out.println("The history stack has no actions to undo!");
			return true;
		} else {
			// System.out.println(history.peek());
			MenuAction action = history.peek();

			if (SignoffAction.signOff) {
				history.clear();
				System.out.println("The history stack has no actions to undo!");
				SignoffAction.signOff = false;
				return true;
			}
			if (action.getClass().equals(AddUserAction.class)) {

				System.out.println("The last menu option selected was 'Sign Up a New User' involving "
						+ AddUserAction.undoUserAdd.getUsername() + ". Undo? (Y/N)");
				String undoAddUser = scanner.nextLine();
				if (!undoAddUser.equals("Y") || undoAddUser == null) {
					return true;
				} else {

					userRepository.delete(AddUserAction.undoUserAdd);
					history.pop();
					System.out.println((AddUserAction.undoUserAdd.getUsername()) + " was deleted from LinkedInUsers");
				}
			} else if (action.getClass().equals(DeleteUserAction.class)) {

				System.out.println("The last menu option selected was 'Deleting a LinkedInUser' involving "
						+ DeleteUserAction.undoUserDelete.getUsername() + ". Undo? (Y/N)");
				String undoDelete = scanner.nextLine();
				if (!undoDelete.equals("Y") || undoDelete == null) {
					return true;
				} else {

					try {
						userRepository.add(DeleteUserAction.undoUserDelete);
						history.pop();
						System.out.println(
								(DeleteUserAction.undoUserDelete.getUsername()) + " was readded to the LinkedInUsers ");
					} catch (LinkedInException e) {
						System.out.println(e);
					}

				}
			} else if (action.getClass().equals(AddConnectionAction.class)) {

				System.out.println("The last menu option selected was 'Add connection' involving "
						+ loggedInUser.getUsername() + ". Undo? (Y/N)");
				String undoAdd = scanner.nextLine();
				if (!undoAdd.equals("Y") || undoAdd == null) {
					return true;
				} else {

					try {
						loggedInUser.removeConnection(userRepository.retrieve(AddConnectionAction.addableUser));
						history.pop();
						System.out.println((AddConnectionAction.addableUser) + " was removed from "
								+ loggedInUser.getUsername() + "'s connections list!");
					} catch (LinkedInException e) {
						System.out.println("Your existing connection was affected by UndoAction!");
						return true;
					}

				}
			} else if (action.getClass().equals(RemoveConnectionAction.class)) {

				System.out.println("The last menu option selected was 'Remove connection' involving "
						+ loggedInUser.getUsername() + ". Undo? (Y/N)");
				String undoRemove = scanner.nextLine();
				if (!undoRemove.equals("Y") || undoRemove == null) {
					return true;
				} else {

					try {
						loggedInUser.addConnection(userRepository.retrieve(RemoveConnectionAction.removableUser));
						history.pop();
						System.out.println((RemoveConnectionAction.removableUser) + " was readded to "
								+ loggedInUser.getUsername() + "'s connections list!");
					} catch (LinkedInException e) {
						System.out.println("Your existing connection was affected by UndoAction!");
						return true;
					}
				}
			} else if (action.getClass().equals(AddSkillsetAction.class)) {

				System.out.println("The last menu option selected was 'Adding a skillset' involving "
						+ loggedInUser.getUsername() + ". Undo? (Y/N)");
				String undoAddSkill = scanner.nextLine();
				if (!undoAddSkill.equals("Y") || undoAddSkill == null) {
					return true;
				} else {

					if (!LinkedInUser.added) {

						System.out.println(AddSkillsetAction.addableSkill + " was duplicated in your skillsets");
						history.pop();
						return true;
					}
					loggedInUser.removeSkillset(AddSkillsetAction.addableSkill);
					history.pop();
					if (LinkedInUser.removed) {

						ApplicationHelper.decrementSkillsetCount(AddSkillsetAction.addableSkill);
					}

					System.out.println((AddSkillsetAction.addableSkill) + " was removed from your skillsets!");
				}
			} else if (action.getClass().equals(RemoveSkillsetAction.class)) {

				System.out.println("The last menu option selected was 'Removing a skillset' involving "
						+ loggedInUser.getUsername() + ".Undo? (Y/N)");
				String undoRemoveSkill = scanner.nextLine();
				if (!undoRemoveSkill.equals("Y") || undoRemoveSkill == null) {
					return true;
				} else {

					if (!LinkedInUser.removed) {

						System.out.println(RemoveSkillsetAction.removableSkill + " didn't exist in your skillsets");
						history.pop();
						return true;
					}
					loggedInUser.addSkillset(RemoveSkillsetAction.removableSkill);
					history.pop();

					// if (LinkedInUser.added) {
					// System.out.println(RemoveSkillsetAction.removableSkill + " has been added to
					// your skillsets.");
					// }
					ApplicationHelper.incrementSkillsetCount(RemoveSkillsetAction.removableSkill);

					System.out.println((RemoveSkillsetAction.removableSkill) + " was readded to your skillsets!");
				}
			}

			return true;
		}
	}

}
