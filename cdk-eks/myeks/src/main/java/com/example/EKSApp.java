package com.example;

import software.amazon.awscdk.core.App;

public class EKSApp {
    public static void main(final String argv[]) {
        App app = new App();

        new EKSStack(app, "myeks");

        // required until https://github.com/awslabs/jsii/issues/456 is resolved
        app.synth();
    }
}
