# Add IAM User to Amazon EKS Cluster

These instructions explain how to access Amazon EKS cluster from a _destination_ machine by somebody who does not have an AWS account. The cluster is created on a _source_ machine.

## On the _source_ machine

### Create AWS Destination IAM User

- Create a [new policy](https://console.aws.amazon.com/iam/home?region=us-west-2#/policies). Call it `AmazonEKSDescribeClusterPolicy` and use the following JSON fragment:

	```
	{
	    "Version": "2012-10-17",
	    "Statement": [
	        {
	            "Effect": "Allow",
	            "Action": "eks:DescribeCluster",
	            "Resource": "*"
	        }
	    ]
	}
	```

- Create a [new group](https://console.aws.amazon.com/iam/home?region=us-west-2#/groups), call it `myeks`. Assign the previously created policy to this group.
- Create a [new AWS user](https://console.aws.amazon.com/iam/home?region=us-west-2#/users), enable programmatic access, add user to the `myeks` group
- Download `.csv` file and share the credentials out of band
- Grab the user ARN:

	```
	USER_ARN=$(aws iam get-user --user-name myeks --query User.Arn --output text)
	```

### Add Destination IAM user to EKS Cluster

- Add IAM user to `aws-auth` ConfigMap for the EKS cluster:

	```
	USER="  mapUsers: |\n    - userarn: $USER_ARN\n      username: myeks\n      groups:\n        - system:masters"
	kubectl get -n kube-system configmap/aws-auth -o yaml | awk "/data:/{print;print \"$USER\";next}1" > /tmp/aws-auth-patch.yml
	kubectl patch configmap/aws-auth -n kube-system --patch "$(cat /tmp/aws-auth-patch.yml)"
	```

## On the _destination_ machine

### Configure AWS CLI for Destination User

- Install [AWS CLI](https://docs.aws.amazon.com/cli/latest/userguide/cli-chap-install.html)
- [Configure](https://docs.aws.amazon.com/cli/latest/userguide/cli-chap-configure.html) AWS CLI. For convenience, `aws configure` command will configure the CLI using the given credentials. Make sure to choose the same region in which the EKS cluster is created, for example `us-west-2`. Choose `json` as the output format.
- Install `aws-iam-authenticator` as explained at https://docs.aws.amazon.com/eks/latest/userguide/install-aws-iam-authenticator.html and include in `PATH`
- Install `kubectl` as explained at https://docs.aws.amazon.com/eks/latest/userguide/install-kubectl.html

### Generate Kubernetes Configuration

- Generate configuration file to access the EKS Cluster:

	```
	eksctl utils write-kubeconfig \
		--name myeks \
		--kubeconfig ./kubeconfig
	```

### Access EKS Cluster

- Use `kubeconfig` to access the cluster:

	```
	kubectl --kubeconfig ./kubeconfig get nodes
	```

	Optionally, set `KUBECONFIG` environment variable:

	```
	export KUBECONFIG=`pwd`/kubeconfig
	```

	And then get nodes as:

	```
	kubectl get nodes
	```

