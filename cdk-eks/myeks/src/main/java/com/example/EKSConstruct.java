package com.example;

import software.amazon.awscdk.core.Construct;
import software.amazon.awscdk.services.ec2.InstanceClass;
import software.amazon.awscdk.services.ec2.InstanceSize;
import software.amazon.awscdk.services.ec2.InstanceType;
import software.amazon.awscdk.services.ec2.Vpc;
import software.amazon.awscdk.services.eks.AddWorkerNodesOptions;
import software.amazon.awscdk.services.eks.Cluster;
import software.amazon.awscdk.services.eks.ClusterProps;

/**
 * Example of a reusable construct. This one defines N buckets.
 */
public class EKSConstruct extends Construct {

    public EKSConstruct(final Construct parent, final String id, final EKSConstructProps props) {
        super(parent, id);

        Vpc vpc = new Vpc(this, "VPC");
        Cluster cluster = new Cluster(parent, props.getClusterName(), ClusterProps.builder().withVpc(vpc).build());

        cluster.addCapacity("nodes",
                AddWorkerNodesOptions.builder()
                        .withInstanceType(
                                InstanceType.of(InstanceClass.BURSTABLE2,
                                        InstanceSize.XLARGE))
                        .withMinCapacity(props.getMinCapacity())
                        .withMaxCapacity(props.getMaxCapacity())
                        .build());

    }
}
