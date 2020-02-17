package com.jemberdin.votingsystem;

import com.jemberdin.votingsystem.model.Vote;

import java.time.LocalDate;

import static com.jemberdin.votingsystem.model.AbstractBaseEntity.START_SEQ;

public class VoteTestData {

    public static final int VOTE1_ID = START_SEQ + 42;
    public static final int VOTE2_ID = VOTE1_ID + 1;
    public static final int VOTE3_ID = VOTE1_ID + 3;

    public static final Vote VOTE1 = new Vote(VOTE1_ID, LocalDate.now());
    public static final Vote VOTE2 = new Vote(VOTE2_ID, LocalDate.now().minusDays(1));


    public static Vote getNewVote() {
        return new Vote(null, LocalDate.now());
    }

    public static Vote getUpdated() {
        Vote updated = new Vote();
        updated.setId(VOTE1_ID);
        updated.setDate(LocalDate.now());
        return updated;
    }

    public static TestMatchers<Vote> VOTE_MATCHERS = TestMatchers.useFieldsComparator(Vote.class, "user", "restaurant");
    public static TestMatchers<Vote> VOTE_MATCHERS_STRING = TestMatchers.useEquals(Vote.class);
}
