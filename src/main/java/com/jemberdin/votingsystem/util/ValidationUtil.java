package com.jemberdin.votingsystem.util;

import com.jemberdin.votingsystem.HasId;
import com.jemberdin.votingsystem.util.exception.CreateMenuForPastDateException;
import com.jemberdin.votingsystem.util.exception.NotFoundException;
import com.jemberdin.votingsystem.util.exception.VotingDateException;
import com.jemberdin.votingsystem.util.exception.VotingTimeException;


import java.time.LocalDate;
import java.time.LocalTime;

import static com.jemberdin.votingsystem.util.DateTimeUtil.FINISHING_UPDATE_VOTE_TIME;

public class ValidationUtil {

    private ValidationUtil() { }

    public static <T> T checkNotFoundWithId(T object, int id) {
        return checkNotFound(object, "id=" + id);
    }

    public static void checkNotFoundWithId(boolean found, int id) {
        checkNotFound(found, "id=" + id);
    }

    public static <T> T checkNotFoundWithDate(T object, LocalDate date) {
        return checkNotFound(object, "date=" + date.toString());
    }

    public static <T> T checkNotFound(T object, String msg) {
        checkNotFound(object != null, msg);
        return object;
    }

    public static void checkNotFound(boolean found, String msg) {
        if (!found) {
            throw new NotFoundException("Not found entity with " + msg);
        }
    }

    public static void checkCreateMenuForDate(LocalDate date) {
        if(date != null && date.isBefore(LocalDate.now())) {
            throw new CreateMenuForPastDateException("Must be at least today's date");
        }
    }

    public static void checkNew(HasId bean) {
        if (!bean.isNew()) {
            throw new IllegalArgumentException(bean + " must be new (id=null)");
        }
    }

    public static void checkVotingDate(LocalDate votingDate, LocalDate currentDate) {
        if (!votingDate.equals(currentDate)) {
            throw new VotingDateException("Must be today's date");
        }
    }

    public static void checkTimeToUpdateVote(LocalTime time) {
        if(time.isAfter(FINISHING_UPDATE_VOTE_TIME)) {
            throw new VotingTimeException("Update is not allowed after " + FINISHING_UPDATE_VOTE_TIME);
        }
    }

    public static void assureIdConsistent(HasId bean, int id) {
//      conservative when you reply, but accept liberally (http://stackoverflow.com/a/32728226/548473)
        if (bean.isNew()) {
            bean.setId(id);
        } else if (bean.id() != id) {
            throw new IllegalArgumentException(bean + " must be with id=" + id);
        }
    }

}
