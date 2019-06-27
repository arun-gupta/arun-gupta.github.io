package com.example;

import software.amazon.awscdk.core.Construct;
import software.amazon.awscdk.core.Stack;
import software.amazon.awscdk.core.StackProps;

public class EKSStack extends Stack {

    public EKSStack(final Construct parent, final String id) {
        this(parent, id, null);
    }

    public EKSStack(final Construct parent, final String id, final StackProps props) {
        super(parent, id, props);

        new EKSConstruct(parent, id, EKSConstructProps.builder().withClusterName(id)
                .withMinCapacity(1)
                .withMaxCapacity(4).build());
    }
}
