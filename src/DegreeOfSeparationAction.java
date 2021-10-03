package edu.institution.actions.asn4;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import edu.institution.UserRepository;
import edu.institution.actions.MenuAction;
import edu.institution.asn2.LinkedInUser;

public class DegreeOfSeparationAction implements MenuAction {

	@Override
	public boolean process(Scanner scanner, UserRepository userRepository, LinkedInUser loggedInUser) {

		String separatedUser;
		List<LinkedInUser> allLinkedInUsers = userRepository.retrieveAll();
		List<LinkedInUser> loggedInUserConnections = new ArrayList<>(), pathUsers = new ArrayList<>(),
				scannedUsers = new ArrayList<>();

		int countArray = 0, countSeparation = -1;

		if (loggedInUser.getConnections() == null || loggedInUser.getConnections().isEmpty()) {

			System.out.println("You have no connections!");
			return true;
		}
		System.out.println("Enter the username of the user you want to check his/her degree of separation from you");
		separatedUser = scanner.nextLine();
		if (separatedUser == null || separatedUser.isEmpty()) {
			System.out.println("The username of the user can not be empty or null");
			return true;
		} else {

			for (LinkedInUser user : allLinkedInUsers) {
				if (user.getUsername().equals(separatedUser))
					countArray++;

			}

			if (countArray == 0) {
				System.out.println("The user you entered is not a LinkedInUser");
				return true;
			} else if (userRepository.retrieve(separatedUser).equals(loggedInUser)) {
				System.out.println("You can not search yourself!");
				return true;
			} else {

				loggedInUserConnections = loggedInUser.getConnections();
				countSeparation = findSeparation(separatedUser, countSeparation, loggedInUser, loggedInUserConnections,
						pathUsers, scannedUsers, userRepository);
				if (countSeparation == -1) {
					System.out.println("You are not connected to " + separatedUser + " in any kind of way!");
				} else {
					System.out.println("There is " + countSeparation + " degree(s) of separation between you and "
							+ separatedUser);
					pathUsers.forEach((path) -> print(path));
				}
			}

			return true;
		}
	}

	public int findSeparation(String separatedUser, int countSeparation, LinkedInUser loggedInUser,
			List<LinkedInUser> loggedInUserConnections, List<LinkedInUser> pathUsers, List<LinkedInUser> scannedUsers,
			UserRepository userRepository) {

		for (LinkedInUser user : loggedInUserConnections) {

			if (user.getUsername().equals(separatedUser)) {

				pathUsers.add(loggedInUser);
				pathUsers.add(userRepository.retrieve(separatedUser));
				scannedUsers.add(loggedInUser);
				countSeparation = 0;

			} else {

				if (user.getConnections() != null && !user.getConnections().isEmpty() && !scannedUsers.contains(user)) {
					pathUsers.add(loggedInUser);
					for (LinkedInUser userConnected : user.getConnections()) {
						scannedUsers.add(user);
						if (userConnected.getUsername().equals(separatedUser)) {

							countSeparation = +1;
							pathUsers.add(user);
							pathUsers.add(userRepository.retrieve(separatedUser));
							break;
						}
						countSeparation = 1;
						countSeparation = countSeparation + findSeparationConnection(separatedUser, countSeparation,
								user, user.getConnections(), pathUsers, scannedUsers, userRepository);
					}
					// for (LinkedInUser userConnected : user.getConnections()) {
					// scannedUsers.add(user);
					// if (userConnected.getUsername().equals(separatedUser)) {

					// countSeparation = 1;
					// pathUsers.add(user);
					// pathUsers.add(userRepository.retrieve(separatedUser));

					// } else {

					// countSeparation = countSeparation + findSeparationConnection(separatedUser,
					// countSeparation,
					// user, user.getConnections(), pathUsers, scannedUsers, userRepository);
					// }

					// }

				}

			}
		}

		return countSeparation;

	}

	public int findSeparationConnection(String separatedUser, int countSeparation, LinkedInUser loggedInUser,
			List<LinkedInUser> loggedInUserConnections, List<LinkedInUser> pathUsers, List<LinkedInUser> scannedUsers,
			UserRepository userRepository) {

		for (LinkedInUser user : loggedInUser.getConnections()) {

			for (LinkedInUser userConnected : user.getConnections()) {
				scannedUsers.add(user);

				if (userConnected.getUsername().equals(separatedUser)) {

					pathUsers.add(loggedInUser);
					pathUsers.add(user);
					pathUsers.add(userRepository.retrieve(separatedUser));

				} else {

					if (user.getConnections() != null && !user.getConnections().isEmpty()
							&& !scannedUsers.contains(user)) {

						countSeparation = countSeparation + findSeparationConnection(separatedUser, countSeparation,
								userConnected, userConnected.getConnections(), pathUsers, scannedUsers, userRepository);

					}

				}
			}

		}
		if (pathUsers.size() == 1) {
			countSeparation = -2;
		}

		return countSeparation;
	}

	private void print(LinkedInUser path) {
		System.out.print(path.getUsername() + "->");

	}

}
