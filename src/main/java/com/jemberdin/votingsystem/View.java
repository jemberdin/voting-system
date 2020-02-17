package com.jemberdin.votingsystem;

import javax.validation.groups.Default;

public class View {

    // Validate only form UI/REST
    public interface Rest extends Default {}

    // Validate only when DB save/update
    public interface Persist extends Default {}
}
