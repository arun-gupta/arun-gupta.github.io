# Install Kubeflow on self-managed Kubernetes on AWS

- Install kops:

  brew update && brew upgrade kops

- Setup env vars:

	export NAME=kops.k8s.local
	export KOPS_STATE_STORE=s3://kops-state-store-aws
	export AWS_AVAILABILITY_ZONES="$(aws ec2 describe-availability-zones --query 'AvailabilityZones[].ZoneName' --output text | awk -v OFS="," '$1=$1')"

- Create cluster:

	kops create cluster ${NAME} --node-count=4 --zones=${AWS_AVAILABILITY_ZONES}

- Download Kubeflow from https://github.com/kubeflow/kubeflow/releases/

- 