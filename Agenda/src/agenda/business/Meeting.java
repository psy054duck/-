package agenda.business;

import java.util.ArrayList;
import agenda.business.Date;

/**
 * Meeting class
 */
public class Meeting {
    private final String label;
    private final Date bDate;
    private final Date eDate;

    private final User sponsor;
    private User participator;

    /**
     * Constructor with detailed information.
     * 
     * @param pLabel title of this meeting
     * @param pSponsor sponsor of this meeting
     * @param pParticipator participator of this meeting
     * @param pBDate begin date of this meeting
     * @param pEDate end date of this meeting
     */
    public Meeting(String pLabel, User pSponsor, User pParticipator,
                   Date pBDate, Date pEDate) {
        if (pLabel == null || pSponsor == null || pParticipator == null
            || pBDate == null || pEDate == null) {
                throw new IllegalArgumentException("Parameters should NOT be null");
        }
        if (pBDate.compareTo(pEDate) >= 0) {
            throw new IllegalArgumentException("begin date should earlier than end date");
        }
        User user = isValid(pSponsor, pParticipator, pBDate, pEDate);
        if (user != null) {
            throw new IllegalArgumentException(user.getName() + " is busy");
        }
        if (pLabel.equals("")) {
            throw new IllegalArgumentException("Label should NOT be empty");
        }

        label = pLabel;
        sponsor = pSponsor;
        participator = pParticipator;
        bDate = pBDate;
        eDate = pEDate;
        sponsor.addMeeting(this);
        participator.addMeeting(this);
    }

    /**
     * Getter of sponsor.
     * 
     * @return the sponsor of this meeting.
     */
    public User getSponsor() {
        return sponsor;
    }

    /**
     * Getter of participator.
     * 
     * @return the participator of this meeting.
     */
    public User getParticipator() {
        return participator;
    }
    
    /**
     * Getter of begin date.
     * 
     * @return the begin date of this meeting.
     */
    public Date getBeginDate() {
        return bDate;
    }

    /**
     * Getter of end date.
     * 
     * @return the end date of this meeting.
     */
    public Date getEndDate() {
        return eDate;
    }

    /**
     * Getter of title.
     * 
     * @return title of this meeting
     */
    public String getTitle() {
        return label;
    }

    /**
     * Convert this meeting to string.
     * 
     * @return the string describing this meeting
     */
    public String toString() {
        return String.format("[" + label + " " + sponsor.getName() + " "
                             + participator.getName() + " " + bDate.toString()
                             + "-" + eDate.toString() + "]");
    }

    /**
     * Judge whether the parameters is valid to construct a Meeting.
     * 
     * @param pSponsor sponsor
     * @param pParticipator participator
     * @param pBDate begin date
     * @param pEDate end date
     * 
     * @return the user that is busy so that can't participate in the given meeting.
     *         null if the parameters is valid.
     */
    private User isValid(User pSponsor, User pParticipator,
                         Date pBDate, Date pEDate) {
        ArrayList<User> users = new ArrayList<User>();
        users.add(pParticipator);
        users.add(pSponsor);

        for (User user : users) {
            if (user.isBusy(pBDate, pEDate)) {
                return user;
            }
        }

        return null;
    }
}
