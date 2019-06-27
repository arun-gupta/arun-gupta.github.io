package com.example;

public class EKSConstructProps {
    private String clusterName;
    private int minCapacity;
    private int maxCapacity;

    public static EKSConstructPropsBuilder builder() {
        return new EKSConstructPropsBuilder();
    }

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public int getMinCapacity() {
        return minCapacity;
    }

    public void setMinCapacity(int minCapacity) {
        this.minCapacity = minCapacity;
    }

    public int getMaxCapacity() {
        return maxCapacity;
    }

    public void setMaxCapacity(int maxCapacity) {
        this.maxCapacity = maxCapacity;
    }

    public static final class EKSConstructPropsBuilder {
        private String clusterName;
        private int minCapacity;
        private int maxCapacity;

        private EKSConstructPropsBuilder() {
        }

        public static EKSConstructPropsBuilder EKSConstructProps() {
            return new EKSConstructPropsBuilder();
        }

        public EKSConstructPropsBuilder withClusterName(String clusterName) {
            this.clusterName = clusterName;
            return this;
        }

        public EKSConstructPropsBuilder withMinCapacity(int minCapacity) {
            this.minCapacity = minCapacity;
            return this;
        }

        public EKSConstructPropsBuilder withMaxCapacity(int maxCapacity) {
            this.maxCapacity = maxCapacity;
            return this;
        }

        public EKSConstructProps build() {
            EKSConstructProps eksConstructProps = new EKSConstructProps();
            eksConstructProps.setClusterName(clusterName);
            eksConstructProps.setMinCapacity(minCapacity);
            eksConstructProps.setMaxCapacity(maxCapacity);
            return eksConstructProps;
        }
    }
}
