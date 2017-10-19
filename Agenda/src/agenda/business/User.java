package agenda.business;

import java.util.ArrayList;

import agenda.business.Meeting;

/**
 * Represent a user in this system.
 * This class store any information about a user,
 * for example, user's name and user's password.
 */
public class User {
    private String userName;
    private String userPassword;
    private ArrayList<Meeting> meetings;

    /**
     * Constructor with detailed information.
     * 
     * @param name user's name
     * @param password user's password
     */
    public User(String name, String password) {
        if (name == null || password == null) {
            throw new IllegalArgumentException("Parameters should NOT be null");
        }
        if (name.equals("")) {
            throw new IllegalArgumentException("Name should NOT be empty");
        }
        if (password.equals("")) {
            throw new IllegalArgumentException("Password should NOT be empty");
        }
        userName = name;
        userPassword = password;
        meetings = new ArrayList<Meeting>();
    }

    /**
     * Getter of user name.
     * 
     * @return user name of this user
     */
    public String getName() {
        return userName;
    }

    /**
     * Getter of user's password.
     * 
     * @return user's password of this user
     */
    public String getPassword() {
        return userPassword;
    }

    /**
     * Setter of user's password.
     * 
     * @param newPassword new password
     */
    public void setPassword(String newPassword) {
        userPassword = newPassword;
    }

    /**
     * Add a meeting to user's meeting list
     * 
     * @param pMeeting the meeting to be added
     */
    public void addMeeting(Meeting pMeeting) {
        meetings.add(pMeeting);
    }

    /**
     * Check password.
     * 
     * @param pPassword to be check whether it match with user's password
     * 
     * @return true if the password is correct
     */
    public boolean checkPassword(String pPassword) {
        return pPassword.equals(userPassword);
    }

    /**
     * Get meetings by date
     * 
     * @param begin begin date
     * @param end end date
     * 
     * @return all meetings that overlap with [begin, end]
     */
    public ArrayList<Meeting> getMeetingsByDate(Date begin, Date end) {
        ArrayList<Meeting> res = new ArrayList<Meeting>();
        for (Meeting meeting : meetings) {
            if ((begin.compareTo(meeting.getBeginDate()) < 0
                    && end.compareTo(meeting.getBeginDate()) > 0)
                    || begin.compareTo(meeting.getEndDate()) < 0
                    && end.compareTo(meeting.getEndDate()) > 0) {
                res.add(meeting);
            }
        }
        return res;
    }

    /**
     * Get meeting by title.
     * 
     * @param title the title of the meeting
     * 
     * @return null if the meeting doesn't exist
     */
    public Meeting getMeetingByTitle(String title) {
        for (Meeting meeting : meetings) {
            if (title.equals(meeting.getTitle())) {
                return meeting;
            }
        }
        return null;
    }

    /**
     * Remove a meeting by title.
     * 
     * @param title the title of the meeting to be deleted
     * 
     * @return false if the meeting doesn't exist
     */
    public boolean deleteMeetingByTitle(String title) {
        Meeting meeting = getMeetingByTitle(title);

        if (meeting == null) return false;

        User sponosor = meeting.getSponsor();
        User participator = meeting.getParticipator();
        sponosor.deleteMeeting(meeting);
        participator.deleteMeeting(meeting);

        return true;
    }

    /**
     * Remove a meeting.
     * 
     * @param meeting the meeting to be removed
     * 
     * @return false if the user doesn't have the meeting
     */
    public boolean deleteMeeting(Meeting meeting) {
        return meetings.remove(meeting);
    }

    /**
     * Clear all meetings.
     * This function also affects the other participators of all meetings.
     */
    public void clearAllMeetings() {
        ArrayList<String> allTitles = new ArrayList<String>();
        ArrayList<User> others = new ArrayList<User>();

        for (Meeting meeting: meetings) {
            allTitles.add(meeting.getTitle());
            User other = meeting.getSponsor();
            if (other.getName().equals(getName())) {
                other = meeting.getParticipator();
            }
            others.add(other);
        }
        meetings.clear();

        for (int i = 0; i < others.size(); ++i) {
            others.get(i).deleteMeetingByTitle(allTitles.get(i));
        }
    }

    /**
     * Judge whether this user is busy during the given time.
     * 
     * @param bDate begin date
     * @param eDate end date
     * 
     * @return true if user is busy during the given time
     */
    public boolean isBusy(Date bDate, Date eDate) {
        for (Meeting m : meetings) {
            if (! (m.getBeginDate().compareTo(eDate) >= 0 || m.getEndDate().compareTo(bDate) <= 0)) {
                return true;
            }
        }
        return false;
    }

}
