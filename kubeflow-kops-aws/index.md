# Install Kubeflow on self-managed Kubernetes on AWS

This post will explain how to setup Kubeflow an self-managed Kubernetes cluster on AWS. Even though it uses kops for creating the cluster but it could've been created any other way, such as CloudFormation or Terraform.

- Install kops:

	```
  brew update && brew upgrade kops
  ```

- Setup env vars:

	```
	export NAME=kops.k8s.local
	export KOPS_STATE_STORE=s3://kops-state-store-aws
	export AWS_AVAILABILITY_ZONES="$(aws ec2 describe-availability-zones --query 'AvailabilityZones[].ZoneName' --output text | awk -v OFS="," '$1=$1')"
	```

- Create cluster:

	```
	kops create cluster ${NAME} --node-count=4 --zones=${AWS_AVAILABILITY_ZONES}
	```

- Download Kubeflow:

	```
	curl -OL https://github.com/kubeflow/kubeflow/releases/download/v0.6.1/kfctl_v0.6.1_darwin.tar.gz
	```

- Setup Kubeflow:

	```
	export PATH=$PATH:/Users/argu/tools/kubeflow/0.6.1
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

- Access Kubeflow dashboard endpoint address:

	```
	kubectl get svc -n istio-system istio-ingressgateway -o jsonpath='{.status.loadBalancer.ingress[0].hostname}'
	```

	It gives the error:

	```
	This page isn’t working a86596f68b0a511e998a30628ef7c2fc-315815572.us-west-2.elb.amazonaws.com didn’t send any data.
	```