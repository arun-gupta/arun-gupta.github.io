# Install Kubeflow on self-managed Kubernetes on AWS

- Install kops:

  brew update && brew upgrade kops

- Setup env vars:

	export NAME=kops.k8s.local
	export KOPS_STATE_STORE=s3://kops-state-store-aws
	export AWS_AVAILABILITY_ZONES="$(aws ec2 describe-availability-zones --query 'AvailabilityZones[].ZoneName' --output text | awk -v OFS="," '$1=$1')"

- Create cluster:

	kops create cluster ${NAME} --node-count=4 --zones=${AWS_AVAILABILITY_ZONES}

- Download Kubeflow from https://github.com/kubeflow/kubeflow/releases/ and extract it
- chmod +x kfctl
- Setup Kubeflow:

	```
	export PATH=$PATH:/Users/argu/tools/kubeflow/0.6.0
	export KFAPP=kfapp
	export CONFIG="https://raw.githubusercontent.com/kubeflow/kubeflow/master/bootstrap/config/kfctl_existing_arrikto.0.6.yaml"

	# Specify credentials for the default user.
	export KUBEFLOW_USER_EMAIL="admin@kubeflow.org"
	export KUBEFLOW_PASSWORD="12341234"

	kfctl init ${KFAPP} --config=${CONFIG} -V
	cd ${KFAPP}
	kfctl generate all -V
	kfctl apply all -V
	```